<div align="center">

<img src="https://capsule-render.vercel.app/api?type=rect&color=121922&height=180&section=header&text=ClooForward&fontSize=50&fontColor=E34B56&fontAlignY=40&desc=Automated%20SMS%20to%20Telegram%20forwarding&descAlignY=65&descSize=16&animation=fadeIn" alt="ClooForward header" width="100%"/>

<img src="https://readme-typing-svg.demolab.com?font=Hanken+Grotesk&weight=400&size=20&duration=3000&pause=800&color=E34B56&center=true&vCenter=true&width=600&lines=Listen+%E2%80%A2+Filter+%E2%80%A2+Forward;Instantly+route+SMS+to+Telegram;Minimalist+Android+Utility" alt="Animated tagline"/>

<br/>

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.22-121922?style=for-the-badge&logo=kotlin&logoColor=E34B56&labelColor=000000)](https://kotlinlang.org/)
[![Android](https://img.shields.io/badge/Android-SDK_34-121922?style=for-the-badge&logo=android&logoColor=E34B56&labelColor=000000)](https://developer.android.com/)
[![Jetpack Compose](https://img.shields.io/badge/Compose-Modern_UI-121922?style=for-the-badge&logo=jetpackcompose&logoColor=E34B56&labelColor=000000)](https://developer.android.com/jetpack/compose)

</div>

<br/>

> **ClooForward** is a minimalist Android utility app built to listen for incoming SMS messages and instantly forward specific ones to a Telegram Bot via a Webhook URL. It runs reliably in the background, ensuring you never miss important alerts like bank transactions or OTPs when away from your primary device.

<br/>

## ✦ The Problem

Many users carry multiple phones or leave their primary SIM device at home, causing them to miss:
- Important bank transaction alerts.
- OTPs for logins and verifications.
- Urgent messages from specific contacts.
- Service notifications.

**ClooForward** solves this by acting as a silent bridge between your SMS inbox and your Telegram account.

<br/>

## ✦ The Solution

ClooForward allows you to define specific keywords or sender IDs (e.g., "CANARA", "SBI", "Credited"). When an SMS arrives, the app checks if it matches your criteria. If it does, the app instantly pushes the message payload to your private Telegram chat using a Telegram Bot.

<br/>

## ✦ Core Features

### Smart SMS Filtering
- Filter by **Sender ID** (e.g., `SBI`, `ICICI`).
- Filter by **Message Body Keywords** (e.g., `Credited`, `UPI`, `Received`).
- Comma-separated configuration for multiple rules.

### Telegram Webhook Integration
- Direct HTTP POST to the Telegram Bot API.
- Secure and instant delivery.
- Formatted alerts: `Bank Alert from: [Sender ID] \n\n [Message Body]`.

### Background Execution
- Utilizes Android's `BroadcastReceiver`.
- Triggers immediately upon SMS arrival.
- Works reliably even when the app is closed.

### Modern UI
- Clean, minimalist dashboard built with **Jetpack Compose**.
- Automatic **Dark/Light Mode** system adaptation.
- One-tap "Start/Stop Service" toggle.

<br/>

## ✦ How It Works

1. User configures Sender IDs, Bot Token, and Chat ID in the app.
2. User taps **Start Forwarding**.
3. An SMS arrives on the device.
4. The `SmsReceiver` wakes up in the background.
5. The app checks if the sender or message contains any of the configured keywords.
6. If a match is found, an asynchronous HTTP POST request is sent via OkHttp.
7. The message instantly appears in the configured Telegram chat.

<br/>

## ✦ Tech Stack

**Frontend / UI**
- Kotlin
- Jetpack Compose
- Material Design 3

**Backend / Networking**
- OkHttp 4 (for Webhook HTTP POST requests)
- Kotlin Coroutines (for non-blocking background network calls)

**Android Components**
- `BroadcastReceiver` (for SMS listening)
- `SharedPreferences` (for local configuration storage)

<br/>

## ✦ Design Principles

- **Minimalist Dashboard:** Only the essential input fields and a single CTA.
- **System-Aware Theme:** Beautiful dark and light modes that respect system settings.
- **Privacy First:** No external servers. Data goes directly from your phone to Telegram's API.

<br/>

## ✦ Setup & Development

### Prerequisites
- **Android Studio** (or command-line Android SDK tools)
- **Java 17**
- A **Telegram Bot Token** (Create one via [@BotFather](https://t.me/botfather) on Telegram)
- Your **Telegram Chat ID** (Use bots like [@userinfobot](https://t.me/userinfobot) to find your ID)

### Run Locally (Terminal)

**1. Clone the repository:**

```bash
git clone https://github.com/yourusername/ClooForward.git
cd ClooForward
```

**2. Build the Debug APK:**

```bash
./gradlew assembleDebug
```

**3. Install on a connected device:**

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

**4. Launch the app:**

```bash
adb shell monkey -p com.cloowork.clooforward -c android.intent.category.LAUNCHER 1
```

<br/>

<div align="center">

<img src="https://capsule-render.vercel.app/api?type=rect&color=121922&height=60&section=footer&animation=fadeIn" alt="Footer" width="100%"/>

**ClooWork** • Building seamless automation utilities.

</div>
