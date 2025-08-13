package com.example.telegrambot.repository;

import com.example.telegrambot.model.BotMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for BotMessage entities.
 */
@Repository
public interface BotMessageRepository extends JpaRepository<BotMessage, Long> {

    List<BotMessage> findByChatIdOrderByCreatedAtDesc(Long chatId);

    Page<BotMessage> findByChatId(Long chatId, Pageable pageable);

    @Query("SELECT COUNT(m) FROM BotMessage m WHERE m.chatId = :chatId AND m.createdAt >= :date")
    long countMessagesByChatIdAndCreatedAtAfter(Long chatId, LocalDateTime date);

    @Query("SELECT m FROM BotMessage m WHERE m.messageType = :messageType ORDER BY m.createdAt DESC")
    List<BotMessage> findByMessageTypeOrderByCreatedAtDesc(BotMessage.MessageType messageType);

    @Query("SELECT COUNT(m) FROM BotMessage m WHERE m.createdAt >= :startDate AND m.createdAt <= :endDate")
    long countMessagesInDateRange(LocalDateTime startDate, LocalDateTime endDate);
}
