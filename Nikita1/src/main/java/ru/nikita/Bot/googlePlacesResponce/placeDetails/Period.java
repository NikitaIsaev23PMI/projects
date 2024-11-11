package ru.nikita.Bot.googlePlacesResponce.placeDetails;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Period {

    @JsonProperty("close")
    private Time close;

    @JsonProperty("open")
    private Time open;

    public Period() {}
}
