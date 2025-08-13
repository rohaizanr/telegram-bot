#!/bin/bash

# Telegram Bot Setup Script
# This script helps you set up the Telegram bot quickly

set -e

echo "🤖 Telegram Bot Setup Script"
echo "============================"

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "❌ Java is not installed. Please install Java 17 or higher."
    exit 1
fi

# Check Java version
JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 17 ]; then
    echo "❌ Java 17 or higher is required. Current version: $JAVA_VERSION"
    exit 1
fi

echo "✅ Java $JAVA_VERSION detected"

# Check if Maven is available
if [ ! -f "./mvnw" ]; then
    echo "❌ Maven wrapper not found. Please ensure you're in the project root directory."
    exit 1
fi

echo "✅ Maven wrapper found"

# Make Maven wrapper executable
chmod +x ./mvnw

# Check for bot configuration
if [ -z "$TELEGRAM_BOT_TOKEN" ] && [ ! -f ".env" ]; then
    echo ""
    echo "⚠️  Bot configuration required!"
    echo ""
    echo "You need to set up your Telegram bot token and username."
    echo "You can either:"
    echo "1. Set environment variables:"
    echo "   export TELEGRAM_BOT_TOKEN='your-bot-token'"
    echo "   export TELEGRAM_BOT_USERNAME='your-bot-username'"
    echo ""
    echo "2. Or copy .env.example to .env and fill in your values:"
    echo "   cp .env.example .env"
    echo "   # Then edit .env file with your bot credentials"
    echo ""
    echo "To get a bot token:"
    echo "1. Open Telegram and search for @BotFather"
    echo "2. Send /newbot command"
    echo "3. Follow the instructions"
    echo "4. Save the token and username"
    echo ""
    
    read -p "Do you want to continue without configuration? (y/N): " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        echo "Please set up your bot configuration first."
        exit 1
    fi
fi

echo ""
echo "🔄 Installing dependencies..."
./mvnw dependency:resolve

echo ""
echo "🧪 Running tests..."
./mvnw test

echo ""
echo "📦 Building application..."
./mvnw clean package -DskipTests

echo ""
echo "✅ Setup completed successfully!"
echo ""
echo "📋 Next steps:"
echo "1. Configure your bot token and username (if not done already)"
echo "2. Run the bot with: ./mvnw spring-boot:run"
echo "3. Or run the JAR: java -jar target/telegram-bot-1.0.0.jar"
echo "4. Test your bot in Telegram by sending /start"
echo ""
echo "📚 For more information, check the README.md file"
echo ""
echo "🚀 Happy coding!"
