package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.data.model.DrawingStep
import com.example.data.model.StepPhase

@Composable
fun StepStrokeGuideVisualizer(
    step: DrawingStep,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulseTransition")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.45f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1E202B))
            .padding(12.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val cx = w / 2f
            val cy = h / 2f

            // Clean parchment sketch canvas background
            drawRect(
                color = Color(0xFF252736),
                size = size
            )

            // Canvas coordinate guide markers
            val markerColor = Color.White.copy(alpha = 0.12f)
            drawLine(
                color = markerColor,
                start = Offset(cx, 16f),
                end = Offset(cx, h - 16f),
                strokeWidth = 1f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
            )
            drawLine(
                color = markerColor,
                start = Offset(16f, cy),
                end = Offset(w - 16f, cy),
                strokeWidth = 1f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
            )

            val activeColor = Color(0xFFFFB84D).copy(alpha = pulseAlpha)
            val baseGuideColor = Color(0xFF6B728E).copy(alpha = 0.7f)
            val strokeThick = 4.5f

            when (step.phase) {
                StepPhase.GEOMETRIC_BASES -> {
                    // Head circle
                    val headRadius = w * 0.22f
                    val headCenter = Offset(cx, h * 0.35f)
                    drawCircle(
                        color = activeColor,
                        radius = headRadius,
                        center = headCenter,
                        style = Stroke(
                            width = strokeThick,
                            cap = StrokeCap.Round
                        )
                    )

                    // Proportions vertical axis
                    drawLine(
                        color = Color(0xFF4DD0E1).copy(alpha = 0.8f),
                        start = Offset(cx, h * 0.12f),
                        end = Offset(cx, h * 0.88f),
                        strokeWidth = 2f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)
                    )

                    // Body oval
                    val ovalWidth = w * 0.44f
                    val ovalHeight = h * 0.32f
                    drawOval(
                        color = activeColor,
                        topLeft = Offset(cx - (ovalWidth / 2f), h * 0.52f),
                        size = Size(ovalWidth, ovalHeight),
                        style = Stroke(
                            width = strokeThick,
                            cap = StrokeCap.Round
                        )
                    )
                }

                StepPhase.SECONDARY_GUIDES -> {
                    // Faint base shapes
                    drawCircle(
                        color = baseGuideColor,
                        radius = w * 0.22f,
                        center = Offset(cx, h * 0.35f),
                        style = Stroke(width = 1.5f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f), 0f))
                    )
                    drawOval(
                        color = baseGuideColor,
                        topLeft = Offset(cx - (w * 0.22f), h * 0.52f),
                        size = Size(w * 0.44f, h * 0.32f),
                        style = Stroke(width = 1.5f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f), 0f))
                    )

                    // Active secondary features (Ears triangles & spine curve)
                    val earLeft = Path().apply {
                        moveTo(cx - (w * 0.18f), h * 0.24f)
                        lineTo(cx - (w * 0.28f), h * 0.10f)
                        lineTo(cx - (w * 0.06f), h * 0.16f)
                    }
                    val earRight = Path().apply {
                        moveTo(cx + (w * 0.06f), h * 0.16f)
                        lineTo(cx + (w * 0.28f), h * 0.10f)
                        lineTo(cx + (w * 0.18f), h * 0.24f)
                    }
                    drawPath(earLeft, activeColor, style = Stroke(strokeThick, cap = StrokeCap.Round))
                    drawPath(earRight, activeColor, style = Stroke(strokeThick, cap = StrokeCap.Round))

                    // Spine S-Curve
                    val spine = Path().apply {
                        moveTo(cx, h * 0.35f)
                        cubicTo(
                            cx - (w * 0.14f), h * 0.48f,
                            cx - (w * 0.16f), h * 0.65f,
                            cx, h * 0.82f
                        )
                    }
                    drawPath(spine, Color(0xFF4DD0E1), style = Stroke(strokeThick, cap = StrokeCap.Round))
                }

                StepPhase.PRIMARY_OUTLINES -> {
                    // Muzzle / Snout wedge
                    val snout = Path().apply {
                        moveTo(cx - (w * 0.09f), h * 0.36f)
                        lineTo(cx, h * 0.45f)
                        lineTo(cx + (w * 0.09f), h * 0.36f)
                        close()
                    }
                    drawPath(snout, activeColor, style = Stroke(strokeThick, cap = StrokeCap.Round))

                    // Limb cylinders
                    drawLine(
                        color = activeColor,
                        start = Offset(cx - (w * 0.10f), h * 0.58f),
                        end = Offset(cx - (w * 0.10f), h * 0.84f),
                        strokeWidth = strokeThick,
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        color = activeColor,
                        start = Offset(cx + (w * 0.10f), h * 0.58f),
                        end = Offset(cx + (w * 0.10f), h * 0.84f),
                        strokeWidth = strokeThick,
                        cap = StrokeCap.Round
                    )
                    // Paws baseline
                    drawOval(
                        color = activeColor,
                        topLeft = Offset(cx - (w * 0.15f), h * 0.82f),
                        size = Size(w * 0.10f, h * 0.06f),
                        style = Stroke(strokeThick)
                    )
                    drawOval(
                        color = activeColor,
                        topLeft = Offset(cx + (w * 0.05f), h * 0.82f),
                        size = Size(w * 0.10f, h * 0.06f),
                        style = Stroke(strokeThick)
                    )
                }

                StepPhase.DEFINING_DETAILS -> {
                    // Almond Eyes
                    val eyeL = Path().apply {
                        moveTo(cx - (w * 0.16f), h * 0.33f)
                        quadraticTo(cx - (w * 0.10f), h * 0.30f, cx - (w * 0.05f), h * 0.33f)
                        quadraticTo(cx - (w * 0.10f), h * 0.36f, cx - (w * 0.16f), h * 0.33f)
                    }
                    val eyeR = Path().apply {
                        moveTo(cx + (w * 0.05f), h * 0.33f)
                        quadraticTo(cx + (w * 0.10f), h * 0.30f, cx + (w * 0.16f), h * 0.33f)
                        quadraticTo(cx + (w * 0.10f), h * 0.36f, cx + (w * 0.05f), h * 0.33f)
                    }
                    drawPath(eyeL, activeColor, style = Stroke(strokeThick, cap = StrokeCap.Round))
                    drawPath(eyeR, activeColor, style = Stroke(strokeThick, cap = StrokeCap.Round))

                    // Nose button
                    drawCircle(color = Color(0xFFFF7043), radius = 6f, center = Offset(cx, h * 0.44f))

                    // Sweeping Tail Crescent
                    val tail = Path().apply {
                        moveTo(cx + (w * 0.12f), h * 0.60f)
                        cubicTo(
                            cx + (w * 0.45f), h * 0.55f,
                            cx + (w * 0.42f), h * 0.85f,
                            cx - (w * 0.02f), h * 0.86f
                        )
                    }
                    drawPath(tail, activeColor, style = Stroke(strokeThick * 1.4f, cap = StrokeCap.Round))
                }

                StepPhase.SHADING_AND_POLISH -> {
                    // Shadow hatching lines
                    val hatchColor = Color(0xFF4DD0E1).copy(alpha = 0.6f)
                    for (i in 0..6) {
                        val offset = i * 12f
                        drawLine(
                            color = hatchColor,
                            start = Offset(cx - (w * 0.18f) + offset, h * 0.86f),
                            end = Offset(cx - (w * 0.10f) + offset, h * 0.90f),
                            strokeWidth = 2.5f,
                            cap = StrokeCap.Round
                        )
                    }

                    // Cheek fluff zigzag
                    val fluff = Path().apply {
                        moveTo(cx - (w * 0.22f), h * 0.38f)
                        lineTo(cx - (w * 0.28f), h * 0.42f)
                        lineTo(cx - (w * 0.20f), h * 0.44f)
                        lineTo(cx - (w * 0.27f), h * 0.48f)
                        lineTo(cx - (w * 0.16f), h * 0.49f)
                    }
                    drawPath(fluff, activeColor, style = Stroke(strokeThick, cap = StrokeCap.Round))

                    // Sparkle star
                    drawCircle(color = Color.White, radius = 5f, center = Offset(cx - (w * 0.11f), h * 0.32f))
                    drawCircle(color = Color.White, radius = 5f, center = Offset(cx + (w * 0.09f), h * 0.32f))
                }
            }
        }
    }
}
