package com.example.util

object YouTubeHelper {
    private val PATTERNS = listOf(
        // youtu.be/<id>
        Regex("""youtu\.be/([a-zA-Z0-9_-]{11})""", RegexOption.IGNORE_CASE),
        // ?v=<id> or &v=<id> (handles www.youtube.com, m.youtube.com, youtube.com with any query params)
        Regex("""[?&]v=([a-zA-Z0-9_-]{11})""", RegexOption.IGNORE_CASE),
        // youtube.com/embed/<id> or youtube-nocookie.com/embed/<id>
        Regex("""youtube(?:-nocookie)?\.com/embed/([a-zA-Z0-9_-]{11})""", RegexOption.IGNORE_CASE),
        // youtube.com/shorts/<id>
        Regex("""youtube\.com/shorts/([a-zA-Z0-9_-]{11})""", RegexOption.IGNORE_CASE),
        // youtube.com/live/<id>
        Regex("""youtube\.com/live/([a-zA-Z0-9_-]{11})""", RegexOption.IGNORE_CASE),
        // youtube.com/v/<id>
        Regex("""youtube\.com/v/([a-zA-Z0-9_-]{11})""", RegexOption.IGNORE_CASE),
        // fallback regex for watch?v= or youtu.be
        Regex("""(?:https?://)?(?:[a-zA-Z0-9-]+\.)?(?:youtube\.com/(?:watch\?.*v=|embed/|shorts/|live/|v/)|youtu\.be/)([a-zA-Z0-9_-]{11})""", RegexOption.IGNORE_CASE)
    )

    fun extractVideoId(urlOrText: String?): String? {
        if (urlOrText.isNullOrBlank()) return null
        val text = urlOrText.trim()

        // Pure 11-char ID
        if (text.matches(Regex("""^[a-zA-Z0-9_-]{11}$"""))) {
            return text
        }

        for (regex in PATTERNS) {
            val match = regex.find(text)
            if (match != null) {
                val id = match.groupValues.getOrNull(1)
                if (!id.isNullOrBlank() && id.length == 11) {
                    return id
                }
            }
        }
        return null
    }

    fun extractEmbedUrl(urlOrText: String?): String? {
        val id = extractVideoId(urlOrText) ?: return null
        return "https://www.youtube.com/embed/$id"
    }

    fun isYouTubeUrl(urlOrText: String?): Boolean {
        return extractVideoId(urlOrText) != null
    }
}
