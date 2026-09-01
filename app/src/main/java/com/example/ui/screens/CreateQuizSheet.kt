package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import com.example.data.model.QuizQuestionItem
import com.example.ui.theme.*
import com.example.ui.viewmodel.GalleryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateQuizSheet(
    viewModel: GalleryViewModel,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedSubject by remember { mutableStateOf("IPAS") }
    var description by remember { mutableStateOf("") }
    var durationMinutes by remember { mutableIntStateOf(15) }
    var dueDate by remember { mutableStateOf("Besok, 17.00 WIB") }

    val subjects = listOf("IPAS", "Matematika", "Bahasa Indonesia", "Pendidikan Pancasila", "PJOK", "Seni Budaya", "Bahasa Sunda / Daerah")

    // Questions list
    val questions = remember {
        mutableStateListOf(
            QuizQuestionItem(
                id = 1,
                questionText = "Apa fungsi utama akar pada tumbuhan?",
                optionA = "Menyerap air & mineral dari tanah",
                optionB = "Melakukan fotosintesis",
                optionC = "Menarik serangga penyerbuk",
                optionD = "Menghasilkan buah",
                correctOptionIndex = 0,
                points = 25,
                explanation = "Akar menyerap air dan zat hara serta memperkokoh tanaman."
            ),
            QuizQuestionItem(
                id = 2,
                questionText = "Proses fotosintesis menghasilkan zat tepung dan gas...",
                optionA = "Karbon dioksida",
                optionB = "Oksigen (O2)",
                optionC = "Nitrogen",
                optionD = "Metana",
                correctOptionIndex = 1,
                points = 25,
                explanation = "Fotosintesis menghasilkan oksigen yang dihirup makhluk hidup."
            )
        )
    }

    var errorMessage by remember { mutableStateOf<String?>(null) }

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
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = FbBluePrimary.copy(alpha = 0.15f),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Quiz,
                                contentDescription = null,
                                tint = FbBluePrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Buat Kuis & Soal Ulangan",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = FbTextPrimary
                        )
                        Text(
                            text = "Wali Kelas SDN 4 Putrajawa (Teten Kurniawan)",
                            fontSize = 11.sp,
                            color = FbTextSecondary
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (errorMessage != null) {
                Surface(
                    color = Color(0xFFFEE2E2),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                ) {
                    Text(
                        text = errorMessage!!,
                        color = Color(0xFFDC2626),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .fillMaxWidth()
            ) {
                // Info Fields
                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Judul Kuis / Ulangan Harian") },
                        placeholder = { Text("Contoh: Ulangan Harian IPAS Bab 2 Ekosistem") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_quiz_title"),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Mata Pelajaran:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = FbTextSecondary
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        subjects.take(4).forEach { subj ->
                            val isSelected = selectedSubject == subj
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedSubject = subj },
                                label = { Text(subj, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = FbBluePrimary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Petunjuk / Deskripsi Soal") },
                        placeholder = { Text("Wajib dikerjakan seluruh siswa SDN 4 Putrajawa...") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3,
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = durationMinutes.toString(),
                            onValueChange = { durationMinutes = it.toIntOrNull() ?: 10 },
                            label = { Text("Durasi (Menit)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )

                        OutlinedTextField(
                            value = dueDate,
                            onValueChange = { dueDate = it },
                            label = { Text("Batas Waktu / Jadwal") },
                            modifier = Modifier.weight(1.4f),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Questions Section Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Daftar Soal Pilihan Ganda (${questions.size} Soal)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = FbTextPrimary
                        )

                        TextButton(
                            onClick = {
                                val nextId = questions.size + 1
                                questions.add(
                                    QuizQuestionItem(
                                        id = nextId,
                                        questionText = "",
                                        optionA = "",
                                        optionB = "",
                                        optionC = "",
                                        optionD = "",
                                        correctOptionIndex = 0,
                                        points = 20,
                                        explanation = ""
                                    )
                                )
                            }
                        ) {
                            Icon(Icons.Default.AddCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Tambah Soal", fontSize = 12.sp)
                        }
                    }
                }

                // Question items
                itemsIndexed(questions) { index, q ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        colors = CardDefaults.cardColors(containerColor = FbBg),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = FbBlueLight
                                ) {
                                    Text(
                                        text = "Soal Nomor ${index + 1}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = FbBluePrimary,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }

                                if (questions.size > 1) {
                                    IconButton(
                                        onClick = { questions.removeAt(index) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DeleteOutline,
                                            contentDescription = "Hapus Soal",
                                            tint = Color(0xFFDC2626),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            OutlinedTextField(
                                value = q.questionText,
                                onValueChange = { newText ->
                                    questions[index] = q.copy(questionText = newText)
                                },
                                placeholder = { Text("Tulis pertanyaan nomor ${index + 1}...") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 2,
                                shape = RoundedCornerShape(8.dp)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Pilihan Jawaban (Pilih centang hijau untuk Kunci Jawaban):",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = FbTextSecondary
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            // Options A, B, C, D
                            listOf(
                                Pair("A", q.optionA),
                                Pair("B", q.optionB),
                                Pair("C", q.optionC),
                                Pair("D", q.optionD)
                            ).forEachIndexed { optIndex, (letter, textVal) ->
                                val isCorrect = q.correctOptionIndex == optIndex
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = if (isCorrect) Color(0xFF16A34A) else Color(0xFFCBD5E1),
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clickable {
                                                questions[index] = q.copy(correctOptionIndex = optIndex)
                                            }
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = letter,
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    OutlinedTextField(
                                        value = textVal,
                                        onValueChange = { newVal ->
                                            questions[index] = when (optIndex) {
                                                0 -> q.copy(optionA = newVal)
                                                1 -> q.copy(optionB = newVal)
                                                2 -> q.copy(optionC = newVal)
                                                else -> q.copy(optionD = newVal)
                                            }
                                        },
                                        placeholder = { Text("Opsi $letter") },
                                        modifier = Modifier.weight(1f),
                                        singleLine = true,
                                        shape = RoundedCornerShape(8.dp)
                                    )

                                    if (isCorrect) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Kunci Jawaban",
                                            tint = Color(0xFF16A34A),
                                            modifier = Modifier
                                                .padding(start = 6.dp)
                                                .size(20.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            OutlinedTextField(
                                value = q.explanation,
                                onValueChange = { exp -> questions[index] = q.copy(explanation = exp) },
                                placeholder = { Text("Pembahasan / Penjelasan jawaban (opsional)") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Publish Button
            Button(
                onClick = {
                    if (title.isBlank()) {
                        errorMessage = "Harap masukkan judul kuis / ulangan harian"
                        return@Button
                    }
                    if (questions.any { it.questionText.isBlank() || it.optionA.isBlank() || it.optionB.isBlank() }) {
                        errorMessage = "Semua soal dan opsi minimal A & B wajib diisi"
                        return@Button
                    }

                    viewModel.createQuiz(
                        title = title,
                        subject = selectedSubject,
                        description = description,
                        durationMinutes = durationMinutes,
                        questions = questions.toList(),
                        dueDate = dueDate,
                        onSuccess = {
                            onDismiss()
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("btn_publish_quiz"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = FbBluePrimary)
            ) {
                Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Publikasikan Soal Ulangan ke Seluruh Siswa",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}
