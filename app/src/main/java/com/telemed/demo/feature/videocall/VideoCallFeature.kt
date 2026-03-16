package com.telemed.demo.feature.videocall

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
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
import com.telemed.demo.domain.usecase.EndCallUseCase
import com.telemed.demo.domain.usecase.GetCallStatusUseCase
import com.telemed.demo.domain.usecase.StartCallUseCase
import com.telemed.demo.ui.responsive.ResponsiveScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class VideoCallUiState(
    val activeCall: Boolean = false,
    val statusMessage: String = "Call not started."
)

class VideoCallViewModel(
    private val startCallUseCase: StartCallUseCase,
    private val endCallUseCase: EndCallUseCase,
    private val getCallStatusUseCase: GetCallStatusUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(VideoCallUiState())
    val uiState: StateFlow<VideoCallUiState> = _uiState.asStateFlow()

    fun refresh() {
        viewModelScope.launch {
            val active = getCallStatusUseCase()
            _uiState.update {
                it.copy(
                    activeCall = active,
                    statusMessage = if (active) "Connected. Video stream placeholder active." else "Call not started."
                )
            }
        }
    }

    fun toggleCall() {
        viewModelScope.launch {
            if (_uiState.value.activeCall) {
                endCallUseCase()
                _uiState.update { it.copy(activeCall = false, statusMessage = "Call ended.") }
            } else {
                val started = startCallUseCase()
                _uiState.update {
                    it.copy(
                        activeCall = started,
                        statusMessage = if (started) {
                            "Connected. Video stream placeholder active."
                        } else {
                            "Connect a doctor first from Doctor Pool."
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun VideoCallScreen(
    viewModel: VideoCallViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    ResponsiveScreen(title = "Video Call Placeholder", onBack = onBack) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Videocam,
                contentDescription = "Video Call",
                tint = MaterialTheme.colorScheme.primary
            )
            Text(text = uiState.statusMessage, style = MaterialTheme.typography.bodyLarge)
            Button(
                onClick = viewModel::toggleCall,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (uiState.activeCall) "End call" else "Start call")
            }
        }
    }
}

