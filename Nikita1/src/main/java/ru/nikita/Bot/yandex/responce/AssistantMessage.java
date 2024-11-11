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
@Setter
@AllArgsConstructor
public class AssistantMessage {

    @JsonProperty("role")
    private String role;

    @JsonProperty("text")
    private String text;
}
