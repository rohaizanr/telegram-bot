package com.example.telegrambot.handler.command;

import com.example.telegrambot.handler.UpdateHandler;
import com.example.telegrambot.service.BotMessageService;
import com.example.telegrambot.service.TelegramUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Handler for the /info command.
 */
@Component
@RequiredArgsConstructor
public class InfoCommandHandler implements UpdateHandler {

    private final TelegramUserService userService;
    private final BotMessageService messageService;

    @Override
    public boolean canHandle(Update update) {
        return update.hasMessage() 
               && update.getMessage().hasText() 
               && update.getMessage().getText().startsWith("/info");
    }

    @Override
    public BotApiMethod<?> handle(Update update) {
        var message = update.getMessage();
        var user = message.getFrom();
        
        // Save the message
        messageService.saveMessage(message);
        
        // Get user info
        var telegramUser = userService.findByChatId(user.getId());
        
        String userInfo;
        if (telegramUser.isPresent()) {
            var dbUser = telegramUser.get();
            userInfo = String.format(
                "👤 **Your Information:**\n\n" +
                "**Chat ID:** `%d`\n" +
                "**Name:** %s %s\n" +
                "**Username:** @%s\n" +
                "**Language:** %s\n" +
                "**Member Since:** %s\n" +
                "**Status:** %s\n\n" +
                "**Statistics:**\n" +
                "• Messages today: %d\n" +
                "• Total active users: %d",
                dbUser.getChatId(),
                dbUser.getFirstName() != null ? dbUser.getFirstName() : "N/A",
                dbUser.getLastName() != null ? dbUser.getLastName() : "",
                dbUser.getUsername() != null ? dbUser.getUsername() : "N/A",
                dbUser.getLanguageCode() != null ? dbUser.getLanguageCode() : "N/A",
                dbUser.getCreatedAt().format(DateTimeFormatter.ofPattern("dd MMM yyyy")),
                dbUser.getIsActive() ? "Active ✅" : "Inactive ❌",
                messageService.countUserMessagesAfter(user.getId(), LocalDateTime.now().toLocalDate().atStartOfDay()),
                userService.getActiveUserCount()
            );
        } else {
            userInfo = "❌ User information not found. Please use /start first.";
        }
        
        return SendMessage.builder()
                .chatId(message.getChatId().toString())
                .text(userInfo)
                .parseMode("Markdown")
                .build();
    }

    @Override
    public int getPriority() {
        return 3;
    }
}
