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

    // Pre-seeded doctors
    val doctors = listOf(
        Doctor("d1", "Dr. Priya Sharma", "MBBS, MD", "General Medicine", "PHC Berasia", "MP-MED-1234", 3, true, "Bhopal"),
        Doctor("d2", "Dr. Amit Verma", "MBBS, MD, DM", "Cardiology", "CHC Sehore", "MP-MED-2345", 7, true, "Sehore"),
        Doctor("d3", "Dr. Sunita Patel", "MBBS, DCH", "Pediatrics", "PHC Sanwer", "MP-MED-3456", 5, true, "Indore")
    )

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
                registeredBy = "Spoke Berasia",
                registeredAt = now.minusHours(2).format(formatter),
                status = ConsultationStatus.WAITING
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
                registeredBy = "Spoke Phanda",
                registeredAt = now.minusHours(1).format(formatter),
                status = ConsultationStatus.WAITING
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
                registeredBy = "Spoke Sanwer",
                registeredAt = now.minusMinutes(45).format(formatter),
                status = ConsultationStatus.IN_PROGRESS
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
                registeredBy = "Spoke Ashta",
                registeredAt = now.minusMinutes(30).format(formatter),
                status = ConsultationStatus.REGISTERED
            ),
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
                registeredBy = "Spoke Depalpur",
                registeredAt = now.minusMinutes(15).format(formatter),
                status = ConsultationStatus.COMPLETED
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
                registeredBy = "Spoke Ichhawar",
                registeredAt = now.minusDays(1).format(formatter),
                status = ConsultationStatus.COMPLETED
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
                registeredBy = "Spoke Mhow",
                registeredAt = now.minusDays(1).format(formatter),
                status = ConsultationStatus.COMPLETED
            )
        ))

        // Build patient queue from today's patients
        patients.filter { it.registeredAt.startsWith(now.toLocalDate().toString()) }.forEach { p ->
            patientQueue.add(PatientQueueItem(p, p.registeredAt, p.status))
        }
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
