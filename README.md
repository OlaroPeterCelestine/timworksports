# Crested Pass

Timwork Sports' unified fan app for AFCON 2027, licensed to Uganda Tourism Board: ticketing, a cashless wallet, a tourism trail with stamps and rewards, and a match predictor — all under one login.

This is a design / investment-pitch prototype. Flows use mock data; there is no production payments, AR, or live location stack.

Two native apps ship the same product:

| Folder | Platform | UI |
| --- | --- | --- |
| `swift/` | iOS 16+ | SwiftUI |
| `kotlin/` | Android 8+ (API 26) | Jetpack Compose, Material 3 |

## Tabs

Home · Discover · Wallet · Trail · You

Predict lives on Home and You (not a fifth-plus tab). Settings opens from You.

Detail routes: Fan Embassy / Squad, match ticket, AR lens stub, Crested Band, Predict, Settings.

## Run iOS (`swift/`)

1. Open `swift/TimworkSports.xcodeproj` in Xcode 15 or later.
2. Select an iPhone simulator.
3. Press Run. Display name is **Crested Pass** (`com.timworksports.crestedpass`).

## Run Android (`kotlin/`)

1. Open the `kotlin/` folder in Android Studio.
2. Let Gradle sync, then run the `app` configuration.

```bash
cd kotlin && ./gradlew installDebug
```

If `local.properties` is missing, Android Studio will write your SDK path on first sync.
