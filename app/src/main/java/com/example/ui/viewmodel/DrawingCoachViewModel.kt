package com.example.ui.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.DrawingStep
import com.example.data.model.DrawingSubject
import com.example.data.model.EvaluationResult
import com.example.data.model.StepStatus
import com.example.data.repository.DrawingLibraryRepository
import com.example.data.service.AiCoachService
import com.example.data.service.TtsManager
import com.example.ui.camera.CameraController
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

private const val TAG = "DrawingCoachVM"

enum class AppScreen {
    HOME,
    SESSION,
    COMPLETED
}

sealed interface EvaluationUiState {
    data object Idle : EvaluationUiState
    data object CapturingFrame : EvaluationUiState
    data object Analyzing : EvaluationUiState
    data class Success(val result: EvaluationResult, val capturedFrame: Bitmap?) : EvaluationUiState
    data class Error(val message: String) : EvaluationUiState
}

class DrawingCoachViewModel(context: Context) : ViewModel() {

    private val aiService = AiCoachService()
    val ttsManager = TtsManager(context)

    private val _screen = MutableStateFlow(AppScreen.HOME)
    val screen: StateFlow<AppScreen> = _screen.asStateFlow()

    private val _subjects = MutableStateFlow<List<DrawingSubject>>(DrawingLibraryRepository.curatedSubjects)
    val subjects: StateFlow<List<DrawingSubject>> = _subjects.asStateFlow()

    private val _currentSubject = MutableStateFlow<DrawingSubject?>(DrawingLibraryRepository.curatedSubjects.firstOrNull())
    val currentSubject: StateFlow<DrawingSubject?> = _currentSubject.asStateFlow()

    private val _currentStepIndex = MutableStateFlow(0)
    val currentStepIndex: StateFlow<Int> = _currentStepIndex.asStateFlow()

    private val _evaluationState = MutableStateFlow<EvaluationUiState>(EvaluationUiState.Idle)
    val evaluationState: StateFlow<EvaluationUiState> = _evaluationState.asStateFlow()

    private val _isHinglishMode = MutableStateFlow(true)
    val isHinglishMode: StateFlow<Boolean> = _isHinglishMode.asStateFlow()

    private val _isAutoCheckActive = MutableStateFlow(false)
    val isAutoCheckActive: StateFlow<Boolean> = _isAutoCheckActive.asStateFlow()

    private val _showGrid = MutableStateFlow(true)
    val showGrid: StateFlow<Boolean> = _showGrid.asStateFlow()

    private val _showReticle = MutableStateFlow(true)
    val showReticle: StateFlow<Boolean> = _showReticle.asStateFlow()

    private val _isGeneratingLesson = MutableStateFlow(false)
    val isGeneratingLesson: StateFlow<Boolean> = _isGeneratingLesson.asStateFlow()

    private val _customLessonError = MutableStateFlow<String?>(null)
    val customLessonError: StateFlow<String?> = _customLessonError.asStateFlow()

    private val _sessionScores = MutableStateFlow<MutableList<Int>>(mutableListOf())
    val sessionScores: StateFlow<List<Int>> = _sessionScores.asStateFlow()

    private val _latestCapturedArtwork = MutableStateFlow<Bitmap?>(null)
    val latestCapturedArtwork: StateFlow<Bitmap?> = _latestCapturedArtwork.asStateFlow()

    private var autoCheckJob: Job? = null

    fun selectSubject(subject: DrawingSubject) {
        _currentSubject.value = subject
    }

    fun startSession(subject: DrawingSubject) {
        _currentSubject.value = subject
        _currentStepIndex.value = 0
        _sessionScores.value = mutableListOf()
        _evaluationState.value = EvaluationUiState.Idle
        _screen.value = AppScreen.SESSION

        speakCurrentStepInstruction()
    }

    fun exitSession() {
        stopAutoCheck()
        ttsManager.stop()
        _screen.value = AppScreen.HOME
    }

    fun speakCurrentStepInstruction() {
        val subject = _currentSubject.value ?: return
        val steps = subject.steps
        if (_currentStepIndex.value in steps.indices) {
            val step = steps[_currentStepIndex.value]
            val speechText = if (_isHinglishMode.value) {
                step.spokenCoachingHinglish
            } else {
                step.spokenCoachingEn
            }
            ttsManager.speak(speechText)
        }
    }

    fun evaluateCurrentDrawing(cameraController: CameraController) {
        val subject = _currentSubject.value ?: return
        val steps = subject.steps
        val stepIdx = _currentStepIndex.value
        if (stepIdx !in steps.indices) return

        val currentStep = steps[stepIdx]

        viewModelScope.launch {
            _evaluationState.value = EvaluationUiState.CapturingFrame
            try {
                val bitmap = cameraController.captureBitmap()
                _latestCapturedArtwork.value = bitmap
                _evaluationState.value = EvaluationUiState.Analyzing

                val result = aiService.evaluateDrawing(
                    drawingBitmap = bitmap,
                    subjectTitle = subject.title,
                    currentStep = currentStep,
                    totalSteps = steps.size
                )

                _sessionScores.value.add(result.accuracyScore)
                _evaluationState.value = EvaluationUiState.Success(result, bitmap)

                // Speak feedback encouragingly!
                val spokenVoice = if (_isHinglishMode.value) {
                    "${result.feedbackHinglish} ${result.correctionGuide}"
                } else {
                    "${result.encouragement} ${result.feedback}"
                }
                ttsManager.speak(spokenVoice)

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Evaluation failed", e)
                _evaluationState.value = EvaluationUiState.Error(
                    e.localizedMessage ?: "Failed to evaluate drawing frame. Please try again."
                )
            }
        }
    }

    fun advanceStep() {
        val subject = _currentSubject.value ?: return
        val nextIdx = _currentStepIndex.value + 1
        if (nextIdx < subject.steps.size) {
            _currentStepIndex.value = nextIdx
            _evaluationState.value = EvaluationUiState.Idle
            speakCurrentStepInstruction()
        } else {
            // Completed all steps!
            _screen.value = AppScreen.COMPLETED
            ttsManager.speak(
                if (_isHinglishMode.value)
                    "Badhai ho! Aapka drawing complete ho gaya hai. Aapne boht khoobsurat sketch banaya hai!"
                else
                    "Congratulations! You completed the drawing lesson with flying colors! Magnificent work!"
            )
        }
    }

    fun previousStep() {
        if (_currentStepIndex.value > 0) {
            _currentStepIndex.value -= 1
            _evaluationState.value = EvaluationUiState.Idle
            speakCurrentStepInstruction()
        }
    }

    fun toggleLanguageMode() {
        val newMode = !_isHinglishMode.value
        _isHinglishMode.value = newMode
        speakCurrentStepInstruction()
    }

    fun toggleGrid() {
        _showGrid.value = !_showGrid.value
    }

    fun toggleReticle() {
        _showReticle.value = !_showReticle.value
    }

    fun toggleAutoCheck(cameraController: CameraController) {
        val newAuto = !_isAutoCheckActive.value
        _isAutoCheckActive.value = newAuto
        if (newAuto) {
            startAutoCheck(cameraController)
        } else {
            stopAutoCheck()
        }
    }

    private fun startAutoCheck(cameraController: CameraController) {
        autoCheckJob?.cancel()
        autoCheckJob = viewModelScope.launch {
            while (isActive && _isAutoCheckActive.value) {
                delay(12_000L) // Scan drawing every 12 seconds
                if (_screen.value == AppScreen.SESSION &&
                    _evaluationState.value !is EvaluationUiState.Analyzing &&
                    _evaluationState.value !is EvaluationUiState.CapturingFrame
                ) {
                    evaluateCurrentDrawing(cameraController)
                }
            }
        }
    }

    private fun stopAutoCheck() {
        autoCheckJob?.cancel()
        autoCheckJob = null
        _isAutoCheckActive.value = false
    }

    fun generateCustomSubject(prompt: String) {
        if (prompt.isBlank()) return
        viewModelScope.launch {
            _isGeneratingLesson.value = true
            _customLessonError.value = null
            try {
                val newSubject = aiService.generateCustomLesson(prompt)
                _subjects.value = listOf(newSubject) + _subjects.value
                _currentSubject.value = newSubject
                _isGeneratingLesson.value = false
                startSession(newSubject)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Custom lesson error", e)
                _isGeneratingLesson.value = false
                _customLessonError.value = "Could not generate lesson: ${e.localizedMessage}. Please try a different subject prompt."
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopAutoCheck()
        ttsManager.release()
    }
}
