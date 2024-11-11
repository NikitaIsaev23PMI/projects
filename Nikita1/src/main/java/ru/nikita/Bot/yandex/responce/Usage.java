package ru.nikita.Bot.yandex.responce;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class Usage {

    @JsonProperty("inputTextTokens")
    private int inputTextTokens;

    @JsonProperty("completionTokens")
    private int completionTokens;

    @JsonProperty("totalTokens")
    private int totalTokens;
}
