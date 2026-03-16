package com.telemed.demo.feature.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telemed.demo.domain.model.SessionUser
import com.telemed.demo.domain.model.UserRole
import com.telemed.demo.domain.usecase.LoginUseCase
import com.telemed.demo.ui.responsive.ResponsiveScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val selectedRole: UserRole = UserRole.HEALTH_WORKER,
    val isLoading: Boolean = false,
    val error: String? = null,
    val loggedInUser: SessionUser? = null
)

class LoginViewModel(
    private val loginUseCase: LoginUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun updateEmail(value: String) = _uiState.update { it.copy(email = value, error = null) }

    fun updatePassword(value: String) = _uiState.update { it.copy(password = value, error = null) }

    fun updateRole(value: UserRole) = _uiState.update { it.copy(selectedRole = value, error = null) }

    fun login() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val state = _uiState.value
            val result = loginUseCase(state.email.trim(), state.password, state.selectedRole)
            _uiState.update {
                if (result.isSuccess) {
                    it.copy(isLoading = false, loggedInUser = result.getOrNull())
                } else {
                    it.copy(
                        isLoading = false,
                        error = result.exceptionOrNull()?.message ?: "Login failed"
                    )
                }
            }
        }
    }

    fun consumeNavigationEvent() {
        _uiState.update { it.copy(loggedInUser = null) }
    }
}

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: (SessionUser) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    uiState.loggedInUser?.let { user ->
        onLoginSuccess(user)
        viewModel.consumeNavigationEvent()
    }

    ResponsiveScreen(title = "Login") {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Welcome to TeleMed Demo",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(text = "Login as")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                UserRole.entries.forEach { role ->
                    FilterChip(
                        selected = uiState.selectedRole == role,
                        onClick = { viewModel.updateRole(role) },
                        label = { Text(role.name.replace("_", " ")) }
                    )
                }
            }
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.email,
                onValueChange = viewModel::updateEmail,
                label = { Text("Email") },
                singleLine = true
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.password,
                onValueChange = viewModel::updatePassword,
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true
            )
            uiState.error?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }
            Button(
                onClick = viewModel::login,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator()
                } else {
                    Text("Continue")
                }
            }
        }
    }
}

