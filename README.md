# Skillful Pixel Dungeon Platformer

Skillful Pixel Dungeon Platformer is a LibGDX action-platformer built around Pixel Dungeon-inspired enemies, items, achievements, and progression systems.

This public repository contains the game code, assets, and build configuration needed to run the desktop build and assemble the Android app. Store-specific identifiers have been replaced with placeholders so the project can be shared safely.

## Get the game

Use the official store pages to download the game, or visit the website for news and support.

[![Get it on Google Play](https://img.shields.io/badge/Get%20it%20on-Google%20Play-414141?logo=googleplay&logoColor=white)](https://play.google.com/store/apps/details?id=com.bilboldev.skillfulpixeldungeonplatformer)
[![Get it on Steam](https://img.shields.io/badge/Get%20it%20on-Steam-171a21?logo=steam&logoColor=white)](https://store.steampowered.com/app/4725620)
[![Visit the website](https://img.shields.io/badge/Visit-the%20website-0a66c2?logo=googlechrome&logoColor=white)](https://bilbolstack.com)

## Project layout

- `core`: shared gameplay code, content logic, and screens
- `desktop`: desktop launcher and optional Steam integration hooks
- `android`: Android launcher, Google Play Games integration hooks, and billing integration hooks
- `assets`: runtime textures, audio, fonts, and localization bundles

## Building

Requirements:

- JDK 17 or newer available through `JAVA_HOME` or on `PATH`
- Android SDK configured locally if you want Android builds

Common commands:

```bash
./gradlew :desktop:classes
./gradlew :desktop:run
./gradlew :android:assembleDebug
```

On Windows, use `gradlew.bat` instead of `./gradlew`.

## Platform configuration

This repository does not include live store credentials or platform IDs.

Google Play Games:

- Fill `android/res/values/game-ids.xml` with your own Play Games app ID and achievement IDs.

Google Play Billing:

- Fill `android/res/values/store-ids.xml` with your own in-app product IDs.

Steam:

- Set a Steam app ID through `SPD_STEAM_APP_ID`, `-Dspd.steamAppId=...`, or a Gradle property named `steamAppId`.
- Optional depot properties for packaging can be supplied through Gradle properties:
  - `steamWindows64DepotId`
  - `steamLinux64DepotId`
  - `steamMacArm64DepotId`

Without a configured Steam app ID, the desktop build runs without Steam ownership checks.

## AI and tooling disclosure

No art assets were created with generative AI, and the game does not include runtime generative AI features. GitHub Copilot was used as an assistive development tool during development.

## License

This project is distributed under the GNU GPL v3. See `LICENSE.txt` for the full license text.