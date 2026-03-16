package com.telemed.demo.domain.model

enum class UserRole {
    HEALTH_WORKER,
    PHARMACIST,
    DOCTOR
}

enum class Gender {
    MALE, FEMALE, OTHER
}

enum class ConsultationStatus {
    REGISTERED, WAITING, IN_PROGRESS, COMPLETED, DECLINED
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

data class Address(
    val village: String,
    val district: String,
    val state: String
)

data class Vitals(
    val weight: Float? = null,
    val temperature: Float? = null,
    val temperatureUnit: String = "F",
    val bpSystolic: Int? = null,
    val bpDiastolic: Int? = null,
    val bloodSugar: Float? = null,
    val hemoglobin: Float? = null,
    val spo2: Int? = null,
    val pulseRate: Int? = null
)

data class LifestyleHistory(
    val alcohol: Boolean = false,
    val tobacco: Boolean = false,
    val drugs: Boolean = false
)

data class MedicalHistory(
    val primaryComplaint: String = "",
    val symptoms: List<String> = emptyList(),
    val knownConditions: List<String> = emptyList(),
    val lifestyle: LifestyleHistory = LifestyleHistory()
)

data class DocumentFile(
    val name: String,
    val type: String, // "report" or "diagnostic"
    val uri: String = ""
)

data class Patient(
    val id: String,
    val fullName: String,
    val guardianName: String = "",
    val gender: Gender = Gender.MALE,
    val age: Int = 0,
    val dob: String = "",
    val mobile: String = "",
    val aadhaar: String = "",
    val address: Address = Address("", "", ""),
    val vitals: Vitals = Vitals(),
    val medicalHistory: MedicalHistory = MedicalHistory(),
    val documents: List<DocumentFile> = emptyList(),
    val registeredBy: String = "",
    val registeredAt: String = "",
    val status: ConsultationStatus = ConsultationStatus.REGISTERED
)

data class Doctor(
    val id: String,
    val name: String,
    val qualification: String = "",
    val specialty: String = "",
    val clinicName: String = "",
    val regNumber: String = "",
    val etaMinutes: Int = 0,
    val isAvailable: Boolean = true,
    val district: String = ""
)

data class MedicineFrequency(
    val morning: Boolean = false,
    val afternoon: Boolean = false,
    val night: Boolean = false
) {
    override fun toString(): String {
        val parts = mutableListOf<String>()
        if (morning) parts.add("Morning")
        if (afternoon) parts.add("Afternoon")
        if (night) parts.add("Night")
        return parts.joinToString(", ").ifEmpty { "As needed" }
    }
}

data class Medicine(
    val name: String = "",
    val dosage: String = "",
    val frequency: MedicineFrequency = MedicineFrequency(),
    val durationDays: Int = 0,
    val instructions: String = ""
)

data class Referral(
    val needed: Boolean = false,
    val specialty: String = "",
    val reason: String = ""
)

data class Prescription(
    val id: String = "",
    val patientId: String = "",
    val patientName: String = "",
    val patientAge: Int = 0,
    val patientGender: String = "",
    val doctorName: String = "",
    val doctorQualification: String = "",
    val clinicName: String = "",
    val regNumber: String = "",
    val date: String = "",
    val chiefComplaints: String = "",
    val diagnosis: String = "",
    val medicines: List<Medicine> = emptyList(),
    val labTests: List<String> = emptyList(),
    val referral: Referral? = null,
    val recommendations: String = "",
    val allergies: String = "",
    val procedures: String = "",
    val imagingNotes: String = ""
)

data class PatientQueueItem(
    val patient: Patient,
    val time: String,
    val status: ConsultationStatus
)

data class DashboardData(
    val recentPatients: List<Patient> = emptyList(),
    val totalRegisteredToday: Int = 0,
    val syncStatus: String = "Synced"
)

data class DoctorConsultationForm(
    val chiefComplaints: String = "",
    val diagnosis: String = "",
    val icdCode: String = "",
    val medicines: List<Medicine> = emptyList(),
    val labTests: List<String> = emptyList(),
    val imagingNotes: String = "",
    val procedures: String = "",
    val allergies: String = "",
    val recommendations: String = "",
    val referral: Referral? = null
)

// ICD-10 diagnosis item for search
data class ICD10Item(
    val code: String,
    val description: String
)

// Drug for autocomplete
data class DrugItem(
    val name: String,
    val commonDosages: List<String> = emptyList()
)
