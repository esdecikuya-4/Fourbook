package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val senderId: Long,
    val senderName: String,
    val senderRole: String = UserRole.MURID.name,
    val senderAvatarColor: Long = 0xFF1877F2,
    val senderAvatarIcon: String = "smile",
    val senderCustomPhoto: String = "",
    val recipientId: Long, // 0 for general class broadcast, or specific user id (e.g. 1 for Pak Teten)
    val recipientName: String,
    val messageText: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val attachmentType: String = "NONE" // NONE, PHOTO, STICKER, QUIZ_LINK
)
