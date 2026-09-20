package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.freshness.model.FreshnessResult
import com.example.freshness.model.FreshnessVerdict
import com.example.freshness.model.ProduceType
import com.example.freshness.ui.components.BioFloraCard
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    val sampleResult = FreshnessResult(
      produceType = ProduceType.BANANA,
      freshnessPercentage = 94f,
      healthyFloraRatio = 92f,
      spoilageOrganismsRatio = 8f,
      daysRemainingRoomTemp = 5.2f,
      daysRemainingRefrigerated = 6.8f,
      textureScore = 92f,
      colorIntegrityScore = 95f,
      skinIntegrityScore = 94f,
      spoilageMarkers = emptyList(),
      detectedHotspots = emptyList(),
      verdict = FreshnessVerdict.OPTIMAL,
      analysisLatencyMs = 14
    )

    composeTestRule.setContent {
      MyApplicationTheme {
        BioFloraCard(result = sampleResult)
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}
