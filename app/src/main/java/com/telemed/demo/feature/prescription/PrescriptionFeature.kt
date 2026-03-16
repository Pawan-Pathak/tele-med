package com.telemed.demo.feature.prescription

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telemed.demo.domain.model.Prescription
import com.telemed.demo.domain.usecase.GeneratePrescriptionUseCase
import com.telemed.demo.domain.usecase.GetPrescriptionUseCase
import com.telemed.demo.ui.components.LabeledValue
import com.telemed.demo.ui.responsive.ResponsiveScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PrescriptionUiState(
    val prescription: Prescription? = null,
    val message: String = "No prescription generated yet."
)

class PrescriptionSummaryViewModel(
    private val getPrescriptionUseCase: GetPrescriptionUseCase,
    private val generatePrescriptionUseCase: GeneratePrescriptionUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(PrescriptionUiState())
    val uiState: StateFlow<PrescriptionUiState> = _uiState.asStateFlow()

    fun load() {
        viewModelScope.launch {
            val existing = getPrescriptionUseCase()
            _uiState.update {
                it.copy(
                    prescription = existing,
                    message = if (existing != null) "Latest prescription available." else "No prescription generated yet."
                )
            }
        }
    }

    fun generate() {
        viewModelScope.launch {
            val generated = generatePrescriptionUseCase()
            _uiState.update {
                if (generated != null) {
                    it.copy(
                        prescription = generated,
                        message = "Prescription generated for ${generated.patientName}."
                    )
                } else {
                    it.copy(message = "Register patient and connect doctor before generating.")
                }
            }
        }
    }
}

@Composable
fun PrescriptionSummaryScreen(
    viewModel: PrescriptionSummaryViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.load()
    }

    ResponsiveScreen(title = "Prescription Summary", onBack = onBack) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = uiState.message, style = MaterialTheme.typography.bodyLarge)
            uiState.prescription?.let { prescription ->
                LabeledValue(label = "Prescription ID", value = prescription.id.take(8))
                LabeledValue(label = "Patient", value = prescription.patientName)
                LabeledValue(label = "Doctor", value = prescription.doctorName)
                LabeledValue(label = "Medicines", value = prescription.medications.joinToString())
                LabeledValue(label = "Notes", value = prescription.notes)
            }
            Button(
                onClick = viewModel::generate,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Generate prescription")
            }
        }
    }
}

