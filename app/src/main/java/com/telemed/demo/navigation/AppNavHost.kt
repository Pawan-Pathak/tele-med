package com.telemed.demo.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.telemed.demo.di.AppViewModelFactory
import com.telemed.demo.feature.dashboard.DashboardScreen
import com.telemed.demo.feature.dashboard.DashboardViewModel
import com.telemed.demo.feature.doctor.DoctorModuleScreen
import com.telemed.demo.feature.doctor.DoctorModuleViewModel
import com.telemed.demo.feature.doctorpool.DoctorPoolScreen
import com.telemed.demo.feature.doctorpool.DoctorPoolViewModel
import com.telemed.demo.feature.healthworker.HealthWorkerModuleScreen
import com.telemed.demo.feature.healthworker.HealthWorkerModuleViewModel
import com.telemed.demo.feature.login.LoginScreen
import com.telemed.demo.feature.login.LoginViewModel
import com.telemed.demo.feature.pharmacist.PharmacistModuleScreen
import com.telemed.demo.feature.pharmacist.PharmacistModuleViewModel
import com.telemed.demo.feature.prescription.PrescriptionSummaryScreen
import com.telemed.demo.feature.prescription.PrescriptionSummaryViewModel
import com.telemed.demo.feature.registration.RegistrationScreen
import com.telemed.demo.feature.registration.RegistrationViewModel
import com.telemed.demo.feature.videocall.VideoCallScreen
import com.telemed.demo.feature.videocall.VideoCallViewModel
import com.telemed.demo.feature.vitals.VitalsCollectionScreen
import com.telemed.demo.feature.vitals.VitalsViewModel

@Composable
fun AppNavHost(
    factory: AppViewModelFactory,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = AppDestination.Login.route
    ) {
        composable(AppDestination.Login.route) {
            val vm: LoginViewModel = viewModel(factory = factory)
            LoginScreen(
                viewModel = vm,
                onLoginSuccess = { _ ->
                    navController.navigate(AppDestination.Dashboard.route) {
                        popUpTo(AppDestination.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(AppDestination.Dashboard.route) {
            val vm: DashboardViewModel = viewModel(factory = factory)
            DashboardScreen(
                viewModel = vm,
                onNavigate = { destination -> navController.navigate(destination.route) }
            )
        }

        composable(AppDestination.Registration.route) {
            val vm: RegistrationViewModel = viewModel(factory = factory)
            RegistrationScreen(viewModel = vm, onBack = { navController.popBackStack() })
        }

        composable(AppDestination.Vitals.route) {
            val vm: VitalsViewModel = viewModel(factory = factory)
            VitalsCollectionScreen(viewModel = vm, onBack = { navController.popBackStack() })
        }

        composable(AppDestination.DoctorPool.route) {
            val vm: DoctorPoolViewModel = viewModel(factory = factory)
            DoctorPoolScreen(viewModel = vm, onBack = { navController.popBackStack() })
        }

        composable(AppDestination.VideoCall.route) {
            val vm: VideoCallViewModel = viewModel(factory = factory)
            VideoCallScreen(viewModel = vm, onBack = { navController.popBackStack() })
        }

        composable(AppDestination.Prescription.route) {
            val vm: PrescriptionSummaryViewModel = viewModel(factory = factory)
            PrescriptionSummaryScreen(viewModel = vm, onBack = { navController.popBackStack() })
        }

        composable(AppDestination.HealthWorkerModule.route) {
            val vm: HealthWorkerModuleViewModel = viewModel(factory = factory)
            HealthWorkerModuleScreen(viewModel = vm, onBack = { navController.popBackStack() })
        }

        composable(AppDestination.PharmacistModule.route) {
            val vm: PharmacistModuleViewModel = viewModel(factory = factory)
            PharmacistModuleScreen(viewModel = vm, onBack = { navController.popBackStack() })
        }

        composable(AppDestination.DoctorModule.route) {
            val vm: DoctorModuleViewModel = viewModel(factory = factory)
            DoctorModuleScreen(viewModel = vm, onBack = { navController.popBackStack() })
        }
    }
}
