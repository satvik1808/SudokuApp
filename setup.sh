#!/bin/bash
set -e

echo "=== SudokuApp Setup ==="

if ! command -v gradle &> /dev/null; then
    if command -v brew &> /dev/null; then
        echo "Installing Gradle via Homebrew..."
        brew install gradle
    else
        echo "ERROR: Gradle not found. Install via: brew install gradle"
        echo "Or open this project directly in Android Studio."
        exit 1
    fi
fi

echo "Generating Gradle wrapper..."
gradle wrapper --gradle-version 8.7

chmod +x gradlew

echo ""
echo "Setup complete!"
echo "Build debug APK:    ./gradlew assembleDebug"
echo "APK location:       app/build/outputs/apk/debug/app-debug.apk"
echo ""
echo "Or open the SudokuApp folder in Android Studio."
