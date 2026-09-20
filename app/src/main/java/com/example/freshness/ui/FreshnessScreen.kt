package com.example.freshness.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.freshness.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FreshnessScreen(
    viewModel: FreshnessViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    var showInfoDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF10B981)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CenterFocusStrong,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Freshness Analyzer",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Real-Time Edge Computer Vision",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.saveCurrentScanToHistory() },
                        modifier = Modifier.testTag("save_scan_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.BookmarkAdd,
                            contentDescription = "Save Scan"
                        )
                    }
                    IconButton(
                        onClick = { showInfoDialog = true },
                        modifier = Modifier.testTag("info_dialog_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = "About Computer Vision & Bacteria Proxies"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
        ) {
            // 1. Prominent Camera Viewfinder (Top Half of the Screen)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp)
            ) {
                CameraViewFinder(
                    result = uiState.currentResult,
                    isLiveCamera = uiState.isLiveCameraActive,
                    showHeatmap = uiState.showHeatmapOverlay,
                    isFrozen = uiState.isFrozen,
                    selectedScenario = uiState.selectedScenario,
                    selectedHotspot = uiState.selectedHotspot,
                    onFrameCaptured = { bitmap ->
                        viewModel.processLiveCameraFrame(bitmap)
                    },
                    onHotspotClicked = { hotspot ->
                        viewModel.selectHotspot(hotspot)
                    },
                    onToggleFreeze = {
                        viewModel.toggleFreeze()
                    },
                    onToggleHeatmap = {
                        viewModel.toggleHeatmap()
                    },
                    onToggleCameraMode = {
                        viewModel.toggleLiveCamera(!uiState.isLiveCameraActive)
                    }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 2. Target Produce Calibration Chips
            ProducePickerBar(
                selectedProduceType = uiState.selectedProduceType,
                onProduceSelected = { produce ->
                    viewModel.selectProduceType(produce)
                }
            )

            Spacer(modifier = Modifier.height(6.dp))

            // 3. Rich Data Panel (Freshness, Flora Proxy Ratio, Shelf-Life Countdown, Surface Structure)
            DashboardMetricsPanel(
                result = uiState.currentResult,
                isRefrigerated = uiState.isRefrigeratedMode,
                isLiveCamera = uiState.isLiveCameraActive,
                selectedScenario = uiState.selectedScenario,
                onToggleRefrigeration = {
                    viewModel.toggleRefrigeration()
                },
                onSelectScenario = { scenario ->
                    viewModel.loadScenario(scenario)
                },
                onSelectHotspot = { hotspot ->
                    viewModel.selectHotspot(hotspot)
                }
            )
        }

        // Hotspot detail inspection dialog
        if (uiState.selectedHotspot != null) {
            FreshnessDetailDialog(
                hotspot = uiState.selectedHotspot,
                onDismiss = { viewModel.selectHotspot(null) }
            )
        }

        // Educational Info Dialog on Optical Bacteria Proxies
        if (showInfoDialog) {
            AlertDialog(
                onDismissRequest = { showInfoDialog = false },
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Biotech,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                },
                title = {
                    Text(
                        text = "How Edge CV Detects Spoilage",
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Microscopic bacteria and fungal spores cannot be seen by phone cameras directly. Instead, this system runs real-time edge computer vision to detect visual proxies of microbial colonization:",
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                        Text(
                            text = "• Bacterial Soft Rot Proxy: Pectolytic maceration causing water-soaked, sunken, dark translucent lesions.",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "• Fungal Spore Proxy: Desaturated low-contrast mycelium patches (Botrytis, Penicillium mold).",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "• Surface Integrity: Laplacian edge variance detects skin micro-wrinkles and loss of cellular turgidity.",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "All inference runs 100% on-device with zero server latency.",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showInfoDialog = false }) {
                        Text("Got it")
                    }
                }
            )
        }
    }
}
