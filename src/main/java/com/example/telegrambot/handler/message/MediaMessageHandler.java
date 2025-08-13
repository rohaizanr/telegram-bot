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
 * Handler for media messages (photos, documents, etc.).
 */
@Component
@RequiredArgsConstructor
public class MediaMessageHandler implements UpdateHandler {

    private final TelegramUserService userService;
    private final BotMessageService messageService;

    @Override
    public boolean canHandle(Update update) {
        if (!update.hasMessage()) {
            return false;
        }
        
        var message = update.getMessage();
        return message.hasPhoto() || message.hasDocument() || message.hasSticker() 
               || message.hasAudio() || message.hasVideo() || message.hasVoice()
               || message.hasContact() || message.hasLocation();
    }

    @Override
    public BotApiMethod<?> handle(Update update) {
        var message = update.getMessage();
        var user = message.getFrom();
        
        // Ensure user exists
        userService.createOrUpdateUser(user);
        
        // Save the message
        messageService.saveMessage(message);
        
        String responseText = generateMediaResponse(message, user.getFirstName());
        
        return SendMessage.builder()
                .chatId(message.getChatId().toString())
                .text(responseText)
                .build();
    }

    @Override
    public int getPriority() {
        return 40; // Higher priority than text messages
    }

    private String generateMediaResponse(org.telegram.telegrambots.meta.api.objects.Message message, String firstName) {
        if (message.hasPhoto()) {
            return String.format("📸 Nice photo, %s! I can see you sent me an image. I wish I could analyze it, but I'm still learning!", firstName);
        } else if (message.hasDocument()) {
            String fileName = message.getDocument().getFileName();
            return String.format("📄 Thanks for the document, %s! I received: %s", firstName, fileName != null ? fileName : "a file");
        } else if (message.hasSticker()) {
            return String.format("😄 Great sticker, %s! I love emojis and stickers!", firstName);
        } else if (message.hasAudio()) {
            return String.format("🎵 Thanks for the audio file, %s! I hope it's a good song!", firstName);
        } else if (message.hasVideo()) {
            return String.format("🎥 Cool video, %s! I wish I could watch it!", firstName);
        } else if (message.hasVoice()) {
            return String.format("🎤 Thanks for the voice message, %s! I heard you loud and clear!", firstName);
        } else if (message.hasContact()) {
            return String.format("👤 Thanks for sharing the contact, %s! I've received the contact information.", firstName);
        } else if (message.hasLocation()) {
            return String.format("📍 Thanks for sharing your location, %s! I can see where you are on the map.", firstName);
        }
        
        return String.format("🤖 Thanks for the media, %s! I received your message.", firstName);
    }
}
