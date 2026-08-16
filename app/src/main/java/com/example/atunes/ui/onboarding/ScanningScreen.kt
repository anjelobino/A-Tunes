package com.example.atunes.ui.onboarding

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.atunes.ui.theme.*

@Composable
fun ScanningScreen(
    vm: OnboardingViewModel = viewModel(),
    onScanComplete: () -> Unit
) {
    val scanState by vm.scanState.collectAsStateWithLifecycle()

    // Auto-start scan
    LaunchedEffect(Unit) { vm.startScan() }

    // Navigate when done
    LaunchedEffect(scanState) {
        if (scanState is ScanState.Done) onScanComplete()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "scan_vinyl")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(3000, easing = LinearEasing)),
        label = "rotation"
    )
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            tween(800, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Spinning scan indicator
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .rotate(rotation)
                    .clip(CircleShape)
                    .background(VinylBlack)
                    .drawBehind {
                        for (i in 1..6) {
                            drawCircle(
                                color = AccentRed.copy(alpha = 0.08f + i * 0.03f),
                                radius = size.minDimension / 2 * (0.3f + i * 0.1f),
                                style = Stroke(width = 1.5.dp.toPx())
                            )
                        }
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(AccentRedLight, AccentRedDark)
                            ),
                            radius = size.minDimension * 0.22f
                        )
                        drawCircle(color = VinylBlack, radius = size.minDimension * 0.05f)
                    }
            )

            Spacer(Modifier.height(40.dp))

            val message = when (val s = scanState) {
                is ScanState.Idle     -> "Preparing your library..."
                is ScanState.Scanning -> "Reading your library… ${s.count} tracks found"
                is ScanState.Done     -> "Found ${s.total} tracks! ✨"
                is ScanState.Error    -> "Oops: ${s.message}"
            }

            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(24.dp))

            // Indeterminate progress bar
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = AccentRed,
                trackColor = Divider,
                strokeCap = StrokeCap.Round
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "This only happens once.\nYour library will be ready instantly next time.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )
        }
    }
}
