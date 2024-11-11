package ru.nikita.Bot.Clients;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import ru.nikita.Bot.googlePlacesResponce.placeDetails.Review;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@Component
@NoArgsConstructor
public class Translater {

    public List<Review> translateComments(List<Review> reviews) {
        try {
            String credentialsPath = "src/main/resources/neon-gist-424015-r8-900b3b066a69.json";

            ServiceAccountCredentials credentials = ServiceAccountCredentials.fromStream(new FileInputStream(credentialsPath));

            Translate translate = TranslateOptions.newBuilder().setCredentials(credentials).build().getService();

            for (Review review : reviews) {
                String comment = review.getText();
                Translation translationComment = translate.translate(comment,
                        Translate.TranslateOption.sourceLanguage("en"),
                        Translate.TranslateOption.targetLanguage("ru"));
                review.setText(translationComment.getTranslatedText());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return reviews;
    }

    @SneakyThrows
    public String translate(String string) {
        String credentialsPath = "src/main/resources/neon-gist-424015-r8-900b3b066a69.json";

        ServiceAccountCredentials credentials = ServiceAccountCredentials.fromStream(new FileInputStream(credentialsPath));

        com.google.cloud.translate.Translate translate = TranslateOptions.newBuilder().setCredentials(credentials).build().getService();

        String translationText = translate.translate(string,
                Translate.TranslateOption.sourceLanguage("en"),
                Translate.TranslateOption.targetLanguage("ru"))
                .getTranslatedText();

        return translationText;
    }
}
