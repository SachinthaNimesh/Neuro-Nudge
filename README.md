# Neuro-Nudge: ADHD Assistance App

**A prototype Android application to help people with ADHD handle sensory overload and forgetfulness.**

This mobile app prototype demonstrates two key features designed for eventual implementation on a wearable device:
- **LostToFound**: Track and locate misplaced items using voice commands
- **SoundSanctuary**: Automatic soothing audio playback in response to sensory overload

---

## Table of Contents

1. [Features](#features)
2. [Project Structure](#project-structure)
3. [Setup & Installation](#setup--installation)
4. [Running the App](#running-the-app)
5. [Usage Guide](#usage-guide)
6. [Testing](#testing)
7. [Technical Details](#technical-details)
8. [Privacy & Battery Considerations](#privacy--battery-considerations)
9. [Next Steps](#next-steps)
10. [Troubleshooting](#troubleshooting)

---

## Features

### 1. LostToFound

**Purpose**: Remember where you left items and retrieve their location via voice or text.

**Capabilities**:
- Record item locations using voice commands (e.g., *"I'm leaving my car keys on the kitchen table"*)
- Store item name, human-readable location, GPS coordinates, and timestamp
- Query item locations (e.g., *"Where are my car keys?"*)
- View all saved items in a searchable list
- Navigate to item locations using Google Maps
- Automatic 30-day cleanup of old records

**Implementation**:
- Room database for local storage
- FusedLocationProvider for GPS coordinates
- Simple NLP parser for command interpretation
- WorkManager for scheduled cleanup

### 2. SoundSanctuary

**Purpose**: Reduce sensory overload by automatically playing soothing sounds when ambient noise exceeds a threshold.

**Capabilities**:
- Continuous ambient sound monitoring
- User-configurable sound threshold (30-120 dB)
- Automatic playback of soothing audio (rain, ocean, etc.) when threshold is exceeded
- Foreground service for background operation
- Volume and playback controls
- Pause/resume functionality

**Implementation**:
- Foreground service with microphone access
- Real-time SPL calculation from audio samples
- MediaPlayer for looping audio playback
- SharedPreferences for settings persistence

### 3. Priority Management

**Behavior**:
- LostToFound voice commands take precedence over SoundSanctuary playback
- Configurable playback behavior (auto-pause, duck, or ignore)
- Microphone conflict resolution

---

## Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/neuronudge/app/
│   │   │   ├── data/
│   │   │   │   ├── model/          # Room entities
│   │   │   │   ├── dao/            # Data Access Objects
│   │   │   │   ├── repository/     # Repository pattern
│   │   │   │   └── AppDatabase.kt
│   │   │   ├── ui/
│   │   │   │   ├── losttofound/    # LostToFound UI components
│   │   │   │   ├── soundsanctuary/ # SoundSanctuary UI components
│   │   │   │   ├── MainActivity.kt
│   │   │   │   └── SettingsActivity.kt
│   │   │   ├── service/
│   │   │   │   └── SoundSanctuaryService.kt
│   │   │   ├── worker/
│   │   │   │   └── CleanupWorker.kt
│   │   │   ├── util/
│   │   │   │   ├── CommandParser.kt
│   │   │   │   └── PreferencesHelper.kt
│   │   │   └── NeuroNudgeApplication.kt
│   │   ├── res/
│   │   │   ├── layout/           # XML layouts
│   │   │   ├── values/           # Strings, colors, themes
│   │   │   ├── menu/             # Menu resources
│   │   │   ├── xml/              # Preferences, backup rules
│   │   │   ├── drawable/         # Icons and graphics
│   │   │   └── raw/              # Audio files
│   │   └── AndroidManifest.xml
│   ├── test/                     # Unit tests
│   └── androidTest/              # Instrumentation tests
├── build.gradle
└── proguard-rules.pro
```

---

## Setup & Installation

### Prerequisites

- **Android Studio**: Arctic Fox (2020.3.1) or newer
- **JDK**: Java 17
- **Android SDK**: API Level 34 (Android 14)
- **Gradle**: 8.0+

### Step-by-Step Setup

1. **Clone the repository**:
   ```bash
   git clone https://github.com/SachinthaNimesh/Neuro-Nudge.git
   cd Neuro-Nudge
   ```

2. **Open in Android Studio**:
   - Launch Android Studio
   - Select "Open an existing project"
   - Navigate to the cloned repository
   - Wait for Gradle sync to complete

3. **Add audio resource** (optional but recommended):
   - Download or create a soothing audio loop (rain, ocean, white noise)
   - Name it `rain_loop.mp3`
   - Place in: `app/src/main/res/raw/rain_loop.mp3`
   - See `app/src/main/res/raw/README.md` for audio sources

4. **Sync Gradle**:
   - Click "Sync Project with Gradle Files" in Android Studio
   - Wait for dependencies to download

5. **Build the project**:
   ```bash
   ./gradlew build
   ```

---

## Running the App

### On Emulator

1. **Create an AVD** (Android Virtual Device):
   - Open AVD Manager in Android Studio
   - Create a new device with API Level 24+
   - Start the emulator

2. **Run the app**:
   - Click the "Run" button (green play icon)
   - Select your emulator
   - App will install and launch automatically

3. **Grant permissions**:
   - When prompted, grant:
     - Microphone access (for voice commands and sound monitoring)
     - Location access (for storing item coordinates)
     - Notification access (for foreground service on Android 13+)

### On Physical Device

1. **Enable Developer Options**:
   - Go to Settings → About Phone
   - Tap "Build Number" 7 times
   - Enable "Developer Options"

2. **Enable USB Debugging**:
   - Settings → Developer Options → USB Debugging

3. **Connect device and run**:
   - Connect via USB
   - Click "Run" in Android Studio
   - Select your device
   - Grant permissions when prompted

### Testing Permissions

**Important**: The app requires several runtime permissions. On first launch:

1. **Microphone**: Required for voice commands and sound monitoring
2. **Location**: Required for storing item GPS coordinates
3. **Notifications** (Android 13+): Required for foreground service

If permissions are denied, some features will not work. You can re-grant them in:
- Settings → Apps → Neuro-Nudge → Permissions

---

## Usage Guide

### LostToFound

#### Saving Items

**Via Voice**:
1. Tap the microphone FAB (floating action button)
2. Say: *"I'm leaving my car keys on the kitchen table"*
3. App will extract item name, location, and save with current GPS coordinates

**Voice Command Examples**:
- *"I left my phone in the bedroom"*
- *"My wallet is on the desk"*
- *"I'm putting my glasses in the drawer"*

#### Finding Items

**Via Voice**:
1. Tap the microphone FAB
2. Say: *"Where are my car keys?"*
3. App displays location and provides "Navigate" button

**Via List**:
1. Go to LostToFound tab
2. Browse or search saved items
3. Tap an item to view details
4. Tap "Navigate" to open Google Maps

#### Managing Items

- **Search**: Use the search bar to filter items by name
- **Delete**: Tap the "Delete" button on any item
- **View Details**: Tap an item card to see full details

### SoundSanctuary

#### Starting the Service

**Option 1**: Via Fragment
1. Go to SoundSanctuary tab
2. Tap "Start Sound Sanctuary"
3. Service runs in foreground (notification appears)

**Option 2**: Via Menu
1. Tap the three-dot menu
2. Select "Toggle Sound Sanctuary"

#### Adjusting Settings

**Sound Threshold**:
- Use the slider to set threshold (30-120 dB)
- Lower = more sensitive (plays audio more often)
- Higher = less sensitive (only plays in very loud environments)

**Volume**:
- Adjust playback volume (0-100%)
- Changes take effect immediately

**Finding Your Threshold**:
1. Start the service
2. Move to different environments (quiet room, busy street)
3. Adjust threshold until auto-play behaves as desired
4. Typical values: 60-80 dB

#### Playback Controls

- **Pause**: Manually pause soothing audio
- **Resume**: Resume playback
- **Stop Service**: Stop sound monitoring entirely

### Settings

Access via Menu → Settings:

- **Auto-stop playback**: Stop audio when sound drops below threshold
- **Playback behavior**: Choose how SoundSanctuary behaves during voice commands
  - Auto-pause: Pause playback for voice commands
  - Duck: Lower volume for voice commands
  - Ignore: Don't process voice commands while playing

---

## Testing

### Running Unit Tests

```bash
./gradlew test
```

Tests include:
- Command parser logic (parsing voice commands)
- Database operations (CRUD for items)

### Running Instrumentation Tests

```bash
./gradlew connectedAndroidTest
```

Tests include:
- Room database integration
- DAO operations with actual SQLite

### Manual Testing Checklist

**LostToFound**:
- [ ] Save item via voice command
- [ ] Query item via voice command
- [ ] View item in list
- [ ] Search items
- [ ] Navigate to item location
- [ ] Delete item
- [ ] Verify 30-day cleanup (change device date or wait)

**SoundSanctuary**:
- [ ] Start service
- [ ] Verify notification appears
- [ ] Adjust threshold
- [ ] Trigger auto-play (make loud noise)
- [ ] Pause/resume playback
- [ ] Adjust volume
- [ ] Stop service

**Permissions**:
- [ ] Deny microphone → verify graceful degradation
- [ ] Deny location → verify fallback (0,0 coordinates)
- [ ] Grant permissions later → verify app works

**Priority Management**:
- [ ] Start SoundSanctuary
- [ ] Use voice command while playing
- [ ] Verify playback behavior matches settings

---

## Technical Details

### Architecture

**Pattern**: MVVM (Model-View-ViewModel)

**Components**:
- **Model**: Room entities, DAOs, Repository
- **View**: Activities, Fragments, XML layouts
- **ViewModel**: AndroidViewModel with LiveData
- **Service**: Foreground service for sound monitoring
- **Worker**: WorkManager for background cleanup

### Key Dependencies

```gradle
// Core
androidx.core:core-ktx:1.12.0
androidx.appcompat:appcompat:1.6.1
com.google.android.material:material:1.11.0

// Architecture Components
androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0
androidx.lifecycle:lifecycle-livedata-ktx:2.7.0

// Room Database
androidx.room:room-runtime:2.6.1
androidx.room:room-ktx:2.6.1

// WorkManager
androidx.work:work-runtime-ktx:2.9.0

// Location
com.google.android.gms:play-services-location:21.0.1

// Preferences
androidx.preference:preference-ktx:1.2.1
```

### Database Schema

**Table**: `lost_items`

| Column       | Type    | Description                    |
|--------------|---------|--------------------------------|
| id           | Long    | Primary key (auto-increment)   |
| itemName     | String  | Name of the item               |
| locationName | String  | Human-readable location        |
| latitude     | Double  | GPS latitude                   |
| longitude    | Double  | GPS longitude                  |
| timestamp    | Long    | Unix timestamp (milliseconds)  |

### Voice Command Parsing

**Algorithm**:
1. Normalize input (lowercase, trim)
2. Detect query vs. store based on keywords
3. Extract item name and location using preposition matching
4. Simple regex-based extraction (no ML/NLP library)

**Limitations**:
- English only
- Simple patterns (may not handle complex sentences)
- No context awareness

**Future Enhancements**:
- Integrate on-device ML model (MLKit)
- Support for multiple languages
- Cloud LLM integration (optional, with user consent)

### Sound Level Calculation

**Method**: RMS (Root Mean Square) to approximate SPL

```kotlin
RMS = sqrt(sum(sample^2) / count)
dB = 20 * log10(RMS / reference) + offset
```

**Reference**: 16-bit PCM max amplitude (32768)

**Offset**: +90 to make values positive and human-readable

**Range**: 0-120 dB (clamped)

**Note**: This is an approximation, not calibrated SPL measurement.

---

## Privacy & Battery Considerations

### Privacy

**Data Storage**:
- All data stored locally (no cloud sync)
- Location data never leaves device
- Voice commands processed on-device only

**Permissions**:
- Microphone: Used only for voice recognition and sound monitoring
- Location: Used only when saving item locations
- Notifications: Required for foreground service

**Recommendations**:
- Inform users about data collection in privacy policy
- Provide opt-in for location tracking
- Add encryption for sensitive data if extending to production

### Battery Optimization

**Current Implementation**:
- Foreground service uses microphone continuously (high battery drain)
- Location fetched only when saving items (minimal impact)
- WorkManager respects battery-not-low constraint

**Optimizations for Production**:
- Use periodic sampling instead of continuous monitoring
- Implement adaptive threshold learning (reduce false positives)
- Add user-configurable monitoring intervals
- Use JobScheduler for opportunistic cleanup

**Wearable Considerations**:
- Wearables have limited battery capacity
- Use lower sample rates and longer intervals
- Implement "active hours" feature (e.g., only monitor 9am-9pm)
- Offload processing to paired phone when possible

---

## Next Steps

### Moving to Wearable Prototype

**Platform Options**:
- Wear OS (Google)
- Tizen (Samsung)
- watchOS (Apple, via companion app)

**Adaptations Needed**:
1. **UI**: Redesign for small circular/square screens
2. **Input**: Use on-device voice recognition (always-listening wake words)
3. **Sensors**: Leverage wearable-specific sensors (heart rate, motion)
4. **Battery**: Implement aggressive power management
5. **Connectivity**: Sync data with phone for navigation

**Recommended Approach**:
1. Start with Wear OS (extends Android codebase)
2. Create companion phone app for setup and data management
3. Use Bluetooth or WiFi for phone-watch communication
4. Implement local ML model for voice on wearable
5. Add haptic feedback for notifications

### Feature Enhancements

**LostToFound**:
- Photo attachment for items
- AR navigation using camera
- Proximity alerts ("You're near your keys!")
- Cloud sync across devices
- Item categories and tags

**SoundSanctuary**:
- Multiple audio tracks (rain, ocean, white noise, etc.)
- Adaptive threshold learning
- Integration with calendar (auto-enable during meetings)
- Biofeedback (combine with heart rate sensor)
- Time-of-day presets

**New Features**:
- Medication reminders
- Focus timer (Pomodoro)
- Habit tracking
- Breathing exercises
- Integration with smart home (lights, temperature)

---

## Troubleshooting

### Build Issues

**Problem**: Gradle sync failed
```
Solution:
- Check internet connection
- Update Gradle: ./gradlew wrapper --gradle-version=8.0
- Invalidate caches: File → Invalidate Caches / Restart
```

**Problem**: AAPT error (resource not found)
```
Solution:
- Ensure all layout files reference valid resources
- Check for typos in @string, @drawable references
- Clean and rebuild: Build → Clean Project → Rebuild
```

### Runtime Issues

**Problem**: App crashes on launch
```
Solution:
- Check Logcat for stack trace
- Verify all required permissions in AndroidManifest.xml
- Ensure Room database migration strategy (fallbackToDestructiveMigration)
```

**Problem**: Voice recognition doesn't work
```
Solution:
- Check microphone permission granted
- Verify device has Google app installed (for SpeechRecognizer)
- Test on physical device (emulator voice input is limited)
```

**Problem**: Location always (0, 0)
```
Solution:
- Grant location permission
- Enable GPS on device
- Wait for GPS fix (may take 30-60 seconds)
- Use emulator extended controls to set mock location
```

**Problem**: Audio doesn't play
```
Solution:
- Check if rain_loop.mp3 exists in res/raw/
- Verify audio format is supported (MP3, OGG, WAV)
- Check device volume and mute settings
- Look for MediaPlayer errors in Logcat
```

**Problem**: Service stops unexpectedly
```
Solution:
- Disable battery optimization for the app
- Ensure foreground service notification is showing
- Check for permission revocations
- On Android 12+, verify FOREGROUND_SERVICE_MICROPHONE permission
```

### Testing Issues

**Problem**: Tests fail with database errors
```
Solution:
- Use in-memory database for tests
- Add @get:Rule val instantTaskExecutorRule = InstantTaskExecutorRule()
- Allow main thread queries for tests: .allowMainThreadQueries()
```

---

## Contributing

This is a prototype for demonstration purposes. For production use:

1. Add comprehensive error handling
2. Implement proper authentication (if adding cloud features)
3. Add analytics (privacy-respecting)
4. Conduct accessibility audit
5. Perform security audit
6. Add comprehensive UI/UX testing
7. Internationalization (i18n)

---

## License

This project is a prototype for demonstration and educational purposes.

---

## Contact & Support

For questions, feedback, or bug reports, please open an issue on GitHub.

---

## Acknowledgments

- Built with Android Jetpack libraries
- Material Design components
- Open-source audio resources (see res/raw/README.md)

---

**End of README**

This project demonstrates a thoughtful approach to helping people with ADHD manage sensory challenges and forgetfulness. The mobile prototype serves as a proof-of-concept for eventual wearable integration, prioritizing simplicity, robustness, and user privacy.
