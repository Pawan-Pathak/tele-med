package com.telemed.demo.domain.usecase

import com.telemed.demo.domain.model.*
import com.telemed.demo.domain.repository.*

// Auth
class LoginUseCase(private val repo: AuthRepository) {
    suspend operator fun invoke(email: String, password: String, role: UserRole) =
        repo.login(email, password, role)
}

class LogoutUseCase(private val repo: AuthRepository) {
    suspend operator fun invoke() = repo.logout()
}

// Patient
class RegisterPatientUseCase(private val repo: PatientRepository) {
    suspend operator fun invoke(patient: Patient) = repo.registerPatient(patient)
}

class GetPatientsUseCase(private val repo: PatientRepository) {
    suspend operator fun invoke() = repo.getPatients()
}

class GetRecentPatientsUseCase(private val repo: PatientRepository) {
    suspend operator fun invoke(limit: Int = 10) = repo.getRecentPatients(limit)
}

class GeneratePatientIdUseCase(private val repo: PatientRepository) {
    suspend operator fun invoke() = repo.generatePatientId()
}

// Location
class GetDistrictsUseCase(private val repo: LocationRepository) {
    suspend operator fun invoke() = repo.getDistricts()
}

class GetVillagesUseCase(private val repo: LocationRepository) {
    suspend operator fun invoke(district: String) = repo.getVillages(district)
}

class GetStatesUseCase(private val repo: LocationRepository) {
    suspend operator fun invoke() = repo.getStates()
}

class SaveSpokeLocationUseCase(private val repo: LocationRepository) {
    suspend operator fun invoke(location: SpokeLocation) = repo.saveSpokeLocation(location)
}

// Consultation
class GetPatientQueueUseCase(private val repo: ConsultationRepository) {
    suspend operator fun invoke() = repo.getPatientQueue()
}

class SetConsentUseCase(private val repo: ConsultationRepository) {
    suspend operator fun invoke(patientId: String, consent: Boolean) = repo.setConsent(patientId, consent)
}

class StartCallUseCase(private val repo: ConsultationRepository) {
    suspend operator fun invoke() = repo.startCall()
}

class EndCallUseCase(private val repo: ConsultationRepository) {
    suspend operator fun invoke() = repo.endCall()
}

// Doctor
class GetDoctorsUseCase(private val repo: DoctorRepository) {
    suspend operator fun invoke() = repo.getDoctors()
}

class ConnectDoctorUseCase(private val repo: DoctorRepository) {
    suspend operator fun invoke(doctorId: String) = repo.connectDoctor(doctorId)
}

class SaveDoctorConsultationUseCase(private val repo: DoctorRepository) {
    suspend operator fun invoke(form: DoctorConsultationForm) = repo.saveDoctorConsultation(form)
}

// Prescription
class GeneratePrescriptionUseCase(private val repo: PrescriptionRepository) {
    suspend operator fun invoke(patient: Patient, doctor: Doctor, consultation: DoctorConsultationForm) =
        repo.generatePrescription(patient, doctor, consultation)
}

class GetLatestPrescriptionUseCase(private val repo: PrescriptionRepository) {
    suspend operator fun invoke() = repo.getLatestPrescription()
}

class GetMedicinesForDispensingUseCase(private val repo: PrescriptionRepository) {
    suspend operator fun invoke() = repo.getMedicinesForDispensing()
}

class MarkMedicineDispensedUseCase(private val repo: PrescriptionRepository) {
    suspend operator fun invoke(name: String, dispensed: Boolean) = repo.markMedicineDispensed(name, dispensed)
}

class GetDispensedStatusUseCase(private val repo: PrescriptionRepository) {
    suspend operator fun invoke() = repo.getDispensedStatus()
}
