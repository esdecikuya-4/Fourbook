package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class AttendanceStatus(
    val code: String,
    val title: String,
    val colorHex: Long,
    val bgHex: Long
) {
    HADIR("H", "Hadir", 0xFF16A34A, 0xFFDCFCE7),
    IZIN("I", "Izin", 0xFFD97706, 0xFFFEF3C7),
    SAKIT("S", "Sakit", 0xFF0284C7, 0xFFE0F2FE),
    ALPA("A", "Alpa", 0xFFDC2626, 0xFFFEE2E2);

    companion object {
        fun fromString(value: String): AttendanceStatus {
            return entries.find { it.name.equals(value, ignoreCase = true) || it.code.equals(value, ignoreCase = true) }
                ?: HADIR
        }
    }
}

@Entity(tableName = "attendance")
data class AttendanceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String, // Format: yyyy-MM-dd (e.g. 2026-08-31)
    val studentId: Long,
    val studentName: String,
    val studentNumber: String = "",
    val avatarColor: Long = 0xFF1877F2,
    val avatarIcon: String = "face_1",
    val customPhotoUri: String = "",
    val status: String = AttendanceStatus.HADIR.name,
    val note: String = "",
    val recordedBy: String = "Wali Kelas",
    val recordedAt: Long = System.currentTimeMillis()
)
