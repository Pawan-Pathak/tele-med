# TeleMed Demo — Telemedicine for Rural Healthcare

A demo telemedicine Android app for field health workers (ASHA workers) enabling remote doctor consultations. Built with **Kotlin**, **Jetpack Compose**, and **Material 3**.

## Features

### Three User Roles
- **Health Worker** — Register patients, collect vitals, upload documents
- **Pharmacist** — Manage consent, facilitate video calls, dispense medicines
- **Doctor** — Consult patients, diagnose, prescribe treatment, generate prescriptions

### Role Selection (Entry Point)
Three role cards on launch, each navigating to role-specific flows with distinct visual identity.

### Module I — Health Worker
- **Session Setup**: Spoke name, district/village selection (cascading dropdowns), auto date/time
- **Dashboard**: New patient registration CTA, recent patients list with status badges, sync indicator
- **Multi-step Registration** (6 steps with progress indicator):
  1. Personal Info — Name, guardian, gender, age, DOB, mobile, Aadhaar
  2. Address — State, district, village (cascading dropdowns)
  3. Vitals — Weight, temperature (°F/°C toggle), BP, blood sugar, hemoglobin, SpO2, pulse rate
  4. Medical History — Primary complaint, symptoms (multi-select chips), conditions, lifestyle toggles
  5. Documents — Upload reports (PDF/image), upload diagnostics (camera/gallery)
  6. Review & Submit — Summary card, submit → auto-generated unique Patient ID (`ASHA-YYYYMMDD-XXXX`)
- **Patient Detail**: View full patient info after registration

### Module II — Pharmacist
- **Login**: Mock credential screen with role-specific branding
- **Patient Queue**: List of patients with status badges (Waiting/In Progress/Done)
- **Consent Screen**: Patient details + large YES/NO buttons
- **Video Call** (stub): Animated connecting state, mock video UI, mute/end controls
- **Prescription Dispensing**: Medication list with checkboxes to mark as dispensed

### Module III — Doctor
- **Login**: Mock credential screen with role-specific branding
- **Patient Queue**: Patient cards with vitals summary, chief complaint, Answer/Decline buttons
- **Incoming Call**: Full-screen animated overlay with Accept/Decline buttons
- **Consultation Screen** (tabbed):
  - Patient Info tab — Pre-filled vitals, complaint, history, documents
  - Rx Form tab — Sections A-F:
    - Chief complaints, diagnosis, ICD-10 code
    - Treatment plan with dynamic medicine rows (name, dosage, frequency toggles, duration)
    - Lab tests (multi-select chips)
    - Imaging notes, procedures, allergies, recommendations
    - Referral toggle with specialty/reason
- **Prescription Preview**: Auto-populated with doctor/patient details, Rx content, digital signature placeholder, share option

## Tech Stack
- **Language**: Kotlin
- **UI**: Jetpack Compose (Material 3)
- **Architecture**: MVVM + Clean Architecture
- **Navigation**: Compose Navigation with role-based nav graphs
- **State Management**: StateFlow + ViewModel
- **DI**: Manual container (Hilt-ready structure)
- **Design**: Teal/green health-oriented palette, large touch targets, high contrast

## Mock Data
- **7 pre-seeded patients** with full vitals and medical history
- **3 mock doctors** with qualifications and locations
- **30 common generic medicines** for autocomplete
- **20 ICD-10 diagnoses** for search
- **10 common lab tests**
- Video call: Placeholder UI with connected state, mute/end buttons

## Package Structure

```text
com.telemed.demo
├── data
│   ├── local/dummy          # In-memory data store with mock data
│   └── repository            # Repository implementations
├── di                         # DI container and ViewModel factory
├── domain
│   ├── model                  # Data models (Patient, Doctor, Prescription, etc.)
│   ├── repository             # Repository interfaces
│   └── usecase                # Use case classes
├── feature
│   ├── roleselection          # Role selection entry screen
│   ├── healthworker           # HW session, dashboard, 6-step registration
│   ├── pharmacist             # Login, queue, consent, video call, dispensing
│   └── doctor                 # Login, queue, incoming call, consultation, prescription
├── navigation                 # Nav destinations and NavHost
├── ui
│   ├── components             # Reusable composables (cards, buttons, chips, etc.)
│   ├── responsive             # Responsive layout and top bar
│   └── theme                  # Material 3 theme, colors, typography
├── MainActivity.kt
└── TeleMedApp.kt
```

## How to Build & Run

1. Open this folder in **Android Studio** (Hedgehog or newer recommended)
2. Let Gradle sync and install missing SDKs if prompted
3. Run the `app` configuration on an emulator or device (API 24+)
4. Select a role on the launch screen to explore the app

## Design Principles
- **Large touch targets** and high contrast text for low-tech field users
- **Multilingual-ready**: All UI text in `strings.xml` (no hardcoded text)
- **Offline-first ready**: In-memory store can be replaced with Room DB
- **Card-based layouts** with minimal cognitive load
- **Role-specific visual identity** (teal for HW, indigo for pharmacist, blue for doctor)

## Notes
- Data is kept in-memory for demo purposes only
- No backend integration; repositories are async-simulated fakes
- Upload/download and PDF generation are mock placeholders
- Replace `InMemory*Repository` classes with real implementations without changing UI
