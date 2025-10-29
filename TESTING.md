# Testing Guide for Neuro-Nudge

This document provides comprehensive testing instructions for the Neuro-Nudge Android application.

---

## Table of Contents

1. [Test Environment Setup](#test-environment-setup)
2. [Unit Tests](#unit-tests)
3. [Instrumentation Tests](#instrumentation-tests)
4. [Manual Testing](#manual-testing)
5. [Sample Test Utterances](#sample-test-utterances)
6. [Edge Cases](#edge-cases)
7. [Performance Testing](#performance-testing)

---

## Test Environment Setup

### Prerequisites
- Android Studio with project opened
- Android device or emulator running (API 24+)
- Permissions granted (Microphone, Location, Notifications)

### Test Data Preparation
1. Clear app data before each test session:
   ```bash
   adb shell pm clear com.neuronudge.app
   ```

2. Set known GPS location (for emulator):
   - Open Extended Controls (⋮ menu in emulator)
   - Location → Set to specific coordinates (e.g., 37.7749, -122.4194)

---

## Unit Tests

### Running Unit Tests

```bash
# Run all unit tests
./gradlew test

# Run specific test class
./gradlew test --tests CommandParserTest

# Run with coverage
./gradlew testDebugUnitTest jacocoTestReport
```

### CommandParserTest

**Tests**: Voice command parsing logic

#### Test Cases

| Test | Input | Expected Output |
|------|-------|-----------------|
| `testParseStoreCommand_Simple` | "I am leaving my keys on the table" | isQuery=false, item="keys", location="table" |
| `testParseStoreCommand_WithLocationDetail` | "I left my phone in the bedroom" | isQuery=false, item="phone", location="bedroom" |
| `testParseQueryCommand_Simple` | "Where did I leave my keys?" | isQuery=true, item="keys", location=null |
| `testParseQueryCommand_WhereIs` | "Where is my wallet?" | isQuery=true, item="wallet", location=null |
| `testParseQueryCommand_Find` | "Find my glasses" | isQuery=true, item="glasses", location=null |
| `testIsValidCommand_Valid` | Various valid commands | true |
| `testExtractItemName` | "Where are my keys?" | "keys" |

**Expected Result**: All 7 tests pass

**To Debug Failures**:
1. Open `CommandParserTest.kt`
2. Set breakpoint in failing test
3. Right-click test → Debug
4. Inspect parsed values

---

## Instrumentation Tests

### Running Instrumentation Tests

```bash
# Ensure device/emulator is connected
adb devices

# Run all instrumentation tests
./gradlew connectedAndroidTest

# Run specific test class
./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.neuronudge.app.data.LostItemDaoTest
```

### LostItemDaoTest

**Tests**: Room database operations

#### Test Cases

| Test | Action | Expected Result |
|------|--------|-----------------|
| `insertAndRetrieveItem` | Insert LostItem, get count | Count = 1, ID > 0 |
| `findItemByName` | Insert "Car Keys", search "car keys" | Found item with matching name (case-insensitive) |
| `deleteOldItems` | Insert old + new item, delete >30 days | Old item deleted, new item remains, deleted count = 1 |

**Expected Result**: All 3 tests pass

**To Debug Failures**:
1. Check Logcat for SQLite errors
2. Verify Room schema annotations
3. Ensure in-memory database creation succeeds

---

## Manual Testing

### LostToFound Feature

#### Test 1: Save Item via Voice (Happy Path)

**Steps**:
1. Launch app
2. Tap microphone FAB
3. Speak: *"I am leaving my car keys on the kitchen table"*
4. Wait for processing

**Expected**:
- Toast: "Item saved successfully"
- Item appears in LostToFound tab
- Item card shows:
  - Item Name: "car keys"
  - Location: "kitchen table"
  - Timestamp: Current date/time
  - Coordinates: Current GPS location

**Pass Criteria**: Item saved and visible in list

---

#### Test 2: Query Item via Voice (Happy Path)

**Steps**:
1. Ensure at least one item saved (from Test 1)
2. Tap microphone FAB
3. Speak: *"Where are my car keys?"*

**Expected**:
- Dialog appears with:
  - Title: "Item Found"
  - Item: car keys
  - Location: kitchen table
  - Coordinates: (lat, long)
  - Saved: timestamp
- "Navigate" button enabled
- "Close" button available

**Pass Criteria**: Correct item details displayed

---

#### Test 3: Navigate to Item

**Steps**:
1. From item found dialog (Test 2), tap "Navigate"

**Expected**:
- Google Maps opens (or map picker)
- Map centered on saved coordinates
- Marker at item location

**Alternative** (if no Maps app):
- Error toast: "No maps app available"

**Pass Criteria**: Maps opens with correct location

---

#### Test 4: Search Items

**Steps**:
1. Add multiple items with different names:
   - "car keys"
   - "phone"
   - "wallet"
2. Go to LostToFound tab
3. Type "key" in search bar

**Expected**:
- Only "car keys" visible in list
- Other items filtered out
- Clear search → all items return

**Pass Criteria**: Search filters correctly

---

#### Test 5: Delete Item

**Steps**:
1. From LostToFound tab, tap "Delete" on any item
2. Confirmation dialog appears
3. Tap "Delete"

**Expected**:
- Item removed from list
- Toast: "Item deleted"
- Database updated (verify by closing and reopening app)

**Pass Criteria**: Item permanently deleted

---

### SoundSanctuary Feature

#### Test 6: Start Service

**Steps**:
1. Go to SoundSanctuary tab
2. Tap "Start Sound Sanctuary"

**Expected**:
- Button text changes to "Stop Sound Sanctuary"
- Notification appears: "Sound Sanctuary Active"
- Notification shows "Monitoring sound levels"
- Service continues in background (press Home, check notification)

**Pass Criteria**: Service running, notification visible

---

#### Test 7: Threshold Adjustment

**Steps**:
1. With service running, adjust threshold slider
2. Move from 30 dB to 120 dB
3. Observe displayed value

**Expected**:
- Value updates in real-time (e.g., "70 dB")
- Preference saved (check Settings)

**Pass Criteria**: Threshold updates and persists

---

#### Test 8: Auto-Play Trigger

**Prerequisites**: Physical device (emulator microphone is limited)

**Steps**:
1. Set threshold to 50 dB (sensitive)
2. Make loud noise (clap, shout, play loud music)

**Expected**:
- Audio starts playing automatically
- Notification updates: "Playing soothing audio"
- Audio loops seamlessly

**Pass Criteria**: Auto-play triggers on loud sound

---

#### Test 9: Manual Playback Control

**Steps**:
1. While audio playing, tap "Pause"
2. Wait 3 seconds
3. Tap "Resume"

**Expected**:
- Pause: Audio stops, notification updates
- Resume: Audio resumes from beginning (looped)

**Pass Criteria**: Controls work correctly

---

#### Test 10: Background Operation

**Steps**:
1. Start service
2. Press Home button
3. Open other apps
4. Wait 5 minutes
5. Swipe down notification shade
6. Tap Neuro-Nudge notification

**Expected**:
- Service still running
- App reopens to same state
- No crashes in background

**Pass Criteria**: Service persists in background

---

### Settings Feature

#### Test 11: Change Preferences

**Steps**:
1. From MainActivity, tap ⋮ → Settings
2. Toggle "Auto-stop playback"
3. Change "Playback behavior" to "Duck"
4. Go back
5. Reopen Settings

**Expected**:
- Settings persist across sessions
- Behavior changes reflected in app logic

**Pass Criteria**: Preferences saved and loaded

---

## Sample Test Utterances

### Store Commands (LostToFound)

**Valid Inputs**:
- *"I am leaving my keys on the table"*
- *"I left my phone in the bedroom"*
- *"My wallet is on the desk"*
- *"I'm putting my glasses in the drawer"*
- *"Leaving my laptop in the car"*

**Expected**: Parse as store commands with item and location

---

**Edge Cases**:
- *"I left my car keys on the kitchen table in the living room"* (multiple locations)
- *"My blue wallet is on the brown desk"* (adjectives)
- *"Keys table"* (minimal input)

**Expected**: Best-effort parsing, may not be perfect

---

### Query Commands (LostToFound)

**Valid Inputs**:
- *"Where are my keys?"*
- *"Where did I leave my phone?"*
- *"Find my wallet"*
- *"Locate my glasses"*
- *"Where is my laptop?"*

**Expected**: Parse as query commands with item name

---

**Edge Cases**:
- *"Keys?"* (single word)
- *"Where did I put that thing I had earlier?"* (vague)
- *"Find the thing"* (ambiguous)

**Expected**: May not parse correctly, requires item name

---

## Edge Cases

### Edge Case 1: Permissions Denied

**Test**: Deny microphone permission

**Steps**:
1. Fresh install, deny microphone
2. Tap microphone FAB

**Expected**:
- Permission rationale dialog
- Option to go to Settings
- No crash

---

**Test**: Deny location permission

**Steps**:
1. Deny location
2. Try to save item via voice

**Expected**:
- Item saved with (0, 0) coordinates
- Toast: "Location unavailable, saved without coordinates"

---

### Edge Case 2: No GPS Fix

**Test**: Location service disabled

**Steps**:
1. Turn off GPS in device settings
2. Save item

**Expected**:
- Item saved with last known location or (0, 0)
- No crash

---

### Edge Case 3: No Audio File

**Test**: Remove rain_loop.mp3

**Steps**:
1. Delete `app/src/main/res/raw/rain_loop.mp3`
2. Rebuild
3. Start SoundSanctuary

**Expected**:
- App compiles (if placeholder exists)
- Playback fails gracefully
- Logcat error: "Audio resource not found"
- No crash

---

### Edge Case 4: Low Battery

**Test**: Device battery < 15%

**Steps**:
1. Drain battery or use emulator with low battery
2. Start SoundSanctuary

**Expected**:
- Service starts
- Android may show battery warning
- WorkManager cleanup may be delayed

---

### Edge Case 5: Network Unavailable

**Test**: Airplane mode

**Steps**:
1. Enable airplane mode
2. Use all features

**Expected**:
- LostToFound works (local only)
- SoundSanctuary works (local audio)
- Navigation may fail (Maps requires network)

---

## Performance Testing

### Memory Leaks

**Test**: Rotate device multiple times

**Steps**:
1. Open app
2. Rotate device 10 times
3. Check memory usage in Android Profiler

**Expected**:
- Memory usage stable
- No significant leaks
- ViewModels survive rotation

---

### Battery Drain

**Test**: Measure battery usage

**Steps**:
1. Fully charge device
2. Start SoundSanctuary
3. Run for 1 hour
4. Check battery stats

**Expected**:
- Battery drain < 10% per hour (monitoring)
- Higher if playing audio continuously
- Acceptable for foreground service

---

### Database Performance

**Test**: Large dataset

**Steps**:
1. Add 1000 items programmatically
2. Search items
3. Measure query time

**Expected**:
- Search < 100ms
- Scrolling smooth
- No ANR (Application Not Responding)

---

## Test Reporting

### Test Results Template

```
Test: [Test Name]
Date: [YYYY-MM-DD]
Tester: [Name]
Device: [Device Model, Android Version]
Build: [APK Version]

Results:
✅ Pass
❌ Fail
⚠️ Partial

Issues:
[List any bugs or unexpected behavior]

Logs:
[Attach relevant Logcat output]

Screenshots:
[Attach if UI-related]
```

---

## Automated Testing (Future)

### UI Tests with Espresso

```kotlin
// Example: Test item save flow
@Test
fun testSaveItemFlow() {
    onView(withId(R.id.fab)).perform(click())
    // Simulate voice input
    onView(withText("Item saved successfully")).check(matches(isDisplayed()))
}
```

### Integration Tests

```kotlin
// Example: Test end-to-end workflow
@Test
fun testLostToFoundWorkflow() {
    // Save → Query → Navigate → Delete
}
```

---

## Debugging Tips

1. **Logcat Filters**:
   - `tag:SoundSanctuary` - Service logs
   - `tag:CleanupWorker` - Worker logs
   - `package:com.neuronudge.app` - All app logs

2. **Database Inspector**:
   - View → Tool Windows → Database Inspector
   - Inspect `lost_items` table in real-time

3. **Network Inspector**:
   - View → Tool Windows → Network Inspector
   - Verify no unexpected network calls

4. **Layout Inspector**:
   - Tools → Layout Inspector
   - Debug UI hierarchy and view properties

---

## Conclusion

This testing guide covers:
- ✅ 7 Unit tests (CommandParser)
- ✅ 3 Instrumentation tests (Room DAO)
- ✅ 11+ Manual test cases (Features)
- ✅ 5 Edge case scenarios
- ✅ Performance tests

**Total Coverage**: ~26 test scenarios

For issues or questions, refer to SETUP_GUIDE.md or README.md.

---

**Happy Testing!** 🧪
