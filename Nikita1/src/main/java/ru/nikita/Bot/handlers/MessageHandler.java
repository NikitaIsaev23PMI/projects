package ru.nikita.Bot.handlers;

import com.vdurmont.emoji.EmojiParser;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessages;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.MessageAutoDeleteTimerChanged;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.nikita.Bot.Clients.ChatGPTClient;
import ru.nikita.Bot.Clients.GooglePlaceApiClient;
import ru.nikita.Bot.Clients.Translater;
import ru.nikita.Bot.TgBot.TelegramBot;
import ru.nikita.Bot.googlePlacesResponce.placeDetails.ResultDetails;
import ru.nikita.Bot.googlePlacesResponce.placeDetails.Review;
import ru.nikita.Bot.googlePlacesResponce.places.Photo;
import ru.nikita.Bot.googlePlacesResponce.places.Result;
import ru.nikita.Bot.yandex.YandexGptApi;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class MessageHandler {

    @Autowired
    private Translater translator;

    @Autowired
    private GooglePlaceApiClient googlePlaceApiClient;

    @Autowired
    private YandexGptApi yandexGptApi;

    private TelegramBot bot;
    private Map<Long, String> userLastMessage = new HashMap<>();
    private List<Integer> botLastMessages = new ArrayList<>();
    private List<Integer> userMessages = new ArrayList<>();

    public MessageHandler(TelegramBot bot) {
        this.bot = bot;
    }

    public void handleMessage(Update update) throws InterruptedException {
        String message = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        String username = update.getMessage().getFrom().getFirstName();

        Integer userMessageId = update.getMessage().getMessageId();
        userMessages.add(userMessageId);

        userLastMessage.put(chatId, message);

        switch (message) {
            case "/info":
                sendInformationAboutBot(chatId);
                break;
            case "/start":
                helloUser(chatId, username);
                break;
            case "/clear":
                clearChat(chatId);
                break;
            case "Рестораны":
            case "Кафе":
            case "Ночные клубы":
                handleEstablishmentRequest(chatId, message);
                break;
            default:
                sendMessage(chatId, "Неизвестная команда. Воспользуйтесь /help для получения помощи.");
                break;
        }
    }

    private void sendInformationAboutBot(long chatId) {

        String helpInformation = EmojiParser.parseToUnicode("Телеграмм-бот OrenburgGO, позволит вам узнать всю информацию о самых популярных заведениях Оренбурга \n"
                +"просто выберите интерисующий вас тип заведения" + "\n"
                +"после чего вам станет доступен список из 20 заведений данного типа, выберите родно из них и узнайте подробную информацию" + "\n" + "\n"
                +"Возникли проблемы?" + "\n" + "Опишите проблему в сообщении отправив Email на почту: nikitaisaev139@gmail.com");
        sendMessage(chatId,helpInformation);
    }

    private void handleEstablishmentRequest(long chatId, String message) {
        sendEstablishmentsList(chatId, message);
        sendMessageWithFilterOptions(chatId);
    }

    private void sendMessageWithFilterOptions(long chatId) {
        sendMessageWithKeyboard(chatId, "Выберите как отфильтровать заведения", createInlineKeyboardMarkup());
    }

    public void handleCallbackQuery(Update update) {
        String callbackQuery = update.getCallbackQuery().getData();
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        int messageId = update.getCallbackQuery().getMessage().getMessageId();
        String userMessage = userLastMessage.get(chatId);
        processCallbackQuery(callbackQuery, userMessage, chatId, messageId);
    }

    private void processCallbackQuery(String callbackQuery, String userMessage, long chatId, int messageId) {
        Integer lastBotMessage = botLastMessages.get(botLastMessages.size() - 1);
        List<String> resultList;
        String nameofEstablishment = (callbackQuery.contains(":")) ? callbackQuery.split(":")[1] : null;
        callbackQuery = (callbackQuery.contains(":")) ? callbackQuery.split(":")[0] : callbackQuery;

        switch (callbackQuery) {
            case "RATING":
                resultList = getSortedEstablishments(userMessage, Comparator.comparingDouble(Result::getRating), true);
                updateMessage(chatId, resultList, messageId, "Отсортированные заведения по рейтингу");
                break;
            case "PRICE":
                resultList = getSortedEstablishments(userMessage, Comparator.comparingInt(Result::getPriceLevel), false);
                updateMessage(chatId, resultList, messageId, "Отсортированные заведения по стоимости");
                break;
            case "PHOTO":
                sendPhotos(chatId, nameofEstablishment, userMessage);
                break;
            case "DETAILS":
                sendAllDetails(chatId, nameofEstablishment, userMessage);
                break;
            case "COMMENT":
                sendComments(chatId, nameofEstablishment, userMessage);
                break;
            case "NotFound":
                sendMessage(chatId, "Ссылка недоступна");
                break;
            case "HELPAI":
                createApiQuestion(userMessage, nameofEstablishment, chatId);
                break;
            default:
                SendInformationOfEstablishment(userMessage, callbackQuery, chatId);
                break;
        }
    }

    private void createApiQuestion(String userMessage, String nameofEstablishment, long chatId) {
        String placeID = getPlaceId(userMessage, nameofEstablishment);
        ResultDetails resultDetails = googlePlaceApiClient.EstablishmentDetails(placeID);
        String informationOfEstablishmentInJSON = googlePlaceApiClient.EstablishmentDetailsJSON(placeID);
        String question = "Вот информация о заведении " + informationOfEstablishmentInJSON;
        String result = yandexGptApi.createRequest(question);

        sendMessage(chatId, "Ожидайте...");
        sleepAndDeleteLastBotMessage(chatId);
        sendMessage(chatId, result);
        sendHelp(nameofEstablishment, chatId, resultDetails.getWebsite(), resultDetails.getUrl());
    }

    private void sleepAndDeleteLastBotMessage(long chatId) {
        try {
            Thread.sleep(3000);
            deleteLastBotMessage(botLastMessages.get(botLastMessages.size() - 1), chatId);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void deleteLastBotMessage(Integer lastBotMessage, long chatId) {
        deleteMessages(Collections.singletonList(lastBotMessage), chatId);
    }

    private void deleteMessages(List<Integer> messageIds, long chatId) {
        DeleteMessages deleteMessages = new DeleteMessages();
        deleteMessages.setChatId(chatId);
        deleteMessages.setMessageIds(messageIds);
        try {
            bot.execute(deleteMessages);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendComments(long chatId, String nameofEstablishment, String userMessage) {
        String placeID = getPlaceId(userMessage, nameofEstablishment);
        ResultDetails resultDetails = googlePlaceApiClient.EstablishmentDetails(placeID);
        List<Review> translatedComments = translator.translateComments(resultDetails.getReviews());

        String commentsText = buildCommentsText(translatedComments, resultDetails.getRating());
        sendMessage(chatId, commentsText);
        sendHelp(nameofEstablishment, chatId, resultDetails.getWebsite(), resultDetails.getUrl());
    }

    private String buildCommentsText(List<Review> reviews, double rating) {
        StringBuilder sb = new StringBuilder();
        String line = EmojiParser.parseToUnicode(":heavy_minus_sign:").repeat(20) + "\n";
        for (Review review : reviews) {
            String ratingStars = EmojiParser.parseToUnicode(":star:").repeat(review.getRating());
            sb.append(review.getAuthorName()).append("\n\n")
                    .append(ratingStars).append("\n\n")
                    .append(review.getText().isBlank() ? "" : EmojiParser.parseToUnicode(":fire:") + review.getText() + EmojiParser.parseToUnicode(":fire:") + "\n")
                    .append("\n").append(line);
        }
        sb.append("Средняя оценка заведения ").append(EmojiParser.parseToUnicode(":boom:")).append(rating).append(EmojiParser.parseToUnicode(":boom:"));
        return sb.toString();
    }

    private void clearChat(long chatId) throws InterruptedException {
        DeleteMessages deleteMessages = new DeleteMessages();
        deleteMessages.setChatId(chatId);
        deleteMessages.setMessageIds(userMessages);
        try {
            bot.execute(deleteMessages);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        DeleteMessages deleteMessages2 = new DeleteMessages();
        deleteMessages2.setChatId(chatId);
        deleteMessages2.setMessageIds(botLastMessages);
        try {
            bot.execute(deleteMessages2);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendAllDetails(long chatId, String nameofEstablishment, String userMessage) {
        try {
            String placeID = getPlaceId(userMessage, nameofEstablishment);
            ResultDetails resultDetails = googlePlaceApiClient.EstablishmentDetails(placeID);
            String detailsText = buildDetailsText(resultDetails, userMessage);
            sendMessage(chatId, detailsText);
            sendHelp(nameofEstablishment, chatId, resultDetails.getWebsite(), resultDetails.getUrl());
        } catch (NullPointerException e) {
            e.printStackTrace();
            sendMessage(chatId, "Проблема с поиском информации о заведении");
        }
    }

    private String buildDetailsText(ResultDetails resultDetails, String userMessage) {

        String servicesInfo = "Услуги: " + "\n" +
                (resultDetails.isServesBeer() ? "Подача пива \n" : "") +
                (resultDetails.isDelivery() ? "Доставка \n" : "") +
                (resultDetails.isServesDinner() ? "Ужин \n" : "") +
                (resultDetails.isReservable() ? "Бронирование столов \n" : "") +
                (resultDetails.isServesBreakfast() ? "Завтрак \n" : "") +
                (resultDetails.getServesLunch() != null ? "Обед \n" : "") +
                (resultDetails.isTakeout() ? "Еда с собой" : "");

        String line = "\n" + EmojiParser.parseToUnicode(":heavy_minus_sign:").repeat(20) + "\n";

        String lunch = resultDetails.getServesLunch() == null ? "false" : resultDetails.getServesLunch();

        return resultDetails.getName() + " " +
                (userMessage.equals("Рестораны") ? EmojiParser.parseToUnicode(":sushi::plate_with_cutlery::wine_glass:") :
                        userMessage.equals("Кафе") ? EmojiParser.parseToUnicode(":waffle::pizza::bowl_with_spoon:") :
                                EmojiParser.parseToUnicode(":tumbler_glass::dancers::man_dancing:")) +
                line + "Адрес: " + resultDetails.getVicinity() + line +
                "Контактный телефон " + resultDetails.getFormattedPhoneNumber() + line +
                "Сейчас заведение " + (resultDetails.getOpeningHours().isOpenNow() ? "Открыто" : "Закрыто") + line +
                servicesInfo + "\n" + line + "\n" +
                (resultDetails.isReservable() && resultDetails.getWebsite() != null ? "Можно забронировать столик по номеру " + resultDetails.getFormattedPhoneNumber() +
                        " Или на сайте " + resultDetails.getWebsite() + line : "");



        /*resultDetails.getName() + " " +
                (userMessage.equals("Рестораны") ? EmojiParser.parseToUnicode(":sushi::plate_with_cutlery::wine_glass:") :
                        userMessage.equals("Кафе") ? EmojiParser.parseToUnicode(":waffle::pizza::bowl_with_spoon:") :
                                EmojiParser.parseToUnicode(":tumbler_glass::dancers::man_dancing:")) +
                line + "Адрес: " + resultDetails.getVicinity() + line +
                "Контактный телефон " + resultDetails.getFormattedPhoneNumber() + line +
                "Сейчас заведение " + (resultDetails.getOpeningHours().isOpenNow() ? "Открыто" : "Закрыто") + line +
                (resultDetails.isDelivery() ? "Есть доставка" + line : "") +
                (resultDetails.isServesBreakfast() ? "Можно позавтракать" + line : "") +
                (lunch.equals("false") ? "" : "Можно пообедать" + line) +
                (resultDetails.isServesDinner() ? "Можно поужинать" + line : "") +
                (resultDetails.isServesBeer() ? "Подают Пиво" + line : "") +
                (resultDetails.isServesWine() ? "Подают Вино" + line : "") +
                (resultDetails.isWheelchairAccessibleEntrance() ? "Есть условия для инвалидных колясок" + line : "") +
                servicesInfo + "\n" + line + "\n" +
                (resultDetails.isReservable() && resultDetails.getWebsite() != null ? "Можно забронировать столик по номеру " + resultDetails.getFormattedPhoneNumber() +
                        " Или на сайте " + resultDetails.getWebsite() + line : "");*/
    }

    private void sendPhotos(long chatId, String nameOfEstablishment, String userMessage) {
        String establishmentId = getPlaceId(userMessage, nameOfEstablishment);
        ResultDetails resultDetails = googlePlaceApiClient.EstablishmentDetails(establishmentId);
        sendPhotos(chatId, resultDetails, nameOfEstablishment);
    }

    private void sendPhotos(long chatId, ResultDetails resultDetails, String nameOfEstablishment) {
        String url = resultDetails.getWebsite();
        String mapsUrl = resultDetails.getUrl();
        try {
            if (resultDetails.getPhotos().size() == 1) {
                sendSinglePhoto(chatId, resultDetails.getPhotos().get(0), "Найдена всего одна фотография", nameOfEstablishment, url, mapsUrl);
            } else if (resultDetails.getPhotos().size() > 1 && resultDetails.getPhotos().size() <= 10) {
                sendMultiplePhotos(chatId, resultDetails.getPhotos(), nameOfEstablishment, url, mapsUrl);
            }
        } catch (NullPointerException e) {
            sendMessage(chatId, "Фотографий не найдено");
        }
    }

    private void sendSinglePhoto(long chatId, Photo photo, String caption, String nameOfEstablishment, String url, String mapsUrl) {
        String photoURL = googlePlaceApiClient.getPhotoUrl(photo.getPhotoReference());
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);
        sendPhoto.setCaption(caption);
        sendPhoto.setPhoto(new InputFile(photoURL));
        sendPhoto.setReplyMarkup(sendHelpButtons(nameOfEstablishment, url, mapsUrl));
        try {
            botLastMessages.add(bot.execute(sendPhoto).getMessageId());
            sendHelp(nameOfEstablishment, chatId, url, mapsUrl);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendMultiplePhotos(long chatId, List<Photo> photos, String nameOfEstablishment, String url, String mapsUrl) {
        SendMediaGroup sendMediaGroup = new SendMediaGroup();
        List<InputMedia> mediaPhotos = photos.stream()
                .map(photo -> new InputMediaPhoto(googlePlaceApiClient.getPhotoUrl(photo.getPhotoReference())))
                .collect(Collectors.toList());
        sendMediaGroup.setChatId(chatId);
        sendMediaGroup.setMedias(mediaPhotos);
        try {
            bot.execute(sendMediaGroup).stream()
                    .forEach(a -> botLastMessages.add(a.getMessageId()));
            sendHelp(nameOfEstablishment, chatId, url, mapsUrl);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private String getPlaceId(String userMessage, String nameOfEstablishment) {
        return googlePlaceApiClient.getAllEstablishments(userMessage).getResults().stream()
                .filter(place -> place.getName().replaceAll(" ", "").equals(nameOfEstablishment))
                .findFirst()
                .orElse(null).getPlaceId();
    }

    private List<String> getSortedEstablishments(String userMessage, Comparator<Result> comparator, boolean sortByRating) {
        return googlePlaceApiClient.getAllEstablishments(userMessage).getResults().stream()
                .sorted(comparator)
                .map(result -> sortByRating ? result.getName() + ": " + result.getRating() : result.getName() + ": " + result.getPriceLevel())
                .collect(Collectors.toList());
    }

    private void sendEstablishmentsList(long chatId, String formValue) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Выберите " + formValue);

        List<List<InlineKeyboardButton>> rowsInline = createInlineKeyboardButtons(googlePlaceApiClient.getStringEstablishments(formValue));
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        markupInline.setKeyboard(rowsInline);
        message.setReplyMarkup(markupInline);

        try {
            botLastMessages.add(bot.execute(message).getMessageId());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private List<List<InlineKeyboardButton>> createInlineKeyboardButtons(List<String> establishments) {
        return establishments.stream()
                .filter(establishment -> establishment.replaceAll(" ", "").length() < 64)
                .map(establishment -> {
                    InlineKeyboardButton btn = new InlineKeyboardButton();
                    btn.setText(establishment);
                    btn.setCallbackData(establishment.replaceAll(" ", ""));
                    return Collections.singletonList(btn);
                })
                .collect(Collectors.toList());
    }

    private InlineKeyboardMarkup createInlineKeyboardMarkup() {
        InlineKeyboardButton buttonRating = createInlineKeyboardButton("По рейтингу", "RATING");
        InlineKeyboardButton buttonPrice = createInlineKeyboardButton("По стоимости", "PRICE");

        List<InlineKeyboardButton> row1 = Arrays.asList(buttonRating, buttonPrice);
        List<List<InlineKeyboardButton>> rowsInline = Collections.singletonList(row1);

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        markupInline.setKeyboard(rowsInline);
        return markupInline;
    }

    private InlineKeyboardButton createInlineKeyboardButton(String text, String callbackData) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callbackData);
        return button;
    }

    private void updateMessage(long chatId, List<String> resultList, int messageId, String newText) {
        updateMessageText(chatId, messageId, newText);
        updateMessageKeyboard(chatId, resultList, messageId);
        sendMessageWithFilterOptions(chatId);
    }

    private void updateMessageText(long chatId, int messageId, String newText) {
        EditMessageText message = new EditMessageText();
        message.setChatId(String.valueOf(chatId));
        message.setMessageId(messageId);
        message.setText(newText);

        try {
            bot.execute(message);
            botLastMessages.add(message.getMessageId());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void updateMessageKeyboard(long chatId, List<String> resultList, int messageId) {
        List<List<InlineKeyboardButton>> rowsInline = createInlineKeyboardButtons(resultList);
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        markupInline.setKeyboard(rowsInline);

        EditMessageReplyMarkup replyMarkup = new EditMessageReplyMarkup();
        replyMarkup.setChatId(String.valueOf(chatId));
        replyMarkup.setMessageId(messageId);
        replyMarkup.setReplyMarkup(markupInline);

        try {
            bot.execute(replyMarkup);
            botLastMessages.add(replyMarkup.getMessageId());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void helloUser(long chatId, String username) {
        String resultString = EmojiParser.parseToUnicode(
                ":fire: Привет " + username + " :fire:\n" +
                        "Этот бот поможет тебе выбрать подходящее для тебя заведение в твоем городе :wink:\n" +
                        "Выбери интересующий тебя тип заведения снизу :point_down:"
        );
        selectionOfEstablishments(chatId, resultString);
    }

    private void selectionOfEstablishments(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        message.setReplyMarkup(createReplyKeyboardMarkup());

        try {
            botLastMessages.add(bot.execute(message).getMessageId());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private ReplyKeyboardMarkup createReplyKeyboardMarkup() {
        List<KeyboardRow> keyboardRows = Arrays.asList(
                createKeyboardRow("Рестораны"),
                createKeyboardRow("Кафе"),
                createKeyboardRow("Ночные клубы")
        );

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setKeyboard(keyboardRows);

        return keyboardMarkup;
    }

    private KeyboardRow createKeyboardRow(String buttonText) {
        KeyboardRow row = new KeyboardRow();
        row.add(buttonText);
        return row;
    }

    public void sendMessage(long chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(text);
        sendMessage.setChatId(String.valueOf(chatId));

        try {
            botLastMessages.add(bot.execute(sendMessage).getMessageId());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void SendInformationOfEstablishment(String TypeOfEstablishment, String callbackQuery, long chatId) {
        callbackQuery = (callbackQuery.contains(":")) ? callbackQuery.split(":")[0] : callbackQuery;
        String finalCallbackQuery = callbackQuery;

        Optional<Result> establishment = googlePlaceApiClient.getAllEstablishments(TypeOfEstablishment).getResults().stream()
                .filter(a -> a.getName().replaceAll(" ", "").equals(finalCallbackQuery))
                .findFirst();

        if (establishment.isPresent()) {
            Result resultEstablishment = establishment.get();
            String placeId = resultEstablishment.getPlaceId();
            ResultDetails placeDetails = googlePlaceApiClient.EstablishmentDetails(placeId);
            String text = buildEstablishmentInfoText(placeDetails);
            sendMessageWithKeyboard(chatId, text, sendHelpButtons(placeDetails.getName().replaceAll(" ", ""), placeDetails.getWebsite(), placeDetails.getUrl()));
        }
    }

    private String buildEstablishmentInfoText(ResultDetails placeDetails) {
        /*String priceLevelInfo = (placeDetails.getPriceLevel() == 0) ? "Сведений по ценовой политике нет" :
                (placeDetails.getPriceLevel() > 2) ? "Цены в " + placeDetails.getName() + " Довольно высокие" :
                        "Цены в " + placeDetails.getName() + " Довольно низкие";
        return placeDetails.getName() + " который находится по адресу " + placeDetails.getFormattedAddress() +
                "\n" + "Сейчас данное заведение " + "\n" +
                "Заведение имеет рейтинг: " + placeDetails.getRating() + (placeDetails.getRating() > 3 ? "Что довольно неплохо" : "Что вызывает сомнения в выборе данного заведения") +
                placeDetails.getName() + " " + priceLevelInfo + " " + placeDetails.getPlaceId();*/

        return EmojiParser.parseToUnicode(placeDetails.getName() + " \n"
        + "Выберите интерисущие вас пункты о этом заведении, так же вы можете стпросить у ИИ стоит ли это место посещения" + "\n"
        + "Кликнув по окну \"Стоит ли посещать?:thinking:\"");
    }

    private void sendMessageWithKeyboard(long chatId, String text, InlineKeyboardMarkup replyMarkup) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        sendMessage.setReplyMarkup(replyMarkup);
        try {
            botLastMessages.add(bot.execute(sendMessage).getMessageId());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private InlineKeyboardMarkup sendHelpButtons(String nameOfEstablishment, String url, String mapsUrl) {
        InlineKeyboardButton photoButton = createInlineKeyboardButton(EmojiParser.parseToUnicode("Фотографии:camera_flash:"), "PHOTO:" + nameOfEstablishment);
        InlineKeyboardButton websiteButton = createUrlOrCallbackButton(EmojiParser.parseToUnicode("Сайт заведения:globe_with_meridians:"), url, "NotFound");
        InlineKeyboardButton allDetailsButton = createInlineKeyboardButton(EmojiParser.parseToUnicode("Вся информация:scroll:"), "DETAILS:" + nameOfEstablishment);
        InlineKeyboardButton commentButton = createInlineKeyboardButton(EmojiParser.parseToUnicode("Комментарии:chart_with_upwards_trend::chart_with_downwards_trend:"), "COMMENT:" + nameOfEstablishment);
        InlineKeyboardButton helpAIButton = createInlineKeyboardButton(EmojiParser.parseToUnicode("Стоит ли посещать?:thinking:"), "HELPAI:" + nameOfEstablishment);
        InlineKeyboardButton mapsButton = createUrlOrCallbackButton(EmojiParser.parseToUnicode("Посмотреть на карте:world_map:"), mapsUrl, "NotFound");

        List<InlineKeyboardButton> row1 = Arrays.asList(photoButton, allDetailsButton);
        List<InlineKeyboardButton> row2 = Arrays.asList(websiteButton, commentButton);
        List<InlineKeyboardButton> row3 = Arrays.asList(mapsButton, helpAIButton);

        List<List<InlineKeyboardButton>> rowsInline = Arrays.asList(row1, row2, row3);

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        markupInline.setKeyboard(rowsInline);
        return markupInline;
    }

    private InlineKeyboardButton createUrlOrCallbackButton(String text, String url, String callbackData) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        if (url != null) {
            button.setUrl(url);
        } else {
            button.setCallbackData(callbackData);
        }
        return button;
    }

    private void sendHelp(String nameOfEstablishment, long chatId, String url, String mapsUrl) {
        String text = EmojiParser.parseToUnicode(nameOfEstablishment);
        sendMessageWithKeyboard(chatId, text, sendHelpButtons(nameOfEstablishment, url, mapsUrl));
    }
}
