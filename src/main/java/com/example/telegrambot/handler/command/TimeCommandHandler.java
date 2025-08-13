package com.example.telegrambot.handler.command;

import com.example.telegrambot.handler.UpdateHandler;
import com.example.telegrambot.service.BotMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Handler for the /time command.
 */
@Component
@RequiredArgsConstructor
public class TimeCommandHandler implements UpdateHandler {

    private final BotMessageService messageService;

    @Override
    public boolean canHandle(Update update) {
        return update.hasMessage() 
               && update.getMessage().hasText() 
               && update.getMessage().getText().startsWith("/time");
    }

    @Override
    public BotApiMethod<?> handle(Update update) {
        var message = update.getMessage();
        
        // Save the message
        messageService.saveMessage(message);
        
        LocalDateTime now = LocalDateTime.now();
        String timeText = String.format(
            "🕐 **Current Server Time:**\n\n" +
            "**Date:** %s\n" +
            "**Time:** %s\n" +
            "**Timezone:** UTC\n\n" +
            "_Time is displayed in server timezone_",
            now.format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy")),
            now.format(DateTimeFormatter.ofPattern("HH:mm:ss"))
        );
        
        return SendMessage.builder()
                .chatId(message.getChatId().toString())
                .text(timeText)
                .parseMode("Markdown")
                .build();
    }

    @Override
    public int getPriority() {
        return 6;
    }
}
