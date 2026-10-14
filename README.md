# jurusanator

Native Android college-major recommender written in **100% Java** (Android platform APIs only, no Kotlin, no third-party libraries).

## Open and run
Open in Android Studio, let Gradle sync, run the `app` configuration. Needs JDK 17, Android SDK Platform 35, AGP 8.7.3, minSdk 23.
CLI: `gradle assembleDebug` -> `app/build/outputs/apk/debug/app-debug.apk`.

## Features
- Neo-brutalist soft-lime / dark-green design, light and dark theme, English + Indonesian (switch any time).
- Animated welcome scene, sound effects (can be muted), floating quiz cards, colourful stats and confetti.
- 67 majors, each with its own vector illustration (drawn in code).
- Quiz: questions are picked at random among the most useful ones and prefer topics related to your previous answer. Each quiz has a saved seed, so it can always be replayed exactly.
- Results: top matches plus one **+1 WILDCARD** major, always shown last.
- Coach: pick a university, see strengths and weaknesses, daily news, study plan, calendar reminders (permission asked first), progress tracking, and a review of why an application failed.

## Honest limits
- Peer comparison and accepted-student benchmarks are **demo assumptions** until real shared data exists.
- News comes from public RSS feeds and needs internet; the app works offline without it.
- Admission dates are estimates from recent years; check official SNPMB announcements.

## Code map
All code is in `app/src/main/java/jurusanator`. Pure logic (`MajorRecommender`, `History`, `Coach`, `Plan`, `Profile`) has no Android imports.
