# Talmudic Stories Daily 

A modern, offline-first Android application that delivers daily Jewish wisdom. Built entirely with **Kotlin** and **Jetpack Compose**, following **MVI (Model-View-Intent)** architecture.

## Tech Stack & Skills Demonstrated

* **UI:** Jetpack Compose (Material3), Custom Theming (Day/Cream/Night modes), Canvas Drawing.
* **Architecture:** MVI (Unidirectional Data Flow), ViewModel, Repository Pattern.
* **Local Data:** Room Database (SQL) for offline caching and favorites.
* **Concurrency:** Kotlin Coroutines & Flow for reactive UI updates.
* **Navigation:** Jetpack Navigation Compose.
* **Data Parsing:** HTML parsing for rich text display.

## Key Features

* **Offline-First:** Stories are cached locally; the app works perfectly without internet.
* **Smart State Management:** Uses MVI to handle UI states (Loading, Success, Error, Empty).
* **Dynamic Theming:** User-toggleable reading themes with custom Canvas graphics.
* **Reactive Favorites:** "Like" status updates instantly across screens using Database Observers.
* **Algorithm:** Custom calendar math to ensure a consistent, non-repeating daily story for every user globally.
