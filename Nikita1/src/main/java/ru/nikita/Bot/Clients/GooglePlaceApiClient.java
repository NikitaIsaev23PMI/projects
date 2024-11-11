package ru.nikita.Bot.Clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import lombok.SneakyThrows;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;



import org.springframework.web.util.UriComponentsBuilder;
import ru.nikita.Bot.googlePlacesResponce.placeDetails.GooglePlaceDetails;
import ru.nikita.Bot.googlePlacesResponce.placeDetails.ResultDetails;
import ru.nikita.Bot.googlePlacesResponce.placeDetails.Review;
import ru.nikita.Bot.googlePlacesResponce.places.GooglePlacesResponse;


import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class GooglePlaceApiClient {

    @Value("${google.key}")
    private String key;

    private final RestTemplate restTemplate;

    private final ObjectMapper mapper;

    @Autowired
    GooglePlaceApiClient(RestTemplate restTemplate, ObjectMapper mapper) {
        this.restTemplate = restTemplate;
        this.mapper = mapper;
    }

    @SneakyThrows
    public GooglePlacesResponse getAllEstablishments(String value) {

        URI uri = UriComponentsBuilder.newInstance()
                .scheme("https")
                .host("maps.googleapis.com")
                .path("/maps/api/place/textsearch/json")
                .queryParam("query", value + " in Orenburg")
                .queryParam("key", key)
                //     .queryParam("language", "en")
                .build()
                .toUri();

        ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);

        GooglePlacesResponse googlePlacesResponse = mapper.readValue(response.getBody(), GooglePlacesResponse.class);

        return googlePlacesResponse;
    }


    public  String ResultStringOfEstablishments(String value) {
        List<String> names = getAllEstablishments(value)
                .getResults()
                .stream()
                .map(ru.nikita.Bot.googlePlacesResponce.places.Result::getName)
                .collect(Collectors.toUnmodifiableList());

        StringBuilder sb = new StringBuilder();

        sb.append(value).append("\n");

        for (String name : names) {
            sb.append(name);
            sb.append("\n");
            sb.append("\n");
        }

        return sb.toString();
    }

    public String listToString(List<String> list) {
        StringBuilder sb = new StringBuilder();
        for (String name : list) {
            sb.append(name);
            sb.append("\n");
        }
        return sb.toString();
    }

    public String getPhotoUrl(String photoReference) {
        return "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + photoReference + "&key=" + key;
    }

    public List<String> getStringEstablishments(String value) {
        List<String> resultListOfEstablishments = new ArrayList<>();
        List<ru.nikita.Bot.googlePlacesResponce.places.Result> results = getAllEstablishments(value).getResults();
        for (ru.nikita.Bot.googlePlacesResponce.places.Result result : results) {
            resultListOfEstablishments.add(result.getName());
        }
        return resultListOfEstablishments;
    }


    @SneakyThrows
    public ResultDetails EstablishmentDetails(String PlaceId) {

        URI uri = UriComponentsBuilder.newInstance()
                .scheme("https")
                .host("maps.googleapis.com")
                .path("/maps/api/place/details/json")
                .queryParam("place_id", PlaceId)
                .queryParam("key", key)
                //.queryParam("language", "en")
                .build()
                .toUri();

        ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);

        ObjectMapper objectMapper = new ObjectMapper();

        GooglePlaceDetails placeDetails = objectMapper.readValue(response.getBody(), GooglePlaceDetails.class);
        return placeDetails.getResult();
    }

    @SneakyThrows
    public String EstablishmentDetailsJSON(String PlaceId) {

        URI uri = UriComponentsBuilder.newInstance()
                .scheme("https")
                .host("maps.googleapis.com")
                .path("/maps/api/place/details/json")
                .queryParam("place_id", PlaceId)
                .queryParam("key", key)
                //.queryParam("language", "en")
                .build()
                .toUri();

        ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);

        ObjectMapper objectMapper = new ObjectMapper();

        GooglePlaceDetails placeDetails = objectMapper.readValue(response.getBody(), GooglePlaceDetails.class);

        ResultDetails r = placeDetails.getResult();

        StringBuilder sb = new StringBuilder();
        int c = 0;
        for(Review review : r.getReviews()) {
            sb.append('|');
            sb.append(review.getText());
            sb.append('|');
            c++;
            if(c == 3){
                break;
            }
        }
        String f = sb.toString();

        String resultString = "название " + r.getName()
        +" Рейтинг " + r.getRating() + " уровень цен(от 1 до 5)" + r.getPriceLevel()
        +" можно позавтракать? " + r.isServesBreakfast() + " можно пообедать? " + (r.getServesLunch() == "true" ? "true" : "false")
                + " можно поужинать? " + r.isServesDinner() + " есть ли доставка? " + r.isDelivery()
                +" есть ли пиво? " + r.isServesBeer() + " вот  несколько коменнтариев посетителей" + f;

        return resultString;
    }
}



