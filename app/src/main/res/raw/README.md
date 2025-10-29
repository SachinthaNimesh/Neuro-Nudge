# Audio Resources

## Adding Soothing Audio

To enable the SoundSanctuary feature, you need to add a soothing audio file to this directory.

### Required File:
- **Filename:** `rain_loop.mp3`
- **Format:** MP3 or OGG
- **Type:** Looping ambient sound (rain, ocean waves, white noise, etc.)
- **Duration:** 30-120 seconds (will loop automatically)

### How to Add:

1. Find or create a soothing ambient sound file
2. Name it `rain_loop.mp3` (or modify the service code to use a different name)
3. Place it in this directory: `app/src/main/res/raw/`

### Free Audio Resources:

You can find free ambient sounds at:
- https://freesound.org/ (CC0 licensed sounds)
- https://pixabay.com/sound-effects/ (Royalty-free sounds)
- https://www.zapsplat.com/ (Free sound effects)

### Alternative:

If you don't have an audio file, the app will still compile and run. The SoundSanctuary service will start, but playback will fail gracefully if the audio resource is missing. You'll see an error in the logs but the app won't crash.

### Creating Your Own Loop:

You can use tools like Audacity (free) to:
1. Record or download ambient sound
2. Trim to desired length
3. Apply fade in/out at ends for seamless looping
4. Export as MP3 or OGG format
5. Place in this directory

### Supported Audio Formats:
- MP3 (recommended)
- OGG Vorbis
- WAV (not recommended - large file size)
- AAC (in MP4 or M4A container)

The Android MediaPlayer class will automatically handle these formats.
