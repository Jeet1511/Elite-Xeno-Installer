<p align="center">
  <img src="https://capsule-render.vercel.app/api?type=waving&height=230&color=0:020617,20:071a2f,45:0a2a46,75:0f4d74,100:22d3ee&text=ELITE%20XENO%20INSTALLER&fontColor=e2f3ff&fontSize=44&fontAlignY=38&desc=Secure%20Android%20Installer%20Experience&descAlignY=58" alt="Elite Xeno Installer Banner" />
</p>

<p align="center">
  <img src="https://readme-typing-svg.demolab.com?font=Orbitron&weight=700&size=24&duration=2400&pause=800&color=32D4FF&center=true&vCenter=true&width=980&lines=Modern+Animated+Installer+UI;Secure+Install+Pipeline+with+Policy+Hardening;Android+10%2B+Ready" alt="Typing animation" />
</p>

<p align="center">
  <a href="https://drive.google.com/file/d/1X4xTg5ZTqhu-ddVlxui_AAogR2D2b6P0/view?usp=drivesdk">
    <img src="https://img.shields.io/badge/%F0%9F%93%A5%20Download%20APK-Google%20Drive-17B6D8?style=for-the-badge" alt="Download APK" />
  </a>
  <a href="https://github.com/Jeet1511/Elite-Xeno-Installer/stargazers">
    <img src="https://img.shields.io/github/stars/Jeet1511/Elite-Xeno-Installer?style=for-the-badge&color=0ea5e9" alt="Stars" />
  </a>
  <a href="LICENSE">
    <img src="https://img.shields.io/badge/License-Apache--2.0-0b1220?style=for-the-badge" alt="License" />
  </a>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Android-10%2B-0f172a?style=flat-square&logo=android&logoColor=3ddc84" alt="Android 10+" />
  <img src="https://img.shields.io/badge/Target%20SDK-34-0f172a?style=flat-square" alt="Target SDK 34" />
  <img src="https://img.shields.io/badge/UI-Animated%20%26%20Premium-0f172a?style=flat-square" alt="Animated UI" />
  <img src="https://img.shields.io/badge/Security-Hardened-0f172a?style=flat-square" alt="Security Hardened" />
</p>

---

## ⚡ Live Highlights

<p align="center">
  <img src="https://github-readme-streak-stats.herokuapp.com?user=Jeet1511&theme=transparent&hide_border=true&stroke=0EA5E9&ring=22D3EE&fire=38BDF8&currStreakLabel=E2F3FF" alt="GitHub streak" />
</p>

- ✨ Premium full-screen UI with hero glow, animated cards, and polished interactions
- 🔐 Install callback token validation to prevent spoofed broadcast handling
- 🛡️ Cleartext network disabled + backup/data extraction restrictions enabled
- 📲 Android 10+ support with responsive layout across device sizes
- 🔗 Direct social action buttons (Instagram + GitHub)

---

## 🚀 Quick Download

<p align="center">
  <a href="https://drive.google.com/file/d/1X4xTg5ZTqhu-ddVlxui_AAogR2D2b6P0/view?usp=drivesdk">
    <img src="https://img.shields.io/badge/GET%20LATEST%20APK-Click%20to%20Download-22D3EE?style=for-the-badge" alt="Get latest APK" />
  </a>
</p>

---

## 🧠 Why this repo exists
Elite Xeno Installer is the installer layer for secure payload deployment with a strong focus on UX + policy-aware engineering.

- Maintains a modern visual identity with smooth motion and high-contrast readability
- Preserves explicit user-consent installation via Android system screens
- Improves real-world release hygiene for public repository safety

---

## 🗂️ Repository Layout

```text
assets/      payload-related public-safe assets (data.bin excluded)
java/        app module source, manifest, gradle setup
res/         shared Android resources (layout, drawable, values, xml)
tools/       build helper scripts
docs/        certificate and release guidance
```

---

## 🔒 Security & Compliance

- Sensitive binary `assets/data.bin` is intentionally removed from public source.
- Reason note is maintained in `assets/DATA_BIN_REMOVED.md`.
- Security policy is documented in `SECURITY.md`.
- Certificate/signing guidance is documented in `docs/CERTIFICATES.md`.

> Play Store acceptance still depends on correct Play Console declarations and permitted-use justification for `REQUEST_INSTALL_PACKAGES`.

---

## 🛠️ Build (Debug)

```powershell
$env:JAVA_HOME="C:\Program Files\Android\Android Studio\jbr"
$env:Path="$env:JAVA_HOME\bin;" + $env:Path
& "C:\Users\Jeet\.gradle\wrapper\dists\gradle-8.9-bin\90cnw93cvbtalezasaz0blq0a\gradle-8.9\bin\gradle.bat" -p ".\java" assembleDebug
```

Output:
`java/build/outputs/apk/debug/java-debug.apk`

---

## 📄 License
Licensed under **Apache License 2.0**. See `LICENSE`.

---

## 👨‍💻 Maintainer
**Jeet**

- GitHub: https://github.com/Jeet1511
- Repository: https://github.com/Jeet1511/Elite-Xeno-Installer
