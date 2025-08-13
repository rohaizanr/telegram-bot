package com.example.telegrambot.service;

import com.example.telegrambot.model.TelegramUser;
import com.example.telegrambot.repository.TelegramUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TelegramUserServiceTest {

    @Mock
    private TelegramUserRepository userRepository;

    @InjectMocks
    private TelegramUserService telegramUserService;

    private User telegramUser;
    private TelegramUser dbUser;

    @BeforeEach
    void setUp() {
        telegramUser = new User();
        telegramUser.setId(123456789L);
        telegramUser.setFirstName("John");
        telegramUser.setLastName("Doe");
        telegramUser.setUserName("johndoe");
        telegramUser.setLanguageCode("en");
        telegramUser.setIsBot(false);

        dbUser = TelegramUser.builder()
                .chatId(123456789L)
                .firstName("John")
                .lastName("Doe")
                .username("johndoe")
                .languageCode("en")
                .isBot(false)
                .isActive(true)
                .build();
    }

    @Test
    void testCreateOrUpdateUser_NewUser() {
        // Given
        when(userRepository.findByChatId(123456789L)).thenReturn(Optional.empty());
        when(userRepository.save(any(TelegramUser.class))).thenReturn(dbUser);

        // When
        TelegramUser result = telegramUserService.createOrUpdateUser(telegramUser);

        // Then
        assertNotNull(result);
        assertEquals(123456789L, result.getChatId());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("johndoe", result.getUsername());
        assertTrue(result.getIsActive());
        
        verify(userRepository).findByChatId(123456789L);
        verify(userRepository).save(any(TelegramUser.class));
    }

    @Test
    void testCreateOrUpdateUser_ExistingUser() {
        // Given
        when(userRepository.findByChatId(123456789L)).thenReturn(Optional.of(dbUser));
        when(userRepository.save(any(TelegramUser.class))).thenReturn(dbUser);

        // When
        TelegramUser result = telegramUserService.createOrUpdateUser(telegramUser);

        // Then
        assertNotNull(result);
        assertEquals(123456789L, result.getChatId());
        assertTrue(result.getIsActive());
        
        verify(userRepository).findByChatId(123456789L);
        verify(userRepository).save(dbUser);
    }

    @Test
    void testFindByChatId() {
        // Given
        when(userRepository.findByChatId(123456789L)).thenReturn(Optional.of(dbUser));

        // When
        Optional<TelegramUser> result = telegramUserService.findByChatId(123456789L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(123456789L, result.get().getChatId());
        
        verify(userRepository).findByChatId(123456789L);
    }

    @Test
    void testUserExists() {
        // Given
        when(userRepository.existsByChatId(123456789L)).thenReturn(true);

        // When
        boolean exists = telegramUserService.userExists(123456789L);

        // Then
        assertTrue(exists);
        verify(userRepository).existsByChatId(123456789L);
    }
}
