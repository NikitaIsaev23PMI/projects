package ru.nikita.Bot.googlePlacesResponce.placeDetails;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpeningHours {

    @JsonProperty("open_now")
    private boolean openNow;

    @JsonProperty("periods")
    private List<Period> periods;

    @JsonProperty("weekday_text")
    private List<String> weekdayText;

    public OpeningHours(){}
}
