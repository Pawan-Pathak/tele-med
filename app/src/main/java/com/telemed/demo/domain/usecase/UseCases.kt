package com.telemed.demo.domain.usecase

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
import com.telemed.demo.domain.repository.AuthRepository
import com.telemed.demo.domain.repository.ConsultationRepository
import com.telemed.demo.domain.repository.DashboardRepository
import com.telemed.demo.domain.repository.DoctorRepository
import com.telemed.demo.domain.repository.PatientRepository
import com.telemed.demo.domain.repository.PrescriptionRepository
import com.telemed.demo.domain.repository.VitalsRepository
import com.telemed.demo.domain.repository.WorkflowRepository

class LoginUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String, role: UserRole): Result<SessionUser> =
        repository.login(email, password, role)
}

class GetCurrentSessionUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(): SessionUser? = repository.getCurrentSession()
}

class LogoutUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke() = repository.logout()
}

class SaveSpokeLocationUseCase(private val repository: WorkflowRepository) {
    suspend operator fun invoke(location: SpokeLocation) = repository.saveSpokeLocation(location)
}

class GetMappedDistrictsUseCase(private val repository: WorkflowRepository) {
    suspend operator fun invoke(): List<String> = repository.getMappedDistricts()
}

class GetMappedVillagesUseCase(private val repository: WorkflowRepository) {
    suspend operator fun invoke(district: String): List<String> = repository.getMappedVillages(district)
}

class RegisterPatientProfileUseCase(private val repository: WorkflowRepository) {
    suspend operator fun invoke(data: PatientRegistrationData): PatientRegistrationData =
        repository.registerPatient(data)
}

class SaveBasicVitalsUseCase(private val repository: WorkflowRepository) {
    suspend operator fun invoke(data: BasicVitalsData) = repository.saveBasicVitals(data)
}

class UploadReportUseCase(private val repository: WorkflowRepository) {
    suspend operator fun invoke(fileName: String) = repository.uploadReport(fileName)
}

class DownloadReportUseCase(private val repository: WorkflowRepository) {
    suspend operator fun invoke(fileName: String): String = repository.downloadReport(fileName)
}

class UploadDiagnosticUseCase(private val repository: WorkflowRepository) {
    suspend operator fun invoke(fileName: String) = repository.uploadDiagnostic(fileName)
}

class ShareDataUseCase(private val repository: WorkflowRepository) {
    suspend operator fun invoke() = repository.shareDataWithDoctorAndPharmacist()
}

class GetSharedPatientProfileUseCase(private val repository: WorkflowRepository) {
    suspend operator fun invoke(): SharedPatientProfile? = repository.getSharedPatientProfile()
}

class SetPharmacistConsentUseCase(private val repository: WorkflowRepository) {
    suspend operator fun invoke(consent: Boolean) = repository.setPharmacistConsent(consent)
}

class InitiatePharmacistCallUseCase(private val repository: WorkflowRepository) {
    suspend operator fun invoke(): Boolean = repository.initiatePharmacistCall()
}

class GetPrescribedMedicationsUseCase(private val repository: WorkflowRepository) {
    suspend operator fun invoke(): List<MedicationItem> = repository.getPrescribedMedications()
}

class RecordDispensationUseCase(private val repository: WorkflowRepository) {
    suspend operator fun invoke(notes: String) = repository.recordDispensation(notes)
}

class GetPharmacistSnapshotUseCase(private val repository: WorkflowRepository) {
    suspend operator fun invoke(): PharmacistSnapshot = repository.getPharmacistSnapshot()
}

class GetDoctorCaseByLocationUseCase(private val repository: WorkflowRepository) {
    suspend operator fun invoke(): DoctorCaseSnapshot = repository.getDoctorCaseByLocation()
}

class SetDoctorCallDecisionUseCase(private val repository: WorkflowRepository) {
    suspend operator fun invoke(attend: Boolean) = repository.setDoctorCallDecision(attend)
}

class SaveDoctorConsultationUseCase(private val repository: WorkflowRepository) {
    suspend operator fun invoke(form: DoctorConsultationForm) = repository.saveDoctorConsultation(form)
}

class GeneratePrescriptionPdfUseCase(private val repository: WorkflowRepository) {
    suspend operator fun invoke(doctorName: String, clinicName: String): String =
        repository.generatePrescriptionPdf(doctorName, clinicName)
}

class RegisterPatientUseCase(private val repository: PatientRepository) {
    suspend operator fun invoke(patient: Patient): Patient = repository.register(patient)
}

class SaveVitalsUseCase(private val repository: VitalsRepository) {
    suspend operator fun invoke(vitals: Vitals) = repository.saveVitals(vitals)
}

class GetDoctorPoolUseCase(private val repository: DoctorRepository) {
    suspend operator fun invoke(): List<Doctor> = repository.getDoctors()
}

class ConnectDoctorUseCase(private val repository: DoctorRepository) {
    suspend operator fun invoke(doctorId: String): Doctor? = repository.connectDoctor(doctorId)
}

class StartCallUseCase(private val repository: ConsultationRepository) {
    suspend operator fun invoke(): Boolean = repository.startCall()
}

class EndCallUseCase(private val repository: ConsultationRepository) {
    suspend operator fun invoke() = repository.endCall()
}

class GetCallStatusUseCase(private val repository: ConsultationRepository) {
    suspend operator fun invoke(): Boolean = repository.isCallActive()
}

class GetPrescriptionUseCase(private val repository: PrescriptionRepository) {
    suspend operator fun invoke(): Prescription? = repository.getLatestPrescription()
}

class GeneratePrescriptionUseCase(private val repository: PrescriptionRepository) {
    suspend operator fun invoke(): Prescription? = repository.generatePrescription()
}

class GetDashboardUseCase(private val repository: DashboardRepository) {
    suspend operator fun invoke(): DashboardData = repository.loadDashboard()
}

