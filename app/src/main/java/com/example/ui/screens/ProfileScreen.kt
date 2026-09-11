package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.GalleryViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: GalleryViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val allPhotos by viewModel.allPhotos.collectAsState()

    var showEditDialog by remember { mutableStateOf(false) }

    val user = currentUser
    if (user == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Silakan masuk dengan akun Fourbook terlebih dahulu.")
        }
        return
    }

    val userPhotos = remember(allPhotos, user) {
        allPhotos.filter { it.uploaderId == user.id }
    }
    val totalLikes = remember(userPhotos) {
        userPhotos.sumOf { it.likeCount }
    }

    if (showEditDialog) {
        EditProfileDialog(
            user = user,
            viewModel = viewModel,
            onDismiss = { showEditDialog = false },
            onSave = { name, username, password, number, color, icon, bio, customPhoto ->
                viewModel.updateProfile(
                    fullName = name,
                    username = username,
                    password = password,
                    studentNumber = number,
                    avatarColor = color,
                    avatarIcon = icon,
                    bio = bio,
                    customPhotoUri = customPhoto
                )
                showEditDialog = false
            }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = FbCanvasBg,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            Surface(
                color = FbCardSurface,
                shadowElevation = 2.dp,
                border = BorderStroke(0.5.dp, FbBorder.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Sdn4SchoolLogo(
                                size = 42.dp,
                                showGlow = true
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "fourbook",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = FbBluePrimary,
                                letterSpacing = (-0.5).sp
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            FilledTonalButton(
                                onClick = { viewModel.setShowQuickSwitchSheet(true) },
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = FbBlueLight,
                                    contentColor = FbBluePrimary
                                ),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(34.dp).testTag("profile_switch_account_btn")
                            ) {
                                Icon(Icons.Default.SwitchAccount, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Ganti Akun", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            IconButton(
                                onClick = { viewModel.logout() },
                                modifier = Modifier.size(34.dp).testTag("profile_logout_btn")
                            ) {
                                Icon(Icons.Default.Logout, contentDescription = "Keluar", tint = Color(0xFFDC2626), modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(FbCanvasBg)
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Facebook-Style Profile Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = FbCardSurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    border = BorderStroke(1.dp, FbBorder.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Cover Banner Gradient
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(95.dp)
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(
                                            Color(0xFF1877F2),
                                            Color(0xFF0866FF),
                                            Color(0xFF00C6FF)
                                        )
                                    )
                                )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color.Black.copy(alpha = 0.35f)
                                ) {
                                    Text(
                                        text = "SDN 4 PUTRAJAWA • ONLINE COMMUNITY",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }

                        // Avatar & Profile Details
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Overlapping Profile Avatar
                            Box(
                                modifier = Modifier
                                    .offset(y = (-45).dp)
                                    .clickable { showEditDialog = true },
                                contentAlignment = Alignment.BottomEnd
                            ) {
                                MemberAvatar(
                                    name = user.fullName,
                                    avatarColor = user.avatarColor,
                                    avatarIcon = user.avatarIcon,
                                    customPhotoUri = user.customPhotoUri,
                                    size = 88.dp
                                )
                                // Camera icon badge
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(FbBluePrimary)
                                        .border(2.dp, Color.White, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CameraAlt,
                                        contentDescription = "Ganti Foto",
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(-32.dp))

                            Text(
                                text = user.fullName,
                                color = FbTextPrimary,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                RoleBadge(user.role)
                                if (user.studentNumber.isNotBlank()) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = FbSurfaceVariant
                                    ) {
                                        Text(
                                            text = user.studentNumber,
                                            color = FbTextSecondary,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Username: @${user.username}",
                                color = FbTextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal
                            )

                            if (user.bio.isNotBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = FbBlueLight.copy(alpha = 0.5f),
                                    border = BorderStroke(0.5.dp, FbBluePrimary.copy(alpha = 0.2f))
                                ) {
                                    Text(
                                        text = "\"${user.bio}\"",
                                        color = FbTextPrimary,
                                        fontSize = 12.sp,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Action buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { showEditDialog = true },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = FbBluePrimary,
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f).testTag("btn_edit_profile")
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Edit Profil & Foto", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }

                                OutlinedButton(
                                    onClick = { viewModel.setShowUploadSheet(true) },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = FbBluePrimary),
                                    border = BorderStroke(1.dp, FbBluePrimary),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Buat Postingan", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }
            }

            // Admin-Only Exclusive Modules: Kelola Kuis & Jurnal Pembelajaran
            if (user.role == UserRole.WALI_KELAS.name) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                        border = BorderStroke(1.5.dp, Color(0xFF86EFAC)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.AdminPanelSettings,
                                        contentDescription = null,
                                        tint = Color(0xFF059669),
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Panel Khusus Admin / Wali Kelas",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 14.5.sp,
                                        color = Color(0xFF065F46)
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFF059669)
                                ) {
                                    Text(
                                        text = "KHUSUS ADMIN",
                                        color = Color.White,
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Quiz Manager Item
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White,
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.setShowAdminQuizManager(true) }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFE0F2FE)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Quiz, contentDescription = null, tint = FbBluePrimary, modifier = Modifier.size(22.dp))
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Kelola Kuis & Ulangan Harian", fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = FbTextPrimary)
                                        Text("Buat soal, pantau jawaban siswa & nilai", fontSize = 11.5.sp, color = FbTextSecondary)
                                    }
                                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = FbTextSecondary)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Journal Manager Item
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White,
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.setShowAdminJournalManager(true) }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFFEF3C7)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.AutoStories, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(22.dp))
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Jurnal Pembelajaran Kelas", fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = FbTextPrimary)
                                        Text("Catatan guru, bimbingan, prestasi & pembinaan siswa", fontSize = 11.5.sp, color = FbTextSecondary)
                                    }
                                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = FbTextSecondary)
                                }
                            }
                        }
                    }
                }
            }

            // Stats Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = FbCardSurface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        border = BorderStroke(1.dp, FbBorder.copy(alpha = 0.4f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.PhotoLibrary,
                                contentDescription = null,
                                tint = FbBluePrimary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${userPhotos.size}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = FbTextPrimary
                            )
                            Text("Postingan Saya", fontSize = 11.sp, color = FbTextSecondary)
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = FbCardSurface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        border = BorderStroke(1.dp, FbBorder.copy(alpha = 0.4f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.ThumbUp,
                                contentDescription = null,
                                tint = FbBluePrimary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$totalLikes",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = FbTextPrimary
                            )
                            Text("Total Suka Diterima", fontSize = 11.sp, color = FbTextSecondary)
                        }
                    }
                }
            }

            // Section Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Postingan & Galeri Saya (${userPhotos.size})",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = FbTextPrimary
                    )
                    if (userPhotos.isNotEmpty()) {
                        TextButton(
                            onClick = {
                                viewModel.setFilterStudent(user.id)
                            }
                        ) {
                            Text("Lihat di Beranda", fontSize = 12.sp, color = FbBluePrimary)
                        }
                    }
                }
            }

            // User's Photos List
            if (userPhotos.isEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = FbCardSurface,
                        border = BorderStroke(1.dp, FbBorder.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.AddAPhoto,
                                contentDescription = null,
                                tint = FbBluePrimary,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Kamu belum membuat postingan",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = FbTextPrimary
                            )
                            Text(
                                text = "Bagikan status, foto kegiatan, atau video kelas sekarang!",
                                fontSize = 12.sp,
                                color = FbTextSecondary,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { viewModel.setShowUploadSheet(true) },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = FbBluePrimary)
                            ) {
                                Text("Buat Postingan Baru", fontSize = 13.sp)
                            }
                        }
                    }
                }
            } else {
                items(userPhotos, key = { it.id }) { photo ->
                    FacebookPostCard(
                        photo = photo,
                        currentUser = user,
                        comments = emptyList(),
                        viewModel = viewModel,
                        onPhotoClick = { viewModel.selectPhoto(photo) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileDialog(
    user: UserEntity,
    viewModel: GalleryViewModel,
    onDismiss: () -> Unit,
    onSave: (
        name: String,
        username: String,
        password: String,
        studentNumber: String,
        avatarColor: Long,
        avatarIcon: String,
        bio: String,
        customPhotoUri: String
    ) -> Unit
) {
    val context = LocalContext.current
    var editFullName by remember { mutableStateOf(user.fullName) }
    var editUsername by remember { mutableStateOf(user.username) }
    var editPassword by remember { mutableStateOf(user.password) }
    var showPassword by remember { mutableStateOf(false) }
    var editStudentNumber by remember { mutableStateOf(user.studentNumber) }
    var editAvatarColor by remember { mutableStateOf(user.avatarColor) }
    var editAvatarIcon by remember { mutableStateOf(user.avatarIcon) }
    var editBio by remember { mutableStateOf(user.bio) }
    var editCustomPhotoUri by remember { mutableStateOf(user.customPhotoUri) }

    // Gallery Picker Launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.saveProfilePhotoUri(uri) { savedPath ->
                editCustomPhotoUri = savedPath
                Toast.makeText(context, "Foto profil berhasil dimuat!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Camera Launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            viewModel.saveProfilePhotoBitmap(bitmap) { savedPath ->
                editCustomPhotoUri = savedPath
                Toast.makeText(context, "Foto kamera berhasil disimpan!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            try {
                cameraLauncher.launch(null)
            } catch (e: Exception) {
                Toast.makeText(context, "Tidak dapat membuka kamera: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Izin kamera diperlukan untuk mengambil foto profil", Toast.LENGTH_SHORT).show()
        }
    }

    val launchCameraSafely = {
        val permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            try {
                cameraLauncher.launch(null)
            } catch (e: Exception) {
                Toast.makeText(context, "Gagal membuka kamera: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AccountCircle, contentDescription = null, tint = FbBluePrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Edit Profil & Akun", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(androidx.compose.foundation.rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Section: Foto Profil Sendiri
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = FbBlueLight.copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, FbBluePrimary.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Foto Profil Sendiri",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = FbBluePrimary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Live Avatar Preview
                        Box(
                            modifier = Modifier.size(76.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            MemberAvatar(
                                fullName = editFullName,
                                avatarColor = editAvatarColor,
                                avatarIcon = editAvatarIcon,
                                customPhotoUri = editCustomPhotoUri,
                                size = 76.dp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            FilledTonalButton(
                                onClick = { launchCameraSafely() },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Kamera", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            FilledTonalButton(
                                onClick = { galleryLauncher.launch("image/*") },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Galeri HP", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        if (editCustomPhotoUri.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            TextButton(
                                onClick = { editCustomPhotoUri = "" },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Icon(Icons.Default.DeleteOutline, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Hapus Foto (Gunakan Avatar)", color = Color(0xFFDC2626), fontSize = 11.sp)
                            }
                        }
                    }
                }

                // Section: Akun & Login
                Text("Informasi Akun & Login", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = FbTextPrimary)

                OutlinedTextField(
                    value = editUsername,
                    onValueChange = { editUsername = it },
                    label = { Text("Username") },
                    leadingIcon = { Icon(Icons.Default.AlternateEmail, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = editPassword,
                    onValueChange = { editPassword = it },
                    label = { Text("Password / Kata Sandi") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (showPassword) "Sembunyikan" else "Lihat"
                            )
                        }
                    },
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // Section: Profil
                Text("Biodata Profil", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = FbTextPrimary)

                OutlinedTextField(
                    value = editFullName,
                    onValueChange = { editFullName = it },
                    label = { Text("Nama Lengkap") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = editStudentNumber,
                    onValueChange = { editStudentNumber = it },
                    label = { Text("Tingkat Kelas / Posisi") },
                    placeholder = { Text("Contoh: Kelas 4 atau Guru") },
                    leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = editBio,
                    onValueChange = { editBio = it },
                    label = { Text("Motto / Bio Singkat") },
                    leadingIcon = { Icon(Icons.Default.FormatQuote, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // Section: Avatar Fallback Selection
                Text("Pilihan Avatar Kartun (Alternatif):", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = FbTextSecondary)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(AVATAR_OPTIONS) { (iconKey, iconLabel) ->
                        val isSelected = editAvatarIcon == iconKey && editCustomPhotoUri.isBlank()
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color(editAvatarColor))
                                .border(
                                    width = if (isSelected) 3.dp else 1.dp,
                                    color = if (isSelected) FbBluePrimary else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable {
                                    editAvatarIcon = iconKey
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = getAvatarIconVector(iconKey),
                                contentDescription = iconLabel,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Text("Pilihan Warna Avatar:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = FbTextSecondary)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(COLOR_OPTIONS) { (colorHex, colorName) ->
                        val isSelected = editAvatarColor == colorHex
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color(colorHex))
                                .border(
                                    width = if (isSelected) 3.dp else 1.dp,
                                    color = if (isSelected) Color.Black else Color.White,
                                    shape = CircleShape
                                )
                                .clickable { editAvatarColor = colorHex },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = colorName,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (editFullName.isBlank()) {
                        Toast.makeText(context, "Nama lengkap tidak boleh kosong", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    onSave(
                        editFullName,
                        editUsername,
                        editPassword,
                        editStudentNumber,
                        editAvatarColor,
                        editAvatarIcon,
                        editBio,
                        editCustomPhotoUri
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = FbBluePrimary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Simpan Perubahan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

