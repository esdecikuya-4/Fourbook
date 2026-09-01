package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.json.JSONArray
import org.json.JSONObject

data class QuizQuestionItem(
    val id: Int = 1,
    val questionText: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctOptionIndex: Int = 0, // 0 = A, 1 = B, 2 = C, 3 = D
    val points: Int = 20,
    val explanation: String = ""
) {
    fun getOptions(): List<String> = listOf(optionA, optionB, optionC, optionD)

    fun toJson(): JSONObject {
        val obj = JSONObject()
        obj.put("id", id)
        obj.put("questionText", questionText)
        obj.put("optionA", optionA)
        obj.put("optionB", optionB)
        obj.put("optionC", optionC)
        obj.put("optionD", optionD)
        obj.put("correctOptionIndex", correctOptionIndex)
        obj.put("points", points)
        obj.put("explanation", explanation)
        return obj
    }

    companion object {
        fun fromJson(obj: JSONObject): QuizQuestionItem {
            return QuizQuestionItem(
                id = obj.optInt("id", 1),
                questionText = obj.optString("questionText", ""),
                optionA = obj.optString("optionA", ""),
                optionB = obj.optString("optionB", ""),
                optionC = obj.optString("optionC", ""),
                optionD = obj.optString("optionD", ""),
                correctOptionIndex = obj.optInt("correctOptionIndex", 0),
                points = obj.optInt("points", 20),
                explanation = obj.optString("explanation", "")
            )
        }

        fun parseQuestionsList(jsonStr: String): List<QuizQuestionItem> {
            val list = mutableListOf<QuizQuestionItem>()
            if (jsonStr.isBlank()) return list
            try {
                val array = JSONArray(jsonStr)
                for (i in 0 until array.length()) {
                    list.add(fromJson(array.getJSONObject(i)))
                }
            } catch (_: Exception) {}
            return list
        }

        fun questionsToJsonString(list: List<QuizQuestionItem>): String {
            val array = JSONArray()
            list.forEach { array.put(it.toJson()) }
            return array.toString()
        }
    }
}

@Entity(tableName = "quizzes")
data class QuizEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val subject: String = "IPAS",
    val description: String = "",
    val durationMinutes: Int = 15,
    val questionsJson: String = "[]",
    val totalPoints: Int = 100,
    val isPublished: Boolean = true,
    val dueDate: String = "",
    val teacherName: String = "Teten Kurniawan",
    val createdAt: Long = System.currentTimeMillis()
) {
    val questions: List<QuizQuestionItem> get() = QuizQuestionItem.parseQuestionsList(questionsJson)
}

@Entity(tableName = "quiz_submissions")
data class QuizSubmissionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val quizId: Long,
    val quizTitle: String,
    val subject: String,
    val studentId: Long,
    val studentName: String,
    val studentNumber: String = "",
    val studentAvatarColor: Long = 0xFF1877F2,
    val studentAvatarIcon: String = "star",
    val studentCustomPhotoUri: String = "",
    val selectedAnswersJson: String = "[]", // e.g. [0, 2, 1, 3]
    val score: Int,
    val totalQuestions: Int,
    val correctCount: Int,
    val isCompleted: Boolean = true,
    val submittedAt: Long = System.currentTimeMillis()
)

enum class JournalCategory(val title: String, val iconName: String, val colorHex: Long, val badgeBg: Long) {
    SIKAP_POSITIF("Sikap Baik & Karakter", "thumb_up", 0xFF16A34A, 0xFFDCFCE7),
    PRESTASI("Prestasi & Bakat", "star", 0xFFD97706, 0xFFFEF3C7),
    PEMBINAAN("Pembinaan / Catatan", "report_problem", 0xFFDC2626, 0xFFFEE2E2),
    KESEHATAN("Kesehatan & Izin", "medical_services", 0xFF0284C7, 0xFFE0F2FE),
    KEGIATAN_HARIAN("Kegiatan Sekolah", "school", 0xFF8B5CF6, 0xFFEDE9FE)
}

@Entity(tableName = "student_journals")
data class StudentJournalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String, // yyyy-MM-dd
    val studentId: Long, // 0 = Seluruh Siswa SDN 4 Putrajawa
    val studentName: String,
    val studentNumber: String = "",
    val studentAvatarColor: Long = 0xFF1877F2,
    val studentAvatarIcon: String = "face_1",
    val studentCustomPhotoUri: String = "",
    val category: String = JournalCategory.SIKAP_POSITIF.name,
    val title: String,
    val description: String,
    val followUpAction: String = "",
    val teacherName: String = "Teten Kurniawan",
    val photoUri: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
