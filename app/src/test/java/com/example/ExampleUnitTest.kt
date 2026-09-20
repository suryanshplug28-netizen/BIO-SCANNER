package com.example

import com.example.freshness.cv.FreshnessComputerVisionEngine
import com.example.freshness.model.ProduceType
import com.example.freshness.sample.SampleProduceDataProvider
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class ExampleUnitTest {

  @Test
  fun testFreshProduceInference() {
    val scenario = SampleProduceDataProvider.scenarios.first { it.id == "banana_fresh" }
    val bitmap = SampleProduceDataProvider.generateScenarioBitmap(scenario)

    val result = FreshnessComputerVisionEngine.analyzeProduceBitmap(bitmap, ProduceType.BANANA)

    assertNotNull(result)
    println("DEBUG testFreshProduceInference score: ${result.freshnessPercentage}, healthy: ${result.healthyFloraRatio}, bad: ${result.spoilageOrganismsRatio}")
    assertTrue("Freshness score should be positive: ${result.freshnessPercentage}", result.freshnessPercentage > 40f)
    assertTrue("Healthy flora should exceed 50%: ${result.healthyFloraRatio}", result.healthyFloraRatio > 50f)
  }

  @Test
  fun testSpoiledProduceProxyDetection() {
    val scenario = SampleProduceDataProvider.scenarios.first { it.id == "banana_senescent" }
    val bitmap = SampleProduceDataProvider.generateScenarioBitmap(scenario)

    val result = FreshnessComputerVisionEngine.analyzeProduceBitmap(bitmap, ProduceType.BANANA)

    assertNotNull(result)
    println("DEBUG testSpoiledProduceProxyDetection score: ${result.freshnessPercentage}, spoilage: ${result.spoilageOrganismsRatio}, spots: ${result.detectedHotspots.size}")
    assertTrue("Spoilage organisms ratio should be significant: ${result.spoilageOrganismsRatio}", result.spoilageOrganismsRatio > 25f)
  }

  @Test
  fun testShelfLifeDecayFunction() {
    val freshScenario = SampleProduceDataProvider.scenarios.first { it.id == "apple_crisp" }
    val freshBmp = SampleProduceDataProvider.generateScenarioBitmap(freshScenario)
    val freshResult = FreshnessComputerVisionEngine.analyzeProduceBitmap(freshBmp, ProduceType.APPLE)

    val moldyScenario = SampleProduceDataProvider.scenarios.first { it.id == "apple_blue_mold" }
    val moldyBmp = SampleProduceDataProvider.generateScenarioBitmap(moldyScenario)
    val moldyResult = FreshnessComputerVisionEngine.analyzeProduceBitmap(moldyBmp, ProduceType.APPLE)

    println("DEBUG apple fresh: ${freshResult.daysRemainingRoomTemp}, moldy: ${moldyResult.daysRemainingRoomTemp}")
    assertTrue("Refrigeration increases shelf life",
      freshResult.daysRemainingRefrigerated >= freshResult.daysRemainingRoomTemp)
  }
}
