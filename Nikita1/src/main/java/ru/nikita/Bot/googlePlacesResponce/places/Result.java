package ru.nikita.Bot.googlePlacesResponce.places;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.nikita.Bot.googlePlacesResponce.placeDetails.OpeningHours;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Result {

    @JsonProperty("business_status")
    private String businessStatus;

    @JsonProperty("formatted_address")
    private String formattedAddress;

    @JsonProperty("geometry")
    private Geometry geometry;

    @JsonProperty("icon")
    private String icon;

    @JsonProperty("icon_background_color")
    private String iconBackgroundColor;

    @JsonProperty("icon_mask_base_uri")
    private String iconMaskBaseUri;

    @JsonProperty("name")
    private String name;

    @JsonProperty("opening_hours")
    private OpeningHours openingHours;

    @JsonProperty("photos")
    private List<Photo> photos;

    @JsonProperty("place_id")
    private String placeId;

    @JsonProperty("plus_code")
    private PlusCode plusCode;

    @JsonProperty("price_level")
    private int priceLevel;

    @JsonProperty("rating")
    private double rating;

    @JsonProperty("reference")
    private String reference;

    @JsonProperty("types")
    private List<String> types;

    @JsonProperty("user_ratings_total")
    private int userRatingsTotal;

    public Result(){}

    @Override
    public String toString() {
        return "Result{" +
                "photos=" + photos +
                ", userRatingsTotal=" + userRatingsTotal +
                ", types=" + types +
                ", reference='" + reference + '\'' +
                ", rating=" + rating +
                ", priceLevel=" + priceLevel +
                ", placeId='" + placeId + '\'' +
                ", plusCode=" + plusCode +
                ", name='" + name + '\'' +
                ", openingHours=" + openingHours +
                ", iconMaskBaseUri='" + iconMaskBaseUri + '\'' +
                ", iconBackgroundColor='" + iconBackgroundColor + '\'' +
                ", geometry=" + geometry +
                ", businessStatus='" + businessStatus + '\'' +
                ", formattedAddress='" + formattedAddress + '\'' +
                ", icon='" + icon + '\'' +
                '}';
    }
}
