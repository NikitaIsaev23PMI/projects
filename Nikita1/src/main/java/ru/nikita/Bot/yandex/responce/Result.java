package ru.nikita.Bot.yandex.responce;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@NoArgsConstructor
@Setter
@AllArgsConstructor
public class Result {

    @JsonProperty("alternatives")
    private List<Alternative> alternatives;

    @JsonProperty("usage")
    private Usage usage;

    @JsonProperty("modelVersion")
    private String modelVersion;
}
