package ru.nikita.Bot.TgBot;

import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.nikita.Bot.Clients.ChatGPTClient;
import ru.nikita.Bot.Clients.Translater;
import ru.nikita.Bot.yandex.YandexGptApi;
import ru.nikita.Bot.Clients.GooglePlaceApiClient;
import ru.nikita.Bot.handlers.MessageHandler;

import java.util.*;

@Getter
public class TelegramBot extends TelegramLongPollingBot {

    @Autowired
    @Lazy
    private MessageHandler messageHandler;

    public TelegramBot(DefaultBotOptions botOptions, String token) {
        super(botOptions, token);
        initializeCommands();
    }

    private void initializeCommands() {
        List<BotCommand> listOfCommands = Arrays.asList(
                new BotCommand("/start", "Получить информацию о функционале бота"),
                new BotCommand("/info", "Помощь в использовании бота"),
                new BotCommand("/clear","удалить историю сообщений")
        );
        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return "OrenburgGO";
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            messageHandler.handleMessage(update);
        } else if (update.hasCallbackQuery()) {
            messageHandler.handleCallbackQuery(update);
        }
    }
}

