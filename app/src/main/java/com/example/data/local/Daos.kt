package com.example.data.local

import androidx.room.*
import com.example.data.model.AttendanceEntity
import com.example.data.model.CommentEntity
import com.example.data.model.PhotoEntity
import com.example.data.model.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users ORDER BY role DESC, fullName ASC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: Long): UserEntity?

    @Query("SELECT * FROM users WHERE LOWER(username) = LOWER(:username) LIMIT 1")
    suspend fun getUserByUsername(username: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    @Delete
    suspend fun deleteUser(user: UserEntity)

    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUserCount(): Int
}

@Dao
interface PhotoDao {
    @Query("SELECT * FROM photos ORDER BY createdAt DESC")
    fun getAllPhotos(): Flow<List<PhotoEntity>>

    @Query("SELECT * FROM photos WHERE category = :category ORDER BY createdAt DESC")
    fun getPhotosByCategory(category: String): Flow<List<PhotoEntity>>

    @Query("SELECT * FROM photos WHERE uploaderId = :userId ORDER BY createdAt DESC")
    fun getPhotosByUser(userId: Long): Flow<List<PhotoEntity>>

    @Query("SELECT * FROM photos WHERE id = :photoId LIMIT 1")
    suspend fun getPhotoById(photoId: Long): PhotoEntity?

    @Query("SELECT * FROM photos WHERE id = :photoId LIMIT 1")
    fun getPhotoFlowById(photoId: Long): Flow<PhotoEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(photo: PhotoEntity): Long

    @Update
    suspend fun updatePhoto(photo: PhotoEntity)

    @Delete
    suspend fun deletePhoto(photo: PhotoEntity)

    @Query("DELETE FROM photos WHERE id = :photoId")
    suspend fun deletePhotoById(photoId: Long)

    @Query("SELECT COUNT(*) FROM photos")
    suspend fun getPhotoCount(): Int
}

@Dao
interface CommentDao {
    @Query("SELECT * FROM comments ORDER BY createdAt ASC")
    fun getAllComments(): Flow<List<CommentEntity>>

    @Query("SELECT * FROM comments WHERE photoId = :photoId ORDER BY createdAt ASC")
    fun getCommentsForPhoto(photoId: Long): Flow<List<CommentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity): Long

    @Update
    suspend fun updateComment(comment: CommentEntity)

    @Delete
    suspend fun deleteComment(comment: CommentEntity)

    @Query("DELETE FROM comments WHERE photoId = :photoId")
    suspend fun deleteCommentsForPhoto(photoId: Long)
}

@Dao
interface AttendanceDao {
    @Query("SELECT * FROM attendance WHERE date = :date ORDER BY studentNumber ASC, studentName ASC")
    fun getAttendanceByDate(date: String): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance WHERE studentId = :studentId ORDER BY date DESC")
    fun getAttendanceForStudent(studentId: Long): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance ORDER BY date DESC, studentName ASC")
    fun getAllAttendance(): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance WHERE studentId = :studentId AND date = :date LIMIT 1")
    suspend fun getAttendanceForStudentOnDate(studentId: Long, date: String): AttendanceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAttendance(attendance: AttendanceEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllAttendance(attendances: List<AttendanceEntity>)

    @Query("DELETE FROM attendance WHERE id = :id")
    suspend fun deleteAttendanceById(id: Long)

    @Query("SELECT COUNT(*) FROM attendance WHERE date = :date")
    suspend fun getAttendanceCountForDate(date: String): Int
}

@Dao
interface QuizDao {
    @Query("SELECT * FROM quizzes ORDER BY createdAt DESC")
    fun getAllQuizzes(): Flow<List<com.example.data.model.QuizEntity>>

    @Query("SELECT * FROM quizzes WHERE id = :quizId LIMIT 1")
    suspend fun getQuizById(quizId: Long): com.example.data.model.QuizEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuiz(quiz: com.example.data.model.QuizEntity): Long

    @Update
    suspend fun updateQuiz(quiz: com.example.data.model.QuizEntity)

    @Query("DELETE FROM quizzes WHERE id = :quizId")
    suspend fun deleteQuizById(quizId: Long)

    @Query("SELECT COUNT(*) FROM quizzes")
    suspend fun getQuizCount(): Int
}

@Dao
interface QuizSubmissionDao {
    @Query("SELECT * FROM quiz_submissions WHERE quizId = :quizId ORDER BY submittedAt DESC")
    fun getSubmissionsForQuiz(quizId: Long): Flow<List<com.example.data.model.QuizSubmissionEntity>>

    @Query("SELECT * FROM quiz_submissions WHERE studentId = :studentId ORDER BY submittedAt DESC")
    fun getSubmissionsForStudent(studentId: Long): Flow<List<com.example.data.model.QuizSubmissionEntity>>

    @Query("SELECT * FROM quiz_submissions WHERE quizId = :quizId AND studentId = :studentId LIMIT 1")
    suspend fun getSubmissionForQuizAndStudent(quizId: Long, studentId: Long): com.example.data.model.QuizSubmissionEntity?

    @Query("SELECT * FROM quiz_submissions WHERE quizId = :quizId AND studentId = :studentId LIMIT 1")
    fun getSubmissionFlow(quizId: Long, studentId: Long): Flow<com.example.data.model.QuizSubmissionEntity?>

    @Query("SELECT * FROM quiz_submissions ORDER BY submittedAt DESC")
    fun getAllSubmissions(): Flow<List<com.example.data.model.QuizSubmissionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubmission(submission: com.example.data.model.QuizSubmissionEntity): Long
}

@Dao
interface StudentJournalDao {
    @Query("SELECT * FROM student_journals ORDER BY date DESC, createdAt DESC")
    fun getAllJournalEntries(): Flow<List<com.example.data.model.StudentJournalEntity>>

    @Query("SELECT * FROM student_journals WHERE studentId = :studentId OR studentId = 0 ORDER BY date DESC, createdAt DESC")
    fun getJournalForStudent(studentId: Long): Flow<List<com.example.data.model.StudentJournalEntity>>

    @Query("SELECT * FROM student_journals WHERE date = :date ORDER BY createdAt DESC")
    fun getJournalByDate(date: String): Flow<List<com.example.data.model.StudentJournalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJournalEntry(entry: com.example.data.model.StudentJournalEntity): Long

    @Update
    suspend fun updateJournalEntry(entry: com.example.data.model.StudentJournalEntity)

    @Query("DELETE FROM student_journals WHERE id = :id")
    suspend fun deleteJournalEntryById(id: Long)

    @Query("SELECT COUNT(*) FROM student_journals")
    suspend fun getJournalCount(): Int
}

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_messages WHERE (senderId = :user1Id AND recipientId = :user2Id) OR (senderId = :user2Id AND recipientId = :user1Id) ORDER BY timestamp ASC")
    fun getConversation(user1Id: Long, user2Id: Long): Flow<List<com.example.data.model.ChatMessageEntity>>

    @Query("SELECT * FROM chat_messages WHERE recipientId = 0 OR recipientId = :userId OR senderId = :userId ORDER BY timestamp DESC")
    fun getRecentMessagesForUser(userId: Long): Flow<List<com.example.data.model.ChatMessageEntity>>

    @Query("SELECT * FROM chat_messages WHERE recipientId = 0 ORDER BY timestamp ASC")
    fun getClassGroupMessages(): Flow<List<com.example.data.model.ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: com.example.data.model.ChatMessageEntity): Long

    @Query("UPDATE chat_messages SET isRead = 1 WHERE recipientId = :userId AND senderId = :senderId")
    suspend fun markAsRead(userId: Long, senderId: Long)

    @Query("DELETE FROM chat_messages WHERE id = :id")
    suspend fun deleteMessageById(id: Long)

    @Query("SELECT COUNT(*) FROM chat_messages WHERE recipientId = :userId AND isRead = 0")
    fun getUnreadCountForUser(userId: Long): Flow<Int>
}

@Dao
interface FriendshipDao {
    @Query("SELECT * FROM friendships WHERE (senderId = :userId OR receiverId = :userId)")
    fun getAllFriendshipsForUser(userId: Long): Flow<List<com.example.data.model.FriendshipEntity>>

    @Query("SELECT * FROM friendships WHERE (senderId = :user1Id AND receiverId = :user2Id) OR (senderId = :user2Id AND receiverId = :user1Id) LIMIT 1")
    fun getFriendshipBetween(user1Id: Long, user2Id: Long): Flow<com.example.data.model.FriendshipEntity?>

    @Query("SELECT * FROM friendships WHERE (senderId = :user1Id AND receiverId = :user2Id) OR (senderId = :user2Id AND receiverId = :user1Id) LIMIT 1")
    suspend fun getFriendshipSync(user1Id: Long, user2Id: Long): com.example.data.model.FriendshipEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFriendship(friendship: com.example.data.model.FriendshipEntity): Long

    @Update
    suspend fun updateFriendship(friendship: com.example.data.model.FriendshipEntity)

    @Query("DELETE FROM friendships WHERE (senderId = :user1Id AND receiverId = :user2Id) OR (senderId = :user2Id AND receiverId = :user1Id)")
    suspend fun deleteFriendshipBetween(user1Id: Long, user2Id: Long)

    @Query("SELECT COUNT(*) FROM friendships WHERE (senderId = :userId OR receiverId = :userId) AND status = 'ACCEPTED'")
    fun getFriendCount(userId: Long): Flow<Int>

    @Query("SELECT * FROM friendships WHERE status = 'ACCEPTED'")
    fun getAllAcceptedFriendships(): Flow<List<com.example.data.model.FriendshipEntity>>
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications WHERE recipientUserId = :userId OR recipientUserId = 0 ORDER BY createdAt DESC")
    fun getNotificationsForUser(userId: Long): Flow<List<com.example.data.model.NotificationEntity>>

    @Query("SELECT COUNT(*) FROM notifications WHERE (recipientUserId = :userId OR recipientUserId = 0) AND isRead = 0 AND senderUserId != :userId")
    fun getUnreadNotificationCount(userId: Long): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: com.example.data.model.NotificationEntity): Long

    @Query("UPDATE notifications SET isRead = 1 WHERE recipientUserId = :userId OR recipientUserId = 0")
    suspend fun markAllNotificationsAsRead(userId: Long)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :notificationId")
    suspend fun markNotificationAsRead(notificationId: Long)

    @Query("DELETE FROM notifications WHERE id = :notificationId")
    suspend fun deleteNotification(notificationId: Long)

    @Query("DELETE FROM notifications WHERE recipientUserId = :userId OR recipientUserId = 0")
    suspend fun clearAllNotifications(userId: Long)
}

