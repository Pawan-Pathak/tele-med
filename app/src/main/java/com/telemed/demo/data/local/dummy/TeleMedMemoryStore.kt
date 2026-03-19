package com.telemed.demo.data.local.dummy

import com.telemed.demo.domain.model.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TeleMedMemoryStore {
    var currentSession: SessionUser? = null
    var spokeLocation: SpokeLocation? = null

    // Patient storage
    val patients = mutableListOf<Patient>()
    val patientQueue = mutableListOf<PatientQueueItem>()

    // Pharmacist state
    var pharmacistConsent: Boolean? = null
    var pharmacistCallInitiated: Boolean = false
    val dispensedMedicines = mutableMapOf<String, Boolean>() // medicine name -> dispensed

    // Doctor state
    var doctorDecision: String = "Pending"
    var doctorConsultationForm: DoctorConsultationForm? = null
    var generatedPrescription: Prescription? = null
    var activeCall: Boolean = false
    var connectedDoctor: Doctor? = null

    // Location data
    val districts = listOf("Bhopal", "Indore", "Sehore", "Jabalpur", "Gwalior")
    val villagesByDistrict = mapOf(
        "Bhopal" to listOf("Berasia", "Phanda", "Sukhi Sewania", "Ratibad", "Misrod"),
        "Indore" to listOf("Sanwer", "Depalpur", "Mhow", "Hatod", "Betma"),
        "Sehore" to listOf("Ashta", "Ichhawar", "Nasrullaganj", "Budhni"),
        "Jabalpur" to listOf("Sihora", "Panagar", "Patan", "Shahpura"),
        "Gwalior" to listOf("Dabra", "Bhitarwar", "Morar", "Banmore")
    )
    val states = listOf("Madhya Pradesh", "Uttar Pradesh", "Rajasthan", "Maharashtra", "Gujarat")

    // Pre-seeded doctors with varied specialties
    val doctors = listOf(
        Doctor("d1", "Dr. Priya Sharma", "MBBS, MD", "General Physician", "PHC Berasia", "MP-MED-1234", 3, true, "Bhopal", listOf("Hindi", "English")),
        Doctor("d2", "Dr. Amit Verma", "MBBS, MD", "General Physician", "CHC Sehore", "MP-MED-2345", 4, true, "Sehore", listOf("Hindi", "English", "Marathi")),
        Doctor("d3", "Dr. Sunita Patel", "MBBS, MD", "General Physician", "PHC Sanwer", "MP-MED-3456", 5, true, "Indore", listOf("Hindi", "Gujarati")),
        Doctor("d4", "Dr. Rajesh Gupta", "MBBS, MD", "General Physician", "DH Jabalpur", "MP-MED-4567", 4, true, "Jabalpur", listOf("Hindi", "English")),
        Doctor("d5", "Dr. Meera Joshi", "MBBS, MD", "General Physician", "PHC Gwalior", "MP-MED-5678", 5, true, "Gwalior", listOf("Hindi", "English", "Urdu")),
        Doctor("d6", "Dr. Kavita Reddy", "MBBS, MD (Pediatrics)", "Pediatrician", "CHC Berasia", "MP-MED-6789", 3, true, "Bhopal", listOf("Hindi", "English")),
        Doctor("d7", "Dr. Sunil Tiwari", "MBBS, MS (Ortho)", "Orthopedic Surgeon", "DH Bhopal", "MP-MED-7890", 6, false, "Bhopal", listOf("Hindi", "English")),
        Doctor("d8", "Dr. Fatima Khan", "MBBS, DGO", "Gynecologist", "PHC Phanda", "MP-MED-8901", 4, true, "Bhopal", listOf("Hindi", "Urdu", "English"))
    )

    // Default health worker for demo
    val defaultHealthWorker = HealthWorker(
        id = "HW-001",
        name = "Anita Sharma",
        phone = "9988776655",
        district = "Bhopal",
        village = "Berasia",
        language = "Hindi"
    )

    // Health worker storage
    var currentHealthWorker: HealthWorker? = null

    // Pre-seeded mock patients
    init {
        seedPatients()
    }

    private fun seedPatients() {
        val now = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

        patients.addAll(listOf(
            Patient(
                id = "ASHA-20260316-0001",
                fullName = "Ramesh Kumar",
                guardianName = "Suresh Kumar",
                gender = Gender.MALE,
                age = 45,
                dob = "1981-03-15",
                mobile = "9876543210",
                aadhaar = "XXXX-XXXX-1234",
                address = Address("Berasia", "Bhopal", "Madhya Pradesh"),
                vitals = Vitals(72f, 99.2f, "F", 130, 85, 140f, 13.5f, 97, 78),
                medicalHistory = MedicalHistory(
                    "Persistent cough and fever for 3 days",
                    listOf("Fever", "Cough", "Fatigue"),
                    listOf("Diabetes", "Hypertension"),
                    LifestyleHistory(false, true, false)
                ),
                documents = listOf(
                    DocumentFile("Blood_Report_March2026.pdf", "report"),
                    DocumentFile("Previous_Prescription.pdf", "report"),
                    DocumentFile("Chest_Xray_2025.jpg", "diagnostic")
                ),
                registeredBy = "Spoke Berasia",
                registeredAt = now.minusHours(2).format(formatter),
                status = ConsultationStatus.WAITING,
                consentGiven = true
            ),
            Patient(
                id = "ASHA-20260316-0002",
                fullName = "Sunita Devi",
                guardianName = "Mohan Lal",
                gender = Gender.FEMALE,
                age = 32,
                dob = "1994-07-22",
                mobile = "9876543211",
                aadhaar = "XXXX-XXXX-2345",
                address = Address("Phanda", "Bhopal", "Madhya Pradesh"),
                vitals = Vitals(58f, 100.4f, "F", 110, 70, 95f, 10.2f, 98, 88),
                medicalHistory = MedicalHistory(
                    "Headache and body pain",
                    listOf("Headache", "Pain", "Fever"),
                    listOf("None"),
                    LifestyleHistory()
                ),
                documents = listOf(
                    DocumentFile("Hemoglobin_Test.pdf", "report")
                ),
                registeredBy = "Spoke Berasia",
                registeredAt = now.minusHours(1).format(formatter),
                status = ConsultationStatus.WAITING,
                consentGiven = true
            ),
            Patient(
                id = "ASHA-20260316-0003",
                fullName = "Abdul Rahman",
                guardianName = "Mohammed Ismail",
                gender = Gender.MALE,
                age = 55,
                dob = "1971-11-08",
                mobile = "9876543212",
                aadhaar = "XXXX-XXXX-3456",
                address = Address("Sanwer", "Indore", "Madhya Pradesh"),
                vitals = Vitals(80f, 98.6f, "F", 150, 95, 220f, 12.8f, 95, 82),
                medicalHistory = MedicalHistory(
                    "Chest pain and breathlessness",
                    listOf("Pain", "Breathlessness"),
                    listOf("Diabetes", "Heart Disease"),
                    LifestyleHistory(true, true, false)
                ),
                documents = listOf(
                    DocumentFile("ECG_Report.pdf", "diagnostic"),
                    DocumentFile("Lipid_Profile.pdf", "report"),
                    DocumentFile("Echo_Report_2025.pdf", "diagnostic")
                ),
                registeredBy = "Spoke Berasia",
                registeredAt = now.minusMinutes(45).format(formatter),
                status = ConsultationStatus.IN_PROGRESS,
                consentGiven = true
            ),
            Patient(
                id = "ASHA-20260316-0004",
                fullName = "Lakshmi Bai",
                guardianName = "Rajendra Prasad",
                gender = Gender.FEMALE,
                age = 28,
                dob = "1998-01-10",
                mobile = "9876543213",
                aadhaar = "XXXX-XXXX-4567",
                address = Address("Ashta", "Sehore", "Madhya Pradesh"),
                vitals = Vitals(52f, 99.8f, "F", 100, 65, 88f, 8.5f, 99, 72),
                medicalHistory = MedicalHistory(
                    "Weakness and dizziness",
                    listOf("Fatigue"),
                    listOf("None"),
                    LifestyleHistory()
                ),
                registeredBy = "Spoke Berasia",
                registeredAt = now.minusMinutes(30).format(formatter),
                status = ConsultationStatus.REGISTERED
            ),  // consent not yet asked
            Patient(
                id = "ASHA-20260316-0005",
                fullName = "Vijay Singh",
                guardianName = "Bhagwan Singh",
                gender = Gender.MALE,
                age = 60,
                dob = "1966-05-20",
                mobile = "9876543214",
                aadhaar = "XXXX-XXXX-5678",
                address = Address("Depalpur", "Indore", "Madhya Pradesh"),
                vitals = Vitals(75f, 98.4f, "F", 145, 92, 180f, 14.0f, 96, 76),
                medicalHistory = MedicalHistory(
                    "Joint pain and swelling in knees",
                    listOf("Pain"),
                    listOf("Hypertension"),
                    LifestyleHistory(false, false, false)
                ),
                documents = listOf(
                    DocumentFile("Knee_Xray.jpg", "diagnostic"),
                    DocumentFile("Uric_Acid_Report.pdf", "report")
                ),
                registeredBy = "Spoke Berasia",
                registeredAt = now.minusMinutes(15).format(formatter),
                status = ConsultationStatus.COMPLETED,
                consentGiven = true
            ),
            Patient(
                id = "ASHA-20260315-0006",
                fullName = "Meena Kumari",
                guardianName = "Ravi Shankar",
                gender = Gender.FEMALE,
                age = 40,
                dob = "1986-09-12",
                mobile = "9876543215",
                aadhaar = "XXXX-XXXX-6789",
                address = Address("Ichhawar", "Sehore", "Madhya Pradesh"),
                vitals = Vitals(65f, 101.2f, "F", 120, 80, 110f, 11.5f, 94, 92),
                medicalHistory = MedicalHistory(
                    "High fever with chills",
                    listOf("Fever", "Cough", "Breathlessness"),
                    listOf("TB"),
                    LifestyleHistory()
                ),
                documents = listOf(
                    DocumentFile("Sputum_Test.pdf", "report"),
                    DocumentFile("Chest_Xray_March.jpg", "diagnostic")
                ),
                registeredBy = "Spoke Berasia",
                registeredAt = now.minusDays(1).format(formatter),
                status = ConsultationStatus.COMPLETED,
                consentGiven = true
            ),
            Patient(
                id = "ASHA-20260315-0007",
                fullName = "Gopal Das",
                guardianName = "Hari Das",
                gender = Gender.MALE,
                age = 50,
                dob = "1976-02-28",
                mobile = "9876543216",
                aadhaar = "XXXX-XXXX-7890",
                address = Address("Mhow", "Indore", "Madhya Pradesh"),
                vitals = Vitals(85f, 98.8f, "F", 155, 98, 250f, 13.0f, 97, 80),
                medicalHistory = MedicalHistory(
                    "Frequent urination and thirst",
                    listOf("Fatigue"),
                    listOf("Diabetes"),
                    LifestyleHistory(true, false, false)
                ),
                documents = listOf(
                    DocumentFile("HbA1c_Report.pdf", "report"),
                    DocumentFile("RFT_Report.pdf", "report")
                ),
                registeredBy = "Spoke Berasia",
                registeredAt = now.minusDays(1).format(formatter),
                status = ConsultationStatus.COMPLETED,
                consentGiven = true
            ),
            // Patient with declined consent (showcases declined state)
            Patient(
                id = "ASHA-20260316-0008",
                fullName = "Sita Ram Yadav",
                guardianName = "Durga Prasad Yadav",
                gender = Gender.FEMALE,
                age = 65,
                dob = "1961-04-18",
                mobile = "9876543217",
                aadhaar = "XXXX-XXXX-8901",
                address = Address("Ratibad", "Bhopal", "Madhya Pradesh"),
                vitals = Vitals(70f, 99.0f, "F", 138, 88, 165f, 12.0f, 96, 74),
                medicalHistory = MedicalHistory(
                    "Back pain and difficulty walking",
                    listOf("Pain", "Fatigue"),
                    listOf("Hypertension", "Diabetes"),
                    LifestyleHistory(false, false, false)
                ),
                registeredBy = "Spoke Berasia",
                registeredAt = now.minusMinutes(20).format(formatter),
                status = ConsultationStatus.DECLINED,
                consentGiven = false
            ),
            // Patient waiting with complete data (for doctor queue demo)
            Patient(
                id = "ASHA-20260316-0009",
                fullName = "Priya Patel",
                guardianName = "Vikram Patel",
                gender = Gender.FEMALE,
                age = 24,
                dob = "2002-06-15",
                mobile = "9876543218",
                aadhaar = "XXXX-XXXX-9012",
                address = Address("Misrod", "Bhopal", "Madhya Pradesh"),
                vitals = Vitals(55f, 100.8f, "F", 108, 68, 92f, 11.0f, 98, 90),
                medicalHistory = MedicalHistory(
                    "Sore throat and difficulty swallowing for 2 days",
                    listOf("Fever", "Pain"),
                    listOf("None"),
                    LifestyleHistory()
                ),
                documents = listOf(
                    DocumentFile("Throat_Culture_Report.pdf", "report")
                ),
                registeredBy = "Spoke Berasia",
                registeredAt = now.minusMinutes(10).format(formatter),
                status = ConsultationStatus.WAITING,
                consentGiven = true
            ),
            // Patient registered today, consent pending (for health worker consent flow)
            Patient(
                id = "ASHA-20260316-0010",
                fullName = "Raju Verma",
                guardianName = "Shyam Verma",
                gender = Gender.MALE,
                age = 38,
                dob = "1988-11-25",
                mobile = "9876543219",
                aadhaar = "XXXX-XXXX-0123",
                address = Address("Sukhi Sewania", "Bhopal", "Madhya Pradesh"),
                vitals = Vitals(78f, 99.4f, "F", 125, 82, 105f, 14.2f, 97, 76),
                medicalHistory = MedicalHistory(
                    "Stomach pain and acidity after meals",
                    listOf("Pain"),
                    listOf("None"),
                    LifestyleHistory(true, true, false)
                ),
                documents = listOf(
                    DocumentFile("Endoscopy_Report_2025.pdf", "diagnostic")
                ),
                registeredBy = "Spoke Berasia",
                registeredAt = now.minusMinutes(5).format(formatter),
                status = ConsultationStatus.REGISTERED
            )  // consent not yet asked
        ))

        // Build patient queue from today's patients
        patients.filter { it.registeredAt.startsWith(now.toLocalDate().toString()) }.forEach { p ->
            patientQueue.add(PatientQueueItem(p, p.registeredAt, p.status))
        }

        // Pre-seed all demo data for complete offline experience
        connectedDoctor = doctors.first()
        seedDemoConsultation()
        seedDemoPrescription()
        seedDemoDispensing()
    }

    // Pre-seed doctor consultation form (shows pre-filled consultation for demo)
    private fun seedDemoConsultation() {
        val patient = patients.first() // Ramesh Kumar
        doctorConsultationForm = DoctorConsultationForm(
            chiefComplaints = patient.medicalHistory.primaryComplaint,
            diagnosis = "Acute upper respiratory infection with underlying uncontrolled hypertension",
            icdCode = "J06.9",
            medicines = listOf(
                Medicine("Paracetamol", "650mg", MedicineFrequency(morning = true, afternoon = true, night = true), 5, "Take after food"),
                Medicine("Amoxicillin", "500mg", MedicineFrequency(morning = true, afternoon = false, night = true), 7, "Take with water"),
                Medicine("Cetirizine", "10mg", MedicineFrequency(morning = false, afternoon = false, night = true), 5, "Take at bedtime"),
                Medicine("Amlodipine", "5mg", MedicineFrequency(morning = true, afternoon = false, night = false), 30, "Continue daily for BP"),
                Medicine("Omeprazole", "20mg", MedicineFrequency(morning = true, afternoon = false, night = false), 7, "Take on empty stomach")
            ),
            labTests = listOf("CBC (Complete Blood Count)", "X-Ray Chest", "Blood Sugar (Fasting/PP)"),
            imagingNotes = "PA view chest X-ray advised to rule out lower respiratory tract infection",
            procedures = "Nebulization with Salbutamol if wheezing persists",
            allergies = "Sulfonamides",
            recommendations = "Rest for 3-5 days. Drink plenty of warm fluids. Monitor BP daily. Follow up after 7 days.",
            referral = Referral(true, "Cardiology", "Uncontrolled hypertension despite medication. Needs specialist evaluation for BP management and cardiac risk assessment.")
        )
    }

    private fun seedDemoPrescription() {
        val patient = patients.first() // Ramesh Kumar
        val doctor = doctors.first()   // Dr. Priya Sharma
        generatedPrescription = Prescription(
            id = "RX-2026-0001",
            patientId = patient.id,
            patientName = patient.fullName,
            patientAge = patient.age,
            patientGender = patient.gender.name,
            doctorName = doctor.name,
            doctorQualification = doctor.qualification,
            clinicName = doctor.clinicName,
            regNumber = doctor.regNumber,
            date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
            chiefComplaints = patient.medicalHistory.primaryComplaint,
            diagnosis = "Acute upper respiratory infection with underlying uncontrolled hypertension",
            medicines = listOf(
                Medicine("Paracetamol", "650mg", MedicineFrequency(morning = true, afternoon = true, night = true), 5, "Take after food"),
                Medicine("Amoxicillin", "500mg", MedicineFrequency(morning = true, afternoon = false, night = true), 7, "Take with water"),
                Medicine("Cetirizine", "10mg", MedicineFrequency(morning = false, afternoon = false, night = true), 5, "Take at bedtime"),
                Medicine("Amlodipine", "5mg", MedicineFrequency(morning = true, afternoon = false, night = false), 30, "Continue daily for BP"),
                Medicine("Omeprazole", "20mg", MedicineFrequency(morning = true, afternoon = false, night = false), 7, "Take on empty stomach")
            ),
            labTests = listOf("CBC (Complete Blood Count)", "X-Ray Chest", "Blood Sugar (Fasting/PP)"),
            referral = Referral(true, "Cardiology", "Uncontrolled hypertension despite medication. Needs specialist evaluation for BP management and cardiac risk assessment."),
            recommendations = "Rest for 3-5 days. Drink plenty of warm fluids. Monitor BP daily. Follow up after 7 days.",
            allergies = "Sulfonamides",
            procedures = "Nebulization with Salbutamol if wheezing persists",
            imagingNotes = "PA view chest X-ray advised to rule out lower respiratory tract infection"
        )
    }

    // Pre-seed dispensing status (partial dispensing for demo)
    private fun seedDemoDispensing() {
        dispensedMedicines["Paracetamol"] = true
        dispensedMedicines["Amoxicillin"] = true
        dispensedMedicines["Cetirizine"] = false
        dispensedMedicines["Amlodipine"] = false
        dispensedMedicines["Omeprazole"] = false
    }

    // Mock drug list (30 common medicines)
    val drugList = listOf(
        DrugItem("Paracetamol", listOf("500mg", "650mg")),
        DrugItem("Amoxicillin", listOf("250mg", "500mg")),
        DrugItem("Azithromycin", listOf("250mg", "500mg")),
        DrugItem("Metformin", listOf("500mg", "850mg", "1000mg")),
        DrugItem("Amlodipine", listOf("2.5mg", "5mg", "10mg")),
        DrugItem("Atorvastatin", listOf("10mg", "20mg", "40mg")),
        DrugItem("Omeprazole", listOf("20mg", "40mg")),
        DrugItem("Ciprofloxacin", listOf("250mg", "500mg")),
        DrugItem("Metronidazole", listOf("200mg", "400mg")),
        DrugItem("Cetirizine", listOf("5mg", "10mg")),
        DrugItem("Ibuprofen", listOf("200mg", "400mg")),
        DrugItem("Diclofenac", listOf("50mg", "100mg")),
        DrugItem("Ranitidine", listOf("150mg", "300mg")),
        DrugItem("Losartan", listOf("25mg", "50mg", "100mg")),
        DrugItem("Enalapril", listOf("2.5mg", "5mg", "10mg")),
        DrugItem("Glimepiride", listOf("1mg", "2mg", "4mg")),
        DrugItem("Pantoprazole", listOf("20mg", "40mg")),
        DrugItem("Doxycycline", listOf("100mg")),
        DrugItem("Ceftriaxone", listOf("250mg", "500mg", "1g")),
        DrugItem("Salbutamol", listOf("2mg", "4mg")),
        DrugItem("Montelukast", listOf("4mg", "5mg", "10mg")),
        DrugItem("Prednisolone", listOf("5mg", "10mg", "20mg")),
        DrugItem("Insulin (Regular)", listOf("40IU/ml", "100IU/ml")),
        DrugItem("ORS (Oral Rehydration Salt)", listOf("1 sachet")),
        DrugItem("Iron + Folic Acid", listOf("1 tablet")),
        DrugItem("Calcium + Vitamin D3", listOf("500mg+250IU")),
        DrugItem("Multivitamin", listOf("1 tablet")),
        DrugItem("Albendazole", listOf("400mg")),
        DrugItem("Chloroquine", listOf("250mg", "500mg")),
        DrugItem("Aspirin", listOf("75mg", "150mg", "325mg"))
    )

    // Mock ICD-10 diagnoses (20 common conditions)
    val icd10List = listOf(
        ICD10Item("J06.9", "Acute upper respiratory infection"),
        ICD10Item("J18.9", "Pneumonia, unspecified"),
        ICD10Item("A09", "Infectious gastroenteritis and colitis"),
        ICD10Item("E11", "Type 2 diabetes mellitus"),
        ICD10Item("I10", "Essential hypertension"),
        ICD10Item("J45", "Asthma"),
        ICD10Item("M54.5", "Low back pain"),
        ICD10Item("K29.7", "Gastritis, unspecified"),
        ICD10Item("B01", "Varicella (chickenpox)"),
        ICD10Item("A01.0", "Typhoid fever"),
        ICD10Item("D50.9", "Iron deficiency anaemia"),
        ICD10Item("N39.0", "Urinary tract infection"),
        ICD10Item("L30.9", "Dermatitis, unspecified"),
        ICD10Item("R50.9", "Fever, unspecified"),
        ICD10Item("G43", "Migraine"),
        ICD10Item("I25.9", "Chronic ischaemic heart disease"),
        ICD10Item("A15", "Respiratory tuberculosis"),
        ICD10Item("B50", "Plasmodium falciparum malaria"),
        ICD10Item("J02.9", "Acute pharyngitis, unspecified"),
        ICD10Item("M79.3", "Panniculitis, unspecified")
    )

    // Common lab tests
    val labTests = listOf(
        "CBC (Complete Blood Count)",
        "LFT (Liver Function Test)",
        "RFT (Renal Function Test)",
        "Urine R/E (Routine Examination)",
        "Blood Sugar (Fasting/PP)",
        "X-Ray Chest",
        "ECG",
        "HbA1c",
        "Lipid Profile",
        "Thyroid Profile"
    )

    // Common symptoms
    val commonSymptoms = listOf(
        "Fever", "Cough", "Headache", "Fatigue", "Pain", "Breathlessness", "Other"
    )

    // Known conditions
    val knownConditions = listOf(
        "Diabetes", "Hypertension", "TB", "Heart Disease", "None"
    )

    private var patientCounter = patients.size

    fun generatePatientId(): String {
        patientCounter++
        val now = LocalDateTime.now()
        val dateStr = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        return "ASHA-$dateStr-${patientCounter.toString().padStart(4, '0')}"
    }
}
