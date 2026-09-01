package com.example.ui.screens

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import coil.compose.AsyncImage
import com.example.data.model.PhotoCategory
import com.example.ui.components.PRESET_TEMPLATES
import com.example.ui.components.PresetIllustration
import com.example.ui.viewmodel.GalleryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadPhotoSheet(
    viewModel: GalleryViewModel,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val allUsers by viewModel.allUsers.collectAsState()

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(PhotoCategory.BELAJAR) }
    var selectedPresetKey by remember { mutableStateOf<String?>("preset_science") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var taggedStudents by remember { mutableStateOf<Set<String>>(emptySet()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            capturedBitmap = bitmap
            selectedImageUri = null
            selectedPresetKey = null
        }
    }

    // Gallery picker launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            capturedBitmap = null
            selectedPresetKey = null
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📸 Unggah Foto ke Galeri SDN 4",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Tutup")
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Error banner if any
            if (errorMessage != null) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFFEE2E2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Text(
                        text = errorMessage!!,
                        color = Color(0xFFDC2626),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

            // Photo Preview Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                when {
                    capturedBitmap != null -> {
                        AsyncImage(
                            model = capturedBitmap,
                            contentDescription = "Preview kamera",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    selectedImageUri != null -> {
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = "Preview galeri",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    selectedPresetKey != null -> {
                        PresetIllustration(
                            presetKey = selectedPresetKey!!,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    else -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.AddPhotoAlternate,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(44.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Pilih sumber foto di bawah",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Source Selector Buttons (Kamera, Galeri HP, Ilustrasi)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { cameraLauncher.launch(null) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_source_camera"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Kamera", fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = { galleryLauncher.launch("image/*") },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_source_gallery"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Galeri HP", fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Or choose from Classroom Presets
            Text(
                text = "Atau pilih Gambar Kegiatan Sekolah:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                items(PRESET_TEMPLATES) { template ->
                    val isSelected = selectedPresetKey == template.key && selectedImageUri == null && capturedBitmap == null
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
                        modifier = Modifier
                            .clickable {
                                selectedPresetKey = template.key
                                selectedImageUri = null
                                capturedBitmap = null
                                when (template.key) {
                                    "preset_science" -> selectedCategory = PhotoCategory.BELAJAR
                                    "preset_art" -> selectedCategory = PhotoCategory.KARYA_SENI
                                    "preset_sports" -> selectedCategory = PhotoCategory.OLAHRAGA
                                    "preset_scout" -> selectedCategory = PhotoCategory.PRAMUKA
                                    "preset_trophy" -> selectedCategory = PhotoCategory.PRESTASI
                                    "preset_event" -> selectedCategory = PhotoCategory.KEGIATAN_BERSAMA
                                }
                            }
                            .testTag("preset_item_${template.key}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                template.icon,
                                contentDescription = null,
                                tint = template.color,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = template.title,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Inputs
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Judul Foto / Kegiatan *") },
                placeholder = { Text("Contoh: Praktik Menanam Biji Kacang") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("upload_title_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Keterangan / Cerita Foto") },
                placeholder = { Text("Ceritakan keseruan momen ini...") },
                maxLines = 3,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("upload_desc_input")
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Category selector
            Text(
                text = "Pilih Album Kategori:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                items(PhotoCategory.values().filter { it != PhotoCategory.SEMUA }) { cat ->
                    val isSelected = selectedCategory == cat
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat.title, fontSize = 11.sp) },
                        modifier = Modifier.testTag("upload_category_${cat.name}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Tag Classmates
            Text(
                text = "Tandai Teman Sekelas di Foto:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                items(allUsers) { user ->
                    val isTagged = taggedStudents.contains(user.fullName)
                    FilterChip(
                        selected = isTagged,
                        onClick = {
                            taggedStudents = if (isTagged) {
                                taggedStudents - user.fullName
                            } else {
                                taggedStudents + user.fullName
                            }
                        },
                        label = { Text(user.fullName.split(" ").firstOrNull() ?: user.fullName, fontSize = 11.sp) },
                        leadingIcon = if (isTagged) {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(12.dp)) }
                        } else null
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Submit Button
            Button(
                onClick = {
                    if (title.isBlank()) {
                        errorMessage = "Judul foto wajib diisi"
                        return@Button
                    }
                    val taggedString = taggedStudents.joinToString(", ")

                    when {
                        capturedBitmap != null -> {
                            viewModel.uploadPhotoWithBitmap(
                                title = title,
                                description = description,
                                category = selectedCategory,
                                bitmap = capturedBitmap!!,
                                taggedStudentNames = taggedString,
                                onSuccess = onDismiss
                            )
                        }
                        selectedImageUri != null -> {
                            viewModel.uploadPhotoWithUri(
                                title = title,
                                description = description,
                                category = selectedCategory,
                                uri = selectedImageUri!!,
                                taggedStudentNames = taggedString,
                                onSuccess = onDismiss
                            )
                        }
                        selectedPresetKey != null -> {
                            viewModel.uploadPhotoWithPreset(
                                title = title,
                                description = description,
                                category = selectedCategory,
                                presetKey = selectedPresetKey!!,
                                taggedStudentNames = taggedString,
                                onSuccess = onDismiss
                            )
                        }
                        else -> {
                            errorMessage = "Silakan pilih foto terlebih dahulu"
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("upload_submit_button"),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.CloudUpload, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Unggah Sekarang", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
