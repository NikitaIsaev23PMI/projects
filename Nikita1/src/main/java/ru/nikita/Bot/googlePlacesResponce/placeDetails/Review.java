package ru.nikita.Bot.googlePlacesResponce.placeDetails;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Review {

    @JsonProperty("author_name")
    private String authorName;

    @JsonProperty("author_url")
    private String authorUrl;

    @JsonProperty("language")
    private String language;

    @JsonProperty("original_language")
    private String originalLanguage;

    @JsonProperty("profile_photo_url")
    private String profilePhotoUrl;

    @JsonProperty("rating")
    private int rating;

    @JsonProperty("relative_time_description")
    private String relativeTimeDescription;

    @JsonProperty("text")
    private String text;

    @JsonProperty("time")
    private long time;

    @JsonProperty("translated")
    private boolean translated;

    public Review(){
    }
}
