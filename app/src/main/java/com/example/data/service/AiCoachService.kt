package com.example.data.service

import android.graphics.Bitmap
import android.util.Log
import com.example.data.model.DrawingStep
import com.example.data.model.DrawingSubject
import com.example.data.model.EvaluationResult
import com.example.data.model.StepPhase
import com.example.data.model.StepStatus
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.json.JSONObject

private const val TAG = "AiCoachService"

class AiCoachService {

    private val evaluationSchema = Schema.obj(
        properties = mapOf(
            "status" to Schema.enumeration(
                values = listOf("ON_TRACK", "NEEDS_CORRECTION", "STEP_COMPLETE", "PAPER_NOT_FOUND"),
                description = "Current status of the user's drawing relative to the expected step"
            ),
            "accuracyScore" to Schema.integer(description = "Assessment score between 0 and 100"),
            "encouragement" to Schema.string(description = "Warm, uplifting positive reinforcement sentence in English"),
            "feedback" to Schema.string(description = "Actionable, constructive feedback explaining what strokes are good and what needs adjusting"),
            "feedbackHinglish" to Schema.string(description = "Friendly encouraging coaching spoken in clear natural Hinglish"),
            "correctionGuide" to Schema.string(description = "Specific geometric correction instructions or next stroke advice"),
            "detectedStrokes" to Schema.string(description = "Brief summary of what lines and geometric shapes were observed on the user's paper"),
            "canProceed" to Schema.boolean(description = "True if the drawing meets the minimum criteria to advance to the next step")
        )
    )

    private val lessonSchema = Schema.obj(
        properties = mapOf(
            "title" to Schema.string(description = "Title of the drawing lesson"),
            "category" to Schema.string(description = "Category such as Animals, Anime, Still Life, Architecture"),
            "difficulty" to Schema.enumeration(values = listOf("Beginner", "Intermediate", "Advanced")),
            "estimatedMinutes" to Schema.integer(description = "Estimated practice minutes"),
            "description" to Schema.string(description = "Short introduction to the lesson"),
            "steps" to Schema.array(
                items = Schema.obj(
                    properties = mapOf(
                        "stepIndex" to Schema.integer(description = "Step number 1 to 5"),
                        "title" to Schema.string(description = "Short step title e.g. Base Geometric Shapes"),
                        "phase" to Schema.enumeration(
                            values = listOf(
                                "GEOMETRIC_BASES",
                                "SECONDARY_GUIDES",
                                "PRIMARY_OUTLINES",
                                "DEFINING_DETAILS",
                                "SHADING_AND_POLISH"
                            )
                        ),
                        "instruction" to Schema.string(description = "Clear written instruction for drawing this step"),
                        "spokenCoachingEn" to Schema.string(description = "Enthusiastic verbal audio coaching in English"),
                        "spokenCoachingHinglish" to Schema.string(description = "Enthusiastic verbal audio coaching in friendly Hinglish"),
                        "tips" to Schema.array(items = Schema.string(), description = "Pro artist tips"),
                        "strokeDiagramDesc" to Schema.string(description = "Visual description of the stroke to render"),
                        "targetGeometricShapes" to Schema.array(items = Schema.string(), description = "Target shapes like circle, oval, line")
                    )
                ),
                description = "Exactly 5 progressive drawing steps starting with basic geometric shapes"
            )
        )
    )

    private val evaluationModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = evaluationSchema
            }
        )
    }

    private val lessonModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = lessonSchema
            }
        )
    }

    suspend fun evaluateDrawing(
        drawingBitmap: Bitmap,
        subjectTitle: String,
        currentStep: DrawingStep,
        totalSteps: Int
    ): EvaluationResult = withContext(Dispatchers.IO) {
        val scaledBitmap = scaleBitmapToMaxDimension(drawingBitmap, 1024)

        val prompt = """
            You are an expert, patient, and encouraging AI Art Instructor and Drawing Coach.
            The user is currently pointing their device camera at their drawing sheet/sketchbook.
            
            Lesson: "$subjectTitle"
            Current Step: Step ${currentStep.stepIndex} of $totalSteps - "${currentStep.title}"
            Phase: ${currentStep.phase.label}
            Step Instruction: "${currentStep.instruction}"
            Expected Geometric Targets: ${currentStep.targetGeometricShapes.joinToString(", ")}
            
            YOUR TASK:
            1. Analyze the live image of the user's paper sheet.
            2. Check if a drawing paper / sketchbook page is visible. If not visible or completely blank/dark, set status to PAPER_NOT_FOUND.
            3. Evaluate the user's strokes against the expected step:
               - Are the proportions aligned?
               - Is the circle/shape too big, too small, or tilted?
               - Are the lines too stiff or out of position?
            4. Provide:
               - status: ON_TRACK (good progress, minor tweaks), NEEDS_CORRECTION (proportions or stroke needs gentle adjustment before moving on), STEP_COMPLETE (step mastered and ready to advance), or PAPER_NOT_FOUND.
               - accuracyScore: integer 0 to 100 based on proportion accuracy and stroke confidence.
               - encouragement: Warm, friendly, supportive compliment (e.g., "Wonderful light pencil strokes!", "Great attempt at placing the head circle!").
               - feedback: Clear, precise feedback on what is drawn well and what needs adjusting.
               - feedbackHinglish: Clear, natural, encouraging coaching in Hinglish (e.g., "Aapka circle thoda bada ho gaya hai, ise upar se thoda sa chhota karein", "Boht badhiya! Lines boht clean hain.").
               - correctionGuide: Specific action to fix the stroke (e.g., "Gently erase the left curve of the oval and bring it 1cm inward", "Darken the center line so it's easier to align").
               - detectedStrokes: Short description of the lines/shapes you actually see on paper.
               - canProceed: true if score >= 65 or if user is ready for next step.
            
            Be gentle, positive, and motivating like a world-class art teacher!
        """.trimIndent()

        try {
            val response = withTimeout(60_000L) {
                evaluationModel.generateContent(
                    content {
                        text(prompt)
                        image(scaledBitmap)
                    }
                )
            }

            val rawJson = response.text ?: throw IllegalStateException("Model returned empty text.")
            Log.d(TAG, "Evaluation response: $rawJson")

            val json = JSONObject(rawJson)
            val statusStr = json.optString("status", "ON_TRACK")
            val status = try {
                StepStatus.valueOf(statusStr)
            } catch (e: Exception) {
                StepStatus.ON_TRACK
            }

            EvaluationResult(
                status = status,
                accuracyScore = json.optInt("accuracyScore", 75),
                encouragement = json.optString("encouragement", "Great work! Keep your pencil moving smoothly."),
                feedback = json.optString("feedback", "Good alignment and stroke placement."),
                feedbackHinglish = json.optString("feedbackHinglish", "Boht badhiya! Aapka sketch sahi direction me ja raha hai."),
                correctionGuide = json.optString("correctionGuide", "Continue following the guide lines."),
                detectedStrokes = json.optString("detectedStrokes", "Light pencil guidelines on paper."),
                canProceed = json.optBoolean("canProceed", true)
            )
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Evaluation timed out", e)
            EvaluationResult(
                status = StepStatus.ON_TRACK,
                accuracyScore = 70,
                encouragement = "The drawing looks promising!",
                feedback = "Network verification timed out, but feel free to continue to the next step when you feel ready.",
                feedbackHinglish = "Verification me thoda time laga, par aap agle step par badh sakte hain.",
                correctionGuide = "Check your proportions with the reference guide and proceed.",
                detectedStrokes = "Pencil strokes detected.",
                canProceed = true
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Firebase AI evaluation failed", e)
            EvaluationResult(
                status = StepStatus.ON_TRACK,
                accuracyScore = 70,
                encouragement = "Keep going! Your confidence is improving with every stroke.",
                feedback = "Unable to analyze live frame: ${e.localizedMessage}. Compare your sheet with the on-screen diagram.",
                feedbackHinglish = "Frame check karne me dikkat aayi. On-screen diagram dekh kar apna drawing continue karein.",
                correctionGuide = "Ensure the drawing sheet is clearly lit and visible in frame.",
                detectedStrokes = "Live visual frame.",
                canProceed = true
            )
        }
    }

    suspend fun generateCustomLesson(userSubjectPrompt: String): DrawingSubject = withContext(Dispatchers.IO) {
        val prompt = """
            Create a structured 5-step drawing lesson for the subject: "$userSubjectPrompt".
            
            Follow the Master Art Coach methodology:
            - Step 1 (GEOMETRIC_BASES): Break down into basic circles, squares, ovals, axis lines.
            - Step 2 (SECONDARY_GUIDES): Secondary angles, connecting cylinders, alignment lines.
            - Step 3 (PRIMARY_OUTLINES): Main silhouette, contour curves, outer boundaries.
            - Step 4 (DEFINING_DETAILS): Internal features, expression, distinct textures.
            - Step 5 (SHADING_AND_POLISH): Light source direction, core shadow, highlights, finishing polish.
            
            Ensure spoken coaching instructions are provided in both English and friendly Hinglish.
        """.trimIndent()

        try {
            val response = withTimeout(60_000L) {
                lessonModel.generateContent(prompt)
            }

            val rawJson = response.text ?: throw IllegalStateException("Model returned empty text.")
            Log.d(TAG, "Lesson response: $rawJson")

            val json = JSONObject(rawJson)
            val title = json.optString("title", userSubjectPrompt.replaceFirstChar { it.uppercase() })
            val category = json.optString("category", "Custom")
            val difficulty = json.optString("difficulty", "Beginner")
            val estimatedMinutes = json.optInt("estimatedMinutes", 15)
            val description = json.optString("description", "Step-by-step master drawing breakdown for $userSubjectPrompt.")

            val stepsJsonArray = json.getJSONArray("steps")
            val stepsList = mutableListOf<DrawingStep>()

            for (i in 0 until stepsJsonArray.length()) {
                val stepObj = stepsJsonArray.getJSONObject(i)
                val phaseStr = stepObj.optString("phase", "GEOMETRIC_BASES")
                val phase = try {
                    StepPhase.valueOf(phaseStr)
                } catch (e: Exception) {
                    when (i) {
                        0 -> StepPhase.GEOMETRIC_BASES
                        1 -> StepPhase.SECONDARY_GUIDES
                        2 -> StepPhase.PRIMARY_OUTLINES
                        3 -> StepPhase.DEFINING_DETAILS
                        else -> StepPhase.SHADING_AND_POLISH
                    }
                }

                val tipsArray = stepObj.optJSONArray("tips")
                val tips = mutableListOf<String>()
                if (tipsArray != null) {
                    for (t in 0 until tipsArray.length()) {
                        tips.add(tipsArray.getString(t))
                    }
                }

                val targetShapesArray = stepObj.optJSONArray("targetGeometricShapes")
                val targetShapes = mutableListOf<String>()
                if (targetShapesArray != null) {
                    for (s in 0 until targetShapesArray.length()) {
                        targetShapes.add(targetShapesArray.getString(s))
                    }
                }

                stepsList.add(
                    DrawingStep(
                        stepIndex = stepObj.optInt("stepIndex", i + 1),
                        title = stepObj.optString("title", "Step ${i + 1}"),
                        phase = phase,
                        instruction = stepObj.optString("instruction", "Follow the stroke guide."),
                        spokenCoachingEn = stepObj.optString("spokenCoachingEn", "Draw lightly with gentle wrist motion."),
                        spokenCoachingHinglish = stepObj.optString("spokenCoachingHinglish", "Halke haath se line draw karein."),
                        tips = if (tips.isNotEmpty()) tips else listOf("Keep pencil pressure light", "Draw from shoulder"),
                        strokeDiagramDesc = stepObj.optString("strokeDiagramDesc", "Geometric guide strokes"),
                        targetGeometricShapes = if (targetShapes.isNotEmpty()) targetShapes else listOf("Guides")
                    )
                )
            }

            DrawingSubject(
                id = "custom_" + System.currentTimeMillis(),
                title = title,
                category = category,
                difficulty = difficulty,
                estimatedMinutes = estimatedMinutes,
                drawableResId = null,
                description = description,
                steps = stepsList
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Failed to generate custom lesson", e)
            throw e
        }
    }

    private fun scaleBitmapToMaxDimension(source: Bitmap, maxDim: Int): Bitmap {
        val width = source.width
        val height = source.height
        if (width <= maxDim && height <= maxDim) return source

        val ratio = width.toFloat() / height.toFloat()
        val newWidth: Int
        val newHeight: Int
        if (width > height) {
            newWidth = maxDim
            newHeight = (maxDim / ratio).toInt().coerceAtLeast(1)
        } else {
            newHeight = maxDim
            newWidth = (maxDim * ratio).toInt().coerceAtLeast(1)
        }
        return Bitmap.createScaledBitmap(source, newWidth, newHeight, true)
    }
}
