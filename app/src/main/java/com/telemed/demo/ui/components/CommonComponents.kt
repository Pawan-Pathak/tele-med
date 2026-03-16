package com.telemed.demo.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.telemed.demo.domain.model.ConsultationStatus
import com.telemed.demo.ui.theme.*

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// HERO / HEADER COMPONENT
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

@Composable
fun HeroHeader(
    title: String,
    subtitle: String = "",
    moduleColor: Color = BrandPrimary,
    moduleLabel: String = "",
    moduleIcon: ImageVector? = null,
    stats: List<Pair<String, String>> = emptyList(),
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(HeaderNavy)
    ) {
        // Decorative circle (top-right)
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
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Module chip (top-left)
            if (moduleLabel.isNotEmpty()) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (moduleIcon != null) {
                            Icon(moduleIcon, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                        }
                        Text(
                            moduleLabel.uppercase(),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.8.sp
                            ),
                            color = Color.White
                        )
                    }
                }
            }

            // Title
            Column {
                Text(
                    title,
                    style = MaterialTheme.typography.displayMedium,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (subtitle.isNotEmpty()) {
                    Text(
                        subtitle,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            }

            // Stats bar
            if (stats.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    stats.forEachIndexed { index, (value, label) ->
                        if (index > 0) {
                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(28.dp)
                                    .background(Color.White.copy(alpha = 0.2f))
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                value,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold, fontSize = 18.sp),
                                color = Color.White
                            )
                            Text(
                                label.uppercase(),
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                color = TextMuted
                            )
                        }
                    }
                }
            }
        }
    }
}

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// ROLE CARD (Entry screen)
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

@Composable
fun RoleCard(
    title: String,
    description: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp), ambientColor = CardShadow, spotColor = CardShadow),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Icon tile
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = color.copy(alpha = 0.10f),
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = title, tint = color, modifier = Modifier.size(30.dp))
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold), color = TextPrimary)
                Spacer(modifier = Modifier.height(2.dp))
                Text(description, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted, modifier = Modifier.size(24.dp))
        }
    }
}

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// LARGE TEXT FIELD
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

@Composable
fun LargeTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    isError: Boolean = false,
    errorMessage: String = "",
    isPassword: Boolean = false,
    singleLine: Boolean = true,
    minLines: Int = 1,
    readOnly: Boolean = false,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label, style = MaterialTheme.typography.labelMedium, color = TextSecondary) },
            modifier = Modifier.fillMaxWidth(),
            textStyle = MaterialTheme.typography.bodyLarge,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            isError = isError,
            singleLine = singleLine,
            minLines = minLines,
            readOnly = readOnly,
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            trailingIcon = trailingIcon,
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BrandPrimary,
                unfocusedBorderColor = OutlineGray,
                focusedContainerColor = BackgroundCard,
                unfocusedContainerColor = BackgroundCard,
                errorBorderColor = StatusAlertText
            )
        )
        if (isError && errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = StatusAlertText,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// LARGE BUTTON (Pill shape)
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

@Composable
fun LargeButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    color: Color = BrandPrimary,
    icon: ImageVector? = null
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp),
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = color,
            disabledContainerColor = color.copy(alpha = 0.4f)
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color.White, strokeWidth = 2.dp)
        } else {
            if (icon != null) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(text, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold))
        }
    }
}

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// STATUS BADGE / PILL
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

@Composable
fun StatusBadge(status: ConsultationStatus) {
    val (text, bgColor, textColor) = when (status) {
        ConsultationStatus.REGISTERED -> Triple("REGISTERED", StatusPendingBg, StatusPendingText)
        ConsultationStatus.WAITING -> Triple("PENDING", StatusAwaitingBg, StatusAwaitingText)
        ConsultationStatus.IN_PROGRESS -> Triple("IN CONSULT", StatusPendingBg, StatusPendingText)
        ConsultationStatus.COMPLETED -> Triple("COMPLETED", StatusDoneBg, StatusDoneText)
        ConsultationStatus.DECLINED -> Triple("DECLINED", StatusAlertBg, StatusAlertText)
    }
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = bgColor
    ) {
        Text(
            text = text,
            color = textColor,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// DROPDOWN FIELD
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    options: List<String>,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String = "",
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { if (enabled) expanded = it }) {
            OutlinedTextField(
                value = value,
                onValueChange = {},
                readOnly = true,
                enabled = enabled,
                label = { Text(label, style = MaterialTheme.typography.labelMedium, color = if (enabled) TextSecondary else TextMuted) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor(),
                textStyle = MaterialTheme.typography.bodyLarge,
                isError = isError,
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BrandPrimary,
                    unfocusedBorderColor = OutlineGray,
                    focusedContainerColor = BackgroundCard,
                    unfocusedContainerColor = BackgroundCard,
                    disabledBorderColor = OutlineGray.copy(alpha = 0.4f),
                    disabledContainerColor = BackgroundCard.copy(alpha = 0.4f)
                )
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option, style = MaterialTheme.typography.bodyLarge) },
                        onClick = { onValueChange(option); expanded = false },
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                    )
                }
            }
        }
        if (isError && errorMessage.isNotEmpty()) {
            Text(errorMessage, color = StatusAlertText, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 16.dp, top = 4.dp))
        }
    }
}

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// SELECTABLE CHIP (Segmented tab style)
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

@Composable
fun SelectableChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        color = if (selected) TextPrimary else BackgroundCard,
        border = if (!selected) BorderStroke(1.dp, OutlineGray) else null,
        modifier = modifier
    ) {
        Text(
            text,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium),
            color = if (selected) Color.White else TextPrimary,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// STEP PROGRESS INDICATOR
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

@Composable
fun StepProgressIndicator(
    currentStep: Int,
    totalSteps: Int,
    stepLabels: List<String>,
    modifier: Modifier = Modifier,
    moduleColor: Color = BrandPrimary
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(vertical = 8.dp)
    ) {
        // Step dots and connectors
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            for (i in 1..totalSteps) {
                val isDone = i < currentStep
                val isCurrent = i == currentStep
                val isUpcoming = i > currentStep

                // Step circle
                Surface(
                    shape = CircleShape,
                    color = when {
                        isDone || isCurrent -> moduleColor
                        else -> InactiveGray
                    },
                    modifier = Modifier.size(28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (isDone) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        } else {
                            Text(
                                "$i",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = if (isCurrent) Color.White else TextMuted
                            )
                        }
                    }
                }

                // Connector line (not after last step)
                if (i < totalSteps) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp)
                            .padding(horizontal = 4.dp)
                            .background(if (i < currentStep) moduleColor else InactiveGray)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Current step label
        Text(
            "Step $currentStep: ${stepLabels.getOrElse(currentStep - 1) { "" }}",
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// SECTION HEADER (Form divider)
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    moduleColor: Color = BrandPrimary
) {
    Column(modifier = modifier.padding(top = 12.dp, bottom = 4.dp)) {
        Text(
            title.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.5.sp
            ),
            color = moduleColor
        )
        Spacer(modifier = Modifier.height(4.dp))
        HorizontalDivider(thickness = 0.5.dp, color = moduleColor.copy(alpha = 0.3f))
    }
}

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// PATIENT SUMMARY CARD (label + value row)
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

@Composable
fun PatientSummaryCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = TextSecondary, modifier = Modifier.weight(0.4f))
        Text(value, style = MaterialTheme.typography.bodyLarge, color = TextPrimary, modifier = Modifier.weight(0.6f))
    }
}

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// PATIENT LIST CARD
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

@Composable
fun PatientListCard(
    name: String,
    id: String,
    subtitle: String,
    status: ConsultationStatus,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Avatar circle with initials
            val initials = name.split(" ").take(2).joinToString("") { it.first().uppercase() }
            Surface(
                shape = CircleShape,
                color = BrandPrimary.copy(alpha = 0.10f),
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(initials, style = MaterialTheme.typography.titleSmall, color = BrandPrimary)
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(name, style = MaterialTheme.typography.titleSmall, color = TextPrimary)
                Text(
                    "$subtitle  •  $id",
                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                    color = TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            StatusBadge(status)
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted, modifier = Modifier.size(20.dp))
        }
    }
}

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// VITALS CARD (Grid cell)
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

@Composable
fun VitalCard(
    label: String,
    value: String,
    unit: String,
    icon: ImageVector,
    moduleColor: Color = HealthWorkerColor,
    isAbnormal: Boolean = false,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = if (isAbnormal) BorderStroke(1.5.dp, StatusAlertText) else null
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Icon tile
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = moduleColor.copy(alpha = 0.12f),
                modifier = Modifier.size(32.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = moduleColor, modifier = Modifier.size(18.dp))
                }
            }
            Column {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        value,
                        style = MaterialTheme.typography.headlineLarge.copy(fontSize = 22.sp),
                        color = if (isAbnormal) StatusAlertText else moduleColor
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(unit, style = MaterialTheme.typography.labelMedium, color = TextMuted)
                    if (isAbnormal) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("⚠", fontSize = 14.sp)
                    }
                }
                Text(label.uppercase(), style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp), color = TextMuted)
            }
        }
    }
}

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// UNIQUE ID DISPLAY
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

@Composable
fun UniqueIdDisplay(
    patientId: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = UniqueIdBg,
            border = BorderStroke(1.5.dp, UniqueIdBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    patientId,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    ),
                    color = BrandPrimary,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = BrandPrimary, modifier = Modifier.size(20.dp))
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text("Auto-generated", style = MaterialTheme.typography.bodySmall, color = TextMuted)
    }
}

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// DATE-TIME FIELD (Read-only, cream style)
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

@Composable
fun DateTimeField(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = TextSecondary)
        Spacer(modifier = Modifier.height(4.dp))
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = DateFieldBg,
            border = BorderStroke(1.dp, DateFieldBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.CalendarToday, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text(value, style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
            }
        }
    }
}

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// UPLOAD ZONE
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

@Composable
fun UploadZone(
    label: String,
    subLabel: String = "PDF, JPG, PNG up to 10MB",
    icon: ImageVector = Icons.Default.AttachFile,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = BackgroundPage,
        border = BorderStroke(1.5.dp, BrandPrimary.copy(alpha = 0.4f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(label, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            Text(subLabel, style = MaterialTheme.typography.bodySmall, color = TextMuted)
        }
    }
}

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// UPLOADED FILE ROW
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

@Composable
fun UploadedFileRow(
    fileName: String,
    fileType: String,
    onRemove: () -> Unit,
    moduleColor: Color = BrandPrimary,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundCard),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // File type icon tile
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (fileType == "report") BackgroundPage else DoctorBg,
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        if (fileType == "report") Icons.Default.Description else Icons.Default.Image,
                        contentDescription = null,
                        tint = if (fileType == "report") BrandPrimary else DoctorColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(fileName, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold), color = TextPrimary)
                Text("Uploaded", style = MaterialTheme.typography.bodySmall, color = TextMuted)
            }
            IconButton(onClick = onRemove, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Close, contentDescription = "Remove", tint = StatusAlertText, modifier = Modifier.size(18.dp))
            }
        }
    }
}

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// PRESCRIPTION ITEM CARD
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

@Composable
fun PrescriptionItemCard(
    name: String,
    dosageLine: String,
    durationDays: Int,
    isDispensed: Boolean = false,
    onDispenseToggle: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDispensed) StatusDispensedBg else BackgroundCard
        ),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (onDispenseToggle != null) {
                Checkbox(checked = isDispensed, onCheckedChange = { onDispenseToggle() })
            } else {
                // Leading icon tile
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = PharmacistBg,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Medication, contentDescription = null, tint = PharmacistColor, modifier = Modifier.size(20.dp))
                    }
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(name, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold), color = TextPrimary)
                Text(dosageLine, style = MaterialTheme.typography.labelMedium, color = TextMuted)
            }
            // Duration badge
            Surface(shape = RoundedCornerShape(20.dp), color = StatusAwaitingBg) {
                Text(
                    "${durationDays}d",
                    style = MaterialTheme.typography.labelSmall,
                    color = StatusAwaitingText,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
            if (isDispensed && onDispenseToggle != null) {
                Surface(shape = RoundedCornerShape(20.dp), color = StatusDispensedBg) {
                    Text("DISPENSED", style = MaterialTheme.typography.labelSmall, color = StatusDispensedText, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                }
            }
        }
    }
}

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// PRESCRIPTION PDF TRIGGER CARD
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

@Composable
fun PrescriptionReadyCard(
    doctorName: String,
    clinicName: String,
    onDownload: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AccentTeal)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Description, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Prescription Ready", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold), color = Color.White)
            }
            Text("$doctorName • $clinicName", style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.7f))
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onDownload,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Download PDF", color = AccentTeal, style = MaterialTheme.typography.labelLarge)
                }
                TextButton(onClick = onShare) {
                    Text("Share", color = Color.White.copy(alpha = 0.85f), style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// LABELED VALUE (simple)
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

@Composable
fun LabeledValue(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = TextSecondary)
        Text(text = value, style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
    }
}

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// INFO CARD
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

@Composable
fun InfoCard(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundCard)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (icon != null) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = BrandPrimary.copy(alpha = 0.10f),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(icon, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(20.dp))
                    }
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                Text(text = subtitle, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            }
        }
    }
}
