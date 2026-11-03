package com.example.data.model

enum class StepStatus {
    ON_TRACK,
    NEEDS_CORRECTION,
    STEP_COMPLETE,
    PAPER_NOT_FOUND
}

data class EvaluationResult(
    val status: StepStatus,
    val accuracyScore: Int,
    val encouragement: String,
    val feedback: String,
    val feedbackHinglish: String,
    val correctionGuide: String,
    val detectedStrokes: String,
    val canProceed: Boolean
)
