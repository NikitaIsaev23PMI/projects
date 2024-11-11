package ru.nikita.Bot.googlePlacesResponce.placeDetails;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Time {

    @JsonProperty("date")
    private String date;

    @JsonProperty("day")
    private int day;

    @JsonProperty("time")
    private String time;

    @JsonProperty("truncated")
    private boolean truncated;

    public Time(){}
}
