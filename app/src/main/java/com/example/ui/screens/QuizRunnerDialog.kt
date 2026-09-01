package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.QuizEntity
import com.example.data.model.QuizSubmissionEntity
import com.example.ui.components.MemberAvatar
import com.example.ui.theme.*
import com.example.ui.viewmodel.GalleryViewModel

@Composable
fun QuizRunnerDialog(
    quiz: QuizEntity,
    viewModel: GalleryViewModel,
    onDismiss: () -> Unit
) {
    val questions = quiz.questions
    val selectedAnswers = remember { mutableStateMapOf<Int, Int>() }
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var isSubmitted by remember { mutableStateOf(false) }
    var submissionResult by remember { mutableStateOf<QuizSubmissionEntity?>(null) }
    val currentUser by viewModel.currentUser.collectAsState()

    Dialog(
        onDismissRequest = {
            if (isSubmitted) onDismiss()
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            color = FbBg,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = FbBlueLight
                        ) {
                            Text(
                                text = "SDN 4 PUTRAJAWA • ${quiz.subject.uppercase()}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = FbBluePrimary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Text(
                            text = quiz.title,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = FbTextPrimary,
                            maxLines = 1
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup")
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFE2E8F0))

                if (!isSubmitted) {
                    // QUIZ ACTIVE RUNNER MODE
                    val currentQ = questions.getOrNull(currentQuestionIndex)
                    if (currentQ != null) {
                        // Progress Bar & Info
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Soal ${currentQuestionIndex + 1} dari ${questions.size}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = FbTextSecondary
                            )

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFFEF3C7)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Timer,
                                        contentDescription = null,
                                        tint = Color(0xFFD97706),
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${quiz.durationMinutes} Menit",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFB45309)
                                    )
                                }
                            }
                        }

                        LinearProgressIndicator(
                            progress = { (currentQuestionIndex + 1).toFloat() / questions.size.toFloat() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = FbBluePrimary,
                            trackColor = Color(0xFFE2E8F0),
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Question Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = FbCardBg),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = currentQ.questionText,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = FbTextPrimary,
                                    lineHeight = 22.sp
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // Options List
                                val options = currentQ.getOptions()
                                val selectedOption = selectedAnswers[currentQuestionIndex]

                                options.forEachIndexed { optIdx, optText ->
                                    val isChosen = selectedOption == optIdx
                                    val letter = when (optIdx) {
                                        0 -> "A"
                                        1 -> "B"
                                        2 -> "C"
                                        else -> "D"
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isChosen) FbBlueLight else FbBg,
                                        border = androidx.compose.foundation.BorderStroke(
                                            width = if (isChosen) 2.dp else 1.dp,
                                            color = if (isChosen) FbBluePrimary else Color(0xFFE2E8F0)
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 5.dp)
                                            .clickable {
                                                selectedAnswers[currentQuestionIndex] = optIdx
                                            }
                                            .testTag("quiz_option_${currentQuestionIndex}_$optIdx")
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Surface(
                                                shape = CircleShape,
                                                color = if (isChosen) FbBluePrimary else Color(0xFF94A3B8),
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Text(
                                                        text = letter,
                                                        color = Color.White,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 13.sp
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.width(12.dp))

                                            Text(
                                                text = optText,
                                                color = if (isChosen) FbBluePrimary else FbTextPrimary,
                                                fontWeight = if (isChosen) FontWeight.Bold else FontWeight.Normal,
                                                fontSize = 14.sp,
                                                modifier = Modifier.weight(1f)
                                            )

                                            if (isChosen) {
                                                Icon(
                                                    imageVector = Icons.Default.CheckCircle,
                                                    contentDescription = null,
                                                    tint = FbBluePrimary,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        // Navigation Buttons (Prev, Next / Submit)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (currentQuestionIndex > 0) {
                                OutlinedButton(
                                    onClick = { currentQuestionIndex-- },
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Sebelumnya")
                                }
                            } else {
                                Spacer(modifier = Modifier.width(10.dp))
                            }

                            if (currentQuestionIndex < questions.size - 1) {
                                Button(
                                    onClick = { currentQuestionIndex++ },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = FbBluePrimary)
                                ) {
                                    Text("Selanjutnya")
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                                }
                            } else {
                                // Final Submit Button
                                Button(
                                    onClick = {
                                        viewModel.submitQuizAnswers(
                                            quiz = quiz,
                                            selectedAnswers = selectedAnswers.toMap(),
                                            onDone = { result ->
                                                submissionResult = result
                                                isSubmitted = true
                                            }
                                        )
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                                    modifier = Modifier.testTag("btn_submit_quiz_answers")
                                ) {
                                    Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Kirim Jawaban", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                } else {
                    // QUIZ RESULT SCORECARD & REVIEW
                    val result = submissionResult
                    if (result != null) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            item {
                                // Trophy & Score Badge
                                Surface(
                                    shape = CircleShape,
                                    color = if (result.score >= 70) Color(0xFFDCFCE7) else Color(0xFFFEF3C7),
                                    modifier = Modifier.size(84.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = if (result.score >= 70) Icons.Default.EmojiEvents else Icons.Default.AutoStories,
                                            contentDescription = null,
                                            tint = if (result.score >= 70) Color(0xFF16A34A) else Color(0xFFD97706),
                                            modifier = Modifier.size(44.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = if (result.score >= 70) "Luar Biasa, Kamu Lulus!" else "Tetap Semangat Belajar!",
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.Black,
                                    color = FbTextPrimary
                                )

                                Text(
                                    text = "Hasil Pengerjaan Kuis Siswa SDN 4 Putrajawa",
                                    fontSize = 12.sp,
                                    color = FbTextSecondary
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // Score Big Display
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = FbCardBg),
                                    shape = RoundedCornerShape(12.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        horizontalArrangement = Arrangement.SpaceEvenly,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text("Nilai Kamu", fontSize = 11.sp, color = FbTextSecondary)
                                            Text(
                                                text = "${result.score}",
                                                fontSize = 32.sp,
                                                fontWeight = FontWeight.Black,
                                                color = if (result.score >= 70) Color(0xFF16A34A) else Color(0xFFD97706)
                                            )
                                        }

                                        Divider(modifier = Modifier.height(40.dp).width(1.dp), color = Color(0xFFE2E8F0))

                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text("Jawaban Benar", fontSize = 11.sp, color = FbTextSecondary)
                                            Text(
                                                text = "${result.correctCount} / ${result.totalQuestions}",
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = FbBluePrimary
                                            )
                                        }

                                        Divider(modifier = Modifier.height(40.dp).width(1.dp), color = Color(0xFFE2E8F0))

                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text("Status", fontSize = 11.sp, color = FbTextSecondary)
                                            Text(
                                                text = if (result.score >= 70) "LULUS" else "REMIDI",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Black,
                                                color = if (result.score >= 70) Color(0xFF16A34A) else Color(0xFFDC2626)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "Pembahasan & Kunci Jawaban:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = FbTextPrimary,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 6.dp)
                                )
                            }

                            // Review each question
                            items(questions.size) { qIdx ->
                                val q = questions[qIdx]
                                val chosen = selectedAnswers[qIdx]
                                val isCorrect = chosen == q.correctOptionIndex

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 5.dp),
                                    colors = CardDefaults.cardColors(containerColor = FbCardBg),
                                    shape = RoundedCornerShape(10.dp),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isCorrect) Color(0xFF86EFAC) else Color(0xFFFCA5A5)
                                    )
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "Soal ${qIdx + 1}",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = FbTextSecondary
                                            )
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = if (isCorrect) Color(0xFFDCFCE7) else Color(0xFFFEE2E2)
                                            ) {
                                                Text(
                                                    text = if (isCorrect) "Benar (+${q.points})" else "Salah (0)",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isCorrect) Color(0xFF16A34A) else Color(0xFFDC2626),
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(q.questionText, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)

                                        Spacer(modifier = Modifier.height(6.dp))
                                        val correctOptText = q.getOptions().getOrNull(q.correctOptionIndex) ?: ""
                                        Text(
                                            text = "Kunci Jawaban: $correctOptText",
                                            fontSize = 12.sp,
                                            color = Color(0xFF16A34A),
                                            fontWeight = FontWeight.Bold
                                        )

                                        if (q.explanation.isNotBlank()) {
                                            Text(
                                                text = "Penjelasan: ${q.explanation}",
                                                fontSize = 11.5.sp,
                                                color = FbTextSecondary,
                                                modifier = Modifier.padding(top = 2.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = onDismiss,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = FbBluePrimary)
                        ) {
                            Text("Selesai & Tutup", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
