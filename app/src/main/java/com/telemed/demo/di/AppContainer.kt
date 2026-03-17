package com.telemed.demo.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.telemed.demo.data.local.dummy.TeleMedMemoryStore
import com.telemed.demo.data.repository.*
import com.telemed.demo.domain.usecase.*
import com.telemed.demo.feature.doctor.DoctorModuleViewModel
import com.telemed.demo.feature.healthworker.HealthWorkerModuleViewModel
import com.telemed.demo.feature.pharmacist.PharmacistModuleViewModel

class AppContainer {
    private val store = TeleMedMemoryStore()

    private val authRepository = InMemoryAuthRepository()
    private val patientRepository = InMemoryPatientRepository(store)
    private val locationRepository = InMemoryLocationRepository(store)
    private val consultationRepository = InMemoryConsultationRepository(store)
    private val doctorRepository = InMemoryDoctorRepository(store)
    private val prescriptionRepository = InMemoryPrescriptionRepository(store)
    private val mockDataRepository = InMemoryMockDataRepository(store)

    // Use cases
    val loginUseCase = LoginUseCase(authRepository)
    val logoutUseCase = LogoutUseCase(authRepository)

    val registerPatientUseCase = RegisterPatientUseCase(patientRepository)
    val getRecentPatientsUseCase = GetRecentPatientsUseCase(patientRepository)

    val getDistrictsUseCase = GetDistrictsUseCase(locationRepository)
    val getVillagesUseCase = GetVillagesUseCase(locationRepository)
    val getStatesUseCase = GetStatesUseCase(locationRepository)
    val saveSpokeLocationUseCase = SaveSpokeLocationUseCase(locationRepository)

    val getPatientQueueUseCase = GetPatientQueueUseCase(consultationRepository)
    val setConsentUseCase = SetConsentUseCase(consultationRepository)
    val startCallUseCase = StartCallUseCase(consultationRepository)
    val endCallUseCase = EndCallUseCase(consultationRepository)

    val getDoctorsUseCase = GetDoctorsUseCase(doctorRepository)
    val connectDoctorUseCase = ConnectDoctorUseCase(doctorRepository)
    val saveDoctorConsultationUseCase = SaveDoctorConsultationUseCase(doctorRepository)
    val findBestDoctorUseCase = FindBestDoctorUseCase(doctorRepository)
    val getAvailableDoctorsByLanguageUseCase = GetAvailableDoctorsByLanguageUseCase(doctorRepository)

    val generatePrescriptionUseCase = GeneratePrescriptionUseCase(prescriptionRepository)
    val getLatestPrescriptionUseCase = GetLatestPrescriptionUseCase(prescriptionRepository)
    val getMedicinesForDispensingUseCase = GetMedicinesForDispensingUseCase(prescriptionRepository)
    val markMedicineDispensedUseCase = MarkMedicineDispensedUseCase(prescriptionRepository)
    val getDispensedStatusUseCase = GetDispensedStatusUseCase(prescriptionRepository)

    fun getMockDataRepository() = mockDataRepository
}

class AppViewModelFactory(
    private val container: AppContainer
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HealthWorkerModuleViewModel::class.java) -> {
                HealthWorkerModuleViewModel(
                    container.getDistrictsUseCase,
                    container.getVillagesUseCase,
                    container.getStatesUseCase,
                    container.saveSpokeLocationUseCase,
                    container.registerPatientUseCase,
                    container.getRecentPatientsUseCase,
                    container.findBestDoctorUseCase,
                    container.startCallUseCase,
                    container.endCallUseCase
                ) as T
            }
            modelClass.isAssignableFrom(PharmacistModuleViewModel::class.java) -> {
                PharmacistModuleViewModel(
                    container.getPatientQueueUseCase,
                    container.setConsentUseCase,
                    container.startCallUseCase,
                    container.endCallUseCase,
                    container.getMedicinesForDispensingUseCase,
                    container.markMedicineDispensedUseCase,
                    container.getDispensedStatusUseCase,
                    container.getLatestPrescriptionUseCase
                ) as T
            }
            modelClass.isAssignableFrom(DoctorModuleViewModel::class.java) -> {
                DoctorModuleViewModel(
                    container.getPatientQueueUseCase,
                    container.getDoctorsUseCase,
                    container.saveDoctorConsultationUseCase,
                    container.generatePrescriptionUseCase,
                    container.startCallUseCase,
                    container.endCallUseCase,
                    container.getMockDataRepository()
                ) as T
            }
            else -> error("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
