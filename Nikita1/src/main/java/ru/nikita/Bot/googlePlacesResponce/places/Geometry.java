package ru.nikita.Bot.googlePlacesResponce.places;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Geometry {

    @JsonProperty("location")
    private Location location;

    @JsonProperty("viewport")
    private Viewport viewport;

    public Geometry(){}


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Geometry{");
        sb.append("location=").append(location);
        sb.append(", viewport=").append(viewport);
        sb.append('}');
        return sb.toString();
    }
}
