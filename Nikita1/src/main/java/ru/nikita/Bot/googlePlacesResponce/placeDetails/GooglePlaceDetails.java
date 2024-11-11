package ru.nikita.Bot.googlePlacesResponce.placeDetails;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GooglePlaceDetails {

    @JsonProperty("html_attributions")
    private List<Object> htmlAttributions;

    @JsonProperty("result")
    private ResultDetails result;
}
