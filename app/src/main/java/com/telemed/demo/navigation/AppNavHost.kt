package com.telemed.demo.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.telemed.demo.di.AppViewModelFactory
import com.telemed.demo.domain.model.UserRole
import com.telemed.demo.feature.doctor.*
import com.telemed.demo.feature.healthworker.*
import com.telemed.demo.feature.pharmacist.*
import com.telemed.demo.feature.roleselection.RoleSelectionScreen

@Composable
fun AppNavHost(
    factory: AppViewModelFactory,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = AppDestination.RoleSelection.route
    ) {
        // ==================== Entry Point ====================
        composable(AppDestination.RoleSelection.route) {
            RoleSelectionScreen(
                onRoleSelected = { role ->
                    when (role) {
                        UserRole.HEALTH_WORKER -> navController.navigate(AppDestination.HWLogin.route)
                        UserRole.PHARMACIST -> navController.navigate(AppDestination.PharmacistLogin.route)
                        UserRole.DOCTOR -> navController.navigate(AppDestination.DoctorLogin.route)
                    }
                }
            )
        }

        // ==================== Health Worker Flow ====================
        composable(AppDestination.HWLogin.route) {
            HWLoginScreen(
                onLoginSuccess = {
                    navController.navigate(AppDestination.HWSessionSetup.route) {
                        popUpTo(AppDestination.HWLogin.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppDestination.HWSessionSetup.route) {
            val vm: HealthWorkerModuleViewModel = viewModel(
                viewModelStoreOwner = remember(navController) {
                    navController.getBackStackEntry(AppDestination.HWSessionSetup.route)
                },
                factory = factory
            )
            HWSessionSetupScreen(
                viewModel = vm,
                onSessionStarted = {
                    navController.navigate(AppDestination.HWDashboard.route)
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppDestination.HWDashboard.route) {
            val vm: HealthWorkerModuleViewModel = viewModel(
                viewModelStoreOwner = remember(navController) {
                    navController.getBackStackEntry(AppDestination.HWSessionSetup.route)
                },
                factory = factory
            )
            HWDashboardScreen(
                viewModel = vm,
                onNewPatient = {
                    navController.navigate(AppDestination.HWRegistrationStep1.route)
                },
                onPatientClick = { patientId ->
                    navController.navigate(AppDestination.HWPatientDetail.createRoute(patientId))
                },
                onConnectDoctor = {
                    navController.navigate(AppDestination.HWConnectDoctor.route)
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppDestination.HWRegistrationStep1.route) {
            val vm: HealthWorkerModuleViewModel = viewModel(
                viewModelStoreOwner = remember(navController) {
                    navController.getBackStackEntry(AppDestination.HWSessionSetup.route)
                },
                factory = factory
            )
            HWRegStep1Screen(
                viewModel = vm,
                onNext = { navController.navigate(AppDestination.HWRegistrationStep2.route) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppDestination.HWRegistrationStep2.route) {
            val vm: HealthWorkerModuleViewModel = viewModel(
                viewModelStoreOwner = remember(navController) {
                    navController.getBackStackEntry(AppDestination.HWSessionSetup.route)
                },
                factory = factory
            )
            HWRegStep2Screen(
                viewModel = vm,
                onNext = { navController.navigate(AppDestination.HWRegistrationStep3.route) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppDestination.HWRegistrationStep3.route) {
            val vm: HealthWorkerModuleViewModel = viewModel(
                viewModelStoreOwner = remember(navController) {
                    navController.getBackStackEntry(AppDestination.HWSessionSetup.route)
                },
                factory = factory
            )
            HWRegStep3Screen(
                viewModel = vm,
                onNext = { navController.navigate(AppDestination.HWRegistrationStep4.route) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppDestination.HWRegistrationStep4.route) {
            val vm: HealthWorkerModuleViewModel = viewModel(
                viewModelStoreOwner = remember(navController) {
                    navController.getBackStackEntry(AppDestination.HWSessionSetup.route)
                },
                factory = factory
            )
            HWRegStep4Screen(
                viewModel = vm,
                onNext = { navController.navigate(AppDestination.HWRegistrationStep5.route) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppDestination.HWRegistrationStep5.route) {
            val vm: HealthWorkerModuleViewModel = viewModel(
                viewModelStoreOwner = remember(navController) {
                    navController.getBackStackEntry(AppDestination.HWSessionSetup.route)
                },
                factory = factory
            )
            HWRegStep5Screen(
                viewModel = vm,
                onNext = { navController.navigate(AppDestination.HWRegistrationStep6.route) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppDestination.HWRegistrationStep6.route) {
            val vm: HealthWorkerModuleViewModel = viewModel(
                viewModelStoreOwner = remember(navController) {
                    navController.getBackStackEntry(AppDestination.HWSessionSetup.route)
                },
                factory = factory
            )
            HWRegStep6Screen(
                viewModel = vm,
                onSubmitSuccess = {
                    // Pop all registration steps back to dashboard
                    navController.popBackStack(AppDestination.HWDashboard.route, inclusive = false)
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = AppDestination.HWPatientDetail.route,
            arguments = listOf(navArgument("patientId") { type = NavType.StringType })
        ) { backStackEntry ->
            val patientId = backStackEntry.arguments?.getString("patientId") ?: ""
            val vm: HealthWorkerModuleViewModel = viewModel(
                viewModelStoreOwner = remember(navController) {
                    navController.getBackStackEntry(AppDestination.HWSessionSetup.route)
                },
                factory = factory
            )
            HWPatientDetailScreen(
                viewModel = vm,
                patientId = patientId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppDestination.HWConnectDoctor.route) {
            val vm: HealthWorkerModuleViewModel = viewModel(
                viewModelStoreOwner = remember(navController) {
                    navController.getBackStackEntry(AppDestination.HWSessionSetup.route)
                },
                factory = factory
            )
            HWConnectDoctorScreen(
                viewModel = vm,
                onVideoCall = {
                    navController.navigate(AppDestination.HWVideoCall.route)
                },
                onPhoneCall = {
                    navController.navigate(AppDestination.HWVideoCall.route)
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppDestination.HWVideoCall.route) {
            val vm: HealthWorkerModuleViewModel = viewModel(
                viewModelStoreOwner = remember(navController) {
                    navController.getBackStackEntry(AppDestination.HWSessionSetup.route)
                },
                factory = factory
            )
            HWVideoCallScreen(
                viewModel = vm,
                onCallEnded = {
                    vm.resetCallState()
                    navController.popBackStack(AppDestination.HWDashboard.route, inclusive = false)
                },
                onBack = {
                    vm.resetCallState()
                    navController.popBackStack()
                }
            )
        }

        // ==================== Pharmacist Flow ====================
        composable(AppDestination.PharmacistLogin.route) {
            PharmacistLoginScreen(
                onLoginSuccess = {
                    navController.navigate(AppDestination.PharmacistQueue.route) {
                        popUpTo(AppDestination.PharmacistLogin.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppDestination.PharmacistQueue.route) {
            val vm: PharmacistModuleViewModel = viewModel(
                viewModelStoreOwner = remember(navController) {
                    navController.getBackStackEntry(AppDestination.PharmacistQueue.route)
                },
                factory = factory
            )
            PharmacistQueueScreen(
                viewModel = vm,
                onPatientSelect = { patientId ->
                    navController.navigate(AppDestination.PharmacistConsent.createRoute(patientId))
                },
                onBack = {
                    navController.popBackStack(AppDestination.RoleSelection.route, inclusive = false)
                }
            )
        }

        composable(
            route = AppDestination.PharmacistConsent.route,
            arguments = listOf(navArgument("patientId") { type = NavType.StringType })
        ) { backStackEntry ->
            val patientId = backStackEntry.arguments?.getString("patientId") ?: ""
            val vm: PharmacistModuleViewModel = viewModel(
                viewModelStoreOwner = remember(navController) {
                    navController.getBackStackEntry(AppDestination.PharmacistQueue.route)
                },
                factory = factory
            )
            PharmacistConsentScreen(
                viewModel = vm,
                patientId = patientId,
                onConsentYes = {
                    navController.navigate(AppDestination.PharmacistVideoCall.route)
                },
                onConsentNo = {
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppDestination.PharmacistVideoCall.route) {
            val vm: PharmacistModuleViewModel = viewModel(
                viewModelStoreOwner = remember(navController) {
                    navController.getBackStackEntry(AppDestination.PharmacistQueue.route)
                },
                factory = factory
            )
            PharmacistVideoCallScreen(
                viewModel = vm,
                onCallEnded = {
                    navController.navigate(AppDestination.PharmacistPrescriptionView.route) {
                        popUpTo(AppDestination.PharmacistQueue.route) { inclusive = false }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppDestination.PharmacistPrescriptionView.route) {
            val vm: PharmacistModuleViewModel = viewModel(
                viewModelStoreOwner = remember(navController) {
                    navController.getBackStackEntry(AppDestination.PharmacistQueue.route)
                },
                factory = factory
            )
            PharmacistPrescriptionViewScreen(
                viewModel = vm,
                onDispenseMedicines = {
                    navController.navigate(AppDestination.PharmacistDispense.route)
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppDestination.PharmacistDispense.route) {
            val vm: PharmacistModuleViewModel = viewModel(
                viewModelStoreOwner = remember(navController) {
                    navController.getBackStackEntry(AppDestination.PharmacistQueue.route)
                },
                factory = factory
            )
            PharmacistDispenseScreen(
                viewModel = vm,
                onDone = {
                    navController.popBackStack(AppDestination.PharmacistQueue.route, inclusive = false)
                },
                onBack = { navController.popBackStack() }
            )
        }

        // ==================== Doctor Flow ====================
        composable(AppDestination.DoctorLogin.route) {
            DoctorLoginScreen(
                onLoginSuccess = {
                    navController.navigate(AppDestination.DoctorQueue.route) {
                        popUpTo(AppDestination.DoctorLogin.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppDestination.DoctorQueue.route) {
            val vm: DoctorModuleViewModel = viewModel(
                viewModelStoreOwner = remember(navController) {
                    navController.getBackStackEntry(AppDestination.DoctorQueue.route)
                },
                factory = factory
            )
            DoctorQueueScreen(
                viewModel = vm,
                onPatientSelect = { patientId ->
                    navController.navigate(AppDestination.DoctorIncomingCall.createRoute(patientId))
                },
                onBack = {
                    navController.popBackStack(AppDestination.RoleSelection.route, inclusive = false)
                }
            )
        }

        composable(
            route = AppDestination.DoctorIncomingCall.route,
            arguments = listOf(navArgument("patientId") { type = NavType.StringType })
        ) { backStackEntry ->
            val patientId = backStackEntry.arguments?.getString("patientId") ?: ""
            val vm: DoctorModuleViewModel = viewModel(
                viewModelStoreOwner = remember(navController) {
                    navController.getBackStackEntry(AppDestination.DoctorQueue.route)
                },
                factory = factory
            )
            DoctorIncomingCallScreen(
                viewModel = vm,
                patientId = patientId,
                onAccept = {
                    navController.navigate(AppDestination.DoctorConsultation.createRoute(patientId)) {
                        popUpTo(AppDestination.DoctorQueue.route) { inclusive = false }
                    }
                },
                onDecline = { navController.popBackStack() }
            )
        }

        composable(
            route = AppDestination.DoctorConsultation.route,
            arguments = listOf(navArgument("patientId") { type = NavType.StringType })
        ) { backStackEntry ->
            val patientId = backStackEntry.arguments?.getString("patientId") ?: ""
            val vm: DoctorModuleViewModel = viewModel(
                viewModelStoreOwner = remember(navController) {
                    navController.getBackStackEntry(AppDestination.DoctorQueue.route)
                },
                factory = factory
            )
            DoctorConsultationScreen(
                viewModel = vm,
                patientId = patientId,
                onPrescriptionGenerated = {
                    navController.navigate(AppDestination.DoctorPrescriptionPreview.route)
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppDestination.DoctorPrescriptionPreview.route) {
            val vm: DoctorModuleViewModel = viewModel(
                viewModelStoreOwner = remember(navController) {
                    navController.getBackStackEntry(AppDestination.DoctorQueue.route)
                },
                factory = factory
            )
            DoctorPrescriptionPreviewScreen(
                viewModel = vm,
                onDone = {
                    navController.popBackStack(AppDestination.DoctorQueue.route, inclusive = false)
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
