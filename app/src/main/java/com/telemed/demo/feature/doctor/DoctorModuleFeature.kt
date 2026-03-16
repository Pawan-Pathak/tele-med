package com.telemed.demo.feature.doctor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telemed.demo.domain.model.DoctorConsultationForm
import com.telemed.demo.domain.model.MedicationItem
import com.telemed.demo.domain.usecase.GeneratePrescriptionPdfUseCase
import com.telemed.demo.domain.usecase.GetDoctorCaseByLocationUseCase
import com.telemed.demo.domain.usecase.SaveDoctorConsultationUseCase
import com.telemed.demo.domain.usecase.SetDoctorCallDecisionUseCase
import com.telemed.demo.ui.responsive.ResponsiveScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DoctorUiState(
    val locationSummary: String = "No location data yet",
    val patientSummary: String = "No patient shared profile yet",
    val chiefComplaints: String = "",
    val diagnosis: String = "",
    val medicineName: String = "",
    val dosage: String = "",
    val frequency: String = "",
    val time: String = "",
    val labTests: String = "",
    val diagnosisImaging: String = "",
    val procedureAdvice: String = "",
    val allergies: String = "",
    val recommendations: String = "",
    val referrals: String = "",
    val status: String = "",
    val generatedPdfName: String = ""
)

class DoctorModuleViewModel(
    private val getDoctorCaseByLocationUseCase: GetDoctorCaseByLocationUseCase,
    private val setDoctorCallDecisionUseCase: SetDoctorCallDecisionUseCase,
    private val saveDoctorConsultationUseCase: SaveDoctorConsultationUseCase,
    private val generatePrescriptionPdfUseCase: GeneratePrescriptionPdfUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(DoctorUiState())
    val uiState: StateFlow<DoctorUiState> = _uiState.asStateFlow()

    fun loadCase() {
        viewModelScope.launch {
            val snapshot = getDoctorCaseByLocationUseCase()
            val location = snapshot.patientProfile?.location
            val registration = snapshot.patientProfile?.registration
            _uiState.update {
                it.copy(
                    locationSummary = if (location == null) {
                        "No location data yet"
                    } else {
                        "${location.spokeName}, ${location.village}, ${location.district} @ ${location.localDateTimeIso}"
                    },
                    patientSummary = if (registration == null) {
                        "No patient shared profile yet"
                    } else {
                        "${registration.fullName} | ID ${registration.uniqueId} | ${registration.gender}, ${registration.age}"
                    },
                    status = "Doctor decision: ${snapshot.doctorDecision}",
                    generatedPdfName = snapshot.generatedPdfName.orEmpty()
                )
            }
        }
    }

    fun attendOrDecline(attend: Boolean) {
        viewModelScope.launch {
            setDoctorCallDecisionUseCase(attend)
            _uiState.update { it.copy(status = if (attend) "Call attended" else "Call declined") }
        }
    }

    fun updateState(transform: (DoctorUiState) -> DoctorUiState) {
        _uiState.update(transform)
    }

    fun saveConsultation() {
        viewModelScope.launch {
            val state = _uiState.value
            val form = DoctorConsultationForm(
                chiefComplaints = state.chiefComplaints,
                diagnosis = state.diagnosis,
                treatmentPlan = listOf(
                    MedicationItem(
                        name = state.medicineName,
                        dosage = state.dosage,
                        frequency = state.frequency,
                        time = state.time
                    )
                ),
                labTestAdvice = state.labTests,
                diagnosisImaging = state.diagnosisImaging,
                procedureAdvice = state.procedureAdvice,
                allergies = state.allergies,
                recommendations = state.recommendations,
                referrals = state.referrals
            )
            saveDoctorConsultationUseCase(form)
            _uiState.update { it.copy(status = "Consultation form saved.") }
        }
    }

    fun generatePdf() {
        viewModelScope.launch {
            val filename = generatePrescriptionPdfUseCase(
                doctorName = "Dr Demo",
                clinicName = "TeleMed Clinic"
            )
            _uiState.update {
                it.copy(
                    generatedPdfName = filename,
                    status = "Prescription PDF generated: $filename"
                )
            }
        }
    }
}

@Composable
fun DoctorModuleScreen(
    viewModel: DoctorModuleViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadCase()
    }

    ResponsiveScreen(title = "Doctor Module", onBack = onBack) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("Location-based patient profile")
            Text(uiState.locationSummary)
            Text(uiState.patientSummary)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { viewModel.attendOrDecline(true) }) { Text("Attend") }
                Button(onClick = { viewModel.attendOrDecline(false) }) { Text("Decline") }
            }

            Text("Doctor Consultation Form", style = MaterialTheme.typography.titleMedium)
            Field("Chief complaints", uiState.chiefComplaints) {
                viewModel.updateState { state -> state.copy(chiefComplaints = it) }
            }
            Field("Diagnosis", uiState.diagnosis) {
                viewModel.updateState { state -> state.copy(diagnosis = it) }
            }
            Field("Medicine", uiState.medicineName) {
                viewModel.updateState { state -> state.copy(medicineName = it) }
            }
            Field("Dosage", uiState.dosage) {
                viewModel.updateState { state -> state.copy(dosage = it) }
            }
            Field("Frequency", uiState.frequency) {
                viewModel.updateState { state -> state.copy(frequency = it) }
            }
            Field("Time", uiState.time) {
                viewModel.updateState { state -> state.copy(time = it) }
            }
            Field("Lab test advice", uiState.labTests) {
                viewModel.updateState { state -> state.copy(labTests = it) }
            }
            Field("Diagnosis imaging", uiState.diagnosisImaging) {
                viewModel.updateState { state -> state.copy(diagnosisImaging = it) }
            }
            Field("Procedure", uiState.procedureAdvice) {
                viewModel.updateState { state -> state.copy(procedureAdvice = it) }
            }
            Field("Allergies", uiState.allergies) {
                viewModel.updateState { state -> state.copy(allergies = it) }
            }
            Field("Recommendations", uiState.recommendations) {
                viewModel.updateState { state -> state.copy(recommendations = it) }
            }
            Field("Referrals", uiState.referrals) {
                viewModel.updateState { state -> state.copy(referrals = it) }
            }

            Button(onClick = viewModel::saveConsultation, modifier = Modifier.fillMaxWidth()) {
                Text("Save consultation")
            }
            Button(onClick = viewModel::generatePdf, modifier = Modifier.fillMaxWidth()) {
                Text("Generate prescription PDF")
            }

            if (uiState.generatedPdfName.isNotBlank()) {
                Text("PDF: ${uiState.generatedPdfName}")
            }
            if (uiState.status.isNotBlank()) {
                Text(uiState.status, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
private fun Field(label: String, value: String, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) }
    )
}

