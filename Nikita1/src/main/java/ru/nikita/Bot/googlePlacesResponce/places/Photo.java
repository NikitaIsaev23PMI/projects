package ru.nikita.Bot.googlePlacesResponce.places;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Photo {

    @JsonProperty("height")
    private int height;

    @JsonProperty("html_attributions")
    private List<String> htmlAttributions;

    @JsonProperty("photo_reference")
    private String photoReference;

    @JsonProperty("width")
    private int width;

    public Photo(){}


    @Override
    public String toString() {
        return "Photo{" +
                "height=" + height +
                ", htmlAttributions=" + htmlAttributions +
                ", photoReference='" + photoReference + '\'' +
                ", width=" + width +
                '}';
    }
}
