package ru.nikita.Bot.googlePlacesResponce.places;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GooglePlacesResponse {

    @JsonProperty("html_attributions")
    private List<Object> htmlAttributions;

    @JsonProperty("next_page_token")
    private String nextPageToken;

    @JsonProperty("results")
    private List<Result> results;

    @JsonProperty("status")
    private String status;

    public GooglePlacesResponse(){}


    @Override
    public String toString() {
        return "GooglePlacesResponse{" +
                "htmlAttributions=" + htmlAttributions +
                ", results=" + results +
                ", status='" + status + '\'' +
                '}';
    }
}
