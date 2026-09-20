package com.example.freshness.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.freshness.model.DegradationHotspot
import com.example.freshness.model.FreshnessResult
import com.example.freshness.model.SpoilageType

@Composable
fun CameraOverlayHud(
    result: FreshnessResult?,
    showHeatmap: Boolean,
    isFrozen: Boolean,
    isLiveCamera: Boolean,
    selectedHotspot: DegradationHotspot?,
    onHotspotClicked: (DegradationHotspot) -> Unit,
    onToggleFreeze: () -> Unit,
    onToggleHeatmap: () -> Unit,
    onToggleCameraMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Scanning Laser Beam Animation
    val infiniteTransition = rememberInfiniteTransition(label = "scan_laser")
    val scanYProgress by infiniteTransition.animateFloat(
        initialValue = 0.08f,
        targetValue = 0.92f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser_y"
    )

    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val viewWidth = maxWidth
        val viewHeight = maxHeight

        // 1. Draw Target Reticle and Scan Sweep Laser on Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val reticleMargin = 36.dp.toPx()
            val reticleLength = 40.dp.toPx()
            val strokeWidth = 3.dp.toPx()
            val reticleColor = if (isFrozen) Color(0xFF38BDF8) else Color(0xFF10B981)

            // Top-Left Corner Bracket
            drawLine(reticleColor, Offset(reticleMargin, reticleMargin), Offset(reticleMargin + reticleLength, reticleMargin), strokeWidth)
            drawLine(reticleColor, Offset(reticleMargin, reticleMargin), Offset(reticleMargin, reticleMargin + reticleLength), strokeWidth)

            // Top-Right Corner Bracket
            drawLine(reticleColor, Offset(w - reticleMargin, reticleMargin), Offset(w - reticleMargin - reticleLength, reticleMargin), strokeWidth)
            drawLine(reticleColor, Offset(w - reticleMargin, reticleMargin), Offset(w - reticleMargin, reticleMargin + reticleLength), strokeWidth)

            // Bottom-Left Corner Bracket
            drawLine(reticleColor, Offset(reticleMargin, h - reticleMargin), Offset(reticleMargin + reticleLength, h - reticleMargin), strokeWidth)
            drawLine(reticleColor, Offset(reticleMargin, h - reticleMargin), Offset(reticleMargin, h - reticleMargin - reticleLength), strokeWidth)

            // Bottom-Right Corner Bracket
            drawLine(reticleColor, Offset(w - reticleMargin, h - reticleMargin), Offset(w - reticleMargin - reticleLength, h - reticleMargin), strokeWidth)
            drawLine(reticleColor, Offset(w - reticleMargin, h - reticleMargin), Offset(w - reticleMargin, h - reticleMargin - reticleLength), strokeWidth)

            // Center Crosshair
            val cx = w / 2f
            val cy = h / 2f
            val crossSize = 14.dp.toPx()
            drawLine(reticleColor.copy(alpha = 0.6f), Offset(cx - crossSize, cy), Offset(cx + crossSize, cy), 1.5.dp.toPx())
            drawLine(reticleColor.copy(alpha = 0.6f), Offset(cx, cy - crossSize), Offset(cx, cy + crossSize), 1.5.dp.toPx())
            drawCircle(reticleColor.copy(alpha = 0.3f), radius = 28.dp.toPx(), center = Offset(cx, cy), style = Stroke(1.dp.toPx()))

            // Active Scanning Sweep Beam
            if (!isFrozen) {
                val laserY = h * scanYProgress
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0x3310B981),
                            Color(0x8810B981),
                            Color(0x3310B981),
                            Color.Transparent
                        ),
                        startY = laserY - 24.dp.toPx(),
                        endY = laserY + 24.dp.toPx()
                    ),
                    topLeft = Offset(reticleMargin, laserY - 18.dp.toPx()),
                    size = Size(w - (reticleMargin * 2), 36.dp.toPx())
                )
                drawLine(
                    color = Color(0xFF34D399).copy(alpha = pulseAlpha),
                    start = Offset(reticleMargin, laserY),
                    end = Offset(w - reticleMargin, laserY),
                    strokeWidth = 2.dp.toPx()
                )
            }
        }

        // 2. Degradation Hotspots / Heatmap Overlay
        if (showHeatmap && result != null) {
            result.detectedHotspots.forEach { hotspot ->
                val boxLeft = viewWidth * hotspot.normalizedX
                val boxTop = viewHeight * hotspot.normalizedY
                val boxW = viewWidth * hotspot.normalizedWidth
                val boxH = viewHeight * hotspot.normalizedHeight

                val boxColor = when (hotspot.spoilageType) {
                    SpoilageType.FUNGAL_MOLD -> Color(0xFFA855F7) // Purple for fungal spores
                    SpoilageType.BACTERIAL_ROT -> Color(0xFFEF4444) // Crimson for soft rot
                    SpoilageType.ENZYMATIC_BROWNING -> Color(0xFFF59E0B) // Amber for browning
                    SpoilageType.OXIDATIVE_BRUISING -> Color(0xFFFB7185) // Rose for bruises
                    SpoilageType.HEALTHY_TISSUE -> Color(0xFF10B981)
                }

                Box(
                    modifier = Modifier
                        .offset(x = boxLeft, y = boxTop)
                        .size(width = boxW.coerceAtLeast(44.dp), height = boxH.coerceAtLeast(34.dp))
                        .clip(RoundedCornerShape(6.dp))
                        .background(boxColor.copy(alpha = 0.22f))
                        .border(1.5.dp, boxColor.copy(alpha = 0.85f), RoundedCornerShape(6.dp))
                        .clickable { onHotspotClicked(hotspot) }
                        .testTag("hotspot_${hotspot.id}")
                ) {
                    // Small floating badge with label
                    Text(
                        text = "${hotspot.label} ${(hotspot.severity * 100).toInt()}%",
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .background(boxColor.copy(alpha = 0.90f), RoundedCornerShape(bottomEnd = 4.dp))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }
        }

        // 3. Top HUD Status Bar (Inference latency, Mode, FPS)
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Live Status Pill
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xDD0F172A),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x4438BDF8))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (isFrozen) Color(0xFF38BDF8) else Color(0xFF10B981))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isFrozen) "FRAME FROZEN" else if (isLiveCamera) "LIVE CAMERA ON-DEVICE" else "SAMPLE SCAN TEST",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Inference Latency Pill
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xDD0F172A),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x4410B981))
            ) {
                Text(
                    text = "LATENCY: ${result?.analysisLatencyMs ?: 12}ms",
                    color = Color(0xFF34D399),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }
        }

        // 4. Viewfinder Floating Controls (Right Side)
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Heatmap Overlay Toggle
            SmallFloatingActionButton(
                onClick = onToggleHeatmap,
                containerColor = if (showHeatmap) Color(0xFF10B981) else Color(0xCC1E293B),
                contentColor = Color.White,
                modifier = Modifier
                    .size(42.dp)
                    .testTag("toggle_heatmap_button")
            ) {
                Icon(
                    imageVector = if (showHeatmap) Icons.Filled.GridOn else Icons.Outlined.GridOff,
                    contentDescription = "Toggle Spoilage Heatmap",
                    modifier = Modifier.size(20.dp)
                )
            }

            // Freeze / Pause Frame Button
            SmallFloatingActionButton(
                onClick = onToggleFreeze,
                containerColor = if (isFrozen) Color(0xFF0284C7) else Color(0xCC1E293B),
                contentColor = Color.White,
                modifier = Modifier
                    .size(42.dp)
                    .testTag("freeze_frame_button")
            ) {
                Icon(
                    imageVector = if (isFrozen) Icons.Filled.PlayArrow else Icons.Filled.Pause,
                    contentDescription = "Freeze Inspection Frame",
                    modifier = Modifier.size(20.dp)
                )
            }

            // Live Camera vs Sample Stream Switcher
            SmallFloatingActionButton(
                onClick = onToggleCameraMode,
                containerColor = Color(0xCC1E293B),
                contentColor = Color.White,
                modifier = Modifier
                    .size(42.dp)
                    .testTag("toggle_camera_source_button")
            ) {
                Icon(
                    imageVector = if (isLiveCamera) Icons.Filled.Cameraswitch else Icons.Filled.CameraAlt,
                    contentDescription = "Switch Camera Source",
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // 5. Bottom Viewfinder Ticker
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(12.dp),
            color = Color(0xCC090D14),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33475569))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${result?.produceType?.emoji ?: "🍎"} ${result?.produceType?.displayName ?: "Produce"}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                Text(
                    text = "HOTSPOTS: ${result?.detectedHotspots?.size ?: 0} DETECTED",
                    color = if ((result?.detectedHotspots?.size ?: 0) > 0) Color(0xFFF87171) else Color(0xFF34D399),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}
