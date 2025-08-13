#!/bin/bash

# Quick Start Script for Telegram Bot
# This script demonstrates how to quickly start the bot

echo "🚀 Quick Start - Telegram Bot"
echo "============================="

# Check if configuration exists
if [ -z "$TELEGRAM_BOT_TOKEN" ]; then
    echo ""
    echo "⚠️  Missing Configuration!"
    echo ""
    echo "To run this bot, you need to set up your Telegram bot credentials:"
    echo ""
    echo "1. Get your bot token from @BotFather on Telegram:"
    echo "   - Open Telegram and search for @BotFather"
    echo "   - Send /newbot command"
    echo "   - Follow the instructions to create your bot"
    echo "   - Copy the bot token and username"
    echo ""
    echo "2. Set environment variables:"
    echo "   export TELEGRAM_BOT_TOKEN='your-bot-token-here'"
    echo "   export TELEGRAM_BOT_USERNAME='your-bot-username-here'"
    echo ""
    echo "3. Run this script again:"
    echo "   ./quick-start.sh"
    echo ""
    echo "Alternative: Copy .env.example to .env and edit with your credentials"
    echo ""
    exit 1
fi

echo "✅ Bot token configured"
echo "✅ Bot username: $TELEGRAM_BOT_USERNAME"

echo ""
echo "🔄 Starting the Telegram Bot..."
echo ""

# Start the bot
./mvnw spring-boot:run
