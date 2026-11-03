# Femlife

A health and wellness Android app for women and expectant mothers. Built for a client as a freelance project.

## Features

- Sign in with Google and user profiles
- Menstrual cycle tracking with symptoms and next period prediction
- Week-by-week pregnancy guide by trimester
- Post-pregnancy care guides
- FemTalk community feed with posts, likes and comments
- Group chat
- Articles and workshops
- Product catalog, with orders sent to the admin over WhatsApp
- Admin tools to manage articles, products and workshops
- Alarms and notifications

## Tech stack

- Kotlin and XML layouts
- ViewModel, LiveData and Coroutines
- Firebase Auth and Firestore
- Supabase Storage for images
- Room for cycles, alarms and notifications
- Navigation Component and ViewPager2
- Glide

## Getting started

1. Open the project in Android Studio.
2. Let Gradle sync.
3. Run the `app` configuration on a device or emulator (Android 9, API 28, or newer).

Sign-in, Firestore and storage depend on the project's own Firebase and Supabase setup. To use your own, replace `app/google-services.json` and the values in `config/ApiConfig.kt`.

## Project structure

```
app/src/main/java/com/example/femlife/
  config/          Firestore and Supabase clients
  data/            Models and Room databases
  repository/      Articles, chat, posts, products and workshops
  ui/activities/   Tracker, pregnancy, articles, products, alarms, profile
  ui/fragments/    Home, FemTalk and chat
  utils/           Images and notifications
```
