package com.telemed.demo.feature.healthworker

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telemed.demo.R
import com.telemed.demo.domain.model.*
import com.telemed.demo.domain.usecase.*
import com.telemed.demo.ui.components.*
import com.telemed.demo.ui.responsive.AppTopBar
import com.telemed.demo.ui.responsive.responsiveHorizontalPadding
import com.telemed.demo.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// ============ ViewModel ============

class HealthWorkerModuleViewModel(
    private val getDistrictsUseCase: GetDistrictsUseCase,
    private val getVillagesUseCase: GetVillagesUseCase,
    private val getStatesUseCase: GetStatesUseCase,
    private val saveSpokeLocationUseCase: SaveSpokeLocationUseCase,
    private val registerPatientUseCase: RegisterPatientUseCase,
    private val getRecentPatientsUseCase: GetRecentPatientsUseCase,
    private val findBestDoctorUseCase: FindBestDoctorUseCase,
    private val startCallUseCase: StartCallUseCase,
    private val endCallUseCase: EndCallUseCase
) : ViewModel() {

    private val _spokeName = MutableStateFlow("Spoke Berasia")
    val spokeName: StateFlow<String> = _spokeName.asStateFlow()
    private val _selectedDistrict = MutableStateFlow("Bhopal")
    val selectedDistrict: StateFlow<String> = _selectedDistrict.asStateFlow()
    private val _selectedVillage = MutableStateFlow("Berasia")
    val selectedVillage: StateFlow<String> = _selectedVillage.asStateFlow()
    private val _districts = MutableStateFlow<List<String>>(emptyList())
    val districts: StateFlow<List<String>> = _districts.asStateFlow()
    private val _villages = MutableStateFlow<List<String>>(emptyList())
    val villages: StateFlow<List<String>> = _villages.asStateFlow()
    private val _states = MutableStateFlow<List<String>>(emptyList())
    val states: StateFlow<List<String>> = _states.asStateFlow()
    private val _currentDateTime = MutableStateFlow("")
    val currentDateTime: StateFlow<String> = _currentDateTime.asStateFlow()
    private val _recentPatients = MutableStateFlow<List<Patient>>(emptyList())
    val recentPatients: StateFlow<List<Patient>> = _recentPatients.asStateFlow()
    private val _syncStatus = MutableStateFlow("Synced")
    val syncStatus: StateFlow<String> = _syncStatus.asStateFlow()
    private val _fullName = MutableStateFlow("")
    val fullName: StateFlow<String> = _fullName.asStateFlow()
    private val _guardianName = MutableStateFlow("")
    val guardianName: StateFlow<String> = _guardianName.asStateFlow()
    private val _gender = MutableStateFlow(Gender.MALE)
    val gender: StateFlow<Gender> = _gender.asStateFlow()
    private val _age = MutableStateFlow("")
    val age: StateFlow<String> = _age.asStateFlow()
    private val _dob = MutableStateFlow("")
    val dob: StateFlow<String> = _dob.asStateFlow()
    private val _mobile = MutableStateFlow("")
    val mobile: StateFlow<String> = _mobile.asStateFlow()
    private val _aadhaar = MutableStateFlow("")
    val aadhaar: StateFlow<String> = _aadhaar.asStateFlow()
    private val _regVillage = MutableStateFlow("")
    val regVillage: StateFlow<String> = _regVillage.asStateFlow()
    private val _regDistrict = MutableStateFlow("")
    val regDistrict: StateFlow<String> = _regDistrict.asStateFlow()
    private val _regState = MutableStateFlow("")
    val regState: StateFlow<String> = _regState.asStateFlow()
    private val _regVillages = MutableStateFlow<List<String>>(emptyList())
    val regVillages: StateFlow<List<String>> = _regVillages.asStateFlow()
    private val _weight = MutableStateFlow("")
    val weight: StateFlow<String> = _weight.asStateFlow()
    private val _temperature = MutableStateFlow("")
    val temperature: StateFlow<String> = _temperature.asStateFlow()
    private val _tempUnit = MutableStateFlow("F")
    val tempUnit: StateFlow<String> = _tempUnit.asStateFlow()
    private val _bpSystolic = MutableStateFlow("")
    val bpSystolic: StateFlow<String> = _bpSystolic.asStateFlow()
    private val _bpDiastolic = MutableStateFlow("")
    val bpDiastolic: StateFlow<String> = _bpDiastolic.asStateFlow()
    private val _bloodSugar = MutableStateFlow("")
    val bloodSugar: StateFlow<String> = _bloodSugar.asStateFlow()
    private val _hemoglobin = MutableStateFlow("")
    val hemoglobin: StateFlow<String> = _hemoglobin.asStateFlow()
    private val _spo2 = MutableStateFlow("")
    val spo2: StateFlow<String> = _spo2.asStateFlow()
    private val _pulseRate = MutableStateFlow("")
    val pulseRate: StateFlow<String> = _pulseRate.asStateFlow()
    private val _primaryComplaint = MutableStateFlow("")
    val primaryComplaint: StateFlow<String> = _primaryComplaint.asStateFlow()
    private val _selectedSymptoms = MutableStateFlow<Set<String>>(emptySet())
    val selectedSymptoms: StateFlow<Set<String>> = _selectedSymptoms.asStateFlow()
    private val _selectedConditions = MutableStateFlow<Set<String>>(emptySet())
    val selectedConditions: StateFlow<Set<String>> = _selectedConditions.asStateFlow()
    private val _alcohol = MutableStateFlow(false)
    val alcohol: StateFlow<Boolean> = _alcohol.asStateFlow()
    private val _tobacco = MutableStateFlow(false)
    val tobacco: StateFlow<Boolean> = _tobacco.asStateFlow()
    private val _drugs = MutableStateFlow(false)
    val drugs: StateFlow<Boolean> = _drugs.asStateFlow()
    private val _uploadedFiles = MutableStateFlow<List<DocumentFile>>(emptyList())
    val uploadedFiles: StateFlow<List<DocumentFile>> = _uploadedFiles.asStateFlow()
    private val _registeredPatient = MutableStateFlow<Patient?>(null)
    val registeredPatient: StateFlow<Patient?> = _registeredPatient.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Video call state
    private val _selectedLanguage = MutableStateFlow("Hindi")
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()
    private val _matchedDoctor = MutableStateFlow<Doctor?>(null)
    val matchedDoctor: StateFlow<Doctor?> = _matchedDoctor.asStateFlow()
    private val _isSearchingDoctor = MutableStateFlow(false)
    val isSearchingDoctor: StateFlow<Boolean> = _isSearchingDoctor.asStateFlow()
    private val _isConnecting = MutableStateFlow(false)
    val isConnecting: StateFlow<Boolean> = _isConnecting.asStateFlow()
    private val _isCallActive = MutableStateFlow(false)
    val isCallActive: StateFlow<Boolean> = _isCallActive.asStateFlow()
    private val _isPhoneCall = MutableStateFlow(false)
    val isPhoneCall: StateFlow<Boolean> = _isPhoneCall.asStateFlow()
    private val _callDuration = MutableStateFlow(0)
    val callDuration: StateFlow<Int> = _callDuration.asStateFlow()
    private val _selectedCallPatient = MutableStateFlow<Patient?>(null)
    val selectedCallPatient: StateFlow<Patient?> = _selectedCallPatient.asStateFlow()
    // Call status: "searching", "doctor_busy", "no_doctor", "connecting", "ringing", "active", "ended", ""
    private val _callStatus = MutableStateFlow("")
    val callStatus: StateFlow<String> = _callStatus.asStateFlow()

    val symptoms = listOf("Fever", "Cough", "Headache", "Fatigue", "Pain", "Breathlessness", "Other")
    val conditions = listOf("Diabetes", "Hypertension", "TB", "Heart Disease", "None")
    val availableLanguages = listOf("Hindi", "English", "Marathi", "Gujarati", "Urdu")

    init { updateDateTime(); loadData() }

    private fun updateDateTime() {
        _currentDateTime.value = LocalDateTime.now().format(DateTimeFormatter.ofPattern("EEE, dd MMM yyyy  •  hh:mm a"))
    }
    private fun loadData() {
        viewModelScope.launch {
            _districts.value = getDistrictsUseCase()
            _states.value = getStatesUseCase()
            _recentPatients.value = getRecentPatientsUseCase()
            // Load villages for the default prefilled district
            if (_selectedDistrict.value.isNotBlank()) {
                _villages.value = getVillagesUseCase(_selectedDistrict.value)
            }
        }
    }
    fun refreshDashboard() { viewModelScope.launch { _recentPatients.value = getRecentPatientsUseCase() } }
    fun setSpokeName(name: String) { _spokeName.value = name }
    fun setDistrict(district: String) { _selectedDistrict.value = district; viewModelScope.launch { _villages.value = getVillagesUseCase(district); _selectedVillage.value = "" } }
    fun setVillage(village: String) { _selectedVillage.value = village }
    fun saveSession() { viewModelScope.launch { saveSpokeLocationUseCase(SpokeLocation(_spokeName.value, _selectedDistrict.value, _selectedVillage.value, LocalDateTime.now().toString())) } }
    fun setFullName(v: String) { _fullName.value = v }
    fun setGuardianName(v: String) { _guardianName.value = v }
    fun setGender(v: Gender) { _gender.value = v }
    fun setAge(v: String) { _age.value = v }
    fun setDob(v: String) { _dob.value = v }
    fun setMobile(v: String) { _mobile.value = v }
    fun setAadhaar(v: String) { _aadhaar.value = v }
    fun setRegVillage(v: String) { _regVillage.value = v }
    fun setRegDistrict(v: String) { _regDistrict.value = v; viewModelScope.launch { _regVillages.value = getVillagesUseCase(v); _regVillage.value = "" } }
    fun setRegState(v: String) { _regState.value = v }
    fun setWeight(v: String) { _weight.value = v }
    fun setTemperature(v: String) { _temperature.value = v }
    fun setTempUnit(v: String) { _tempUnit.value = v }
    fun setBpSystolic(v: String) { _bpSystolic.value = v }
    fun setBpDiastolic(v: String) { _bpDiastolic.value = v }
    fun setBloodSugar(v: String) { _bloodSugar.value = v }
    fun setHemoglobin(v: String) { _hemoglobin.value = v }
    fun setSpo2(v: String) { _spo2.value = v }
    fun setPulseRate(v: String) { _pulseRate.value = v }
    fun setPrimaryComplaint(v: String) { _primaryComplaint.value = v }
    fun toggleSymptom(s: String) { _selectedSymptoms.value = _selectedSymptoms.value.toMutableSet().apply { if (contains(s)) remove(s) else add(s) } }
    fun toggleCondition(c: String) { _selectedConditions.value = _selectedConditions.value.toMutableSet().apply { if (contains(c)) remove(c) else add(c) } }
    fun setAlcohol(v: Boolean) { _alcohol.value = v }
    fun setTobacco(v: Boolean) { _tobacco.value = v }
    fun setDrugs(v: Boolean) { _drugs.value = v }
    fun addMockDocument(type: String) { val name = if (type == "report") "Report_${_uploadedFiles.value.size + 1}.pdf" else "Xray_${_uploadedFiles.value.size + 1}.jpg"; _uploadedFiles.value = _uploadedFiles.value + DocumentFile(name, type) }
    fun removeDocument(doc: DocumentFile) { _uploadedFiles.value = _uploadedFiles.value - doc }

    // Consent management
    fun setPatientConsent(patientId: String, consent: Boolean) {
        viewModelScope.launch {
            val idx = _recentPatients.value.indexOfFirst { it.id == patientId }
            if (idx >= 0) {
                val updated = _recentPatients.value.toMutableList()
                updated[idx] = updated[idx].copy(consentGiven = consent)
                _recentPatients.value = updated
            }
        }
    }

    fun getPatientById(patientId: String): Patient? {
        return _recentPatients.value.find { it.id == patientId }
    }

    fun submitRegistration() {
        viewModelScope.launch {
            _isLoading.value = true
            val patient = Patient(id = "", fullName = _fullName.value, guardianName = _guardianName.value, gender = _gender.value, age = _age.value.toIntOrNull() ?: 0, dob = _dob.value, mobile = _mobile.value, aadhaar = _aadhaar.value, address = Address(_regVillage.value, _regDistrict.value, _regState.value), vitals = Vitals(_weight.value.toFloatOrNull(), _temperature.value.toFloatOrNull(), _tempUnit.value, _bpSystolic.value.toIntOrNull(), _bpDiastolic.value.toIntOrNull(), _bloodSugar.value.toFloatOrNull(), _hemoglobin.value.toFloatOrNull(), _spo2.value.toIntOrNull(), _pulseRate.value.toIntOrNull()), medicalHistory = MedicalHistory(_primaryComplaint.value, _selectedSymptoms.value.toList(), _selectedConditions.value.toList(), LifestyleHistory(_alcohol.value, _tobacco.value, _drugs.value)), documents = _uploadedFiles.value, registeredBy = _spokeName.value.ifEmpty { "Health Worker" })
            _registeredPatient.value = registerPatientUseCase(patient)
            _isLoading.value = false
        }
    }

    fun resetRegistration() {
        _fullName.value = ""; _guardianName.value = ""; _gender.value = Gender.MALE; _age.value = ""; _dob.value = ""; _mobile.value = ""; _aadhaar.value = ""; _regVillage.value = ""; _regDistrict.value = ""; _regState.value = ""; _weight.value = ""; _temperature.value = ""; _bpSystolic.value = ""; _bpDiastolic.value = ""; _bloodSugar.value = ""; _hemoglobin.value = ""; _spo2.value = ""; _pulseRate.value = ""; _primaryComplaint.value = ""; _selectedSymptoms.value = emptySet(); _selectedConditions.value = emptySet(); _alcohol.value = false; _tobacco.value = false; _drugs.value = false; _uploadedFiles.value = emptyList(); _registeredPatient.value = null
    }

    // Call workflow
    fun setLanguage(lang: String) { _selectedLanguage.value = lang }

    fun selectPatientForCall(patient: Patient) {
        _selectedCallPatient.value = patient
    }

    fun searchAndConnectDoctor(isPhone: Boolean = false) {
        viewModelScope.launch {
            _isPhoneCall.value = isPhone
            _isSearchingDoctor.value = true
            _matchedDoctor.value = null
            _callStatus.value = "searching"

            // Find best doctor matching language and district
            val doctor = findBestDoctorUseCase(_selectedLanguage.value, _selectedDistrict.value)

            if (doctor == null) {
                _isSearchingDoctor.value = false
                _callStatus.value = "no_doctor"
                return@launch
            }

            _matchedDoctor.value = doctor
            _isSearchingDoctor.value = false

            // Simulate doctor might be busy (for demo: if doctor ETA > 6, simulate busy then retry)
            if (doctor.etaMinutes > 6) {
                _callStatus.value = "doctor_busy"
                delay(2000)
                // Auto-retry with next available
                _callStatus.value = "searching"
                delay(1500)
            }

            _callStatus.value = "ringing"
            _isConnecting.value = true
            delay(2500)
            startCallUseCase()
            _isConnecting.value = false
            _isCallActive.value = true
            _callStatus.value = "active"

            startCallTimer()
        }
    }

    private fun startCallTimer() {
        viewModelScope.launch {
            _callDuration.value = 0
            while (_isCallActive.value) {
                delay(1000)
                if (_isCallActive.value) {
                    _callDuration.value = _callDuration.value + 1
                }
            }
        }
    }

    fun endCall() {
        viewModelScope.launch {
            endCallUseCase()
            _isCallActive.value = false
            _isConnecting.value = false
            _callDuration.value = 0
        }
    }

    fun resetCallState() {
        _matchedDoctor.value = null
        _isSearchingDoctor.value = false
        _isConnecting.value = false
        _isCallActive.value = false
        _isPhoneCall.value = false
        _callDuration.value = 0
        _selectedCallPatient.value = null
        _callStatus.value = ""
    }
}

// ============ Health Worker Login Screen ============

@Composable
fun HWLoginScreen(
    onLoginSuccess: () -> Unit,
    onBack: () -> Unit
) {
    var email by remember { mutableStateOf("healthworker@demo.com") }
    var password by remember { mutableStateOf("demo1234") }
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { AppTopBar(title = stringResource(R.string.role_health_worker), onBack = onBack) },
        containerColor = BackgroundPage
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                shape = CircleShape,
                color = HealthWorkerBg,
                modifier = Modifier.size(80.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.VolunteerActivism, contentDescription = null, tint = HealthWorkerColor, modifier = Modifier.size(44.dp))
                }
            }

            Text(stringResource(R.string.role_health_worker), style = MaterialTheme.typography.headlineMedium, color = HealthWorkerColor)

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
                color = HealthWorkerColor
            )
        }
    }
}

// ============ Session Setup Screen ============

@Composable
fun HWSessionSetupScreen(viewModel: HealthWorkerModuleViewModel, onSessionStarted: () -> Unit, onBack: () -> Unit) {
    val spokeName by viewModel.spokeName.collectAsState()
    val selectedDistrict by viewModel.selectedDistrict.collectAsState()
    val selectedVillage by viewModel.selectedVillage.collectAsState()
    val districts by viewModel.districts.collectAsState()
    val villages by viewModel.villages.collectAsState()
    val currentDateTime by viewModel.currentDateTime.collectAsState()
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()

    Scaffold(topBar = { AppTopBar(title = stringResource(R.string.session_setup), onBack = onBack) }, containerColor = BackgroundPage) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            // Module identity chip
            Surface(shape = RoundedCornerShape(20.dp), color = HealthWorkerBg) {
                Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.PersonSearch, contentDescription = null, tint = HealthWorkerColor, modifier = Modifier.size(16.dp))
                    Text("HEALTH WORKER", style = MaterialTheme.typography.labelSmall, color = HealthWorkerColor)
                }
            }

            LargeTextField(value = spokeName, onValueChange = viewModel::setSpokeName, label = stringResource(R.string.spoke_name))
            DropdownField(value = selectedDistrict, onValueChange = viewModel::setDistrict, label = stringResource(R.string.district), options = districts)
            DropdownField(value = selectedVillage, onValueChange = viewModel::setVillage, label = stringResource(R.string.village), options = villages, enabled = selectedDistrict.isNotBlank())

            // Language selection
            DropdownField(
                value = selectedLanguage,
                onValueChange = viewModel::setLanguage,
                label = "Preferred Language",
                options = viewModel.availableLanguages
            )

            DateTimeField(value = currentDateTime, label = stringResource(R.string.date_time))

            Spacer(modifier = Modifier.height(8.dp))

            LargeButton(text = stringResource(R.string.start_session), onClick = { viewModel.saveSession(); onSessionStarted() }, enabled = spokeName.isNotBlank() && selectedDistrict.isNotBlank() && selectedVillage.isNotBlank(), icon = Icons.Default.PlayArrow, color = HealthWorkerColor)
        }
    }
}

// ============ Dashboard ============

@Composable
fun HWDashboardScreen(viewModel: HealthWorkerModuleViewModel, onNewPatient: () -> Unit, onPatientClick: (String) -> Unit, onConsentClick: (String) -> Unit, onConnectDoctor: () -> Unit, onBack: () -> Unit) {
    val recentPatients by viewModel.recentPatients.collectAsState()
    val spokeName by viewModel.spokeName.collectAsState()
    val selectedDistrict by viewModel.selectedDistrict.collectAsState()
    val currentDateTime by viewModel.currentDateTime.collectAsState()

    LaunchedEffect(Unit) { viewModel.refreshDashboard() }

    Scaffold(containerColor = BackgroundPage) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            HeroHeader(
                title = stringResource(R.string.dashboard),
                subtitle = "$spokeName · $selectedDistrict · $currentDateTime",
                moduleColor = HealthWorkerColor,
                moduleLabel = "Health Worker",
                moduleIcon = Icons.Default.PersonSearch,
                stats = listOf(
                    "${recentPatients.size}" to "Patients Today",
                    "${recentPatients.count { it.status == ConsultationStatus.WAITING }}" to "Pending",
                    "${recentPatients.count { it.status == ConsultationStatus.COMPLETED }}" to "Completed"
                )
            )

            Column(modifier = Modifier.fillMaxSize().padding(horizontal = responsiveHorizontalPadding(), vertical = 16.dp)) {
                // Action buttons row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    LargeButton(
                        text = stringResource(R.string.new_patient),
                        onClick = onNewPatient,
                        icon = Icons.Default.PersonAdd,
                        color = HealthWorkerColor,
                        modifier = Modifier.weight(1f)
                    )
                    LargeButton(
                        text = "Connect Doctor",
                        onClick = onConnectDoctor,
                        icon = Icons.Default.VideoCall,
                        color = DoctorColor,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(stringResource(R.string.recent_patients), style = MaterialTheme.typography.headlineSmall, color = TextPrimary)
                Spacer(modifier = Modifier.height(8.dp))

                if (recentPatients.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.People, contentDescription = null, modifier = Modifier.size(64.dp), tint = TextSecondary)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No patients registered yet for today's visit", style = MaterialTheme.typography.bodyMedium, color = TextMuted)
                        }
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        contentPadding = PaddingValues(top = 12.dp, bottom = 16.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(recentPatients, key = { it.id }) { patient ->
                            UnifiedPatientCard(
                                patient = patient,
                                status = patient.status,
                                flow = PatientCardFlow.HEALTH_WORKER,
                                onClick = { onPatientClick(patient.id) },
                                line3Text = patient.medicalHistory.primaryComplaint.takeIf { it.isNotBlank() },
                                line4Text = patient.registeredAt.takeIf { it.isNotBlank() },
                                onConsentClick = if (patient.consentGiven == null) {{ onConsentClick(patient.id) }} else null
                            )
                        }
                    }
                }
            }
        }
    }
}

// ============ HW Consent Screen ============

@Composable
fun HWConsentScreen(
    viewModel: HealthWorkerModuleViewModel,
    patientId: String,
    onConsentGiven: () -> Unit,
    onConsentDeclined: () -> Unit,
    onBack: () -> Unit
) {
    val patients by viewModel.recentPatients.collectAsState()
    val patient = patients.find { it.id == patientId }

    Scaffold(
        topBar = { AppTopBar(title = "Patient Consent", onBack = onBack) },
        containerColor = BackgroundPage
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (patient != null) {
                // Patient info card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = HealthWorkerBg)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Surface(shape = CircleShape, color = HealthWorkerColor.copy(alpha = 0.15f), modifier = Modifier.size(48.dp)) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        patient.fullName.split(" ").take(2).joinToString("") { it.first().uppercase() },
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = HealthWorkerColor
                                    )
                                }
                            }
                            Column {
                                Text(patient.fullName, style = MaterialTheme.typography.titleLarge, color = TextPrimary)
                                Text("ID: ${patient.id}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                Text("${patient.age}y • ${patient.gender.name} • ${patient.address.village}", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                            }
                        }
                        if (patient.medicalHistory.primaryComplaint.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.MedicalInformation, contentDescription = null, tint = HealthWorkerColor, modifier = Modifier.size(16.dp))
                                Text("Complaint: ${patient.medicalHistory.primaryComplaint}", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                            }
                        }
                    }
                }

                // Current consent status
                if (patient.consentGiven != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (patient.consentGiven == true) StatusDoneBg else StatusAlertBg
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                if (patient.consentGiven == true) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                contentDescription = null,
                                tint = if (patient.consentGiven == true) StatusDoneText else StatusAlertText,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                if (patient.consentGiven == true) "Consent already given" else "Consent was declined",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (patient.consentGiven == true) StatusDoneText else StatusAlertText
                            )
                        }
                    }
                }

                // Consent request card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = BackgroundCard),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Surface(shape = CircleShape, color = HealthWorkerBg, modifier = Modifier.size(56.dp)) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.HowToReg, contentDescription = null, tint = HealthWorkerColor, modifier = Modifier.size(32.dp))
                            }
                        }

                        Text(
                            "Does the patient consent to teleconsultation?",
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center,
                            color = TextPrimary
                        )

                        Text(
                            "The patient must give verbal consent before connecting with the doctor for a video/phone consultation.",
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            color = TextSecondary
                        )

                        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Button(
                                onClick = {
                                    viewModel.setPatientConsent(patientId, true)
                                    onConsentGiven()
                                },
                                modifier = Modifier.fillMaxWidth().height(52.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CallAcceptGreen),
                                shape = RoundedCornerShape(24.dp)
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(22.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Yes, Patient Consents", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                            }
                            Button(
                                onClick = {
                                    viewModel.setPatientConsent(patientId, false)
                                    onConsentDeclined()
                                },
                                modifier = Modifier.fillMaxWidth().height(52.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CallDeclineRed),
                                shape = RoundedCornerShape(24.dp)
                            ) {
                                Icon(Icons.Default.Cancel, contentDescription = null, modifier = Modifier.size(22.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("No, Declined", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ============ Connect Doctor Screen (Patient selection + call type) ============

@Composable
fun HWConnectDoctorScreen(
    viewModel: HealthWorkerModuleViewModel,
    onVideoCall: () -> Unit,
    onPhoneCall: () -> Unit,
    onBack: () -> Unit
) {
    val recentPatients by viewModel.recentPatients.collectAsState()
    val selectedCallPatient by viewModel.selectedCallPatient.collectAsState()
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()

    val waitingPatients = recentPatients.filter { it.status == ConsultationStatus.WAITING || it.status == ConsultationStatus.REGISTERED }

    Scaffold(containerColor = BackgroundPage) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // ── Top bar: back + call type icons side-by-side + patient name + status ──
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = HeaderNavy,
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back button
                    IconButton(onClick = onBack, modifier = Modifier.size(40.dp)) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }

                    // Call type icons side by side
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = DoctorColor.copy(alpha = 0.25f),
                            modifier = Modifier.size(34.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Videocam, contentDescription = "Video", tint = Color.White, modifier = Modifier.size(19.dp))
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = CallAcceptGreen.copy(alpha = 0.25f),
                            modifier = Modifier.size(34.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Phone, contentDescription = "Phone", tint = Color.White, modifier = Modifier.size(19.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Center: patient name or consultation label
                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = selectedCallPatient?.fullName ?: "Doctor Consultation",
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.White,
                            maxLines = 1
                        )
                        if (selectedCallPatient != null) {
                            Text(
                                text = "${selectedCallPatient!!.age}y · ${selectedCallPatient!!.gender.name}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.6f),
                                maxLines = 1
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Right: ready status badge
                    Surface(shape = RoundedCornerShape(12.dp), color = Color.White.copy(alpha = 0.12f)) {
                        Text(
                            text = if (selectedCallPatient != null) "Ready" else "Select Patient",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // ── One-liner info banner ──
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = DoctorBg
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = DoctorColor, modifier = Modifier.size(16.dp))
                    Text(
                        "Connect with a $selectedLanguage-speaking doctor via video or phone",
                        style = MaterialTheme.typography.bodySmall,
                        color = DoctorColor,
                        maxLines = 1
                    )
                }
            }

            // ── Scrollable content area ──
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Language selection (compact)
                SectionHeader("Language", moduleColor = HealthWorkerColor, icon = Icons.Default.Language)
                DropdownField(
                    value = selectedLanguage,
                    onValueChange = viewModel::setLanguage,
                    label = "Language",
                    options = viewModel.availableLanguages
                )

                // Patient selection
                SectionHeader("Select Patient", moduleColor = HealthWorkerColor, icon = Icons.Default.PersonSearch)

                if (waitingPatients.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = BackgroundCard)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.PersonOff, contentDescription = null, tint = TextMuted, modifier = Modifier.size(24.dp))
                            Text("No patients waiting", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                        }
                    }
                } else {
                    waitingPatients.forEach { patient ->
                        val isSelected = selectedCallPatient?.id == patient.id
                        Card(
                            onClick = { viewModel.selectPatientForCall(patient) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) HealthWorkerBg else BackgroundCard
                            ),
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, HealthWorkerColor) else null
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Surface(shape = CircleShape, color = HealthWorkerColor.copy(alpha = 0.12f), modifier = Modifier.size(40.dp)) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            patient.fullName.split(" ").take(2).joinToString("") { it.first().uppercase() },
                                            style = MaterialTheme.typography.labelMedium,
                                            color = HealthWorkerColor
                                        )
                                    }
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(patient.fullName, style = MaterialTheme.typography.titleSmall, color = TextPrimary)
                                    Text("${patient.age}y • ${patient.gender.name} • ${patient.medicalHistory.primaryComplaint}", style = MaterialTheme.typography.bodySmall, color = TextSecondary, maxLines = 1)
                                }
                                if (isSelected) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = HealthWorkerColor, modifier = Modifier.size(22.dp))
                                }
                            }
                        }
                    }
                }

                if (selectedCallPatient == null && waitingPatients.isNotEmpty()) {
                    Text(
                        "Select a patient above to enable call options",
                        style = MaterialTheme.typography.bodySmall,
                        color = StatusAlertText,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // ── Bottom pinned call controls ──
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = BackgroundCard,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Side-by-side call buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Video Call Button
                        Button(
                            onClick = {
                                if (selectedCallPatient != null) {
                                    viewModel.searchAndConnectDoctor(isPhone = false)
                                    onVideoCall()
                                }
                            },
                            modifier = Modifier.weight(1f).height(52.dp),
                            enabled = selectedCallPatient != null,
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DoctorColor,
                                disabledContainerColor = DoctorColor.copy(alpha = 0.3f)
                            )
                        ) {
                            Icon(Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Video Call", style = MaterialTheme.typography.titleSmall)
                        }

                        // Phone Call Button
                        Button(
                            onClick = {
                                if (selectedCallPatient != null) {
                                    viewModel.searchAndConnectDoctor(isPhone = true)
                                    onPhoneCall()
                                }
                            },
                            modifier = Modifier.weight(1f).height(52.dp),
                            enabled = selectedCallPatient != null,
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CallAcceptGreen,
                                disabledContainerColor = CallAcceptGreen.copy(alpha = 0.3f)
                            )
                        ) {
                            Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Phone Call", style = MaterialTheme.typography.titleSmall)
                        }
                    }
                }
            }
        }
    }
}

// ============ HWConnectDoctorScreen Preview ============

@Preview(showBackground = true, showSystemUi = true, name = "Connect Doctor - Patient Selected")
@Composable
private fun HWConnectDoctorScreenPreview() {
    MaterialTheme {
        val mockPatient = Patient(
            id = "P-001",
            fullName = "Ramesh Kumar",
            gender = Gender.MALE,
            age = 65,
            mobile = "9876543210",
            medicalHistory = MedicalHistory(primaryComplaint = "Chest pain"),
            status = ConsultationStatus.WAITING
        )
        val mockPatient2 = Patient(
            id = "P-002",
            fullName = "Sita Devi",
            gender = Gender.FEMALE,
            age = 58,
            medicalHistory = MedicalHistory(primaryComplaint = "Fever & cough"),
            status = ConsultationStatus.REGISTERED
        )
        val selectedPatient = mockPatient

        Scaffold(containerColor = BackgroundPage) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // ── Top bar ──
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = HeaderNavy,
                    shadowElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {}, modifier = Modifier.size(40.dp)) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                            Surface(shape = RoundedCornerShape(8.dp), color = DoctorColor.copy(alpha = 0.25f), modifier = Modifier.size(34.dp)) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Videocam, contentDescription = "Video", tint = Color.White, modifier = Modifier.size(19.dp))
                                }
                            }
                            Surface(shape = RoundedCornerShape(8.dp), color = CallAcceptGreen.copy(alpha = 0.25f), modifier = Modifier.size(34.dp)) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Phone, contentDescription = "Phone", tint = Color.White, modifier = Modifier.size(19.dp))
                                }
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(selectedPatient.fullName, style = MaterialTheme.typography.titleSmall, color = Color.White, maxLines = 1)
                            Text("${selectedPatient.age}y · ${selectedPatient.gender.name}", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.6f), maxLines = 1)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Surface(shape = RoundedCornerShape(12.dp), color = Color.White.copy(alpha = 0.12f)) {
                            Text("Ready", style = MaterialTheme.typography.labelSmall, color = Color.White, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                        }
                    }
                }

                // ── One-liner info banner ──
                Surface(modifier = Modifier.fillMaxWidth(), color = DoctorBg) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = DoctorColor, modifier = Modifier.size(16.dp))
                        Text("Connect with a Hindi-speaking doctor via video or phone", style = MaterialTheme.typography.bodySmall, color = DoctorColor, maxLines = 1)
                    }
                }

                // ── Content ──
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Language", style = MaterialTheme.typography.titleSmall, color = HealthWorkerColor)
                    Surface(modifier = Modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(12.dp), color = BackgroundCard) {
                        Row(modifier = Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("Hindi", style = MaterialTheme.typography.bodyMedium, color = TextPrimary, modifier = Modifier.weight(1f))
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = TextMuted)
                        }
                    }

                    Text("Select Patient", style = MaterialTheme.typography.titleSmall, color = HealthWorkerColor)

                    // Selected patient card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = HealthWorkerBg),
                        border = androidx.compose.foundation.BorderStroke(2.dp, HealthWorkerColor)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(shape = CircleShape, color = HealthWorkerColor.copy(alpha = 0.12f), modifier = Modifier.size(40.dp)) {
                                Box(contentAlignment = Alignment.Center) { Text("RK", style = MaterialTheme.typography.labelMedium, color = HealthWorkerColor) }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Ramesh Kumar", style = MaterialTheme.typography.titleSmall, color = TextPrimary)
                                Text("65y • MALE • Chest pain", style = MaterialTheme.typography.bodySmall, color = TextSecondary, maxLines = 1)
                            }
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = HealthWorkerColor, modifier = Modifier.size(22.dp))
                        }
                    }

                    // Unselected patient card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = BackgroundCard)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(shape = CircleShape, color = HealthWorkerColor.copy(alpha = 0.12f), modifier = Modifier.size(40.dp)) {
                                Box(contentAlignment = Alignment.Center) { Text("SD", style = MaterialTheme.typography.labelMedium, color = HealthWorkerColor) }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Sita Devi", style = MaterialTheme.typography.titleSmall, color = TextPrimary)
                                Text("58y • FEMALE • Fever & cough", style = MaterialTheme.typography.bodySmall, color = TextSecondary, maxLines = 1)
                            }
                        }
                    }
                }

                // ── Bottom pinned call controls ──
                Surface(modifier = Modifier.fillMaxWidth(), color = BackgroundCard, shadowElevation = 8.dp) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Button(
                                onClick = {},
                                modifier = Modifier.weight(1f).height(52.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = DoctorColor)
                            ) {
                                Icon(Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(22.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Video Call", style = MaterialTheme.typography.titleSmall)
                            }
                            Button(
                                onClick = {},
                                modifier = Modifier.weight(1f).height(52.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CallAcceptGreen)
                            ) {
                                Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(22.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Phone Call", style = MaterialTheme.typography.titleSmall)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Connect Doctor - No Patient Selected")
@Composable
private fun HWConnectDoctorScreenNoSelectionPreview() {
    MaterialTheme {
        Scaffold(containerColor = BackgroundPage) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // ── Top bar ──
                Surface(modifier = Modifier.fillMaxWidth(), color = HeaderNavy, shadowElevation = 4.dp) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {}, modifier = Modifier.size(40.dp)) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                            Surface(shape = RoundedCornerShape(8.dp), color = DoctorColor.copy(alpha = 0.25f), modifier = Modifier.size(34.dp)) {
                                Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.Videocam, contentDescription = "Video", tint = Color.White, modifier = Modifier.size(19.dp)) }
                            }
                            Surface(shape = RoundedCornerShape(8.dp), color = CallAcceptGreen.copy(alpha = 0.25f), modifier = Modifier.size(34.dp)) {
                                Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.Phone, contentDescription = "Phone", tint = Color.White, modifier = Modifier.size(19.dp)) }
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Doctor Consultation", style = MaterialTheme.typography.titleSmall, color = Color.White, maxLines = 1)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Surface(shape = RoundedCornerShape(12.dp), color = Color.White.copy(alpha = 0.12f)) {
                            Text("Select Patient", style = MaterialTheme.typography.labelSmall, color = Color.White, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                        }
                    }
                }

                Surface(modifier = Modifier.fillMaxWidth(), color = DoctorBg) {
                    Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = DoctorColor, modifier = Modifier.size(16.dp))
                        Text("Connect with a Hindi-speaking doctor via video or phone", style = MaterialTheme.typography.bodySmall, color = DoctorColor, maxLines = 1)
                    }
                }

                Column(
                    modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Language", style = MaterialTheme.typography.titleSmall, color = HealthWorkerColor)
                    Surface(modifier = Modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(12.dp), color = BackgroundCard) {
                        Row(modifier = Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("Hindi", style = MaterialTheme.typography.bodyMedium, color = TextPrimary, modifier = Modifier.weight(1f))
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = TextMuted)
                        }
                    }

                    Text("Select Patient", style = MaterialTheme.typography.titleSmall, color = HealthWorkerColor)

                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = BackgroundCard)) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Surface(shape = CircleShape, color = HealthWorkerColor.copy(alpha = 0.12f), modifier = Modifier.size(40.dp)) {
                                Box(contentAlignment = Alignment.Center) { Text("RK", style = MaterialTheme.typography.labelMedium, color = HealthWorkerColor) }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Ramesh Kumar", style = MaterialTheme.typography.titleSmall, color = TextPrimary)
                                Text("65y • MALE • Chest pain", style = MaterialTheme.typography.bodySmall, color = TextSecondary, maxLines = 1)
                            }
                        }
                    }

                    Text("Select a patient above to enable call options", style = MaterialTheme.typography.bodySmall, color = StatusAlertText, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                }

                // ── Bottom pinned call controls (disabled) ──
                Surface(modifier = Modifier.fillMaxWidth(), color = BackgroundCard, shadowElevation = 8.dp) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Button(
                                onClick = {}, modifier = Modifier.weight(1f).height(52.dp), enabled = false,
                                shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(containerColor = DoctorColor, disabledContainerColor = DoctorColor.copy(alpha = 0.3f))
                            ) {
                                Icon(Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(22.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Video Call", style = MaterialTheme.typography.titleSmall)
                            }
                            Button(
                                onClick = {}, modifier = Modifier.weight(1f).height(52.dp), enabled = false,
                                shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(containerColor = CallAcceptGreen, disabledContainerColor = CallAcceptGreen.copy(alpha = 0.3f))
                            ) {
                                Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(22.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Phone Call", style = MaterialTheme.typography.titleSmall)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ============ HW Video Call Screen ============

@Composable
fun HWVideoCallScreen(
    viewModel: HealthWorkerModuleViewModel,
    onCallEnded: () -> Unit,
    onBack: () -> Unit
) {
    val isSearchingDoctor by viewModel.isSearchingDoctor.collectAsState()
    val matchedDoctor by viewModel.matchedDoctor.collectAsState()
    val isConnecting by viewModel.isConnecting.collectAsState()
    val isCallActive by viewModel.isCallActive.collectAsState()
    val isPhoneCall by viewModel.isPhoneCall.collectAsState()
    val callDuration by viewModel.callDuration.collectAsState()
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()
    val selectedCallPatient by viewModel.selectedCallPatient.collectAsState()
    val callStatus by viewModel.callStatus.collectAsState()

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.3f,
        animationSpec = infiniteRepeatable(animation = tween(800, easing = FastOutSlowInEasing), repeatMode = RepeatMode.Reverse),
        label = "pulse"
    )

    val durationText = remember(callDuration) {
        val minutes = callDuration / 60
        val seconds = callDuration % 60
        String.format("%02d:%02d", minutes, seconds)
    }

    Scaffold { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding).background(CallDarkBg)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // ── Top bar: call type icons side-by-side + patient info (minimized) ──
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White.copy(alpha = 0.08f))
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Left: call type icons side by side
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        // Video call icon
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (!isPhoneCall && isCallActive) DoctorColor else Color.White.copy(alpha = 0.12f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Videocam, contentDescription = "Video", tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                        }
                        // Phone call icon
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isPhoneCall && isCallActive) CallAcceptGreen else Color.White.copy(alpha = 0.12f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Phone, contentDescription = "Phone", tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                        }
                    }

                    // Center: patient name (if available)
                    if (selectedCallPatient != null) {
                        Text(
                            selectedCallPatient!!.fullName,
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.White,
                            maxLines = 1
                        )
                    }

                    // Right: duration or status
                    Surface(shape = RoundedCornerShape(12.dp), color = Color.White.copy(alpha = 0.12f)) {
                        Text(
                            if (isCallActive) durationText else callStatus.replace("_", " ").replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                // ── Call status indicator bar ──
                if (callStatus.isNotEmpty() && !isCallActive) {
                    val (statusText, statusColor, statusBg) = when (callStatus) {
                        "searching" -> Triple("Searching for available doctor...", Color.White, Color.White.copy(alpha = 0.1f))
                        "doctor_busy" -> Triple("Doctor is busy, finding another...", StatusBusyText, StatusBusyBg.copy(alpha = 0.3f))
                        "no_doctor" -> Triple("No doctor available", StatusAlertText, StatusAlertBg.copy(alpha = 0.3f))
                        "ringing" -> Triple("Ringing doctor...", CallAcceptGreen, CallAcceptGreen.copy(alpha = 0.15f))
                        else -> Triple(callStatus, Color.White, Color.Transparent)
                    }
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = statusBg
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            when (callStatus) {
                                "doctor_busy" -> Icon(Icons.Default.DoNotDisturb, contentDescription = null, tint = statusColor, modifier = Modifier.size(18.dp))
                                "no_doctor" -> Icon(Icons.Default.PersonOff, contentDescription = null, tint = statusColor, modifier = Modifier.size(18.dp))
                                "ringing" -> Icon(Icons.Default.RingVolume, contentDescription = null, tint = statusColor, modifier = Modifier.size(18.dp))
                                else -> CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                            }
                            Text(statusText, style = MaterialTheme.typography.bodySmall, color = statusColor)
                        }
                    }
                }

                // ── Main content area ──
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(24.dp)
                    ) {
                        when {
                            callStatus == "searching" || (isSearchingDoctor && callStatus != "doctor_busy") -> {
                                Icon(Icons.Default.Search, contentDescription = null, tint = Color.White.copy(alpha = 0.5f), modifier = Modifier.size(64.dp))
                                Text("Finding a doctor...", style = MaterialTheme.typography.headlineSmall, color = Color.White)
                                Text("Looking for doctors who speak $selectedLanguage", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.7f), textAlign = TextAlign.Center)
                                CircularProgressIndicator(color = Color.White, strokeWidth = 3.dp)
                            }
                            callStatus == "doctor_busy" -> {
                                Icon(Icons.Default.DoNotDisturb, contentDescription = null, tint = StatusBusyText, modifier = Modifier.size(64.dp))
                                Text("Doctor Busy", style = MaterialTheme.typography.headlineSmall, color = Color.White)
                                Text("The matched doctor is currently busy. Searching for another available doctor...", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.7f), textAlign = TextAlign.Center)
                                CircularProgressIndicator(color = StatusBusyText, strokeWidth = 3.dp)
                            }
                            callStatus == "no_doctor" -> {
                                Icon(Icons.Default.PersonOff, contentDescription = null, tint = StatusAlertText, modifier = Modifier.size(64.dp))
                                Text("No Doctor Available", style = MaterialTheme.typography.headlineSmall, color = Color.White)
                                Text("No doctors speaking $selectedLanguage are available right now. Please try again later.", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.7f), textAlign = TextAlign.Center)
                                Spacer(modifier = Modifier.height(16.dp))
                                LargeButton(text = "Go Back", onClick = { viewModel.resetCallState(); onBack() }, color = HealthWorkerColor)
                            }
                            isConnecting || callStatus == "ringing" -> {
                                DoctorAvatarImage(doctorName = matchedDoctor!!.name, modifier = Modifier.size(120.dp).scale(pulseScale))
                                Text(
                                    if (isPhoneCall) "Calling..." else "Connecting...",
                                    style = MaterialTheme.typography.headlineMedium, color = Color.White
                                )
                                Text(matchedDoctor!!.name, style = MaterialTheme.typography.titleMedium, color = Color.White.copy(alpha = 0.9f))
                                Text("${matchedDoctor!!.specialty} • ${matchedDoctor!!.qualification}", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.6f))
                                Text("Languages: ${matchedDoctor!!.languages.joinToString(", ")}", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.5f))
                                CircularProgressIndicator(color = Color.White, strokeWidth = 3.dp)
                            }
                            isCallActive -> {
                                if (isPhoneCall) {
                                    DoctorAvatarImage(doctorName = matchedDoctor!!.name, modifier = Modifier.size(100.dp))
                                    Text(matchedDoctor!!.name, style = MaterialTheme.typography.headlineMedium, color = Color.White)
                                    Text(matchedDoctor!!.specialty, style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.7f))
                                    Surface(shape = RoundedCornerShape(20.dp), color = CallAcceptGreen.copy(alpha = 0.3f)) {
                                        Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Icon(Icons.Default.Phone, contentDescription = null, tint = CallAcceptGreen, modifier = Modifier.size(16.dp))
                                            Text("Phone Call Active • $durationText", style = MaterialTheme.typography.labelMedium, color = CallAcceptGreen)
                                        }
                                    }
                                    // Sound waves
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                                        repeat(5) { i ->
                                            val height by infiniteTransition.animateFloat(
                                                initialValue = 12f, targetValue = 36f,
                                                animationSpec = infiniteRepeatable(animation = tween(400 + i * 100, easing = FastOutSlowInEasing), repeatMode = RepeatMode.Reverse),
                                                label = "wave$i"
                                            )
                                            Box(modifier = Modifier.width(6.dp).height(height.dp).clip(RoundedCornerShape(3.dp)).background(CallAcceptGreen.copy(alpha = 0.6f)))
                                        }
                                    }
                                } else {
                                    DoctorAvatarImage(doctorName = matchedDoctor!!.name, modifier = Modifier.size(80.dp))
                                    Text(matchedDoctor!!.name, style = MaterialTheme.typography.titleLarge, color = Color.White)
                                    Surface(shape = RoundedCornerShape(20.dp), color = DoctorColor.copy(alpha = 0.3f)) {
                                        Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Icon(Icons.Default.Videocam, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                            Text("Video Call • $durationText", style = MaterialTheme.typography.labelMedium, color = Color.White)
                                        }
                                    }
                                    // Mock video area
                                    Surface(modifier = Modifier.fillMaxWidth().height(180.dp), shape = RoundedCornerShape(16.dp), color = HeaderNavyLighter) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                DoctorAvatarImage(doctorName = matchedDoctor!!.name, modifier = Modifier.size(64.dp))
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text("Video Call Active", color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.bodySmall)
                                            }
                                        }
                                        Box(modifier = Modifier.fillMaxSize().padding(8.dp), contentAlignment = Alignment.BottomEnd) {
                                            Surface(modifier = Modifier.size(width = 70.dp, height = 90.dp), shape = RoundedCornerShape(8.dp), color = Color.DarkGray) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Icon(Icons.Default.Person, contentDescription = null, tint = Color.White.copy(alpha = 0.5f), modifier = Modifier.size(28.dp))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // ── Bottom call controls ──
                if (isCallActive) {
                    Row(
                        modifier = Modifier.fillMaxWidth().background(Color.White.copy(alpha = 0.06f)).padding(vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FilledIconButton(onClick = { }, modifier = Modifier.size(56.dp), colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color.White.copy(alpha = 0.15f))) {
                            Icon(Icons.Default.MicOff, contentDescription = "Mute", tint = Color.White)
                        }
                        FilledIconButton(onClick = { viewModel.endCall(); onCallEnded() }, modifier = Modifier.size(64.dp), colors = IconButtonDefaults.filledIconButtonColors(containerColor = CallDeclineRed)) {
                            Icon(Icons.Default.CallEnd, contentDescription = "End Call", tint = Color.White, modifier = Modifier.size(28.dp))
                        }
                        FilledIconButton(onClick = { }, modifier = Modifier.size(56.dp), colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color.White.copy(alpha = 0.15f))) {
                            Icon(if (isPhoneCall) Icons.Default.VolumeUp else Icons.Default.VideocamOff, contentDescription = null, tint = Color.White)
                        }
                    }
                }
            }
        }
    }
}

// ============ Doctor Avatar Image Composable ============

@Composable
fun DoctorAvatarImage(
    doctorName: String,
    modifier: Modifier = Modifier
) {
    // Generate initials and a color from the doctor name for the demo
    val initials = doctorName
        .removePrefix("Dr. ")
        .split(" ")
        .take(2)
        .joinToString("") { it.first().uppercase() }

    Surface(
        shape = CircleShape,
        color = DoctorColor,
        modifier = modifier
    ) {
        Box(contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxSize(0.45f)
                )
                Text(
                    initials,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    ),
                    color = Color.White
                )
            }
        }
    }
}

// ============ Step 1: Personal Info ============

@Composable
fun HWRegStep1Screen(viewModel: HealthWorkerModuleViewModel, onNext: () -> Unit, onBack: () -> Unit) {
    val fullName by viewModel.fullName.collectAsState()
    val guardianName by viewModel.guardianName.collectAsState()
    val gender by viewModel.gender.collectAsState()
    val age by viewModel.age.collectAsState()
    val dob by viewModel.dob.collectAsState()
    val mobile by viewModel.mobile.collectAsState()
    val aadhaar by viewModel.aadhaar.collectAsState()
    val stepLabels = listOf("Personal", "Address", "Vitals", "History", "Docs", "Review")

    Scaffold(topBar = { AppTopBar(title = stringResource(R.string.step_personal_info), onBack = onBack) }, containerColor = BackgroundPage) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            StepProgressIndicator(1, 6, stepLabels, Modifier.padding(horizontal = 16.dp, vertical = 8.dp), HealthWorkerColor)
            Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                LargeTextField(value = fullName, onValueChange = viewModel::setFullName, label = stringResource(R.string.full_name))
                LargeTextField(value = guardianName, onValueChange = viewModel::setGuardianName, label = stringResource(R.string.guardian_name))
                SectionHeader(stringResource(R.string.gender), moduleColor = HealthWorkerColor)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Gender.entries.forEach { g ->
                        SelectableChip(text = when (g) { Gender.MALE -> stringResource(R.string.male); Gender.FEMALE -> stringResource(R.string.female); Gender.OTHER -> stringResource(R.string.other) }, selected = gender == g, onClick = { viewModel.setGender(g) })
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    LargeTextField(value = age, onValueChange = viewModel::setAge, label = stringResource(R.string.age), keyboardType = KeyboardType.Number, modifier = Modifier.weight(1f))
                    LargeTextField(value = dob, onValueChange = viewModel::setDob, label = stringResource(R.string.date_of_birth), modifier = Modifier.weight(1.5f), trailingIcon = { Icon(Icons.Default.CalendarToday, null, tint = BrandPrimary) })
                }
                LargeTextField(value = mobile, onValueChange = viewModel::setMobile, label = stringResource(R.string.mobile_number), keyboardType = KeyboardType.Phone)
                LargeTextField(value = aadhaar, onValueChange = viewModel::setAadhaar, label = stringResource(R.string.aadhaar_number), keyboardType = KeyboardType.Number)
            }
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f).height(52.dp), shape = RoundedCornerShape(24.dp)) { Text(stringResource(R.string.back)) }
                Button(onClick = onNext, modifier = Modifier.weight(1f).height(52.dp), enabled = fullName.isNotBlank(), shape = RoundedCornerShape(24.dp), colors = ButtonDefaults.buttonColors(containerColor = HealthWorkerColor)) { Text(stringResource(R.string.next)) }
            }
        }
    }
}

// ============ Step 2: Address ============

@Composable
fun HWRegStep2Screen(viewModel: HealthWorkerModuleViewModel, onNext: () -> Unit, onBack: () -> Unit) {
    val regVillage by viewModel.regVillage.collectAsState()
    val regDistrict by viewModel.regDistrict.collectAsState()
    val regState by viewModel.regState.collectAsState()
    val districts by viewModel.districts.collectAsState()
    val regVillages by viewModel.regVillages.collectAsState()
    val states by viewModel.states.collectAsState()
    val stepLabels = listOf("Personal", "Address", "Vitals", "History", "Docs", "Review")

    Scaffold(topBar = { AppTopBar(title = stringResource(R.string.step_address), onBack = onBack) }, containerColor = BackgroundPage) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            StepProgressIndicator(2, 6, stepLabels, Modifier.padding(horizontal = 16.dp, vertical = 8.dp), HealthWorkerColor)
            Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                DropdownField(value = regState, onValueChange = viewModel::setRegState, label = stringResource(R.string.state), options = states)
                DropdownField(value = regDistrict, onValueChange = viewModel::setRegDistrict, label = stringResource(R.string.district), options = districts, enabled = regState.isNotBlank())
                DropdownField(value = regVillage, onValueChange = viewModel::setRegVillage, label = stringResource(R.string.village), options = regVillages, enabled = regDistrict.isNotBlank())
            }
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f).height(52.dp), shape = RoundedCornerShape(24.dp)) { Text(stringResource(R.string.back)) }
                Button(onClick = onNext, modifier = Modifier.weight(1f).height(52.dp), shape = RoundedCornerShape(24.dp), colors = ButtonDefaults.buttonColors(containerColor = HealthWorkerColor)) { Text(stringResource(R.string.next)) }
            }
        }
    }
}

// ============ Step 3: Vitals ============

@Composable
fun HWRegStep3Screen(viewModel: HealthWorkerModuleViewModel, onNext: () -> Unit, onBack: () -> Unit) {
    val weight by viewModel.weight.collectAsState()
    val temperature by viewModel.temperature.collectAsState()
    val tempUnit by viewModel.tempUnit.collectAsState()
    val bpSystolic by viewModel.bpSystolic.collectAsState()
    val bpDiastolic by viewModel.bpDiastolic.collectAsState()
    val bloodSugar by viewModel.bloodSugar.collectAsState()
    val hemoglobin by viewModel.hemoglobin.collectAsState()
    val spo2 by viewModel.spo2.collectAsState()
    val pulseRate by viewModel.pulseRate.collectAsState()
    val stepLabels = listOf("Personal", "Address", "Vitals", "History", "Docs", "Review")

    Scaffold(topBar = { AppTopBar(title = stringResource(R.string.step_vitals), onBack = onBack) }, containerColor = BackgroundPage) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            StepProgressIndicator(3, 6, stepLabels, Modifier.padding(horizontal = 16.dp, vertical = 8.dp), HealthWorkerColor)
            Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                LargeTextField(value = weight, onValueChange = viewModel::setWeight, label = stringResource(R.string.weight_kg), keyboardType = KeyboardType.Decimal)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    LargeTextField(value = temperature, onValueChange = viewModel::setTemperature, label = "${stringResource(R.string.temperature)} (°$tempUnit)", keyboardType = KeyboardType.Decimal, modifier = Modifier.weight(1f))
                    SelectableChip(text = "°F", selected = tempUnit == "F", onClick = { viewModel.setTempUnit("F") })
                    SelectableChip(text = "°C", selected = tempUnit == "C", onClick = { viewModel.setTempUnit("C") })
                }
                SectionHeader(stringResource(R.string.blood_pressure), moduleColor = HealthWorkerColor)
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    LargeTextField(value = bpSystolic, onValueChange = viewModel::setBpSystolic, label = stringResource(R.string.systolic), keyboardType = KeyboardType.Number, modifier = Modifier.weight(1f))
                    LargeTextField(value = bpDiastolic, onValueChange = viewModel::setBpDiastolic, label = stringResource(R.string.diastolic), keyboardType = KeyboardType.Number, modifier = Modifier.weight(1f))
                }
                LargeTextField(value = bloodSugar, onValueChange = viewModel::setBloodSugar, label = stringResource(R.string.blood_sugar), keyboardType = KeyboardType.Decimal)
                LargeTextField(value = hemoglobin, onValueChange = viewModel::setHemoglobin, label = stringResource(R.string.hemoglobin), keyboardType = KeyboardType.Decimal)
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    LargeTextField(value = spo2, onValueChange = viewModel::setSpo2, label = stringResource(R.string.spo2), keyboardType = KeyboardType.Number, modifier = Modifier.weight(1f))
                    LargeTextField(value = pulseRate, onValueChange = viewModel::setPulseRate, label = stringResource(R.string.pulse_rate), keyboardType = KeyboardType.Number, modifier = Modifier.weight(1f))
                }
            }
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f).height(52.dp), shape = RoundedCornerShape(24.dp)) { Text(stringResource(R.string.back)) }
                Button(onClick = onNext, modifier = Modifier.weight(1f).height(52.dp), shape = RoundedCornerShape(24.dp), colors = ButtonDefaults.buttonColors(containerColor = HealthWorkerColor)) { Text(stringResource(R.string.next)) }
            }
        }
    }
}

// ============ Step 4: Medical History ============

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HWRegStep4Screen(viewModel: HealthWorkerModuleViewModel, onNext: () -> Unit, onBack: () -> Unit) {
    val primaryComplaint by viewModel.primaryComplaint.collectAsState()
    val selectedSymptoms by viewModel.selectedSymptoms.collectAsState()
    val selectedConditions by viewModel.selectedConditions.collectAsState()
    val alcohol by viewModel.alcohol.collectAsState()
    val tobacco by viewModel.tobacco.collectAsState()
    val drugs by viewModel.drugs.collectAsState()
    val stepLabels = listOf("Personal", "Address", "Vitals", "History", "Docs", "Review")

    Scaffold(topBar = { AppTopBar(title = stringResource(R.string.step_medical_history), onBack = onBack) }, containerColor = BackgroundPage) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            StepProgressIndicator(4, 6, stepLabels, Modifier.padding(horizontal = 16.dp, vertical = 8.dp), HealthWorkerColor)
            Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                LargeTextField(value = primaryComplaint, onValueChange = viewModel::setPrimaryComplaint, label = stringResource(R.string.primary_complaint), singleLine = false, minLines = 3)
                SectionHeader(stringResource(R.string.symptoms), moduleColor = HealthWorkerColor)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    viewModel.symptoms.forEach { s -> SelectableChip(text = s, selected = selectedSymptoms.contains(s), onClick = { viewModel.toggleSymptom(s) }) }
                }
                SectionHeader(stringResource(R.string.known_conditions), moduleColor = HealthWorkerColor)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    viewModel.conditions.forEach { c -> SelectableChip(text = c, selected = selectedConditions.contains(c), onClick = { viewModel.toggleCondition(c) }) }
                }
                SectionHeader(stringResource(R.string.lifestyle_history), moduleColor = HealthWorkerColor)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SelectableChip(text = stringResource(R.string.alcohol), selected = alcohol, onClick = { viewModel.setAlcohol(!alcohol) })
                    SelectableChip(text = stringResource(R.string.tobacco), selected = tobacco, onClick = { viewModel.setTobacco(!tobacco) })
                    SelectableChip(text = stringResource(R.string.drugs), selected = drugs, onClick = { viewModel.setDrugs(!drugs) })
                }
            }
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f).height(52.dp), shape = RoundedCornerShape(24.dp)) { Text(stringResource(R.string.back)) }
                Button(onClick = onNext, modifier = Modifier.weight(1f).height(52.dp), shape = RoundedCornerShape(24.dp), colors = ButtonDefaults.buttonColors(containerColor = HealthWorkerColor)) { Text(stringResource(R.string.next)) }
            }
        }
    }
}

// ============ Step 5: Documents ============

@Composable
fun HWRegStep5Screen(viewModel: HealthWorkerModuleViewModel, onNext: () -> Unit, onBack: () -> Unit) {
    val uploadedFiles by viewModel.uploadedFiles.collectAsState()
    val stepLabels = listOf("Personal", "Address", "Vitals", "History", "Docs", "Review")

    Scaffold(topBar = { AppTopBar(title = stringResource(R.string.step_documents), onBack = onBack) }, containerColor = BackgroundPage) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            StepProgressIndicator(5, 6, stepLabels, Modifier.padding(horizontal = 16.dp, vertical = 8.dp), HealthWorkerColor)
            Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                SectionHeader(stringResource(R.string.upload_reports), moduleColor = HealthWorkerColor)
                UploadZone(label = "Tap to upload Report", icon = Icons.Default.UploadFile, onClick = { viewModel.addMockDocument("report") })
                SectionHeader(stringResource(R.string.upload_diagnostics), moduleColor = HealthWorkerColor)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    UploadZone(label = "Gallery", icon = Icons.Default.Image, onClick = { viewModel.addMockDocument("diagnostic") }, modifier = Modifier.weight(1f))
                    UploadZone(label = "Camera", icon = Icons.Default.CameraAlt, onClick = { viewModel.addMockDocument("diagnostic") }, modifier = Modifier.weight(1f))
                }
                if (uploadedFiles.isNotEmpty()) {
                    SectionHeader("Uploaded Files (${uploadedFiles.size})", moduleColor = HealthWorkerColor, icon = Icons.Default.AttachFile)
                    uploadedFiles.forEach { doc ->
                        UploadedFileRow(fileName = doc.name, fileType = doc.type, onRemove = { viewModel.removeDocument(doc) }, moduleColor = HealthWorkerColor)
                    }
                }
            }
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f).height(52.dp), shape = RoundedCornerShape(24.dp)) { Text(stringResource(R.string.back)) }
                Button(onClick = onNext, modifier = Modifier.weight(1f).height(52.dp), shape = RoundedCornerShape(24.dp), colors = ButtonDefaults.buttonColors(containerColor = HealthWorkerColor)) { Text(stringResource(R.string.next)) }
            }
        }
    }
}

// ============ Step 6: Review & Submit ============

@Composable
fun HWRegStep6Screen(viewModel: HealthWorkerModuleViewModel, onSubmitSuccess: () -> Unit, onBack: () -> Unit) {
    val fullName by viewModel.fullName.collectAsState()
    val guardianName by viewModel.guardianName.collectAsState()
    val gender by viewModel.gender.collectAsState()
    val age by viewModel.age.collectAsState()
    val dob by viewModel.dob.collectAsState()
    val mobile by viewModel.mobile.collectAsState()
    val aadhaar by viewModel.aadhaar.collectAsState()
    val regVillage by viewModel.regVillage.collectAsState()
    val regDistrict by viewModel.regDistrict.collectAsState()
    val regState by viewModel.regState.collectAsState()
    val weight by viewModel.weight.collectAsState()
    val temperature by viewModel.temperature.collectAsState()
    val bpSystolic by viewModel.bpSystolic.collectAsState()
    val bpDiastolic by viewModel.bpDiastolic.collectAsState()
    val bloodSugar by viewModel.bloodSugar.collectAsState()
    val hemoglobin by viewModel.hemoglobin.collectAsState()
    val spo2 by viewModel.spo2.collectAsState()
    val pulseRate by viewModel.pulseRate.collectAsState()
    val primaryComplaint by viewModel.primaryComplaint.collectAsState()
    val selectedSymptoms by viewModel.selectedSymptoms.collectAsState()
    val selectedConditions by viewModel.selectedConditions.collectAsState()
    val uploadedFiles by viewModel.uploadedFiles.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val registeredPatient by viewModel.registeredPatient.collectAsState()
    val stepLabels = listOf("Personal", "Address", "Vitals", "History", "Docs", "Review")

    Scaffold(topBar = { AppTopBar(title = stringResource(R.string.step_review), onBack = onBack) }, containerColor = BackgroundPage) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (registeredPatient != null) {
                // Success screen
                Column(modifier = Modifier.fillMaxSize().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusDoneText, modifier = Modifier.size(80.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(stringResource(R.string.registration_success), style = MaterialTheme.typography.headlineMedium, color = StatusDoneText)
                    Spacer(modifier = Modifier.height(24.dp))
                    UniqueIdDisplay(patientId = registeredPatient!!.id)
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(onClick = { }, shape = RoundedCornerShape(24.dp)) { Icon(Icons.Default.Share, contentDescription = null); Spacer(Modifier.width(4.dp)); Text(stringResource(R.string.share_id)) }
                        OutlinedButton(onClick = { }, shape = RoundedCornerShape(24.dp)) { Icon(Icons.Default.ContentCopy, contentDescription = null); Spacer(Modifier.width(4.dp)); Text(stringResource(R.string.copy_id)) }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                    LargeButton(text = stringResource(R.string.done), onClick = { viewModel.resetRegistration(); viewModel.refreshDashboard(); onSubmitSuccess() }, color = HealthWorkerColor)
                }
            } else {
                StepProgressIndicator(6, 6, stepLabels, Modifier.padding(horizontal = 16.dp, vertical = 8.dp), HealthWorkerColor)
                Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    SectionHeader("Personal Information", moduleColor = HealthWorkerColor, icon = Icons.Default.Person)
                    PatientSummaryCard("Name", fullName); PatientSummaryCard("Guardian", guardianName); PatientSummaryCard("Gender", gender.name); PatientSummaryCard("Age", age); PatientSummaryCard("DOB", dob.ifEmpty { "Not provided" }); PatientSummaryCard("Mobile", mobile); PatientSummaryCard("Aadhaar", aadhaar.ifEmpty { "Not provided" })
                    SectionHeader("Address", moduleColor = HealthWorkerColor, icon = Icons.Default.LocationOn)
                    PatientSummaryCard("Village", regVillage); PatientSummaryCard("District", regDistrict); PatientSummaryCard("State", regState)
                    SectionHeader("Vitals", moduleColor = HealthWorkerColor, icon = Icons.Default.MonitorHeart)
                    PatientSummaryCard("Weight", if (weight.isNotEmpty()) "$weight kg" else "—"); PatientSummaryCard("Temperature", if (temperature.isNotEmpty()) "$temperature°" else "—"); PatientSummaryCard("BP", if (bpSystolic.isNotEmpty()) "$bpSystolic/$bpDiastolic mmHg" else "—"); PatientSummaryCard("Blood Sugar", if (bloodSugar.isNotEmpty()) "$bloodSugar mg/dL" else "—"); PatientSummaryCard("Hemoglobin", if (hemoglobin.isNotEmpty()) "$hemoglobin g/dL" else "—"); PatientSummaryCard("SpO2", if (spo2.isNotEmpty()) "$spo2%" else "—"); PatientSummaryCard("Pulse", if (pulseRate.isNotEmpty()) "$pulseRate bpm" else "—")
                    SectionHeader("Medical History", moduleColor = HealthWorkerColor, icon = Icons.Default.MedicalInformation)
                    PatientSummaryCard("Complaint", primaryComplaint.ifEmpty { "—" }); PatientSummaryCard("Symptoms", selectedSymptoms.joinToString(", ").ifEmpty { "—" }); PatientSummaryCard("Conditions", selectedConditions.joinToString(", ").ifEmpty { "—" })
                    SectionHeader("Documents", moduleColor = HealthWorkerColor, icon = Icons.Default.Description)
                    PatientSummaryCard("Files", if (uploadedFiles.isNotEmpty()) "${uploadedFiles.size} file(s)" else "None")
                }
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f).height(52.dp), shape = RoundedCornerShape(24.dp)) { Text(stringResource(R.string.back)) }
                    LargeButton(text = stringResource(R.string.submit), onClick = { viewModel.submitRegistration() }, isLoading = isLoading, color = HealthWorkerColor, icon = Icons.Default.Send, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

// ============ Patient Detail Screen ============

@Composable
fun HWPatientDetailScreen(viewModel: HealthWorkerModuleViewModel, patientId: String, onBack: () -> Unit) {
    val patients by viewModel.recentPatients.collectAsState()
    val patient = patients.find { it.id == patientId }

    Scaffold(topBar = { AppTopBar(title = stringResource(R.string.patient_details), onBack = onBack) }, containerColor = BackgroundPage) { padding ->
        if (patient == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { Text("Patient not found") }
        } else {
            Column(modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = HealthWorkerBg)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(patient.fullName, style = MaterialTheme.typography.headlineMedium, color = HealthWorkerColor)
                        Text("ID: ${patient.id}", style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(4.dp))
                        StatusBadge(patient.status)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                SectionHeader("Personal Info", moduleColor = HealthWorkerColor, icon = Icons.Default.Person)
                PatientSummaryCard("Age", "${patient.age}"); PatientSummaryCard("Gender", patient.gender.name); PatientSummaryCard("Mobile", patient.mobile); PatientSummaryCard("Guardian", patient.guardianName)
                SectionHeader("Address", moduleColor = HealthWorkerColor, icon = Icons.Default.LocationOn)
                PatientSummaryCard("Village", patient.address.village); PatientSummaryCard("District", patient.address.district)
                SectionHeader("Vitals", moduleColor = HealthWorkerColor, icon = Icons.Default.MonitorHeart)
                patient.vitals.let { v -> PatientSummaryCard("Weight", "${v.weight ?: "—"} kg"); PatientSummaryCard("Temp", "${v.temperature ?: "—"}°${v.temperatureUnit}"); PatientSummaryCard("BP", "${v.bpSystolic ?: "—"}/${v.bpDiastolic ?: "—"} mmHg"); PatientSummaryCard("SpO2", "${v.spo2 ?: "—"}%"); PatientSummaryCard("Pulse", "${v.pulseRate ?: "—"} bpm") }
                SectionHeader("Chief Complaint", moduleColor = HealthWorkerColor, icon = Icons.Default.Report)
                Text(patient.medicalHistory.primaryComplaint.ifEmpty { "—" }, style = MaterialTheme.typography.bodyLarge)
                PatientSummaryCard("Registered", patient.registeredAt); PatientSummaryCard("By", patient.registeredBy)
            }
        }
    }
}
