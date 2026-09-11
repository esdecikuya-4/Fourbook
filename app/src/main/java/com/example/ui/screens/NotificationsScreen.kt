package com.example.ui.screens

import androidx.compose.animation.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NotificationEntity
import com.example.data.model.NotificationType
import com.example.data.model.UserRole
import com.example.ui.theme.*
import com.example.ui.viewmodel.GalleryViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    viewModel: GalleryViewModel,
    modifier: Modifier = Modifier
) {
    val notifications by viewModel.notifications.collectAsState()
    val unreadCount by viewModel.unreadNotificationsCount.collectAsState()
    var filterOnlyUnread by remember { mutableStateOf(false) }

    val filteredNotifications = remember(notifications, filterOnlyUnread) {
        if (filterOnlyUnread) {
            notifications.filter { !it.isRead }
        } else {
            notifications
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
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    TopAppBar(
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    "Notifikasi",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    color = FbTextPrimary
                                )
                                if (unreadCount > 0) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(
                                        color = FbBluePrimary,
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text(
                                            text = "$unreadCount Baru",
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        },
                        actions = {
                            if (unreadCount > 0) {
                                TextButton(
                                    onClick = { viewModel.markAllNotificationsAsRead() },
                                    modifier = Modifier.testTag("btn_mark_all_read")
                                ) {
                                    Icon(
                                        Icons.Default.DoneAll,
                                        contentDescription = "Tandai Dibaca",
                                        tint = FbBluePrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        "Tandai Dibaca",
                                        color = FbBluePrimary,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                            if (notifications.isNotEmpty()) {
                                IconButton(
                                    onClick = { viewModel.clearAllNotifications() },
                                    modifier = Modifier.testTag("btn_clear_all_notifs")
                                ) {
                                    Icon(
                                        Icons.Outlined.DeleteSweep,
                                        contentDescription = "Bersihkan Semua",
                                        tint = FbTextSecondary
                                    )
                                }
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = FbCardBg)
                    )

                    // Filter chips: Semua vs Belum Dibaca
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = !filterOnlyUnread,
                            onClick = { filterOnlyUnread = false },
                            label = { Text("Semua (${notifications.size})", fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = FbBluePrimary.copy(alpha = 0.12f),
                                selectedLabelColor = FbBluePrimary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = !filterOnlyUnread,
                                borderColor = if (!filterOnlyUnread) FbBluePrimary else Color(0xFFE2E8F0)
                            )
                        )
                        FilterChip(
                            selected = filterOnlyUnread,
                            onClick = { filterOnlyUnread = true },
                            label = { Text("Belum Dibaca ($unreadCount)", fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = FbBluePrimary.copy(alpha = 0.12f),
                                selectedLabelColor = FbBluePrimary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = filterOnlyUnread,
                                borderColor = if (filterOnlyUnread) FbBluePrimary else Color(0xFFE2E8F0)
                            )
                        )
                    }
                    Divider(color = Color(0xFFE2E8F0), thickness = 0.5.dp)
                }
            }
        },
    ) { paddingValues ->
        if (filteredNotifications.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(FbBg)
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Surface(
                        color = FbBluePrimary.copy(alpha = 0.1f),
                        shape = CircleShape,
                        modifier = Modifier.size(80.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Outlined.NotificationsNone,
                                contentDescription = "Kosong",
                                tint = FbBluePrimary,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        if (filterOnlyUnread) "Tidak ada notifikasi belum dibaca" else "Belum ada notifikasi baru",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = FbTextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "Notifikasi untuk postingan, suka, komentar, pertemanan, dan pesan akan muncul di sini.",
                        color = FbTextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(FbBg)
                    .padding(paddingValues),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(filteredNotifications, key = { it.id }) { notif ->
                    NotificationItemCard(
                        notification = notif,
                        onClick = {
                            viewModel.markNotificationAsRead(notif.id)
                            when (notif.type) {
                                NotificationType.NEW_POST.name,
                                NotificationType.LIKE.name,
                                NotificationType.COMMENT.name -> {
                                    if (notif.targetId > 0) {
                                        viewModel.selectPhotoById(notif.targetId)
                                    }
                                }
                                NotificationType.MESSAGE.name -> {
                                    viewModel.openChatWithUserById(notif.senderUserId)
                                }
                                NotificationType.FRIEND_REQUEST.name,
                                NotificationType.FRIEND_ACCEPTED.name -> {
                                    viewModel.openChatWithUserById(notif.senderUserId)
                                }
                            }
                        },
                        onAcceptFriend = {
                            viewModel.markNotificationAsRead(notif.id)
                            viewModel.acceptFriendRequestByUserId(notif.senderUserId)
                        },
                        onDeclineFriend = {
                            viewModel.markNotificationAsRead(notif.id)
                            viewModel.removeOrCancelFriendship(notif.senderUserId)
                            viewModel.deleteNotification(notif.id)
                        },
                        onDelete = {
                            viewModel.deleteNotification(notif.id)
                        }
                    )
                    Divider(color = Color(0xFFF1F5F9), thickness = 1.dp)
                }
            }
        }
    }
}

@Composable
fun NotificationItemCard(
    notification: NotificationEntity,
    onClick: () -> Unit,
    onAcceptFriend: () -> Unit,
    onDeclineFriend: () -> Unit,
    onDelete: () -> Unit
) {
    val notifType = remember(notification.type) {
        try {
            NotificationType.valueOf(notification.type)
        } catch (_: Exception) {
            NotificationType.NEW_POST
        }
    }

    val (badgeIcon, badgeBg) = when (notifType) {
        NotificationType.LIKE -> Pair(Icons.Filled.ThumbUp, Color(0xFF1877F2))
        NotificationType.COMMENT -> Pair(Icons.Filled.ChatBubble, Color(0xFF10B981))
        NotificationType.NEW_POST -> Pair(Icons.Filled.Campaign, Color(0xFFF59E0B))
        NotificationType.MESSAGE -> Pair(Icons.Filled.Mail, Color(0xFF8B5CF6))
        NotificationType.FRIEND_REQUEST -> Pair(Icons.Filled.PersonAdd, Color(0xFF1877F2))
        NotificationType.FRIEND_ACCEPTED -> Pair(Icons.Filled.CheckCircle, Color(0xFF10B981))
    }

    val timeFormatted = remember(notification.createdAt) {
        formatNotificationTime(notification.createdAt)
    }

    val itemBg = if (!notification.isRead) Color(0xFFEBF5FF) else FbCardBg

    Surface(
        color = itemBg,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("notification_item_${notification.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Avatar with Badge Icon overlay
            Box(modifier = Modifier.size(52.dp)) {
                Surface(
                    shape = CircleShape,
                    color = Color(notification.senderAvatarColor.takeIf { it != 0L } ?: 0xFF1877F2),
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        val iconVector = when (notification.senderAvatarIcon) {
                            "teacher" -> Icons.Default.School
                            "star" -> Icons.Default.Star
                            "palette" -> Icons.Default.Brush
                            "sports" -> Icons.Default.SportsSoccer
                            "music" -> Icons.Default.MusicNote
                            "camp" -> Icons.Default.EmojiFlags
                            else -> Icons.Default.Person
                        }
                        Icon(
                            iconVector,
                            contentDescription = notification.senderName,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // Mini badge icon on bottom right
                Surface(
                    shape = CircleShape,
                    color = badgeBg,
                    border = androidx.compose.foundation.BorderStroke(2.dp, itemBg),
                    modifier = Modifier
                        .size(22.dp)
                        .align(Alignment.BottomEnd)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            badgeIcon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Text info & actions
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        notification.title,
                        fontWeight = if (!notification.isRead) FontWeight.Bold else FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = FbTextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        timeFormatted,
                        fontSize = 11.sp,
                        color = if (!notification.isRead) FbBluePrimary else FbTextSecondary,
                        fontWeight = if (!notification.isRead) FontWeight.SemiBold else FontWeight.Normal
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    notification.message,
                    fontSize = 13.sp,
                    color = if (!notification.isRead) Color(0xFF1E293B) else FbTextSecondary,
                    lineHeight = 18.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                // Inline actions for Friend Requests (Facebook style)
                if (notifType == NotificationType.FRIEND_REQUEST) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = onAcceptFriend,
                            colors = ButtonDefaults.buttonColors(containerColor = FbBluePrimary),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text("Konfirmasi", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        OutlinedButton(
                            onClick = onDeclineFriend,
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text("Hapus", fontSize = 12.sp, color = FbTextSecondary)
                        }
                    }
                }
            }

            // Unread blue dot indicator & delete button
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.padding(start = 6.dp)
            ) {
                if (!notification.isRead) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(FbBluePrimary)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Hapus Notifikasi",
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

private fun formatNotificationTime(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    return when {
        seconds < 60 -> "Baru saja"
        minutes < 60 -> "$minutes mnt lalu"
        hours < 24 -> "$hours jam lalu"
        days < 7 -> "$days hari lalu"
        else -> {
            val sdf = SimpleDateFormat("dd MMM", Locale("id", "ID"))
            sdf.format(Date(timestamp))
        }
    }
}
