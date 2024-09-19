package org.example.buggybank.service;

import lombok.extern.slf4j.Slf4j;
import org.example.buggybank.config.BotConfig;
import org.example.buggybank.model.User;
import org.example.buggybank.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
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

            switch (messageText) {
                case "/start":
                    registerUser(update.getMessage());
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    break;
                case "/help":
                    sendMessage(chatId, HELP_TEXT);
                    break;
                default:
                    sendMessage(chatId, "Invalid command");
            }
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

        try {
            execute(message);
        }
        catch (TelegramApiException e) {
            log.error("Error send message: {}", e.getMessage(), e);
        }
    }
    private void startCommandReceived(long chatId, String name) {
        String message = String.format(GREETINGS, name);
        sendMessage(chatId, message);
        log.info("Starting command: {}", message);
    }
}
