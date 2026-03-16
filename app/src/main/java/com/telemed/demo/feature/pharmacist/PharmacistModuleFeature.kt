package com.telemed.demo.feature.pharmacist

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
import com.telemed.demo.domain.model.MedicationItem
import com.telemed.demo.domain.usecase.GetPharmacistSnapshotUseCase
import com.telemed.demo.domain.usecase.InitiatePharmacistCallUseCase
import com.telemed.demo.domain.usecase.RecordDispensationUseCase
import com.telemed.demo.domain.usecase.SetPharmacistConsentUseCase
import com.telemed.demo.ui.responsive.ResponsiveScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PharmacistUiState(
    val consentGiven: Boolean? = null,
    val callStarted: Boolean = false,
    val medications: List<MedicationItem> = emptyList(),
    val dispensationNotes: String = "",
    val status: String = ""
)

class PharmacistModuleViewModel(
    private val setPharmacistConsentUseCase: SetPharmacistConsentUseCase,
    private val initiatePharmacistCallUseCase: InitiatePharmacistCallUseCase,
    private val getPharmacistSnapshotUseCase: GetPharmacistSnapshotUseCase,
    private val recordDispensationUseCase: RecordDispensationUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(PharmacistUiState())
    val uiState: StateFlow<PharmacistUiState> = _uiState.asStateFlow()

    fun load() {
        viewModelScope.launch {
            val snapshot = getPharmacistSnapshotUseCase()
            _uiState.update {
                it.copy(
                    consentGiven = snapshot.consentGiven,
                    callStarted = snapshot.callInitiated,
                    medications = snapshot.prescribedMedications,
                    dispensationNotes = snapshot.dispensationNotes,
                    status = if (snapshot.patientProfile == null) {
                        "No shared patient profile yet."
                    } else {
                        "Patient profile available for pharmacist workflow."
                    }
                )
            }
        }
    }

    fun setConsent(consent: Boolean) {
        viewModelScope.launch {
            setPharmacistConsentUseCase(consent)
            _uiState.update {
                it.copy(
                    consentGiven = consent,
                    status = if (consent) "Consent captured: YES" else "Consent captured: NO"
                )
            }
        }
    }

    fun startCall() {
        viewModelScope.launch {
            val started = initiatePharmacistCallUseCase()
            _uiState.update {
                it.copy(
                    callStarted = started,
                    status = if (started) {
                        "Video/audio call initiated with doctor."
                    } else {
                        "Cannot initiate call until consent is YES."
                    }
                )
            }
        }
    }

    fun updateDispensationNotes(value: String) {
        _uiState.update { it.copy(dispensationNotes = value) }
    }

    fun saveDispensation() {
        viewModelScope.launch {
            recordDispensationUseCase(_uiState.value.dispensationNotes)
            _uiState.update { it.copy(status = "Medication dispensation recorded.") }
        }
    }
}

@Composable
fun PharmacistModuleScreen(
    viewModel: PharmacistModuleViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.load()
    }

    ResponsiveScreen(title = "Pharmacist Module", onBack = onBack) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("Confirm patient willingness to proceed")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { viewModel.setConsent(true) }) { Text("YES") }
                Button(onClick = { viewModel.setConsent(false) }) { Text("NO") }
            }

            Button(onClick = viewModel::startCall, modifier = Modifier.fillMaxWidth()) {
                Text("Initiate video/audio call with doctor")
            }

            Text("Prescribed Medications", style = MaterialTheme.typography.titleMedium)
            if (uiState.medications.isEmpty()) {
                Text("No medications available yet.")
            } else {
                uiState.medications.forEach {
                    Text("- ${it.name}: ${it.dosage}, ${it.frequency}, ${it.time}")
                }
            }

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.dispensationNotes,
                onValueChange = viewModel::updateDispensationNotes,
                label = { Text("Dispensation notes") }
            )
            Button(onClick = viewModel::saveDispensation, modifier = Modifier.fillMaxWidth()) {
                Text("Record medication dispensation")
            }

            if (uiState.status.isNotBlank()) {
                Text(uiState.status, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

