package org.example.buggybank.service;

import lombok.extern.slf4j.Slf4j;
import org.example.buggybank.config.BotConfig;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Service
public class BuggyBankBot extends TelegramLongPollingBot {

    private final BotConfig config;

    public BuggyBankBot(BotConfig config) {
        this.config = config;
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
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    break;
                default:
                    sendMessege(chatId, "Invalid command");
            }
        }
    }

    private void sendMessege (long chatId, String textToSend) {
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
        String answer = "Hi, " + name + ", nice to meet you";
        log.info("Starting command: {}", answer);

        sendMessege(chatId, answer);
    }
}
