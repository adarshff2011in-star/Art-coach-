package com.example.data.model

enum class StepPhase(val label: String) {
    GEOMETRIC_BASES("Phase 1: Geometric Base"),
    SECONDARY_GUIDES("Phase 2: Secondary Lines"),
    PRIMARY_OUTLINES("Phase 3: Primary Outline"),
    DEFINING_DETAILS("Phase 4: Defining Details"),
    SHADING_AND_POLISH("Phase 5: Shading & Polish")
}

data class DrawingStep(
    val stepIndex: Int,
    val title: String,
    val phase: StepPhase,
    val instruction: String,
    val spokenCoachingEn: String,
    val spokenCoachingHinglish: String,
    val tips: List<String>,
    val strokeDiagramDesc: String,
    val targetGeometricShapes: List<String>
)

data class DrawingSubject(
    val id: String,
    val title: String,
    val category: String,
    val difficulty: String,
    val estimatedMinutes: Int,
    val drawableResId: Int? = null,
    val customImageUri: String? = null,
    val description: String,
    val steps: List<DrawingStep>
)
