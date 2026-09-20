package com.example.freshness.ui.components

import android.Manifest
import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.freshness.model.DegradationHotspot
import com.example.freshness.model.FreshnessResult
import com.example.freshness.sample.SampleProduceDataProvider
import com.example.freshness.sample.SampleScenario
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.util.concurrent.Executors

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraViewFinder(
    result: FreshnessResult?,
    isLiveCamera: Boolean,
    showHeatmap: Boolean,
    isFrozen: Boolean,
    selectedScenario: SampleScenario?,
    selectedHotspot: DegradationHotspot?,
    onFrameCaptured: (Bitmap) -> Unit,
    onHotspotClicked: (DegradationHotspot) -> Unit,
    onToggleFreeze: () -> Unit,
    onToggleHeatmap: () -> Unit,
    onToggleCameraMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Sample bitmap for fallback / scenario mode
    val sampleBitmap = remember(selectedScenario) {
        val sc = selectedScenario ?: SampleProduceDataProvider.scenarios.first()
        SampleProduceDataProvider.generateScenarioBitmap(sc, 400, 400)
    }

    // Trigger frame capture on scenario change
    LaunchedEffect(sampleBitmap, isLiveCamera) {
        if (!isLiveCamera || !cameraPermissionState.status.isGranted) {
            onFrameCaptured(sampleBitmap)
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .background(Color(0xFF0A0E14))
            .testTag("camera_view_finder")
    ) {
        if (isLiveCamera && cameraPermissionState.status.isGranted) {
            // Live CameraX Feed
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    val previewView = PreviewView(ctx).apply {
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                    }

                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                    val cameraExecutor = Executors.newSingleThreadExecutor()

                    cameraProviderFuture.addListener({
                        try {
                            val cameraProvider = cameraProviderFuture.get()

                            val preview = Preview.Builder().build().also {
                                it.surfaceProvider = previewView.surfaceProvider
                            }

                            val imageAnalysis = ImageAnalysis.Builder()
                                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                                .build()

                            imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                                val bitmap = imageProxy.toBitmap()
                                onFrameCaptured(bitmap)
                                imageProxy.close()
                            }

                            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview,
                                imageAnalysis
                            )
                        } catch (exc: Exception) {
                            Log.e("CameraViewFinder", "Camera initialization failed", exc)
                        }
                    }, ContextCompat.getMainExecutor(ctx))

                    previewView
                }
            )
        } else if (isLiveCamera && !cameraPermissionState.status.isGranted) {
            // Permission Request State
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Filled.CameraAlt,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Camera Permission Required",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Grant camera permission to enable real-time fruit & vegetable freshness analysis.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF94A3B8),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { cameraPermissionState.launchPermissionRequest() },
                    modifier = Modifier.testTag("request_camera_permission_button")
                ) {
                    Text("Grant Permission")
                }
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(
                    onClick = onToggleCameraMode,
                    modifier = Modifier.testTag("switch_to_sample_button")
                ) {
                    Text("Use Interactive Sample Scan Mode", color = Color(0xFF38BDF8), fontSize = 12.sp)
                }
            }
        } else {
            // Interactive Sample Stream View (Works directly in browser emulator without camera)
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Image(
                    bitmap = sampleBitmap.asImageBitmap(),
                    contentDescription = "Simulated Produce Stream",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Real-Time Computer Vision Overlay HUD (Reticle, Heatmaps, Hotspots)
        CameraOverlayHud(
            result = result,
            showHeatmap = showHeatmap,
            isFrozen = isFrozen,
            isLiveCamera = isLiveCamera && cameraPermissionState.status.isGranted,
            selectedHotspot = selectedHotspot,
            onHotspotClicked = onHotspotClicked,
            onToggleFreeze = onToggleFreeze,
            onToggleHeatmap = onToggleHeatmap,
            onToggleCameraMode = onToggleCameraMode
        )
    }
}
