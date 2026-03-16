package com.telemed.demo.feature.pharmacist

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telemed.demo.R
import com.telemed.demo.domain.model.*
import com.telemed.demo.domain.usecase.*
import com.telemed.demo.ui.components.*
import com.telemed.demo.ui.responsive.AppTopBar
import com.telemed.demo.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ============ ViewModel ============

class PharmacistModuleViewModel(
    private val getPatientQueueUseCase: GetPatientQueueUseCase,
    private val setConsentUseCase: SetConsentUseCase,
    private val startCallUseCase: StartCallUseCase,
    private val endCallUseCase: EndCallUseCase,
    private val getMedicinesForDispensingUseCase: GetMedicinesForDispensingUseCase,
    private val markMedicineDispensedUseCase: MarkMedicineDispensedUseCase,
    private val getDispensedStatusUseCase: GetDispensedStatusUseCase,
    private val getLatestPrescriptionUseCase: GetLatestPrescriptionUseCase
) : ViewModel() {

    private val _patientQueue = MutableStateFlow<List<PatientQueueItem>>(emptyList())
    val patientQueue: StateFlow<List<PatientQueueItem>> = _patientQueue.asStateFlow()

    private val _selectedPatient = MutableStateFlow<Patient?>(null)
    val selectedPatient: StateFlow<Patient?> = _selectedPatient.asStateFlow()

    private val _isCallActive = MutableStateFlow(false)
    val isCallActive: StateFlow<Boolean> = _isCallActive.asStateFlow()

    private val _isConnecting = MutableStateFlow(false)
    val isConnecting: StateFlow<Boolean> = _isConnecting.asStateFlow()

    private val _medicines = MutableStateFlow<List<Medicine>>(emptyList())
    val medicines: StateFlow<List<Medicine>> = _medicines.asStateFlow()

    private val _dispensedStatus = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val dispensedStatus: StateFlow<Map<String, Boolean>> = _dispensedStatus.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _prescription = MutableStateFlow<Prescription?>(null)
    val prescription: StateFlow<Prescription?> = _prescription.asStateFlow()

    init {
        loadQueue()
    }

    fun loadQueue() {
        viewModelScope.launch {
            _patientQueue.value = getPatientQueueUseCase()
        }
    }

    fun selectPatient(patient: Patient) {
        _selectedPatient.value = patient
    }

    fun setConsent(patientId: String, consent: Boolean) {
        viewModelScope.launch {
            setConsentUseCase(patientId, consent)
            loadQueue()
            if (consent) {
                _isConnecting.value = true
                delay(2000) // Simulate connecting
                startCallUseCase()
                _isConnecting.value = false
                _isCallActive.value = true
            }
        }
    }

    fun endCall() {
        viewModelScope.launch {
            endCallUseCase()
            _isCallActive.value = false
            loadPrescriptionData()
        }
    }

    fun loadPrescriptionData() {
        viewModelScope.launch {
            _medicines.value = getMedicinesForDispensingUseCase()
            _dispensedStatus.value = getDispensedStatusUseCase()
            _prescription.value = getLatestPrescriptionUseCase()
        }
    }

    fun toggleDispensed(medicineName: String) {
        viewModelScope.launch {
            val current = _dispensedStatus.value[medicineName] ?: false
            markMedicineDispensedUseCase(medicineName, !current)
            _dispensedStatus.value = getDispensedStatusUseCase()
        }
    }

    fun submitDispensation() {
        viewModelScope.launch {
            _isLoading.value = true
            delay(500) // simulate
            _isLoading.value = false
        }
    }
}

// ============ Pharmacist Login Screen ============

@Composable
fun PharmacistLoginScreen(
    onLoginSuccess: () -> Unit,
    onBack: () -> Unit
) {
    var email by remember { mutableStateOf("pharmacist@demo.com") }
    var password by remember { mutableStateOf("demo1234") }
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { AppTopBar(title = stringResource(R.string.role_pharmacist), onBack = onBack) },
        containerColor = BackgroundPage
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Surface(
                shape = CircleShape,
                color = PharmacistBg,
                modifier = Modifier.size(80.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.LocalPharmacy, contentDescription = null, tint = PharmacistColor, modifier = Modifier.size(44.dp))
                }
            }

            Text(stringResource(R.string.role_pharmacist), style = MaterialTheme.typography.headlineMedium, color = PharmacistColor)

            Spacer(modifier = Modifier.height(8.dp))

            LargeTextField(value = email, onValueChange = { email = it }, label = stringResource(R.string.email))
            LargeTextField(value = password, onValueChange = { password = it }, label = stringResource(R.string.password), isPassword = true)

            Spacer(modifier = Modifier.height(8.dp))

            LargeButton(
                text = stringResource(R.string.login),
                onClick = {
                    isLoading = true
                    onLoginSuccess()
                },
                isLoading = isLoading,
                color = PharmacistColor
            )
        }
    }
}

// ============ Patient Queue Screen ============

@Composable
fun PharmacistQueueScreen(
    viewModel: PharmacistModuleViewModel,
    onPatientSelect: (String) -> Unit,
    onBack: () -> Unit
) {
    val queue by viewModel.patientQueue.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadQueue() }

    Scaffold(
        topBar = { AppTopBar(title = stringResource(R.string.patient_queue), onBack = onBack) },
        containerColor = BackgroundPage
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Module identity chip
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = PharmacistBg,
                modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.LocalPharmacy, contentDescription = null, tint = PharmacistColor, modifier = Modifier.size(16.dp))
                    Text("Pharmacist", style = MaterialTheme.typography.labelSmall, color = PharmacistColor)
                }
            }

            if (queue.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.People, contentDescription = null, modifier = Modifier.size(64.dp), tint = TextSecondary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No patients in queue", style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(queue) { item ->
                        PatientListCard(
                            name = item.patient.fullName,
                            patientId = item.patient.id,
                            subtitle = item.time,
                            status = item.status,
                            onClick = {
                                viewModel.selectPatient(item.patient)
                                onPatientSelect(item.patient.id)
                            }
                        )
                    }
                }
            }
        }
    }
}

// ============ Consent Screen ============

@Composable
fun PharmacistConsentScreen(
    viewModel: PharmacistModuleViewModel,
    patientId: String,
    onConsentYes: () -> Unit,
    onConsentNo: () -> Unit,
    onBack: () -> Unit
) {
    val selectedPatient by viewModel.selectedPatient.collectAsState()
    val patient = selectedPatient

    Scaffold(
        topBar = { AppTopBar(title = stringResource(R.string.patient_consent), onBack = onBack) },
        containerColor = BackgroundPage
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (patient != null) {
                // Patient details card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = PharmacistBg)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(patient.fullName, style = MaterialTheme.typography.headlineSmall, color = TextPrimary)
                        Text("ID: ${patient.id}", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Age: ${patient.age} • ${patient.gender.name}", style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
                        if (patient.medicalHistory.primaryComplaint.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Complaint: ${patient.medicalHistory.primaryComplaint}", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Consent question
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = BackgroundCard),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = PharmacistBg,
                        modifier = Modifier.size(64.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.HowToReg, contentDescription = null, tint = PharmacistColor, modifier = Modifier.size(36.dp))
                        }
                    }

                    Text(
                        stringResource(R.string.consent_question),
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        color = TextPrimary
                    )

                    // Full-width stacked YES/NO buttons
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // YES button — full width
                        Button(
                            onClick = {
                                viewModel.setConsent(patientId, true)
                                onConsentYes()
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CallAcceptGreen),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.yes), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        }

                        // NO button — full width
                        Button(
                            onClick = {
                                viewModel.setConsent(patientId, false)
                                onConsentNo()
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CallDeclineRed),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Icon(Icons.Default.Cancel, contentDescription = null, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.no), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}

// ============ Video Call Screen (Stub) ============

@Composable
fun PharmacistVideoCallScreen(
    viewModel: PharmacistModuleViewModel,
    onCallEnded: () -> Unit,
    onBack: () -> Unit
) {
    val isConnecting by viewModel.isConnecting.collectAsState()
    val isCallActive by viewModel.isCallActive.collectAsState()

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Scaffold { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(CallDarkBg),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Avatar placeholder
                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.12f),
                    modifier = Modifier
                        .size(120.dp)
                        .then(if (isConnecting) Modifier.scale(pulseScale) else Modifier)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(60.dp)
                        )
                    }
                }

                Text(
                    if (isConnecting) stringResource(R.string.connecting_doctor)
                    else if (isCallActive) stringResource(R.string.call_connected)
                    else "Call Ended",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White
                )

                if (isConnecting) {
                    CircularProgressIndicator(color = Color.White, strokeWidth = 3.dp)
                }

                if (isCallActive) {
                    // Mock video area
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .height(200.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = HeaderNavyLighter
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Videocam, contentDescription = null, tint = Color.White.copy(alpha = 0.5f), modifier = Modifier.size(48.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Video Call Active", color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Call controls
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        // Mute button
                        FilledIconButton(
                            onClick = { },
                            modifier = Modifier.size(56.dp),
                            colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color.White.copy(alpha = 0.15f))
                        ) {
                            Icon(Icons.Default.MicOff, contentDescription = stringResource(R.string.mute), tint = Color.White)
                        }

                        // End call button
                        FilledIconButton(
                            onClick = {
                                viewModel.endCall()
                                onCallEnded()
                            },
                            modifier = Modifier.size(56.dp),
                            colors = IconButtonDefaults.filledIconButtonColors(containerColor = CallDeclineRed)
                        ) {
                            Icon(Icons.Default.CallEnd, contentDescription = stringResource(R.string.end_call), tint = Color.White)
                        }

                        // Camera toggle
                        FilledIconButton(
                            onClick = { },
                            modifier = Modifier.size(56.dp),
                            colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color.White.copy(alpha = 0.15f))
                        ) {
                            Icon(Icons.Default.VideocamOff, contentDescription = null, tint = Color.White)
                        }
                    }
                }
            }
        }
    }
}

// ============ Prescription Dispense Screen ============

@Composable
fun PharmacistDispenseScreen(
    viewModel: PharmacistModuleViewModel,
    onDone: () -> Unit,
    onBack: () -> Unit
) {
    val medicines by viewModel.medicines.collectAsState()
    val dispensedStatus by viewModel.dispensedStatus.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val prescription by viewModel.prescription.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadPrescriptionData() }

    Scaffold(
        topBar = { AppTopBar(title = stringResource(R.string.prescribed_medicines), onBack = onBack) },
        containerColor = BackgroundPage
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (medicines.isEmpty() && prescription == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.MedicalInformation, contentDescription = null, modifier = Modifier.size(64.dp), tint = TextSecondary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No prescription available yet.", style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
                        Text("Waiting for doctor to complete consultation.", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                    }
                }
            } else {
                // Prescription ready card at the top
                if (prescription != null) {
                    PrescriptionReadyCard()
                    Spacer(modifier = Modifier.height(12.dp))
                }

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(medicines) { medicine ->
                        val isDispensed = dispensedStatus[medicine.name] ?: false
                        PrescriptionItemCard(
                            medicineName = medicine.name,
                            details = "${medicine.dosage} • ${medicine.frequency}",
                            subDetails = "${medicine.durationDays} days • ${medicine.instructions}",
                            isDispensed = isDispensed,
                            onToggleDispensed = { viewModel.toggleDispensed(medicine.name) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                LargeButton(
                    text = stringResource(R.string.submit_dispensation),
                    onClick = {
                        viewModel.submitDispensation()
                        onDone()
                    },
                    isLoading = isLoading,
                    color = PharmacistColor,
                    icon = Icons.Default.CheckCircle
                )
            }
        }
    }
}
