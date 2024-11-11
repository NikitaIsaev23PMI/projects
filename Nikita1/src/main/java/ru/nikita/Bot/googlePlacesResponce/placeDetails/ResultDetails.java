package ru.nikita.Bot.googlePlacesResponce.placeDetails;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import ru.nikita.Bot.googlePlacesResponce.places.Geometry;
import ru.nikita.Bot.googlePlacesResponce.places.Photo;
import ru.nikita.Bot.googlePlacesResponce.places.PlusCode;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResultDetails {

    @JsonProperty("address_components")
    private List<AddressComponent> addressComponents;

    @JsonProperty("adr_address")
    private String adrAddress;

    @JsonProperty("business_status")
    private String businessStatus;

    @JsonProperty("serves_lunch")
    private String servesLunch;

    @JsonProperty("curbside_pickup")
    private boolean curbsidePickup;

    @JsonProperty("current_opening_hours")
    private OpeningHours currentOpeningHours;

    @JsonProperty("permanently_closed")
    private boolean permanentlyClosed;

    @JsonProperty("serves_breakfast")
    private boolean servesBreakfast;

    @JsonProperty("delivery")
    private boolean delivery;

    @JsonProperty("dine_in")
    private boolean dineIn;

    @JsonProperty("formatted_address")
    private String formattedAddress;

    @JsonProperty("formatted_phone_number")
    private String formattedPhoneNumber;

    @JsonProperty("takeout")
    private boolean takeout;

    @JsonProperty("geometry")
    private Geometry geometry;

    @JsonProperty("icon")
    private String icon;

    @JsonProperty("icon_background_color")
    private String iconBackgroundColor;

    @JsonProperty("icon_mask_base_uri")
    private String iconMaskBaseUri;

    @JsonProperty("international_phone_number")
    private String internationalPhoneNumber;

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

    @JsonProperty("reservable")
    private boolean reservable;

    @JsonProperty("reviews")
    private List<Review> reviews;

    @JsonProperty("serves_dinner")
    private boolean servesDinner;

    @JsonProperty("serves_beer")
    private boolean servesBeer;

    @JsonProperty("serves_wine")
    private boolean servesWine;

    @JsonProperty("types")
    private List<String> types;

    @JsonProperty("url")
    private String url;

    @JsonProperty("user_ratings_total")
    private int userRatingsTotal;

    @JsonProperty("utc_offset")
    private int utcOffset;

    @JsonProperty("vicinity")
    private String vicinity;

    @JsonProperty("website")
    private String website;

    @JsonProperty("wheelchair_accessible_entrance")
    private boolean wheelchairAccessibleEntrance;

    public ResultDetails() {}

    @Override
    public String toString() {
        return "addressComponents=" + addressComponents +
                ", adrAddress='" + adrAddress + '\'' +
                ", businessStatus='" + businessStatus + '\'' +
                ", servesLunch='" + servesLunch + '\'' +
                ", curbsidePickup=" + curbsidePickup +
                ", permanentlyClosed=" + permanentlyClosed +
                ", servesBreakfast=" + servesBreakfast +
                ", delivery=" + delivery +
                ", dineIn=" + dineIn +
                ", formattedAddress='" + formattedAddress + '\'' +
                ", formattedPhoneNumber='" + formattedPhoneNumber + '\'' +
                ", takeout=" + takeout +
                ", geometry=" + geometry +
                ", icon='" + icon + '\'' +
                ", iconBackgroundColor='" + iconBackgroundColor + '\'' +
                ", iconMaskBaseUri='" + iconMaskBaseUri + '\'' +
                ", internationalPhoneNumber='" + internationalPhoneNumber + '\'' +
                ", name='" + name + '\'' +
                ", placeId='" + placeId + '\'' +
                ", plusCode=" + plusCode +
                ", priceLevel=" + priceLevel +
                ", rating=" + rating +
                ", reference='" + reference + '\'' +
                ", reservable=" + reservable +
                ", servesDinner=" + servesDinner +
                ", servesBeer=" + servesBeer +
                ", servesWine=" + servesWine +
                ", types=" + types +
                ", userRatingsTotal=" + userRatingsTotal +
                ", utcOffset=" + utcOffset +
                ", vicinity='" + vicinity + '\'' +
                ", website='" + website + '\'' +
                ", wheelchairAccessibleEntrance=" + wheelchairAccessibleEntrance;
    }
}