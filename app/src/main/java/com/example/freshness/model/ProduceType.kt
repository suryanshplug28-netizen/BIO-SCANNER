package com.example.freshness.model

enum class ProduceType(
    val id: String,
    val displayName: String,
    val category: String,
    val emoji: String,
    val baselineShelfLifeDaysRoomTemp: Float,
    val refrigerationMultiplier: Float,
    val expectedHueCenter: Float, // 0..360
    val expectedHueTolerance: Float,
    val expectedMinSaturation: Float, // 0..1
    val expectedMaxBrightness: Float,
    val expectedMinBrightness: Float,
    val typicalRoughnessThreshold: Float,
    val keySpoilageOrganisms: String,
    val healthyFloraDescription: String,
    val preservationTips: String
) {
    BANANA(
        id = "banana",
        displayName = "Banana",
        category = "Fruit",
        emoji = "🍌",
        baselineShelfLifeDaysRoomTemp = 6.0f,
        refrigerationMultiplier = 1.3f, // Bananas chill-injure in fridge, skin darkens
        expectedHueCenter = 55.0f, // Yellow
        expectedHueTolerance = 25.0f,
        expectedMinSaturation = 0.45f,
        expectedMaxBrightness = 0.95f,
        expectedMinBrightness = 0.35f,
        typicalRoughnessThreshold = 0.18f,
        keySpoilageOrganisms = "Colletotrichum musae (Anthracnose), Fusarium oxysporum",
        healthyFloraDescription = "Endophytic Bacillus spp., epiphytic yeast flora",
        preservationTips = "Wrap stem with wax/foil to slow ethylene gas emission. Keep at 13-16°C."
    ),
    TOMATO(
        id = "tomato",
        displayName = "Tomato",
        category = "Vegetable",
        emoji = "🍅",
        baselineShelfLifeDaysRoomTemp = 7.0f,
        refrigerationMultiplier = 1.8f,
        expectedHueCenter = 8.0f, // Red
        expectedHueTolerance = 22.0f,
        expectedMinSaturation = 0.50f,
        expectedMaxBrightness = 0.88f,
        expectedMinBrightness = 0.25f,
        typicalRoughnessThreshold = 0.12f,
        keySpoilageOrganisms = "Erwinia carotovora (Bacterial Soft Rot), Alternaria alternata",
        healthyFloraDescription = "Pseudomonas fluorescens (benign), lactic acid bacteria",
        preservationTips = "Store stem-side down at room temperature away from direct sunlight."
    ),
    APPLE(
        id = "apple",
        displayName = "Apple",
        category = "Fruit",
        emoji = "🍎",
        baselineShelfLifeDaysRoomTemp = 14.0f,
        refrigerationMultiplier = 3.5f,
        expectedHueCenter = 4.0f, // Vibrant Red or 110 for green
        expectedHueTolerance = 30.0f,
        expectedMinSaturation = 0.40f,
        expectedMaxBrightness = 0.92f,
        expectedMinBrightness = 0.30f,
        typicalRoughnessThreshold = 0.10f,
        keySpoilageOrganisms = "Penicillium expansum (Blue Mold), Botrytis cinerea",
        healthyFloraDescription = "Cryptococcus yeasts, Aureobasidium pullulans (cuticle protector)",
        preservationTips = "Keep in crisper drawer with high humidity; separate bruised items."
    ),
    AVOCADO(
        id = "avocado",
        displayName = "Avocado",
        category = "Fruit",
        emoji = "🥑",
        baselineShelfLifeDaysRoomTemp = 4.5f,
        refrigerationMultiplier = 2.4f,
        expectedHueCenter = 85.0f, // Olive green to dark
        expectedHueTolerance = 35.0f,
        expectedMinSaturation = 0.25f,
        expectedMaxBrightness = 0.65f,
        expectedMinBrightness = 0.15f,
        typicalRoughnessThreshold = 0.35f,
        keySpoilageOrganisms = "Dothiorella gregaria, Colletotrichum gloeosporioides",
        healthyFloraDescription = "Protective epiphytic bacteria, harmless actinomycetes",
        preservationTips = "Once ripe, refrigerate immediately or submerge half-cut in lemon water."
    ),
    STRAWBERRY(
        id = "strawberry",
        displayName = "Strawberry",
        category = "Fruit",
        emoji = "🍓",
        baselineShelfLifeDaysRoomTemp = 3.0f,
        refrigerationMultiplier = 2.8f,
        expectedHueCenter = 355.0f, // Deep crimson red
        expectedHueTolerance = 18.0f,
        expectedMinSaturation = 0.60f,
        expectedMaxBrightness = 0.85f,
        expectedMinBrightness = 0.28f,
        typicalRoughnessThreshold = 0.28f,
        keySpoilageOrganisms = "Botrytis cinerea (Grey Mold), Rhizopus stolonifer",
        healthyFloraDescription = "Rhodotorula yeasts, plant commensal Microbacterium",
        preservationTips = "Do not wash until ready to consume; keep moisture strictly below 85%."
    ),
    LEAFY_GREENS(
        id = "leafy_greens",
        displayName = "Leafy Greens",
        category = "Vegetable",
        emoji = "🥬",
        baselineShelfLifeDaysRoomTemp = 3.5f,
        refrigerationMultiplier = 3.0f,
        expectedHueCenter = 120.0f, // Bright Chlorophyll Green
        expectedHueTolerance = 30.0f,
        expectedMinSaturation = 0.45f,
        expectedMaxBrightness = 0.80f,
        expectedMinBrightness = 0.20f,
        typicalRoughnessThreshold = 0.22f,
        keySpoilageOrganisms = "Pseudomonas marginalis, Erwinia carotovora (Bacterial Soft Rot)",
        healthyFloraDescription = "Pantoea agglomerans, beneficial phyllosphere bacilli",
        preservationTips = "Line container with dry paper towel to absorb condensation and prevent rotting."
    ),
    BELL_PEPPER(
        id = "bell_pepper",
        displayName = "Bell Pepper",
        category = "Vegetable",
        emoji = "🫑",
        baselineShelfLifeDaysRoomTemp = 7.5f,
        refrigerationMultiplier = 2.5f,
        expectedHueCenter = 115.0f, // Green/Red
        expectedHueTolerance = 45.0f,
        expectedMinSaturation = 0.50f,
        expectedMaxBrightness = 0.85f,
        expectedMinBrightness = 0.25f,
        typicalRoughnessThreshold = 0.12f,
        keySpoilageOrganisms = "Botrytis cinerea, Xanthomonas campestris",
        healthyFloraDescription = "Harmless epiphytic Methylobacterium, cuticle yeasts",
        preservationTips = "Keep dry in produce bag; ensure stem remains intact and firm."
    ),
    ORANGE(
        id = "orange",
        displayName = "Citrus / Orange",
        category = "Fruit",
        emoji = "🍊",
        baselineShelfLifeDaysRoomTemp = 10.0f,
        refrigerationMultiplier = 2.8f,
        expectedHueCenter = 32.0f, // Vibrant Orange
        expectedHueTolerance = 18.0f,
        expectedMinSaturation = 0.65f,
        expectedMaxBrightness = 0.90f,
        expectedMinBrightness = 0.35f,
        typicalRoughnessThreshold = 0.25f,
        keySpoilageOrganisms = "Penicillium digitatum (Green Mold), Penicillium italicum (Blue Mold)",
        healthyFloraDescription = "Lactic acid flora, essential citrus peel oil antimicrobials",
        preservationTips = "Ensure ventilation; citrus peel contains natural limonene preservation."
    );

    companion object {
        fun default(): ProduceType = BANANA
        fun findById(id: String): ProduceType = entries.find { it.id == id } ?: default()
    }
}
