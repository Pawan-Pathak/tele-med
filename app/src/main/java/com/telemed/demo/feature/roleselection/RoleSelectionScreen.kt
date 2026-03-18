package com.telemed.demo.feature.roleselection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            .background(BackgroundPage)
    ) {
        // Hero header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(HeaderNavy)
        ) {
            // Decorative circle
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 40.dp, y = (-30).dp)
                    .clip(CircleShape)
                    .background(HeaderNavyLighter)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                // HelpAge app icon
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = BrandPrimary,
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.VolunteerActivism,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.displayMedium,
                    color = Color.White
                )

                Text(
                    text = stringResource(R.string.app_tagline),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
        }

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.select_role),
                style = MaterialTheme.typography.headlineSmall,
                color = TextPrimary,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(4.dp))

            RoleCard(
                title = stringResource(R.string.role_health_worker),
                description = stringResource(R.string.role_hw_desc),
                icon = Icons.Default.PersonSearch,
                color = HealthWorkerColor,
                onClick = { onRoleSelected(UserRole.HEALTH_WORKER) }
            )

            RoleCard(
                title = stringResource(R.string.role_pharmacist),
                description = stringResource(R.string.role_pharmacist_desc),
                icon = Icons.Default.LocalPharmacy,
                color = PharmacistColor,
                onClick = { onRoleSelected(UserRole.PHARMACIST) }
            )

            RoleCard(
                title = stringResource(R.string.role_doctor),
                description = stringResource(R.string.role_doctor_desc),
                icon = Icons.Default.MedicalServices,
                color = DoctorColor,
                onClick = { onRoleSelected(UserRole.DOCTOR) }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
