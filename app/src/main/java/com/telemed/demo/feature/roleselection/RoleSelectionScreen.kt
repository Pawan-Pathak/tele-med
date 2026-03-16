package com.telemed.demo.feature.roleselection

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.telemed.demo.R
import com.telemed.demo.domain.model.UserRole
import com.telemed.demo.ui.components.RoleCard
import com.telemed.demo.ui.theme.*

@Composable
fun RoleSelectionScreen(
    onRoleSelected: (UserRole) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        // App icon/logo placeholder
        Surface(
            shape = MaterialTheme.shapes.large,
            color = PrimaryTeal.copy(alpha = 0.1f),
            modifier = Modifier.size(80.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Default.LocalHospital,
                    contentDescription = null,
                    tint = PrimaryTeal,
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.displayMedium,
            color = PrimaryTealDark,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = stringResource(R.string.app_tagline),
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = stringResource(R.string.select_role),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        RoleCard(
            title = stringResource(R.string.role_health_worker),
            description = stringResource(R.string.role_hw_desc),
            icon = Icons.Default.PersonSearch,
            color = HealthWorkerColor,
            onClick = { onRoleSelected(UserRole.HEALTH_WORKER) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        RoleCard(
            title = stringResource(R.string.role_pharmacist),
            description = stringResource(R.string.role_pharmacist_desc),
            icon = Icons.Default.LocalPharmacy,
            color = PharmacistColor,
            onClick = { onRoleSelected(UserRole.PHARMACIST) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        RoleCard(
            title = stringResource(R.string.role_doctor),
            description = stringResource(R.string.role_doctor_desc),
            icon = Icons.Default.MedicalServices,
            color = DoctorColor,
            onClick = { onRoleSelected(UserRole.DOCTOR) }
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}
