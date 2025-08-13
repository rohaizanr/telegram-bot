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
 * Handler for the /stats command.
 */
@Component
@RequiredArgsConstructor
public class StatsCommandHandler implements UpdateHandler {

    private final TelegramUserService userService;
    private final BotMessageService messageService;

    @Override
    public boolean canHandle(Update update) {
        return update.hasMessage() 
               && update.getMessage().hasText() 
               && update.getMessage().getText().startsWith("/stats");
    }

    @Override
    public BotApiMethod<?> handle(Update update) {
        var message = update.getMessage();
        
        // Save the message
        messageService.saveMessage(message);
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = now.toLocalDate().atTime(23, 59, 59);
        
        long activeUsers = userService.getActiveUserCount();
        long todayMessages = messageService.countMessagesInDateRange(startOfDay, endOfDay);
        long newUsersToday = userService.getUsersCreatedAfter(startOfDay).size();
        
        String statsText = String.format(
            "📊 **Bot Statistics**\n\n" +
            "**User Statistics:**\n" +
            "• Total Active Users: %d\n" +
            "• New Users Today: %d\n\n" +
            "**Message Statistics:**\n" +
            "• Messages Today: %d\n\n" +
            "**Server Information:**\n" +
            "• Current Time: %s\n" +
            "• Uptime: Since bot restart\n" +
            "• Status: ✅ Online\n\n" +
            "_Statistics updated in real-time_",
            activeUsers,
            newUsersToday,
            todayMessages,
            now.format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss"))
        );
        
        return SendMessage.builder()
                .chatId(message.getChatId().toString())
                .text(statsText)
                .parseMode("Markdown")
                .build();
    }

    @Override
    public int getPriority() {
        return 5;
    }
}
