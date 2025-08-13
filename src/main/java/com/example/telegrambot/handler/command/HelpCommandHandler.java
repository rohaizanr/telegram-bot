package com.example.telegrambot.handler.command;

import com.example.telegrambot.handler.UpdateHandler;
import com.example.telegrambot.service.BotMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Handler for the /help command.
 */
@Component
@RequiredArgsConstructor
public class HelpCommandHandler implements UpdateHandler {

    private final BotMessageService messageService;

    @Override
    public boolean canHandle(Update update) {
        return update.hasMessage() 
               && update.getMessage().hasText() 
               && update.getMessage().getText().startsWith("/help");
    }

    @Override
    public BotApiMethod<?> handle(Update update) {
        var message = update.getMessage();
        
        // Save the message
        messageService.saveMessage(message);
        
        String helpText = "🤖 **Bot Commands Help**\n\n" +
                "**Basic Commands:**\n" +
                "• `/start` - Start the bot and get welcome message\n" +
                "• `/help` - Show this help message\n" +
                "• `/info` - Get your user information\n" +
                "• `/stats` - Show bot usage statistics\n\n" +
                "**Interactive Commands:**\n" +
                "• `/echo <message>` - Echo your message back\n" +
                "• `/time` - Get current server time\n\n" +
                "**Other Features:**\n" +
                "• Send any text message and I'll respond\n" +
                "• Send photos, documents, stickers and I'll acknowledge them\n\n" +
                "**Tips:**\n" +
                "• Commands are case-sensitive and start with `/`\n" +
                "• You can send me any type of message!\n\n" +
                "Need more help? Just ask! 😊";
        
        return SendMessage.builder()
                .chatId(message.getChatId().toString())
                .text(helpText)
                .parseMode("Markdown")
                .build();
    }

    @Override
    public int getPriority() {
        return 2;
    }
}
