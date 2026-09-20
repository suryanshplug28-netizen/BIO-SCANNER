package com.example.freshness.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.freshness.model.FreshnessResult
import com.example.freshness.model.FreshnessVerdict

@Composable
fun ShelfLifePredictorCard(
    result: FreshnessResult?,
    isRefrigerated: Boolean,
    onToggleRefrigeration: () -> Unit,
    modifier: Modifier = Modifier
) {
    val daysRemaining = if (isRefrigerated) {
        result?.daysRemainingRefrigerated ?: 0f
    } else {
        result?.daysRemainingRoomTemp ?: 0f
    }

    val verdict = result?.verdict ?: FreshnessVerdict.OPTIMAL

    val statusColor = when (verdict) {
        FreshnessVerdict.OPTIMAL -> Color(0xFF10B981)
        FreshnessVerdict.GOOD -> Color(0xFF0D9488)
        FreshnessVerdict.CONSUME_SOON -> Color(0xFFF59E0B)
        FreshnessVerdict.CRITICAL_SPOILAGE -> Color(0xFFEF4444)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("shelf_life_predictor_card"),
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
            // Header with Room Temp vs Fridge Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF59E0B).copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.HourglassBottom,
                            contentDescription = "Shelf-Life Countdown",
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Shelf-Life Predictor",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isRefrigerated) "Chilled Storage (4°C)" else "Room Temperature (21°C)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Storage mode pill button
                FilterChip(
                    selected = isRefrigerated,
                    onClick = onToggleRefrigeration,
                    label = {
                        Text(
                            text = if (isRefrigerated) "Fridge (4°C)" else "Room Temp",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = if (isRefrigerated) Icons.Filled.AcUnit else Icons.Filled.Thermostat,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF0284C7),
                        selectedLabelColor = Color.White,
                        selectedLeadingIconColor = Color.White
                    ),
                    modifier = Modifier.testTag("toggle_refrigeration_chip")
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Large Countdown Display
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Estimated Freshness Countdown",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = if (daysRemaining <= 0.2f) "< 12" else String.format("%.1f", daysRemaining),
                            fontSize = 44.sp,
                            fontWeight = FontWeight.Black,
                            color = statusColor,
                            letterSpacing = (-1).sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (daysRemaining <= 0.2f) "HOURS" else "DAYS",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = statusColor
                        )
                    }
                }

                // Verdict Pill Badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = statusColor.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, statusColor.copy(alpha = 0.7f))
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = verdict.label.uppercase(),
                            color = statusColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                        Text(
                            text = if (daysRemaining > 2f) "Good to Keep" else if (daysRemaining > 0f) "Use Rapidly" else "Do Not Eat",
                            color = statusColor,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Storage & Preservation Tip
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "💡",
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = result?.produceType?.preservationTips ?: "Store in a cool dry area away from ethylene producers.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}
