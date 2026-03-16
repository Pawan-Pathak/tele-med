package com.telemed.demo.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.telemed.demo.data.local.dummy.TeleMedMemoryStore
import com.telemed.demo.data.repository.InMemoryAuthRepository
import com.telemed.demo.data.repository.InMemoryConsultationRepository
import com.telemed.demo.data.repository.InMemoryDashboardRepository
import com.telemed.demo.data.repository.InMemoryDoctorRepository
import com.telemed.demo.data.repository.InMemoryPatientRepository
import com.telemed.demo.data.repository.InMemoryPrescriptionRepository
import com.telemed.demo.data.repository.InMemoryVitalsRepository
import com.telemed.demo.data.repository.InMemoryWorkflowRepository
import com.telemed.demo.domain.usecase.ConnectDoctorUseCase
import com.telemed.demo.domain.usecase.DownloadReportUseCase
import com.telemed.demo.domain.usecase.EndCallUseCase
import com.telemed.demo.domain.usecase.GeneratePrescriptionUseCase
import com.telemed.demo.domain.usecase.GeneratePrescriptionPdfUseCase
import com.telemed.demo.domain.usecase.GetCallStatusUseCase
import com.telemed.demo.domain.usecase.GetCurrentSessionUseCase
import com.telemed.demo.domain.usecase.GetDashboardUseCase
import com.telemed.demo.domain.usecase.GetDoctorCaseByLocationUseCase
import com.telemed.demo.domain.usecase.GetDoctorPoolUseCase
import com.telemed.demo.domain.usecase.GetMappedDistrictsUseCase
import com.telemed.demo.domain.usecase.GetMappedVillagesUseCase
import com.telemed.demo.domain.usecase.GetPharmacistSnapshotUseCase
import com.telemed.demo.domain.usecase.GetPrescriptionUseCase
import com.telemed.demo.domain.usecase.InitiatePharmacistCallUseCase
import com.telemed.demo.domain.usecase.LoginUseCase
import com.telemed.demo.domain.usecase.LogoutUseCase
import com.telemed.demo.domain.usecase.RecordDispensationUseCase
import com.telemed.demo.domain.usecase.RegisterPatientUseCase
import com.telemed.demo.domain.usecase.RegisterPatientProfileUseCase
import com.telemed.demo.domain.usecase.SaveBasicVitalsUseCase
import com.telemed.demo.domain.usecase.SaveDoctorConsultationUseCase
import com.telemed.demo.domain.usecase.SaveSpokeLocationUseCase
import com.telemed.demo.domain.usecase.SaveVitalsUseCase
import com.telemed.demo.domain.usecase.SetDoctorCallDecisionUseCase
import com.telemed.demo.domain.usecase.SetPharmacistConsentUseCase
import com.telemed.demo.domain.usecase.ShareDataUseCase
import com.telemed.demo.domain.usecase.StartCallUseCase
import com.telemed.demo.domain.usecase.UploadDiagnosticUseCase
import com.telemed.demo.domain.usecase.UploadReportUseCase
import com.telemed.demo.feature.dashboard.DashboardViewModel
import com.telemed.demo.feature.doctor.DoctorModuleViewModel
import com.telemed.demo.feature.doctorpool.DoctorPoolViewModel
import com.telemed.demo.feature.healthworker.HealthWorkerModuleViewModel
import com.telemed.demo.feature.login.LoginViewModel
import com.telemed.demo.feature.pharmacist.PharmacistModuleViewModel
import com.telemed.demo.feature.prescription.PrescriptionSummaryViewModel
import com.telemed.demo.feature.registration.RegistrationViewModel
import com.telemed.demo.feature.videocall.VideoCallViewModel
import com.telemed.demo.feature.vitals.VitalsViewModel

class AppContainer {
    private val store = TeleMedMemoryStore()

    private val authRepository = InMemoryAuthRepository()
    private val patientRepository = InMemoryPatientRepository(store)
    private val vitalsRepository = InMemoryVitalsRepository(store)
    private val doctorRepository = InMemoryDoctorRepository(store)
    private val consultationRepository = InMemoryConsultationRepository(store)
    private val prescriptionRepository = InMemoryPrescriptionRepository(store)
    private val dashboardRepository = InMemoryDashboardRepository(store)
    private val workflowRepository = InMemoryWorkflowRepository(store)

    val loginUseCase = LoginUseCase(authRepository)
    val getCurrentSessionUseCase = GetCurrentSessionUseCase(authRepository)
    val logoutUseCase = LogoutUseCase(authRepository)

    val getMappedDistrictsUseCase = GetMappedDistrictsUseCase(workflowRepository)
    val getMappedVillagesUseCase = GetMappedVillagesUseCase(workflowRepository)
    val saveSpokeLocationUseCase = SaveSpokeLocationUseCase(workflowRepository)
    val registerPatientProfileUseCase = RegisterPatientProfileUseCase(workflowRepository)
    val saveBasicVitalsUseCase = SaveBasicVitalsUseCase(workflowRepository)
    val uploadReportUseCase = UploadReportUseCase(workflowRepository)
    val downloadReportUseCase = DownloadReportUseCase(workflowRepository)
    val uploadDiagnosticUseCase = UploadDiagnosticUseCase(workflowRepository)
    val shareDataUseCase = ShareDataUseCase(workflowRepository)

    val setPharmacistConsentUseCase = SetPharmacistConsentUseCase(workflowRepository)
    val initiatePharmacistCallUseCase = InitiatePharmacistCallUseCase(workflowRepository)
    val getPharmacistSnapshotUseCase = GetPharmacistSnapshotUseCase(workflowRepository)
    val recordDispensationUseCase = RecordDispensationUseCase(workflowRepository)

    val getDoctorCaseByLocationUseCase = GetDoctorCaseByLocationUseCase(workflowRepository)
    val setDoctorCallDecisionUseCase = SetDoctorCallDecisionUseCase(workflowRepository)
    val saveDoctorConsultationUseCase = SaveDoctorConsultationUseCase(workflowRepository)
    val generatePrescriptionPdfUseCase = GeneratePrescriptionPdfUseCase(workflowRepository)

    val registerPatientUseCase = RegisterPatientUseCase(patientRepository)
    val saveVitalsUseCase = SaveVitalsUseCase(vitalsRepository)
    val getDoctorPoolUseCase = GetDoctorPoolUseCase(doctorRepository)
    val connectDoctorUseCase = ConnectDoctorUseCase(doctorRepository)
    val startCallUseCase = StartCallUseCase(consultationRepository)
    val endCallUseCase = EndCallUseCase(consultationRepository)
    val getCallStatusUseCase = GetCallStatusUseCase(consultationRepository)
    val getPrescriptionUseCase = GetPrescriptionUseCase(prescriptionRepository)
    val generatePrescriptionUseCase = GeneratePrescriptionUseCase(prescriptionRepository)
    val getDashboardUseCase = GetDashboardUseCase(dashboardRepository)
}

class AppViewModelFactory(
    private val container: AppContainer
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(container.loginUseCase) as T
            }
            modelClass.isAssignableFrom(HealthWorkerModuleViewModel::class.java) -> {
                HealthWorkerModuleViewModel(
                    container.getMappedDistrictsUseCase,
                    container.getMappedVillagesUseCase,
                    container.saveSpokeLocationUseCase,
                    container.registerPatientProfileUseCase,
                    container.saveBasicVitalsUseCase,
                    container.uploadReportUseCase,
                    container.downloadReportUseCase,
                    container.uploadDiagnosticUseCase,
                    container.shareDataUseCase
                ) as T
            }
            modelClass.isAssignableFrom(PharmacistModuleViewModel::class.java) -> {
                PharmacistModuleViewModel(
                    container.setPharmacistConsentUseCase,
                    container.initiatePharmacistCallUseCase,
                    container.getPharmacistSnapshotUseCase,
                    container.recordDispensationUseCase
                ) as T
            }
            modelClass.isAssignableFrom(DoctorModuleViewModel::class.java) -> {
                DoctorModuleViewModel(
                    container.getDoctorCaseByLocationUseCase,
                    container.setDoctorCallDecisionUseCase,
                    container.saveDoctorConsultationUseCase,
                    container.generatePrescriptionPdfUseCase
                ) as T
            }
            modelClass.isAssignableFrom(RegistrationViewModel::class.java) -> {
                RegistrationViewModel(container.registerPatientUseCase) as T
            }
            modelClass.isAssignableFrom(VitalsViewModel::class.java) -> {
                VitalsViewModel(container.saveVitalsUseCase) as T
            }
            modelClass.isAssignableFrom(DoctorPoolViewModel::class.java) -> {
                DoctorPoolViewModel(container.getDoctorPoolUseCase, container.connectDoctorUseCase) as T
            }
            modelClass.isAssignableFrom(VideoCallViewModel::class.java) -> {
                VideoCallViewModel(container.startCallUseCase, container.endCallUseCase, container.getCallStatusUseCase) as T
            }
            modelClass.isAssignableFrom(PrescriptionSummaryViewModel::class.java) -> {
                PrescriptionSummaryViewModel(container.getPrescriptionUseCase, container.generatePrescriptionUseCase) as T
            }
            modelClass.isAssignableFrom(DashboardViewModel::class.java) -> {
                DashboardViewModel(container.getDashboardUseCase, container.getCurrentSessionUseCase) as T
            }
            else -> error("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}

