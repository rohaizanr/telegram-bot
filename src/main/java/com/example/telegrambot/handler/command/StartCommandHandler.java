package com.example.telegrambot.handler.command;

import com.example.telegrambot.handler.UpdateHandler;
import com.example.telegrambot.service.BotMessageService;
import com.example.telegrambot.service.TelegramUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Handler for the /start command.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class StartCommandHandler implements UpdateHandler {

    private final TelegramUserService userService;
    private final BotMessageService messageService;

    @Override
    public boolean canHandle(Update update) {
        return update.hasMessage() 
               && update.getMessage().hasText() 
               && update.getMessage().getText().startsWith("/start");
    }

    @Override
    public BotApiMethod<?> handle(Update update) {
        var message = update.getMessage();
        var user = message.getFrom();
        
        // Save or update user
        userService.createOrUpdateUser(user);
        
        // Save the message
        messageService.saveMessage(message);
        
        log.info("User {} started the bot", user.getFirstName());
        
        String welcomeText = String.format(
            "👋 Hello %s! Welcome to our Telegram Bot!\n\n" +
            "I'm here to help you. Here are some commands you can try:\n" +
            "• /help - Show available commands\n" +
            "• /info - Get your user information\n" +
            "• /stats - Show bot statistics\n" +
            "• /echo <message> - Echo your message\n\n" +
            "Just send me any message and I'll respond!",
            user.getFirstName()
        );
        
        return SendMessage.builder()
                .chatId(message.getChatId().toString())
                .text(welcomeText)
                .build();
    }

    @Override
    public int getPriority() {
        return 1;
    }
}
