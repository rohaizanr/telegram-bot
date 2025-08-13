# Telegram Bot with Spring Boot

A production-ready Telegram bot built with Spring Boot following best practices. This bot includes user management, message handling, comprehensive logging, database integration, and a REST API for administration.

## 🚀 Features

- **Modern Spring Boot Architecture**: Built with Spring Boot 3.x and Java 17
- **Comprehensive Message Handling**: Support for text, media, commands, and callback queries
- **Database Integration**: JPA with H2 (development) and PostgreSQL (production) support
- **User Management**: Automatic user registration and management
- **Message Logging**: Complete message history and analytics
- **REST API**: Admin endpoints for statistics and user management
- **Caching**: Built-in caching for improved performance
- **Async Processing**: Non-blocking message processing
- **Error Handling**: Comprehensive error handling and logging
- **Testing**: Unit tests with Mockito and integration tests
- **Production Ready**: Multiple environment configurations

## 📋 Prerequisites

- **Java 17** or higher
- **Maven 3.6+**
- **Telegram Bot Token** (obtained from [@BotFather](https://t.me/botfather))
- **PostgreSQL** (for production deployment)

## 🛠️ Getting Started

### 1. Create a Telegram Bot

1. Open Telegram and search for [@BotFather](https://t.me/botfather)
2. Start a chat with BotFather
3. Send `/newbot` command
4. Follow the instructions to create your bot
5. Save the **Bot Token** and **Bot Username**

### 2. Clone and Setup

```bash
# Clone the repository
git clone <your-repository-url>
cd telegram-bot

# Make the Maven wrapper executable (Unix/macOS)
chmod +x mvnw
```

### 3. Configuration

#### Development Configuration

Create or update `src/main/resources/application.properties`:

```properties
# Telegram Bot Configuration
telegram.bot.token=YOUR_BOT_TOKEN_HERE
telegram.bot.username=YOUR_BOT_USERNAME_HERE

# Other configurations are already set for development
```

#### Environment Variables (Recommended)

Set the following environment variables:

```bash
export TELEGRAM_BOT_TOKEN="your-bot-token-here"
export TELEGRAM_BOT_USERNAME="your-bot-username-here"
```

### 4. Running the Application

#### Development Mode

```bash
# Using Maven wrapper (recommended)
./mvnw spring-boot:run

# Or using Maven directly
mvn spring-boot:run
```

#### Using JAR file

```bash
# Build the application
./mvnw clean package

# Run the JAR file
java -jar target/telegram-bot-1.0.0.jar
```

### 5. Verify the Bot is Working

1. Open Telegram and search for your bot username
2. Start a chat with your bot
3. Send `/start` command
4. You should receive a welcome message

## 🏗️ Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/example/telegrambot/
│   │       ├── TelegramBotApplication.java          # Main application class
│   │       ├── bot/
│   │       │   ├── TelegramBot.java                 # Main bot implementation
│   │       │   └── BotInitializer.java              # Bot registration
│   │       ├── config/
│   │       │   ├── TelegramBotConfig.java           # Bot configuration
│   │       │   └── TelegramBotProperties.java       # Configuration properties
│   │       ├── controller/
│   │       │   ├── AdminController.java             # Admin REST API
│   │       │   └── CustomErrorController.java       # Error handling
│   │       ├── handler/
│   │       │   ├── UpdateHandler.java               # Handler interface
│   │       │   ├── command/                         # Command handlers
│   │       │   │   ├── StartCommandHandler.java
│   │       │   │   ├── HelpCommandHandler.java
│   │       │   │   ├── InfoCommandHandler.java
│   │       │   │   ├── EchoCommandHandler.java
│   │       │   │   ├── StatsCommandHandler.java
│   │       │   │   └── TimeCommandHandler.java
│   │       │   └── message/                         # Message handlers
│   │       │       ├── TextMessageHandler.java
│   │       │       └── MediaMessageHandler.java
│   │       ├── model/
│   │       │   ├── TelegramUser.java                # User entity
│   │       │   └── BotMessage.java                  # Message entity
│   │       ├── repository/
│   │       │   ├── TelegramUserRepository.java      # User repository
│   │       │   └── BotMessageRepository.java        # Message repository
│   │       └── service/
│   │           ├── TelegramUserService.java         # User service
│   │           └── BotMessageService.java           # Message service
│   └── resources/
│       ├── application.properties                   # Development config
│       └── application-prod.properties              # Production config
├── test/
│   ├── java/
│   │   └── com/example/telegrambot/
│   │       ├── TelegramBotApplicationTests.java
│   │       └── service/
│   │           └── TelegramUserServiceTest.java
│   └── resources/
│       └── application-test.properties              # Test configuration
└── pom.xml                                          # Maven configuration
```

## 🤖 Bot Commands

| Command | Description |
|---------|-------------|
| `/start` | Start the bot and get welcome message |
| `/help` | Show available commands and help |
| `/info` | Get your user information and statistics |
| `/stats` | Show bot usage statistics |
| `/echo <message>` | Echo your message back |
| `/time` | Get current server time |

## 🔧 Configuration Options

### Application Properties

| Property | Description | Default |
|----------|-------------|---------|
| `telegram.bot.token` | Bot token from BotFather | Required |
| `telegram.bot.username` | Bot username | Required |
| `server.port` | Server port | 8080 |
| `spring.datasource.url` | Database URL | H2 in-memory |
| `spring.jpa.hibernate.ddl-auto` | Hibernate DDL mode | create-drop |

### Environment Variables

- `TELEGRAM_BOT_TOKEN`: Your bot token
- `TELEGRAM_BOT_USERNAME`: Your bot username
- `DATABASE_URL`: PostgreSQL connection URL (production)
- `DB_USERNAME`: Database username (production)
- `DB_PASSWORD`: Database password (production)

## 📊 REST API Endpoints

### Admin Endpoints

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/admin/stats` | GET | Get bot statistics |
| `/api/admin/users` | GET | Get all active users |
| `/api/admin/users/{chatId}` | GET | Get user by chat ID |
| `/api/admin/health` | GET | Health check |

### Health Check

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/actuator/health` | GET | Spring Boot actuator health |

## 🧪 Testing

### Running Tests

```bash
# Run all tests
./mvnw test

# Run tests with coverage
./mvnw test jacoco:report

# Run specific test class
./mvnw test -Dtest=TelegramUserServiceTest
```

### Test Structure

- **Unit Tests**: Service layer testing with Mockito
- **Integration Tests**: Full application context testing
- **Repository Tests**: Database layer testing

## 🚀 Production Deployment

### 1. Database Setup (PostgreSQL)

```sql
-- Create database
CREATE DATABASE telegram_bot;

-- Create user (optional)
CREATE USER bot_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE telegram_bot TO bot_user;
```

### 2. Production Configuration

Set environment variables:

```bash
export SPRING_PROFILES_ACTIVE=prod
export TELEGRAM_BOT_TOKEN="your-production-bot-token"
export TELEGRAM_BOT_USERNAME="your-production-bot-username"
export DATABASE_URL="jdbc:postgresql://localhost:5432/telegram_bot"
export DB_USERNAME="bot_user"
export DB_PASSWORD="your_password"
```

### 3. Build and Deploy

```bash
# Build production JAR
./mvnw clean package -Pprod

# Run in production
java -jar target/telegram-bot-1.0.0.jar --spring.profiles.active=prod
```

### 4. Docker Deployment (Optional)

Create `Dockerfile`:

```dockerfile
FROM openjdk:17-jdk-slim

COPY target/telegram-bot-1.0.0.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app.jar"]
```

Build and run:

```bash
docker build -t telegram-bot .
docker run -p 8080:8080 \
  -e TELEGRAM_BOT_TOKEN="your-token" \
  -e TELEGRAM_BOT_USERNAME="your-username" \
  telegram-bot
```

## 🔍 Monitoring and Logging

### Log Levels

- **Development**: DEBUG level for detailed debugging
- **Production**: INFO level for important events only

### Health Monitoring

- **Actuator**: `/api/actuator/health` for health checks
- **Custom Health**: `/api/admin/health` for bot-specific health

### Database Monitoring

- **H2 Console**: Available at `/h2-console` (development only)
- **Statistics**: Available through admin API endpoints

## 🛡️ Security Best Practices

1. **Environment Variables**: Store sensitive data in environment variables
2. **Token Security**: Never commit bot tokens to version control
3. **Database Security**: Use strong passwords and connection encryption
4. **Input Validation**: All user inputs are validated and sanitized
5. **Error Handling**: Errors are logged but sensitive information is not exposed

## 🔧 Customization

### Adding New Commands

1. Create a new handler class implementing `UpdateHandler`
2. Add `@Component` annotation
3. Implement `canHandle()` and `handle()` methods
4. Set appropriate priority with `getPriority()`

Example:

```java
@Component
@RequiredArgsConstructor
public class WeatherCommandHandler implements UpdateHandler {
    
    @Override
    public boolean canHandle(Update update) {
        return update.hasMessage() 
               && update.getMessage().hasText() 
               && update.getMessage().getText().startsWith("/weather");
    }
    
    @Override
    public BotApiMethod<?> handle(Update update) {
        // Implementation here
    }
    
    @Override
    public int getPriority() {
        return 10;
    }
}
```

### Adding Database Entities

1. Create entity class with JPA annotations
2. Create repository interface extending `JpaRepository`
3. Create service class for business logic
4. Add necessary configurations

## 📝 Troubleshooting

### Common Issues

1. **Bot not responding**: Check bot token and username
2. **Database errors**: Verify database connection and credentials
3. **Port already in use**: Change server port in configuration
4. **Memory issues**: Increase JVM heap size

### Debug Mode

Enable debug logging:

```properties
logging.level.com.example.telegrambot=DEBUG
logging.level.org.telegram=DEBUG
```

### Checking Bot Status

```bash
# Check if bot is registered
curl -X GET "https://api.telegram.org/bot<YOUR_BOT_TOKEN>/getMe"

# Check webhook status
curl -X GET "https://api.telegram.org/bot<YOUR_BOT_TOKEN>/getWebhookInfo"
```

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Ensure all tests pass
6. Submit a pull request

## 📞 Support

For support and questions:

1. Check the [Issues](https://github.com/your-repo/issues) page
2. Create a new issue with detailed information
3. Include logs and configuration details

## 🔄 Updates and Maintenance

- Regularly update dependencies
- Monitor security advisories
- Keep Telegram Bot API version current
- Review and update documentation

---

**Happy Coding! 🎉**
