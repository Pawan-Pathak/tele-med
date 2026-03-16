package com.telemed.demo.domain.repository

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
import com.telemed.demo.domain.model.BasicVitalsData

interface AuthRepository {
    suspend fun login(email: String, password: String, role: UserRole): Result<SessionUser>
    suspend fun getCurrentSession(): SessionUser?
    suspend fun logout()
}

interface WorkflowRepository {
    suspend fun saveSpokeLocation(location: SpokeLocation)
    suspend fun getMappedDistricts(): List<String>
    suspend fun getMappedVillages(district: String): List<String>

    suspend fun registerPatient(data: PatientRegistrationData): PatientRegistrationData
    suspend fun saveBasicVitals(data: BasicVitalsData)
    suspend fun uploadReport(fileName: String)
    suspend fun downloadReport(fileName: String): String
    suspend fun uploadDiagnostic(fileName: String)
    suspend fun shareDataWithDoctorAndPharmacist()

    suspend fun getSharedPatientProfile(): SharedPatientProfile?

    suspend fun setPharmacistConsent(consent: Boolean)
    suspend fun initiatePharmacistCall(): Boolean
    suspend fun getPrescribedMedications(): List<MedicationItem>
    suspend fun recordDispensation(notes: String)
    suspend fun getPharmacistSnapshot(): PharmacistSnapshot

    suspend fun getDoctorCaseByLocation(): DoctorCaseSnapshot
    suspend fun setDoctorCallDecision(attend: Boolean)
    suspend fun saveDoctorConsultation(form: DoctorConsultationForm)
    suspend fun generatePrescriptionPdf(doctorName: String, clinicName: String): String
}

interface PatientRepository {
    suspend fun register(patient: Patient): Patient
    suspend fun getCurrentPatient(): Patient?
}

interface VitalsRepository {
    suspend fun saveVitals(vitals: Vitals)
    suspend fun getLatestVitals(): Vitals?
}

interface DoctorRepository {
    suspend fun getDoctors(): List<Doctor>
    suspend fun connectDoctor(doctorId: String): Doctor?
    suspend fun getConnectedDoctor(): Doctor?
}

interface ConsultationRepository {
    suspend fun startCall(): Boolean
    suspend fun endCall()
    suspend fun isCallActive(): Boolean
}

interface PrescriptionRepository {
    suspend fun getLatestPrescription(): Prescription?
    suspend fun generatePrescription(): Prescription?
}

interface DashboardRepository {
    suspend fun loadDashboard(): DashboardData
}

