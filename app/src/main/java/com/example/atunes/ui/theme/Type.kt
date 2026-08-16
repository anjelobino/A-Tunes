package com.example.atunes.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.atunes.R

// ── Font Families ────────────────────────────────────────────────────────────

val PoppinsFamily = FontFamily(
    Font(R.font.poppins_regular, FontWeight.Normal),
    Font(R.font.poppins_medium, FontWeight.Medium),
    Font(R.font.poppins_semibold, FontWeight.SemiBold),
    Font(R.font.poppins_bold, FontWeight.Bold)
)

val InterFamily = FontFamily(
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_medium, FontWeight.Medium),
    Font(R.font.inter_semibold, FontWeight.SemiBold)
)

// ── Typography Scale ─────────────────────────────────────────────────────────

val VinylTypography = Typography(
    // Display — used for "Hey, Listener" greeting
    displayLarge = TextStyle(
        fontFamily = PoppinsFamily,
        fontWeight = FontWeight.Bold,
        fontSize   = 36.sp,
        lineHeight = 44.sp
    ),
    displayMedium = TextStyle(
        fontFamily = PoppinsFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize   = 28.sp,
        lineHeight = 36.sp
    ),
    // Headlines — screen titles, album names
    headlineLarge = TextStyle(
        fontFamily = PoppinsFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize   = 24.sp,
        lineHeight = 32.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = PoppinsFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize   = 20.sp,
        lineHeight = 28.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = PoppinsFamily,
        fontWeight = FontWeight.Medium,
        fontSize   = 16.sp,
        lineHeight = 24.sp
    ),
    // Body — track lists, descriptions
    bodyLarge = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Normal,
        fontSize   = 16.sp,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Normal,
        fontSize   = 14.sp,
        lineHeight = 20.sp
    ),
    bodySmall = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Normal,
        fontSize   = 12.sp,
        lineHeight = 16.sp
    ),
    // Labels — chips, badges, time stamps
    labelLarge = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Medium,
        fontSize   = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Medium,
        fontSize   = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Medium,
        fontSize   = 10.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.5.sp
    )
)