package com.telemed.demo.ui.theme

import androidx.compose.ui.graphics.Color

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// PRIMARY BRAND — HI Samagra
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
val BrandPrimary = Color(0xFFF5B731)          // HI Samagra golden yellow
val BrandPrimaryLight = Color(0xFFF7C44E)
val BrandPrimaryDark = Color(0xFFD4960A)
val BrandPrimaryContainer = Color(0xFFFFF8E1) // light yellow bg

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// BACKGROUNDS
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
val BackgroundPage = Color(0xFFFAF7F2)         // warm cream off-white
val BackgroundCard = Color(0xFFFFFFFF)          // white cards
val BackgroundDark = Color(0xFF1C1B1F)
val SurfaceDark = Color(0xFF2C2B30)

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// HEADER / HERO
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
val HeaderNavy = Color(0xFF2D2A26)             // dark charcoal (complements yellow)
val HeaderNavyLighter = Color(0xFF3D3A35)      // lighter charcoal for decorative circle

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// ACCENT TEAL (Clinical trust — Doctor module)
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
val AccentTeal = Color(0xFF0B6E6E)
val AccentTealLight = Color(0xFF1A9E9E)
val AccentTealContainer = Color(0xFFE0F2F2)

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// TEXT
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
val TextPrimary = Color(0xFF1A1A1A)
val TextSecondary = Color(0xFF6B6B6B)
val TextMuted = Color(0xFF9E9E9E)
val TextOnDark = Color(0xFFFFFFFF)

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// MODULE IDENTITY COLORS
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
val HealthWorkerColor = Color(0xFFD4960A)      // deep gold — field/grassroots
val PharmacistColor = Color(0xFF7B5EA7)        // muted violet — dispensary
val DoctorColor = Color(0xFF0B6E6E)            // clinical teal — medical authority

val HealthWorkerBg = Color(0xFFFFF8E1)
val PharmacistBg = Color(0xFFF3EDF9)
val DoctorBg = Color(0xFFE0F2F2)

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// STATUS COLORS (bg + text pairs)
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// Normal/OK/Done / Consent YES
val StatusDoneBg = Color(0xFFE3F4E8)
val StatusDoneText = Color(0xFF1B6B2E)

// Alert/Critical / Declined/NO
val StatusAlertBg = Color(0xFFFDE8E3)
val StatusAlertText = Color(0xFFC0392B)

// Pending/In Review / In Consult
val StatusPendingBg = Color(0xFFE3F0FD)
val StatusPendingText = Color(0xFF1A56A0)

// Dispensed
val StatusDispensedBg = Color(0xFFEDE3F8)
val StatusDispensedText = Color(0xFF5B3A8A)

// Awaiting
val StatusAwaitingBg = Color(0xFFFFF3CD)
val StatusAwaitingText = Color(0xFF856404)

// Uploaded
val StatusUploadedBg = Color(0xFFE3F4E8)
val StatusUploadedText = Color(0xFF1B6B2E)

// Doctor Busy
val StatusBusyBg = Color(0xFFFFF0E0)
val StatusBusyText = Color(0xFFD4770B)

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// DIVIDER / OUTLINE
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
val DividerColor = Color(0xFFE0E0E0)
val OutlineGray = Color(0xFFE0E0E0)
val CardShadow = Color(0x14000000)             // rgba(0,0,0,0.08)

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// FORM SPECIAL
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
val DateFieldBg = Color(0xFFFFF8E1)
val DateFieldBorder = Color(0xFFF5B731)
val UniqueIdBg = Color(0xFFFFF8E1)
val UniqueIdBorder = Color(0xFFF5B731)

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// INACTIVE / STEP
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
val InactiveGray = Color(0xFFD3D1C7)
val InactiveTabText = Color(0xFF9E9E9E)

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// CALL SCREEN BACKGROUNDS
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
val CallDarkBg = Color(0xFF2D2A26)
val CallAcceptGreen = Color(0xFF2E7D32)
val CallDeclineRed = Color(0xFFC0392B)

// Legacy compatibility aliases
val AccentRed = Color(0xFFC0392B)
val CardBackground = Color(0xFFFFFFFF)

// MMS / WhatsApp colors
val WhatsAppGreen = Color(0xFF25D366)
val MMSBlue = Color(0xFF0078D7)
