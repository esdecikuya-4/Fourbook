package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class PostType(val title: String, val iconName: String) {
    TEXT_STATUS("Status Teks", "edit_note"),
    PHOTO("Foto", "photo_camera"),
    VIDEO("Video Sekolah", "videocam"),
    AUDIO("Musik & MP3", "music_note")
}

enum class ReactionType(val emoji: String, val label: String, val colorHex: Long) {
    LIKE("👍", "Suka", 0xFF1877F2),
    LOVE("❤️", "Super", 0xFFE41E3F),
    CARE("👏", "Hebat", 0xFFF59E0B),
    HAHA("😆", "Lucu", 0xFFFBBF24),
    FIRE("🔥", "Semangat", 0xFFEA580C)
}

enum class PhotoCategory(val title: String, val iconName: String, val colorHex: Long) {
    SEMUA("Semua", "all", 0xFF1D4ED8),
    BELAJAR("Kegiatan Belajar", "school", 0xFF2563EB),
    KARYA_SENI("Karya & Proyek", "palette", 0xFF8B5CF6),
    OLAHRAGA("Olahraga & Senam", "sports", 0xFFEA580C),
    PRAMUKA("Pramuka & Ekskul", "camp", 0xFF16A34A),
    PRESTASI("Prestasi & Lomba", "trophy", 0xFFCA8A04),
    KEGIATAN_BERSAMA("Acara & Pentas", "party", 0xFFDB2777);

    val displayName: String get() = title
}

@Entity(tableName = "photos")
data class PhotoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String = "",
    val description: String = "",
    val category: String = PhotoCategory.BELAJAR.name,
    val postType: String = PostType.PHOTO.name,
    val imageUri: String = "", // URI, file path, or preset key
    val presetKey: String = "", // e.g., "preset_science", "preset_art", etc.
    val videoUri: String = "",
    val videoDurationSec: Int = 0,
    val videoPresetKey: String = "", // e.g. "video_science", "video_futsal", etc.
    val audioUri: String = "", // URI or file path of MP3/audio uploaded from phone
    val audioTitle: String = "", // Title of music / song / audio track
    val audioArtist: String = "", // Performer, Singer, or Narrator
    val audioDurationSec: Int = 0, // Duration in seconds
    val audioPresetKey: String = "", // e.g. "audio_mars_sdn4", "audio_hymne_guru", "audio_acoustic_study"
    val statusBackgroundKey: String = "", // "none", "gradient_fire", "gradient_ocean", "gradient_sunset", "gradient_purple", "gradient_emerald"
    val feelingOrActivity: String = "", // e.g. "merasa bersemangat 🌟", "sedang belajar Matematika 📐"
    val uploaderId: Long,
    val uploaderName: String,
    val uploaderRole: String,
    val uploaderAvatarColor: Long,
    val uploaderAvatarIcon: String,
    val uploaderCustomPhotoUri: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val likeCount: Int = 0,
    val viewCount: Int = 0, // Total views/listens count
    val likedByUserIds: String = "[]", // JSON or comma separated ids
    val reactionsJson: String = "{}", // e.g. {"LIKE": 3, "LOVE": 2}
    val taggedStudentNames: String = "", // comma separated student names
    val shareCount: Int = 0
)

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val photoId: Long,
    val userId: Long,
    val userName: String,
    val userRole: String,
    val userAvatarColor: Long,
    val userCustomPhotoUri: String = "",
    val text: String,
    val createdAt: Long = System.currentTimeMillis()
)

