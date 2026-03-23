<h1 align="center">Elite Xeno Installer</h1>

<p align="center">
  <img src="https://readme-typing-svg.demolab.com?font=Orbitron&size=22&duration=2800&pause=900&color=32D4FF&center=true&vCenter=true&width=850&lines=Secure+Installer+for+Elite+Xeno;Modern+UI+%2B+Animated+Experience;Android+10%2B+Compatible" alt="Typing animation" />
</p>

<p align="center">
  <a href="https://drive.google.com/file/d/1X4xTg5ZTqhu-ddVlxui_AAogR2D2b6P0/view?usp=drivesdk">
    <img src="https://img.shields.io/badge/Download%20APK-Google%20Drive-32D4FF?style=for-the-badge&logo=android" alt="Download APK" />
  </a>
  <img src="https://img.shields.io/badge/Platform-Android%2010%2B-0A1327?style=for-the-badge" alt="Android 10+" />
  <img src="https://img.shields.io/badge/Status-Active%20Development-171F33?style=for-the-badge" alt="Status" />
</p>

---

## Overview
Elite Xeno Installer is a secure Android installer shell for the Elite Xeno payload flow with:
- Premium animated UI and full-screen responsive design
- Install progress tracking and user-confirmed package install flow
- Instagram and GitHub quick-access actions
- Android 10+ compatibility (`minSdk 29`)

---

## ✨ Highlights
- **Modern Presentation**: polished cards, hero glow, and animated interactions
- **Secure Install Pipeline**: PackageInstaller session flow + callback token checks
- **Play-Readiness Improvements**: cleartext disabled, backup/data extraction restricted
- **Cleaner Repository**: sensitive payload binary removed from source control

---

## 📥 Download
- **Latest APK**: https://drive.google.com/file/d/1X4xTg5ZTqhu-ddVlxui_AAogR2D2b6P0/view?usp=drivesdk

---

## 🧩 Project Structure

```text
assets/                    # payload-related assets (public repo excludes data.bin)
java/                      # Android module source + manifest + gradle config
res/                       # shared Android resources (layout/drawable/values/xml)
tools/                     # helper scripts (payload build utilities)
```

---

## 🔐 Security Notes
- `assets/data.bin` is intentionally removed from this public repository.
- Reason: payload binaries can be reverse engineered/decrypted when distributed publicly.
- See `assets/DATA_BIN_REMOVED.md` for policy note.
- See `SECURITY.md` and `docs/CERTIFICATES.md` for release hardening guidance.

---

## 🚀 Build (Debug)

```powershell
$env:JAVA_HOME="C:\Program Files\Android\Android Studio\jbr"
$env:Path="$env:JAVA_HOME\bin;" + $env:Path
& "C:\Users\Jeet\.gradle\wrapper\dists\gradle-8.9-bin\90cnw93cvbtalezasaz0blq0a\gradle-8.9\bin\gradle.bat" -p ".\java" assembleDebug
```

APK output:
`java/build/outputs/apk/debug/java-debug.apk`

---

## ⚠️ Play Store Note
Google Play approval also depends on Play Console declarations (especially for `REQUEST_INSTALL_PACKAGES`).
This repository includes in-app disclosure and technical hardening, but policy forms must also be completed accurately in Play Console.

---

## 👤 Author
**Jeet**
- GitHub: https://github.com/Jeet1511
