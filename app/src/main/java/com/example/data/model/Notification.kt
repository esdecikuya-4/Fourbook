package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class NotificationType {
    NEW_POST,
    LIKE,
    COMMENT,
    MESSAGE,
    FRIEND_REQUEST,
    FRIEND_ACCEPTED
}

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val recipientUserId: Long = 0, // 0 = broadcast to all (like a new class post)
    val senderUserId: Long,
    val senderName: String,
    val senderRole: String = UserRole.MURID.name,
    val senderAvatarColor: Long = 0xFF1877F2,
    val senderAvatarIcon: String = "face_1",
    val senderCustomPhotoUri: String = "",
    val type: String = NotificationType.NEW_POST.name,
    val title: String,
    val message: String,
    val targetId: Long = 0, // photoId / chat recipient / studentId
    val createdAt: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)
