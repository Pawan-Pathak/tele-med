package com.telemed.demo.data.repository

import com.telemed.demo.data.local.dummy.TeleMedMemoryStore
import com.telemed.demo.domain.model.*
import com.telemed.demo.domain.repository.*
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

class InMemoryAuthRepository : AuthRepository {
    private var currentSession: SessionUser? = null

    override suspend fun login(email: String, password: String, role: UserRole): Result<SessionUser> {
        delay(500)
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
    override suspend fun logout() { currentSession = null }
}

class InMemoryPatientRepository(
    private val store: TeleMedMemoryStore
) : PatientRepository {
    override suspend fun registerPatient(patient: Patient): Patient {
        delay(300)
        val registered = patient.copy(
            id = if (patient.id.isBlank()) store.generatePatientId() else patient.id,
            registeredAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
            status = ConsultationStatus.REGISTERED
        )
        store.patients.add(registered)
        store.patientQueue.add(
            PatientQueueItem(registered, registered.registeredAt, ConsultationStatus.WAITING)
        )
        return registered
    }

    override suspend fun getPatients(): List<Patient> = store.patients.toList()

    override suspend fun getPatientById(id: String): Patient? =
        store.patients.find { it.id == id }

    override suspend fun getRecentPatients(limit: Int): List<Patient> =
        store.patients.sortedByDescending { it.registeredAt }.take(limit)

    override suspend fun generatePatientId(): String = store.generatePatientId()
}

class InMemoryLocationRepository(
    private val store: TeleMedMemoryStore
) : LocationRepository {
    override suspend fun getDistricts(): List<String> = store.districts
    override suspend fun getVillages(district: String): List<String> =
        store.villagesByDistrict[district].orEmpty()
    override suspend fun getStates(): List<String> = store.states
    override suspend fun saveSpokeLocation(location: SpokeLocation) {
        store.spokeLocation = location
    }
    override suspend fun getSpokeLocation(): SpokeLocation? = store.spokeLocation
}

class InMemoryConsultationRepository(
    private val store: TeleMedMemoryStore
) : ConsultationRepository {
    override suspend fun getPatientQueue(): List<PatientQueueItem> =
        store.patientQueue.toList()

    override suspend fun updatePatientStatus(patientId: String, status: ConsultationStatus) {
        val idx = store.patientQueue.indexOfFirst { it.patient.id == patientId }
        if (idx >= 0) {
            val item = store.patientQueue[idx]
            store.patientQueue[idx] = item.copy(status = status)
        }
        val pIdx = store.patients.indexOfFirst { it.id == patientId }
        if (pIdx >= 0) {
            store.patients[pIdx] = store.patients[pIdx].copy(status = status)
        }
    }

    override suspend fun setConsent(patientId: String, consent: Boolean) {
        store.pharmacistConsent = consent
        if (consent) {
            updatePatientStatus(patientId, ConsultationStatus.IN_PROGRESS)
        } else {
            updatePatientStatus(patientId, ConsultationStatus.DECLINED)
        }
    }

    override suspend fun startCall(): Boolean {
        delay(300)
        store.activeCall = true
        return true
    }

    override suspend fun endCall() {
        delay(150)
        store.activeCall = false
    }

    override suspend fun isCallActive(): Boolean = store.activeCall
}

class InMemoryDoctorRepository(
    private val store: TeleMedMemoryStore
) : DoctorRepository {
    override suspend fun getDoctors(): List<Doctor> =
        store.doctors.sortedBy { it.etaMinutes }

    override suspend fun getDoctorsByDistrict(district: String): List<Doctor> =
        store.doctors.filter { it.district == district || it.isAvailable }

    override suspend fun connectDoctor(doctorId: String): Doctor? {
        delay(400)
        val doctor = store.doctors.firstOrNull { it.id == doctorId && it.isAvailable }
        store.connectedDoctor = doctor
        return doctor
    }

    override suspend fun getConnectedDoctor(): Doctor? = store.connectedDoctor

    override suspend fun saveDoctorConsultation(form: DoctorConsultationForm) {
        delay(200)
        store.doctorConsultationForm = form
    }

    override suspend fun getDoctorConsultation(): DoctorConsultationForm? =
        store.doctorConsultationForm
}

class InMemoryPrescriptionRepository(
    private val store: TeleMedMemoryStore
) : PrescriptionRepository {
    override suspend fun generatePrescription(
        patient: Patient,
        doctor: Doctor,
        consultation: DoctorConsultationForm
    ): Prescription {
        delay(300)
        val prescription = Prescription(
            id = UUID.randomUUID().toString().take(8).uppercase(),
            patientId = patient.id,
            patientName = patient.fullName,
            patientAge = patient.age,
            patientGender = patient.gender.name,
            doctorName = doctor.name,
            doctorQualification = doctor.qualification,
            clinicName = doctor.clinicName,
            regNumber = doctor.regNumber,
            date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
            chiefComplaints = consultation.chiefComplaints,
            diagnosis = consultation.diagnosis,
            medicines = consultation.medicines,
            labTests = consultation.labTests,
            referral = consultation.referral,
            recommendations = consultation.recommendations,
            allergies = consultation.allergies,
            procedures = consultation.procedures,
            imagingNotes = consultation.imagingNotes
        )
        store.generatedPrescription = prescription
        return prescription
    }

    override suspend fun getLatestPrescription(): Prescription? = store.generatedPrescription

    override suspend fun getMedicinesForDispensing(): List<Medicine> =
        store.generatedPrescription?.medicines ?: emptyList()

    override suspend fun markMedicineDispensed(medicineName: String, dispensed: Boolean) {
        store.dispensedMedicines[medicineName] = dispensed
    }

    override suspend fun getDispensedStatus(): Map<String, Boolean> =
        store.dispensedMedicines.toMap()
}

class InMemoryMockDataRepository(
    private val store: TeleMedMemoryStore
) : MockDataRepository {
    override fun getDrugList(): List<DrugItem> = store.drugList
    override fun getICD10List(): List<ICD10Item> = store.icd10List
    override fun getLabTests(): List<String> = store.labTests
    override fun getCommonSymptoms(): List<String> = store.commonSymptoms
    override fun getKnownConditions(): List<String> = store.knownConditions
}
