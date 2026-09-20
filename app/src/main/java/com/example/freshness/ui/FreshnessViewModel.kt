package com.example.freshness.ui

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.freshness.cv.FreshnessComputerVisionEngine
import com.example.freshness.model.*
import com.example.freshness.sample.SampleProduceDataProvider
import com.example.freshness.sample.SampleScenario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class FreshnessUiState(
    val selectedProduceType: ProduceType = ProduceType.BANANA,
    val currentResult: FreshnessResult? = null,
    val isAnalyzing: Boolean = false,
    val isLiveCameraActive: Boolean = true,
    val selectedScenario: SampleScenario? = null,
    val isFrozen: Boolean = false,
    val showHeatmapOverlay: Boolean = true,
    val isRefrigeratedMode: Boolean = false,
    val selectedHotspot: DegradationHotspot? = null,
    val scanHistory: List<ScanHistoryItem> = emptyList(),
    val torchEnabled: Boolean = false
)

class FreshnessViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(FreshnessUiState())
    val uiState: StateFlow<FreshnessUiState> = _uiState.asStateFlow()

    private var isProcessingFrame = false
    private var lastFrameProcessTime = 0L

    init {
        // Run initial baseline scan with sample pristine banana scenario
        val defaultScenario = SampleProduceDataProvider.scenarios.first()
        loadScenario(defaultScenario)
    }

    fun selectProduceType(produceType: ProduceType) {
        _uiState.update { it.copy(selectedProduceType = produceType) }
        // If in sample mode, pick matching scenario or re-analyze current bitmap
        val matchingScenario = SampleProduceDataProvider.scenarios.firstOrNull { it.produceType == produceType }
        if (matchingScenario != null && !_uiState.value.isLiveCameraActive) {
            loadScenario(matchingScenario)
        } else {
            // Re-evaluate current result with new produce baseline if available
            val currentRes = _uiState.value.currentResult
            if (currentRes != null) {
                viewModelScope.launch(Dispatchers.Default) {
                    val scenario = _uiState.value.selectedScenario ?: SampleProduceDataProvider.scenarios.first()
                    val bmp = SampleProduceDataProvider.generateScenarioBitmap(scenario)
                    val newResult = FreshnessComputerVisionEngine.analyzeProduceBitmap(bmp, produceType)
                    _uiState.update { it.copy(currentResult = newResult) }
                }
            }
        }
    }

    fun processLiveCameraFrame(bitmap: Bitmap) {
        if (_uiState.value.isFrozen) return
        if (isProcessingFrame) return

        val now = System.currentTimeMillis()
        // Throttle inference to ~15-20 FPS for battery and thermal optimization
        if (now - lastFrameProcessTime < 55) return

        isProcessingFrame = true
        lastFrameProcessTime = now

        viewModelScope.launch(Dispatchers.Default) {
            try {
                val produceType = _uiState.value.selectedProduceType
                val result = FreshnessComputerVisionEngine.analyzeProduceBitmap(bitmap, produceType)
                _uiState.update {
                    it.copy(
                        currentResult = result,
                        isAnalyzing = false
                    )
                }
            } finally {
                isProcessingFrame = false
            }
        }
    }

    fun loadScenario(scenario: SampleScenario) {
        _uiState.update {
            it.copy(
                selectedScenario = scenario,
                selectedProduceType = scenario.produceType,
                isAnalyzing = true
            )
        }
        viewModelScope.launch(Dispatchers.Default) {
            val bmp = SampleProduceDataProvider.generateScenarioBitmap(scenario)
            val result = FreshnessComputerVisionEngine.analyzeProduceBitmap(bmp, scenario.produceType)
            _uiState.update {
                it.copy(
                    currentResult = result,
                    isAnalyzing = false
                )
            }
        }
    }

    fun toggleLiveCamera(active: Boolean) {
        _uiState.update { it.copy(isLiveCameraActive = active) }
        if (!active && _uiState.value.selectedScenario == null) {
            val matching = SampleProduceDataProvider.scenarios.firstOrNull { it.produceType == _uiState.value.selectedProduceType }
                ?: SampleProduceDataProvider.scenarios.first()
            loadScenario(matching)
        }
    }

    fun toggleFreeze() {
        _uiState.update { it.copy(isFrozen = !it.isFrozen) }
    }

    fun toggleHeatmap() {
        _uiState.update { it.copy(showHeatmapOverlay = !it.showHeatmapOverlay) }
    }

    fun toggleRefrigeration() {
        _uiState.update { it.copy(isRefrigeratedMode = !it.isRefrigeratedMode) }
    }

    fun toggleTorch() {
        _uiState.update { it.copy(torchEnabled = !it.torchEnabled) }
    }

    fun selectHotspot(hotspot: DegradationHotspot?) {
        _uiState.update { it.copy(selectedHotspot = hotspot) }
    }

    fun saveCurrentScanToHistory() {
        val current = _uiState.value.currentResult ?: return
        val item = ScanHistoryItem(
            id = UUID.randomUUID().toString(),
            produceType = current.produceType,
            freshnessPercentage = current.freshnessPercentage,
            daysRemainingRoomTemp = current.daysRemainingRoomTemp,
            verdict = current.verdict,
            timestamp = System.currentTimeMillis()
        )
        _uiState.update {
            it.copy(scanHistory = listOf(item) + it.scanHistory.take(19))
        }
    }
}
