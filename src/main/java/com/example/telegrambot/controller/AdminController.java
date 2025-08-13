package com.example.telegrambot.controller;

import com.example.telegrambot.service.TelegramUserService;
import com.example.telegrambot.service.BotMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * REST API controller for bot administration and statistics.
 */
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final TelegramUserService userService;
    private final BotMessageService messageService;

    /**
     * Get bot statistics.
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = LocalDateTime.now().toLocalDate().atTime(23, 59, 59);
        
        stats.put("totalActiveUsers", userService.getActiveUserCount());
        stats.put("newUsersToday", userService.getUsersCreatedAfter(startOfDay).size());
        stats.put("messagesToday", messageService.countMessagesInDateRange(startOfDay, endOfDay));
        stats.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.ok(stats);
    }

    /**
     * Get user information by chat ID.
     */
    @GetMapping("/users/{chatId}")
    public ResponseEntity<?> getUserInfo(@PathVariable Long chatId) {
        return userService.findByChatId(chatId)
                .map(user -> ResponseEntity.ok(user))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get all active users.
     */
    @GetMapping("/users")
    public ResponseEntity<?> getActiveUsers() {
        return ResponseEntity.ok(userService.getActiveUsers());
    }

    /**
     * Health check endpoint.
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(health);
    }
}
