package com.example.telegrambot.service;

import com.example.telegrambot.model.TelegramUser;
import com.example.telegrambot.repository.TelegramUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing Telegram users.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TelegramUserService {

    private final TelegramUserRepository userRepository;

    /**
     * Creates or updates a user from a Telegram User object.
     */
    @Transactional
    @CacheEvict(value = "users", key = "#user.id")
    public TelegramUser createOrUpdateUser(User user) {
        Optional<TelegramUser> existingUser = userRepository.findByChatId(user.getId());
        
        TelegramUser telegramUser;
        if (existingUser.isPresent()) {
            telegramUser = existingUser.get();
            updateUserInfo(telegramUser, user);
        } else {
            telegramUser = createNewUser(user);
        }
        
        TelegramUser savedUser = userRepository.save(telegramUser);
        log.info("User {} ({}) saved/updated", savedUser.getFirstName(), savedUser.getChatId());
        return savedUser;
    }

    /**
     * Finds a user by chat ID.
     */
    @Cacheable(value = "users", key = "#chatId")
    public Optional<TelegramUser> findByChatId(Long chatId) {
        return userRepository.findByChatId(chatId);
    }

    /**
     * Gets all active users.
     */
    public List<TelegramUser> getActiveUsers() {
        return userRepository.findByIsActiveTrue();
    }

    /**
     * Gets the count of active users.
     */
    public long getActiveUserCount() {
        return userRepository.countActiveUsers();
    }

    /**
     * Deactivates a user.
     */
    @Transactional
    @CacheEvict(value = "users", key = "#chatId")
    public void deactivateUser(Long chatId) {
        Optional<TelegramUser> user = userRepository.findByChatId(chatId);
        if (user.isPresent()) {
            user.get().setIsActive(false);
            userRepository.save(user.get());
            log.info("User {} deactivated", chatId);
        }
    }

    /**
     * Checks if a user exists.
     */
    public boolean userExists(Long chatId) {
        return userRepository.existsByChatId(chatId);
    }

    /**
     * Gets users created after a specific date.
     */
    public List<TelegramUser> getUsersCreatedAfter(LocalDateTime date) {
        return userRepository.findUsersCreatedAfter(date);
    }

    private TelegramUser createNewUser(User user) {
        return TelegramUser.builder()
                .chatId(user.getId())
                .username(user.getUserName())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .languageCode(user.getLanguageCode())
                .isBot(user.getIsBot())
                .isActive(true)
                .build();
    }

    private void updateUserInfo(TelegramUser telegramUser, User user) {
        telegramUser.setUsername(user.getUserName());
        telegramUser.setFirstName(user.getFirstName());
        telegramUser.setLastName(user.getLastName());
        telegramUser.setLanguageCode(user.getLanguageCode());
        telegramUser.setIsActive(true); // Reactivate if they were inactive
    }
}
