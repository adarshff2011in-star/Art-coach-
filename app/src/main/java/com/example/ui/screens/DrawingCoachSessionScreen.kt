package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.GridOff
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.model.StepStatus
import com.example.ui.camera.CameraController
import com.example.ui.camera.CameraPreview
import com.example.ui.components.StepStrokeGuideVisualizer
import com.example.ui.theme.FeedbackCorrectionCoral
import com.example.ui.theme.FeedbackSuccessGreen
import com.example.ui.theme.FeedbackWarningAmber
import com.example.ui.theme.OchreGoldPrimary
import com.example.ui.theme.StudioSurfaceElevated
import com.example.ui.theme.StudioTealSecondary
import com.example.ui.viewmodel.DrawingCoachViewModel
import com.example.ui.viewmodel.EvaluationUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawingCoachSessionScreen(
    viewModel: DrawingCoachViewModel,
    onExit: () -> Unit
) {
    val context = LocalContext.current
    val subject by viewModel.currentSubject.collectAsState()
    val stepIndex by viewModel.currentStepIndex.collectAsState()
    val evalState by viewModel.evaluationState.collectAsState()
    val isHinglish by viewModel.isHinglishMode.collectAsState()
    val isAutoCheck by viewModel.isAutoCheckActive.collectAsState()
    val showGrid by viewModel.showGrid.collectAsState()
    val showReticle by viewModel.showReticle.collectAsState()
    val isSpeaking by viewModel.ttsManager.isSpeaking.collectAsState()
    val isMuted by viewModel.ttsManager.isMuted.collectAsState()

    val cameraController = remember { CameraController(context) }
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Stroke Diagram, 1: Full Reference

    // Check & request camera permission
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        cameraController.hasCameraPermission = isGranted
    }

    LaunchedEffect(Unit) {
        val permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            cameraController.hasCameraPermission = true
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    BackHandler {
        viewModel.exitSession()
        onExit()
    }

    val currentStep = subject?.steps?.getOrNull(stepIndex)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF13141C))
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Column {
                    Text(
                        text = subject?.title ?: "Drawing Lesson",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Step ${stepIndex + 1} of ${subject?.steps?.size ?: 5} • ${currentStep?.phase?.label ?: ""}",
                        fontSize = 11.sp,
                        color = OchreGoldPrimary
                    )
                }
            },
            navigationIcon = {
                IconButton(
                    onClick = {
                        viewModel.exitSession()
                        onExit()
                    },
                    modifier = Modifier.testTag("exit_session_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Exit Session",
                        tint = Color.White
                    )
                }
            },
            actions = {
                // Hinglish / English Toggle
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF282A3A),
                    modifier = Modifier
                        .testTag("session_language_toggle")
                        .clickable { viewModel.toggleLanguageMode() }
                ) {
                    Text(
                        text = if (isHinglish) "Hinglish" else "English",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = StudioTealSecondary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Speech repeat button
                IconButton(
                    onClick = { viewModel.speakCurrentStepInstruction() },
                    modifier = Modifier.testTag("replay_voice_button")
                ) {
                    Icon(
                        imageVector = if (isSpeaking) Icons.AutoMirrored.Filled.VolumeUp else Icons.Default.VolumeOff,
                        contentDescription = "Replay Voice",
                        tint = if (isSpeaking) OchreGoldPrimary else Color.Gray
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFF1E202B)
            )
        )

        // Progress bar across the steps
        val totalSteps = subject?.steps?.size ?: 1
        val progress = (stepIndex + 1).toFloat() / totalSteps.toFloat()
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth(),
            color = OchreGoldPrimary,
            trackColor = Color(0xFF282A3A)
        )

        // Main content: Scrollable pane with Stroke Guide + Live Camera
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Step Instruction & Spoken Coaching Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E202B))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = currentStep?.title ?: "Current Step",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = Color.White
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = StudioTealSecondary.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = currentStep?.phase?.label ?: "",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = StudioTealSecondary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = currentStep?.instruction ?: "",
                        fontSize = 13.sp,
                        color = Color(0xFFE2E4EC),
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Coach Voice Bubble
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF282A3A),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = OchreGoldPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isHinglish)
                                    "\"${currentStep?.spokenCoachingHinglish}\""
                                else
                                    "\"${currentStep?.spokenCoachingEn}\"",
                                fontSize = 12.sp,
                                color = OchreGoldPrimary,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            // Reference / Stroke Guide Section with Tabs
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E202B))
            ) {
                Column {
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Color(0xFF1E202B),
                        contentColor = OchreGoldPrimary,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                color = OchreGoldPrimary
                            )
                        }
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = { Text("Stroke Guide Diagram", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = { Text("Full Reference Artwork", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(Color(0xFF13141C))
                    ) {
                        if (selectedTab == 0 && currentStep != null) {
                            StepStrokeGuideVisualizer(
                                step = currentStep,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            if (subject?.drawableResId != null) {
                                Image(
                                    painter = painterResource(id = subject!!.drawableResId!!),
                                    contentDescription = "Reference Artwork",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Fit
                                )
                            } else {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Full reference for ${subject?.title}",
                                        color = Color.Gray,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Live Camera View (Observing user's drawing sheet)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E202B))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(if (cameraController.hasCameraPermission) FeedbackSuccessGreen else Color.Red)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Live Drawing Camera",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }

                        // Camera controls (Torch, Flip, Grid, Reticle)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { cameraController.toggleTorch() },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = if (cameraController.isTorchOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                                    contentDescription = "Toggle Torch",
                                    tint = if (cameraController.isTorchOn) OchreGoldPrimary else Color.Gray,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            IconButton(
                                onClick = { cameraController.isFrontCamera = !cameraController.isFrontCamera },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FlipCameraAndroid,
                                    contentDescription = "Flip Camera",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            IconButton(
                                onClick = { viewModel.toggleGrid() },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = if (showGrid) Icons.Default.GridOn else Icons.Default.GridOff,
                                    contentDescription = "Toggle Grid",
                                    tint = if (showGrid) StudioTealSecondary else Color.Gray,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Camera preview window
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.Black)
                    ) {
                        if (cameraController.hasCameraPermission) {
                            CameraPreview(
                                controller = cameraController,
                                showGrid = showGrid,
                                showAlignmentGuide = showReticle,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = null,
                                    tint = Color.Gray,
                                    modifier = Modifier.size(44.dp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Camera Permission Required",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Point your camera at your drawing sheet for real-time AI guidance.",
                                    color = Color(0xFFA5A8B8),
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                                    colors = ButtonDefaults.buttonColors(containerColor = OchreGoldPrimary)
                                ) {
                                    Text("Grant Camera Access", color = Color(0xFF1E202B), fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // Auto-Scan Badge overlay at top-left
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isAutoCheck) OchreGoldPrimary.copy(alpha = 0.9f) else Color.Black.copy(alpha = 0.6f),
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(8.dp)
                                .clickable { viewModel.toggleAutoCheck(cameraController) }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Timer,
                                    contentDescription = null,
                                    tint = if (isAutoCheck) Color(0xFF1E202B) else Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isAutoCheck) "Auto-Check Active" else "Auto-Check Off",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isAutoCheck) Color(0xFF1E202B) else Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Primary tactile CTA: "Check My Drawing"
                    Button(
                        onClick = { viewModel.evaluateCurrentDrawing(cameraController) },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = OchreGoldPrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("check_drawing_button"),
                        enabled = evalState !is EvaluationUiState.Analyzing && evalState !is EvaluationUiState.CapturingFrame
                    ) {
                        if (evalState is EvaluationUiState.Analyzing || evalState is EvaluationUiState.CapturingFrame) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color(0xFF1E202B),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Coach is Examining Your Sheet...",
                                color = Color(0xFF1E202B),
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = Color(0xFF1E202B),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Check My Drawing Now",
                                color = Color(0xFF1E202B),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }

            // Real-time AI Evaluation Feedback Card
            AnimatedVisibility(
                visible = evalState is EvaluationUiState.Success || evalState is EvaluationUiState.Error,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut()
            ) {
                when (val state = evalState) {
                    is EvaluationUiState.Success -> {
                        val result = state.result
                        val statusColor = when (result.status) {
                            StepStatus.STEP_COMPLETE -> FeedbackSuccessGreen
                            StepStatus.ON_TRACK -> FeedbackSuccessGreen
                            StepStatus.NEEDS_CORRECTION -> FeedbackWarningAmber
                            StepStatus.PAPER_NOT_FOUND -> FeedbackCorrectionCoral
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("evaluation_result_card"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF222533)),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, statusColor.copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                // Status header row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = when (result.status) {
                                                StepStatus.STEP_COMPLETE, StepStatus.ON_TRACK -> Icons.Default.Check
                                                StepStatus.NEEDS_CORRECTION -> Icons.Default.Warning
                                                StepStatus.PAPER_NOT_FOUND -> Icons.Default.Info
                                            },
                                            contentDescription = null,
                                            tint = statusColor,
                                            modifier = Modifier.size(22.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = when (result.status) {
                                                StepStatus.STEP_COMPLETE -> "Step Mastered!"
                                                StepStatus.ON_TRACK -> "Looking Great!"
                                                StepStatus.NEEDS_CORRECTION -> "Needs Gentle Adjustment"
                                                StepStatus.PAPER_NOT_FOUND -> "Adjust Camera on Paper"
                                            },
                                            fontWeight = FontWeight.Bold,
                                            color = statusColor,
                                            fontSize = 15.sp
                                        )
                                    }

                                    // Score Badge
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = statusColor.copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = "${result.accuracyScore}% Match",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = statusColor,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Spoken / Voice feedback
                                Text(
                                    text = if (isHinglish) result.feedbackHinglish else result.encouragement,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White,
                                    lineHeight = 19.sp
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                // Detailed feedback & Correction Guide
                                Text(
                                    text = result.feedback,
                                    fontSize = 13.sp,
                                    color = Color(0xFFC0C4D6),
                                    lineHeight = 18.sp
                                )

                                if (result.correctionGuide.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = Color(0xFF1E202B),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Text(
                                                text = "PROPORTION & STROKE CORRECTION:",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = StudioTealSecondary,
                                                letterSpacing = 0.5.sp
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = result.correctionGuide,
                                                fontSize = 12.sp,
                                                color = OchreGoldPrimary,
                                                lineHeight = 16.sp
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Actions: Advance Step or Retry
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = { viewModel.evaluateCurrentDrawing(cameraController) },
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("recheck_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Refresh,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp),
                                            tint = Color.White
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Recheck", color = Color.White, fontSize = 13.sp)
                                    }

                                    Button(
                                        onClick = { viewModel.advanceStep() },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = OchreGoldPrimary),
                                        modifier = Modifier
                                            .weight(1.3f)
                                            .testTag("next_step_button")
                                    ) {
                                        Text(
                                            text = if (stepIndex + 1 < (subject?.steps?.size ?: 5)) "Next Step" else "Complete Artwork!",
                                            color = Color(0xFF1E202B),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                            contentDescription = null,
                                            tint = Color(0xFF1E202B),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    is EvaluationUiState.Error -> {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF332025))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "Evaluation Notice",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFF5252),
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = state.message,
                                    fontSize = 12.sp,
                                    color = Color(0xFFE2E4EC)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = { viewModel.evaluateCurrentDrawing(cameraController) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF43465C))
                                    ) {
                                        Text("Try Again", color = Color.White)
                                    }
                                    Button(
                                        onClick = { viewModel.advanceStep() },
                                        colors = ButtonDefaults.buttonColors(containerColor = OchreGoldPrimary)
                                    ) {
                                        Text("Proceed Anyway", color = Color(0xFF1E202B))
                                    }
                                }
                            }
                        }
                    }

                    else -> Unit
                }
            }
        }

        // Bottom step navigation footer
        Surface(
            color = Color(0xFF1E202B),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Previous step button
                FilledTonalButton(
                    onClick = { viewModel.previousStep() },
                    enabled = stepIndex > 0,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color(0xFF282A3A)),
                    modifier = Modifier.testTag("prev_step_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Previous Step",
                        modifier = Modifier.size(16.dp),
                        tint = if (stepIndex > 0) Color.White else Color.Gray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Prev",
                        color = if (stepIndex > 0) Color.White else Color.Gray,
                        fontSize = 12.sp
                    )
                }

                Text(
                    text = "${stepIndex + 1} / ${subject?.steps?.size ?: 5}",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 14.sp
                )

                // Next Step button
                Button(
                    onClick = { viewModel.advanceStep() },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = OchreGoldPrimary),
                    modifier = Modifier.testTag("footer_next_button")
                ) {
                    Text(
                        text = if (stepIndex + 1 < (subject?.steps?.size ?: 5)) "Next" else "Finish",
                        color = Color(0xFF1E202B),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Next Step",
                        modifier = Modifier.size(16.dp),
                        tint = Color(0xFF1E202B)
                    )
                }
            }
        }
    }
}
