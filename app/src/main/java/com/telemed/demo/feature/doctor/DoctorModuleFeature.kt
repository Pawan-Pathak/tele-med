package com.telemed.demo.feature.doctor

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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telemed.demo.R
import com.telemed.demo.domain.model.*
import com.telemed.demo.domain.repository.MockDataRepository
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

class DoctorModuleViewModel(
    private val getPatientQueueUseCase: GetPatientQueueUseCase,
    private val getDoctorsUseCase: GetDoctorsUseCase,
    private val saveDoctorConsultationUseCase: SaveDoctorConsultationUseCase,
    private val generatePrescriptionUseCase: GeneratePrescriptionUseCase,
    private val startCallUseCase: StartCallUseCase,
    private val endCallUseCase: EndCallUseCase,
    private val mockDataRepository: MockDataRepository
) : ViewModel() {

    private val _patientQueue = MutableStateFlow<List<PatientQueueItem>>(emptyList())
    val patientQueue: StateFlow<List<PatientQueueItem>> = _patientQueue.asStateFlow()

    private val _selectedPatient = MutableStateFlow<Patient?>(null)
    val selectedPatient: StateFlow<Patient?> = _selectedPatient.asStateFlow()

    private val _currentDoctor = MutableStateFlow<Doctor?>(null)
    val currentDoctor: StateFlow<Doctor?> = _currentDoctor.asStateFlow()

    // Consultation form fields
    private val _chiefComplaints = MutableStateFlow("")
    val chiefComplaints: StateFlow<String> = _chiefComplaints.asStateFlow()
    private val _diagnosis = MutableStateFlow("")
    val diagnosis: StateFlow<String> = _diagnosis.asStateFlow()
    private val _icdCode = MutableStateFlow("")
    val icdCode: StateFlow<String> = _icdCode.asStateFlow()
    private val _medicines = MutableStateFlow<List<Medicine>>(listOf(Medicine()))
    val medicines: StateFlow<List<Medicine>> = _medicines.asStateFlow()
    private val _selectedLabTests = MutableStateFlow<Set<String>>(emptySet())
    val selectedLabTests: StateFlow<Set<String>> = _selectedLabTests.asStateFlow()
    private val _imagingNotes = MutableStateFlow("")
    val imagingNotes: StateFlow<String> = _imagingNotes.asStateFlow()
    private val _procedures = MutableStateFlow("")
    val procedures: StateFlow<String> = _procedures.asStateFlow()
    private val _allergies = MutableStateFlow("")
    val allergies: StateFlow<String> = _allergies.asStateFlow()
    private val _recommendations = MutableStateFlow("")
    val recommendations: StateFlow<String> = _recommendations.asStateFlow()
    private val _referralNeeded = MutableStateFlow(false)
    val referralNeeded: StateFlow<Boolean> = _referralNeeded.asStateFlow()
    private val _referralSpecialty = MutableStateFlow("")
    val referralSpecialty: StateFlow<String> = _referralSpecialty.asStateFlow()
    private val _referralReason = MutableStateFlow("")
    val referralReason: StateFlow<String> = _referralReason.asStateFlow()

    private val _prescription = MutableStateFlow<Prescription?>(null)
    val prescription: StateFlow<Prescription?> = _prescription.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Mock data
    val drugList = mockDataRepository.getDrugList()
    val icd10List = mockDataRepository.getICD10List()
    val labTests = mockDataRepository.getLabTests()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _patientQueue.value = getPatientQueueUseCase()
            val doctors = getDoctorsUseCase()
            _currentDoctor.value = doctors.firstOrNull()
        }
    }

    fun refreshQueue() {
        viewModelScope.launch { _patientQueue.value = getPatientQueueUseCase() }
    }

    fun selectPatient(patient: Patient) {
        _selectedPatient.value = patient
        _chiefComplaints.value = patient.medicalHistory.primaryComplaint
    }

    fun acceptCall(patientId: String) {
        viewModelScope.launch {
            startCallUseCase()
        }
    }

    fun declineCall() {
        // Return to queue
    }

    // Form setters
    fun setChiefComplaints(v: String) { _chiefComplaints.value = v }
    fun setDiagnosis(v: String) { _diagnosis.value = v }
    fun setIcdCode(v: String) { _icdCode.value = v }
    fun setImagingNotes(v: String) { _imagingNotes.value = v }
    fun setProcedures(v: String) { _procedures.value = v }
    fun setAllergies(v: String) { _allergies.value = v }
    fun setRecommendations(v: String) { _recommendations.value = v }
    fun setReferralNeeded(v: Boolean) { _referralNeeded.value = v }
    fun setReferralSpecialty(v: String) { _referralSpecialty.value = v }
    fun setReferralReason(v: String) { _referralReason.value = v }

    fun toggleLabTest(test: String) {
        _selectedLabTests.value = _selectedLabTests.value.toMutableSet().apply {
            if (contains(test)) remove(test) else add(test)
        }
    }

    fun addMedicine() {
        _medicines.value = _medicines.value + Medicine()
    }

    fun removeMedicine(index: Int) {
        _medicines.value = _medicines.value.toMutableList().apply { if (size > 1) removeAt(index) }
    }

    fun updateMedicine(index: Int, medicine: Medicine) {
        _medicines.value = _medicines.value.toMutableList().apply { set(index, medicine) }
    }

    fun saveConsultation() {
        viewModelScope.launch {
            _isLoading.value = true
            val form = DoctorConsultationForm(
                chiefComplaints = _chiefComplaints.value,
                diagnosis = _diagnosis.value,
                icdCode = _icdCode.value,
                medicines = _medicines.value.filter { it.name.isNotBlank() },
                labTests = _selectedLabTests.value.toList(),
                imagingNotes = _imagingNotes.value,
                procedures = _procedures.value,
                allergies = _allergies.value,
                recommendations = _recommendations.value,
                referral = if (_referralNeeded.value) Referral(true, _referralSpecialty.value, _referralReason.value) else null
            )
            saveDoctorConsultationUseCase(form)

            val patient = _selectedPatient.value
            val doctor = _currentDoctor.value
            if (patient != null && doctor != null) {
                val rx = generatePrescriptionUseCase(patient, doctor, form)
                _prescription.value = rx
            }
            endCallUseCase()
            _isLoading.value = false
        }
    }
}

// ============ Doctor Login Screen ============

@Composable
fun DoctorLoginScreen(
    onLoginSuccess: () -> Unit,
    onBack: () -> Unit
) {
    var email by remember { mutableStateOf("doctor@demo.com") }
    var password by remember { mutableStateOf("demo1234") }
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { AppTopBar(title = stringResource(R.string.role_doctor), onBack = onBack) },
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
                color = DoctorBg,
                modifier = Modifier.size(80.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.MedicalServices, contentDescription = null, tint = DoctorColor, modifier = Modifier.size(44.dp))
                }
            }

            Text(stringResource(R.string.role_doctor), style = MaterialTheme.typography.headlineMedium, color = DoctorColor)

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
                color = DoctorColor
            )
        }
    }
}

// ============ Doctor Patient Queue ============

@Composable
fun DoctorQueueScreen(
    viewModel: DoctorModuleViewModel,
    onPatientSelect: (String) -> Unit,
    onBack: () -> Unit
) {
    val queue by viewModel.patientQueue.collectAsState()
    val doctor by viewModel.currentDoctor.collectAsState()

    LaunchedEffect(Unit) { viewModel.refreshQueue() }

    Scaffold(
        containerColor = BackgroundPage
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Hero header with doctor info
            HeroHeader(
                title = stringResource(R.string.patient_queue),
                subtitle = doctor?.let { "${it.name} • ${it.specialty}" } ?: "",
                moduleChip = "Doctor",
                moduleColor = DoctorColor,
                onBack = onBack,
                stats = listOf(
                    queue.size.toString() to "Waiting",
                    queue.size.toString() to "Today"
                )
            )

            if (queue.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.People, contentDescription = null, modifier = Modifier.size(64.dp), tint = TextSecondary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No patients waiting", style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(queue) { item ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = BackgroundCard),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // Avatar
                                    Surface(
                                        shape = CircleShape,
                                        color = DoctorBg,
                                        modifier = Modifier.size(44.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                item.patient.fullName.take(2).uppercase(),
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = DoctorColor
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(item.patient.fullName, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                                        Text("${item.patient.id} • ${item.patient.age}y ${item.patient.gender.name}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                    }
                                    StatusBadge(item.status)
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Vitals summary row
                                item.patient.vitals.let { v ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(BackgroundPage, RoundedCornerShape(8.dp))
                                            .padding(8.dp),
                                        horizontalArrangement = Arrangement.SpaceEvenly
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text("BP", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                                            Text("${v.bpSystolic ?: "—"}/${v.bpDiastolic ?: "—"}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                                        }
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text("SpO2", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                                            Text("${v.spo2 ?: "—"}%", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                                        }
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text("Pulse", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                                            Text("${v.pulseRate ?: "—"}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    "Complaint: ${item.patient.medicalHistory.primaryComplaint}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                    maxLines = 1
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = {
                                            viewModel.selectPatient(item.patient)
                                            onPatientSelect(item.patient.id)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = CallAcceptGreen),
                                        modifier = Modifier.weight(1f).height(44.dp),
                                        shape = RoundedCornerShape(24.dp)
                                    ) {
                                        Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(stringResource(R.string.answer))
                                    }
                                    OutlinedButton(
                                        onClick = { },
                                        modifier = Modifier.weight(1f).height(44.dp),
                                        shape = RoundedCornerShape(24.dp)
                                    ) {
                                        Text(stringResource(R.string.decline), color = TextSecondary)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ============ Incoming Call Screen ============

@Composable
fun DoctorIncomingCallScreen(
    viewModel: DoctorModuleViewModel,
    patientId: String,
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    val selectedPatient by viewModel.selectedPatient.collectAsState()
    val patient = selectedPatient

    val infiniteTransition = rememberInfiniteTransition(label = "ring")
    val ringScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ring"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CallDarkBg),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(stringResource(R.string.incoming_call), style = MaterialTheme.typography.titleMedium, color = Color.White.copy(alpha = 0.7f))

            Surface(
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.12f),
                modifier = Modifier.size(120.dp).scale(ringScale)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(64.dp))
                }
            }

            Text(
                patient?.fullName ?: "Patient",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )

            if (patient != null) {
                Text(
                    "${patient.address.village}, ${patient.address.district}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(48.dp)) {
                // Decline
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    FilledIconButton(
                        onClick = {
                            viewModel.declineCall()
                            onDecline()
                        },
                        modifier = Modifier.size(72.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = CallDeclineRed)
                    ) {
                        Icon(Icons.Default.CallEnd, contentDescription = stringResource(R.string.decline_call), tint = Color.White, modifier = Modifier.size(36.dp))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(stringResource(R.string.decline_call), color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.labelMedium)
                }

                // Accept
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    FilledIconButton(
                        onClick = {
                            viewModel.acceptCall(patientId)
                            onAccept()
                        },
                        modifier = Modifier.size(72.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = CallAcceptGreen)
                    ) {
                        Icon(Icons.Default.Call, contentDescription = stringResource(R.string.accept_call), tint = Color.White, modifier = Modifier.size(36.dp))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(stringResource(R.string.accept_call), color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

// ============ Consultation Screen ============

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DoctorConsultationScreen(
    viewModel: DoctorModuleViewModel,
    patientId: String,
    onPrescriptionGenerated: () -> Unit,
    onBack: () -> Unit
) {
    val selectedPatient by viewModel.selectedPatient.collectAsState()
    val chiefComplaints by viewModel.chiefComplaints.collectAsState()
    val diagnosis by viewModel.diagnosis.collectAsState()
    val icdCode by viewModel.icdCode.collectAsState()
    val medicines by viewModel.medicines.collectAsState()
    val selectedLabTests by viewModel.selectedLabTests.collectAsState()
    val imagingNotes by viewModel.imagingNotes.collectAsState()
    val procedures by viewModel.procedures.collectAsState()
    val allergies by viewModel.allergies.collectAsState()
    val recommendations by viewModel.recommendations.collectAsState()
    val referralNeeded by viewModel.referralNeeded.collectAsState()
    val referralSpecialty by viewModel.referralSpecialty.collectAsState()
    val referralReason by viewModel.referralReason.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = { AppTopBar(title = stringResource(R.string.consultation), onBack = onBack) },
        containerColor = BackgroundPage
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Segmented tab chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SelectableChip(
                    text = stringResource(R.string.patient_details),
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 }
                )
                SelectableChip(
                    text = "Rx Form",
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 }
                )
            }

            when (selectedTab) {
                0 -> {
                    // Patient info panel
                    val patient = selectedPatient
                    if (patient != null) {
                        Column(
                            modifier = Modifier.verticalScroll(rememberScrollState()).padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            // Patient header card
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = DoctorBg)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(patient.fullName, style = MaterialTheme.typography.headlineSmall, color = TextPrimary)
                                    Text("${patient.id} • ${patient.age}y • ${patient.gender.name}", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            SectionHeader(stringResource(R.string.vitals_summary), moduleColor = DoctorColor)

                            // Vitals grid
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                patient.vitals.let { v ->
                                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        VitalCard(label = "Weight", value = "${v.weight ?: "—"} kg", icon = Icons.Default.MonitorWeight)
                                        VitalCard(label = "BP", value = "${v.bpSystolic ?: "—"}/${v.bpDiastolic ?: "—"}", icon = Icons.Default.Favorite)
                                        VitalCard(label = "Hb", value = "${v.hemoglobin ?: "—"} g/dL", icon = Icons.Default.Bloodtype)
                                    }
                                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        VitalCard(label = "Temp", value = "${v.temperature ?: "—"}°${v.temperatureUnit}", icon = Icons.Default.Thermostat)
                                        VitalCard(label = "Sugar", value = "${v.bloodSugar ?: "—"} mg/dL", icon = Icons.Default.Water)
                                        VitalCard(label = "SpO2", value = "${v.spo2 ?: "—"}%", icon = Icons.Default.Air)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            SectionHeader("Chief Complaint", moduleColor = DoctorColor)
                            Text(patient.medicalHistory.primaryComplaint, style = MaterialTheme.typography.bodyLarge, color = TextPrimary)

                            Spacer(modifier = Modifier.height(4.dp))

                            SectionHeader("Symptoms", moduleColor = DoctorColor)
                            Text(patient.medicalHistory.symptoms.joinToString(", ").ifEmpty { "None" }, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)

                            Spacer(modifier = Modifier.height(4.dp))

                            SectionHeader("Known Conditions", moduleColor = DoctorColor)
                            Text(patient.medicalHistory.knownConditions.joinToString(", ").ifEmpty { "None" }, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)

                            Spacer(modifier = Modifier.height(4.dp))

                            SectionHeader("Lifestyle", moduleColor = DoctorColor)
                            val lifestyle = patient.medicalHistory.lifestyle
                            val ls = mutableListOf<String>()
                            if (lifestyle.alcohol) ls.add("Alcohol")
                            if (lifestyle.tobacco) ls.add("Tobacco")
                            if (lifestyle.drugs) ls.add("Drugs")
                            Text(ls.joinToString(", ").ifEmpty { "None reported" }, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)

                            Spacer(modifier = Modifier.height(4.dp))

                            SectionHeader("Documents", moduleColor = DoctorColor)
                            Text("${patient.documents.size} file(s) uploaded", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                        }
                    }
                }
                1 -> {
                    // Consultation form
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Section A: Chief Complaints
                        SectionHeader("A. ${stringResource(R.string.chief_complaints)}", moduleColor = DoctorColor)
                        LargeTextField(value = chiefComplaints, onValueChange = viewModel::setChiefComplaints, label = stringResource(R.string.chief_complaints), singleLine = false, minLines = 2)

                        // Section B: Diagnosis
                        SectionHeader("B. ${stringResource(R.string.diagnosis)}", moduleColor = DoctorColor)
                        LargeTextField(value = diagnosis, onValueChange = viewModel::setDiagnosis, label = stringResource(R.string.diagnosis), singleLine = false, minLines = 2)
                        LargeTextField(value = icdCode, onValueChange = viewModel::setIcdCode, label = stringResource(R.string.icd_code))

                        // Section C: Treatment Plan
                        SectionHeader("C. ${stringResource(R.string.treatment_plan)}", moduleColor = DoctorColor)
                        medicines.forEachIndexed { index, medicine ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = BackgroundCard),
                                elevation = CardDefaults.cardElevation(1.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("Medicine ${index + 1}", style = MaterialTheme.typography.labelLarge, color = DoctorColor, modifier = Modifier.weight(1f))
                                        if (medicines.size > 1) {
                                            IconButton(onClick = { viewModel.removeMedicine(index) }) {
                                                Icon(Icons.Default.RemoveCircle, contentDescription = stringResource(R.string.remove), tint = CallDeclineRed)
                                            }
                                        }
                                    }
                                    LargeTextField(value = medicine.name, onValueChange = { viewModel.updateMedicine(index, medicine.copy(name = it)) }, label = stringResource(R.string.medicine_name))
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        LargeTextField(value = medicine.dosage, onValueChange = { viewModel.updateMedicine(index, medicine.copy(dosage = it)) }, label = stringResource(R.string.dosage), modifier = Modifier.weight(1f))
                                        LargeTextField(value = medicine.durationDays.let { if (it == 0) "" else it.toString() }, onValueChange = { viewModel.updateMedicine(index, medicine.copy(durationDays = it.toIntOrNull() ?: 0)) }, label = stringResource(R.string.duration_days), keyboardType = KeyboardType.Number, modifier = Modifier.weight(1f))
                                    }
                                    // Frequency toggles
                                    Text(stringResource(R.string.frequency), style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        SelectableChip(text = stringResource(R.string.morning), selected = medicine.frequency.morning, onClick = { viewModel.updateMedicine(index, medicine.copy(frequency = medicine.frequency.copy(morning = !medicine.frequency.morning))) })
                                        SelectableChip(text = stringResource(R.string.afternoon), selected = medicine.frequency.afternoon, onClick = { viewModel.updateMedicine(index, medicine.copy(frequency = medicine.frequency.copy(afternoon = !medicine.frequency.afternoon))) })
                                        SelectableChip(text = stringResource(R.string.night), selected = medicine.frequency.night, onClick = { viewModel.updateMedicine(index, medicine.copy(frequency = medicine.frequency.copy(night = !medicine.frequency.night))) })
                                    }
                                    LargeTextField(value = medicine.instructions, onValueChange = { viewModel.updateMedicine(index, medicine.copy(instructions = it)) }, label = stringResource(R.string.special_instructions))
                                }
                            }
                        }
                        OutlinedButton(
                            onClick = { viewModel.addMedicine() },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = DoctorColor)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(stringResource(R.string.add_medicine), color = DoctorColor)
                        }

                        // Section D: Lab Tests
                        SectionHeader("D. ${stringResource(R.string.lab_tests)}", moduleColor = DoctorColor)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            viewModel.labTests.forEach { test ->
                                SelectableChip(text = test, selected = selectedLabTests.contains(test), onClick = { viewModel.toggleLabTest(test) })
                            }
                        }

                        // Section E: Other Clinical Info
                        SectionHeader("E. Other Clinical Info", moduleColor = DoctorColor)
                        LargeTextField(value = imagingNotes, onValueChange = viewModel::setImagingNotes, label = stringResource(R.string.imaging_notes), singleLine = false, minLines = 2)
                        LargeTextField(value = procedures, onValueChange = viewModel::setProcedures, label = stringResource(R.string.procedures))
                        LargeTextField(value = allergies, onValueChange = viewModel::setAllergies, label = stringResource(R.string.allergies))
                        LargeTextField(value = recommendations, onValueChange = viewModel::setRecommendations, label = stringResource(R.string.recommendations), singleLine = false, minLines = 2)

                        // Referral
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(stringResource(R.string.referral_needed), style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f), color = TextPrimary)
                            Switch(
                                checked = referralNeeded,
                                onCheckedChange = viewModel::setReferralNeeded,
                                colors = SwitchDefaults.colors(checkedTrackColor = DoctorColor)
                            )
                        }
                        if (referralNeeded) {
                            LargeTextField(value = referralSpecialty, onValueChange = viewModel::setReferralSpecialty, label = stringResource(R.string.specialty))
                            LargeTextField(value = referralReason, onValueChange = viewModel::setReferralReason, label = stringResource(R.string.referral_reason))
                        }
                    }

                    // Bottom button
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        LargeButton(
                            text = stringResource(R.string.generate_pdf),
                            onClick = {
                                viewModel.saveConsultation()
                                onPrescriptionGenerated()
                            },
                            isLoading = isLoading,
                            color = DoctorColor,
                            icon = Icons.Default.Description,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

// ============ Prescription Preview Screen ============

@Composable
fun DoctorPrescriptionPreviewScreen(
    viewModel: DoctorModuleViewModel,
    onDone: () -> Unit,
    onBack: () -> Unit
) {
    val prescription by viewModel.prescription.collectAsState()
    val doctor by viewModel.currentDoctor.collectAsState()

    Scaffold(
        topBar = { AppTopBar(title = stringResource(R.string.preview_prescription), onBack = onBack) },
        containerColor = BackgroundPage
    ) { padding ->
        val rx = prescription
        if (rx == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = DoctorColor)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Generating prescription...", style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
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
                    // Prescription ready card
                    PrescriptionReadyCard()

                    Spacer(modifier = Modifier.height(8.dp))

                    // Prescription header
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = DoctorBg)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(rx.clinicName, style = MaterialTheme.typography.titleLarge, color = DoctorColor)
                            Text(rx.doctorName, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                            Text(rx.doctorQualification, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                            Text("Reg: ${rx.regNumber}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = DividerColor)
                            Text("Date: ${rx.date}", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Patient info
                    SectionHeader("Patient", moduleColor = DoctorColor)
                    PatientSummaryCard("Name", rx.patientName)
                    PatientSummaryCard("ID", rx.patientId)
                    PatientSummaryCard("Age/Gender", "${rx.patientAge}y / ${rx.patientGender}")

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = DividerColor)

                    SectionHeader("Diagnosis", moduleColor = DoctorColor)
                    Text(rx.diagnosis.ifEmpty { "—" }, style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
                    Text("Chief Complaints: ${rx.chiefComplaints}", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = DividerColor)

                    // Rx symbol
                    Text("Rx", style = MaterialTheme.typography.headlineLarge, color = DoctorColor)
                    Spacer(modifier = Modifier.height(4.dp))

                    rx.medicines.forEachIndexed { idx, med ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = BackgroundCard),
                            elevation = CardDefaults.cardElevation(1.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("${idx + 1}. ${med.name} — ${med.dosage}", style = MaterialTheme.typography.titleSmall, color = TextPrimary)
                                Text("${med.frequency} for ${med.durationDays} days", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                                if (med.instructions.isNotBlank()) {
                                    Text(med.instructions, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    if (rx.labTests.isNotEmpty()) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = DividerColor)
                        SectionHeader("Lab Tests Advised", moduleColor = DoctorColor)
                        rx.labTests.forEach { test -> Text("• $test", style = MaterialTheme.typography.bodyMedium, color = TextPrimary) }
                    }

                    if (rx.recommendations.isNotBlank()) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = DividerColor)
                        SectionHeader("Recommendations", moduleColor = DoctorColor)
                        Text(rx.recommendations, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                    }

                    if (rx.referral != null) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = DividerColor)
                        SectionHeader("Referral", moduleColor = DoctorColor)
                        Text("Specialty: ${rx.referral.specialty}", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                        Text("Reason: ${rx.referral.reason}", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                    }

                    // Digital signature placeholder
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
                        Text(stringResource(R.string.digital_signature), style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                    }
                }

                // Bottom actions
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { /* mock share */ },
                        modifier = Modifier.weight(1f).height(52.dp),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, tint = DoctorColor)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(stringResource(R.string.share_prescription), color = DoctorColor)
                    }
                    Button(
                        onClick = onDone,
                        modifier = Modifier.weight(1f).height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DoctorColor),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Text(stringResource(R.string.done))
                    }
                }
            }
        }
    }
}
