package com.example.freshness.sample

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import com.example.freshness.model.ProduceType
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

data class SampleScenario(
    val id: String,
    val produceType: ProduceType,
    val stageName: String,
    val stageDescription: String,
    val expectedFreshnessTier: String,
    val simulatedBrowningLevel: Float, // 0..1
    val simulatedMoldLevel: Float,     // 0..1
    val simulatedWrinkleLevel: Float   // 0..1
)

object SampleProduceDataProvider {

    val scenarios = listOf(
        SampleScenario(
            id = "banana_fresh",
            produceType = ProduceType.BANANA,
            stageName = "Pristine Ripe",
            stageDescription = "Bright yellow peel, intact stem, optimal turgor pressure",
            expectedFreshnessTier = "95% Fresh",
            simulatedBrowningLevel = 0.02f,
            simulatedMoldLevel = 0.0f,
            simulatedWrinkleLevel = 0.05f
        ),
        SampleScenario(
            id = "banana_spotted",
            produceType = ProduceType.BANANA,
            stageName = "Sugar Spotted",
            stageDescription = "Enzymatic melanin spots appearing across peel surface",
            expectedFreshnessTier = "65% Ripe",
            simulatedBrowningLevel = 0.32f,
            simulatedMoldLevel = 0.01f,
            simulatedWrinkleLevel = 0.20f
        ),
        SampleScenario(
            id = "banana_senescent",
            produceType = ProduceType.BANANA,
            stageName = "Overripe / Bruised",
            stageDescription = "Heavy browning, skin softening, high bacterial proxy index",
            expectedFreshnessTier = "28% Critical",
            simulatedBrowningLevel = 0.75f,
            simulatedMoldLevel = 0.18f,
            simulatedWrinkleLevel = 0.65f
        ),
        SampleScenario(
            id = "tomato_fresh",
            produceType = ProduceType.TOMATO,
            stageName = "Vine Crisp Fresh",
            stageDescription = "Glossy red epidermis, firm cuticle, high vitamin C retention",
            expectedFreshnessTier = "94% Fresh",
            simulatedBrowningLevel = 0.01f,
            simulatedMoldLevel = 0.0f,
            simulatedWrinkleLevel = 0.03f
        ),
        SampleScenario(
            id = "tomato_soft_rot",
            produceType = ProduceType.TOMATO,
            stageName = "Soft Rot Lesions",
            stageDescription = "Water-soaked necrotic patches, bacterial maceration proxy",
            expectedFreshnessTier = "34% Spoiled",
            simulatedBrowningLevel = 0.55f,
            simulatedMoldLevel = 0.22f,
            simulatedWrinkleLevel = 0.70f
        ),
        SampleScenario(
            id = "apple_crisp",
            produceType = ProduceType.APPLE,
            stageName = "Crisp Harvest",
            stageDescription = "Wax cuticle intact, firm cellular matrix, zero bruising",
            expectedFreshnessTier = "96% Peak",
            simulatedBrowningLevel = 0.01f,
            simulatedMoldLevel = 0.0f,
            simulatedWrinkleLevel = 0.02f
        ),
        SampleScenario(
            id = "apple_blue_mold",
            produceType = ProduceType.APPLE,
            stageName = "Penicillium Mold",
            stageDescription = "Localized grey-green fungal spore cluster with sunken bruise",
            expectedFreshnessTier = "22% Danger",
            simulatedBrowningLevel = 0.45f,
            simulatedMoldLevel = 0.60f,
            simulatedWrinkleLevel = 0.50f
        ),
        SampleScenario(
            id = "avocado_optimal",
            produceType = ProduceType.AVOCADO,
            stageName = "Ready to Eat",
            stageDescription = "Rich dark olive skin, ideal firmness, healthy lipid balance",
            expectedFreshnessTier = "88% Prime",
            simulatedBrowningLevel = 0.08f,
            simulatedMoldLevel = 0.0f,
            simulatedWrinkleLevel = 0.15f
        ),
        SampleScenario(
            id = "strawberry_mold",
            produceType = ProduceType.STRAWBERRY,
            stageName = "Botrytis Rot",
            stageDescription = "Fluffy grey mold mycelium spreading across drupelets",
            expectedFreshnessTier = "18% Expired",
            simulatedBrowningLevel = 0.40f,
            simulatedMoldLevel = 0.70f,
            simulatedWrinkleLevel = 0.80f
        ),
        SampleScenario(
            id = "greens_wilted",
            produceType = ProduceType.LEAFY_GREENS,
            stageName = "Wilted / Yellowing",
            stageDescription = "Chlorophyll breakdown, leaf margin rot, high microbial decay",
            expectedFreshnessTier = "30% Warning",
            simulatedBrowningLevel = 0.50f,
            simulatedMoldLevel = 0.15f,
            simulatedWrinkleLevel = 0.85f
        )
    )

    /**
     * Synthesizes a realistic visual bitmap corresponding to the produce scenario
     * with accurate color distribution, browning lesions, and mold proxies.
     * This provides a true pixel input for the Computer Vision Engine.
     */
    fun generateScenarioBitmap(scenario: SampleScenario, width: Int = 300, height: Int = 300): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Dark background representing inspection chamber / dark surface
        canvas.drawColor(Color.rgb(18, 24, 30))

        val centerX = width / 2f
        val centerY = height / 2f
        val baseRadius = minOf(width, height) * 0.38f

        // Base Produce Color
        val (baseR, baseG, baseB) = when (scenario.produceType) {
            ProduceType.BANANA -> Triple(245, 215, 45)
            ProduceType.TOMATO -> Triple(225, 40, 35)
            ProduceType.APPLE -> Triple(210, 35, 45)
            ProduceType.AVOCADO -> Triple(60, 75, 45)
            ProduceType.STRAWBERRY -> Triple(220, 25, 40)
            ProduceType.LEAFY_GREENS -> Triple(45, 175, 65)
            ProduceType.BELL_PEPPER -> Triple(50, 185, 70)
            ProduceType.ORANGE -> Triple(250, 140, 25)
        }

        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(
                centerX - baseRadius * 0.3f,
                centerY - baseRadius * 0.3f,
                baseRadius * 1.3f,
                Color.rgb(minOf(255, (baseR * 1.15).toInt()), minOf(255, (baseG * 1.15).toInt()), minOf(255, (baseB * 1.15).toInt())),
                Color.rgb((baseR * 0.75).toInt(), (baseG * 0.75).toInt(), (baseB * 0.75).toInt()),
                Shader.TileMode.CLAMP
            )
        }

        // Draw main produce body
        if (scenario.produceType == ProduceType.BANANA) {
            canvas.save()
            canvas.rotate(-25f, centerX, centerY)
            canvas.drawRoundRect(centerX - baseRadius * 1.2f, centerY - baseRadius * 0.45f,
                centerX + baseRadius * 1.2f, centerY + baseRadius * 0.45f,
                baseRadius * 0.35f, baseRadius * 0.35f, paint)
            canvas.restore()
        } else {
            canvas.drawCircle(centerX, centerY, baseRadius, paint)
        }

        // Draw Browning / Rot spots if present
        if (scenario.simulatedBrowningLevel > 0.05f) {
            val spotPaint = Paint(Paint.ANTI_ALIAS_FLAG)
            val spotCount = (scenario.simulatedBrowningLevel * 18).toInt()
            val rnd = Random(scenario.id.hashCode())

            for (i in 0 until spotCount) {
                val angle = rnd.nextFloat() * 2f * Math.PI.toFloat()
                val dist = rnd.nextFloat() * (baseRadius * 0.75f)
                val sx = centerX + cos(angle) * dist
                val sy = centerY + sin(angle) * dist
                val sRadius = (rnd.nextFloat() * 16f + 8f) * (0.8f + scenario.simulatedBrowningLevel)

                spotPaint.color = Color.argb(
                    (180 + (scenario.simulatedBrowningLevel * 75)).toInt().coerceAtMost(255),
                    65, 38, 18 // Dark melanin rot
                )
                canvas.drawCircle(sx, sy, sRadius, spotPaint)
            }
        }

        // Draw Mold / Fungal proxy clusters if present
        if (scenario.simulatedMoldLevel > 0.04f) {
            val moldPaint = Paint(Paint.ANTI_ALIAS_FLAG)
            val moldSpots = (scenario.simulatedMoldLevel * 12).toInt()
            val rnd = Random(scenario.id.hashCode() + 99)

            for (i in 0 until moldSpots) {
                val mx = centerX + (rnd.nextFloat() - 0.5f) * baseRadius * 1.1f
                val my = centerY + (rnd.nextFloat() - 0.5f) * baseRadius * 1.1f
                val mRadius = (rnd.nextFloat() * 20f + 10f)

                // Fuzzy grey/white/blue-green spore colony
                moldPaint.color = Color.argb(210, 195, 205, 200)
                canvas.drawCircle(mx, my, mRadius, moldPaint)

                moldPaint.color = Color.argb(160, 110, 150, 140)
                canvas.drawCircle(mx, my, mRadius * 0.6f, moldPaint)
            }
        }

        return bitmap
    }
}
