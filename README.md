# Leben in Deutschland - Exam Preparation Flashcards

A professional Android application developed with **Kotlin** and **Jetpack Compose** to help users prepare for the "Leben in Deutschland" (Naturalization) test.

## 📱 Features
- **300+ Questions:** Comprehensive coverage of all general test questions.
- **Interactive Flashcards:** Smooth 3D-flip animations for a modern learning experience.
- **Smart Statistics:** Track correct and incorrect answers with anti-cheat logic (stats don't increase if you listen to the answer first).
- **Text-to-Speech (TTS):** Integrated audio support for both questions and answers in German.
- **Search & Navigation:** Jump to specific questions or navigate sequentially.
- **Modern UI:** Clean, responsive design with dark/light mode support and high-quality visuals.

## 🛠 Tech Stack
- **Language:** [Kotlin](https://kotlinlang.org/)
- **UI Framework:** [Jetpack Compose](https://developer.android.com/jetpack/compose)
- **Architecture:** MVVM (Model-View-ViewModel)
- **Asynchronous Work:** Coroutines & Flow
- **Image Loading:** [Coil](https://coil-kt.github.io/coil/)
- **Data Source:** JSON-based local asset management

## 🏛 Clean Architecture
The project follows clean coding principles with a structured package organization:
- `ui/screens`: Isolated screen compositions (Start, Flashcard).
- `ui/components`: Reusable UI elements (Flashcards, Nav bars).
- `viewmodel`: Business logic and state management.
- `data`: Data models and asset handlers.

## 👨‍💻 Developed By
**Ferdi Durmaz**  
*Tescoferdi7777@gmail.com*

---
*Disclaimer: This app is a private educational project and is completely free and ad-free.*
