package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class FriendshipStatus {
    PENDING,
    ACCEPTED,
    DECLINED
}

@Entity(tableName = "friendships")
data class FriendshipEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val senderId: Long,
    val receiverId: Long,
    val status: String = FriendshipStatus.ACCEPTED.name,
    val createdAt: Long = System.currentTimeMillis()
)
