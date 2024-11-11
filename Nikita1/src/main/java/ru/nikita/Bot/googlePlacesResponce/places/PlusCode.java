package ru.nikita.Bot.googlePlacesResponce.places;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlusCode {

    @JsonProperty("compound_code")
    private String compoundCode;

    @JsonProperty("global_code")
    private String globalCode;

    public PlusCode(){}

    @Override
    public String toString() {
        return "PlusCode{" +
                "compoundCode='" + compoundCode + '\'' +
                ", globalCode='" + globalCode + '\'' +
                '}';
    }
}
