package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.*
import com.example.ui.components.MemberAvatar
import com.example.ui.components.RoleBadge
import com.example.ui.components.Sdn4SchoolLogo
import com.example.ui.theme.*
import com.example.ui.viewmodel.GalleryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MembersDirectoryScreen(
    viewModel: GalleryViewModel,
    modifier: Modifier = Modifier
) {
    val allUsers by viewModel.allUsers.collectAsState()
    val allPhotos by viewModel.allPhotos.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val myFriendships by viewModel.myFriendships.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedTabFilter by remember { mutableStateOf("SEMUA") } // SEMUA, TEMAN, PERMINTAAN

    val pendingRequestsCount = remember(myFriendships, currentUser) {
        val myId = currentUser?.id ?: 0L
        myFriendships.count { it.receiverId == myId && it.status == FriendshipStatus.PENDING.name }
    }

    val filteredUsers = remember(allUsers, searchQuery, selectedTabFilter, myFriendships, currentUser) {
        val myId = currentUser?.id ?: 0L
        val baseList = when (selectedTabFilter) {
            "TEMAN" -> {
                val friendIds = myFriendships.filter { it.status == FriendshipStatus.ACCEPTED.name }
                    .map { if (it.senderId == myId) it.receiverId else it.senderId }
                allUsers.filter { it.id in friendIds }
            }
            "PERMINTAAN" -> {
                val requesterIds = myFriendships.filter { it.receiverId == myId && it.status == FriendshipStatus.PENDING.name }
                    .map { it.senderId }
                allUsers.filter { it.id in requesterIds }
            }
            else -> allUsers
        }

        if (searchQuery.isBlank()) baseList
        else baseList.filter {
            it.fullName.contains(searchQuery, ignoreCase = true) ||
            it.username.contains(searchQuery, ignoreCase = true) ||
            it.studentNumber.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = FbBg,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            Surface(
                color = FbCardBg,
                shadowElevation = 2.dp,
                border = BorderStroke(0.5.dp, Color(0xFFE2E8F0))
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
                        Sdn4SchoolLogo(
                            size = 40.dp,
                            showGlow = true
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Teman & Komunitas",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = FbTextPrimary,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "SDN 4 Putrajawa",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = FbBluePrimary
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = FbBluePrimary.copy(alpha = 0.12f),
                            modifier = Modifier.padding(2.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.People,
                                    contentDescription = null,
                                    tint = FbBluePrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${allUsers.size}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FbBluePrimary
                                )
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
                .background(FbBg)
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Search field
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Cari nama teman, admin guru, atau no. induk...", fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = FbTextSecondary) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = FbCardBg,
                        unfocusedContainerColor = FbCardBg,
                        focusedBorderColor = FbBluePrimary,
                        unfocusedBorderColor = Color(0xFFE2E8F0)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("members_search_input")
                )
            }

            // Tab Filters: Semua Warga | Teman Saya | Permintaan Masuk
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedTabFilter == "SEMUA",
                        onClick = { selectedTabFilter = "SEMUA" },
                        label = { Text("Semua (${allUsers.size})", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = FbBluePrimary.copy(alpha = 0.12f),
                            selectedLabelColor = FbBluePrimary
                        )
                    )
                    FilterChip(
                        selected = selectedTabFilter == "TEMAN",
                        onClick = { selectedTabFilter = "TEMAN" },
                        label = {
                            val count = myFriendships.count { it.status == FriendshipStatus.ACCEPTED.name }
                            Text("Teman Saya ($count)", fontSize = 12.sp)
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = FbBluePrimary.copy(alpha = 0.12f),
                            selectedLabelColor = FbBluePrimary
                        )
                    )
                    FilterChip(
                        selected = selectedTabFilter == "PERMINTAAN",
                        onClick = { selectedTabFilter = "PERMINTAAN" },
                        label = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Permintaan", fontSize = 12.sp)
                                if (pendingRequestsCount > 0) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Surface(
                                        color = Color(0xFFEF4444),
                                        shape = CircleShape,
                                        modifier = Modifier.size(16.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                "$pendingRequestsCount",
                                                color = Color.White,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = FbBluePrimary.copy(alpha = 0.12f),
                            selectedLabelColor = FbBluePrimary
                        )
                    )
                }
            }

            // Member list items
            items(filteredUsers, key = { it.id }) { user ->
                val userPhotos = allPhotos.filter { it.uploaderId == user.id }
                val isMe = currentUser?.id == user.id
                val myId = currentUser?.id ?: 0L

                val friendship = remember(myFriendships, user.id, myId) {
                    myFriendships.find {
                        (it.senderId == myId && it.receiverId == user.id) ||
                        (it.senderId == user.id && it.receiverId == myId)
                    }
                }

                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.showMemberProfile(user)
                        }
                        .testTag("member_card_${user.id}"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = if (isMe) Color(0xFFEFF6FF) else FbCardBg
                    ),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            MemberAvatar(
                                fullName = user.fullName,
                                avatarColor = user.avatarColor,
                                avatarIcon = user.avatarIcon,
                                customPhotoUri = user.customPhotoUri,
                                size = 50.dp
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = user.fullName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = FbTextPrimary
                                    )
                                    if (isMe) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = FbBluePrimary
                                        ) {
                                            Text(
                                                text = "Saya",
                                                color = Color.White,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(3.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    RoleBadge(user.role)
                                    if (user.studentNumber.isNotBlank()) {
                                        Text(
                                            text = user.studentNumber,
                                            fontSize = 11.sp,
                                            color = FbTextSecondary
                                        )
                                    }
                                }

                                if (user.bio.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = "\"${user.bio}\"",
                                        fontSize = 11.sp,
                                        color = FbTextSecondary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }

                        // Bottom Actions: Facebook-style Add Friend & Message buttons
                        if (!isMe && currentUser != null) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Divider(color = Color(0xFFF1F5F9), thickness = 0.5.dp)
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Add Friend / Friendship Status button
                                when {
                                    friendship == null -> {
                                        Button(
                                            onClick = { viewModel.sendFriendRequest(user) },
                                            colors = ButtonDefaults.buttonColors(containerColor = FbBluePrimary),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(36.dp)
                                                .testTag("btn_add_friend_${user.id}")
                                        ) {
                                            Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Tambah Jadi Teman", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    friendship.status == FriendshipStatus.ACCEPTED.name -> {
                                        OutlinedButton(
                                            onClick = { viewModel.showMemberProfile(user) },
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF10B981)),
                                            border = BorderStroke(1.dp, Color(0xFF10B981)),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(36.dp)
                                                .testTag("btn_friends_${user.id}")
                                        ) {
                                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Berteman", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    friendship.status == FriendshipStatus.PENDING.name && friendship.senderId == myId -> {
                                        OutlinedButton(
                                            onClick = { viewModel.removeOrCancelFriendship(user.id) },
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = FbTextSecondary),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(36.dp)
                                                .testTag("btn_cancel_request_${user.id}")
                                        ) {
                                            Icon(Icons.Default.HourglassTop, contentDescription = null, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Batal Permintaan", fontSize = 11.sp)
                                        }
                                    }
                                    friendship.status == FriendshipStatus.PENDING.name && friendship.receiverId == myId -> {
                                        Button(
                                            onClick = { viewModel.acceptFriendRequest(user) },
                                            colors = ButtonDefaults.buttonColors(containerColor = FbBluePrimary),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(36.dp)
                                                .testTag("btn_confirm_friend_${user.id}")
                                        ) {
                                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Konfirmasi Teman", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                // Message button
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFF1F5F9),
                                    modifier = Modifier
                                        .height(36.dp)
                                        .clickable { viewModel.openChatWith(user) }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Filled.ChatBubble,
                                            contentDescription = "Kirim Pesan",
                                            tint = FbBluePrimary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            "Pesan",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = FbTextPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MemberProfileDetailDialog(
    member: UserEntity,
    currentUser: UserEntity?,
    allPhotos: List<PhotoEntity>,
    myFriendships: List<FriendshipEntity>,
    onDismiss: () -> Unit,
    onSendFriendRequest: () -> Unit,
    onAcceptFriendRequest: () -> Unit,
    onRemoveFriendship: () -> Unit,
    onOpenChat: () -> Unit,
    onSelectPhoto: (PhotoEntity) -> Unit
) {
    val isMe = currentUser?.id == member.id
    val myId = currentUser?.id ?: 0L
    val memberPhotos = remember(allPhotos, member.id) {
        allPhotos.filter { it.uploaderId == member.id }
    }

    val friendship = remember(myFriendships, member.id, myId) {
        myFriendships.find {
            (it.senderId == myId && it.receiverId == member.id) ||
            (it.senderId == member.id && it.receiverId == myId)
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = FbCardBg,
            shadowElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(vertical = 16.dp)
                .testTag("member_profile_dialog")
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Header Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFF1877F2),
                                    Color(0xFF0052CC),
                                    Color(0xFF4F46E5)
                                )
                            )
                        )
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup", tint = Color.White)
                    }

                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                "SDN 4 PUTRAJAWA",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                // Avatar overlapping banner
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .offset(y = (-36).dp)
                            .size(76.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = FbCardBg,
                            border = BorderStroke(3.dp, Color.White),
                            shadowElevation = 4.dp,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                MemberAvatar(
                                    fullName = member.fullName,
                                    avatarColor = member.avatarColor,
                                    avatarIcon = member.avatarIcon,
                                    customPhotoUri = member.customPhotoUri,
                                    size = 70.dp
                                )
                            }
                        }
                    }

                    // User Info
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.offset(y = (-24).dp)
                    ) {
                        Text(
                            text = member.fullName,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = FbTextPrimary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            RoleBadge(member.role)
                            if (member.studentNumber.isNotBlank()) {
                                Text(
                                    text = member.studentNumber,
                                    fontSize = 12.sp,
                                    color = FbTextSecondary
                                )
                            }
                        }
                        if (member.bio.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "\"${member.bio}\"",
                                fontSize = 12.sp,
                                color = FbTextSecondary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Facebook-style Action Buttons in Profile Dialog
                        if (!isMe && currentUser != null) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                when {
                                    friendship == null -> {
                                        Button(
                                            onClick = onSendFriendRequest,
                                            colors = ButtonDefaults.buttonColors(containerColor = FbBluePrimary),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(42.dp)
                                                .testTag("dialog_btn_add_friend")
                                        ) {
                                            Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Tambah Teman", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        }
                                    }
                                    friendship.status == FriendshipStatus.ACCEPTED.name -> {
                                        OutlinedButton(
                                            onClick = onRemoveFriendship,
                                            shape = RoundedCornerShape(10.dp),
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF10B981)),
                                            border = BorderStroke(1.5.dp, Color(0xFF10B981)),
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(42.dp)
                                                .testTag("dialog_btn_remove_friend")
                                        ) {
                                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Sudah Berteman", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        }
                                    }
                                    friendship.status == FriendshipStatus.PENDING.name && friendship.senderId == myId -> {
                                        OutlinedButton(
                                            onClick = onRemoveFriendship,
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(42.dp)
                                                .testTag("dialog_btn_cancel_request")
                                        ) {
                                            Icon(Icons.Default.HourglassTop, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Batal Permintaan", fontSize = 12.sp)
                                        }
                                    }
                                    friendship.status == FriendshipStatus.PENDING.name && friendship.receiverId == myId -> {
                                        Button(
                                            onClick = onAcceptFriendRequest,
                                            colors = ButtonDefaults.buttonColors(containerColor = FbBluePrimary),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(42.dp)
                                                .testTag("dialog_btn_accept_friend")
                                        ) {
                                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Terima Permintaan", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        }
                                    }
                                }

                                Button(
                                    onClick = onOpenChat,
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE4E6EB)),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(42.dp)
                                        .testTag("dialog_btn_message")
                                ) {
                                    Icon(
                                        Icons.Filled.ChatBubble,
                                        contentDescription = null,
                                        tint = FbTextPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        "Kirim Pesan",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = FbTextPrimary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Divider(color = Color(0xFFE2E8F0), thickness = 0.5.dp)
                        Spacer(modifier = Modifier.height(12.dp))

                        // Stats Summary Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    "${memberPhotos.size}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = FbBluePrimary
                                )
                                Text("Postingan", fontSize = 11.sp, color = FbTextSecondary)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    "${memberPhotos.sumOf { it.likeCount }}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color(0xFFE11D48)
                                )
                                Text("Suka Diterima", fontSize = 11.sp, color = FbTextSecondary)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    "${memberPhotos.sumOf { it.viewCount }}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color(0xFF10B981)
                                )
                                Text("Tayangan", fontSize = 11.sp, color = FbTextSecondary)
                            }
                        }

                        if (memberPhotos.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                "Karya & Postingan ${member.fullName.split(" ").first()}:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = FbTextPrimary,
                                modifier = Modifier.align(Alignment.Start)
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(memberPhotos) { photo ->
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = Color(0xFFF1F5F9),
                                        border = BorderStroke(0.5.dp, Color(0xFFE2E8F0)),
                                        modifier = Modifier
                                            .width(130.dp)
                                            .clickable { onSelectPhoto(photo) }
                                    ) {
                                        Column(modifier = Modifier.padding(8.dp)) {
                                            Text(
                                                photo.title.ifBlank { photo.description },
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 12.sp,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Icon(Icons.Default.Favorite, contentDescription = null, tint = Color(0xFFE11D48), modifier = Modifier.size(10.dp))
                                                Text("${photo.likeCount}", fontSize = 10.sp, color = FbTextSecondary)
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Icon(Icons.Default.Visibility, contentDescription = null, tint = FbTextSecondary, modifier = Modifier.size(10.dp))
                                                Text("${photo.viewCount}", fontSize = 10.sp, color = FbTextSecondary)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
