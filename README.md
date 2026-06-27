# Orion — Offline AI Assistant for Android

> 🇧🇷 [Versão em Português](README.pt-br.md)

Orion is an artificial intelligence assistant that runs **100% offline** on your Android device. Powered by the **Gemma** language model via **Google MediaPipe**, it enables conversations with a local AI without sending any data to external servers. Access is protected by **biometric authentication** (fingerprint or device PIN).

---

## Screenshots

> <p align="center">
  <img src="screenshots/1.jpeg" width="30%" alt="Tela Principal" />
  <img src="screenshots/2.jpeg" width="30%" alt="Segunda Tela" />
  <img src="screenshots/3.jpeg" width="30%" alt="Terceira Tela" />
</p>

---

## Features

- **100% offline AI** — the Gemma model runs directly on the device, no internet required
- **Persistent chat history** — all conversations are saved locally in SQLite and restored on next launch
- **Biometric authentication** — access protected by fingerprint, face recognition or device PIN
- **Bubble-style chat UI** — user messages on the right (blue) and AI responses on the left (green)
- **Responses in Brazilian Portuguese** — prompt engineered to force pt-BR output using Gemma's official turn tags
- **Async AI loading** — the model is initialized on a background thread to keep the UI responsive
- **Full privacy** — no data ever leaves the device

---

## Tech Stack

| Technology | Version | Description |
|---|---|---|
| **Java** | 11 | Main development language |
| **Android SDK** | API 35 (Android 15) | Target compile SDK |
| **Min SDK** | API 24 (Android 7.0) | Minimum supported version |
| **Google MediaPipe** | 0.10.29 | ML framework for running Gemma on-device |
| **Gemma (model.bin)** | — | Local language model bundled as an asset |
| **AndroidX Biometric** | 1.1.0 | Biometric authentication (fingerprint, face, PIN) |
| **AndroidX AppCompat** | 1.7.1 | Component compatibility across Android versions |
| **Material Design** | 1.12.0 | Google's visual components and design system |
| **ConstraintLayout** | 2.2.1 | Flexible view positioning layout system |
| **RecyclerView** | AndroidX | Efficient chat message list rendering |
| **SQLite** | Android Native | Local database for conversation history |
| **Gradle (Kotlin DSL)** | 8.8.0 (AGP) | Build system and dependency management |

---

## Architecture & Project Structure

```
Orion/
└── app/src/main/
    ├── java/com/example/project_orion/
    │   ├── LockActivity.java     # Biometric lock screen (app launcher entry point)
    │   ├── MainActivity.java     # Main chat screen, AI initialization and message sending
    │   ├── ChatAdapter.java      # RecyclerView adapter for rendering chat bubbles
    │   ├── ChatDbHelper.java     # SQLite data access layer for chat history
    │   └── Message.java          # Message entity model (text + sender flag)
    ├── assets/
    │   └── model.bin             # Compiled Gemma model for local inference
    └── res/
        ├── layout/
        │   ├── activity_main.xml # Chat layout (RecyclerView + input field + send button)
        │   └── item_message.xml  # Individual chat bubble layout
        ├── drawable/
        │   ├── bubble_user.xml        # Blue user bubble
        │   ├── bubble_ai.xml          # Green AI bubble
        │   ├── bg_chat_gradient.xml   # Chat background gradient
        │   ├── bg_input_field.xml     # Input field style
        │   └── bg_send_button_circle.xml # Send button style
        └── values/               # Colors, strings and themes
```

---

## How the AI Works

Orion uses the **MediaPipe Tasks GenAI** library to load and run the **Gemma** model directly in device memory.

**Initialization flow:**

```
App starts → LockActivity (biometrics) → MainActivity
    → model.bin copied from assets to internal storage
    → LlmInference initialized on background thread
    → "Orion Ready!" toast → Chat unlocked
```

**Prompt format (Gemma instruct):**

```
<start_of_turn>user
You are Orion, a helpful and intelligent assistant.
Answer the question below concisely and always in Brazilian Portuguese.

Question: {user message}
<end_of_turn>
<start_of_turn>model
```

---

## Database

The app uses **SQLite** to persist conversation history between sessions.

**Table `messages`**

| Column | Type | Description |
|---|---|---|
| `_id` | INTEGER PK | Unique identifier (autoincrement) |
| `text` | TEXT | Message content |
| `is_user` | INTEGER | `1` = user, `0` = AI |

---

## Biometric Authentication

The app uses `androidx.biometric.BiometricPrompt` to require authentication before granting access to the chat. Accepted methods:

- Fingerprint (Biometric Strong)
- Secure face recognition
- Device PIN / Password / Pattern (fallback)

If authentication fails or is cancelled, the app closes. On success, the user is sent to the chat and `LockActivity` is finished (preventing navigation back to it with the back button).

---

## Getting Started

### Prerequisites

- Android Studio (Hedgehog or later)
- JDK 11+
- Physical Android device running Android 7.0+ (API 24) — **recommended** due to AI model processing demands
- The `model.bin` file (compiled Gemma model) must be placed in `app/src/main/assets/`

> ⚠️ The `model.bin` file is large (~1–4 GB depending on the variant) and is typically not versioned in the repository. Download it separately and add it to the `assets/` folder.

### Steps

1. Clone or download the repository
2. Add `model.bin` to `app/src/main/assets/`
3. Open the project in **Android Studio**
4. Wait for Gradle sync to complete
5. Connect a physical device (emulators don't support real biometrics or GPU inference)
6. Click **Run ▶** or press `Shift + F10`

---

## Key Dependencies

```kotlin
// On-device AI with MediaPipe
implementation("com.google.mediapipe:tasks-genai:0.10.29")

// Biometric authentication
implementation("androidx.biometric:biometric:1.1.0")

// UI
implementation(libs.appcompat)          // 1.7.1
implementation(libs.material)           // 1.12.0
implementation(libs.constraintlayout)   // 2.2.1
```

---

## Future Improvements

- [ ] Streaming response support (real-time token output)
- [ ] Multiple AI model selection
- [ ] Clear chat history option
- [ ] Multiple chat sessions
- [ ] History export
- [ ] Dedicated dark mode
- [ ] Voice input support

---

## ⚠️ Notes

- The Gemma model consumes significant RAM and GPU resources. Devices with at least **6 GB of RAM** are recommended.
- The first launch may be slow as `model.bin` is copied from assets to the app's internal storage.
- The app does not collect or transmit any data — all inference is local.

---

## Author

Built as an Android portfolio project focused on on-device AI and privacy.

---

## License

This project is licensed under the MIT License.
