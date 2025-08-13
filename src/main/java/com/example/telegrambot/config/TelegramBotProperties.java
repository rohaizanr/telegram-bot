package com.example.telegrambot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;

/**
 * Configuration properties for the Telegram bot.
 */
@Data
@Component
@Validated
@ConfigurationProperties(prefix = "telegram.bot")
public class TelegramBotProperties {

    @NotBlank(message = "Bot token cannot be blank")
    private String token;

    @NotBlank(message = "Bot username cannot be blank")
    private String username;
}
