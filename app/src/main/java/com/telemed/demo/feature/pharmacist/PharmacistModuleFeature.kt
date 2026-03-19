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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telemed.demo.R
import com.telemed.demo.domain.model.*
import com.telemed.demo.domain.usecase.*
import com.telemed.demo.ui.components.*
import com.telemed.demo.ui.responsive.AppTopBar
import com.telemed.demo.ui.theme.*
import com.telemed.demo.feature.healthworker.DoctorAvatarImage
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
                delay(2000)
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
            delay(500)
            _isLoading.value = false
        }
    }

    // Dispensation status helpers
    fun getDispensationStatus(): String {
        val meds = _medicines.value
        val status = _dispensedStatus.value
        if (meds.isEmpty()) return "No Prescription"
        val dispensedCount = meds.count { status[it.name] == true }
        return when {
            dispensedCount == 0 -> "Not Dispensed"
            dispensedCount < meds.size -> "Partial ($dispensedCount/${meds.size})"
            else -> "Fully Dispensed"
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
                            id = item.patient.id,
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
    onViewPrescription: () -> Unit,
    onDispenseMedicines: () -> Unit,
    onBack: () -> Unit
) {
    val selectedPatient by viewModel.selectedPatient.collectAsState()
    val prescription by viewModel.prescription.collectAsState()
    val patient = selectedPatient

    LaunchedEffect(Unit) { viewModel.loadPrescriptionData() }

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
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = PharmacistBg)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(patient.fullName, style = MaterialTheme.typography.headlineSmall, color = TextPrimary)
                        Text("ID: ${patient.id}", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Age: ${patient.age} \u2022 ${patient.gender.name}", style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
                        if (patient.medicalHistory.primaryComplaint.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Complaint: ${patient.medicalHistory.primaryComplaint}", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                        }
                    }
                }
            }

            // Show prescription actions if prescription exists
            if (prescription != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = StatusDoneBg),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusDoneText, modifier = Modifier.size(22.dp))
                            Text("Prescription Available", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold), color = StatusDoneText)
                        }
                        Text("${prescription!!.doctorName} \u2022 ${prescription!!.clinicName}", style = MaterialTheme.typography.bodySmall, color = StatusDoneText.copy(alpha = 0.8f))
                        Text("${prescription!!.medicines.size} medicines prescribed \u2022 ${prescription!!.date}", style = MaterialTheme.typography.bodySmall, color = StatusDoneText.copy(alpha = 0.8f))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedButton(
                                onClick = onViewPrescription,
                                modifier = Modifier.weight(1f).height(44.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Description, contentDescription = null, tint = PharmacistColor, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("View Rx", color = PharmacistColor, style = MaterialTheme.typography.labelLarge)
                            }
                            Button(
                                onClick = onDispenseMedicines,
                                modifier = Modifier.weight(1f).height(44.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PharmacistColor)
                            ) {
                                Icon(Icons.Default.LocalPharmacy, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Dispense", style = MaterialTheme.typography.labelLarge)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

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
                    Surface(shape = CircleShape, color = PharmacistBg, modifier = Modifier.size(64.dp)) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.HowToReg, contentDescription = null, tint = PharmacistColor, modifier = Modifier.size(36.dp))
                        }
                    }

                    Text(stringResource(R.string.consent_question), style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center, color = TextPrimary)

                    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(
                            onClick = { viewModel.setConsent(patientId, true); onConsentYes() },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CallAcceptGreen),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.yes), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        }
                        Button(
                            onClick = { viewModel.setConsent(patientId, false); onConsentNo() },
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

// ============ Video Call Screen ============

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
        initialValue = 1f, targetValue = 1.3f,
        animationSpec = infiniteRepeatable(animation = tween(800, easing = FastOutSlowInEasing), repeatMode = RepeatMode.Reverse),
        label = "pulse"
    )

    Scaffold { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding).background(CallDarkBg),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(24.dp)) {
                DoctorAvatarImage(
                    doctorName = "Dr. Priya Sharma",
                    modifier = Modifier.size(120.dp).then(if (isConnecting) Modifier.scale(pulseScale) else Modifier)
                )

                Text(
                    if (isConnecting) stringResource(R.string.connecting_doctor)
                    else if (isCallActive) stringResource(R.string.call_connected)
                    else "Call Ended",
                    style = MaterialTheme.typography.headlineMedium, color = Color.White
                )

                if (isConnecting) {
                    CircularProgressIndicator(color = Color.White, strokeWidth = 3.dp)
                }

                if (isCallActive) {
                    Surface(modifier = Modifier.fillMaxWidth(0.85f).height(200.dp), shape = RoundedCornerShape(16.dp), color = HeaderNavyLighter) {
                        Box(contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                DoctorAvatarImage(doctorName = "Dr. Priya Sharma", modifier = Modifier.size(72.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Video Call Active", color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                        Box(modifier = Modifier.fillMaxSize().padding(12.dp), contentAlignment = Alignment.BottomEnd) {
                            Surface(modifier = Modifier.size(width = 80.dp, height = 100.dp), shape = RoundedCornerShape(8.dp), color = Color.DarkGray) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = Color.White.copy(alpha = 0.5f), modifier = Modifier.size(32.dp))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                        FilledIconButton(onClick = { }, modifier = Modifier.size(56.dp), colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color.White.copy(alpha = 0.15f))) {
                            Icon(Icons.Default.MicOff, contentDescription = stringResource(R.string.mute), tint = Color.White)
                        }
                        FilledIconButton(onClick = { viewModel.endCall(); onCallEnded() }, modifier = Modifier.size(56.dp), colors = IconButtonDefaults.filledIconButtonColors(containerColor = CallDeclineRed)) {
                            Icon(Icons.Default.CallEnd, contentDescription = stringResource(R.string.end_call), tint = Color.White)
                        }
                        FilledIconButton(onClick = { }, modifier = Modifier.size(56.dp), colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color.White.copy(alpha = 0.15f))) {
                            Icon(Icons.Default.VideocamOff, contentDescription = null, tint = Color.White)
                        }
                    }
                }
            }
        }
    }
}

// ============ Prescription View Screen (full prescription details) ============

@Composable
fun PharmacistPrescriptionViewScreen(
    viewModel: PharmacistModuleViewModel,
    onDispenseMedicines: () -> Unit,
    onBack: () -> Unit
) {
    val prescription by viewModel.prescription.collectAsState()
    val selectedPatient by viewModel.selectedPatient.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadPrescriptionData() }

    Scaffold(
        topBar = { AppTopBar(title = "Prescription", onBack = onBack) },
        containerColor = BackgroundPage
    ) { padding ->
        val rx = prescription
        if (rx == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.MedicalInformation, contentDescription = null, modifier = Modifier.size(64.dp), tint = TextSecondary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No prescription available yet.", style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
                    Text("Waiting for doctor to complete consultation.", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                }
            }
        } else {
            Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Prescription header card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = PharmacistBg)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Surface(shape = CircleShape, color = PharmacistColor.copy(alpha = 0.15f), modifier = Modifier.size(48.dp)) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Description, contentDescription = null, tint = PharmacistColor, modifier = Modifier.size(26.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(rx.clinicName, style = MaterialTheme.typography.titleLarge, color = PharmacistColor)
                            Text(rx.doctorName, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                            Text(rx.doctorQualification, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                            Text("Reg: ${rx.regNumber}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = DividerColor)
                            Text("Date: ${rx.date}", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Patient info section
                    SectionHeader("Patient Information", moduleColor = PharmacistColor)
                    PatientSummaryCard("Name", rx.patientName)
                    PatientSummaryCard("ID", rx.patientId)
                    PatientSummaryCard("Age/Gender", "${rx.patientAge}y / ${rx.patientGender}")

                    // Vitals from selected patient
                    selectedPatient?.let { patient ->
                        patient.vitals.let { v ->
                            if (v.weight != null || v.bpSystolic != null || v.spo2 != null) {
                                Spacer(modifier = Modifier.height(4.dp))
                                SectionHeader("Vitals", moduleColor = PharmacistColor)
                                if (v.weight != null) PatientSummaryCard("Weight", "${v.weight} kg")
                                if (v.temperature != null) PatientSummaryCard("Temp", "${v.temperature}${v.temperatureUnit}")
                                if (v.bpSystolic != null) PatientSummaryCard("BP", "${v.bpSystolic}/${v.bpDiastolic} mmHg")
                                if (v.spo2 != null) PatientSummaryCard("SpO2", "${v.spo2}%")
                                if (v.pulseRate != null) PatientSummaryCard("Pulse", "${v.pulseRate} bpm")
                            }
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = DividerColor)

                    // Diagnosis
                    SectionHeader("Diagnosis", moduleColor = PharmacistColor)
                    Text(rx.diagnosis.ifEmpty { "\u2014" }, style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
                    if (rx.chiefComplaints.isNotBlank()) {
                        Text("Chief Complaints: ${rx.chiefComplaints}", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                    }

                    if (rx.allergies.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = StatusAlertBg)
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = StatusAlertText, modifier = Modifier.size(20.dp))
                                Column {
                                    Text("ALLERGIES", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = StatusAlertText)
                                    Text(rx.allergies, style = MaterialTheme.typography.bodyMedium, color = StatusAlertText)
                                }
                            }
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = DividerColor)

                    // Rx - Medicines
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Rx", style = MaterialTheme.typography.headlineLarge, color = PharmacistColor)
                        Surface(shape = RoundedCornerShape(20.dp), color = PharmacistBg) {
                            Text("${rx.medicines.size} medicines", style = MaterialTheme.typography.labelSmall, color = PharmacistColor, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))

                    rx.medicines.forEachIndexed { idx, med ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = BackgroundCard),
                            elevation = CardDefaults.cardElevation(1.dp)
                        ) {
                            Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = PharmacistBg,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text("${idx + 1}", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = PharmacistColor)
                                    }
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(med.name, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold), color = TextPrimary)
                                    Text("${med.dosage} \u2022 ${med.frequency}", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                                    Text("${med.durationDays} days", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                                    if (med.instructions.isNotBlank()) {
                                        Text(med.instructions, style = MaterialTheme.typography.bodySmall, color = PharmacistColor)
                                    }
                                }
                                if (med.durationDays > 0) {
                                    Surface(shape = RoundedCornerShape(20.dp), color = StatusAwaitingBg) {
                                        Text("${med.durationDays}d", style = MaterialTheme.typography.labelSmall, color = StatusAwaitingText, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    // Lab tests
                    if (rx.labTests.isNotEmpty()) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = DividerColor)
                        SectionHeader("Lab Tests Advised", moduleColor = PharmacistColor)
                        rx.labTests.forEach { test ->
                            Row(modifier = Modifier.padding(vertical = 2.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.Science, contentDescription = null, tint = PharmacistColor, modifier = Modifier.size(16.dp))
                                Text(test, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                            }
                        }
                    }

                    // Recommendations
                    if (rx.recommendations.isNotBlank()) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = DividerColor)
                        SectionHeader("Recommendations", moduleColor = PharmacistColor)
                        Text(rx.recommendations, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                    }

                    // Referral
                    if (rx.referral != null) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = DividerColor)
                        SectionHeader("Referral", moduleColor = PharmacistColor)
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = StatusPendingBg)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Specialty: ${rx.referral.specialty}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = StatusPendingText)
                                Text("Reason: ${rx.referral.reason}", style = MaterialTheme.typography.bodyMedium, color = StatusPendingText)
                            }
                        }
                    }

                    // Procedures
                    if (rx.procedures.isNotBlank()) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = DividerColor)
                        SectionHeader("Procedures", moduleColor = PharmacistColor)
                        Text(rx.procedures, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                    }

                    // Imaging notes
                    if (rx.imagingNotes.isNotBlank()) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = DividerColor)
                        SectionHeader("Imaging Notes", moduleColor = PharmacistColor)
                        Text(rx.imagingNotes, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                    }

                    // Digital signature
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = DividerColor)
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .background(BackgroundPage, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(rx.doctorName, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = TextPrimary)
                            Text(stringResource(R.string.digital_signature), style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Bottom actions
                Surface(modifier = Modifier.fillMaxWidth(), color = BackgroundCard, shadowElevation = 8.dp) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Share options
                        var showShareOptions by remember { mutableStateOf(false) }
                        var shareConfirmMessage by remember { mutableStateOf("") }

                        if (shareConfirmMessage.isNotEmpty()) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = StatusDoneBg)
                            ) {
                                Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusDoneText, modifier = Modifier.size(18.dp))
                                    Text(shareConfirmMessage, style = MaterialTheme.typography.bodySmall, color = StatusDoneText, modifier = Modifier.weight(1f))
                                    IconButton(onClick = { shareConfirmMessage = "" }, modifier = Modifier.size(22.dp)) {
                                        Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = StatusDoneText, modifier = Modifier.size(14.dp))
                                    }
                                }
                            }
                        }

                        if (showShareOptions) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = BackgroundPage)
                            ) {
                                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text("Share via", style = MaterialTheme.typography.labelMedium, color = TextPrimary)
                                    ShareOptionRow(label = "WhatsApp", subtitle = "Share PDF", iconTint = WhatsAppGreen, icon = Icons.Default.Chat, bgColor = Color(0xFFE7F5E7)) {
                                        showShareOptions = false; shareConfirmMessage = "Shared via WhatsApp!"
                                    }
                                    ShareOptionRow(label = "SMS", subtitle = "Send summary", iconTint = Color(0xFF1A73E8), icon = Icons.Default.Message, bgColor = Color(0xFFE3F0FD)) {
                                        showShareOptions = false; shareConfirmMessage = "Shared via SMS!"
                                    }
                                    ShareOptionRow(label = "MMS", subtitle = "Picture message", iconTint = MMSBlue, icon = Icons.Default.Mms, bgColor = Color(0xFFE0ECFF)) {
                                        showShareOptions = false; shareConfirmMessage = "Shared via MMS!"
                                    }
                                }
                            }
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedButton(
                                onClick = { showShareOptions = !showShareOptions },
                                modifier = Modifier.weight(1f).height(52.dp),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, tint = PharmacistColor, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Share", color = PharmacistColor, style = MaterialTheme.typography.titleSmall)
                            }
                            Button(
                                onClick = onDispenseMedicines,
                                modifier = Modifier.weight(1f).height(52.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PharmacistColor)
                            ) {
                                Icon(Icons.Default.LocalPharmacy, contentDescription = null, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Dispense", style = MaterialTheme.typography.titleSmall)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ============ Medicine Dispensation Screen ============

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
    val selectedPatient by viewModel.selectedPatient.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadPrescriptionData() }

    // Dispensation calculations
    val dispensedCount = medicines.count { dispensedStatus[it.name] == true }
    val totalCount = medicines.size
    val progress = if (totalCount > 0) dispensedCount.toFloat() / totalCount else 0f
    val dispensationLabel = when {
        totalCount == 0 -> "No Prescription"
        dispensedCount == 0 -> "Not Dispensed"
        dispensedCount < totalCount -> "Partially Dispensed ($dispensedCount/$totalCount)"
        else -> "Fully Dispensed"
    }
    val dispensationColor = when {
        totalCount == 0 -> TextMuted
        dispensedCount == 0 -> StatusAlertText
        dispensedCount < totalCount -> StatusAwaitingText
        else -> StatusDoneText
    }
    val dispensationBg = when {
        totalCount == 0 -> BackgroundPage
        dispensedCount == 0 -> StatusAlertBg
        dispensedCount < totalCount -> StatusAwaitingBg
        else -> StatusDoneBg
    }

    var showSubmitConfirm by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { AppTopBar(title = "Dispense Medicines", onBack = onBack) },
        containerColor = BackgroundPage
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (medicines.isEmpty() && prescription == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.MedicalInformation, contentDescription = null, modifier = Modifier.size(64.dp), tint = TextSecondary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No prescription available yet.", style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
                    }
                }
            } else {
                // Patient header bar
                selectedPatient?.let { patient ->
                    Surface(modifier = Modifier.fillMaxWidth(), color = PharmacistBg) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            val initials = patient.fullName.split(" ").take(2).joinToString("") { it.first().uppercase() }
                            Surface(shape = CircleShape, color = PharmacistColor.copy(alpha = 0.15f), modifier = Modifier.size(36.dp)) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(initials, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = PharmacistColor)
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(patient.fullName, style = MaterialTheme.typography.titleSmall, color = TextPrimary)
                                Text("${patient.age}y \u2022 ${patient.gender.name} \u2022 ${patient.id}", style = MaterialTheme.typography.bodySmall, color = TextSecondary, maxLines = 1)
                            }
                            prescription?.let {
                                Text("Dr. ${it.doctorName.removePrefix("Dr. ")}", style = MaterialTheme.typography.bodySmall, color = PharmacistColor)
                            }
                        }
                    }
                }

                // Dispensation progress card
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = dispensationBg)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                when {
                                    dispensedCount == 0 -> Icons.Default.Warning
                                    dispensedCount < totalCount -> Icons.Default.Pending
                                    else -> Icons.Default.CheckCircle
                                },
                                contentDescription = null,
                                tint = dispensationColor,
                                modifier = Modifier.size(22.dp)
                            )
                            Text(dispensationLabel, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold), color = dispensationColor, modifier = Modifier.weight(1f))
                            Text("$dispensedCount/$totalCount", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = dispensationColor)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                            color = dispensationColor,
                            trackColor = dispensationColor.copy(alpha = 0.15f)
                        )
                    }
                }

                // Mark all button
                if (totalCount > 0) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        if (dispensedCount < totalCount) {
                            TextButton(onClick = {
                                medicines.forEach { med ->
                                    if (dispensedStatus[med.name] != true) viewModel.toggleDispensed(med.name)
                                }
                            }) {
                                Icon(Icons.Default.DoneAll, contentDescription = null, tint = PharmacistColor, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Mark All Dispensed", color = PharmacistColor, style = MaterialTheme.typography.labelMedium)
                            }
                        } else {
                            TextButton(onClick = {
                                medicines.forEach { med ->
                                    if (dispensedStatus[med.name] == true) viewModel.toggleDispensed(med.name)
                                }
                            }) {
                                Icon(Icons.Default.Undo, contentDescription = null, tint = TextMuted, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Unmark All", color = TextMuted, style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }
                }

                // Medicines list with dispensation controls
                LazyColumn(
                    modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(medicines) { medicine ->
                        val isDispensed = dispensedStatus[medicine.name] ?: false
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isDispensed) StatusDoneBg else BackgroundCard
                            ),
                            elevation = CardDefaults.cardElevation(if (isDispensed) 0.dp else 1.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Checkbox(
                                    checked = isDispensed,
                                    onCheckedChange = { viewModel.toggleDispensed(medicine.name) },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = StatusDoneText,
                                        uncheckedColor = PharmacistColor
                                    )
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        medicine.name,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                                        color = if (isDispensed) StatusDoneText else TextPrimary
                                    )
                                    Text(
                                        "${medicine.dosage} \u2022 ${medicine.frequency}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (isDispensed) StatusDoneText.copy(alpha = 0.7f) else TextSecondary
                                    )
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text(
                                            "${medicine.durationDays} days",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TextMuted
                                        )
                                        if (medicine.instructions.isNotBlank()) {
                                            Text(
                                                "\u2022 ${medicine.instructions}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = PharmacistColor
                                            )
                                        }
                                    }
                                }
                                if (isDispensed) {
                                    Surface(shape = RoundedCornerShape(20.dp), color = StatusDoneText.copy(alpha = 0.12f)) {
                                        Text("DISPENSED", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = StatusDoneText, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                                    }
                                }
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(8.dp)) }
                }

                // Submit confirmation dialog
                if (showSubmitConfirm) {
                    AlertDialog(
                        onDismissRequest = { showSubmitConfirm = false },
                        icon = {
                            Icon(
                                if (dispensedCount == totalCount) Icons.Default.CheckCircle else Icons.Default.Warning,
                                contentDescription = null,
                                tint = if (dispensedCount == totalCount) StatusDoneText else StatusAwaitingText,
                                modifier = Modifier.size(40.dp)
                            )
                        },
                        title = { Text("Confirm Dispensation") },
                        text = {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("$dispensedCount of $totalCount medicines have been marked as dispensed.")
                                if (dispensedCount < totalCount) {
                                    val undispensed = medicines.filter { dispensedStatus[it.name] != true }
                                    Text("Not dispensed:", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold), color = StatusAlertText)
                                    undispensed.forEach { med ->
                                        Text("\u2022 ${med.name}", style = MaterialTheme.typography.bodySmall, color = StatusAlertText)
                                    }
                                }
                            }
                        },
                        confirmButton = {
                            Button(
                                onClick = { showSubmitConfirm = false; viewModel.submitDispensation(); onDone() },
                                colors = ButtonDefaults.buttonColors(containerColor = PharmacistColor)
                            ) {
                                Text("Submit")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showSubmitConfirm = false }) {
                                Text("Cancel")
                            }
                        }
                    )
                }

                // Bottom action bar
                Surface(modifier = Modifier.fillMaxWidth(), color = BackgroundCard, shadowElevation = 8.dp) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Dispensation summary
                        if (dispensedCount > 0 && dispensedCount < totalCount) {
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = StatusAwaitingBg)
                            ) {
                                Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(Icons.Default.Info, contentDescription = null, tint = StatusAwaitingText, modifier = Modifier.size(16.dp))
                                    Text("${totalCount - dispensedCount} medicine(s) still not dispensed", style = MaterialTheme.typography.bodySmall, color = StatusAwaitingText)
                                }
                            }
                        }

                        LargeButton(
                            text = if (dispensedCount == totalCount) "Submit - Fully Dispensed" else "Submit Dispensation ($dispensedCount/$totalCount)",
                            onClick = { showSubmitConfirm = true },
                            isLoading = isLoading,
                            color = if (dispensedCount == totalCount) StatusDoneText else PharmacistColor,
                            icon = if (dispensedCount == totalCount) Icons.Default.CheckCircle else Icons.Default.LocalPharmacy,
                            enabled = true
                        )
                    }
                }
            }
        }
    }
}

// ============ Share Option Row (reusable) ============

@Composable
private fun ShareOptionRow(
    label: String,
    subtitle: String,
    iconTint: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    bgColor: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(shape = CircleShape, color = iconTint, modifier = Modifier.size(36.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(label, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold), color = iconTint)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted, modifier = Modifier.size(18.dp))
        }
    }
}
