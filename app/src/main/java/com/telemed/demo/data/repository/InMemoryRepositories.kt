package com.telemed.demo.data.repository

import com.telemed.demo.data.local.dummy.TeleMedMemoryStore
import com.telemed.demo.domain.model.BasicVitalsData
import com.telemed.demo.domain.model.DashboardData
import com.telemed.demo.domain.model.DoctorCaseSnapshot
import com.telemed.demo.domain.model.Doctor
import com.telemed.demo.domain.model.DoctorConsultationForm
import com.telemed.demo.domain.model.MedicationItem
import com.telemed.demo.domain.model.Patient
import com.telemed.demo.domain.model.PatientRegistrationData
import com.telemed.demo.domain.model.PharmacistSnapshot
import com.telemed.demo.domain.model.Prescription
import com.telemed.demo.domain.model.SessionUser
import com.telemed.demo.domain.model.SharedPatientProfile
import com.telemed.demo.domain.model.SpokeLocation
import com.telemed.demo.domain.model.UserRole
import com.telemed.demo.domain.model.Vitals
import com.telemed.demo.domain.repository.AuthRepository
import com.telemed.demo.domain.repository.ConsultationRepository
import com.telemed.demo.domain.repository.DashboardRepository
import com.telemed.demo.domain.repository.DoctorRepository
import com.telemed.demo.domain.repository.PatientRepository
import com.telemed.demo.domain.repository.PrescriptionRepository
import com.telemed.demo.domain.repository.VitalsRepository
import com.telemed.demo.domain.repository.WorkflowRepository
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

class InMemoryAuthRepository : AuthRepository {
    private var currentSession: SessionUser? = null

    override suspend fun login(email: String, password: String, role: UserRole): Result<SessionUser> {
        delay(600)
        return if (email.contains("@") && password.length >= 4) {
            val session = SessionUser(
                email = email,
                role = role,
                displayName = email.substringBefore("@").replaceFirstChar { it.uppercase() }
            )
            currentSession = session
            Result.success(session)
        } else {
            Result.failure(IllegalArgumentException("Enter valid demo credentials."))
        }
    }

    override suspend fun getCurrentSession(): SessionUser? = currentSession

    override suspend fun logout() {
        currentSession = null
    }
}

class InMemoryWorkflowRepository(
    private val store: TeleMedMemoryStore
) : WorkflowRepository {
    override suspend fun saveSpokeLocation(location: SpokeLocation) {
        delay(100)
        store.spokeLocation = location
    }

    override suspend fun getMappedDistricts(): List<String> = store.mappedDistricts

    override suspend fun getMappedVillages(district: String): List<String> =
        store.mappedVillagesByDistrict[district].orEmpty()

    override suspend fun registerPatient(data: PatientRegistrationData): PatientRegistrationData {
        delay(250)
        val assignedId = data.copy(
            uniqueId = if (data.uniqueId.isBlank()) "TM-${System.currentTimeMillis()}" else data.uniqueId
        )
        store.latestRegistration = assignedId
        store.currentPatient = Patient(
            id = assignedId.uniqueId,
            fullName = assignedId.fullName,
            age = assignedId.age,
            gender = assignedId.gender,
            phone = assignedId.mobileNumber
        )
        return assignedId
    }

    override suspend fun saveBasicVitals(data: BasicVitalsData) {
        delay(200)
        store.latestBasicVitals = data
        store.latestVitals = Vitals(
            heartRate = 76,
            bloodPressure = data.bloodPressure,
            temperatureC = data.temperatureC,
            spo2 = 98
        )
    }

    override suspend fun uploadReport(fileName: String) {
        delay(120)
        store.uploadedReports.add(fileName)
    }

    override suspend fun downloadReport(fileName: String): String {
        delay(120)
        return if (store.uploadedReports.contains(fileName)) {
            "Downloaded $fileName"
        } else {
            "Report $fileName not found"
        }
    }

    override suspend fun uploadDiagnostic(fileName: String) {
        delay(120)
        store.uploadedDiagnostics.add(fileName)
    }

    override suspend fun shareDataWithDoctorAndPharmacist() {
        delay(150)
        store.dataShared = true
    }

    override suspend fun getSharedPatientProfile(): SharedPatientProfile? {
        if (!store.dataShared) return null
        val registration = store.latestRegistration ?: return null
        return SharedPatientProfile(
            registration = registration,
            location = store.spokeLocation,
            vitals = store.latestBasicVitals,
            uploadedReports = store.uploadedReports.toList(),
            uploadedDiagnostics = store.uploadedDiagnostics.toList()
        )
    }

    override suspend fun setPharmacistConsent(consent: Boolean) {
        delay(100)
        store.pharmacistConsent = consent
    }

    override suspend fun initiatePharmacistCall(): Boolean {
        delay(180)
        val allowed = store.pharmacistConsent == true
        store.pharmacistCallInitiated = allowed
        store.activeCall = allowed
        return allowed
    }

    override suspend fun getPrescribedMedications(): List<MedicationItem> {
        return store.doctorConsultationForm?.treatmentPlan ?: store.doctorMedicationTemplate
    }

    override suspend fun recordDispensation(notes: String) {
        delay(120)
        store.dispensationNotes = notes
    }

    override suspend fun getPharmacistSnapshot(): PharmacistSnapshot {
        return PharmacistSnapshot(
            patientProfile = getSharedPatientProfile(),
            consentGiven = store.pharmacistConsent,
            callInitiated = store.pharmacistCallInitiated,
            prescribedMedications = getPrescribedMedications(),
            dispensationNotes = store.dispensationNotes
        )
    }

    override suspend fun getDoctorCaseByLocation(): DoctorCaseSnapshot {
        return DoctorCaseSnapshot(
            patientProfile = getSharedPatientProfile(),
            doctorDecision = store.doctorDecision,
            consultationForm = store.doctorConsultationForm,
            generatedPdfName = store.generatedPrescriptionPdfName
        )
    }

    override suspend fun setDoctorCallDecision(attend: Boolean) {
        delay(90)
        store.doctorDecision = if (attend) "Attended" else "Declined"
        if (!attend) {
            store.activeCall = false
        }
    }

    override suspend fun saveDoctorConsultation(form: DoctorConsultationForm) {
        delay(220)
        store.doctorConsultationForm = form
        val patientName = store.latestRegistration?.fullName ?: "Patient"
        val doctorName = store.connectedDoctor?.name ?: "Doctor on Duty"
        store.latestPrescription = Prescription(
            id = UUID.randomUUID().toString(),
            patientName = patientName,
            doctorName = doctorName,
            medications = form.treatmentPlan.map { "${it.name} ${it.dosage}, ${it.frequency}, ${it.time}" },
            notes = form.recommendations
        )
    }

    override suspend fun generatePrescriptionPdf(doctorName: String, clinicName: String): String {
        delay(180)
        val now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm"))
        val filename = "Prescription_${clinicName.replace(" ", "")}_${doctorName.replace(" ", "")}_$now.pdf"
        store.generatedPrescriptionPdfName = filename
        return filename
    }
}

class InMemoryPatientRepository(
    private val store: TeleMedMemoryStore
) : PatientRepository {
    override suspend fun register(patient: Patient): Patient {
        delay(300)
        store.currentPatient = patient
        return patient
    }

    override suspend fun getCurrentPatient(): Patient? = store.currentPatient
}

class InMemoryVitalsRepository(
    private val store: TeleMedMemoryStore
) : VitalsRepository {
    override suspend fun saveVitals(vitals: Vitals) {
        delay(250)
        store.latestVitals = vitals
    }

    override suspend fun getLatestVitals(): Vitals? = store.latestVitals
}

class InMemoryDoctorRepository(
    private val store: TeleMedMemoryStore
) : DoctorRepository {
    override suspend fun getDoctors(): List<Doctor> {
        delay(250)
        return store.doctors.sortedBy { it.etaMinutes }
    }

    override suspend fun connectDoctor(doctorId: String): Doctor? {
        delay(500)
        val doctor = store.doctors.firstOrNull { it.id == doctorId && it.isAvailable }
        store.connectedDoctor = doctor
        return doctor
    }

    override suspend fun getConnectedDoctor(): Doctor? = store.connectedDoctor
}

class InMemoryConsultationRepository(
    private val store: TeleMedMemoryStore
) : ConsultationRepository {
    override suspend fun startCall(): Boolean {
        delay(300)
        val canStart = store.connectedDoctor != null
        store.activeCall = canStart
        return canStart
    }

    override suspend fun endCall() {
        delay(150)
        store.activeCall = false
    }

    override suspend fun isCallActive(): Boolean = store.activeCall
}

class InMemoryPrescriptionRepository(
    private val store: TeleMedMemoryStore
) : PrescriptionRepository {
    override suspend fun getLatestPrescription(): Prescription? = store.latestPrescription

    override suspend fun generatePrescription(): Prescription? {
        delay(300)
        val patient = store.currentPatient ?: return null
        val doctor = store.connectedDoctor ?: return null

        val prescription = Prescription(
            id = UUID.randomUUID().toString(),
            patientName = patient.fullName,
            doctorName = doctor.name,
            medications = listOf("Paracetamol 500mg", "Hydration salts"),
            notes = "Take medicines after meals for 3 days and monitor temperature."
        )
        store.latestPrescription = prescription
        return prescription
    }
}

class InMemoryDashboardRepository(
    private val store: TeleMedMemoryStore
) : DashboardRepository {
    override suspend fun loadDashboard(): DashboardData {
        delay(200)
        return DashboardData(
            patientName = store.currentPatient?.fullName ?: "Guest Patient",
            lastVitals = store.latestVitals,
            connectedDoctor = store.connectedDoctor,
            hasActiveCall = store.activeCall,
            latestPrescription = store.latestPrescription
        )
    }
}

