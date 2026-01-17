# Calyx - Call Log Analyzer

A beautiful Android app that transforms your call history into interactive leaderboards showing your most frequent contacts.

## Features

- 📊 **Dual Rankings** - View contacts by "Most Called" or "Most Talked"
- 🏆 **Beautiful Leaderboard** - Podium display for top 3 with animated entries
- ⏱️ **Time Filters** - Switch between Weekly and All Time views
- 📱 **Contact Integration** - Shows names and profile photos
- 🔒 **100% Privacy** - All data stays on your device

## Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose with Material Design 3
- **Architecture:** MVVM with StateFlow
- **Min SDK:** Android 8.0 (API 26)
- **Target SDK:** Android 14 (API 34)

## Building the Project

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17
- Android SDK with API 34

### Steps

1. Clone the repository
2. Open the project in Android Studio
3. Sync Gradle files
4. Run on an emulator or device

```bash
# Or build via command line
./gradlew assembleDebug
```

## Required Permissions

- `READ_CALL_LOG` - Access call history for analysis
- `READ_CONTACTS` - Display contact names and photos

## Project Structure

```
com.calyx.app/
├── data/
│   ├── models/         # Data classes (CallerStats, CallEntry, etc.)
│   └── repository/     # CallLogRepository for data access
├── ui/
│   ├── theme/          # Material 3 theming
│   ├── screens/        # Splash, Permissions, Leaderboard screens
│   └── components/     # Reusable UI components
├── utils/              # Utility functions
└── MainActivity.kt     # App entry point
```

## Screenshots

_Coming soon_

## License

MIT License

## Author

Built by @daudibrahimhasan  
A product of Nexasity AI
