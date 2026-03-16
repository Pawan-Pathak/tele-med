package com.telemed.demo.feature.doctorpool

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import com.telemed.demo.domain.model.Doctor
import com.telemed.demo.domain.usecase.ConnectDoctorUseCase
import com.telemed.demo.domain.usecase.GetDoctorPoolUseCase
import com.telemed.demo.ui.responsive.ResponsiveScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DoctorPoolUiState(
    val doctors: List<Doctor> = emptyList(),
    val selectedDoctorId: String? = null,
    val message: String? = null
)

class DoctorPoolViewModel(
    private val getDoctorPoolUseCase: GetDoctorPoolUseCase,
    private val connectDoctorUseCase: ConnectDoctorUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(DoctorPoolUiState())
    val uiState: StateFlow<DoctorPoolUiState> = _uiState.asStateFlow()

    fun loadDoctors() {
        if (_uiState.value.doctors.isNotEmpty()) return
        viewModelScope.launch {
            _uiState.update { it.copy(doctors = getDoctorPoolUseCase()) }
        }
    }

    fun selectDoctor(doctorId: String) {
        _uiState.update { it.copy(selectedDoctorId = doctorId, message = null) }
    }

    fun connect() {
        val selectedDoctorId = _uiState.value.selectedDoctorId ?: return
        viewModelScope.launch {
            val doctor = connectDoctorUseCase(selectedDoctorId)
            _uiState.update {
                it.copy(
                    message = if (doctor != null) {
                        "Connected to ${doctor.name}."
                    } else {
                        "Selected doctor is unavailable."
                    }
                )
            }
        }
    }
}

@Composable
fun DoctorPoolScreen(
    viewModel: DoctorPoolViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadDoctors()
    }

    ResponsiveScreen(title = "Doctor Pool Connect", onBack = onBack) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            uiState.doctors.forEach { doctor ->
                val isSelected = uiState.selectedDoctorId == doctor.id
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = doctor.isAvailable) { viewModel.selectDoctor(doctor.id) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(doctor.name, style = MaterialTheme.typography.titleMedium)
                            Text("${doctor.specialty} - ETA ${doctor.etaMinutes} min")
                        }
                        Text(
                            if (!doctor.isAvailable) "Busy" else if (isSelected) "Selected" else "Available",
                            color = if (doctor.isAvailable) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            uiState.message?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
            Button(onClick = viewModel::connect, modifier = Modifier.fillMaxWidth()) {
                Text("Connect doctor")
            }
        }
    }
}

