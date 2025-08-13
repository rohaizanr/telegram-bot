package com.example.telegrambot.handler;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Interface for handling different types of Telegram updates.
 */
public interface UpdateHandler {
    
    /**
     * Checks if this handler can process the given update.
     */
    boolean canHandle(Update update);
    
    /**
     * Handles the update and returns a response.
     */
    BotApiMethod<?> handle(Update update);
    
    /**
     * Returns the priority of this handler (lower number = higher priority).
     */
    default int getPriority() {
        return 100;
    }
}
