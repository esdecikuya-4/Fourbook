package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.data.model.CommentEntity
import com.example.data.model.PhotoCategory
import com.example.data.model.PhotoEntity
import com.example.data.model.UserRole
import com.example.data.repository.GalleryRepository
import com.example.ui.components.MemberAvatar
import com.example.ui.components.PhotoDisplay
import com.example.ui.components.RoleBadge
import com.example.ui.viewmodel.GalleryViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoDetailScreen(
    photo: PhotoEntity,
    viewModel: GalleryViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val comments by viewModel.photoComments.collectAsState()
    var commentText by remember { mutableStateOf("") }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var showEditPhotoDialog by remember { mutableStateOf(false) }
    var editTitle by remember(photo.title) { mutableStateOf(photo.title) }
    var editFeeling by remember(photo.feelingOrActivity) { mutableStateOf(photo.feelingOrActivity) }

    val isLiked = remember(photo.likedByUserIds, currentUser?.id) {
        if (currentUser == null) false
        else GalleryRepository.parseLikedUserIds(photo.likedByUserIds).contains(currentUser!!.id)
    }

    val canDelete = remember(currentUser, photo) {
        currentUser != null && (currentUser!!.id == photo.uploaderId || currentUser!!.role == UserRole.WALI_KELAS.name)
    }

    val formattedDate = remember(photo.createdAt) {
        val sdf = SimpleDateFormat("EEEE, d MMMM yyyy - HH:mm", Locale("id", "ID"))
        sdf.format(Date(photo.createdAt))
    }

    val catEnum = PhotoCategory.values().find { it.name == photo.category } ?: PhotoCategory.BELAJAR

    if (showEditPhotoDialog) {
        AlertDialog(
            onDismissRequest = { showEditPhotoDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Edit Detail Foto", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = editTitle,
                        onValueChange = { editTitle = it },
                        label = { Text("Judul / Caption Foto *") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        maxLines = 5,
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = editFeeling,
                        onValueChange = { editFeeling = it },
                        label = { Text("Aktivitas / Catatan") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editTitle.isNotBlank()) {
                            viewModel.updatePost(photo.id, editTitle, editFeeling)
                            showEditPhotoDialog = false
                        }
                    },
                    enabled = editTitle.isNotBlank()
                ) {
                    Text("Simpan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditPhotoDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("Hapus Foto?") },
            text = { Text("Apakah Anda yakin ingin menghapus '${photo.title}' dari galeri SDN 4 Putrajawa?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmDialog = false
                        viewModel.deletePhoto(photo.id) {
                            onBack()
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Hapus", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Detail Foto Kegiatan", fontSize = 17.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("detail_back_button")
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    if (canDelete) {
                        IconButton(
                            onClick = {
                                editTitle = photo.title
                                editFeeling = photo.feelingOrActivity
                                showEditPhotoDialog = true
                            },
                            modifier = Modifier.testTag("detail_edit_button")
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Edit Foto",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        IconButton(
                            onClick = { showDeleteConfirmDialog = true },
                            modifier = Modifier.testTag("detail_delete_button")
                        ) {
                            Icon(
                                Icons.Default.DeleteOutline,
                                contentDescription = "Hapus Foto",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            // Comment input bottom bar
            Surface(
                tonalElevation = 6.dp,
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (currentUser != null) {
                        MemberAvatar(
                            fullName = currentUser!!.fullName,
                            avatarColor = currentUser!!.avatarColor,
                            avatarIcon = currentUser!!.avatarIcon,
                            customPhotoUri = currentUser!!.customPhotoUri,
                            size = 36.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    OutlinedTextField(
                        value = commentText,
                        onValueChange = { commentText = it },
                        placeholder = { Text("Beri komentar untuk teman...", fontSize = 13.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("comment_input_field"),
                        shape = RoundedCornerShape(20.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            if (commentText.isNotBlank()) {
                                viewModel.addComment(photo.id, commentText)
                                commentText = ""
                            }
                        },
                        enabled = commentText.isNotBlank(),
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                if (commentText.isNotBlank()) MaterialTheme.colorScheme.primary else Color.LightGray.copy(alpha = 0.5f),
                                CircleShape
                            )
                            .testTag("comment_send_button")
                    ) {
                        Icon(
                            Icons.Default.Send,
                            contentDescription = "Kirim",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {
            // Big Photo Display
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .background(Color.Black)
                ) {
                    PhotoDisplay(
                        imageUri = photo.imageUri,
                        presetKey = photo.presetKey,
                        contentDescription = photo.title,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // Photo Metadata Header
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(catEnum.colorHex).copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = catEnum.title,
                                color = Color(catEnum.colorHex),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                        // Like button on detail
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isLiked) Color(0xFFFFE4E6) else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .clickable { viewModel.toggleLike(photo.id) }
                                .testTag("detail_like_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isLiked) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                                    contentDescription = "Suka",
                                    tint = if (isLiked) Color(0xFFE11D48) else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${photo.likeCount} Suka",
                                    color = if (isLiked) Color(0xFFE11D48) else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = photo.title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = formattedDate,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (photo.description.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = photo.description,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 20.sp
                        )
                    }

                    if (photo.taggedStudentNames.isNotBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.People,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Teman di foto: ${photo.taggedStudentNames}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(14.dp))

                    // Uploader info
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        MemberAvatar(
                            fullName = photo.uploaderName,
                            avatarColor = photo.uploaderAvatarColor,
                            avatarIcon = photo.uploaderAvatarIcon,
                            customPhotoUri = photo.uploaderCustomPhotoUri,
                            size = 44.dp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = photo.uploaderName,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                RoleBadge(photo.uploaderRole)
                            }
                            Text(
                                text = "Pengunggah Foto",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "💬 Komentar Teman Sekelas (${comments.size})",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Quick encouragement chips
                    androidx.compose.foundation.lazy.LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val quickPraise = listOf(
                            "👏 Hebat banget!",
                            "👍 Keren sekali!",
                            "🔥 Semangat belajar!",
                            "❤️ Mantap!",
                            "🌟 Luar biasa!",
                            "📚 Rajin terus ya!",
                            "🏆 Juara!"
                        )
                        items(quickPraise) { praise ->
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable {
                                        viewModel.addComment(photo.id, praise)
                                    }
                            ) {
                                Text(
                                    text = praise,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Comments List
            if (comments.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Outlined.ChatBubbleOutline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Belum ada komentar",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Tulis kata-kata semangat atau apresiasi untuk foto ini!",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(comments, key = { it.id }) { comment ->
                    CommentItem(
                        comment = comment,
                        currentUserId = currentUser?.id ?: 0L,
                        canModify = currentUser != null && (currentUser!!.id == comment.userId || currentUser!!.role == UserRole.WALI_KELAS.name),
                        onDelete = { viewModel.deleteComment(comment) },
                        onEdit = { newText -> viewModel.updateComment(comment, newText) }
                    )
                }
            }
        }
    }
}

@Composable
fun CommentItem(
    comment: CommentEntity,
    currentUserId: Long,
    canModify: Boolean,
    onDelete: () -> Unit,
    onEdit: (String) -> Unit
) {
    val formattedTime = remember(comment.createdAt) {
        val sdf = SimpleDateFormat("d MMM, HH:mm", Locale("id", "ID"))
        sdf.format(Date(comment.createdAt))
    }
    val isTeacherComment = comment.userRole == UserRole.WALI_KELAS.name
    var showEditDialog by remember { mutableStateOf(false) }
    var editedText by remember(comment.text) { mutableStateOf(comment.text) }

    val isDark = androidx.compose.foundation.isSystemInDarkTheme()
    val bubbleBg = if (isTeacherComment) {
        if (isDark) Color(0xFF1B3320) else Color(0xFFE8F5E9)
    } else {
        if (isDark) Color(0xFF3A3B3C) else Color(0xFFF0F2F5)
    }
    val bubbleBorder = if (isTeacherComment) {
        if (isDark) Color(0xFF2E7D32) else Color(0xFFA5D6A7)
    } else {
        if (isDark) Color(0xFF4E4F50) else Color(0xFFE4E6EB)
    }
    val authorTextColor = if (isTeacherComment) {
        if (isDark) Color(0xFF86EFAC) else Color(0xFF14532D)
    } else {
        if (isDark) Color(0xFFF0F2F5) else Color(0xFF050505)
    }
    val commentTextColor = if (isTeacherComment) {
        if (isDark) Color(0xFFE2E8F0) else Color(0xFF1E293B)
    } else {
        if (isDark) Color(0xFFE4E6EB) else Color(0xFF1C1E21)
    }

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Edit Komentar", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Komentar oleh ${comment.userName}:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = editedText,
                        onValueChange = { editedText = it },
                        label = { Text("Isi Komentar") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        maxLines = 4,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editedText.isNotBlank()) {
                            onEdit(editedText)
                            showEditDialog = false
                        }
                    },
                    enabled = editedText.isNotBlank()
                ) {
                    Text("Simpan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        MemberAvatar(
            fullName = comment.userName,
            avatarColor = comment.userAvatarColor,
            avatarIcon = if (isTeacherComment) "teacher" else "star",
            customPhotoUri = comment.userCustomPhotoUri,
            size = 32.dp
        )
        Spacer(modifier = Modifier.width(10.dp))
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = bubbleBg,
            border = androidx.compose.foundation.BorderStroke(1.dp, bubbleBorder),
            modifier = Modifier.weight(1f)
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = comment.userName,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = authorTextColor
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        RoleBadge(comment.userRole)
                    }
                    Text(
                        text = formattedTime,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = comment.text,
                    fontSize = 13.sp,
                    color = commentTextColor,
                    lineHeight = 18.sp
                )

                if (canModify) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Edit",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .clickable {
                                    editedText = comment.text
                                    showEditDialog = true
                                }
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Hapus",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.error.copy(alpha = 0.85f),
                            modifier = Modifier
                                .clickable { onDelete() }
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}
