package com.example.practice.campus.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val CampusBlue = Color(0xFF245AE8)
val CampusNavy = Color(0xFF192334)
val CampusMuted = Color(0xFF718096)
val CampusBackground = Color(0xFFF6F8FA)
val CampusBorder = Color(0xFFE1E7EF)
val CampusRed = Color(0xFFDC3545)

@Composable
fun CampusTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = CampusBlue,
            onPrimary = Color.White,
            primaryContainer = Color(0xFFDCEAFF),
            onPrimaryContainer = Color(0xFF24449A),
            background = CampusBackground,
            surface = Color.White,
            onSurface = CampusNavy,
            onBackground = CampusNavy,
            onSurfaceVariant = CampusMuted,
            outline = Color(0xFFCBD5E1),
            secondaryContainer = Color(0xFFDCEAFF),
            error = CampusRed
        ),
        content = content
    )
}
