package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.QuizEntity
import com.example.data.model.QuizSubmissionEntity
import com.example.data.model.UserRole
import com.example.ui.components.MemberAvatar
import com.example.ui.theme.*
import com.example.ui.viewmodel.GalleryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    viewModel: GalleryViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val allQuizzes by viewModel.allQuizzes.collectAsState()
    val allSubmissions by viewModel.allSubmissions.collectAsState()
    val activeQuizToTake by viewModel.activeQuizToTake.collectAsState()
    val showCreateQuizSheet by viewModel.showCreateQuizSheet.collectAsState()
    val selectedQuizForSubmissions by viewModel.selectedQuizForSubmissions.collectAsState()

    var selectedSubjectFilter by remember { mutableStateOf("Semua") }
    val subjects = listOf("Semua", "IPAS", "Matematika", "Bahasa Indonesia", "Pendidikan Pancasila", "PJOK")

    val isTeacher = currentUser?.role == UserRole.WALI_KELAS.name

    val filteredQuizzes = remember(allQuizzes, selectedSubjectFilter) {
        if (selectedSubjectFilter == "Semua") allQuizzes
        else allQuizzes.filter { it.subject.equals(selectedSubjectFilter, ignoreCase = true) }
    }

    Box(modifier = modifier.fillMaxSize().background(FbBg)) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp)
        ) {
            // Header Info Card
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = FbCardBg),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = FbBlueLight,
                                    modifier = Modifier.size(42.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Quiz,
                                            contentDescription = null,
                                            tint = FbBluePrimary,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Kuis & Ulangan Harian",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 18.sp,
                                        color = FbTextPrimary
                                    )
                                    Text(
                                        text = "SDN 4 PUTRAJAWA • Wajib Dikerjakan Siswa",
                                        fontSize = 11.sp,
                                        color = FbTextSecondary
                                    )
                                }
                            }

                            if (isTeacher) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFF059669)
                                ) {
                                    Text(
                                        text = "MODE GURU",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        if (isTeacher) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { viewModel.setShowCreateQuizSheet(true) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("btn_open_create_quiz"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = FbBluePrimary)
                            ) {
                                Icon(Icons.Default.AddCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Buat Soal Ulangan / Kuis Baru", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Subject Filter Bar
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(subjects) { subj ->
                        val isSelected = selectedSubjectFilter == subj
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedSubjectFilter = subj },
                            label = { Text(subj, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = FbBluePrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }

            // Quizzes List
            if (filteredQuizzes.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        colors = CardDefaults.cardColors(containerColor = FbCardBg),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.MenuBook,
                                contentDescription = null,
                                tint = FbTextSecondary,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Belum Ada Soal Ulangan",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = FbTextPrimary
                            )
                            Text(
                                text = "Soal kuis dan ulangan harian dari Pak Teten Kurniawan akan tampil di sini.",
                                fontSize = 12.sp,
                                color = FbTextSecondary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(filteredQuizzes) { quiz ->
                    val userSubmission = allSubmissions.find {
                        it.quizId == quiz.id && it.studentId == (currentUser?.id ?: 0L)
                    }
                    val quizSubmissionsCount = allSubmissions.count { it.quizId == quiz.id }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .testTag("quiz_card_${quiz.id}"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = FbCardBg),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            // Top Row: Subject & Due Date
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = when (quiz.subject) {
                                        "IPAS" -> Color(0xFFDCFCE7)
                                        "Matematika" -> Color(0xFFE0F2FE)
                                        "Bahasa Indonesia" -> Color(0xFFFEF3C7)
                                        else -> FbBlueLight
                                    }
                                ) {
                                    Text(
                                        text = quiz.subject.uppercase(),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when (quiz.subject) {
                                            "IPAS" -> Color(0xFF15803D)
                                            "Matematika" -> Color(0xFF0369A1)
                                            "Bahasa Indonesia" -> Color(0xFFB45309)
                                            else -> FbBluePrimary
                                        },
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Schedule,
                                        contentDescription = null,
                                        tint = FbTextSecondary,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = quiz.dueDate.ifBlank { "Tersedia" },
                                        fontSize = 11.sp,
                                        color = FbTextSecondary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = quiz.title,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = FbTextPrimary
                            )

                            if (quiz.description.isNotBlank()) {
                                Text(
                                    text = quiz.description,
                                    fontSize = 12.5.sp,
                                    color = FbTextSecondary,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Meta details: Questions count, duration, points, teacher
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.HelpOutline, contentDescription = null, tint = FbTextSecondary, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text("${quiz.questions.size} Soal", fontSize = 11.5.sp, color = FbTextSecondary)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Timer, contentDescription = null, tint = FbTextSecondary, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text("${quiz.durationMinutes} Menit", fontSize = 11.5.sp, color = FbTextSecondary)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.StarBorder, contentDescription = null, tint = FbTextSecondary, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text("Maks ${quiz.totalPoints} Poin", fontSize = 11.5.sp, color = FbTextSecondary)
                                }
                            }

                            Divider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFF1F5F9))

                            // Action Area
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (isTeacher) {
                                    // Teacher sees submission count & manage buttons
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "$quizSubmissionsCount Siswa Menjawab",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = FbBluePrimary
                                        )
                                    }

                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        OutlinedButton(
                                            onClick = { viewModel.setSelectedQuizForSubmissions(quiz) },
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Text("Lihat Rekap Nilai", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }

                                        IconButton(
                                            onClick = { viewModel.deleteQuiz(quiz.id) },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(Icons.Default.DeleteOutline, contentDescription = "Hapus", tint = Color(0xFFDC2626), modifier = Modifier.size(18.dp))
                                        }
                                    }
                                } else {
                                    // Student sees submission status or Start Quiz button
                                    if (userSubmission != null) {
                                        // Already answered
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = Color(0xFFDCFCE7)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "Sudah Selesai: Nilai ${userSubmission.score}",
                                                    fontSize = 11.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF15803D)
                                                )
                                            }
                                        }

                                        Button(
                                            onClick = { viewModel.setActiveQuizToTake(quiz) },
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = FbBg),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, FbBluePrimary)
                                        ) {
                                            Text("Ulangi / Review", fontSize = 11.5.sp, color = FbBluePrimary, fontWeight = FontWeight.Bold)
                                        }
                                    } else {
                                        // Need to take
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = Color(0xFFFEE2E2)
                                        ) {
                                            Text(
                                                text = "Wajib Dikerjakan",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFDC2626),
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }

                                        Button(
                                            onClick = { viewModel.setActiveQuizToTake(quiz) },
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = FbBluePrimary),
                                            modifier = Modifier.testTag("btn_take_quiz_${quiz.id}")
                                        ) {
                                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Kerjakan Soal", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }

        // Active Quiz Runner Modal
        activeQuizToTake?.let { quiz ->
            QuizRunnerDialog(
                quiz = quiz,
                viewModel = viewModel,
                onDismiss = { viewModel.setActiveQuizToTake(null) }
            )
        }

        // Teacher Quiz Submissions Review Modal
        selectedQuizForSubmissions?.let { quiz ->
            TeacherQuizSubmissionsSheet(
                quiz = quiz,
                viewModel = viewModel,
                onDismiss = { viewModel.setSelectedQuizForSubmissions(null) }
            )
        }

        // Create Quiz Sheet
        if (showCreateQuizSheet) {
            CreateQuizSheet(
                viewModel = viewModel,
                onDismiss = { viewModel.setShowCreateQuizSheet(false) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherQuizSubmissionsSheet(
    quiz: QuizEntity,
    viewModel: GalleryViewModel,
    onDismiss: () -> Unit
) {
    val allSubmissions by viewModel.allSubmissions.collectAsState()
    val quizSubmissions = remember(allSubmissions, quiz.id) {
        allSubmissions.filter { it.quizId == quiz.id }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = FbCardBg
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Rekap Nilai Siswa SDN 4 Putrajawa",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = FbTextPrimary
                    )
                    Text(
                        text = quiz.title,
                        fontSize = 12.sp,
                        color = FbTextSecondary
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Tutup")
                }
            }

            Divider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFE2E8F0))

            if (quizSubmissions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Belum ada siswa yang mengumpulkan jawaban kuis ini.",
                        fontSize = 13.sp,
                        color = FbTextSecondary
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(quizSubmissions) { sub ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = FbBg),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    MemberAvatar(
                                        fullName = sub.studentName,
                                        avatarColor = sub.studentAvatarColor,
                                        avatarIcon = sub.studentAvatarIcon,
                                        customPhotoUri = sub.studentCustomPhotoUri,
                                        size = 38.dp
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = sub.studentName,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = FbTextPrimary
                                        )
                                        Text(
                                            text = "${sub.studentNumber} • Benar: ${sub.correctCount}/${sub.totalQuestions}",
                                            fontSize = 11.5.sp,
                                            color = FbTextSecondary
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (sub.score >= 70) Color(0xFFDCFCE7) else Color(0xFFFEF3C7)
                                ) {
                                    Text(
                                        text = "${sub.score}",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 18.sp,
                                        color = if (sub.score >= 70) Color(0xFF15803D) else Color(0xFFB45309),
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
