# Nova AI: God Mode Personal Assistant 🌌

Nova is a high-privilege, root-access personal AI assistant designed specifically for Android (tested on Samsung Galaxy A14). It combines deep system integration with a flexible Python-based "Brain" running in Termux to provide a "God Mode" experience.

## ✨ Features

- **🛡️ God Mode & Root Access**: Execute system-level commands (reboot, shutdown, battery stats) directly via voice.
- **🔓 Lockscreen Interaction**: Floating overlay and activity bypass allow you to talk to Nova without unlocking your phone.
- **🎙️ Background Wake Word**: Responds to "Nova" even when the screen is off or other apps are running.
- **🧠 Hybrid Intelligence**:
    - **Local Processing**: Immediate response for system tasks and local memory.
    - **Python Brain**: Offloads complex AI "thinking" to a local FastAPI server (Termux or PC).
- **🔋 Battery Optimized**: Adaptive listening cycles that adjust based on charging status and power save modes.
- **🚨 Emergency Kill Switch**: One-tap system-wide shutdown of all Nova services and listening modules.
- **🔐 Security First**: Critical actions (like rebooting) require a hardware-bound security code: `nova secure 2205`.

---

## 🚀 Installation & Setup

### 1. Android App (Frontend)
1. Clone this repository.
2. Open the project in **Android Studio**.
3. Build and install the APK on a **Rooted** Android device.
4. **Permissions required**:
    - Root Access (Grant via Magisk/Kitsune).
    - Record Audio (Microphone).
    - Display over other apps (Overlay).
    - Post Notifications.

### 2. Termux Brain (Backend)
To enable Nova's advanced "thinking" capabilities:
1. Install **Termux** from F-Droid.
2. Run the following commands:
   ```bash
   pkg update && pkg upgrade
   pkg install python
   pip install fastapi uvicorn
   ```
3. Copy `scripts/nova_brain.py` to your Termux home directory.
4. Start the brain:
   ```bash
   python nova_brain.py
   ```

---

## 🛠️ Usage

- **Wake Word**: Just say **"Nova"** to activate.
- **Voice Commands**:
    - *"Nova, what is my battery level?"*
    - *"Nova, my name is [Your Name]."* (Nova will remember this).
    - *"Nova, reboot device nova secure 2205."* (Root action).
- **Kill Switch**: Tap the red ⚠️ icon in the app top bar to instantly stop all background processes.

---

## 🏗️ Architecture

- **Android App**: MVVM, Hilt (DI), Room (Local Database), Retrofit (API), Jetpack Compose (UI).
- **Root Service**: Specialized service for handling `su` shell commands.
- **Voice System**: Android SpeechRecognizer (STT) and TextToSpeech (TTS).
- **Backend**: Python FastAPI running locally on `127.0.0.1:8000`.

---

## ⚠️ Disclaimer
Nova requires **Root Access**. Use with caution. The developers are not responsible for any damage to your device or data loss caused by misuse of root privileges.

---

**Developed for Nova AI Project** 🚀
