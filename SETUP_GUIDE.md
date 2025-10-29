# Neuro-Nudge Setup & Testing Guide

Complete step-by-step instructions to build, run, and test the Neuro-Nudge Android application.

---

## Prerequisites

Before you begin, ensure you have:

### Required Software
- **Android Studio** (Arctic Fox 2020.3.1 or newer)
  - Download: https://developer.android.com/studio
- **Java Development Kit (JDK) 17**
  - Included with Android Studio or download separately
- **Git** (for cloning the repository)
  - Download: https://git-scm.com/downloads

### Recommended
- **Physical Android device** (for full feature testing)
  - Android 7.0 (API 24) or higher
  - USB cable for connection
- **Google Play Services** installed on device (for location services)

---

## Step 1: Clone the Repository

```bash
git clone https://github.com/SachinthaNimesh/Neuro-Nudge.git
cd Neuro-Nudge
```

---

## Step 2: Open Project in Android Studio

1. Launch **Android Studio**
2. Click **File → Open**
3. Navigate to the cloned `Neuro-Nudge` directory
4. Click **OK**
5. Wait for Gradle sync to complete (may take 2-5 minutes on first run)

**Troubleshooting**:
- If Gradle sync fails, click **File → Sync Project with Gradle Files**
- Ensure you have a stable internet connection for dependency downloads
- Check that JDK 17 is selected: **File → Project Structure → SDK Location**

---

## Step 3: Verify Project Structure

After Gradle sync, verify these directories exist:
```
Neuro-Nudge/
├── app/
│   ├── build.gradle
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── java/com/neuronudge/app/
│       │   └── res/
│       ├── test/
│       └── androidTest/
├── build.gradle
├── settings.gradle
└── gradle/
```

---

## Step 4: Build the Project

### Option A: Using Android Studio
1. Click **Build → Make Project** (or press Ctrl+F9 / Cmd+F9)
2. Wait for build to complete (check bottom status bar)
3. Ensure "BUILD SUCCESSFUL" appears in the Build Output

### Option B: Using Command Line
```bash
# On Linux/Mac
./gradlew assembleDebug

# On Windows
gradlew.bat assembleDebug
```

**Expected Output**:
```
BUILD SUCCESSFUL in 30s
```

**Common Build Errors**:

| Error | Solution |
|-------|----------|
| "SDK location not found" | Set SDK path in `local.properties` or via Android Studio settings |
| "Failed to resolve: androidx.*" | Ensure internet connection, sync Gradle again |
| "Unsupported class file major version" | Update to JDK 17 |

---

## Step 5: Set Up Testing Environment

### Option A: Physical Device (Recommended)

1. **Enable Developer Options** on your Android device:
   - Go to **Settings → About Phone**
   - Tap **Build Number** 7 times
   - Go back to **Settings → Developer Options**

2. **Enable USB Debugging**:
   - In Developer Options, enable **USB Debugging**
   - Connect device via USB
   - Accept the "Allow USB debugging?" prompt on device

3. **Verify Connection**:
   - In Android Studio, check the device dropdown (top toolbar)
   - Your device should appear in the list

### Option B: Android Emulator

1. **Open AVD Manager**:
   - Click **Tools → AVD Manager**
   - Or click the device icon in the top toolbar

2. **Create Virtual Device**:
   - Click **Create Virtual Device**
   - Select a device definition (e.g., "Pixel 6")
   - Click **Next**

3. **Select System Image**:
   - Choose an API Level 24+ image (recommended: API 33 or 34)
   - Click **Download** if not already installed
   - Click **Next**, then **Finish**

4. **Start Emulator**:
   - Click the green play button next to your AVD
   - Wait for emulator to boot (1-2 minutes)

---

## Step 6: Run the Application

1. **Select Run Configuration**:
   - Ensure "app" is selected in the run configuration dropdown (top toolbar)

2. **Select Device**:
   - Choose your physical device or running emulator from the device dropdown

3. **Run**:
   - Click the green **Run** button (or press Shift+F10)
   - Wait for APK to build and install (30-60 seconds)

4. **Grant Permissions** (on first launch):
   - The app will request permissions
   - **Microphone**: Tap **Allow** (required for voice commands and sound monitoring)
   - **Location**: Tap **Allow all the time** or **While using the app** (required for item locations)
   - **Notifications** (Android 13+): Tap **Allow** (required for foreground service)

**Expected Behavior**:
- App launches showing two tabs: "LostToFound" and "SoundSanctuary"
- Bottom navigation and floating action button (microphone) visible
- Empty state message: "No items saved yet"

---

## Step 7: Test LostToFound Feature

### Test 1: Save an Item via Voice

1. **Tap the microphone FAB** (floating action button at bottom-right)
2. **Grant microphone permission** if prompted
3. **Speak clearly**: *"I am leaving my keys on the kitchen table"*
4. **Verify**:
   - Toast message appears: "Item saved successfully"
   - Item appears in the list with:
     - Item name: "keys"
     - Location: "kitchen table"
     - Timestamp
     - Current GPS coordinates (or 0,0 if location unavailable)

### Test 2: Query an Item via Voice

1. **Tap the microphone FAB**
2. **Speak**: *"Where are my keys?"*
3. **Verify**:
   - Dialog appears showing:
     - Item: keys
     - Location: kitchen table
     - Coordinates
     - Timestamp
   - "Navigate" button available

### Test 3: Navigate to Item

1. **In the dialog or item list**, tap **Navigate**
2. **Verify**:
   - Google Maps opens (or map picker appears)
   - Location marker at saved coordinates
   - If no maps app installed, error message appears

### Test 4: Search and Manage Items

1. **Go to LostToFound tab**
2. **Use search bar**: Type "keys"
3. **Verify**: Only matching items appear
4. **Tap an item card**: Details dialog appears
5. **Tap Delete**: Confirmation dialog appears
6. **Confirm deletion**: Item removed from list

---

## Step 8: Test SoundSanctuary Feature

### Test 1: Start the Service

1. **Go to SoundSanctuary tab**
2. **Tap "Start Sound Sanctuary"**
3. **Verify**:
   - Button text changes to "Stop Sound Sanctuary"
   - Notification appears: "Sound Sanctuary Active - Monitoring sound levels"
   - App continues monitoring in background

### Test 2: Adjust Threshold

1. **Move threshold slider** (30-120 dB range)
2. **Verify**: Value updates as you slide (e.g., "70 dB")
3. **Test auto-play**:
   - Set threshold to 50 dB (very sensitive)
   - Make a loud noise (clap, talk loudly)
   - **Note**: On emulator, this won't work properly; use physical device

### Test 3: Volume Control

1. **Adjust volume slider** (0-100%)
2. **Verify**: Percentage updates
3. **If audio is playing**: Volume changes in real-time

### Test 4: Playback Controls

1. **Tap Pause**: Audio playback pauses (if playing)
2. **Tap Resume**: Audio resumes
3. **Verify**: Notification updates to show current state

### Test 5: Background Operation

1. **Press Home button** (exit app to background)
2. **Verify**: Notification remains visible
3. **Make loud noise**: Audio should still auto-play
4. **Swipe down notification**: Tap to return to app
5. **Stop service**: Notification disappears

---

## Step 9: Test Settings

1. **From MainActivity**, tap **⋮ menu → Settings**
2. **Verify settings screen** shows:
   - "Auto-stop playback" switch
   - "Playback behavior during voice commands" dropdown
3. **Change settings**:
   - Toggle "Auto-stop playback"
   - Select different playback behavior
4. **Go back**: Settings saved automatically

---

## Step 10: Test WorkManager Cleanup (Optional)

The app automatically deletes items older than 30 days. To test:

### Manual Testing
1. **Use ADB** to change device date:
   ```bash
   # Forward 31 days
   adb shell su 0 date MMDDhhmm2025
   ```
2. **Wait 24 hours** or manually trigger WorkManager
3. **Check items**: Old items should be deleted

### Programmatic Testing
Modify `CleanupWorker.DAYS_TO_KEEP` to `0` (days) temporarily, trigger worker, verify all items deleted.

---

## Step 11: Run Tests

### Unit Tests
```bash
./gradlew test
```

**Expected**: All tests pass for `CommandParserTest`

### Instrumentation Tests
```bash
./gradlew connectedAndroidTest
```

**Required**: Device or emulator must be running

**Expected**: All tests pass for `LostItemDaoTest`

---

## Troubleshooting Common Issues

### Issue: Voice recognition doesn't work
**Solutions**:
- Ensure microphone permission granted
- Verify Google app installed (required for SpeechRecognizer)
- Test on physical device (emulator voice input is limited)
- Check device volume is not muted

### Issue: Location is always (0, 0)
**Solutions**:
- Grant location permission
- Enable GPS/Location services on device
- Wait 30-60 seconds for GPS fix
- On emulator: Use Extended Controls (⋮ menu) → Location to set mock location

### Issue: Audio doesn't play
**Solutions**:
- Check `rain_loop.mp3` exists in `app/src/main/res/raw/`
- Verify device volume is up
- Check for MediaPlayer errors in Logcat
- Ensure audio file is valid MP3 format

### Issue: Service stops in background
**Solutions**:
- Disable battery optimization: Settings → Apps → Neuro-Nudge → Battery → Unrestricted
- Ensure notification permission granted (Android 13+)
- Check manufacturer-specific battery settings (e.g., Xiaomi MIUI, Samsung)

### Issue: App crashes on launch
**Solutions**:
- Check Logcat for stack trace
- Verify all permissions in AndroidManifest.xml
- Clear app data: Settings → Apps → Neuro-Nudge → Storage → Clear Data
- Uninstall and reinstall

### Issue: Build fails with "Duplicate class" error
**Solutions**:
- Clean project: Build → Clean Project
- Invalidate caches: File → Invalidate Caches / Restart
- Delete `.gradle` and `build` directories, sync again

---

## Viewing Logs

### Using Android Studio Logcat

1. Open **Logcat** tab (bottom of Android Studio)
2. Select your device
3. Filter by package: `com.neuronudge.app`
4. Look for tags:
   - `SoundSanctuary`: Service logs
   - `CleanupWorker`: Cleanup job logs
   - `LostToFound`: Feature logs

### Using ADB
```bash
adb logcat | grep "neuronudge"
```

---

## Adding Real Audio (Optional Enhancement)

The included `rain_loop.mp3` is a silent placeholder. For actual soothing audio:

1. **Download ambient sound** from:
   - Freesound.org (CC0 license)
   - Pixabay (royalty-free)

2. **Prepare audio** using Audacity:
   - Trim to 30-60 seconds
   - Add fade in/out (500ms) at start/end
   - Export as MP3 (128 kbps)

3. **Replace file**:
   ```bash
   cp your_audio.mp3 app/src/main/res/raw/rain_loop.mp3
   ```

4. **Rebuild**:
   ```bash
   ./gradlew assembleDebug
   ```

---

## Next Steps After Setup

1. **Customize threshold**: Find your ideal sound threshold in different environments
2. **Test edge cases**: Deny permissions, disable GPS, low battery, etc.
3. **Modify audio**: Replace placeholder with real soothing sounds
4. **Extend features**: Add more item categories, multiple audio tracks, etc.
5. **Export APK**: Build → Build Bundle(s) / APK(s) → Build APK(s)

---

## Getting Help

- **Documentation**: See main `README.md` for detailed feature descriptions
- **Issues**: Check existing issues on GitHub
- **Logs**: Always check Logcat for error details

---

**Congratulations!** Your Neuro-Nudge app is now fully set up and tested. Enjoy using the prototype!
