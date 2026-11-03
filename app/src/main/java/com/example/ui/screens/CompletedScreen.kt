package com.example.ui.screens

import android.graphics.Bitmap
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.FeedbackSuccessGreen
import com.example.ui.theme.OchreGoldPrimary
import com.example.ui.theme.StudioTealSecondary
import com.example.ui.viewmodel.DrawingCoachViewModel

@Composable
fun CompletedScreen(
    viewModel: DrawingCoachViewModel,
    onReturnHome: () -> Unit
) {
    val subject by viewModel.currentSubject.collectAsState()
    val scores by viewModel.sessionScores.collectAsState()
    val finalArtwork by viewModel.latestCapturedArtwork.collectAsState()
    val isHinglish by viewModel.isHinglishMode.collectAsState()

    val averageScore = if (scores.isNotEmpty()) {
        scores.average().toInt()
    } else {
        88
    }

    BackHandler {
        viewModel.exitSession()
        onReturnHome()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF13141C))
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Trophy / Mastery Badge
        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(OchreGoldPrimary, Color(0xFFC78500))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.WorkspacePremium,
                contentDescription = "Mastery Trophy",
                tint = Color(0xFF1A1B28),
                modifier = Modifier.size(46.dp)
            )
        }

        Text(
            text = if (isHinglish) "Boht Khoobsurat! Sketch Complete!" else "Magnificent! Artwork Complete!",
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Text(
            text = "You successfully broke down \"${subject?.title}\" into basic shapes, guides, contours, and shading like a true artist.",
            fontSize = 13.sp,
            color = Color(0xFFA5A8B8),
            textAlign = TextAlign.Center,
            lineHeight = 18.sp
        )

        // Certificate / Score card
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("completion_certificate_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFF1E202B))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "ARTIST PROGRESS REPORT",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = StudioTealSecondary,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = subject?.title ?: "Drawing Lesson",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    // Score pill
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = OchreGoldPrimary.copy(alpha = 0.2f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = OchreGoldPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$averageScore% Mastery",
                                fontWeight = FontWeight.ExtraBold,
                                color = OchreGoldPrimary,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Stats row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    StatPill("Steps Mastered", "${subject?.steps?.size ?: 5} / ${subject?.steps?.size ?: 5}")
                    StatPill("Proportions", "Verified")
                    StatPill("Line Control", "Excellent")
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Coach Mentor Quote
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF282A3A),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = OchreGoldPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "COACH MENTOR FEEDBACK",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = OchreGoldPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (isHinglish)
                                "\"Aapne geometric construction ko boht acche se follow kiya. Basic circle aur axis line sahi hone ki wajah se poora sketch symmetrical bana hai. Keep practicing daily!\""
                            else
                                "\"You demonstrated outstanding discipline with foundational geometric shapes! Starting loose and light allowed your primary contours to fall naturally into proportion. Keep sketching!\"",
                            fontSize = 12.sp,
                            color = Color(0xFFE2E4EC),
                            lineHeight = 17.sp
                        )
                    }
                }
            }
        }

        // Side-by-side comparison of user's camera drawing & lesson reference
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E202B))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Artwork Comparison",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Reference artwork
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF262838))
                        ) {
                            if (subject?.drawableResId != null) {
                                Image(
                                    painter = painterResource(id = subject!!.drawableResId!!),
                                    contentDescription = "Lesson Reference",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Fit
                                )
                            } else {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Brush,
                                        contentDescription = null,
                                        tint = Color.Gray,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Reference Goal", fontSize = 11.sp, color = Color(0xFFA5A8B8))
                    }

                    // User's camera photo
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF262838))
                        ) {
                            if (finalArtwork != null) {
                                Image(
                                    bitmap = finalArtwork!!.asImageBitmap(),
                                    contentDescription = "Your Paper Drawing",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = FeedbackSuccessGreen,
                                            modifier = Modifier.size(32.dp)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("Pencil Sketch", fontSize = 11.sp, color = Color.Gray)
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Your Sketchbook", fontSize = 11.sp, color = OchreGoldPrimary)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // CTA Buttons
        Button(
            onClick = {
                viewModel.exitSession()
                onReturnHome()
            },
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = OchreGoldPrimary),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("practice_another_button")
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                tint = Color(0xFF1E202B),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Practice Another Subject",
                color = Color(0xFF1E202B),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        OutlinedButton(
            onClick = {
                if (subject != null) {
                    viewModel.startSession(subject!!)
                }
            },
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("retake_lesson_button")
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Retake This Lesson",
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun StatPill(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = Color(0xFFA5A8B8)
        )
    }
}
