package com.telemed.demo.data.local.dummy

import com.telemed.demo.domain.model.Doctor
import com.telemed.demo.domain.model.DoctorConsultationForm
import com.telemed.demo.domain.model.MedicationItem
import com.telemed.demo.domain.model.Patient
import com.telemed.demo.domain.model.PatientRegistrationData
import com.telemed.demo.domain.model.Prescription
import com.telemed.demo.domain.model.SessionUser
import com.telemed.demo.domain.model.SpokeLocation
import com.telemed.demo.domain.model.Vitals
import com.telemed.demo.domain.model.BasicVitalsData

class TeleMedMemoryStore {
    var currentSession: SessionUser? = null

    var spokeLocation: SpokeLocation? = null
    var latestRegistration: PatientRegistrationData? = null
    var latestBasicVitals: BasicVitalsData? = null
    var dataShared: Boolean = false
    var pharmacistConsent: Boolean? = null
    var pharmacistCallInitiated: Boolean = false
    var dispensationNotes: String = ""
    var doctorDecision: String = "Pending"
    var doctorConsultationForm: DoctorConsultationForm? = null
    var generatedPrescriptionPdfName: String? = null

    val uploadedReports = mutableListOf<String>()
    val uploadedDiagnostics = mutableListOf<String>()

    var currentPatient: Patient? = null
    var latestVitals: Vitals? = null
    var connectedDoctor: Doctor? = null
    var activeCall: Boolean = false
    var latestPrescription: Prescription? = null

    val mappedDistricts = listOf("Bhopal", "Indore", "Sehore")
    val mappedVillagesByDistrict = mapOf(
        "Bhopal" to listOf("Berasia", "Phanda", "Sukhi Sewania"),
        "Indore" to listOf("Sanwer", "Depalpur", "Mhow"),
        "Sehore" to listOf("Ashta", "Ichhawar", "Nasrullaganj")
    )

    val doctorMedicationTemplate = listOf(
        MedicationItem("Paracetamol", "500 mg", "Twice daily", "After meal"),
        MedicationItem("ORS", "1 sachet", "Twice daily", "Any time")
    )

    val doctors = listOf(
        Doctor("d1", "Dr. Sarah Coleman", "General Medicine", 3, true),
        Doctor("d2", "Dr. Amit Mehra", "Cardiology", 7, true),
        Doctor("d3", "Dr. Lucia Park", "Pulmonology", 5, false),
        Doctor("d4", "Dr. Marcus Reed", "Pediatrics", 2, true)
    )
}

