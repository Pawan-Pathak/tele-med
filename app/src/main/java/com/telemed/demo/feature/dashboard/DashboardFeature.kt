package com.telemed.demo.feature.dashboard

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
import com.telemed.demo.domain.model.DashboardData
import com.telemed.demo.domain.model.SessionUser
import com.telemed.demo.domain.model.UserRole
import com.telemed.demo.domain.usecase.GetCurrentSessionUseCase
import com.telemed.demo.domain.usecase.GetDashboardUseCase
import com.telemed.demo.navigation.AppDestination
import com.telemed.demo.ui.components.InfoCard
import com.telemed.demo.ui.responsive.ResponsiveScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DashboardUiState(
    val data: DashboardData? = null,
    val sessionUser: SessionUser? = null,
    val isLoading: Boolean = false
)

class DashboardViewModel(
    private val getDashboardUseCase: GetDashboardUseCase,
    private val getCurrentSessionUseCase: GetCurrentSessionUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    fun loadDashboard() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val data = getDashboardUseCase()
            val session = getCurrentSessionUseCase()
            _uiState.update { it.copy(isLoading = false, data = data, sessionUser = session) }
        }
    }
}

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigate: (AppDestination) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadDashboard()
    }

    ResponsiveScreen(title = "Dashboard") {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (uiState.isLoading) {
                Text("Loading dashboard...", style = MaterialTheme.typography.bodyLarge)
            }
            InfoCard(
                title = "Logged in as",
                subtitle = uiState.sessionUser?.let {
                    "${it.displayName} (${it.role.name.replace("_", " ")})"
                } ?: "Guest"
            )
            InfoCard(
                title = uiState.data?.patientName ?: "Guest Patient",
                subtitle = "Select a module below to continue workflow."
            )
            InfoCard(
                title = "Vitals",
                subtitle = uiState.data?.lastVitals?.let {
                    "HR ${it.heartRate}, BP ${it.bloodPressure}, Temp ${it.temperatureC} C, SpO2 ${it.spo2}%"
                } ?: "No vitals captured yet"
            )
            InfoCard(
                title = "Doctor",
                subtitle = uiState.data?.connectedDoctor?.let {
                    "${it.name} (${it.specialty})"
                } ?: "No doctor connected"
            )
            InfoCard(
                title = "Consultation",
                subtitle = if (uiState.data?.hasActiveCall == true) "Active call in progress" else "No active call"
            )
            InfoCard(
                title = "Prescription",
                subtitle = uiState.data?.latestPrescription?.notes ?: "No prescription generated"
            )

            when (uiState.sessionUser?.role) {
                UserRole.HEALTH_WORKER -> {
                    Button(onClick = { onNavigate(AppDestination.HealthWorkerModule) }, modifier = Modifier.fillMaxWidth()) {
                        Text("Open Health Worker Module")
                    }
                }
                UserRole.PHARMACIST -> {
                    Button(onClick = { onNavigate(AppDestination.PharmacistModule) }, modifier = Modifier.fillMaxWidth()) {
                        Text("Open Pharmacist Module")
                    }
                }
                UserRole.DOCTOR -> {
                    Button(onClick = { onNavigate(AppDestination.DoctorModule) }, modifier = Modifier.fillMaxWidth()) {
                        Text("Open Doctor Module")
                    }
                }
                null -> {
                    Text("Please login again to access role-specific modules.")
                }
            }

            Button(onClick = { onNavigate(AppDestination.Registration) }, modifier = Modifier.fillMaxWidth()) {
                Text("Legacy: Patient Registration")
            }
            Button(onClick = { onNavigate(AppDestination.Vitals) }, modifier = Modifier.fillMaxWidth()) {
                Text("Legacy: Vitals Collection")
            }
            Button(onClick = { onNavigate(AppDestination.DoctorPool) }, modifier = Modifier.fillMaxWidth()) {
                Text("Legacy: Doctor Pool Connect")
            }
            Button(onClick = { onNavigate(AppDestination.VideoCall) }, modifier = Modifier.fillMaxWidth()) {
                Text("Legacy: Video Call Placeholder")
            }
            Button(onClick = { onNavigate(AppDestination.Prescription) }, modifier = Modifier.fillMaxWidth()) {
                Text("Legacy: Prescription Summary")
            }
            Button(onClick = viewModel::loadDashboard, modifier = Modifier.fillMaxWidth()) {
                Text("Refresh Dashboard")
            }
        }
    }
}

