package com.telemed.demo.navigation

sealed class AppDestination(val route: String) {
    data object Login : AppDestination("login")
    data object Dashboard : AppDestination("dashboard")
    data object HealthWorkerModule : AppDestination("health_worker_module")
    data object PharmacistModule : AppDestination("pharmacist_module")
    data object DoctorModule : AppDestination("doctor_module")

    // Legacy demo routes kept for compatibility with previous scaffold.
    data object Registration : AppDestination("registration")
    data object Vitals : AppDestination("vitals")
    data object DoctorPool : AppDestination("doctor_pool")
    data object VideoCall : AppDestination("video_call")
    data object Prescription : AppDestination("prescription")
}

