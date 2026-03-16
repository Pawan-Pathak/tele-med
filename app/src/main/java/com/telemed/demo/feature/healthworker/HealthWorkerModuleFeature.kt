package com.telemed.demo.feature.healthworker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telemed.demo.R
import com.telemed.demo.domain.model.*
import com.telemed.demo.domain.usecase.*
import com.telemed.demo.ui.components.*
import com.telemed.demo.ui.responsive.AppTopBar
import com.telemed.demo.ui.theme.*
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
    private val getRecentPatientsUseCase: GetRecentPatientsUseCase
) : ViewModel() {

    private val _spokeName = MutableStateFlow("")
    val spokeName: StateFlow<String> = _spokeName.asStateFlow()
    private val _selectedDistrict = MutableStateFlow("")
    val selectedDistrict: StateFlow<String> = _selectedDistrict.asStateFlow()
    private val _selectedVillage = MutableStateFlow("")
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

    val symptoms = listOf("Fever", "Cough", "Headache", "Fatigue", "Pain", "Breathlessness", "Other")
    val conditions = listOf("Diabetes", "Hypertension", "TB", "Heart Disease", "None")

    init { updateDateTime(); loadData() }

    private fun updateDateTime() {
        _currentDateTime.value = LocalDateTime.now().format(DateTimeFormatter.ofPattern("EEE, dd MMM yyyy  •  hh:mm a"))
    }
    private fun loadData() {
        viewModelScope.launch { _districts.value = getDistrictsUseCase(); _states.value = getStatesUseCase(); _recentPatients.value = getRecentPatientsUseCase() }
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
            DateTimeField(value = currentDateTime, label = stringResource(R.string.date_time))

            Spacer(modifier = Modifier.height(8.dp))

            LargeButton(text = stringResource(R.string.start_session), onClick = { viewModel.saveSession(); onSessionStarted() }, enabled = spokeName.isNotBlank() && selectedDistrict.isNotBlank() && selectedVillage.isNotBlank(), icon = Icons.Default.PlayArrow, color = HealthWorkerColor)
        }
    }
}

// ============ Dashboard ============

@Composable
fun HWDashboardScreen(viewModel: HealthWorkerModuleViewModel, onNewPatient: () -> Unit, onPatientClick: (String) -> Unit, onBack: () -> Unit) {
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

            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                LargeButton(text = stringResource(R.string.new_patient), onClick = onNewPatient, icon = Icons.Default.PersonAdd, color = HealthWorkerColor)

                Spacer(modifier = Modifier.height(20.dp))

                Text(stringResource(R.string.recent_patients), style = MaterialTheme.typography.headlineSmall, color = TextPrimary)
                Spacer(modifier = Modifier.height(8.dp))

                if (recentPatients.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("No patients registered yet.", style = MaterialTheme.typography.bodyMedium, color = TextMuted)
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.weight(1f)) {
                        items(recentPatients) { patient ->
                            PatientListCard(
                                name = patient.fullName,
                                id = patient.id,
                                subtitle = "Age: ${patient.age} · ${patient.gender.name}",
                                status = patient.status,
                                onClick = { onPatientClick(patient.id) }
                            )
                        }
                    }
                }
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
                    SectionHeader("Uploaded Files (${uploadedFiles.size})", moduleColor = HealthWorkerColor)
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
                    SectionHeader("Personal Information", moduleColor = HealthWorkerColor)
                    PatientSummaryCard("Name", fullName); PatientSummaryCard("Guardian", guardianName); PatientSummaryCard("Gender", gender.name); PatientSummaryCard("Age", age); PatientSummaryCard("DOB", dob.ifEmpty { "Not provided" }); PatientSummaryCard("Mobile", mobile); PatientSummaryCard("Aadhaar", aadhaar.ifEmpty { "Not provided" })
                    SectionHeader("Address", moduleColor = HealthWorkerColor)
                    PatientSummaryCard("Village", regVillage); PatientSummaryCard("District", regDistrict); PatientSummaryCard("State", regState)
                    SectionHeader("Vitals", moduleColor = HealthWorkerColor)
                    PatientSummaryCard("Weight", if (weight.isNotEmpty()) "$weight kg" else "—"); PatientSummaryCard("Temperature", if (temperature.isNotEmpty()) "$temperature°" else "—"); PatientSummaryCard("BP", if (bpSystolic.isNotEmpty()) "$bpSystolic/$bpDiastolic mmHg" else "—"); PatientSummaryCard("Blood Sugar", if (bloodSugar.isNotEmpty()) "$bloodSugar mg/dL" else "—"); PatientSummaryCard("Hemoglobin", if (hemoglobin.isNotEmpty()) "$hemoglobin g/dL" else "—"); PatientSummaryCard("SpO2", if (spo2.isNotEmpty()) "$spo2%" else "—"); PatientSummaryCard("Pulse", if (pulseRate.isNotEmpty()) "$pulseRate bpm" else "—")
                    SectionHeader("Medical History", moduleColor = HealthWorkerColor)
                    PatientSummaryCard("Complaint", primaryComplaint.ifEmpty { "—" }); PatientSummaryCard("Symptoms", selectedSymptoms.joinToString(", ").ifEmpty { "—" }); PatientSummaryCard("Conditions", selectedConditions.joinToString(", ").ifEmpty { "—" })
                    SectionHeader("Documents", moduleColor = HealthWorkerColor)
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
                SectionHeader("Personal Info", moduleColor = HealthWorkerColor)
                PatientSummaryCard("Age", "${patient.age}"); PatientSummaryCard("Gender", patient.gender.name); PatientSummaryCard("Mobile", patient.mobile); PatientSummaryCard("Guardian", patient.guardianName)
                SectionHeader("Address", moduleColor = HealthWorkerColor)
                PatientSummaryCard("Village", patient.address.village); PatientSummaryCard("District", patient.address.district)
                SectionHeader("Vitals", moduleColor = HealthWorkerColor)
                patient.vitals.let { v -> PatientSummaryCard("Weight", "${v.weight ?: "—"} kg"); PatientSummaryCard("Temp", "${v.temperature ?: "—"}°${v.temperatureUnit}"); PatientSummaryCard("BP", "${v.bpSystolic ?: "—"}/${v.bpDiastolic ?: "—"} mmHg"); PatientSummaryCard("SpO2", "${v.spo2 ?: "—"}%"); PatientSummaryCard("Pulse", "${v.pulseRate ?: "—"} bpm") }
                SectionHeader("Chief Complaint", moduleColor = HealthWorkerColor)
                Text(patient.medicalHistory.primaryComplaint.ifEmpty { "—" }, style = MaterialTheme.typography.bodyLarge)
                PatientSummaryCard("Registered", patient.registeredAt); PatientSummaryCard("By", patient.registeredBy)
            }
        }
    }
}
