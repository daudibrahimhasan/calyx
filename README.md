# calyz - Smart Call Insights & Leaderboards

**calyz** is a premium Android application designed to transform your raw call history into meaningful, actionable insights. Featuring a state-of-the-art Jetpack Compose UI, it identifies your most frequent contacts, analyzes communication trends, and provides a beautiful interface for visualizing your calling habits.

---

## ✨ Key Features

- 🏆 **Dynamic Leaderboards** – Instantly rank your connections using "Most Called" (frequency) or "Most Talked" (duration) metrics.
- 📈 **Advanced Analytics** – Track your communication health with activity heatmaps and week-over-week trend comparisons.
- ⚡ **Optimized Performance** – Features an incremental sync engine with a local Room database for near-instant load times.
- 📅 **Flexible Time-Control** – Seamlessly toggle between **Weekly** snapshots and **All-Time** historical data.
- 🎨 **Premium Aesthetics** – A high-end design system featuring glassmorphism, fluid animations, and a curated color palette.
- 🔒 **Privacy First** – Your data is yours. All call logs are processed and stored strictly on-device. No external API calls, no tracking.

---

## 🛠️ Technology Stack

- **Framework:** Jetpack Compose (Material Design 3)
- **Architecture:** MVVM (Model-View-ViewModel) with Kotlin Coroutines & StateFlow
- **Database:** Room Persistence Library (Offline-first architecture)
- **Dependency Management:** Gradle Kotlin DSL (KTS)
- **Minimum SDK:** Android 8.0 (API 26)
- **Target SDK:** Android 14 (API 34)
- **Tooling:** Android Gradle Plugin 8.2.2+ (Java 21 Compatible)

---

## 🏗️ Project Architecture

```text
com.calyx.app/
├── data/
│   ├── local/          # Room DB, Entity definitions, and DAOs
│   ├── models/         # Core data models (CallerStats, TimeRange, etc.)
│   └── repository/     # Optimized CallLogRepository with sync logic
├── ui/
│   ├── theme/          # Design system tokens and typography
│   ├── screens/        # Feature screens (Leaderboard, Stats, Profile)
│   ├── components/     # Reusable UI elements (RankedList, Podium)
│   └── share/          # Utilities for generating shareable contact posters
├── utils/              # Specialized date and phone number processors
└── MainActivity.kt     # App entry point and navigation host
```

---

## 🚀 Getting Started

### Prerequisites

- **Android Studio** Hedgehog (2023.1.1) or newer
- **JDK 17 or 21**
- **Android SDK** API 34+

### How to Use

1. **Download** the source code as a ZIP file from the repository.
2. **Extract** the files to a folder on your computer.
3. Open **Android Studio** and select **"Open"**.
4. Navigate to the extracted folder and select the `calyz` directory.
5. Sync Gradle files (requires internet connection).
6. Run on a physical device (recommended for real call log data) or emulator.

---

## 🛡️ Permissions

To provide its core functionality, **calyz** requires:

- `READ_CALL_LOG`: To analyze your communication patterns.
- `READ_CONTACTS`: To display names and profile photos instead of raw numbers.

---

## 📄 License

MIT License

**Built with ❤️ by @daudibrahimhasan**  
_A Product of Nexasity AI_
