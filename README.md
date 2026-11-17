# Checklist Wallpaper

## Build in VS Code (command line)
1. Install JDK 17 and Android SDK command-line tools (or Android Studio's SDK, which is installed once).
2. Set ANDROID_HOME to your SDK folder.
3. Open this folder in VS Code and run in the terminal:
   gradle assembleDebug
4. The APK appears at app/build/outputs/apk/debug/app-debug.apk

## Install
1. Copy app-debug.apk to your phone and install it (allow "Install unknown apps").
2. Settings > Wallpaper > Live wallpaper > Checklist Wallpaper.
3. Tap a row to tick it.

## Change tasks
Edit the `items` list in app/src/main/java/com/example/checklist/ChecklistWallpaperService.kt
