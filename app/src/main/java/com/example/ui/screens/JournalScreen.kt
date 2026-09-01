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
import com.example.data.model.JournalCategory
import com.example.data.model.StudentJournalEntity
import com.example.data.model.UserRole
import com.example.ui.components.MemberAvatar
import com.example.ui.theme.*
import com.example.ui.viewmodel.GalleryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalScreen(
    viewModel: GalleryViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val allJournals by viewModel.allJournals.collectAsState()
    val showCreateJournalSheet by viewModel.showCreateJournalSheet.collectAsState()

    var selectedCategoryFilter by remember { mutableStateOf("Semua") }
    var searchQuery by remember { mutableStateOf("") }

    val isTeacher = currentUser?.role == UserRole.WALI_KELAS.name

    val filteredJournals = remember(allJournals, selectedCategoryFilter, searchQuery) {
        allJournals.filter { entry ->
            val matchCategory = selectedCategoryFilter == "Semua" || entry.category == selectedCategoryFilter
            val matchSearch = searchQuery.isBlank() ||
                    entry.title.contains(searchQuery, ignoreCase = true) ||
                    entry.studentName.contains(searchQuery, ignoreCase = true) ||
                    entry.description.contains(searchQuery, ignoreCase = true)
            matchCategory && matchSearch
        }
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
                                    color = Color(0xFFDCFCE7),
                                    modifier = Modifier.size(42.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.MenuBook,
                                            contentDescription = null,
                                            tint = Color(0xFF16A34A),
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Jurnal Harian Siswa",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 18.sp,
                                        color = FbTextPrimary
                                    )
                                    Text(
                                        text = "SDN 4 PUTRAJAWA • Catatan Perilaku & Prestasi",
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
                                        text = "GURU WALI",
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
                                onClick = { viewModel.setShowCreateJournalSheet(true) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("btn_open_create_journal"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A))
                            ) {
                                Icon(Icons.Default.PostAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Catat Kejadian / Prestasi Baru", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Cari nama siswa atau peristiwa...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = FbTextSecondary) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Hapus")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = FbCardBg,
                        unfocusedContainerColor = FbCardBg
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Category Filter Bar
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedCategoryFilter == "Semua",
                            onClick = { selectedCategoryFilter = "Semua" },
                            label = { Text("Semua", fontSize = 11.5.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = FbBluePrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }

                    items(JournalCategory.entries) { cat ->
                        val isSelected = selectedCategoryFilter == cat.name
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategoryFilter = cat.name },
                            label = { Text(cat.title, fontSize = 11.5.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(cat.colorHex),
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }

            // Journal Entries List
            if (filteredJournals.isEmpty()) {
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
                                imageVector = Icons.Default.EventNote,
                                contentDescription = null,
                                tint = FbTextSecondary,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Belum Ada Catatan Jurnal",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = FbTextPrimary
                            )
                            Text(
                                text = "Catatan kejadian dan bimbingan siswa SDN 4 Putrajawa akan tercatat di sini.",
                                fontSize = 12.sp,
                                color = FbTextSecondary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(filteredJournals) { entry ->
                    val catEnum = JournalCategory.entries.find { it.name == entry.category } ?: JournalCategory.SIKAP_POSITIF

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .testTag("journal_card_${entry.id}"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = FbCardBg),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            // Top Student Row & Category Badge
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    MemberAvatar(
                                        fullName = entry.studentName,
                                        avatarColor = entry.studentAvatarColor,
                                        avatarIcon = entry.studentAvatarIcon,
                                        customPhotoUri = entry.studentCustomPhotoUri,
                                        size = 36.dp
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = entry.studentName,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = FbTextPrimary
                                        )
                                        Text(
                                            text = "${entry.studentNumber} • Tanggal: ${entry.date}",
                                            fontSize = 11.5.sp,
                                            color = FbTextSecondary
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(catEnum.badgeBg)
                                ) {
                                    Text(
                                        text = catEnum.title,
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(catEnum.colorHex),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = entry.title,
                                fontSize = 15.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = FbTextPrimary
                            )

                            Text(
                                text = entry.description,
                                fontSize = 13.sp,
                                color = FbTextPrimary,
                                lineHeight = 19.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )

                            if (entry.followUpAction.isNotBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = FbBg,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Lightbulb,
                                            contentDescription = null,
                                            tint = Color(0xFFD97706),
                                            modifier = Modifier.size(16.dp).padding(top = 2.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = "Tindak Lanjut / Catatan Guru:",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF92400E)
                                            )
                                            Text(
                                                text = entry.followUpAction,
                                                fontSize = 12.sp,
                                                color = FbTextPrimary
                                            )
                                        }
                                    }
                                }
                            }

                            Divider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFF1F5F9))

                            // Footer: Teacher signature & delete button
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = null,
                                        tint = FbBluePrimary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Dicatat oleh: ${entry.teacherName}",
                                        fontSize = 11.sp,
                                        color = FbTextSecondary
                                    )
                                }

                                if (isTeacher) {
                                    IconButton(
                                        onClick = { viewModel.deleteJournalEntry(entry.id) },
                                        modifier = Modifier.size(30.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DeleteOutline,
                                            contentDescription = "Hapus",
                                            tint = Color(0xFFDC2626),
                                            modifier = Modifier.size(18.dp)
                                        )
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

        if (showCreateJournalSheet) {
            CreateJournalSheet(
                viewModel = viewModel,
                onDismiss = { viewModel.setShowCreateJournalSheet(false) }
            )
        }
    }
}
