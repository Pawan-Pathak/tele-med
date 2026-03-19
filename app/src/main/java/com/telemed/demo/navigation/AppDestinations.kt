package com.telemed.demo.navigation

sealed class AppDestination(val route: String) {
    // Entry point
    data object RoleSelection : AppDestination("role_selection")

    // Health Worker flow
    data object HWLogin : AppDestination("hw_login")
    data object HWSessionSetup : AppDestination("hw_session_setup")
    data object HWDashboard : AppDestination("hw_dashboard")
    data object HWRegistrationStep1 : AppDestination("hw_reg_step1")
    data object HWRegistrationStep2 : AppDestination("hw_reg_step2")
    data object HWRegistrationStep3 : AppDestination("hw_reg_step3")
    data object HWRegistrationStep4 : AppDestination("hw_reg_step4")
    data object HWRegistrationStep5 : AppDestination("hw_reg_step5")
    data object HWRegistrationStep6 : AppDestination("hw_reg_step6")
    data object HWPatientDetail : AppDestination("hw_patient_detail/{patientId}") {
        fun createRoute(patientId: String) = "hw_patient_detail/$patientId"
    }
    data object HWConsent : AppDestination("hw_consent/{patientId}") {
        fun createRoute(patientId: String) = "hw_consent/$patientId"
    }
    data object HWConnectDoctor : AppDestination("hw_connect_doctor")
    data object HWVideoCall : AppDestination("hw_video_call")

    // Pharmacist flow
    data object PharmacistLogin : AppDestination("pharmacist_login")
    data object PharmacistQueue : AppDestination("pharmacist_queue")
    data object PharmacistConsent : AppDestination("pharmacist_consent/{patientId}") {
        fun createRoute(patientId: String) = "pharmacist_consent/$patientId"
    }
    data object PharmacistVideoCall : AppDestination("pharmacist_video_call")
    data object PharmacistPrescriptionView : AppDestination("pharmacist_prescription_view")
    data object PharmacistDispense : AppDestination("pharmacist_dispense")

    // Doctor flow
    data object DoctorLogin : AppDestination("doctor_login")
    data object DoctorQueue : AppDestination("doctor_queue")
    data object DoctorIncomingCall : AppDestination("doctor_incoming_call/{patientId}") {
        fun createRoute(patientId: String) = "doctor_incoming_call/$patientId"
    }
    data object DoctorConsultation : AppDestination("doctor_consultation/{patientId}") {
        fun createRoute(patientId: String) = "doctor_consultation/$patientId"
    }
    data object DoctorPrescriptionPreview : AppDestination("doctor_prescription_preview")
}
