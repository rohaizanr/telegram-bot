package com.example.telegrambot.bot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Initializer for registering the Telegram bot.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class BotInitializer {

    private final TelegramBotsApi telegramBotsApi;
    private final TelegramBot telegramBot;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        try {
            telegramBotsApi.registerBot(telegramBot);
            log.info("Telegram bot successfully registered!");
        } catch (TelegramApiException e) {
            log.error("Failed to register Telegram bot: {}", e.getMessage(), e);
        }
    }
}
