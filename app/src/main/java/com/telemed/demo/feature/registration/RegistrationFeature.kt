package com.telemed.demo.feature.registration

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
import com.telemed.demo.domain.model.Patient
import com.telemed.demo.domain.usecase.RegisterPatientUseCase
import com.telemed.demo.ui.responsive.ResponsiveScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class RegistrationUiState(
    val fullName: String = "",
    val age: String = "",
    val gender: String = "",
    val phone: String = "",
    val confirmation: String? = null,
    val error: String? = null
)

class RegistrationViewModel(
    private val registerPatientUseCase: RegisterPatientUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(RegistrationUiState())
    val uiState: StateFlow<RegistrationUiState> = _uiState.asStateFlow()

    fun updateName(value: String) = _uiState.update { it.copy(fullName = value, confirmation = null, error = null) }
    fun updateAge(value: String) = _uiState.update { it.copy(age = value, confirmation = null, error = null) }
    fun updateGender(value: String) = _uiState.update { it.copy(gender = value, confirmation = null, error = null) }
    fun updatePhone(value: String) = _uiState.update { it.copy(phone = value, confirmation = null, error = null) }

    fun register() {
        val state = _uiState.value
        val ageValue = state.age.toIntOrNull()
        if (state.fullName.isBlank() || ageValue == null || state.gender.isBlank() || state.phone.isBlank()) {
            _uiState.update { it.copy(error = "Please complete all fields with valid values.") }
            return
        }

        viewModelScope.launch {
            val patient = Patient(
                id = UUID.randomUUID().toString(),
                fullName = state.fullName.trim(),
                age = ageValue,
                gender = state.gender.trim(),
                phone = state.phone.trim()
            )
            registerPatientUseCase(patient)
            _uiState.update { it.copy(confirmation = "Patient registered successfully.", error = null) }
        }
    }
}

@Composable
fun RegistrationScreen(
    viewModel: RegistrationViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    ResponsiveScreen(title = "Patient Registration", onBack = onBack) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.fullName,
                onValueChange = viewModel::updateName,
                label = { Text("Full name") },
                singleLine = true
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.age,
                onValueChange = viewModel::updateAge,
                label = { Text("Age") },
                singleLine = true
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.gender,
                onValueChange = viewModel::updateGender,
                label = { Text("Gender") },
                singleLine = true
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.phone,
                onValueChange = viewModel::updatePhone,
                label = { Text("Phone") },
                singleLine = true
            )
            uiState.confirmation?.let {
                Text(text = it, color = MaterialTheme.colorScheme.primary)
            }
            uiState.error?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }
            Button(
                onClick = viewModel::register,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save patient")
            }
        }
    }
}

