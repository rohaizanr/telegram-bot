# Development Guide

This guide provides detailed information for developers working on the Telegram Bot project.

## 🏗️ Architecture Overview

### Layer Structure

```
┌─────────────────────────────────────┐
│           Controller Layer          │  ← REST API endpoints
├─────────────────────────────────────┤
│             Bot Layer               │  ← Telegram bot implementation
├─────────────────────────────────────┤
│            Handler Layer            │  ← Update handlers (commands, messages)
├─────────────────────────────────────┤
│            Service Layer            │  ← Business logic
├─────────────────────────────────────┤
│          Repository Layer           │  ← Data access
├─────────────────────────────────────┤
│            Model Layer              │  ← JPA entities
└─────────────────────────────────────┘
```

### Key Components

1. **TelegramBot**: Main bot implementation handling updates
2. **UpdateHandlers**: Strategy pattern for processing different update types
3. **Services**: Business logic and data operations
4. **Repositories**: JPA data access layer
5. **Controllers**: REST API for administration

## 🔧 Development Setup

### Prerequisites

- Java 17+
- Maven 3.6+
- IDE (IntelliJ IDEA recommended)
- PostgreSQL (for production testing)

### IDE Setup

#### IntelliJ IDEA

1. Import as Maven project
2. Set Project SDK to Java 17
3. Enable annotation processing for Lombok
4. Install Lombok plugin
5. Configure code style (optional)

#### VS Code

1. Install Java Extension Pack
2. Install Lombok Annotations Support
3. Configure Java runtime

### Running in Development Mode

```bash
# Set environment variables
export TELEGRAM_BOT_TOKEN="your-bot-token"
export TELEGRAM_BOT_USERNAME="your-bot-username"

# Run with development profile
./mvnw spring-boot:run

# Or run with specific profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Database Console

In development mode, H2 console is available at:
- URL: http://localhost:8080/h2-console
- JDBC URL: jdbc:h2:mem:testdb
- Username: sa
- Password: password

## 🧪 Testing

### Test Structure

```
src/test/java/
├── com/example/telegrambot/
│   ├── TelegramBotApplicationTests.java    # Integration tests
│   ├── service/
│   │   └── TelegramUserServiceTest.java    # Service layer tests
│   ├── handler/
│   │   └── CommandHandlerTest.java         # Handler tests
│   └── repository/
│       └── RepositoryTest.java             # Repository tests
```

### Running Tests

```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=TelegramUserServiceTest

# Run tests with coverage
./mvnw test jacoco:report

# Run integration tests only
./mvnw test -Dtest="*IntegrationTest"
```

### Writing Tests

#### Service Layer Test Example

```java
@ExtendWith(MockitoExtension.class)
class TelegramUserServiceTest {
    
    @Mock
    private TelegramUserRepository userRepository;
    
    @InjectMocks
    private TelegramUserService userService;
    
    @Test
    void shouldCreateNewUser() {
        // Given
        User telegramUser = createTestUser();
        when(userRepository.save(any())).thenReturn(createTestDbUser());
        
        // When
        TelegramUser result = userService.createOrUpdateUser(telegramUser);
        
        // Then
        assertThat(result).isNotNull();
        verify(userRepository).save(any(TelegramUser.class));
    }
}
```

## 🏗️ Adding New Features

### Adding a New Command Handler

1. Create handler class implementing `UpdateHandler`:

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
        // Implementation
    }
    
    @Override
    public int getPriority() {
        return 10; // Lower number = higher priority
    }
}
```

2. Spring will automatically detect and register the handler
3. Add tests for the new handler

### Adding a New Entity

1. Create JPA entity:

```java
@Entity
@Table(name = "weather_data")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeatherData {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "location")
    private String location;
    
    @Column(name = "temperature")
    private Double temperature;
    
    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
```

2. Create repository interface:

```java
@Repository
public interface WeatherDataRepository extends JpaRepository<WeatherData, Long> {
    List<WeatherData> findByLocation(String location);
}
```

3. Create service:

```java
@Service
@RequiredArgsConstructor
public class WeatherService {
    
    private final WeatherDataRepository repository;
    
    public WeatherData saveWeatherData(WeatherData data) {
        return repository.save(data);
    }
}
```

### Adding REST API Endpoints

```java
@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
public class WeatherController {
    
    private final WeatherService weatherService;
    
    @GetMapping("/{location}")
    public ResponseEntity<WeatherData> getWeather(@PathVariable String location) {
        return ResponseEntity.ok(weatherService.getByLocation(location));
    }
}
```

## 🔧 Configuration

### Application Properties

```properties
# Custom properties
app.feature.weather.enabled=true
app.feature.weather.api-key=${WEATHER_API_KEY:}

# Telegram specific
telegram.bot.token=${TELEGRAM_BOT_TOKEN}
telegram.bot.username=${TELEGRAM_BOT_USERNAME}
```

### Configuration Class

```java
@Data
@Component
@ConfigurationProperties(prefix = "app.feature.weather")
public class WeatherProperties {
    private boolean enabled = false;
    private String apiKey;
}
```

## 📝 Code Style Guidelines

### General Principles

1. Follow Spring Boot conventions
2. Use Lombok to reduce boilerplate
3. Write comprehensive JavaDoc for public methods
4. Use meaningful variable and method names
5. Keep methods small and focused

### Naming Conventions

- Classes: PascalCase (`TelegramUserService`)
- Methods: camelCase (`createOrUpdateUser`)
- Constants: UPPER_SNAKE_CASE (`MAX_RETRY_ATTEMPTS`)
- Packages: lowercase (`com.example.telegrambot.service`)

### Code Organization

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class ExampleService {
    
    // Constants first
    private static final int MAX_ATTEMPTS = 3;
    
    // Dependencies
    private final ExampleRepository repository;
    
    // Public methods
    public Example create(CreateRequest request) {
        log.info("Creating example: {}", request.getName());
        // Implementation
    }
    
    // Private helper methods
    private void validateRequest(CreateRequest request) {
        // Validation logic
    }
}
```

## 🚀 Deployment

### Environment Profiles

- `default`: Development with H2 database
- `prod`: Production with PostgreSQL
- `test`: Testing configuration

### Building for Production

```bash
# Build production JAR
./mvnw clean package -Pprod

# Build Docker image
docker build -t telegram-bot:latest .

# Run with Docker Compose
docker-compose up -d
```

### Environment Variables

Required for production:

```bash
TELEGRAM_BOT_TOKEN=your-production-token
TELEGRAM_BOT_USERNAME=your-bot-username
DATABASE_URL=jdbc:postgresql://localhost:5432/telegram_bot
DB_USERNAME=bot_user
DB_PASSWORD=secure_password
SPRING_PROFILES_ACTIVE=prod
```

## 🔍 Debugging

### Common Issues

1. **Bot not responding**: Check token and network connectivity
2. **Database errors**: Verify connection string and credentials
3. **Memory issues**: Increase JVM heap size
4. **Handler not working**: Check priority and canHandle() logic

### Debug Configuration

```properties
# Enable debug logging
logging.level.com.example.telegrambot=DEBUG
logging.level.org.telegram=DEBUG
logging.level.org.springframework.web=DEBUG

# SQL logging
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

### Useful Endpoints

- Health: `/api/admin/health`
- Stats: `/api/admin/stats`
- Users: `/api/admin/users`
- H2 Console: `/h2-console` (dev only)

## 📚 Resources

### Documentation

- [Spring Boot Reference](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Telegram Bot API](https://core.telegram.org/bots/api)
- [TelegramBots Java Library](https://github.com/rubenlagus/TelegramBots)

### Tools

- [BotFather](https://t.me/botfather) - Create and manage bots
- [API Tester](https://web.telegram.org/) - Test bot functionality
- [ngrok](https://ngrok.com/) - Local webhook testing

## 🤝 Contributing

### Pull Request Process

1. Create feature branch from `main`
2. Implement feature with tests
3. Update documentation
4. Submit pull request
5. Code review and merge

### Commit Messages

Use conventional commits:

```
feat: add weather command handler
fix: resolve user creation issue
docs: update API documentation
test: add integration tests for commands
```

### Code Review Checklist

- [ ] Code follows style guidelines
- [ ] Tests are included and pass
- [ ] Documentation is updated
- [ ] No security vulnerabilities
- [ ] Performance considerations addressed
