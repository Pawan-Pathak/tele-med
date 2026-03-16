package com.telemed.demo.feature.healthworker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telemed.demo.domain.model.BasicVitalsData
import com.telemed.demo.domain.model.LifestyleHistory
import com.telemed.demo.domain.model.PatientRegistrationData
import com.telemed.demo.domain.model.SpokeLocation
import com.telemed.demo.domain.usecase.DownloadReportUseCase
import com.telemed.demo.domain.usecase.GetMappedDistrictsUseCase
import com.telemed.demo.domain.usecase.GetMappedVillagesUseCase
import com.telemed.demo.domain.usecase.RegisterPatientProfileUseCase
import com.telemed.demo.domain.usecase.SaveBasicVitalsUseCase
import com.telemed.demo.domain.usecase.SaveSpokeLocationUseCase
import com.telemed.demo.domain.usecase.ShareDataUseCase
import com.telemed.demo.domain.usecase.UploadDiagnosticUseCase
import com.telemed.demo.domain.usecase.UploadReportUseCase
import com.telemed.demo.ui.responsive.ResponsiveScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime

data class HealthWorkerUiState(
    val spokeName: String = "",
    val district: String = "",
    val village: String = "",
    val localDateTime: String = "",
    val districts: List<String> = emptyList(),
    val villages: List<String> = emptyList(),
    val fullName: String = "",
    val spouseOrFatherName: String = "",
    val gender: String = "",
    val age: String = "",
    val dob: String = "",
    val state: String = "",
    val mobile: String = "",
    val aadhaar: String = "",
    val uniqueId: String = "",
    val weight: String = "",
    val temperature: String = "",
    val bloodPressure: String = "",
    val bloodSugar: String = "",
    val hemoglobin: String = "",
    val otherVitals: String = "",
    val primaryComplaint: String = "",
    val basicSymptoms: String = "",
    val medicalHistory: String = "",
    val alcohol: Boolean = false,
    val tobacco: Boolean = false,
    val drugs: Boolean = false,
    val reportFileName: String = "cbc_report.pdf",
    val diagnosticFileName: String = "xray_chest.png",
    val statusMessage: String = ""
)

class HealthWorkerModuleViewModel(
    private val getMappedDistrictsUseCase: GetMappedDistrictsUseCase,
    private val getMappedVillagesUseCase: GetMappedVillagesUseCase,
    private val saveSpokeLocationUseCase: SaveSpokeLocationUseCase,
    private val registerPatientProfileUseCase: RegisterPatientProfileUseCase,
    private val saveBasicVitalsUseCase: SaveBasicVitalsUseCase,
    private val uploadReportUseCase: UploadReportUseCase,
    private val downloadReportUseCase: DownloadReportUseCase,
    private val uploadDiagnosticUseCase: UploadDiagnosticUseCase,
    private val shareDataUseCase: ShareDataUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(HealthWorkerUiState())
    val uiState: StateFlow<HealthWorkerUiState> = _uiState.asStateFlow()

    fun bootstrap() {
        viewModelScope.launch {
            val districts = getMappedDistrictsUseCase()
            _uiState.update {
                it.copy(
                    districts = districts,
                    district = districts.firstOrNull().orEmpty(),
                    localDateTime = LocalDateTime.now().toString()
                )
            }
            loadVillages(_uiState.value.district)
        }
    }

    private fun loadVillages(district: String) {
        viewModelScope.launch {
            val villages = getMappedVillagesUseCase(district)
            _uiState.update {
                it.copy(villages = villages, village = villages.firstOrNull().orEmpty())
            }
        }
    }

    fun updateField(transform: (HealthWorkerUiState) -> HealthWorkerUiState) {
        _uiState.update(transform)
    }

    fun onDistrictSelected(district: String) {
        _uiState.update { it.copy(district = district) }
        loadVillages(district)
    }

    fun onVillageSelected(village: String) {
        _uiState.update { it.copy(village = village) }
    }

    fun saveAndShare() {
        viewModelScope.launch {
            val state = _uiState.value
            val registration = registerPatientProfileUseCase(
                PatientRegistrationData(
                    uniqueId = state.uniqueId,
                    fullName = state.fullName,
                    spouseOrFatherName = state.spouseOrFatherName,
                    gender = state.gender,
                    age = state.age.toIntOrNull() ?: 0,
                    dob = state.dob,
                    village = state.village,
                    district = state.district,
                    state = state.state,
                    mobileNumber = state.mobile,
                    aadhaarNumber = state.aadhaar
                )
            )

            saveSpokeLocationUseCase(
                SpokeLocation(
                    spokeName = state.spokeName,
                    district = state.district,
                    village = state.village,
                    localDateTimeIso = state.localDateTime
                )
            )

            saveBasicVitalsUseCase(
                BasicVitalsData(
                    weightKg = state.weight.toDoubleOrNull() ?: 0.0,
                    temperatureC = state.temperature.toDoubleOrNull() ?: 0.0,
                    bloodPressure = state.bloodPressure,
                    bloodSugar = state.bloodSugar.toDoubleOrNull() ?: 0.0,
                    hemoglobin = state.hemoglobin.toDoubleOrNull() ?: 0.0,
                    otherVitals = state.otherVitals,
                    primaryComplaint = state.primaryComplaint,
                    basicSymptoms = state.basicSymptoms,
                    medicalHistory = state.medicalHistory,
                    lifestyleHistory = LifestyleHistory(
                        alcohol = state.alcohol,
                        tobacco = state.tobacco,
                        drugs = state.drugs
                    )
                )
            )

            uploadReportUseCase(state.reportFileName)
            uploadDiagnosticUseCase(state.diagnosticFileName)
            shareDataUseCase()
            _uiState.update {
                it.copy(
                    uniqueId = registration.uniqueId,
                    statusMessage = "Saved and shared with doctor + pharmacist. ID: ${registration.uniqueId}"
                )
            }
        }
    }

    fun downloadReport() {
        viewModelScope.launch {
            val message = downloadReportUseCase(_uiState.value.reportFileName)
            _uiState.update { it.copy(statusMessage = message) }
        }
    }
}

@Composable
fun HealthWorkerModuleScreen(
    viewModel: HealthWorkerModuleViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.bootstrap()
    }

    ResponsiveScreen(title = "Health Worker Module", onBack = onBack) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SectionTitle("Pre-Consultation (Before Login)")
            OutlinedTextField(
                value = uiState.spokeName,
                onValueChange = { viewModel.updateField { state -> state.copy(spokeName = it) } },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Spoke Name") }
            )
            Text("District (mapped list)")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                uiState.districts.forEach { district ->
                    Button(onClick = { viewModel.onDistrictSelected(district) }) { Text(district) }
                }
            }
            Text("Village (mapped list)")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                uiState.villages.forEach { village ->
                    Button(onClick = { viewModel.onVillageSelected(village) }) { Text(village) }
                }
            }
            Text("Auto-captured local date/time: ${uiState.localDateTime}")

            SectionTitle("New Patient Registration")
            TextFieldLine("Full Name", uiState.fullName) { value ->
                viewModel.updateField { it.copy(fullName = value) }
            }
            TextFieldLine("Spouse/Father's Name", uiState.spouseOrFatherName) { value ->
                viewModel.updateField { it.copy(spouseOrFatherName = value) }
            }
            TextFieldLine("Gender", uiState.gender) { value ->
                viewModel.updateField { it.copy(gender = value) }
            }
            TextFieldLine("Age", uiState.age) { value ->
                viewModel.updateField { it.copy(age = value) }
            }
            TextFieldLine("DOB (YYYY-MM-DD)", uiState.dob) { value ->
                viewModel.updateField { it.copy(dob = value) }
            }
            TextFieldLine("State", uiState.state) { value ->
                viewModel.updateField { it.copy(state = value) }
            }
            TextFieldLine("Mobile Number", uiState.mobile) { value ->
                viewModel.updateField { it.copy(mobile = value) }
            }
            TextFieldLine("Aadhaar Number", uiState.aadhaar) { value ->
                viewModel.updateField { it.copy(aadhaar = value) }
            }

            SectionTitle("Basic Vitals + History")
            TextFieldLine("Weight (kg)", uiState.weight) { value ->
                viewModel.updateField { it.copy(weight = value) }
            }
            TextFieldLine("Temperature (C)", uiState.temperature) { value ->
                viewModel.updateField { it.copy(temperature = value) }
            }
            TextFieldLine("Blood Pressure", uiState.bloodPressure) { value ->
                viewModel.updateField { it.copy(bloodPressure = value) }
            }
            TextFieldLine("Blood Sugar", uiState.bloodSugar) { value ->
                viewModel.updateField { it.copy(bloodSugar = value) }
            }
            TextFieldLine("Hemoglobin", uiState.hemoglobin) { value ->
                viewModel.updateField { it.copy(hemoglobin = value) }
            }
            TextFieldLine("Other vitals", uiState.otherVitals) { value ->
                viewModel.updateField { it.copy(otherVitals = value) }
            }
            TextFieldLine("Primary complaint", uiState.primaryComplaint) { value ->
                viewModel.updateField { it.copy(primaryComplaint = value) }
            }
            TextFieldLine("Basic symptoms", uiState.basicSymptoms) { value ->
                viewModel.updateField { it.copy(basicSymptoms = value) }
            }
            TextFieldLine("Medical history", uiState.medicalHistory) { value ->
                viewModel.updateField { it.copy(medicalHistory = value) }
            }

            LifestyleSwitch("Alcohol", uiState.alcohol) { enabled ->
                viewModel.updateField { it.copy(alcohol = enabled) }
            }
            LifestyleSwitch("Tobacco", uiState.tobacco) { enabled ->
                viewModel.updateField { it.copy(tobacco = enabled) }
            }
            LifestyleSwitch("Drugs", uiState.drugs) { enabled ->
                viewModel.updateField { it.copy(drugs = enabled) }
            }

            SectionTitle("Additional Capabilities")
            TextFieldLine("Report filename", uiState.reportFileName) { value ->
                viewModel.updateField { it.copy(reportFileName = value) }
            }
            TextFieldLine("X-ray/Diagnostic filename", uiState.diagnosticFileName) { value ->
                viewModel.updateField { it.copy(diagnosticFileName = value) }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = viewModel::saveAndShare) {
                    Text("Save + Share")
                }
                Button(onClick = viewModel::downloadReport) {
                    Text("Download Report")
                }
            }

            if (uiState.uniqueId.isNotBlank()) {
                Text("Unique ID: ${uiState.uniqueId}", color = MaterialTheme.colorScheme.primary)
            }
            if (uiState.statusMessage.isNotBlank()) {
                Text(uiState.statusMessage)
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            modifier = Modifier.padding(12.dp),
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
private fun TextFieldLine(label: String, value: String, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) }
    )
}

@Composable
private fun LifestyleSwitch(label: String, value: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label)
        Switch(checked = value, onCheckedChange = onChange)
    }
}

