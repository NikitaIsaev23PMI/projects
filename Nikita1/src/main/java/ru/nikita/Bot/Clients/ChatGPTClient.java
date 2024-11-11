package ru.nikita.Bot.Clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.nikita.Bot.chatGPTResponces.ChatCompletionObject;

import java.util.List;

@Component
@Deprecated
public class ChatGPTClient {

    @Value("${GPTToken}")
    private String GTPtoken;

    private final ObjectMapper mapper;

    private final RestTemplate restTemplate;

    public ChatGPTClient(RestTemplate restTemplate, ObjectMapper mapper) {
        this.restTemplate = restTemplate;
        this.mapper = mapper;
    }

    public void createRequest(String message){

        OpenAiService service = new OpenAiService(GTPtoken);

        ChatCompletionRequest ch = ChatCompletionRequest.builder()
                .messages(List.of(new ChatMessage("user", message)))
                .model("gpt-3.5-turbo")
                .build();

        service.createChatCompletion(ch)
                .getChoices().forEach(System.out::println);
    }

}
