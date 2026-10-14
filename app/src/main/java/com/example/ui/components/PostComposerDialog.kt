package com.example.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import com.example.util.YouTubeHelper
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.example.data.model.PhotoCategory
import com.example.data.model.PostType
import com.example.data.model.UserEntity
import com.example.ui.viewmodel.GalleryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostComposerDialog(
    currentUser: UserEntity?,
    initialPostType: PostType = PostType.TEXT_STATUS,
    viewModel: GalleryViewModel,
    onDismiss: () -> Unit
) {
    var selectedPostType by remember { mutableStateOf(initialPostType) }

    // Common fields
    var title by remember { mutableStateOf("") }
    var contentText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(PhotoCategory.BELAJAR) }
    var selectedFeeling by remember { mutableStateOf("") }
    var taggedStudents by remember { mutableStateOf(listOf<String>()) }

    // Status Post fields
    var selectedGradientKey by remember { mutableStateOf("gradient_sunset") }

    // Photo Post fields
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedPhotoPresetKey by remember { mutableStateOf("preset_science") }
    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }

    // Video Post fields
    var selectedVideoUri by remember { mutableStateOf<Uri?>(null) }
    var selectedVideoPresetKey by remember { mutableStateOf("video_science") }
    var videoDurationSec by remember { mutableIntStateOf(45) }
    var youtubeUrlInput by remember { mutableStateOf("") }

    // Audio / Music Post fields
    var selectedAudioUri by remember { mutableStateOf<Uri?>(null) }
    var selectedAudioPresetKey by remember { mutableStateOf("") }
    var audioArtistName by remember { mutableStateOf(currentUser?.fullName ?: "SDN 4 Putrajawa") }
    var audioDurationSec by remember { mutableIntStateOf(90) }
    var audioFileName by remember { mutableStateOf("") }

    // Pickers
    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            capturedBitmap = null
            selectedPhotoPresetKey = ""
        }
    }
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            capturedBitmap = bitmap
            selectedImageUri = null
            selectedPhotoPresetKey = ""
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
            Toast.makeText(context, "Izin kamera diperlukan untuk mengambil foto", Toast.LENGTH_SHORT).show()
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
    val videoPickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            selectedVideoUri = uri
            selectedVideoPresetKey = ""
        }
    }
    val audioPickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            selectedAudioUri = uri
            selectedAudioPresetKey = ""
            audioFileName = uri.lastPathSegment ?: "audio_file.mp3"
        }
    }

    var showFeelingSheet by remember { mutableStateOf(false) }
    var showTagSheet by remember { mutableStateOf(false) }

    val feelingsList = listOf(
        "🌟 merasa bersemangat",
        "📐 sedang belajar Matematika",
        "🔬 sedang eksperimen Sains",
        "🎨 sedang melukis & berkarya",
        "⚽ sedang main futsal",
        "⛺ sedang latihan Pramuka",
        "🏆 merasa bangga juara",
        "🧹 sedang piket kelas",
        "📖 sedang membaca di perpus",
        "😊 merasa senang & gembira"
    )

    val classmateNames = listOf(
        "Dimas Aditya Pratama",
        "Siti Nurhaliza",
        "Rizky Alfaridzi",
        "Zahra Aulia Putri",
        "Farhan Maulana",
        "Semua Siswa SDN 4 Putrajawa"
    )

    val gradients = listOf(
        Pair("none", "Polos"),
        Pair("gradient_sunset", "🔥 Sunset"),
        Pair("gradient_ocean", "🌊 Laut"),
        Pair("gradient_emerald", "🌿 Zamrud"),
        Pair("gradient_purple", "🔮 Ungu"),
        Pair("gradient_fire", "⚡ Api"),
        Pair("gradient_blue_purple", "🌸 Neon")
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
            ) {
                // Top Bar
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 2.dp,
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = onDismiss) {
                                Icon(Icons.Default.Close, contentDescription = "Tutup")
                            }
                            Text(
                                text = "Buat Postingan",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Button(
                            onClick = {
                                when (selectedPostType) {
                                    PostType.TEXT_STATUS -> {
                                        val tags = taggedStudents.joinToString(", ")
                                        val detectedYt = YouTubeHelper.extractEmbedUrl(youtubeUrlInput).takeIf { !it.isNullOrBlank() }
                                            ?: YouTubeHelper.extractEmbedUrl(contentText)
                                            ?: YouTubeHelper.extractEmbedUrl(title)

                                        if (detectedYt != null) {
                                            viewModel.createVideoPost(
                                                title = if (title.isBlank()) "Video YouTube" else title.trim(),
                                                description = contentText.trim(),
                                                category = selectedCategory,
                                                videoUri = null,
                                                youtubeUrl = detectedYt,
                                                videoPresetKey = "",
                                                videoDurationSec = 0,
                                                feeling = selectedFeeling,
                                                taggedStudentNames = tags,
                                                onSuccess = onDismiss
                                            )
                                        } else if (contentText.isNotBlank()) {
                                            viewModel.createStatusPost(
                                                text = contentText,
                                                bgGradientKey = if (selectedGradientKey == "none") "" else selectedGradientKey,
                                                feeling = selectedFeeling,
                                                category = selectedCategory,
                                                taggedStudentNames = tags,
                                                onSuccess = onDismiss
                                            )
                                        }
                                    }
                                    PostType.PHOTO -> {
                                        val finalTitle = if (title.isBlank()) "Foto Kegiatan Kelas 4" else title
                                        val finalDesc = contentText
                                        val tags = taggedStudents.joinToString(", ")
                                        when {
                                            selectedImageUri != null -> {
                                                viewModel.uploadPhotoWithUri(
                                                    title = finalTitle,
                                                    description = finalDesc,
                                                    category = selectedCategory,
                                                    uri = selectedImageUri!!,
                                                    feeling = selectedFeeling,
                                                    taggedStudentNames = tags,
                                                    onSuccess = onDismiss
                                                )
                                            }
                                            capturedBitmap != null -> {
                                                viewModel.uploadPhotoWithBitmap(
                                                    title = finalTitle,
                                                    description = finalDesc,
                                                    category = selectedCategory,
                                                    bitmap = capturedBitmap!!,
                                                    feeling = selectedFeeling,
                                                    taggedStudentNames = tags,
                                                    onSuccess = onDismiss
                                                )
                                            }
                                            else -> {
                                                viewModel.uploadPhotoWithPreset(
                                                    title = finalTitle,
                                                    description = finalDesc,
                                                    category = selectedCategory,
                                                    presetKey = selectedPhotoPresetKey,
                                                    feeling = selectedFeeling,
                                                    taggedStudentNames = tags,
                                                    onSuccess = onDismiss
                                                )
                                            }
                                        }
                                    }
                                    PostType.VIDEO -> {
                                        val detectedYt = YouTubeHelper.extractEmbedUrl(youtubeUrlInput).takeIf { !it.isNullOrBlank() }
                                            ?: YouTubeHelper.extractEmbedUrl(contentText)
                                            ?: YouTubeHelper.extractEmbedUrl(title)
                                        val finalTitle = if (title.isBlank()) {
                                            if (detectedYt != null) "Video YouTube" else "Video Kegiatan SDN 4"
                                        } else title
                                        val finalDesc = contentText
                                        val tags = taggedStudents.joinToString(", ")
                                        viewModel.createVideoPost(
                                            title = finalTitle,
                                            description = finalDesc,
                                            category = selectedCategory,
                                            videoUri = selectedVideoUri,
                                            youtubeUrl = detectedYt ?: youtubeUrlInput.trim(),
                                            videoPresetKey = if (detectedYt != null || selectedVideoUri != null) "" else selectedVideoPresetKey,
                                            videoDurationSec = if (detectedYt != null) 0 else videoDurationSec,
                                            feeling = selectedFeeling,
                                            taggedStudentNames = tags,
                                            onSuccess = onDismiss
                                        )
                                    }
                                    PostType.AUDIO -> {
                                        val finalTitle = if (title.isBlank()) "Lagu & Musik SDN 4 Putrajawa" else title
                                        val finalDesc = contentText
                                        val tags = taggedStudents.joinToString(", ")
                                        val finalArtist = if (audioArtistName.isBlank()) currentUser?.fullName ?: "SDN 4 Putrajawa" else audioArtistName
                                        viewModel.createAudioPost(
                                            title = finalTitle,
                                            description = finalDesc,
                                            audioArtist = finalArtist,
                                            category = selectedCategory,
                                            audioUri = selectedAudioUri,
                                            audioPresetKey = selectedAudioPresetKey,
                                            audioDurationSec = audioDurationSec,
                                            feeling = selectedFeeling,
                                            taggedStudentNames = tags,
                                            onSuccess = onDismiss
                                        )
                                    }
                                }
                            },
                            enabled = when (selectedPostType) {
                                PostType.TEXT_STATUS -> contentText.isNotBlank() || youtubeUrlInput.isNotBlank()
                                PostType.PHOTO -> (selectedImageUri != null || capturedBitmap != null || selectedPhotoPresetKey.isNotBlank())
                                PostType.VIDEO -> (selectedVideoUri != null || selectedVideoPresetKey.isNotBlank() || youtubeUrlInput.isNotBlank() || YouTubeHelper.isYouTubeUrl(contentText) || YouTubeHelper.isYouTubeUrl(title))
                                PostType.AUDIO -> (selectedAudioUri != null || selectedAudioPresetKey.isNotBlank() || title.isNotBlank())
                            },
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Posting", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Author Header & Privacy selector
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MemberAvatar(
                        fullName = currentUser?.fullName ?: "Warga Sekolah",
                        avatarColor = currentUser?.avatarColor ?: 0xFF4F46E5,
                        avatarIcon = currentUser?.avatarIcon ?: "star",
                        size = 46.dp
                    )

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = currentUser?.fullName ?: "Warga SDN 4",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (selectedFeeling.isNotBlank()) {
                                Text(
                                    text = "— $selectedFeeling",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Audience pill
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.Public, contentDescription = "Publik", modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.primary)
                                Text(
                                    text = "Seluruh Warga SDN 4 Putrajawa",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Post Type Switcher Tabs (Status | Foto | Video | Musik)
                TabRow(
                    selectedTabIndex = when (selectedPostType) {
                        PostType.TEXT_STATUS -> 0
                        PostType.PHOTO -> 1
                        PostType.VIDEO -> 2
                        PostType.AUDIO -> 3
                    },
                    modifier = Modifier.padding(horizontal = 16.dp),
                    containerColor = Color.Transparent,
                    divider = {}
                ) {
                    Tab(
                        selected = selectedPostType == PostType.TEXT_STATUS,
                        onClick = { selectedPostType = PostType.TEXT_STATUS },
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.EditNote, contentDescription = null, modifier = Modifier.size(16.dp))
                                Text("Status", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    )
                    Tab(
                        selected = selectedPostType == PostType.PHOTO,
                        onClick = { selectedPostType = PostType.PHOTO },
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(16.dp))
                                Text("Foto", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    )
                    Tab(
                        selected = selectedPostType == PostType.VIDEO,
                        onClick = { selectedPostType = PostType.VIDEO },
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(16.dp))
                                Text("Video", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    )
                    Tab(
                        selected = selectedPostType == PostType.AUDIO,
                        onClick = { selectedPostType = PostType.AUDIO },
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.MusicNote, contentDescription = null, modifier = Modifier.size(16.dp))
                                Text("Musik", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Scrollable Content Area
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp)
                ) {
                    when (selectedPostType) {
                        PostType.TEXT_STATUS -> {
                            // Status Teks Composer
                            if (selectedGradientKey != "none") {
                                val brush = getGradientForKey(selectedGradientKey)
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(brush)
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    OutlinedTextField(
                                        value = contentText,
                                        onValueChange = { contentText = it },
                                        placeholder = {
                                            Text(
                                                "Apa yang sedang kamu pikirkan, ${currentUser?.fullName?.split(" ")?.firstOrNull() ?: "teman"}?",
                                                color = Color.White.copy(alpha = 0.7f),
                                                textAlign = TextAlign.Center,
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        },
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = Color.White,
                                            unfocusedTextColor = Color.White,
                                            focusedBorderColor = Color.Transparent,
                                            unfocusedBorderColor = Color.Transparent,
                                            cursorColor = Color.White
                                        ),
                                        textStyle = androidx.compose.ui.text.TextStyle(
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.Center,
                                            color = Color.White
                                        ),
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            } else {
                                OutlinedTextField(
                                    value = contentText,
                                    onValueChange = { contentText = it },
                                    placeholder = {
                                        Text(
                                            "Apa yang sedang kamu pikirkan, ${currentUser?.fullName?.split(" ")?.firstOrNull() ?: "teman"}?",
                                            fontSize = 16.sp
                                        )
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(min = 140.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color.Transparent,
                                        unfocusedBorderColor = Color.Transparent
                                    )
                                )
                            }

                            val statusYtId = YouTubeHelper.extractVideoId(contentText)
                            if (statusYtId != null) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                                    border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(width = 80.dp, height = 48.dp)
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(Color.Black),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            androidx.compose.foundation.Image(
                                                painter = coil.compose.rememberAsyncImagePainter("https://img.youtube.com/vi/$statusYtId/hqdefault.jpg"),
                                                contentDescription = "Thumbnail YouTube",
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                            )
                                            Icon(
                                                imageVector = Icons.Default.PlayCircle,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                        Column(modifier = Modifier.weight(1f)) {
                                            Surface(
                                                color = Color(0xFFDC2626),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    text = "LINK YOUTUBE TERDETEKSI",
                                                    color = Color.White,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = "Otomatis disematkan sebagai video YouTube yang dapat diputar langsung!",
                                                fontSize = 11.sp,
                                                color = Color(0xFF991B1B),
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Background Gradients Selector
                            Text(
                                text = "Pilih Latar Belakang Status:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(gradients) { (key, label) ->
                                    val isSelected = selectedGradientKey == key
                                    Surface(
                                        onClick = { selectedGradientKey = key },
                                        shape = RoundedCornerShape(12.dp),
                                        border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
                                        color = if (key == "none") MaterialTheme.colorScheme.surfaceVariant else Color.Transparent,
                                        modifier = Modifier.size(width = 75.dp, height = 48.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .then(if (key != "none") Modifier.background(getGradientForKey(key)) else Modifier),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = label,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (key == "none") MaterialTheme.colorScheme.onSurface else Color.White
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        PostType.PHOTO -> {
                            // Photo Composer
                            OutlinedTextField(
                                value = title,
                                onValueChange = { title = it },
                                label = { Text("Judul Foto / Kegiatan") },
                                placeholder = { Text("Contoh: Hasil Karya Seni Origami") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = contentText,
                                onValueChange = { contentText = it },
                                label = { Text("Keterangan Foto") },
                                placeholder = { Text("Ceritakan keseruan momen ini...") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                minLines = 2
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Photo Source Actions
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { launchCameraSafely() },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Kamera", fontSize = 12.sp)
                                }

                                OutlinedButton(
                                    onClick = { imagePickerLauncher.launch("image/*") },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Galeri HP", fontSize = 12.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Preset Educational Illustrations Catalog
                            Text(
                                text = "Atau Pilih Ilustrasi Seni & Edukasi Kelas 4:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            val photoPresets = listOf(
                                Pair("preset_science", "Sains IPA"),
                                Pair("preset_art", "Karya Seni"),
                                Pair("preset_sports", "Olahraga"),
                                Pair("preset_scout", "Pramuka"),
                                Pair("preset_trophy", "Prestasi"),
                                Pair("preset_event", "Kebersamaan")
                            )

                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(photoPresets) { (pKey, pTitle) ->
                                    val isSelected = selectedPhotoPresetKey == pKey && selectedImageUri == null && capturedBitmap == null
                                    Card(
                                        modifier = Modifier
                                            .width(110.dp)
                                            .height(95.dp)
                                            .clickable {
                                                selectedPhotoPresetKey = pKey
                                                selectedImageUri = null
                                                capturedBitmap = null
                                            },
                                        shape = RoundedCornerShape(12.dp),
                                        border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
                                    ) {
                                        Box(modifier = Modifier.fillMaxSize()) {
                                            PresetPhotoCanvas(presetKey = pKey, modifier = Modifier.fillMaxSize())
                                            Surface(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .align(Alignment.BottomCenter),
                                                color = Color.Black.copy(alpha = 0.65f)
                                            ) {
                                                Text(
                                                    text = pTitle,
                                                    color = Color.White,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    textAlign = TextAlign.Center,
                                                    modifier = Modifier.padding(2.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        PostType.VIDEO -> {
                            // Video Composer
                            // YouTube Link Box (Copas Link YouTube)
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                                border = BorderStroke(1.5.dp, Color(0xFFFCA5A5)),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Surface(
                                                color = Color(0xFFDC2626),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    text = "YT",
                                                    color = Color.White,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                )
                                            }
                                            Text(
                                                text = "Posting dengan Link YouTube",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = Color(0xFF991B1B)
                                            )
                                        }

                                        // Quick Paste from Clipboard button
                                        Button(
                                            onClick = {
                                                val clip = clipboardManager.getText()?.text
                                                if (!clip.isNullOrBlank()) {
                                                    youtubeUrlInput = clip.trim()
                                                    val extracted = YouTubeHelper.extractVideoId(clip)
                                                    if (extracted != null) {
                                                        Toast.makeText(context, "Link YouTube berhasil ditempel!", Toast.LENGTH_SHORT).show()
                                                    } else {
                                                        Toast.makeText(context, "Teks dari clipboard telah ditempel", Toast.LENGTH_SHORT).show()
                                                    }
                                                } else {
                                                    Toast.makeText(context, "Clipboard kosong, salin link dari YouTube terlebih dahulu", Toast.LENGTH_SHORT).show()
                                                }
                                            },
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ContentPaste,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "Tempel Link",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedTextField(
                                        value = youtubeUrlInput,
                                        onValueChange = { 
                                            youtubeUrlInput = it
                                            if (it.isNotBlank()) {
                                                selectedVideoPresetKey = ""
                                                selectedVideoUri = null
                                            }
                                        },
                                        label = { Text("Tautan Video YouTube (Link URL)") },
                                        placeholder = { Text("Contoh: https://www.youtube.com/watch?v=... atau https://youtu.be/...") },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(10.dp),
                                        singleLine = true,
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.PlayCircle,
                                                contentDescription = null,
                                                tint = Color(0xFFDC2626)
                                            )
                                        },
                                        trailingIcon = {
                                            if (youtubeUrlInput.isNotBlank()) {
                                                IconButton(onClick = { youtubeUrlInput = "" }) {
                                                    Icon(Icons.Default.Close, contentDescription = "Hapus URL", modifier = Modifier.size(16.dp))
                                                }
                                            }
                                        },
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedContainerColor = Color.White,
                                            unfocusedContainerColor = Color.White,
                                            focusedBorderColor = Color(0xFFDC2626),
                                            unfocusedBorderColor = Color(0xFFFCA5A5)
                                        )
                                    )

                                    val detectedYtId = YouTubeHelper.extractVideoId(youtubeUrlInput)
                                        ?: YouTubeHelper.extractVideoId(contentText)
                                        ?: YouTubeHelper.extractVideoId(title)

                                    if (detectedYtId != null) {
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Card(
                                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(8.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(width = 96.dp, height = 58.dp)
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(Color.Black),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    androidx.compose.foundation.Image(
                                                        painter = coil.compose.rememberAsyncImagePainter("https://img.youtube.com/vi/$detectedYtId/hqdefault.jpg"),
                                                        contentDescription = "Preview Thumbnail",
                                                        modifier = Modifier.fillMaxSize(),
                                                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                                    )
                                                    Icon(
                                                        imageVector = Icons.Default.PlayCircle,
                                                        contentDescription = null,
                                                        tint = Color.White,
                                                        modifier = Modifier.size(24.dp)
                                                    )
                                                }

                                                Column(modifier = Modifier.weight(1f)) {
                                                    Surface(
                                                        color = Color(0xFFDC2626),
                                                        shape = RoundedCornerShape(4.dp)
                                                    ) {
                                                        Text(
                                                            text = "YOUTUBE TERSEDIA",
                                                            color = Color.White,
                                                            fontSize = 9.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                        )
                                                    }
                                                    Spacer(modifier = Modifier.height(2.dp))
                                                    Text(
                                                        text = "Video ID: $detectedYtId",
                                                        color = Color(0xFFE2E8F0),
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                    Text(
                                                        text = "Video dapat diputar langsung di aplikasi Android!",
                                                        color = Color(0xFF94A3B8),
                                                        fontSize = 10.sp
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = title,
                                onValueChange = { title = it },
                                label = { Text("Judul Video") },
                                placeholder = { Text("Contoh: Praktik Rangkaian Listrik IPA") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = contentText,
                                onValueChange = { contentText = it },
                                label = { Text("Deskripsi Video") },
                                placeholder = { Text("Jelaskan video dokumentasi kelas ini...") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                minLines = 2
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Atau Gunakan Rekaman HP / Animasi Kelas:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            // Video file picker
                            OutlinedButton(
                                onClick = { videoPickerLauncher.launch("video/*") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.VideoFile, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Pilih File Video dari HP / Rekaman")
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Video Presets Catalog with instant preview!
                            Text(
                                text = "Koleksi Video Animasi & Dokumentasi Kelas 4:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                items(VideoPresetsData.presets) { preset ->
                                    val isSelected = selectedVideoPresetKey == preset.key
                                    Card(
                                        modifier = Modifier
                                            .width(150.dp)
                                            .clickable {
                                                selectedVideoPresetKey = preset.key
                                                videoDurationSec = preset.durationSec
                                                if (title.isBlank()) title = preset.title
                                            },
                                        shape = RoundedCornerShape(12.dp),
                                        border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
                                    ) {
                                        Column {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(85.dp)
                                                    .background(preset.primaryColor)
                                            ) {
                                                PresetVideoCanvasAnimation(
                                                    presetKey = preset.key,
                                                    isPlaying = false,
                                                    progressFraction = 0.2f,
                                                    modifier = Modifier.fillMaxSize()
                                                )
                                                Surface(
                                                    modifier = Modifier
                                                        .align(Alignment.BottomEnd)
                                                        .padding(4.dp),
                                                    color = Color.Black.copy(alpha = 0.7f),
                                                    shape = RoundedCornerShape(4.dp)
                                                ) {
                                                    Text(
                                                        text = "${preset.durationSec}s",
                                                        color = Color.White,
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                    )
                                                }
                                            }

                                            Column(modifier = Modifier.padding(8.dp)) {
                                                Text(
                                                    text = preset.title,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    maxLines = 1
                                                )
                                                Text(
                                                    text = preset.categoryName,
                                                    fontSize = 10.sp,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        PostType.AUDIO -> {
                            Column {
                                OutlinedTextField(
                                    value = title,
                                    onValueChange = { title = it },
                                    label = { Text("Judul Musik / Lagu MP3") },
                                    placeholder = { Text("Contoh: Mars SDN 4 Putrajawa, Lagu Daerah...") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = audioArtistName,
                                    onValueChange = { audioArtistName = it },
                                    label = { Text("Nama Penyanyi / Pengisi Suara (Artis)") },
                                    placeholder = { Text("Contoh: Teten Kurniawan / Paduan Suara SDN 4") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = contentText,
                                    onValueChange = { contentText = it },
                                    label = { Text("Lirik / Catatan Musik") },
                                    placeholder = { Text("Tuliskan lirik lagu, pesan, atau cerita di balik lagu ini...") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    minLines = 2
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Audio file picker from HP
                                OutlinedButton(
                                    onClick = { audioPickerLauncher.launch("audio/*") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.AudioFile, contentDescription = null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (audioFileName.isNotBlank()) "File: $audioFileName" else "Pilih File Musik MP3 dari HP",
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                if (selectedAudioUri != null) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(10.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                            Text(
                                                text = "File MP3 terpasang & siap diunggah",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Audio Presets Catalog (Lagu Sekolah & Karya SDN 4)
                                Text(
                                    text = "Pilihan Musik & Lagu SDN 4 Putrajawa:",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(6.dp))

                                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    items(PresetAudioTracks.tracks) { track ->
                                        val isSelected = selectedAudioPresetKey == track.key
                                        Card(
                                            modifier = Modifier
                                                .width(180.dp)
                                                .clickable {
                                                    selectedAudioPresetKey = track.key
                                                    selectedAudioUri = null
                                                    audioFileName = ""
                                                    audioDurationSec = track.durationSec
                                                    if (title.isBlank() || title.startsWith("Musik")) title = track.title
                                                    audioArtistName = track.artist
                                                    if (contentText.isBlank()) contentText = track.lyricsOrNote
                                                },
                                            shape = RoundedCornerShape(12.dp),
                                            border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
                                        ) {
                                            Column(modifier = Modifier.padding(10.dp)) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    Surface(
                                                        shape = CircleShape,
                                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                                        modifier = Modifier.size(36.dp)
                                                    ) {
                                                        Box(contentAlignment = Alignment.Center) {
                                                            Icon(
                                                                imageVector = Icons.Default.MusicNote,
                                                                contentDescription = null,
                                                                tint = MaterialTheme.colorScheme.primary,
                                                                modifier = Modifier.size(20.dp)
                                                            )
                                                        }
                                                    }
                                                    Column {
                                                        Text(
                                                            text = track.title,
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis
                                                        )
                                                        Text(
                                                            text = track.artist,
                                                            fontSize = 10.sp,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                            maxLines = 1
                                                        )
                                                    }
                                                }
                                                Spacer(modifier = Modifier.height(6.dp))
                                                Surface(
                                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                                    shape = RoundedCornerShape(4.dp)
                                                ) {
                                                    Text(
                                                        text = "${track.genre} • ${track.durationSec}s",
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                        color = MaterialTheme.colorScheme.primary
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Facebook Post Extras: Feeling & Tagging
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Tambahkan ke Postinganmu:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                item {
                                    // YouTube quick action chip
                                    FilterChip(
                                        selected = youtubeUrlInput.isNotBlank() || selectedPostType == PostType.VIDEO,
                                        onClick = {
                                            selectedPostType = PostType.VIDEO
                                            val clip = clipboardManager.getText()?.text
                                            if (youtubeUrlInput.isBlank() && !clip.isNullOrBlank() && YouTubeHelper.isYouTubeUrl(clip)) {
                                                youtubeUrlInput = clip.trim()
                                                Toast.makeText(context, "Link YouTube dari clipboard berhasil ditempel!", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        label = {
                                            Text(
                                                text = if (youtubeUrlInput.isNotBlank()) "YouTube Terpasang" else "Link YouTube",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFDC2626)
                                            )
                                        },
                                        leadingIcon = {
                                            Icon(
                                                Icons.Default.PlayCircle,
                                                contentDescription = "YouTube",
                                                tint = Color(0xFFDC2626),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    )
                                }
                                item {
                                    // Feeling button
                                    FilterChip(
                                        selected = selectedFeeling.isNotBlank(),
                                        onClick = { showFeelingSheet = true },
                                        label = {
                                            Text(
                                                text = if (selectedFeeling.isNotBlank()) selectedFeeling else "Perasaan/Aktivitas",
                                                fontSize = 11.sp
                                            )
                                        },
                                        leadingIcon = {
                                            Icon(Icons.Default.SentimentSatisfiedAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                                        }
                                    )
                                }
                                item {
                                    // Tag friends button
                                    FilterChip(
                                        selected = taggedStudents.isNotEmpty(),
                                        onClick = { showTagSheet = true },
                                        label = {
                                            Text(
                                                text = if (taggedStudents.isNotEmpty()) "${taggedStudents.size} Ditandai" else "Tandai Teman",
                                                fontSize = 11.sp
                                            )
                                        },
                                        leadingIcon = {
                                            Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Category Selector Chips
                    Text(
                        text = "Kategori Kegiatan:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(
                            listOf(
                                PhotoCategory.BELAJAR,
                                PhotoCategory.KARYA_SENI,
                                PhotoCategory.OLAHRAGA,
                                PhotoCategory.PRAMUKA,
                                PhotoCategory.PRESTASI,
                                PhotoCategory.KEGIATAN_BERSAMA
                            )
                        ) { cat ->
                            val isSelected = selectedCategory == cat
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedCategory = cat },
                                label = { Text(cat.displayName, fontSize = 11.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }

    // Feeling selector sheet/dialog
    if (showFeelingSheet) {
        AlertDialog(
            onDismissRequest = { showFeelingSheet = false },
            title = { Text("Bagaimana Perasaan / Sedang Apa?", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    feelingsList.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedFeeling = item
                                    showFeelingSheet = false
                                }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = item, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    selectedFeeling = ""
                    showFeelingSheet = false
                }) {
                    Text("Hapus Pilihan")
                }
            }
        )
    }

    // Tag Classmates dialog
    if (showTagSheet) {
        AlertDialog(
            onDismissRequest = { showTagSheet = false },
            title = { Text("Tandai Warga Sekolah / Teman", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    classmateNames.forEach { name ->
                        val isChecked = taggedStudents.contains(name)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    taggedStudents = if (isChecked) {
                                        taggedStudents - name
                                    } else {
                                        taggedStudents + name
                                    }
                                }
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = { checked ->
                                    taggedStudents = if (checked) taggedStudents + name else taggedStudents - name
                                }
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = name, fontSize = 14.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showTagSheet = false }) {
                    Text("Selesai (${taggedStudents.size})")
                }
            }
        )
    }
}
