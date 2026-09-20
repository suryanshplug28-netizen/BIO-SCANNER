package com.example.freshness.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.ui.window.Dialog
import com.example.freshness.model.DegradationHotspot
import com.example.freshness.model.SpoilageType

@Composable
fun FreshnessDetailDialog(
    hotspot: DegradationHotspot?,
    onDismiss: () -> Unit
) {
    if (hotspot == null) return

    val badgeColor = Color(hotspot.spoilageType.badgeColorHex)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("hotspot_detail_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(badgeColor)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = hotspot.label,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Filled.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Severity Bar
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Degradation Severity",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${(hotspot.severity * 100).toInt()}%",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = badgeColor
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { hotspot.severity },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = badgeColor,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Biological Proxy Context
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Text(
                            text = "Microbiological Etiology",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = when (hotspot.spoilageType) {
                                SpoilageType.FUNGAL_MOLD -> "Mycelium hyphae penetration identified. Fungi such as Botrytis cinerea or Penicillium digest cell walls and produce mycotoxins that can penetrate deep beyond the visible surface spot."
                                SpoilageType.BACTERIAL_ROT -> "Bacterial soft rot proxy detected (Erwinia carotovora / Pectobacterium). Pectolytic enzymes cause rapid maceration of parenchyma tissue resulting in water-soaked collapse."
                                SpoilageType.ENZYMATIC_BROWNING -> "Polyphenol oxidase (PPO) catalyzed oxidation of phenolic compounds into brown melanoid pigments following cell membrane breakdown."
                                SpoilageType.OXIDATIVE_BRUISING -> "Impact damage causing internal cellular micro-fractures, localized moisture seepage, and early microbial colonization."
                                SpoilageType.HEALTHY_TISSUE -> "Healthy tissue with natural variation."
                            },
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 17.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Safety Guidance Action
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (hotspot.spoilageType == SpoilageType.FUNGAL_MOLD || hotspot.severity > 0.6f)
                        Color(0xFFFEF2F2) else Color(0xFFF0FDF4),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (hotspot.spoilageType == SpoilageType.FUNGAL_MOLD || hotspot.severity > 0.6f)
                            Color(0xFFFCA5A5) else Color(0xFF86EFAC)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Warning,
                            contentDescription = null,
                            tint = if (hotspot.spoilageType == SpoilageType.FUNGAL_MOLD || hotspot.severity > 0.6f)
                                Color(0xFFDC2626) else Color(0xFF16A34A),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = when (hotspot.spoilageType) {
                                SpoilageType.FUNGAL_MOLD -> "Do not consume soft porous fruits with visible mold; spores spread invisibly throughout the food."
                                SpoilageType.BACTERIAL_ROT -> "Excise affected section generously (at least 2.5 cm) on firm vegetables; discard if soft or foul-smelling."
                                else -> "Cosmetic discoloration; item remains safe to eat or cook."
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (hotspot.spoilageType == SpoilageType.FUNGAL_MOLD || hotspot.severity > 0.6f)
                                Color(0xFF991B1B) else Color(0xFF166534)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dismiss_hotspot_dialog_button")
                ) {
                    Text("Understood")
                }
            }
        }
    }
}
