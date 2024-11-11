package ru.nikita.Bot.config;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.nikita.Bot.TgBot.TelegramBot;

@Configuration
public class TelegramBotInitializer {
    @SneakyThrows
    @Bean
    public TelegramBot telegramBot(@Value("${bot.token}") String token, TelegramBotsApi api) {
        var botOptions = new DefaultBotOptions();
        var bot =  new TelegramBot(botOptions, token);
        api.registerBot(bot);
        return bot;
    }

    @SneakyThrows
    @Bean
    public TelegramBotsApi telegramBotsApi() {
        return new TelegramBotsApi(DefaultBotSession.class);
    }

}
