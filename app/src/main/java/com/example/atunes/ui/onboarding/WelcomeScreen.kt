package com.example.atunes.ui.onboarding

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.atunes.ui.theme.*

@Composable
fun WelcomeScreen(
    selectedFolder: String? = null,
    onSelectFolder: () -> Unit,
    onGetStarted: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "vinyl")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing)
        ),
        label = "rotation"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary),
        contentAlignment = Alignment.Center
    ) {
        // Background glow
        Box(
            modifier = Modifier
                .size(320.dp)
                .align(Alignment.TopCenter)
                .offset(y = (-60).dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(AccentRed.copy(alpha = 0.25f), Color.Transparent)
                    ),
                    shape = CircleShape
                )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(80.dp))

            // Spinning vinyl record hero
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .rotate(rotation)
                    .clip(CircleShape)
                    .background(VinylBlack)
                    .drawBehind {
                        // Vinyl grooves
                        for (i in 1..8) {
                            drawCircle(
                                color = Color.White.copy(alpha = 0.04f),
                                radius = size.minDimension / 2 * (0.35f + i * 0.07f),
                                style = Stroke(width = 1.5.dp.toPx())
                            )
                        }
                        // Red center label
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(AccentRedLight, AccentRedDark)
                            ),
                            radius = size.minDimension * 0.18f
                        )
                        // Center hole
                        drawCircle(
                            color = VinylBlack,
                            radius = size.minDimension * 0.04f
                        )
                    },
                contentAlignment = Alignment.Center
            ) {}

            Spacer(Modifier.height(48.dp))

            // App name
            Text(
                text = "ATunes",
                style = MaterialTheme.typography.displayMedium,
                color = AccentRed
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Your music, your device.\nNo internet needed.",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 26.sp
            )

            Spacer(Modifier.height(48.dp))

            // CTA Button
            Button(
                onClick = onGetStarted,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentRed,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Get Started",
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            Spacer(Modifier.height(16.dp))

            OutlinedButton(
                onClick = onSelectFolder,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, AccentRed.copy(alpha = 0.5f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentRed)
            ) {
                Icon(Icons.Rounded.Folder, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = if (selectedFolder.isNullOrBlank()) "Choose Music Folder" else "Folder: $selectedFolder",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}
