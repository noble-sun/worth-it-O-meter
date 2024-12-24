# WorthItOMeter

WorthItOMeter is a Wear OS app that helps you track the daily value of a product based on the date it was purchased and the price paid. This app is built with modern Android development practices and utilizes a rich set of tools and frameworks to ensure a smooth and responsive experience on Wear OS devices.

---

## Features

- **Daily Valuation**: Input the purchase date and price of a product, and the app calculates how much value the item holds each day since purchase.
- **Modern UI**: Built using Jetpack Compose for Wear OS, providing an elegant, intuitive, and responsive user interface.
- **Data Persistence**: Utilizes ProtoBuf and DataStore to securely and efficiently store user data.
- **Enhanced Wear OS Features**: Integrates Horologist to optimize for Wear OS capabilities, such as navigation, input, and system UI interactions.

---

## Tech Stack

- **Minimum API Level**: Android API Level 34
- **UI Framework**: Jetpack Compose for Wear OS
- **Persistence**: ProtoBuf and DataStore
- **Wear OS Utilities**: Horologist for Wear OS optimizations

---

## Build and Installation

### Prerequisites
1. Install **Android Studio** (Electric Eel or later).
2. Set up your Wear OS watch for debugging:
   - Enable **Developer Options**: Go to **Settings > About > Build number** on your watch and tap it 7 times.
   - Enable **ADB Debugging** in **Settings > Developer Options**.
   - Enable **Wireless Debugging** or **Debug over Wi-Fi**.
3. Ensure your computer and Wear OS watch are on the same Wi-Fi network.
4. Install `adb` (Android Debug Bridge) if not already available.

### Steps to Build
1. **Clone the Repository**:
   ```bash
   git clone git@github.com:noble-sun/worth-it-O-meter.git
   ```
2. **Open the Project**:
   - Open the project in Android Studio.
3. **Build the APK**:
   - Navigate to **Build > Build Bundle(s) / APK(s) > Build APK(s)** in Android Studio.
   - The APK file will be generated in the following directory:
     ```
     app/build/outputs/apk/debug/app-debug.apk
     ```

### Steps to Install
1. **Connect to the Watch**:
   - Pair your watch with your computer using ADB:
     ```bash
     adb pair <watch-ip-address>:<port>
     adb connect <watch-ip-address>:<port>
     ```
2. **Install the APK**:
   - Run the following command to install the app on your watch:
     ```bash
     adb install path/to/your-apk-file.apk
     ```
3. **Verify Installation**:
   - Once installed, the app will appear in the app drawer on your Wear OS watch.

---

## How It Works

1. Give the name of the product
2. Enter the amount paid for the product.
3. Input the date when the product was purchased.
4. The app calculates and displays the daily value of the product since the purchase date.
5. It will list all the products saved with their daily value and how many days its been since bought.

---

## TODO
Things that I'll maybe do in the future.*
- **Refactoring**: Review and refine codebase for better readability, maintainability, and performance.
- **App Tile**: Implement a Wear OS app tile for quick access to key app features.
- **Unit Tests**: Add comprehensive test coverage for core calculations and data handling.

---

## Acknowledgements

- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [DataStore](https://developer.android.com/topic/libraries/architecture/datastore)
- [Horologist](https://github.com/google/horologist)

---

This README was made primarily using Large Language Models because:
- LAZY

###### *go to the other .md file on this project.

