package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class UserRole(val label: String, val badgeColorHex: Long) {
    MURID("Siswa", 0xFF1877F2),
    KETUA_KELAS("Ketua Kelas", 0xFFD97706),
    WALI_KELAS("Guru", 0xFF059669)
}

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val username: String,
    val password: String,
    val fullName: String,
    val role: String = UserRole.MURID.name,
    val studentNumber: String = "", // No. Absen, NISN, atau NIP
    val avatarColor: Long = 0xFF1877F2,
    val avatarIcon: String = "teacher",
    val bio: String = "Warga SDN 4 Putrajawa",
    val customPhotoUri: String = "", // Path file foto dari galeri/kamera
    val joinedAt: Long = System.currentTimeMillis()
)
