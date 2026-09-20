package com.example.freshness.model

enum class FreshnessVerdict(val label: String, val code: String) {
    OPTIMAL("Peak Freshness", "OPTIMAL"),
    GOOD("Good & Safe", "GOOD"),
    CONSUME_SOON("Consume Soon", "WARNING"),
    CRITICAL_SPOILAGE("Spoiled / Rotten", "DANGER");

    companion object {
        fun fromScore(score: Float): FreshnessVerdict = when {
            score >= 80f -> OPTIMAL
            score >= 60f -> GOOD
            score >= 38f -> CONSUME_SOON
            else -> CRITICAL_SPOILAGE
        }
    }
}

enum class SpoilageType(val displayName: String, val badgeColorHex: Long) {
    HEALTHY_TISSUE("Intact Epicuticle", 0xFF10B981),
    ENZYMATIC_BROWNING("Enzymatic Browning", 0xFFF59E0B),
    OXIDATIVE_BRUISING("Mechanical Bruise", 0xFFE11D48),
    BACTERIAL_ROT("Bacterial Soft Rot", 0xFFDC2626),
    FUNGAL_MOLD("Fungal Spore / Mold", 0xFF9333EA)
}

data class DegradationHotspot(
    val id: String,
    val normalizedX: Float, // 0..1
    val normalizedY: Float, // 0..1
    val normalizedWidth: Float,
    val normalizedHeight: Float,
    val severity: Float, // 0..1
    val spoilageType: SpoilageType,
    val label: String,
    val description: String
)

data class SpoilageMarker(
    val name: String,
    val severityScore: Float, // 0..1
    val detected: Boolean,
    val details: String
)

data class FreshnessResult(
    val produceType: ProduceType,
    val freshnessPercentage: Float, // 0..100
    val healthyFloraRatio: Float,   // 0..100
    val spoilageOrganismsRatio: Float, // 0..100
    val daysRemainingRoomTemp: Float,
    val daysRemainingRefrigerated: Float,
    val textureScore: Float, // 0..100
    val colorIntegrityScore: Float, // 0..100
    val skinIntegrityScore: Float, // 0..100
    val spoilageMarkers: List<SpoilageMarker>,
    val detectedHotspots: List<DegradationHotspot>,
    val verdict: FreshnessVerdict,
    val analysisLatencyMs: Long,
    val inferenceEngine: String = "Edge Lightweight Vision Engine (TFLite/CV)",
    val timestamp: Long = System.currentTimeMillis()
)

data class ScanHistoryItem(
    val id: String,
    val produceType: ProduceType,
    val freshnessPercentage: Float,
    val daysRemainingRoomTemp: Float,
    val verdict: FreshnessVerdict,
    val timestamp: Long
)
