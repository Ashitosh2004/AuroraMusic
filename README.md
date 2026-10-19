# Aurora Music

<p align="center">
  <strong>A calm, personal home for the music on your Android device.</strong><br>
  Discover your library, build playlists, and settle into a player that feels like yours.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/platform-Android-3DDC84?logo=android&logoColor=white" alt="Platform: Android">
  <img src="https://img.shields.io/badge/min%20Android-8.0%20(API%2026)-34A853" alt="Minimum Android version: 8.0">
  <img src="https://img.shields.io/badge/Kotlin-1.9.23-7F52FF?logo=kotlin&logoColor=white" alt="Kotlin 1.9.23">
  <img src="https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4" alt="Jetpack Compose and Material 3">
</p>

> **Live demo:** Deployment URL not provided yet.

## A look inside

<table>
  <tr>
    <td align="center"><strong>Home</strong><br><img src="Aurora%20Music/Screenshot_20261017_134313.png" width="220" alt="Aurora Music home screen with recently added tracks, albums, and artists"></td>
    <td align="center"><strong>Library</strong><br><img src="Aurora%20Music/Screenshot_20261017_134700.png" width="220" alt="Music library with search and song list"></td>
  </tr>
  <tr>
    <td align="center"><strong>Now playing</strong><br><img src="Aurora%20Music/Screenshot_20261017_134437.png" width="220" alt="Now playing screen with album artwork and queue"></td>
    <td align="center"><strong>Vinyl player</strong><br><img src="Aurora%20Music/Screenshot_20261017_134637.png" width="220" alt="Vinyl-inspired player design"></td>
  </tr>
  <tr>
    <td align="center"><strong>Favorites</strong><br><img src="Aurora%20Music/Screenshot_20261017_134739.png" width="220" alt="Favorites screen with saved songs"></td>
    <td align="center"><strong>Playlists</strong><br><img src="Aurora%20Music/Screenshot_20261017_134807.png" width="220" alt="Smart and custom playlists screen"></td>
  </tr>
  <tr>
    <td colspan="2" align="center"><strong>Make it yours</strong><br><img src="Aurora%20Music/Screenshot_20261017_134520.png" width="220" alt="Settings screen with theme and accent color options"></td>
  </tr>
</table>

## Features

- Browse music on your device by song, album, and artist.
- Search your library and save tracks to Favorites.
- Create playlists and use smart playlists such as Recently Played.
- Keep listening with background playback and a home-screen widget.
- Choose from multiple player layouts and personalize the look with themes and accent colors.
- Fine-tune listening with playback controls, an equalizer, and a sleep timer.

## Built with

- Kotlin and Android
- Jetpack Compose and Material 3
- AndroidX Media3 (ExoPlayer and MediaSession)
- DataStore Preferences
- Coil

## Run locally

1. Clone the repository and open it in Android Studio.
2. Use JDK 17 and allow Gradle to sync.
3. Run the `app` configuration on an Android device or emulator running Android 8.0 (API 26) or later.

To build a debug APK from the project root on Windows:

```powershell
.\gradlew.bat assembleDebug
```

## Project details

- **Application ID:** `com.zephyr.musicplayer`
- **Minimum Android version:** 8.0 (API 26)
- **Compile / target SDK:** 34
- **Version:** 1.0.0
