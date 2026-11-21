# ScrollBar Android
Native Android floating scroll assistant. Target Android 13+, min Android 8.

## Build
Requires JDK 17 and Gradle 8.9:
`gradle assembleDebug`
APK: `app/build/outputs/apk/debug/app-debug.apk`

GitHub Actions builds the APK on every push to main and on manual dispatch. Download it from Actions → latest successful run → Artifacts.

## Setup
1. Install the APK.
2. Open ScrollBar and enable its Accessibility Service in Android Settings → Accessibility.
3. Tap Start / refresh floating bar.
4. Grant overlay permission if Android asks.

## Controls
Tap upper bar area to scroll up, lower area to scroll down, middle area to toggle auto-scroll. Drag to move vertically. Preferences include speed, size, opacity, edge, and theme.

## Limitations
Accessibility scrolling depends on the target app exposing scrollable nodes. The draggable bar repositions the control, not the document's exact scroll thumb. Proportional jump-to-position is not universally available to third-party Android apps. Some games, protected screens, and custom-rendered apps may not respond.