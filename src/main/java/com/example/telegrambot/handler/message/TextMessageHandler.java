package com.example.telegrambot.handler.message;

import com.example.telegrambot.handler.UpdateHandler;
import com.example.telegrambot.service.BotMessageService;
import com.example.telegrambot.service.TelegramUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Handler for regular text messages.
 */
@Component
@RequiredArgsConstructor
public class TextMessageHandler implements UpdateHandler {

    private final TelegramUserService userService;
    private final BotMessageService messageService;

    @Override
    public boolean canHandle(Update update) {
        return update.hasMessage() 
               && update.getMessage().hasText() 
               && !update.getMessage().getText().startsWith("/");
    }

    @Override
    public BotApiMethod<?> handle(Update update) {
        var message = update.getMessage();
        var user = message.getFrom();
        
        // Ensure user exists
        userService.createOrUpdateUser(user);
        
        // Save the message
        messageService.saveMessage(message);
        
        String responseText = generateResponse(message.getText(), user.getFirstName());
        
        return SendMessage.builder()
                .chatId(message.getChatId().toString())
                .text(responseText)
                .build();
    }

    @Override
    public int getPriority() {
        return 50; // Lower priority than commands
    }

    private String generateResponse(String messageText, String firstName) {
        String lowerText = messageText.toLowerCase();
        
        if (lowerText.contains("hello") || lowerText.contains("hi") || lowerText.contains("hey")) {
            return String.format("Hello %s! 👋 How can I help you today?", firstName);
        } else if (lowerText.contains("how are you") || lowerText.contains("how do you do")) {
            return "I'm doing great! 😊 Thanks for asking. How are you?";
        } else if (lowerText.contains("thank") || lowerText.contains("thanks")) {
            return "You're welcome! 😊 I'm happy to help!";
        } else if (lowerText.contains("bye") || lowerText.contains("goodbye") || lowerText.contains("see you")) {
            return "Goodbye! 👋 Feel free to message me anytime!";
        } else if (lowerText.contains("help")) {
            return "I'm here to help! 🤖 Try using /help to see all available commands.";
        } else if (lowerText.contains("weather")) {
            return "🌤️ I wish I could tell you the weather, but I don't have access to weather data yet. Try asking me something else!";
        } else if (lowerText.contains("joke")) {
            return "😄 Why don't scientists trust atoms? Because they make up everything!";
        } else if (lowerText.contains("time")) {
            return "⏰ For the current time, use the /time command!";
        } else {
            return String.format(
                "Thanks for your message, %s! 💬\n\n" +
                "I received: \"%s\"\n\n" +
                "I'm a simple bot, but I'm learning! Try using /help to see what I can do.",
                firstName, messageText
            );
        }
    }
}
