package com.example.atunes.ui.components

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.atunes.ui.theme.AccentRed
import com.example.atunes.ui.theme.BackgroundSecondary

/**
 * Animated sun/moon theme toggle switch.
 * [isDark] = true → moon icon; false → sun icon.
 * 300ms spring transition with icon morph.
 */
@Composable
fun ThemeToggle(
    isDark: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val transition = updateTransition(targetState = isDark, label = "ThemeToggle")

    val circleOffset by transition.animateFloat(
        transitionSpec = { spring(dampingRatio = 0.7f, stiffness = 300f) },
        label = "offset"
    ) { dark -> if (dark) 1f else 0f }

    Box(
        modifier = modifier
            .width(52.dp)
            .height(28.dp)
            .clip(CircleShape)
            .background(
                if (isDark) Color(0xFF2A2A2A) else Color(0xFFE8E0D5)
            )
            .clickable { onToggle() },
        contentAlignment = Alignment.CenterStart
    ) {
        val trackWidth = 52.dp
        val thumbSize = 22.dp
        val padding = 3.dp
        val travel = trackWidth - thumbSize - padding * 2

        Box(
            modifier = Modifier
                .padding(start = padding + travel * circleOffset)
                .size(thumbSize)
                .clip(CircleShape)
                .background(AccentRed)
                .drawBehind {
                    // Glow ring
                    drawCircle(
                        color = AccentRed.copy(alpha = 0.3f),
                        radius = size.minDimension / 2 + 3.dp.toPx(),
                        style = Stroke(width = 2.dp.toPx())
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            // Sun rays or moon crescent drawn via Canvas
            androidx.compose.foundation.Canvas(modifier = Modifier.size(14.dp)) {
                val cx = size.width / 2
                val cy = size.height / 2
                val r = size.minDimension / 2 * 0.6f

                if (isDark) {
                    // Moon crescent
                    drawArc(
                        color = Color.White,
                        startAngle = 30f,
                        sweepAngle = 300f,
                        useCenter = false,
                        style = Stroke(width = 2.dp.toPx())
                    )
                } else {
                    // Sun circle
                    drawCircle(color = Color.White, radius = r * 0.7f)
                    // Sun rays
                    for (i in 0 until 8) {
                        val angle = Math.toRadians(i * 45.0)
                        val startR = r * 0.85f
                        val endR = r * 1.1f
                        drawLine(
                            color = Color.White,
                            start = androidx.compose.ui.geometry.Offset(
                                (cx + startR * kotlin.math.cos(angle)).toFloat(),
                                (cy + startR * kotlin.math.sin(angle)).toFloat()
                            ),
                            end = androidx.compose.ui.geometry.Offset(
                                (cx + endR * kotlin.math.cos(angle)).toFloat(),
                                (cy + endR * kotlin.math.sin(angle)).toFloat()
                            ),
                            strokeWidth = 1.5.dp.toPx()
                        )
                    }
                }
            }
        }
    }
}
