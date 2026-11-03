package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.DrawingSubject
import com.example.ui.theme.FeedbackSuccessGreen
import com.example.ui.theme.OchreGoldPrimary
import com.example.ui.theme.StudioSurfaceElevated
import com.example.ui.theme.StudioTealSecondary
import com.example.ui.viewmodel.DrawingCoachViewModel

@Composable
fun HomeScreen(
    viewModel: DrawingCoachViewModel,
    onStartLesson: (DrawingSubject) -> Unit
) {
    val subjects by viewModel.subjects.collectAsState()
    val isHinglish by viewModel.isHinglishMode.collectAsState()
    val isMuted by viewModel.ttsManager.isMuted.collectAsState()
    val isGenerating by viewModel.isGeneratingLesson.collectAsState()
    val customError by viewModel.customLessonError.collectAsState()

    var customPrompt by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var showDeskSetupGuide by remember { mutableStateOf(false) }

    val categories = listOf("All", "Animals", "Still Life", "Anime", "Custom")

    val filteredSubjects = if (selectedCategory == "All") {
        subjects
    } else {
        subjects.filter { it.category.equals(selectedCategory, ignoreCase = true) }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF13141C))
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            // Top Bar with branding & quick toggles
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(OchreGoldPrimary, StudioTealSecondary)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Brush,
                            contentDescription = "Art Coach Logo",
                            tint = Color(0xFF1A1B28),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Art Coach AI",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFF5F5FA)
                        )
                        Text(
                            text = "Real-Time Camera Drawing Tutor",
                            fontSize = 12.sp,
                            color = StudioTealSecondary
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Language Mode Pill
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF282A3A),
                        modifier = Modifier
                            .testTag("language_toggle_button")
                            .clickable { viewModel.toggleLanguageMode() }
                    ) {
                        Text(
                            text = if (isHinglish) "Hinglish 🇮🇳" else "English 🇺🇸",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = OchreGoldPrimary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Audio Mute toggle
                    IconButton(
                        onClick = { viewModel.ttsManager.toggleMute() },
                        modifier = Modifier.testTag("audio_mute_toggle")
                    ) {
                        Icon(
                            imageVector = if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                            contentDescription = "Toggle Audio Voice",
                            tint = if (isMuted) Color.Gray else OchreGoldPrimary
                        )
                    }
                }
            }
        }

        // Hero Card: Multimodal Camera Tutor
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E202B))
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = OchreGoldPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "PATIENT & ENCOURAGING MENTOR",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = OchreGoldPrimary,
                                letterSpacing = 1.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Point Your Camera.\nDraw Line-By-Line.",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            lineHeight = 30.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Our AI observes your sketchbook paper live via your camera, verifies your strokes, and gently coaches your geometric proportions in real time.",
                            fontSize = 13.sp,
                            color = Color(0xFFA5A8B8),
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = { showDeskSetupGuide = !showDeskSetupGuide },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF282A3A)),
                                modifier = Modifier.testTag("setup_guide_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.HelpOutline,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = StudioTealSecondary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (showDeskSetupGuide) "Hide Camera Setup" else "Camera Setup Guide",
                                    fontSize = 12.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        // Expandable Camera Desk Setup Guide
        item {
            AnimatedVisibility(visible = showDeskSetupGuide) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF222533))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = OchreGoldPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "How to Position Your Device",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 15.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "1. Place your sketchbook or drawing sheet flat on a desk.\n" +
                                    "2. Prop your phone against a coffee mug, laptop base, or desk stand with the back camera angled down toward your paper.\n" +
                                    "3. Keep good lighting so your pencil strokes are clearly visible.\n" +
                                    "4. Listen to the coach's audio instructions and tap 'Check My Drawing' whenever you finish a step!",
                            fontSize = 13.sp,
                            color = Color(0xFFC0C4D6),
                            lineHeight = 20.sp
                        )
                    }
                }
            }
        }

        // Custom Subject Creator ("Draw Anything")
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = StudioSurfaceElevated)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Create,
                            contentDescription = null,
                            tint = StudioTealSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Want to Draw Something Specific?",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 15.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Enter any subject (e.g., 'Dragon', 'Cute Owl', 'Vintage Vespa') and the AI coach will break it down into 5 geometric stages for you.",
                        fontSize = 12.sp,
                        color = Color(0xFFA5A8B8)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = customPrompt,
                            onValueChange = { customPrompt = it },
                            placeholder = { Text("e.g. Cartoon Dragon, Rose, Anime Eye", fontSize = 13.sp, color = Color.Gray) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("custom_subject_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = OchreGoldPrimary,
                                unfocusedBorderColor = Color(0xFF43465C),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    if (customPrompt.isNotBlank()) {
                                        viewModel.generateCustomSubject(customPrompt)
                                    }
                                }
                            )
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Button(
                            onClick = {
                                if (customPrompt.isNotBlank()) {
                                    viewModel.generateCustomSubject(customPrompt)
                                }
                            },
                            enabled = !isGenerating && customPrompt.isNotBlank(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = OchreGoldPrimary),
                            modifier = Modifier.testTag("generate_lesson_button")
                        ) {
                            if (isGenerating) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = Color(0xFF1E202B),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = "Create",
                                    color = Color(0xFF1A1B28),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    if (customError != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = customError ?: "",
                            color = Color(0xFFFF5252),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Category Filter Chips
        item {
            Column {
                Text(
                    text = "Master Drawing Lessons",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(10.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = OchreGoldPrimary,
                                selectedLabelColor = Color(0xFF1A1B28),
                                containerColor = Color(0xFF1E202B),
                                labelColor = Color(0xFFC0C4D6)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                    }
                }
            }
        }

        // Curated Lesson Cards
        items(filteredSubjects) { subject ->
            LessonCard(
                subject = subject,
                onStart = {
                    viewModel.startSession(subject)
                    onStartLesson(subject)
                }
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun LessonCard(
    subject: DrawingSubject,
    onStart: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("lesson_card_${subject.id}")
            .clickable { onStart() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFF1E202B))
    ) {
        Column {
            // Preview Image or Illustrated Header
            if (subject.drawableResId != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .background(Color(0xFF262838))
                ) {
                    Image(
                        painter = painterResource(id = subject.drawableResId),
                        contentDescription = subject.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )

                    // Category Pill on top-right
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF13141C).copy(alpha = 0.85f),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(10.dp)
                    ) {
                        Text(
                            text = subject.category,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = StudioTealSecondary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            } else {
                // Generative artwork placeholder
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF282A3A), Color(0xFF1C1D29))
                            )
                        )
                        .padding(16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Brush,
                            contentDescription = null,
                            tint = OchreGoldPrimary,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = subject.title,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 18.sp
                            )
                            Text(
                                text = "AI Generated Custom Lesson",
                                color = StudioTealSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = subject.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = when (subject.difficulty.lowercase()) {
                            "beginner" -> FeedbackSuccessGreen.copy(alpha = 0.2f)
                            "intermediate" -> OchreGoldPrimary.copy(alpha = 0.2f)
                            else -> Color(0xFFFF5252).copy(alpha = 0.2f)
                        }
                    ) {
                        Text(
                            text = subject.difficulty,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = when (subject.difficulty.lowercase()) {
                                "beginner" -> FeedbackSuccessGreen
                                "intermediate" -> OchreGoldPrimary
                                else -> Color(0xFFFF5252)
                            },
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = subject.description,
                    fontSize = 13.sp,
                    color = Color(0xFFA5A8B8),
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "~${subject.estimatedMinutes} mins • ${subject.steps.size} Steps",
                            fontSize = 12.sp,
                            color = Color(0xFFA5A8B8)
                        )
                    }

                    Button(
                        onClick = onStart,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = OchreGoldPrimary),
                        modifier = Modifier.testTag("start_lesson_${subject.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color(0xFF1A1B28),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Start Lesson",
                            color = Color(0xFF1A1B28),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}
