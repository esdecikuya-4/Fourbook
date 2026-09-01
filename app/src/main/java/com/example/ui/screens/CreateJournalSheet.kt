package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.JournalCategory
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import com.example.ui.components.MemberAvatar
import com.example.ui.theme.*
import com.example.ui.viewmodel.GalleryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateJournalSheet(
    viewModel: GalleryViewModel,
    onDismiss: () -> Unit
) {
    val allUsers by viewModel.allUsers.collectAsState()
    val students = remember(allUsers) {
        allUsers.filter { it.role != UserRole.WALI_KELAS.name }
    }

    var selectedStudent by remember { mutableStateOf<UserEntity?>(null) } // null = Seluruh Siswa
    var selectedCategory by remember { mutableStateOf(JournalCategory.SIKAP_POSITIF) }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var followUpAction by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showStudentDropdown by remember { mutableStateOf(false) }

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
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFDCFCE7),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.AutoStories,
                                contentDescription = null,
                                tint = Color(0xFF16A34A),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Catat Jurnal Harian Siswa",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = FbTextPrimary
                        )
                        Text(
                            text = "SDN 4 Putrajawa • Catatan Guru (Teten Kurniawan)",
                            fontSize = 11.sp,
                            color = FbTextSecondary
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Tutup")
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

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
                // Student Selection
                item {
                    Text(
                        text = "Siswa Terkait:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = FbTextSecondary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = FbBg,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showStudentDropdown = !showStudentDropdown }
                            .testTag("select_journal_student")
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (selectedStudent != null) {
                                    MemberAvatar(
                                        fullName = selectedStudent!!.fullName,
                                        avatarColor = selectedStudent!!.avatarColor,
                                        avatarIcon = selectedStudent!!.avatarIcon,
                                        customPhotoUri = selectedStudent!!.customPhotoUri,
                                        size = 30.dp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "${selectedStudent!!.fullName} (${selectedStudent!!.studentNumber})",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Groups,
                                        contentDescription = null,
                                        tint = FbBluePrimary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Seluruh Siswa SDN 4 Putrajawa",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = FbBluePrimary
                                    )
                                }
                            }

                            Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                    }

                    if (showStudentDropdown) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = FbCardBg),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(6.dp)) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (selectedStudent == null) FbBlueLight else Color.Transparent,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            selectedStudent = null
                                            showStudentDropdown = false
                                        }
                                        .padding(8.dp)
                                ) {
                                    Text("🏫 Seluruh Siswa SDN 4 Putrajawa", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }

                                students.forEach { st ->
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (selectedStudent?.id == st.id) FbBlueLight else Color.Transparent,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                selectedStudent = st
                                                showStudentDropdown = false
                                            }
                                            .padding(8.dp)
                                    ) {
                                        Text("${st.fullName} (${st.studentNumber})", fontSize = 13.sp)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Category Selection
                    Text(
                        text = "Kategori Kejadian:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = FbTextSecondary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        JournalCategory.entries.take(3).forEach { cat ->
                            val isSelected = selectedCategory == cat
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedCategory = cat },
                                label = { Text(cat.title, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(cat.colorHex),
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        JournalCategory.entries.drop(3).forEach { cat ->
                            val isSelected = selectedCategory == cat
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedCategory = cat },
                                label = { Text(cat.title, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(cat.colorHex),
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Judul Kejadian / Prestasi") },
                        placeholder = { Text("Contoh: Dimas Membantu Teman & Menjaga Ketertiban") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_journal_title"),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Kronologi / Deskripsi Kejadian") },
                        placeholder = { Text("Ceritakan kejadian secara jelas dan objektif...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_journal_desc"),
                        minLines = 3,
                        maxLines = 6,
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = followUpAction,
                        onValueChange = { followUpAction = it },
                        label = { Text("Tindak Lanjut / Catatan Guru") },
                        placeholder = { Text("Contoh: Diberi apresiasi bintang penghargaan di depan kelas.") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (title.isBlank() || description.isBlank()) {
                        errorMessage = "Judul dan deskripsi kejadian wajib diisi"
                        return@Button
                    }

                    viewModel.addJournalEntry(
                        student = selectedStudent,
                        category = selectedCategory,
                        title = title,
                        description = description,
                        followUpAction = followUpAction,
                        onSuccess = {
                            onDismiss()
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("btn_save_journal"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A))
            ) {
                Icon(imageVector = Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Simpan ke Jurnal Harian", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}
