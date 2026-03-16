# TeleMed Demo (Jetpack Compose)

A role-based telemedicine demo app using **MVVM + Clean Architecture** with Jetpack Compose.

## Included

- Single-activity Compose app (`MainActivity`) with app-level container (`TeleMedApp`)
- Clean layers: `domain`, `data`, `feature`, `navigation`, `ui`, `di`
- Dummy in-memory repositories and seeded doctor data
- ViewModel + UiState flow for each screen
- Responsive composables with adaptive horizontal padding
- Role-based navigation and modules:
  - Login with role selector (Health Worker / Pharmacist / Doctor)
  - Dashboard (role-aware module launcher)
  - Health Worker Module
  - Pharmacist Module
  - Doctor Module
  - Legacy demo screens are still available from Dashboard

## Scope Coverage

### I. Health Worker Module

- Pre-consultation: spoke, district/village mapped list, auto local date/time
- New patient registration:
  - full name, spouse/father name, gender, age, DOB
  - address (village, district, state)
  - mobile, Aadhaar
- Basic vitals and history:
  - weight, temperature, BP, blood sugar, hemoglobin, other vitals
  - primary complaint, symptoms, medical history
  - lifestyle history (alcohol/tobacco/drugs)
- Additional actions:
  - upload/download reports (dummy)
  - upload diagnostics/X-ray (dummy)
  - unique ID auto-generation on registration
  - share with doctor + pharmacist

### II. Pharmacist Module

- Consent screen (YES/NO)
- Initiate video/audio call flow on consent YES
- View prescribed medications
- Record medication dispensation notes

### III. Doctor Module

- Location-based patient profile summary
- Attend/Decline call options
- Access pre-filled patient info
- Consultation form includes:
  - chief complaints, diagnosis
  - treatment plan (medicine + dosage + frequency + time)
  - lab test advice
  - diagnosis imaging, procedure, allergies
  - recommendations, referrals
- Prescription PDF generation placeholder (dummy filename)

## Package Structure

```text
com.telemed.demo
|- data
|  |- local/dummy
|  |- repository
|- di
|- domain
|  |- model
|  |- repository
|  |- usecase
|- feature
|  |- dashboard
|  |- doctor
|  |- doctorpool
|  |- healthworker
|  |- login
|  |- pharmacist
|  |- prescription
|  |- registration
|  |- videocall
|  |- vitals
|- navigation
|- ui
|  |- components
|  |- responsive
|  |- theme
|- MainActivity.kt
|- TeleMedApp.kt
```

## How to Run

1. Open this folder in Android Studio.
2. Let Gradle sync and install missing SDKs if prompted.
3. Run the `app` configuration on an emulator/device.

## Notes

- Data is kept in-memory for demo purposes only.
- No backend integration yet; repositories are intentionally fake and async-simulated.
- Upload/download and PDF generation are scaffold placeholders in in-memory logic.
- You can replace `InMemory*Repository` classes with API + database implementations later without changing the UI contract.
