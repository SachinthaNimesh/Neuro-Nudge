# Audio Resources

## Current Audio File

A minimal placeholder MP3 file (`rain_loop.mp3`) is included for development and testing purposes. This is a 1-second silent audio loop that allows the app to compile and run without errors.

## Adding Real Soothing Audio

To enable proper SoundSanctuary functionality, replace the placeholder with an actual soothing audio file:

### Required File:
- **Filename:** `rain_loop.mp3` (replace existing file)
- **Format:** MP3 or OGG
- **Type:** Looping ambient sound (rain, ocean waves, white noise, etc.)
- **Duration:** 30-120 seconds (will loop automatically)
- **Quality:** 128-192 kbps recommended for balance between quality and size

### How to Replace:

1. Find or create a soothing ambient sound file
2. Name it `rain_loop.mp3`
3. Replace the existing placeholder file in: `app/src/main/res/raw/rain_loop.mp3`
4. Rebuild the project

### Free Audio Resources:

You can find free ambient sounds at:
- **Freesound.org** (https://freesound.org/) - CC0 licensed sounds
- **Pixabay** (https://pixabay.com/sound-effects/) - Royalty-free sounds
- **Zapsplat** (https://www.zapsplat.com/) - Free sound effects
- **YouTube Audio Library** - Royalty-free ambient sounds

### Creating Your Own Loop:

You can use tools like **Audacity** (free, open-source) to:
1. Record or download ambient sound
2. Trim to desired length (30-60 seconds is ideal)
3. Apply fade in/out at the beginning and end for seamless looping
4. Normalize audio levels for consistent playback
5. Export as MP3 (128-192 kbps) or OGG format
6. Replace the placeholder file in this directory

### Supported Audio Formats:
- **MP3** (recommended) - Best compatibility
- **OGG Vorbis** - Good compression, open format
- **WAV** (not recommended - very large file size)
- **AAC** (in MP4 or M4A container) - Good quality

The Android MediaPlayer class natively supports these formats.

### Recommended Ambient Sounds:

For ADHD sensory comfort, these types work well:
- **Rain sounds** - Gentle rainfall, thunderstorm (distant)
- **Ocean waves** - Beach waves, underwater sounds
- **White/Pink/Brown noise** - Consistent, masking sounds
- **Nature sounds** - Forest ambience, birdsong, wind
- **Instrumental** - Soft piano, ambient music

### Note:

The current placeholder is silent and serves only to allow compilation. For actual therapeutic benefit, replace it with a real ambient audio file as described above.

