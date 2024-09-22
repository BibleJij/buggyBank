package org.example.buggybank.service;

import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.example.buggybank.config.BotConfig;
import org.example.buggybank.model.User;
import org.example.buggybank.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.example.buggybank.constant.ConstantBuggyBankBot.*;

@Slf4j
@Service
public class BuggyBankBot extends TelegramLongPollingBot {

    private final BotConfig config;

    @Autowired
    private UserRepository userRepository;

    public BuggyBankBot(BotConfig config) {

        this.config = config;

        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand(COMMAND_START, DESCRIPTION_START));
        listOfCommands.add(new BotCommand(COMMAND_MY_DATA, DESCRIPTION_MY_DATA));
        listOfCommands.add(new BotCommand(COMMAND_DELETE_DATA, DESCRIPTION_DELETE_DATA));
        listOfCommands.add(new BotCommand(COMMAND_HELP, DESCRIPTION_HELP));
        listOfCommands.add(new BotCommand(COMMAND_SETTINGS, DESCRIPTION_SETTINGS));
        listOfCommands.add(new BotCommand("/register", "OK"));

        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        }
        catch (TelegramApiException e) {
            log.error("Error setting bot's command list: {}", e.getMessage(), e);
        }
    }

    @Override
    public String getBotUsername() {

        return config.getBotName();
    }

    @Override
    public String getBotToken() {

        return config.getBotToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()) {

            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            if(messageText.contains("/send") && config.getAdminId() == chatId) {
                var textSend = EmojiParser.parseToUnicode(messageText.substring(messageText.indexOf(" ")));
                var users = userRepository.findAll();
                for (User user : users) {
                    sendMessage(user.getChatId(), textSend);
                }
            }
            else {
                switch (messageText) {
                    case "/start":
                        registerUser(update.getMessage());
                        startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                        break;
                    case "/help":
                        sendMessage(chatId, HELP_TEXT);
                        break;
                    case "/deletedata":
                        deleteUser(update.getMessage());
                        sendMessage(chatId, "delete");
                        break;
                    case "/register":
                        register(chatId);
                        break;
                    default:
                        sendMessage(chatId, "Invalid command");
                }
            }
        } else if (update.hasCallbackQuery()) {
            String callbackQuery = update.getCallbackQuery().getData();
            long messageId = update.getCallbackQuery().getMessage().getMessageId();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            if (callbackQuery.equals(YES_BUTTON)) {
                String text = "You press yes button";
                executeEditMessageText(text,chatId,messageId);

            } else if (callbackQuery.equals(NO_BUTTON)) {
                String text = "You press no button";
                executeEditMessageText(text,chatId,messageId);
            }
        }

    }

    private void deleteUser(Message message) {

        if (userRepository.findById(message.getFrom().getId()).isPresent()) {
            userRepository.deleteById(message.getFrom().getId());
        }
    }
    private void registerUser(Message message) {

        if(userRepository.findById(message.getChatId()).isEmpty()) {
            var chatId = message.getChatId();
            var chat = message.getChat();

            User user = new User();
            user.setChatId(chatId);
            user.setFirstName(chat.getFirstName());
            user.setLastName(chat.getLastName());
            user.setUserName(chat.getUserName());
            user.setRegisteredDate(new Timestamp(System.currentTimeMillis()));

            userRepository.save(user);
            log.info("Registered user: {}", user.getUserName());
        }
    }

    private void sendMessage(long chatId, String textToSend) {

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(textToSend);

        executeMessage(message);
    }

    private void startCommandReceived(long chatId, String name) {
        String message = String.format(GREETINGS, name);
        firstKeyboard(chatId, message);
        log.info("Starting command: {}", message);
    }

    private void register(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Вы действительно хотите зарегистрироваться?");

        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLne = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        var yesButton = new InlineKeyboardButton();

        yesButton.setText("Yes");
        yesButton.setCallbackData(YES_BUTTON);

        var noButton = new InlineKeyboardButton();
        noButton.setText("No");
        noButton.setCallbackData(NO_BUTTON);
        rowInline.add(yesButton);
        rowInline.add(noButton);
        rowsInLne.add(rowInline);

        markupInLine.setKeyboard(rowsInLne);
        message.setReplyMarkup(markupInLine);

        executeMessage(message);
    }

    private void firstKeyboard(long chatId, String textToSend) {

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(textToSend);

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();

        row.add("weather");
        row.add("temperature");
        row.add("humidity");
        keyboard.add(row);

        row = new KeyboardRow();

        row.add("register");
        row.add("sex");
        row.add("branch");
        keyboard.add(row);

        replyKeyboardMarkup.setKeyboard(keyboard);
        message.setReplyMarkup(replyKeyboardMarkup);
        executeMessage(message);
    }

    private void executeEditMessageText(String text, long chatId, long messageId) {
        EditMessageText message = new EditMessageText();
        message.setChatId(chatId);
        message.setText(text);
        message.setMessageId((int) messageId);

        try {
            execute(message);
        }
        catch (TelegramApiException e) {
            log.error("Error send message: {}", e.getMessage(), e);
        }
    }

    private void executeMessage(SendMessage message) {
        try {
            execute(message);
        }
        catch (TelegramApiException e) {
            log.error("Error send message: {}", e.getMessage(), e);
        }
    }
}
