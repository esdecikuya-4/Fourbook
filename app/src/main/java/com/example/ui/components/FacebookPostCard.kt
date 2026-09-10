package com.example.ui.components

import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.CommentEntity
import com.example.data.model.PhotoEntity
import com.example.data.model.PostType
import com.example.data.model.ReactionType
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import com.example.data.repository.GalleryRepository
import com.example.ui.viewmodel.GalleryViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

private val FbBlue = Color(0xFF1877F2)
private val FbGrayBg = Color(0xFFF0F2F5)
private val FbBorder = Color(0xFFE4E6EB)
private val FbTextSec = Color(0xFF65676B)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FacebookPostCard(
    photo: PhotoEntity,
    currentUser: UserEntity?,
    comments: List<CommentEntity>,
    allUsers: List<UserEntity> = emptyList(),
    viewModel: GalleryViewModel,
    onPhotoClick: (PhotoEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    var showCommentSection by remember { mutableStateOf(false) }
    var showReactionPicker by remember { mutableStateOf(false) }
    var showReactionsDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var showEditPostDialog by remember { mutableStateOf(false) }
    var editPostTitle by remember(photo.title) { mutableStateOf(photo.title) }
    var editPostFeeling by remember(photo.feelingOrActivity) { mutableStateOf(photo.feelingOrActivity) }
    var commentInput by remember { mutableStateOf("") }
    var showOptionsMenu by remember { mutableStateOf(false) }
    var isAudioPlaying by remember { mutableStateOf(false) }

    val likedUserIds = remember(photo.likedByUserIds) {
        GalleryRepository.parseLikedUserIds(photo.likedByUserIds)
    }
    val userReactions = remember(photo.reactionsJson) {
        GalleryRepository.parseUserReactions(photo.reactionsJson)
    }
    val isLikedByMe = currentUser?.id?.let { likedUserIds.contains(it) } == true
    val myReaction = currentUser?.id?.let { userReactions[it] } ?: if (isLikedByMe) ReactionType.LIKE else null
    val isOwnerOrTeacher = currentUser?.id == photo.uploaderId || currentUser?.role == UserRole.WALI_KELAS.name

    // Top reacted types on this post
    val topReactions = remember(userReactions, photo.likeCount) {
        if (userReactions.isNotEmpty()) {
            userReactions.values.groupingBy { it }.eachCount().entries
                .sortedByDescending { it.value }
                .take(3)
                .map { it.key }
        } else if (photo.likeCount > 0) {
            listOf(ReactionType.LIKE, ReactionType.LOVE)
        } else {
            emptyList()
        }
    }

    if (showEditPostDialog) {
        AlertDialog(
            onDismissRequest = { showEditPostDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Edit Postingan", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = editPostTitle,
                        onValueChange = { editPostTitle = it },
                        label = { Text("Isi / Caption Postingan *") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 6,
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = editPostFeeling,
                        onValueChange = { editPostFeeling = it },
                        label = { Text("Perasaan / Aktivitas (Opsional)") },
                        placeholder = { Text("contoh: Merasa bersemangat 🌟") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editPostTitle.isNotBlank()) {
                            viewModel.updatePost(photo.id, editPostTitle, editPostFeeling)
                            showEditPostDialog = false
                        }
                    },
                    enabled = editPostTitle.isNotBlank()
                ) {
                    Text("Simpan Perubahan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditPostDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("Hapus Postingan?", fontWeight = FontWeight.Bold) },
            text = { Text("Apakah Anda yakin ingin menghapus postingan ini dari beranda Fourbook?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmDialog = false
                        viewModel.deletePhoto(photo.id)
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

    if (showReactionsDialog) {
        PostReactionsDialog(
            photo = photo,
            userReactions = userReactions,
            likedUserIds = likedUserIds,
            allUsers = allUsers,
            onDismiss = { showReactionsDialog = false }
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .testTag("post_card_${photo.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, FbBorder.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // 1. Post Header (Avatar, Name, Time, Role, 3-dots Menu)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    MemberAvatar(
                        fullName = photo.uploaderName,
                        avatarColor = photo.uploaderAvatarColor,
                        avatarIcon = photo.uploaderAvatarIcon,
                        customPhotoUri = photo.uploaderCustomPhotoUri,
                        size = 44.dp
                    )

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = photo.uploaderName,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            RoleBadge(role = photo.uploaderRole)
                        }

                        // Time, Privacy & Feeling
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = formatTimeAgo(photo.createdAt),
                                fontSize = 11.sp,
                                color = FbTextSec
                            )
                            Text(
                                text = "•",
                                fontSize = 11.sp,
                                color = FbTextSec
                            )
                            Icon(
                                imageVector = Icons.Default.Public,
                                contentDescription = "Publik",
                                modifier = Modifier.size(11.dp),
                                tint = FbTextSec
                            )
                            if (photo.feelingOrActivity.isNotBlank()) {
                                Text(
                                    text = "— ${photo.feelingOrActivity}",
                                    fontSize = 11.sp,
                                    color = FbBlue,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

                // 3-dots Options Menu
                Box {
                    IconButton(
                        onClick = { showOptionsMenu = true },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("post_menu_btn_${photo.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Menu Postingan",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    DropdownMenu(
                        expanded = showOptionsMenu,
                        onDismissRequest = { showOptionsMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Bagikan Postingan") },
                            leadingIcon = { Icon(Icons.Default.Share, contentDescription = null) },
                            onClick = {
                                showOptionsMenu = false
                                val shareText = viewModel.sharePost(photo)
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Bagikan Postingan Fourbook SDN 4"))
                            }
                        )
                        if (isOwnerOrTeacher) {
                            DropdownMenuItem(
                                text = { Text("Edit Postingan") },
                                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                                onClick = {
                                    showOptionsMenu = false
                                    editPostTitle = photo.title
                                    editPostFeeling = photo.feelingOrActivity
                                    showEditPostDialog = true
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Hapus Postingan", color = MaterialTheme.colorScheme.error) },
                                leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                                onClick = {
                                    showOptionsMenu = false
                                    showDeleteConfirmDialog = true
                                }
                            )
                        }
                    }
                }
            }

            // 2. Post Content (Text Status, Video, Audio MP3, or Photo)
            when (photo.postType) {
                PostType.TEXT_STATUS.name -> {
                    if (photo.statusBackgroundKey.isNotBlank() && photo.statusBackgroundKey != "none") {
                        val brush = getGradientForKey(photo.statusBackgroundKey)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 160.dp, max = 240.dp)
                                .background(brush)
                                .padding(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = photo.description,
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                lineHeight = 26.sp
                            )
                        }
                    } else {
                        Text(
                            text = photo.description,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            lineHeight = 21.sp
                        )
                    }
                }

                PostType.VIDEO.name -> {
                    if (photo.title.isNotBlank()) {
                        Text(
                            text = photo.title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
                        )
                    }
                    if (photo.description.isNotBlank()) {
                        Text(
                            text = photo.description,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    ClassVideoPlayer(
                        videoPresetKey = if (photo.videoPresetKey.isNotBlank()) photo.videoPresetKey else "video_science",
                        videoDurationSec = photo.videoDurationSec,
                        videoTitle = photo.title,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }

                PostType.AUDIO.name -> {
                    // Audio post preview card
                    if (photo.title.isNotBlank()) {
                        Text(
                            text = photo.title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
                        )
                    }
                    if (photo.description.isNotBlank()) {
                        Text(
                            text = photo.description,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    AudioTrackPostPlayer(
                        audioKey = photo.audioPresetKey.ifEmpty { "audio_mars_sdn4" },
                        isPlaying = isAudioPlaying,
                        onTogglePlay = { isAudioPlaying = !isAudioPlaying },
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }

                else -> {
                    // PHOTO post
                    if (photo.title.isNotBlank()) {
                        Text(
                            text = photo.title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
                        )
                    }
                    if (photo.description.isNotBlank()) {
                        Text(
                            text = photo.description,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(230.dp)
                            .clickable { onPhotoClick(photo) }
                    ) {
                        PhotoDisplay(
                            imageUri = photo.imageUri,
                            presetKey = photo.presetKey.ifEmpty { "preset_event" },
                            contentDescription = photo.title,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            // Tagged classmates chip
            if (photo.taggedStudentNames.isNotBlank()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.People,
                        contentDescription = "Bersama",
                        modifier = Modifier.size(14.dp),
                        tint = FbBlue
                    )
                    Text(
                        text = "bersama ${photo.taggedStudentNames}",
                        fontSize = 12.sp,
                        color = FbBlue,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // 3. Social Metrics Bar (Stacked Reaction Badges + Comments & Shares)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Reactions stack & count
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { showReactionsDialog = true }
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                        .testTag("reaction_summary_${photo.id}")
                ) {
                    if (photo.likeCount > 0) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy((-4).dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            topReactions.forEach { rType ->
                                Surface(
                                    shape = CircleShape,
                                    color = Color(rType.colorHex).copy(alpha = 0.15f),
                                    border = BorderStroke(1.5.dp, Color.White),
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(rType.emoji, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${photo.likeCount}",
                            fontSize = 12.sp,
                            color = FbTextSec,
                            fontWeight = FontWeight.SemiBold
                        )
                    } else {
                        Text(
                            text = "Jadilah yang pertama menyukai 👍",
                            fontSize = 11.sp,
                            color = FbTextSec.copy(alpha = 0.8f)
                        )
                    }
                }

                // Right: Comments & Share counts
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${comments.size} Komentar",
                        fontSize = 12.sp,
                        color = FbTextSec,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { showCommentSection = !showCommentSection }
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                            .testTag("comment_count_btn_${photo.id}")
                    )
                    Text(
                        text = "•",
                        fontSize = 12.sp,
                        color = FbTextSec
                    )
                    Text(
                        text = "${photo.shareCount} Bagikan",
                        fontSize = 12.sp,
                        color = FbTextSec,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            HorizontalDivider(
                color = FbBorder.copy(alpha = 0.6f),
                thickness = 1.dp,
                modifier = Modifier.padding(horizontal = 14.dp)
            )

            // Floating Reactions Dock Overlay
            AnimatedVisibility(
                visible = showReactionPicker,
                enter = fadeIn() + expandVertically() + scaleIn(),
                exit = fadeOut() + shrinkVertically() + scaleOut()
            ) {
                Surface(
                    shape = RoundedCornerShape(28.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp,
                    shadowElevation = 8.dp,
                    border = BorderStroke(1.dp, FbBorder),
                    modifier = Modifier
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                        .align(Alignment.Start)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf(
                            ReactionType.LIKE,
                            ReactionType.LOVE,
                            ReactionType.CARE,
                            ReactionType.HAHA,
                            ReactionType.FIRE
                        ).forEach { rType ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable {
                                        viewModel.toggleReaction(photo.id, rType)
                                        showReactionPicker = false
                                    }
                                    .padding(4.dp)
                            ) {
                                Text(text = rType.emoji, fontSize = 26.sp)
                            }
                        }
                    }
                }
            }

            // 4. Facebook-style Action Buttons Bar: Suka | Komentar | Bagikan
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Like / React Button with combined click & long press
                val activeReaction = myReaction
                val reactionLabel = activeReaction?.label ?: "Suka"
                val reactionColor = if (activeReaction != null) Color(activeReaction.colorHex) else FbTextSec

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (activeReaction != null) reactionColor.copy(alpha = 0.08f) else Color.Transparent,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .combinedClickable(
                            onClick = {
                                if (activeReaction != null) {
                                    viewModel.toggleReaction(photo.id, activeReaction)
                                } else {
                                    viewModel.toggleReaction(photo.id, ReactionType.LIKE)
                                }
                            },
                            onLongClick = {
                                showReactionPicker = !showReactionPicker
                            }
                        )
                        .testTag("like_button_${photo.id}")
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (activeReaction != null) {
                            Text(text = activeReaction.emoji, fontSize = 18.sp)
                        } else {
                            Icon(
                                imageVector = Icons.Outlined.ThumbUp,
                                contentDescription = "Suka",
                                tint = FbTextSec,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = reactionLabel,
                            fontSize = 13.sp,
                            fontWeight = if (activeReaction != null) FontWeight.Bold else FontWeight.Medium,
                            color = reactionColor
                        )

                        // Mini reaction picker dropdown indicator
                        IconButton(
                            onClick = { showReactionPicker = !showReactionPicker },
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Pilih Reaksi",
                                tint = reactionColor.copy(alpha = 0.7f),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }

                // Comment Button
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (showCommentSection) FbBlue.copy(alpha = 0.08f) else Color.Transparent,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { showCommentSection = !showCommentSection }
                        .testTag("comment_button_${photo.id}")
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (showCommentSection) Icons.Filled.ChatBubble else Icons.Outlined.ChatBubbleOutline,
                            contentDescription = "Komentar",
                            tint = if (showCommentSection) FbBlue else FbTextSec,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Komentar",
                            fontSize = 13.sp,
                            fontWeight = if (showCommentSection) FontWeight.Bold else FontWeight.Medium,
                            color = if (showCommentSection) FbBlue else FbTextSec
                        )
                    }
                }

                // Share Button
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Transparent,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            val shareText = viewModel.sharePost(photo)
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, shareText)
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Bagikan Postingan Fourbook SDN 4"))
                        }
                        .testTag("share_button_${photo.id}")
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Share,
                            contentDescription = "Bagikan",
                            tint = FbTextSec,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Bagikan",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = FbTextSec
                        )
                    }
                }
            }

            // 5. Fourbook Inline Comments Section
            AnimatedVisibility(
                visible = showCommentSection,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(FbGrayBg.copy(alpha = 0.5f))
                        .padding(12.dp)
                ) {
                    // Encouragement praise chips
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                    ) {
                        val quickPraise = listOf(
                            "👏 Hebat banget!",
                            "👍 Keren sekali!",
                            "🔥 Semangat belajar!",
                            "❤️ Mantap!",
                            "🌟 Luar biasa!",
                            "📚 Rajin terus ya!",
                            "🏆 Siswa juara!"
                        )
                        items(quickPraise) { praise ->
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = BorderStroke(1.dp, FbBorder),
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
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }

                    // Comments list
                    if (comments.isEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.ChatBubbleOutline,
                                    contentDescription = null,
                                    tint = FbTextSec.copy(alpha = 0.6f),
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Belum ada komentar",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Tulis kata-kata semangat atau apresiasi untuk teman sekelas!",
                                    fontSize = 11.sp,
                                    color = FbTextSec,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            comments.forEach { comment ->
                                PostCommentBubble(
                                    comment = comment,
                                    currentUserId = currentUser?.id ?: 0L,
                                    currentUserRole = currentUser?.role ?: "",
                                    onDelete = { viewModel.deleteComment(comment) },
                                    onEdit = { newText -> viewModel.updateComment(comment, newText) }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Comment Input Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        MemberAvatar(
                            fullName = currentUser?.fullName ?: "Siswa",
                            avatarColor = currentUser?.avatarColor ?: 0xFF4F46E5,
                            avatarIcon = currentUser?.avatarIcon ?: "smile",
                            customPhotoUri = currentUser?.customPhotoUri ?: "",
                            size = 32.dp
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        TextField(
                            value = commentInput,
                            onValueChange = { commentInput = it },
                            placeholder = {
                                Text(
                                    text = "Tulis komentar sebagai ${currentUser?.fullName?.split(" ")?.firstOrNull() ?: "Siswa"}...",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("comment_input_${photo.id}"),
                            colors = TextFieldDefaults.colors(
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                            keyboardActions = KeyboardActions(
                                onSend = {
                                    if (commentInput.isNotBlank()) {
                                        viewModel.addComment(photo.id, commentInput)
                                        commentInput = ""
                                        keyboardController?.hide()
                                    }
                                }
                            )
                        )

                        IconButton(
                            onClick = {
                                if (commentInput.isNotBlank()) {
                                    viewModel.addComment(photo.id, commentInput)
                                    commentInput = ""
                                    keyboardController?.hide()
                                }
                            },
                            enabled = commentInput.isNotBlank(),
                            modifier = Modifier
                                .size(36.dp)
                                .background(
                                    if (commentInput.isNotBlank()) FbBlue else Color.LightGray.copy(alpha = 0.4f),
                                    CircleShape
                                )
                                .testTag("send_comment_btn_${photo.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Kirim Komentar",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PostCommentBubble(
    comment: CommentEntity,
    currentUserId: Long,
    currentUserRole: String,
    onDelete: () -> Unit,
    onEdit: (String) -> Unit
) {
    val isTeacherComment = comment.userRole == UserRole.WALI_KELAS.name
    val canModify = currentUserId == comment.userId || currentUserRole == UserRole.WALI_KELAS.name
    var isLiked by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var editedText by remember(comment.text) { mutableStateOf(comment.text) }

    val isDark = isSystemInDarkTheme()

    // Facebook-style Comment Bubble Background & High-contrast Text Colors
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
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top
    ) {
        MemberAvatar(
            fullName = comment.userName,
            avatarColor = comment.userAvatarColor,
            avatarIcon = if (isTeacherComment) "teacher" else "star",
            customPhotoUri = comment.userCustomPhotoUri,
            size = 34.dp
        )

        Column(modifier = Modifier.weight(1f)) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = bubbleBg,
                border = BorderStroke(1.dp, bubbleBorder),
                tonalElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = comment.userName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = authorTextColor
                        )
                        RoleBadge(role = comment.userRole)
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = comment.text,
                        fontSize = 13.sp,
                        color = commentTextColor,
                        lineHeight = 18.sp
                    )
                }
            }

            // Comment footer (time, like, edit, delete)
            Row(
                modifier = Modifier.padding(start = 8.dp, top = 3.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = formatTimeAgo(comment.createdAt),
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = if (isLiked) "❤️ Disukai" else "Suka",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isLiked) Color(0xFFE11D48) else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .clickable { isLiked = !isLiked }
                        .padding(horizontal = 2.dp)
                )

                if (canModify) {
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
                            .padding(horizontal = 2.dp)
                    )

                    Text(
                        text = "Hapus",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                        modifier = Modifier
                            .clickable { onDelete() }
                            .padding(horizontal = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AudioTrackPostPlayer(
    audioKey: String,
    isPlaying: Boolean,
    onTogglePlay: () -> Unit,
    modifier: Modifier = Modifier
) {
    val trackInfo = remember(audioKey) {
        PresetAudioTracks.getByKey(audioKey) ?: PresetAudioTracks.tracks.first()
    }

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = trackInfo.primaryColor.copy(alpha = 0.1f),
        border = BorderStroke(1.dp, trackInfo.primaryColor.copy(alpha = 0.3f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconButton(
                onClick = onTogglePlay,
                modifier = Modifier
                    .size(46.dp)
                    .background(trackInfo.primaryColor, CircleShape)
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Jeda" else "Putar Lagu",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = trackInfo.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${trackInfo.artist} • ${PresetAudioTracks.formatDuration(trackInfo.durationSec)}",
                    fontSize = 11.sp,
                    color = FbTextSec,
                    maxLines = 1
                )

                // Equalizer animated wave
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier.height(14.dp)
                ) {
                    trackInfo.waveHeights.take(10).forEachIndexed { idx, heightFrac ->
                        val infiniteTransition = rememberInfiniteTransition(label = "audio_wave")
                        val animHeight by infiniteTransition.animateFloat(
                            initialValue = heightFrac * 0.4f,
                            targetValue = heightFrac,
                            animationSpec = infiniteRepeatable(
                                animation = tween(300 + (idx * 60), easing = FastOutSlowInEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "wave_height"
                        )

                        val currentHeight = if (isPlaying) animHeight else (heightFrac * 0.4f)
                        Box(
                            modifier = Modifier
                                .width(3.dp)
                                .fillMaxHeight(currentHeight.coerceIn(0.15f, 1f))
                                .background(trackInfo.primaryColor, RoundedCornerShape(2.dp))
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PostReactionsDialog(
    photo: PhotoEntity,
    userReactions: Map<Long, ReactionType>,
    likedUserIds: List<Long>,
    allUsers: List<UserEntity>,
    onDismiss: () -> Unit
) {
    var selectedFilter by remember { mutableStateOf<ReactionType?>(null) }

    // Map user details
    val reactedUsers = remember(likedUserIds, userReactions, allUsers) {
        likedUserIds.mapNotNull { uId ->
            val user = allUsers.find { it.id == uId }
            val reaction = userReactions[uId] ?: ReactionType.LIKE
            if (user != null) Pair(user, reaction) else null
        }
    }

    val filteredList = remember(reactedUsers, selectedFilter) {
        if (selectedFilter == null) reactedUsers
        else reactedUsers.filter { it.second == selectedFilter }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 480.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Orang yang Bereaksi (${reactedUsers.size})",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup")
                    }
                }

                // Reaction Category Filter Tabs
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedFilter == null,
                            onClick = { selectedFilter = null },
                            label = { Text("Semua ${reactedUsers.size}") }
                        )
                    }
                    val existingTypes = reactedUsers.map { it.second }.distinct()
                    items(existingTypes) { rType ->
                        val count = reactedUsers.count { it.second == rType }
                        FilterChip(
                            selected = selectedFilter == rType,
                            onClick = { selectedFilter = rType },
                            label = { Text("${rType.emoji} $count") }
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))

                // List of users
                if (filteredList.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Belum ada reaksi yang tercatat",
                            fontSize = 13.sp,
                            color = FbTextSec
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        filteredList.forEach { (user, rType) ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box {
                                        MemberAvatar(
                                            fullName = user.fullName,
                                            avatarColor = user.avatarColor,
                                            avatarIcon = user.avatarIcon,
                                            customPhotoUri = user.customPhotoUri,
                                            size = 40.dp
                                        )
                                        Surface(
                                            shape = CircleShape,
                                            color = Color.White,
                                            shadowElevation = 2.dp,
                                            modifier = Modifier
                                                .size(18.dp)
                                                .align(Alignment.BottomEnd)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(rType.emoji, fontSize = 10.sp)
                                            }
                                        }
                                    }

                                    Column {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(
                                                text = user.fullName,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            RoleBadge(role = user.role)
                                        }
                                        if (user.studentNumber.isNotBlank()) {
                                            Text(
                                                text = user.studentNumber,
                                                fontSize = 11.sp,
                                                color = FbTextSec
                                            )
                                        }
                                    }
                                }

                                Text(
                                    text = rType.label,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(rType.colorHex)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

fun formatTimeAgo(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    return when {
        minutes < 1 -> "Baru saja"
        minutes < 60 -> "$minutes m"
        hours < 24 -> "$hours jam"
        days < 7 -> "$days hari"
        else -> "${days / 7} minggu"
    }
}
