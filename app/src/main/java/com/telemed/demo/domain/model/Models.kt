package com.telemed.demo.domain.model

enum class UserRole {
    HEALTH_WORKER,
    PHARMACIST,
    DOCTOR
}

data class SessionUser(
    val email: String,
    val role: UserRole,
    val displayName: String
)

data class SpokeLocation(
    val spokeName: String,
    val district: String,
    val village: String,
    val localDateTimeIso: String
)

data class PatientRegistrationData(
    val uniqueId: String,
    val fullName: String,
    val spouseOrFatherName: String,
    val gender: String,
    val age: Int,
    val dob: String,
    val village: String,
    val district: String,
    val state: String,
    val mobileNumber: String,
    val aadhaarNumber: String
)

data class LifestyleHistory(
    val alcohol: Boolean,
    val tobacco: Boolean,
    val drugs: Boolean
)

data class BasicVitalsData(
    val weightKg: Double,
    val temperatureC: Double,
    val bloodPressure: String,
    val bloodSugar: Double,
    val hemoglobin: Double,
    val otherVitals: String,
    val primaryComplaint: String,
    val basicSymptoms: String,
    val medicalHistory: String,
    val lifestyleHistory: LifestyleHistory
)

data class SharedPatientProfile(
    val registration: PatientRegistrationData,
    val location: SpokeLocation?,
    val vitals: BasicVitalsData?,
    val uploadedReports: List<String>,
    val uploadedDiagnostics: List<String>
)

data class MedicationItem(
    val name: String,
    val dosage: String,
    val frequency: String,
    val time: String
)

data class DoctorConsultationForm(
    val chiefComplaints: String,
    val diagnosis: String,
    val treatmentPlan: List<MedicationItem>,
    val labTestAdvice: String,
    val diagnosisImaging: String,
    val procedureAdvice: String,
    val allergies: String,
    val recommendations: String,
    val referrals: String
)

data class DoctorCaseSnapshot(
    val patientProfile: SharedPatientProfile?,
    val doctorDecision: String,
    val consultationForm: DoctorConsultationForm?,
    val generatedPdfName: String?
)

data class PharmacistSnapshot(
    val patientProfile: SharedPatientProfile?,
    val consentGiven: Boolean?,
    val callInitiated: Boolean,
    val prescribedMedications: List<MedicationItem>,
    val dispensationNotes: String
)

data class Patient(
    val id: String,
    val fullName: String,
    val age: Int,
    val gender: String,
    val phone: String
)

data class Vitals(
    val heartRate: Int,
    val bloodPressure: String,
    val temperatureC: Double,
    val spo2: Int
)

data class Doctor(
    val id: String,
    val name: String,
    val specialty: String,
    val etaMinutes: Int,
    val isAvailable: Boolean
)

data class Prescription(
    val id: String,
    val patientName: String,
    val doctorName: String,
    val medications: List<String>,
    val notes: String
)

data class DashboardData(
    val patientName: String,
    val lastVitals: Vitals?,
    val connectedDoctor: Doctor?,
    val hasActiveCall: Boolean,
    val latestPrescription: Prescription?
)

