package com.telemed.demo.domain.repository

import com.telemed.demo.domain.model.*

interface AuthRepository {
    suspend fun login(email: String, password: String, role: UserRole): Result<SessionUser>
    suspend fun getCurrentSession(): SessionUser?
    suspend fun logout()
}

interface PatientRepository {
    suspend fun registerPatient(patient: Patient): Patient
    suspend fun getPatients(): List<Patient>
    suspend fun getPatientById(id: String): Patient?
    suspend fun getRecentPatients(limit: Int = 10): List<Patient>
    suspend fun generatePatientId(): String
}

interface LocationRepository {
    suspend fun getDistricts(): List<String>
    suspend fun getVillages(district: String): List<String>
    suspend fun getStates(): List<String>
    suspend fun saveSpokeLocation(location: SpokeLocation)
    suspend fun getSpokeLocation(): SpokeLocation?
}

interface ConsultationRepository {
    suspend fun getPatientQueue(): List<PatientQueueItem>
    suspend fun updatePatientStatus(patientId: String, status: ConsultationStatus)
    suspend fun setConsent(patientId: String, consent: Boolean)
    suspend fun startCall(): Boolean
    suspend fun endCall()
    suspend fun isCallActive(): Boolean
}

interface DoctorRepository {
    suspend fun getDoctors(): List<Doctor>
    suspend fun getDoctorsByDistrict(district: String): List<Doctor>
    suspend fun connectDoctor(doctorId: String): Doctor?
    suspend fun getConnectedDoctor(): Doctor?
    suspend fun saveDoctorConsultation(form: DoctorConsultationForm)
    suspend fun getDoctorConsultation(): DoctorConsultationForm?
}

interface PrescriptionRepository {
    suspend fun generatePrescription(
        patient: Patient,
        doctor: Doctor,
        consultation: DoctorConsultationForm
    ): Prescription
    suspend fun getLatestPrescription(): Prescription?
    suspend fun getMedicinesForDispensing(): List<Medicine>
    suspend fun markMedicineDispensed(medicineName: String, dispensed: Boolean)
    suspend fun getDispensedStatus(): Map<String, Boolean>
}

interface MockDataRepository {
    fun getDrugList(): List<DrugItem>
    fun getICD10List(): List<ICD10Item>
    fun getLabTests(): List<String>
    fun getCommonSymptoms(): List<String>
    fun getKnownConditions(): List<String>
}
