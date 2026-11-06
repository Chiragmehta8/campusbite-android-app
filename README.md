# CampusBite - Android Studio project

Offline college canteen ordering demo built with Kotlin and XML Views.

## Open the project
1. Extract the ZIP.
2. In Android Studio choose **Open** and select the `campusbite` folder (the folder containing `settings.gradle.kts`).
3. Allow Gradle sync to finish, then run the `app` configuration on an emulator or Android device.

## Features
- Food menu with categories: All, Snacks, Meals, Drinks, Desserts
- Search menu items by name
- Add items to cart and calculate total price
- Confirm and place a simulated order
- Save order history locally with SQLite
- No login, internet connection, payment gateway, or server required

## Main source files
- `app/src/main/java/com/example/campusbite/MainActivity.kt` - menu, search, cart, order confirmation and UI event handling
- `app/src/main/java/com/example/campusbite/OrderDatabaseHelper.kt` - SQLite database creation, insert and history query
- `app/src/main/res/layout/activity_main.xml` - screen layout

## Syllabus mapping
- Kotlin variables, lists, loops, conditions and functions
- Android Activity and `onCreate()`
- XML Views and layouts
- `findViewById()` and `setOnClickListener()` event handling
- Toast and AlertDialog
- SQLiteOpenHelper, SQLiteDatabase, ContentValues and Cursor

The app is a demonstration prototype. It does not connect to a real canteen or process payments.
