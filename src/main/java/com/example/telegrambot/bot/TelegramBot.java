package com.example.telegrambot.bot;

import com.example.telegrambot.config.TelegramBotProperties;
import com.example.telegrambot.handler.UpdateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * Main Telegram bot implementation using long polling.
 */
@Component
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {

    private final TelegramBotProperties botProperties;
    private final List<UpdateHandler> updateHandlers;
    private final Executor taskExecutor;

    public TelegramBot(TelegramBotProperties botProperties, 
                      List<UpdateHandler> updateHandlers,
                      @Qualifier("telegramTaskExecutor") Executor taskExecutor) {
        super(botProperties.getToken());
        this.botProperties = botProperties;
        this.taskExecutor = taskExecutor;
        // Sort handlers by priority
        this.updateHandlers = updateHandlers.stream()
                .sorted(Comparator.comparingInt(UpdateHandler::getPriority))
                .toList();
        
        log.info("Telegram bot initialized with {} handlers", updateHandlers.size());
    }

    @Override
    public String getBotUsername() {
        return botProperties.getUsername();
    }

    @Override
    public String getBotToken() {
        return botProperties.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        // Process updates asynchronously
        taskExecutor.execute(() -> processUpdate(update));
    }

    private void processUpdate(Update update) {
        try {
            log.debug("Processing update: {}", update.getUpdateId());
            
            // Find the first handler that can process this update
            UpdateHandler handler = updateHandlers.stream()
                    .filter(h -> h.canHandle(update))
                    .findFirst()
                    .orElse(null);

            if (handler != null) {
                BotApiMethod<?> response = handler.handle(update);
                if (response != null) {
                    execute(response);
                }
            } else {
                log.warn("No handler found for update: {}", update.getUpdateId());
                handleUnknownUpdate(update);
            }
            
        } catch (Exception e) {
            log.error("Error processing update {}: {}", update.getUpdateId(), e.getMessage(), e);
            handleError(update, e);
        }
    }

    private void handleUnknownUpdate(Update update) {
        try {
            if (update.hasMessage() && update.getMessage().hasText()) {
                SendMessage message = SendMessage.builder()
                        .chatId(update.getMessage().getChatId().toString())
                        .text("❓ Sorry, I didn't understand that. Try /help to see available commands.")
                        .build();
                execute(message);
            }
        } catch (TelegramApiException e) {
            log.error("Error sending unknown update response: {}", e.getMessage());
        }
    }

    private void handleError(Update update, Exception e) {
        try {
            if (update.hasMessage()) {
                SendMessage message = SendMessage.builder()
                        .chatId(update.getMessage().getChatId().toString())
                        .text("❌ Oops! Something went wrong. Please try again later.")
                        .build();
                execute(message);
            }
        } catch (TelegramApiException ex) {
            log.error("Error sending error response: {}", ex.getMessage());
        }
    }
}
