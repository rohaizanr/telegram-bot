package com.example.telegrambot.service;

import com.example.telegrambot.model.BotMessage;
import com.example.telegrambot.repository.BotMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for managing bot messages.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BotMessageService {

    private final BotMessageRepository messageRepository;

    /**
     * Saves a message from Telegram.
     */
    @Transactional
    public BotMessage saveMessage(Message message) {
        BotMessage botMessage = BotMessage.builder()
                .chatId(message.getChatId())
                .messageId(message.getMessageId())
                .messageText(message.getText())
                .messageType(determineMessageType(message))
                .userFirstName(message.getFrom().getFirstName())
                .userUsername(message.getFrom().getUserName())
                .build();

        BotMessage savedMessage = messageRepository.save(botMessage);
        log.debug("Message saved: {} from user {}", savedMessage.getId(), savedMessage.getUserFirstName());
        return savedMessage;
    }

    /**
     * Gets messages for a specific chat.
     */
    public List<BotMessage> getMessagesByChatId(Long chatId) {
        return messageRepository.findByChatIdOrderByCreatedAtDesc(chatId);
    }

    /**
     * Gets paginated messages for a specific chat.
     */
    public Page<BotMessage> getMessagesByChatId(Long chatId, Pageable pageable) {
        return messageRepository.findByChatId(chatId, pageable);
    }

    /**
     * Counts messages for a user in a specific time period.
     */
    public long countUserMessagesAfter(Long chatId, LocalDateTime date) {
        return messageRepository.countMessagesByChatIdAndCreatedAtAfter(chatId, date);
    }

    /**
     * Gets messages by type.
     */
    public List<BotMessage> getMessagesByType(BotMessage.MessageType messageType) {
        return messageRepository.findByMessageTypeOrderByCreatedAtDesc(messageType);
    }

    /**
     * Counts messages in a date range.
     */
    public long countMessagesInDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return messageRepository.countMessagesInDateRange(startDate, endDate);
    }

    private BotMessage.MessageType determineMessageType(Message message) {
        if (message.hasText()) {
            if (message.getText().startsWith("/")) {
                return BotMessage.MessageType.COMMAND;
            }
            return BotMessage.MessageType.TEXT;
        } else if (message.hasPhoto()) {
            return BotMessage.MessageType.PHOTO;
        } else if (message.hasDocument()) {
            return BotMessage.MessageType.DOCUMENT;
        } else if (message.hasSticker()) {
            return BotMessage.MessageType.STICKER;
        } else if (message.hasAudio()) {
            return BotMessage.MessageType.AUDIO;
        } else if (message.hasVideo()) {
            return BotMessage.MessageType.VIDEO;
        } else if (message.hasVoice()) {
            return BotMessage.MessageType.VOICE;
        } else if (message.hasContact()) {
            return BotMessage.MessageType.CONTACT;
        } else if (message.hasLocation()) {
            return BotMessage.MessageType.LOCATION;
        }
        return BotMessage.MessageType.TEXT;
    }
}
