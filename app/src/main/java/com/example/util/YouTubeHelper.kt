package com.example.util

object YouTubeHelper {
    private val YOUTUBE_REGEX = Regex(
        """(?:https?://)?(?:www\.)?(?:youtube\.com/(?:watch\?v=|embed/|shorts/)|youtu\.be/)([a-zA-Z0-9_-]{11})""",
        RegexOption.IGNORE_CASE
    )

    fun extractVideoId(urlOrText: String?): String? {
        if (urlOrText.isNullOrBlank()) return null
        return YOUTUBE_REGEX.find(urlOrText)?.groupValues?.getOrNull(1)
    }

    fun extractEmbedUrl(urlOrText: String?): String? {
        val id = extractVideoId(urlOrText) ?: return null
        return "https://www.youtube.com/embed/$id"
    }

    fun isYouTubeUrl(urlOrText: String?): Boolean {
        return extractVideoId(urlOrText) != null
    }
}
