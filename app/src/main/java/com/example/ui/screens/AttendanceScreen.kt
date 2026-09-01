package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AttendanceEntity
import com.example.data.model.AttendanceStatus
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import com.example.ui.components.MemberAvatar
import com.example.ui.components.Sdn4SchoolLogo
import com.example.ui.theme.*
import com.example.ui.viewmodel.GalleryViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(
    viewModel: GalleryViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val selectedDate by viewModel.selectedAttendanceDate.collectAsStateWithLifecycle()
    val attendanceList by viewModel.attendanceForSelectedDate.collectAsStateWithLifecycle()
    val summary by viewModel.attendanceSummary.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilterStatus by remember { mutableStateOf<AttendanceStatus?>(null) }
    var studentForNoteDialog by remember { mutableStateOf<UserEntity?>(null) }
    var noteInputText by remember { mutableStateOf("") }
    var showRecapDialog by remember { mutableStateOf(false) }

    // Date parsing & display formatting
    val calendar = remember(selectedDate) {
        val cal = Calendar.getInstance()
        try {
            val parsed = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(selectedDate)
            if (parsed != null) cal.time = parsed
        } catch (_: Exception) {}
        cal
    }

    val displayDateFormatted = remember(selectedDate) {
        try {
            val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(selectedDate)
            if (date != null) {
                SimpleDateFormat("EEEE, d MMMM yyyy", Locale("id", "ID")).format(date)
            } else {
                selectedDate
            }
        } catch (_: Exception) {
            selectedDate
        }
    }

    val isToday = remember(selectedDate) {
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        selectedDate == todayStr
    }

    // Filter students only (exclude teacher from student attendance list)
    val students = remember(allUsers) {
        allUsers.filter { it.role != UserRole.WALI_KELAS.name }
    }

    // Map studentId to existing Attendance record
    val attendanceMap = remember(attendanceList) {
        attendanceList.associateBy { it.studentId }
    }

    // Filtered list based on search and status filter
    val filteredStudents = remember(students, attendanceMap, searchQuery, selectedFilterStatus) {
        students.filter { student ->
            val matchesSearch = student.fullName.contains(searchQuery, ignoreCase = true) ||
                    student.studentNumber.contains(searchQuery, ignoreCase = true)
            val currentStatus = attendanceMap[student.id]?.let { AttendanceStatus.fromString(it.status) }
                ?: AttendanceStatus.HADIR

            val matchesFilter = selectedFilterStatus == null || currentStatus == selectedFilterStatus
            matchesSearch && matchesFilter
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Surface(
                color = PolishHeaderSurface,
                shadowElevation = 2.dp,
                border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Centered Header Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Left School Logo
                        Sdn4SchoolLogo(
                            size = 44.dp,
                            showGlow = true
                        )

                        // Centered Title & School Name
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = PrimaryPurple.copy(alpha = 0.1f),
                                modifier = Modifier.padding(bottom = 1.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = Color(0xFFD97706),
                                        modifier = Modifier.size(10.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "SDN 4 PUTRAJAWA",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = PrimaryPurple,
                                        letterSpacing = 0.6.sp
                                    )
                                }
                            }
                            Text(
                                text = "Absensi Siswa Harian",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = PolishTextPrimary,
                                textAlign = TextAlign.Center
                            )
                        }

                        // Right User Avatar or Switch Button
                        if (currentUser != null) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryContainerPurple)
                                    .clickable {
                                        viewModel.setShowQuickSwitchSheet(true)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                MemberAvatar(
                                    fullName = currentUser!!.fullName,
                                    avatarColor = currentUser!!.avatarColor,
                                    avatarIcon = currentUser!!.avatarIcon,
                                    customPhotoUri = currentUser!!.customPhotoUri,
                                    size = 32.dp
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.size(38.dp))
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(PolishBackground),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Date Navigator Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = PolishCardWhite),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Prev Day Button
                            IconButton(
                                onClick = {
                                    val prevCal = Calendar.getInstance().apply {
                                        time = calendar.time
                                        add(Calendar.DAY_OF_YEAR, -1)
                                    }
                                    val newDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(prevCal.time)
                                    viewModel.setSelectedAttendanceDate(newDate)
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(PolishSurfaceVariant, CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Hari Sebelumnya",
                                    tint = PolishTextPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            // Center Date Display
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarToday,
                                        contentDescription = null,
                                        tint = PrimaryPurple,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = displayDateFormatted,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PolishTextPrimary,
                                        textAlign = TextAlign.Center
                                    )
                                }
                                if (isToday) {
                                    Text(
                                        text = "Hari Ini",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF16A34A)
                                    )
                                } else {
                                    Text(
                                        text = "Tanggal: $selectedDate",
                                        fontSize = 10.sp,
                                        color = PolishTextSecondary
                                    )
                                }
                            }

                            // Next Day Button
                            IconButton(
                                onClick = {
                                    val nextCal = Calendar.getInstance().apply {
                                        time = calendar.time
                                        add(Calendar.DAY_OF_YEAR, 1)
                                    }
                                    val newDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(nextCal.time)
                                    viewModel.setSelectedAttendanceDate(newDate)
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(PolishSurfaceVariant, CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Hari Berikutnya",
                                    tint = PolishTextPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        if (!isToday) {
                            Spacer(modifier = Modifier.height(8.dp))
                            FilledTonalButton(
                                onClick = {
                                    val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                                    viewModel.setSelectedAttendanceDate(todayStr)
                                },
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Today,
                                    contentDescription = null,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Kembali ke Hari Ini", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // 2. Attendance Summary Statistics Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = PolishCardWhite),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Rekap Kehadiran",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PolishTextPrimary
                                )
                                Text(
                                    text = "Total Siswa: ${summary.totalStudents} orang",
                                    fontSize = 12.sp,
                                    color = PolishTextSecondary
                                )
                            }

                            // Percentage Badge
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (summary.percentage >= 80) Color(0xFFDCFCE7) else Color(0xFFFEF3C7),
                                border = BorderStroke(
                                    1.dp,
                                    if (summary.percentage >= 80) Color(0xFF16A34A).copy(alpha = 0.4f) else Color(0xFFD97706).copy(alpha = 0.4f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = if (summary.percentage >= 80) Color(0xFF16A34A) else Color(0xFFD97706),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${summary.percentage}% Hadir",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (summary.percentage >= 80) Color(0xFF15803D) else Color(0xFFB45309)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Linear Progress Indicator
                        LinearProgressIndicator(
                            progress = { summary.percentage / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = Color(0xFF16A34A),
                            trackColor = PolishSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // 4 KPI Stat Badges (Hadir, Izin, Sakit, Alpa)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AttendanceStatusChip(
                                label = "Hadir",
                                count = summary.hadirCount,
                                color = Color(0xFF16A34A),
                                bgColor = Color(0xFFDCFCE7),
                                isSelected = selectedFilterStatus == AttendanceStatus.HADIR,
                                onClick = {
                                    selectedFilterStatus = if (selectedFilterStatus == AttendanceStatus.HADIR) null else AttendanceStatus.HADIR
                                },
                                modifier = Modifier.weight(1f)
                            )

                            AttendanceStatusChip(
                                label = "Izin",
                                count = summary.izinCount,
                                color = Color(0xFFD97706),
                                bgColor = Color(0xFFFEF3C7),
                                isSelected = selectedFilterStatus == AttendanceStatus.IZIN,
                                onClick = {
                                    selectedFilterStatus = if (selectedFilterStatus == AttendanceStatus.IZIN) null else AttendanceStatus.IZIN
                                },
                                modifier = Modifier.weight(1f)
                            )

                            AttendanceStatusChip(
                                label = "Sakit",
                                count = summary.sakitCount,
                                color = Color(0xFF0284C7),
                                bgColor = Color(0xFFE0F2FE),
                                isSelected = selectedFilterStatus == AttendanceStatus.SAKIT,
                                onClick = {
                                    selectedFilterStatus = if (selectedFilterStatus == AttendanceStatus.SAKIT) null else AttendanceStatus.SAKIT
                                },
                                modifier = Modifier.weight(1f)
                            )

                            AttendanceStatusChip(
                                label = "Alpa",
                                count = summary.alpaCount,
                                color = Color(0xFFDC2626),
                                bgColor = Color(0xFFFEE2E2),
                                isSelected = selectedFilterStatus == AttendanceStatus.ALPA,
                                onClick = {
                                    selectedFilterStatus = if (selectedFilterStatus == AttendanceStatus.ALPA) null else AttendanceStatus.ALPA
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // 3. Quick Action Buttons (Tandai Semua Hadir & Rekap WA)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            viewModel.markAllStudentsPresent()
                            Toast.makeText(context, "Semua siswa ditandai HADIR ✅", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF16A34A),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("btn_mark_all_present")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DoneAll,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Semua Hadir",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            showRecapDialog = true
                        },
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, PrimaryPurple),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryPurple),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("btn_recap_attendance")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Salin Rekap WA",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // 4. Student Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            "Cari nama siswa atau no absen...",
                            fontSize = 13.sp,
                            color = PolishTextSecondary
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = PolishTextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = PolishTextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = PolishCardWhite,
                        unfocusedContainerColor = PolishCardWhite,
                        focusedBorderColor = PrimaryPurple,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    ),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("attendance_search_input")
                )
            }

            // 5. Active Filter Status Indication
            if (selectedFilterStatus != null) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Menampilkan status: ${selectedFilterStatus!!.title} (${filteredStudents.size} siswa)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryPurple
                        )
                        TextButton(
                            onClick = { selectedFilterStatus = null },
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("Reset Filter", fontSize = 11.sp, color = Color(0xFFDC2626))
                        }
                    }
                }
            }

            // 6. Section Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Daftar Absensi Siswa",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = PolishTextPrimary
                    )
                    Text(
                        text = "${filteredStudents.size} Murid",
                        fontSize = 12.sp,
                        color = PolishTextSecondary
                    )
                }
            }

            // 7. Student Attendance Items
            items(filteredStudents, key = { it.id }) { student ->
                val currentRecord = attendanceMap[student.id]
                val currentStatus = currentRecord?.let { AttendanceStatus.fromString(it.status) }
                    ?: AttendanceStatus.HADIR
                val note = currentRecord?.note ?: ""

                StudentAttendanceRowCard(
                    student = student,
                    currentStatus = currentStatus,
                    note = note,
                    onStatusSelected = { newStatus ->
                        viewModel.setStudentAttendance(student, newStatus, note)
                    },
                    onEditNoteClick = {
                        studentForNoteDialog = student
                        noteInputText = note
                    }
                )
            }

            // Empty search state
            if (filteredStudents.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = PolishCardWhite),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.SearchOff,
                                contentDescription = null,
                                tint = PolishTextSecondary,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Tidak ada siswa ditemukan",
                                fontWeight = FontWeight.Bold,
                                color = PolishTextPrimary
                            )
                            Text(
                                text = "Coba kata kunci lain atau reset filter status.",
                                fontSize = 12.sp,
                                color = PolishTextSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            // Bottom Spacing
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Dialog: Edit Catatan/Alasan Absensi (Izin/Sakit)
    if (studentForNoteDialog != null) {
        val student = studentForNoteDialog!!
        val currentRecord = attendanceMap[student.id]
        val currentStatus = currentRecord?.let { AttendanceStatus.fromString(it.status) }
            ?: AttendanceStatus.HADIR

        AlertDialog(
            onDismissRequest = { studentForNoteDialog = null },
            icon = {
                Icon(
                    imageVector = Icons.Default.EditNote,
                    contentDescription = null,
                    tint = PrimaryPurple,
                    modifier = Modifier.size(28.dp)
                )
            },
            title = {
                Text(
                    text = "Catatan Absensi",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Siswa: ${student.fullName} (${student.studentNumber})",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = PolishTextPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Keterangan / Alasan (Opsional):",
                        fontSize = 12.sp,
                        color = PolishTextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    OutlinedTextField(
                        value = noteInputText,
                        onValueChange = { noteInputText = it },
                        placeholder = { Text("Contoh: Demam flu, izin ke luar kota, dll...", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.setStudentAttendance(student, currentStatus, noteInputText.trim())
                        studentForNoteDialog = null
                        Toast.makeText(context, "Catatan berhasil disimpan", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
                ) {
                    Text("Simpan")
                }
            },
            dismissButton = {
                TextButton(onClick = { studentForNoteDialog = null }) {
                    Text("Batal")
                }
            }
        )
    }

    // Dialog: Salin Rekap WhatsApp
    if (showRecapDialog) {
        val recapText = remember(selectedDate, summary, attendanceList) {
            viewModel.exportAttendanceRecapText()
        }

        AlertDialog(
            onDismissRequest = { showRecapDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = null,
                    tint = Color(0xFF16A34A),
                    modifier = Modifier.size(30.dp)
                )
            },
            title = {
                Text(
                    text = "Rekap Absensi Siap Kirim",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PolishSurfaceVariant, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = recapText,
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        color = PolishTextPrimary
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Rekap Absensi Kelas 4", recapText)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Rekap berhasil disalin ke clipboard! 📋", Toast.LENGTH_LONG).show()
                        showRecapDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A))
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Salin Teks")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRecapDialog = false }) {
                    Text("Tutup")
                }
            }
        )
    }
}

/**
 * Interactive Student Attendance Card Row
 */
@Composable
fun StudentAttendanceRowCard(
    student: UserEntity,
    currentStatus: AttendanceStatus,
    note: String,
    onStatusSelected: (AttendanceStatus) -> Unit,
    onEditNoteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("attendance_row_${student.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PolishCardWhite),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Student Avatar
                MemberAvatar(
                    fullName = student.fullName,
                    avatarColor = student.avatarColor,
                    avatarIcon = student.avatarIcon,
                    customPhotoUri = student.customPhotoUri,
                    size = 40.dp
                )

                Spacer(modifier = Modifier.width(10.dp))

                // Name & Student Number
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = student.fullName,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = PolishTextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (student.role == UserRole.KETUA_KELAS.name) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFFFEF3C7)
                            ) {
                                Text(
                                    text = "Ketua",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFB45309),
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }
                    Text(
                        text = if (student.studentNumber.isNotBlank()) student.studentNumber else "Siswa Kelas 4",
                        fontSize = 11.sp,
                        color = PolishTextSecondary
                    )
                }

                // Note action button
                IconButton(
                    onClick = onEditNoteClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (note.isNotBlank()) Icons.Filled.StickyNote2 else Icons.Outlined.EditNote,
                        contentDescription = "Catatan",
                        tint = if (note.isNotBlank()) PrimaryPurple else PolishTextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 4 Segmented Status Buttons [ H | I | S | A ]
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp)
                    .background(PolishSurfaceVariant, RoundedCornerShape(12.dp))
                    .padding(3.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                AttendanceStatus.entries.forEach { status ->
                    val isSelected = currentStatus == status

                    val activeBg = Color(status.bgHex)
                    val activeFg = Color(status.colorHex)

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(9.dp))
                            .background(if (isSelected) activeBg else Color.Transparent)
                            .clickable { onStatusSelected(status) }
                            .testTag("status_btn_${student.id}_${status.code}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = status.code,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                                color = if (isSelected) activeFg else PolishTextSecondary
                            )
                            if (isSelected) {
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = status.title,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = activeFg
                                )
                            }
                        }
                    }
                }
            }

            // If note is present, display it nicely
            if (note.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = PrimaryContainerPurple.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = PrimaryPurple,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = note,
                            fontSize = 11.sp,
                            color = OnPrimaryContainerPurple,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

/**
 * Top Status Filter Chip
 */
@Composable
fun AttendanceStatusChip(
    label: String,
    count: Int,
    color: Color,
    bgColor: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) color else bgColor,
        border = BorderStroke(if (isSelected) 1.5.dp else 1.dp, color.copy(alpha = 0.5f)),
        modifier = modifier
            .height(52.dp)
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 4.dp, vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "$count",
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (isSelected) Color.White else color,
                lineHeight = 16.sp
            )
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White.copy(alpha = 0.9f) else color.copy(alpha = 0.85f),
                lineHeight = 12.sp
            )
        }
    }
}
