# 📱 Fulcrum Kiosk
"Stay on target." — *Gold Five*
<p align="center">
  <img src="https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" />
  <img src="https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" />
  <img src="https://img.shields.io/badge/Jetpack_Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white" />
  <img src="https://img.shields.io/badge/Material_3-757575?style=for-the-badge&logo=materialdesign&logoColor=white" />
</p>

**Fulcrum Kiosk** is an enterprise-grade interactive terminal solution for Android. It allows you to lock down any device into a secure interface, serve high-performance Web Apps (PWAs), and manage your fleet in real-time via the NATS protocol.

---

## 🚀 Key Features

- **🔒 Enterprise Lockdown (Lock Task Mode)**: Complete device restriction to prevent users from exiting the app or accessing system settings.
- **🎨 Modern Material 3 UI**: Adaptive administration interface (Tablet/Mobile) featuring dynamic color schemes and fluid transitions.
- **☁️ Remote Control via NATS**: Real-time messaging integration for remote monitoring, command execution, and heartbeat tracking.
- **🌐 PWA Optimization**: Dedicated web engine isolation for business-critical web applications.
- **🛡️ Vault Security**: Secure secret management (NATS credentials, Tailscale keys) using the Android Keystore system.
- **📡 Advanced Connectivity**: Integrated Wi-Fi manager and optional.

---

## 🛠 Tech Stack

| Category | Technology |
| :--- | :--- |
| **Language** | Kotlin 1.9+ |
| **UI Framework** | Jetpack Compose (Material 3) |
| **Architecture** | MVVM + Clean Architecture |
| **Messaging** | NATS Client for Android |
| **Build System** | Gradle Kotlin DSL (.kts) with Version Catalogs |

---

## 🛡️ Security First

Safety is a core pillar of Fulcrum Kiosk:
- **Zero-Plaintext Policy**: Sensitive data is encrypted via `VaultManager` and stored in the Android Keystore.

---

## 🤝 How to Contribute

Contributions make the open-source community an amazing place!
1. **Fork** the project.
2. Create your Feature Branch: `git checkout -b feature/AmazingFeature`.
3. Commit your changes: `git commit -m 'Add some AmazingFeature'`.
4. Push to the Branch: `git push origin feature/AmazingFeature`.
5. Open a **Pull Request**.

---

## 🐛 Issues & Feature Requests

Found a bug or have a brilliant idea?
- Report bugs at the [GitHub Issue Tracker](https://github.com/wared2003/fulcrum-kiosk/issues).
- Propose new features by opening a "Feature Request" ticket.

---

## 👥 Contributors

- **Wared2003** ([@wared2003](https://github.com/wared2003)) - *Lead Developer & Architect*

---

## 📄 License

This project is licensed under the AGPL V3 becose  *This is the way.*

*Built with ❤️ in a galaxy far, far away.*
