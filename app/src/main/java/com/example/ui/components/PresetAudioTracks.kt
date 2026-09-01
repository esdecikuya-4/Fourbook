package com.example.ui.components

import androidx.compose.ui.graphics.Color

data class PresetAudioInfo(
    val key: String,
    val title: String,
    val artist: String,
    val genre: String,
    val durationSec: Int,
    val lyricsOrNote: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val waveHeights: List<Float>
)

object PresetAudioTracks {
    val tracks = listOf(
        PresetAudioInfo(
            key = "audio_mars_sdn4",
            title = "Mars SDN 4 Putrajawa",
            artist = "Paduan Suara Siswa & Guru SDN 4",
            genre = "Lagu Wajib Sekolah",
            durationSec = 145,
            lyricsOrNote = "SDN 4 Putrajawa berilmu berakhlak mulia, melangkah pasti meraih cita Indonesia jaya!",
            primaryColor = Color(0xFF1E40AF),
            secondaryColor = Color(0xFF3B82F6),
            waveHeights = listOf(0.3f, 0.6f, 0.8f, 0.95f, 0.7f, 0.85f, 0.6f, 0.9f, 0.75f, 0.4f, 0.8f, 1.0f, 0.65f, 0.5f, 0.7f)
        ),
        PresetAudioInfo(
            key = "audio_hymne_guru",
            title = "Hymne Guru (Terpujilah Wahai Ibu Bapak Guru)",
            artist = "Vokal Bersama Warga SDN 4",
            genre = "Lagu Nasional",
            durationSec = 180,
            lyricsOrNote = "Terpujilah wahai engkau ibu bapak guru, namamu akan selalu hidup dalam sanubariku...",
            primaryColor = Color(0xFF047857),
            secondaryColor = Color(0xFF10B981),
            waveHeights = listOf(0.2f, 0.4f, 0.6f, 0.75f, 0.85f, 0.9f, 0.7f, 0.65f, 0.8f, 0.85f, 0.6f, 0.5f, 0.7f, 0.4f, 0.3f)
        ),
        PresetAudioInfo(
            key = "audio_acoustic_study",
            title = "Musik Akustik Belajar Ceria & Fokus",
            artist = "Klub Musik Akustik SDN 4",
            genre = "Instrumental Edukasi",
            durationSec = 210,
            lyricsOrNote = "Petikan gitar akustik lembut yang menenangkan pikiran untuk menemani belajar dan membaca buku.",
            primaryColor = Color(0xFFD97706),
            secondaryColor = Color(0xFFF59E0B),
            waveHeights = listOf(0.4f, 0.7f, 0.5f, 0.8f, 0.6f, 0.9f, 0.85f, 0.7f, 0.95f, 0.6f, 0.75f, 0.5f, 0.8f, 0.6f, 0.4f)
        ),
        PresetAudioInfo(
            key = "audio_manuk_dadali",
            title = "Lagu Daerah: Manuk Dadali",
            artist = "Ansambel Angklung SDN 4 Putrajawa",
            genre = "Seni Tradisional Sunda",
            durationSec = 165,
            lyricsOrNote = "Mesat ngapung luhur jauh di awang-awang, meberkeun jangjangna bangun taya karingrang...",
            primaryColor = Color(0xFF7C3AED),
            secondaryColor = Color(0xFF8B5CF6),
            waveHeights = listOf(0.5f, 0.8f, 0.95f, 0.7f, 0.85f, 0.6f, 0.9f, 1.0f, 0.75f, 0.8f, 0.65f, 0.9f, 0.55f, 0.7f, 0.45f)
        ),
        PresetAudioInfo(
            key = "audio_guruku_tersayang",
            title = "Guruku Tersayang",
            artist = "Siti Nurhaliza & Vokal SDN 4",
            genre = "Lagu Anak & Edukasi",
            durationSec = 130,
            lyricsOrNote = "Pagiku cerahku matahari bersinar kugendong tas merahku di pundak... Selamat pagi semua!",
            primaryColor = Color(0xFFDB2777),
            secondaryColor = Color(0xFFF43F5E),
            waveHeights = listOf(0.35f, 0.65f, 0.85f, 0.75f, 0.9f, 0.8f, 0.95f, 0.7f, 0.85f, 0.6f, 0.75f, 0.9f, 0.5f, 0.4f, 0.6f)
        )
    )

    fun getByKey(key: String): PresetAudioInfo? {
        return tracks.find { it.key == key } ?: tracks.firstOrNull()
    }

    fun formatDuration(seconds: Int): String {
        val mins = seconds / 60
        val secs = seconds % 60
        return "%02d:%02d".format(mins, secs)
    }
}
