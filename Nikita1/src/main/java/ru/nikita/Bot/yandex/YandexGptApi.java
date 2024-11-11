package ru.nikita.Bot.yandex;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.nikita.Bot.yandex.responce.YandexResponce;

@Component
public class YandexGptApi {

    @Value("${Yandex}")
    private String token;

    @Value("${YandexFolder}")
    private String folderId;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper mapper;



    public String createRequest(String questionForAI) {
        String url = "https://llm.api.cloud.yandex.net/foundationModels/v1/completion";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Api-Key " + token);
        headers.add("x-folder-id", folderId);

        questionForAI = questionForAI.replaceAll("\n", "");

        String instructionForAI = "Ты ассистент по выбору заведений. Проанализируй информацию о заведении и комментарии людей, которые я предоставлю ниже. Дай свое мнение по заведению исходя из этой информации и используй свои знания. Пожалуйста, ответь не как нейронная сеть, а как человек, дающий совет.";

        String jsonString = createJsonForYandex(0.9,instructionForAI,questionForAI);

        HttpEntity<String> entity = new HttpEntity<>(jsonString, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        YandexResponce yandexResponse;
        try {
            yandexResponse = mapper.readValue(response.getBody(), YandexResponce.class);
        } catch (JsonProcessingException e) {
            System.out.println("Ошибка с отправкой запроса к Яндексу!");
            throw new RuntimeException(e);
        }

        return yandexResponse.getResult().getAlternatives().getFirst().getMessage().getText();
    }


    private String createJsonForYandex(double temperature, String instructionsForAI, String questionForAI){

        ObjectNode jsonPayload = mapper.createObjectNode();
        jsonPayload.put("modelUri", String.format("gpt://%s/yandexgpt-lite", folderId));
        ObjectNode completionOptions = jsonPayload.putObject("completionOptions");
        completionOptions.put("stream", false);
        completionOptions.put("temperature", temperature);
        completionOptions.put("maxTokens", "1000");

        ObjectNode systemMessage = mapper.createObjectNode();
        systemMessage.put("role", "system");
        systemMessage.put("text", instructionsForAI);

        ObjectNode userMessage = mapper.createObjectNode();

        userMessage.put("role", "user");
        userMessage.put("text", questionForAI);

        jsonPayload.putArray("messages").add(systemMessage).add(userMessage);

        String jsonString;
        try {
            jsonString = mapper.writeValueAsString(jsonPayload);
            return jsonString;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Ошибка при создании JSON строки!", e);
        }
    }
}



















/*public String createRequest(String question){

    String url = "https://llm.api.cloud.yandex.net/foundationModels/v1/completion";

    RestTemplate restTemplate = new RestTemplate();

    // Создание JSON тела запроса
    JSONObject requestBody = new JSONObject();
    requestBody.put("modelUri", "gpt://b1g73s23opo8h6ad5to5/yandexgpt-lite");

    JSONObject completionOptions = new JSONObject();
    completionOptions.put("stream", false);
    completionOptions.put("temperature", 0.6);
    completionOptions.put("maxTokens", 2000);

    requestBody.put("completionOptions", completionOptions);

    JSONArray messages = new JSONArray();

    JSONObject systemMessage = new JSONObject();
    systemMessage.put("role", "system");
    systemMessage.put("text", "ответь на вопрос");
    messages.put(systemMessage);

    JSONObject userMessage = new JSONObject();
    userMessage.put("role", "user");
    userMessage.put("text", question);
    messages.put(userMessage);

    requestBody.put("messages", messages);

    // Установка заголовков
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("Authorization", "Bearer " + token);
    headers.set("x-folder-id", folderId);

    // Создание HttpEntity для отправки
    HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);

    // Отправка POST-запроса
    ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

    return response.getBody();
}*/
