package com.example.telegrambot.handler.command;

import com.example.telegrambot.handler.UpdateHandler;
import com.example.telegrambot.service.BotMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Handler for the /echo command.
 */
@Component
@RequiredArgsConstructor
public class EchoCommandHandler implements UpdateHandler {

    private final BotMessageService messageService;

    @Override
    public boolean canHandle(Update update) {
        return update.hasMessage() 
               && update.getMessage().hasText() 
               && update.getMessage().getText().startsWith("/echo");
    }

    @Override
    public BotApiMethod<?> handle(Update update) {
        var message = update.getMessage();
        var text = message.getText();
        
        // Save the message
        messageService.saveMessage(message);
        
        String echoText;
        if (text.length() > 5) { // "/echo" is 5 characters
            String messageToEcho = text.substring(5).trim();
            if (!messageToEcho.isEmpty()) {
                echoText = "🔊 **Echo:** " + messageToEcho;
            } else {
                echoText = "❌ Please provide a message to echo.\n\nExample: `/echo Hello World!`";
            }
        } else {
            echoText = "❌ Please provide a message to echo.\n\nExample: `/echo Hello World!`";
        }
        
        return SendMessage.builder()
                .chatId(message.getChatId().toString())
                .text(echoText)
                .parseMode("Markdown")
                .build();
    }

    @Override
    public int getPriority() {
        return 4;
    }
}
