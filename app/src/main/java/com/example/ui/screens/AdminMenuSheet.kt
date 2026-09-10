package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserRole
import com.example.ui.components.MemberAvatar
import com.example.ui.theme.*
import com.example.ui.viewmodel.GalleryViewModel
import com.example.ui.viewmodel.MainTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminMenuSheet(
    viewModel: GalleryViewModel,
    onDismiss: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    val attendanceSummary by viewModel.attendanceSummary.collectAsState()

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
            // Header Profile
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    MemberAvatar(
                        fullName = currentUser?.fullName ?: "Teten Kurniawan",
                        avatarColor = currentUser?.avatarColor ?: 0xFF1877F2,
                        avatarIcon = currentUser?.avatarIcon ?: "teacher",
                        customPhotoUri = currentUser?.customPhotoUri ?: "",
                        size = 46.dp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = currentUser?.fullName ?: "Teten Kurniawan",
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                color = FbTextPrimary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = null,
                                tint = FbBluePrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Text(
                            text = "Admin / Wali Kelas SDN 4 Putrajawa",
                            fontSize = 11.5.sp,
                            color = Color(0xFF059669),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "NIP. ${if (currentUser?.studentNumber.isNullOrBlank()) "197604062010011010" else currentUser?.studentNumber}",
                            fontSize = 10.5.sp,
                            color = FbTextSecondary
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup")
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quick Stats Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = FbBg),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Total Warga", fontSize = 11.sp, color = FbTextSecondary)
                        Text("${allUsers.count { it.role != UserRole.WALI_KELAS.name }}", fontWeight = FontWeight.Black, fontSize = 18.sp, color = FbBluePrimary)
                    }
                    Divider(modifier = Modifier.height(30.dp).width(1.dp), color = Color(0xFFCBD5E1))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Kehadiran Siswa", fontSize = 11.sp, color = FbTextSecondary)
                        Text("${attendanceSummary.percentage}%", fontWeight = FontWeight.Black, fontSize = 18.sp, color = Color(0xFF16A34A))
                    }
                    Divider(modifier = Modifier.height(30.dp).width(1.dp), color = Color(0xFFCBD5E1))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Hadir / Izin", fontSize = 11.sp, color = FbTextSecondary)
                        Text("${attendanceSummary.hadirCount} / ${attendanceSummary.izinCount + attendanceSummary.sakitCount}", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = FbTextPrimary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "MENU KELOLA KHUSUS ADMIN",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = FbTextSecondary,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Menu Items Grid/List
            AdminMenuCard(
                icon = Icons.Default.Campaign,
                iconBg = Color(0xFFDCFCE7),
                iconTint = Color(0xFF16A34A),
                title = "Buat Pengumuman & Berita Sekolah",
                description = "Posting berita, kegiatan siswa, atau pengumuman resmi SDN 4 Putrajawa ke beranda",
                onClick = {
                    onDismiss()
                    viewModel.openComposer(com.example.data.model.PostType.TEXT_STATUS)
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            AdminMenuCard(
                icon = Icons.Default.Quiz,
                iconBg = Color(0xFFE0F2FE),
                iconTint = FbBluePrimary,
                title = "Buat Kuis & Soal Ulangan Harian",
                description = "Buat soal pilihan ganda interaktif, kunci jawaban, dan durasi pengerjaan",
                onClick = {
                    onDismiss()
                    viewModel.setShowCreateQuizSheet(true)
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            AdminMenuCard(
                icon = Icons.Default.AutoStories,
                iconBg = Color(0xFFFEF3C7),
                iconTint = Color(0xFFD97706),
                title = "Tambah Catatan Jurnal Harian Siswa",
                description = "Catat prestasi, kejadian sehari-hari, bimbingan, atau pembinaan karakter siswa",
                onClick = {
                    onDismiss()
                    viewModel.setShowCreateJournalSheet(true)
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            AdminMenuCard(
                icon = Icons.Default.AccountCircle,
                iconBg = Color(0xFFF3E8FF),
                iconTint = Color(0xFF9333EA),
                title = "Edit Profil & Foto Wajah Admin",
                description = "Ganti foto profil dengan kamera / upload foto galeri ala Facebook",
                onClick = {
                    onDismiss()
                    viewModel.setActiveTab(MainTab.PROFIL)
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Server Hosting Status Info
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF0FDF4),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBBF7D0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF16A34A))
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Server Hosting Terhubung",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF166534)
                        )
                        Text(
                            text = "https://balallica.my.id (API Sinkronisasi Aktif)",
                            fontSize = 11.sp,
                            color = Color(0xFF15803D)
                        )
                    }
                    Icon(
                        Icons.Default.CloudDone,
                        contentDescription = null,
                        tint = Color(0xFF16A34A),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AdminMenuCard(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = FbBg,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = iconBg,
                modifier = Modifier.size(42.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.5.sp,
                    color = FbTextPrimary
                )
                Text(
                    text = description,
                    fontSize = 11.sp,
                    color = FbTextSecondary,
                    lineHeight = 15.sp
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = FbTextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
