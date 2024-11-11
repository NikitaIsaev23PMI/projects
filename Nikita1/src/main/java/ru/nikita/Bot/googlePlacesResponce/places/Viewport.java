package ru.nikita.Bot.googlePlacesResponce.places;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Viewport {

    @JsonProperty("northeast")
    private Location northeast;

    @JsonProperty("southwest")
    private Location southwest;

    public Viewport(){}

    @Override
    public String toString() {
        return "Viewport{" +
                "northeast=" + northeast +
                ", southwest=" + southwest +
                '}';
    }
}
