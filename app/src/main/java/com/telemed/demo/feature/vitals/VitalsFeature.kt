package com.telemed.demo.feature.vitals

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telemed.demo.domain.model.Vitals
import com.telemed.demo.domain.usecase.SaveVitalsUseCase
import com.telemed.demo.ui.responsive.ResponsiveScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class VitalsUiState(
    val heartRate: String = "",
    val bloodPressure: String = "",
    val temperature: String = "",
    val spo2: String = "",
    val message: String? = null,
    val error: String? = null
)

class VitalsViewModel(
    private val saveVitalsUseCase: SaveVitalsUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(VitalsUiState())
    val uiState: StateFlow<VitalsUiState> = _uiState.asStateFlow()

    fun updateHeartRate(value: String) = _uiState.update { it.copy(heartRate = value, message = null, error = null) }
    fun updateBloodPressure(value: String) = _uiState.update { it.copy(bloodPressure = value, message = null, error = null) }
    fun updateTemperature(value: String) = _uiState.update { it.copy(temperature = value, message = null, error = null) }
    fun updateSpo2(value: String) = _uiState.update { it.copy(spo2 = value, message = null, error = null) }

    fun save() {
        val state = _uiState.value
        val heartRate = state.heartRate.toIntOrNull()
        val temperature = state.temperature.toDoubleOrNull()
        val spo2 = state.spo2.toIntOrNull()
        if (heartRate == null || temperature == null || spo2 == null || state.bloodPressure.isBlank()) {
            _uiState.update { it.copy(error = "Provide valid vitals values before saving.") }
            return
        }

        viewModelScope.launch {
            saveVitalsUseCase(
                Vitals(
                    heartRate = heartRate,
                    bloodPressure = state.bloodPressure.trim(),
                    temperatureC = temperature,
                    spo2 = spo2
                )
            )
            _uiState.update { it.copy(message = "Vitals captured.", error = null) }
        }
    }
}

@Composable
fun VitalsCollectionScreen(
    viewModel: VitalsViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    ResponsiveScreen(title = "Vitals Collection", onBack = onBack) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.heartRate,
                onValueChange = viewModel::updateHeartRate,
                label = { Text("Heart rate (bpm)") },
                singleLine = true
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.bloodPressure,
                onValueChange = viewModel::updateBloodPressure,
                label = { Text("Blood pressure (e.g. 120/80)") },
                singleLine = true
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.temperature,
                onValueChange = viewModel::updateTemperature,
                label = { Text("Temperature (C)") },
                singleLine = true
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.spo2,
                onValueChange = viewModel::updateSpo2,
                label = { Text("SpO2 (%)") },
                singleLine = true
            )
            uiState.message?.let {
                Text(text = it, color = MaterialTheme.colorScheme.primary)
            }
            uiState.error?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }
            Button(onClick = viewModel::save, modifier = Modifier.fillMaxWidth()) {
                Text("Save vitals")
            }
        }
    }
}

