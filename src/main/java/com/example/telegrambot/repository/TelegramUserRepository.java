package com.example.telegrambot.repository;

import com.example.telegrambot.model.TelegramUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for TelegramUser entities.
 */
@Repository
public interface TelegramUserRepository extends JpaRepository<TelegramUser, Long> {

    Optional<TelegramUser> findByChatId(Long chatId);

    List<TelegramUser> findByIsActiveTrue();

    @Query("SELECT COUNT(u) FROM TelegramUser u WHERE u.isActive = true")
    long countActiveUsers();

    @Query("SELECT u FROM TelegramUser u WHERE u.createdAt >= :date")
    List<TelegramUser> findUsersCreatedAfter(LocalDateTime date);

    boolean existsByChatId(Long chatId);
}
