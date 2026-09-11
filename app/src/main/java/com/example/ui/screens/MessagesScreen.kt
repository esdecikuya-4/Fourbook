package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChatMessageEntity
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import com.example.ui.components.MemberAvatar
import com.example.ui.components.RoleBadge
import com.example.ui.components.Sdn4SchoolLogo
import com.example.ui.theme.*
import com.example.ui.viewmodel.GalleryViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesScreen(
    viewModel: GalleryViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    val recentMessages by viewModel.recentMessages.collectAsState()
    val showChatSheet by viewModel.showChatConversationSheet.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var showNewChatDialog by remember { mutableStateOf(false) }

    val user = currentUser
    if (user == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Silakan masuk untuk menggunakan fitur Pesan.")
        }
        return
    }

    // Other classmates / teachers
    val otherUsers = remember(allUsers, user) {
        allUsers.filter { it.id != user.id }
    }

    // Active friends story list (ordered by Admin, Ketua, then others)
    val activeFriends = remember(otherUsers) {
        otherUsers.sortedWith(
            compareByDescending<UserEntity> { it.role == UserRole.WALI_KELAS.name }
                .thenByDescending { it.role == UserRole.KETUA_KELAS.name }
                .thenBy { it.fullName }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = FbBg,
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
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            MemberAvatar(
                                fullName = user.fullName,
                                avatarColor = user.avatarColor,
                                avatarIcon = user.avatarIcon,
                                customPhotoUri = user.customPhotoUri,
                                size = 36.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Pesan",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black,
                                    color = FbTextPrimary
                                )
                                Text(
                                    text = "Messenger SDN 4 Putrajawa",
                                    fontSize = 11.sp,
                                    color = FbBluePrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // New Message Button
                            FilledTonalIconButton(
                                onClick = { showNewChatDialog = true },
                                colors = IconButtonDefaults.filledTonalIconButtonColors(
                                    containerColor = FbBlueLight,
                                    contentColor = FbBluePrimary
                                ),
                                modifier = Modifier.size(36.dp).testTag("btn_new_message")
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "Tulis Pesan Baru", modifier = Modifier.size(18.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Search input
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Cari pesan atau teman...", fontSize = 13.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = FbTextSecondary) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = "Hapus")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FbBluePrimary,
                            unfocusedBorderColor = FbBorder,
                            focusedContainerColor = FbBg,
                            unfocusedContainerColor = FbBg
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("search_messages_input")
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showNewChatDialog = true },
                containerColor = FbBluePrimary,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.testTag("fab_new_message")
            ) {
                Icon(Icons.Default.Chat, contentDescription = "Kirim Pesan Baru")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(FbBg)
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // Horizontal Active Friends Row (FB Messenger style)
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(FbCardSurface)
                        .padding(vertical = 12.dp)
                ) {
                    Text(
                        text = "AKTIF SEKARANG",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = FbTextSecondary,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                        letterSpacing = 0.5.sp
                    )

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // General Group Chat Bubble
                        item {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .width(64.dp)
                                    .clickable { viewModel.openChatWith(null) }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(52.dp)
                                        .clip(CircleShape)
                                        .background(Brush.linearGradient(listOf(Color(0xFF1877F2), Color(0xFF06B6D4)))),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Groups,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Grup Kelas",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FbTextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        // Friends Bubbles
                        items(activeFriends) { friend ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .width(64.dp)
                                    .clickable { viewModel.openChatWith(friend) }
                            ) {
                                Box {
                                    MemberAvatar(
                                        fullName = friend.fullName,
                                        avatarColor = friend.avatarColor,
                                        avatarIcon = friend.avatarIcon,
                                        customPhotoUri = friend.customPhotoUri,
                                        size = 52.dp,
                                        showOnlineDot = true
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (friend.role == UserRole.WALI_KELAS.name) "Pak Teten" else friend.fullName.split(" ").firstOrNull() ?: friend.fullName,
                                    fontSize = 11.sp,
                                    fontWeight = if (friend.role == UserRole.WALI_KELAS.name) FontWeight.Black else FontWeight.Medium,
                                    color = if (friend.role == UserRole.WALI_KELAS.name) Color(0xFF059669) else FbTextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                Divider(color = FbBorder.copy(alpha = 0.5f), thickness = 1.dp)
            }

            // Pinned Class Group Chat Room Item
            item {
                Surface(
                    color = FbCardSurface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.openChatWith(null) }
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(FbBluePrimary, Color(0xFF0EA5E9)))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Forum,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Forum & Grup Kelas SDN 4",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.5.sp,
                                    color = FbTextPrimary
                                )
                                Text(
                                    text = "Terbaru",
                                    fontSize = 10.5.sp,
                                    color = FbBluePrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Pesan & pengumuman terbuka untuk seluruh siswa dan guru.",
                                fontSize = 12.sp,
                                color = FbTextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            // Direct Chat Channels Header
            item {
                Text(
                    text = "PERCAKAPAN LANGSUNG",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = FbTextSecondary,
                    modifier = Modifier.padding(start = 16.dp, top = 14.dp, bottom = 4.dp),
                    letterSpacing = 0.5.sp
                )
            }

            // Filtered users list for 1-on-1 chatting
            val chatTargets = if (searchQuery.isBlank()) {
                activeFriends
            } else {
                activeFriends.filter {
                    it.fullName.contains(searchQuery, ignoreCase = true) ||
                    it.studentNumber.contains(searchQuery, ignoreCase = true)
                }
            }

            if (chatTargets.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Tidak ada teman ditemukan.",
                            color = FbTextSecondary,
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                items(chatTargets) { targetUser ->
                    val isTeacher = targetUser.role == UserRole.WALI_KELAS.name

                    Surface(
                        color = if (isTeacher) Color(0xFFF0FDF4) else FbCardSurface,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.openChatWith(targetUser) }
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(
                            1.dp,
                            if (isTeacher) Color(0xFF86EFAC) else FbBorder.copy(alpha = 0.6f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            MemberAvatar(
                                fullName = targetUser.fullName,
                                avatarColor = targetUser.avatarColor,
                                avatarIcon = targetUser.avatarIcon,
                                customPhotoUri = targetUser.customPhotoUri,
                                size = 48.dp,
                                showOnlineDot = true
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = targetUser.fullName,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = FbTextPrimary
                                        )
                                        if (isTeacher) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(
                                                Icons.Default.Verified,
                                                contentDescription = "Admin / Wali Kelas",
                                                tint = Color(0xFF059669),
                                                modifier = Modifier.size(15.dp)
                                            )
                                        }
                                    }

                                    RoleBadge(role = targetUser.role)
                                }

                                Spacer(modifier = Modifier.height(3.dp))

                                Text(
                                    text = if (isTeacher) "Wali Kelas & Admin • Kirim pesan/tanya tugas" else targetUser.bio,
                                    fontSize = 12.sp,
                                    color = FbTextSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Icon(
                                imageVector = Icons.Default.ChatBubbleOutline,
                                contentDescription = "Buka Chat",
                                tint = if (isTeacher) Color(0xFF059669) else FbBluePrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // Modal Sheet for Chat Conversation
    if (showChatSheet) {
        ChatConversationDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.closeChatConversation() }
        )
    }

    // Dialog for picking a recipient to start a new chat
    if (showNewChatDialog) {
        AlertDialog(
            onDismissRequest = { showNewChatDialog = false },
            title = { Text("Pilih Penerima Pesan", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 350.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = FbBlueLight,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    showNewChatDialog = false
                                    viewModel.openChatWith(null)
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Groups, contentDescription = null, tint = FbBluePrimary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Grup & Forum Kelas", fontWeight = FontWeight.Bold, color = FbBluePrimary)
                            }
                        }
                    }

                    items(otherUsers) { target ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = FbBg,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    showNewChatDialog = false
                                    viewModel.openChatWith(target)
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                MemberAvatar(
                                    fullName = target.fullName,
                                    avatarColor = target.avatarColor,
                                    avatarIcon = target.avatarIcon,
                                    customPhotoUri = target.customPhotoUri,
                                    size = 36.dp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(target.fullName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(target.role, fontSize = 10.sp, color = FbTextSecondary)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showNewChatDialog = false }) {
                    Text("Tutup")
                }
            }
        )
    }
}

/**
 * Fullscreen / BottomSheet Chat Conversation Dialog with Facebook Messenger Aesthetics
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatConversationDialog(
    viewModel: GalleryViewModel,
    onDismiss: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val recipient by viewModel.activeChatRecipient.collectAsState()
    val messages by viewModel.activeConversationMessages.collectAsState()

    var messageInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Auto-scroll to bottom when messages change
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    val user = currentUser ?: return

    val targetTitle = recipient?.fullName ?: "Forum & Grup Kelas SDN 4"
    val isTeacher = recipient?.role == UserRole.WALI_KELAS.name

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color.White,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .background(Color.White)
        ) {
            // Chat Top Bar (FB Messenger Style)
            Surface(
                color = Color.White,
                shadowElevation = 2.dp,
                border = BorderStroke(0.5.dp, Color(0xFFE2E8F0))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Kembali", tint = FbBluePrimary)
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        if (recipient != null) {
                            Box(modifier = Modifier.clickable { viewModel.showMemberProfile(recipient) }) {
                                MemberAvatar(
                                    fullName = recipient!!.fullName,
                                    avatarColor = recipient!!.avatarColor,
                                    avatarIcon = recipient!!.avatarIcon,
                                    customPhotoUri = recipient!!.customPhotoUri,
                                    size = 40.dp,
                                    showOnlineDot = true
                                )
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(FbBluePrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Groups, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(
                            modifier = Modifier.clickable(enabled = recipient != null) {
                                recipient?.let { viewModel.showMemberProfile(it) }
                            }
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = targetTitle,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.5.sp,
                                    color = FbTextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                if (isTeacher) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        Icons.Default.Verified,
                                        contentDescription = "Admin Verified",
                                        tint = Color(0xFF059669),
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                            Text(
                                text = if (recipient != null) "Aktif sekarang • SDN 4 Putrajawa" else "Grup Obrolan Semua Siswa & Guru",
                                fontSize = 11.sp,
                                color = Color(0xFF16A34A),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Quick Actions
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (recipient != null) {
                            IconButton(onClick = { viewModel.showMemberProfile(recipient) }) {
                                Icon(Icons.Default.AccountCircle, contentDescription = "Lihat Profil Lengkap", tint = FbBluePrimary)
                            }
                        }
                        IconButton(onClick = { viewModel.sendChatMessage("👍", "STICKER") }) {
                            Text("👍", fontSize = 18.sp)
                        }
                    }
                }
            }

            // Messages List
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color(0xFFF8FAFC))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (messages.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (recipient != null) {
                                MemberAvatar(
                                    fullName = recipient!!.fullName,
                                    avatarColor = recipient!!.avatarColor,
                                    avatarIcon = recipient!!.avatarIcon,
                                    customPhotoUri = recipient!!.customPhotoUri,
                                    size = 72.dp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = recipient!!.fullName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = if (isTeacher) "Wali Kelas & Admin SDN 4 Putrajawa" else "Teman Sekelas SDN 4 Putrajawa",
                                    fontSize = 12.sp,
                                    color = FbTextSecondary
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Mulai obrolan ramah sekarang! Tulis pesan atau kirim salam di bawah.",
                                fontSize = 12.sp,
                                color = FbTextSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                items(messages, key = { it.id }) { msg ->
                    val isMine = msg.senderId == user.id
                    val timeStr = remember(msg.timestamp) {
                        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(msg.timestamp))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        if (!isMine) {
                            MemberAvatar(
                                fullName = msg.senderName,
                                avatarColor = msg.senderAvatarColor,
                                avatarIcon = msg.senderAvatarIcon,
                                customPhotoUri = msg.senderCustomPhoto,
                                size = 28.dp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                        }

                        Column(
                            horizontalAlignment = if (isMine) Alignment.End else Alignment.Start
                        ) {
                            // Show sender name in group chat
                            if (!isMine && recipient == null) {
                                Text(
                                    text = msg.senderName,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FbBluePrimary,
                                    modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
                                )
                            }

                            // Message Bubble
                            Surface(
                                shape = RoundedCornerShape(
                                    topStart = 16.dp,
                                    topEnd = 16.dp,
                                    bottomStart = if (isMine) 16.dp else 4.dp,
                                    bottomEnd = if (isMine) 4.dp else 16.dp
                                ),
                                color = if (isMine) FbBluePrimary else MaterialTheme.colorScheme.surfaceVariant,
                                shadowElevation = 1.dp,
                                border = if (isMine) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                modifier = Modifier.widthIn(max = 280.dp)
                            ) {
                                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                                    Text(
                                        text = msg.messageText,
                                        fontSize = if (msg.attachmentType == "STICKER") 28.sp else 13.5.sp,
                                        color = if (isMine) Color.White else MaterialTheme.colorScheme.onSurface,
                                        lineHeight = 18.sp
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = timeStr,
                                        fontSize = 9.sp,
                                        color = if (isMine) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.End,
                                        modifier = Modifier.align(Alignment.End)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Quick Emojis Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val emojis = listOf("👋 Halo!", "👍 Siap", "😊 Terima kasih", "📚 Tugas", "⭐ Keren")
                emojis.forEach { em ->
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.clickable {
                            viewModel.sendChatMessage(em)
                        }
                    ) {
                        Text(
                            text = em,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }

            // Chat Input Bar
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 4.dp,
                border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = messageInput,
                        onValueChange = { messageInput = it },
                        placeholder = { Text("Ketik pesan...", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("chat_input_field")
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            if (messageInput.isNotBlank()) {
                                viewModel.sendChatMessage(messageInput)
                                messageInput = ""
                            }
                        },
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = FbBluePrimary,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.size(44.dp).testTag("chat_send_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Kirim",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
