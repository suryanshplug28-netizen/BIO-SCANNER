package com.example.freshness.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.freshness.model.DegradationHotspot
import com.example.freshness.model.FreshnessResult
import com.example.freshness.model.FreshnessVerdict
import com.example.freshness.sample.SampleProduceDataProvider
import com.example.freshness.sample.SampleScenario

@Composable
fun DashboardMetricsPanel(
    result: FreshnessResult?,
    isRefrigerated: Boolean,
    isLiveCamera: Boolean,
    selectedScenario: SampleScenario?,
    onToggleRefrigeration: () -> Unit,
    onSelectScenario: (SampleScenario) -> Unit,
    onSelectHotspot: (DegradationHotspot) -> Unit,
    modifier: Modifier = Modifier
) {
    val freshnessScore = result?.freshnessPercentage ?: 92f
    val verdict = result?.verdict ?: FreshnessVerdict.OPTIMAL

    val scoreColor = when {
        freshnessScore >= 80f -> Color(0xFF10B981)
        freshnessScore >= 60f -> Color(0xFF0D9488)
        freshnessScore >= 40f -> Color(0xFFF59E0B)
        else -> Color(0xFFEF4444)
    }

    val animatedScoreProgress by animateFloatAsState(
        targetValue = freshnessScore / 100f,
        animationSpec = tween(750),
        label = "score_progress"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // 1. Primary Freshness Score Hero Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("freshness_score_card"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
            ),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "OVERALL FRESHNESS INDEX",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "${freshnessScore.toInt()}",
                            fontSize = 54.sp,
                            fontWeight = FontWeight.Black,
                            color = scoreColor,
                            letterSpacing = (-1.5).sp
                        )
                        Text(
                            text = "%",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = scoreColor
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = scoreColor.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, scoreColor.copy(alpha = 0.6f))
                    ) {
                        Text(
                            text = verdict.label,
                            color = scoreColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // Circular Progress Dial
                Box(
                    modifier = Modifier.size(90.dp),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                        val strokeW = 10.dp.toPx()
                        // Track
                        drawCircle(
                            color = Color(0x2294A3B8),
                            style = Stroke(width = strokeW)
                        )
                        // Progress Arc
                        drawArc(
                            color = scoreColor,
                            startAngle = -90f,
                            sweepAngle = 360f * animatedScoreProgress,
                            useCenter = false,
                            style = Stroke(width = strokeW, cap = StrokeCap.Round)
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = when (verdict) {
                                FreshnessVerdict.OPTIMAL -> Icons.Filled.CheckCircle
                                FreshnessVerdict.GOOD -> Icons.Filled.ThumbUp
                                FreshnessVerdict.CONSUME_SOON -> Icons.Filled.Warning
                                FreshnessVerdict.CRITICAL_SPOILAGE -> Icons.Filled.Cancel
                            },
                            contentDescription = null,
                            tint = scoreColor,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = result?.produceType?.category ?: "Produce",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // 2. Microbial Flora Ratio Card
        BioFloraCard(result = result)

        // 3. Shelf-Life Predictor Card
        ShelfLifePredictorCard(
            result = result,
            isRefrigerated = isRefrigerated,
            onToggleRefrigeration = onToggleRefrigeration
        )

        // 4. Surface Structure & Integrity Metrics Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("surface_metrics_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
            ),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Text(
                    text = "Surface Structure & Bio-Integrity",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "High-resolution texture frequency & colorimetry breakdown",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Metric Rows
                MetricRow(
                    label = "Texture Turgor (Smooth vs Wrinkled)",
                    score = result?.textureScore ?: 90f,
                    icon = Icons.Filled.Compress
                )
                Spacer(modifier = Modifier.height(10.dp))
                MetricRow(
                    label = "Pigment Vitality (Chlorophyll / Carotenoid)",
                    score = result?.colorIntegrityScore ?: 88f,
                    icon = Icons.Filled.Palette
                )
                Spacer(modifier = Modifier.height(10.dp))
                MetricRow(
                    label = "Cuticle & Epicuticular Wax Integrity",
                    score = result?.skinIntegrityScore ?: 94f,
                    icon = Icons.Filled.HealthAndSafety
                )
            }
        }

        // 5. Detected Spoilage Markers & Hotspots
        if (result != null && result.detectedHotspots.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("detected_hotspots_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Detected Spoilage Hotspots (${result.detectedHotspots.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Tap to inspect",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 4.dp)
                    ) {
                        items(result.detectedHotspots) { hotspot ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    Color(hotspot.spoilageType.badgeColorHex).copy(alpha = 0.7f)
                                ),
                                modifier = Modifier
                                    .clickable { onSelectHotspot(hotspot) }
                                    .testTag("hotspot_item_${hotspot.id}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(Color(hotspot.spoilageType.badgeColorHex))
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = hotspot.label,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "Severity: ${(hotspot.severity * 100).toInt()}%",
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 6. Interactive Sample Simulation Scenarios (Great for browser emulator testing)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("sample_simulation_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
            ),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Synthetic Scan Test Bench",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Test computer vision across degradation stages",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (!isLiveCamera) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF0284C7).copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "ACTIVE",
                                color = Color(0xFF0284C7),
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(SampleProduceDataProvider.scenarios) { scenario ->
                        val isSelected = scenario.id == selectedScenario?.id

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(
                                if (isSelected) 1.5.dp else 1.dp,
                                if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                            ),
                            modifier = Modifier
                                .clickable { onSelectScenario(scenario) }
                                .testTag("scenario_${scenario.id}")
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = "${scenario.produceType.emoji} ${scenario.stageName}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = scenario.expectedFreshnessTier,
                                    fontSize = 10.sp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun MetricRow(
    label: String,
    score: Float,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    val barColor = when {
        score >= 80f -> Color(0xFF10B981)
        score >= 60f -> Color(0xFF0D9488)
        score >= 40f -> Color(0xFFF59E0B)
        else -> Color(0xFFEF4444)
    }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = barColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = label,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium
                )
            }
            Text(
                text = "${score.toInt()}%",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = barColor
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { score / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = barColor,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}
