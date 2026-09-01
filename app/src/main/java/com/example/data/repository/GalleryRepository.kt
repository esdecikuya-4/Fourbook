package com.example.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.example.data.local.AppDatabase
import com.example.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID

class GalleryRepository(private val database: AppDatabase, private val context: Context) {
    private val userDao = database.userDao()
    private val photoDao = database.photoDao()
    private val commentDao = database.commentDao()
    private val attendanceDao = database.attendanceDao()
    private val quizDao = database.quizDao()
    private val quizSubmissionDao = database.quizSubmissionDao()
    private val studentJournalDao = database.studentJournalDao()
    private val chatMessageDao = database.chatMessageDao()
    private val friendshipDao = database.friendshipDao()
    private val notificationDao = database.notificationDao()

    val allPhotos: Flow<List<PhotoEntity>> = photoDao.getAllPhotos()
    val allUsers: Flow<List<UserEntity>> = userDao.getAllUsers()
    val allAttendance: Flow<List<AttendanceEntity>> = attendanceDao.getAllAttendance()
    val allQuizzes: Flow<List<QuizEntity>> = quizDao.getAllQuizzes()
    val allJournals: Flow<List<StudentJournalEntity>> = studentJournalDao.getAllJournalEntries()
    val allSubmissions: Flow<List<QuizSubmissionEntity>> = quizSubmissionDao.getAllSubmissions()
    val allComments: Flow<List<CommentEntity>> = commentDao.getAllComments()

    // Notification operations
    fun getNotificationsForUser(userId: Long): Flow<List<NotificationEntity>> =
        notificationDao.getNotificationsForUser(userId)

    fun getUnreadNotificationCount(userId: Long): Flow<Int> =
        notificationDao.getUnreadNotificationCount(userId)

    suspend fun markAllNotificationsAsRead(userId: Long): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            notificationDao.markAllNotificationsAsRead(userId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun markNotificationAsRead(notificationId: Long): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            notificationDao.markNotificationAsRead(notificationId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteNotification(notificationId: Long): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            notificationDao.deleteNotification(notificationId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun clearAllNotifications(userId: Long): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            notificationDao.clearAllNotifications(userId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createNotification(notification: NotificationEntity): Result<Long> = withContext(Dispatchers.IO) {
        try {
            val id = notificationDao.insertNotification(notification)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Friendship operations (Facebook-style)
    fun getAllFriendshipsForUser(userId: Long): Flow<List<FriendshipEntity>> =
        friendshipDao.getAllFriendshipsForUser(userId)

    fun getFriendshipBetween(user1Id: Long, user2Id: Long): Flow<FriendshipEntity?> =
        friendshipDao.getFriendshipBetween(user1Id, user2Id)

    fun getFriendCount(userId: Long): Flow<Int> =
        friendshipDao.getFriendCount(userId)

    suspend fun sendFriendRequest(sender: UserEntity, receiver: UserEntity): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val existing = friendshipDao.getFriendshipSync(sender.id, receiver.id)
            if (existing != null) {
                if (existing.status == FriendshipStatus.ACCEPTED.name) {
                    return@withContext Result.success(Unit)
                }
                val updated = existing.copy(
                    senderId = sender.id,
                    receiverId = receiver.id,
                    status = FriendshipStatus.PENDING.name,
                    createdAt = System.currentTimeMillis()
                )
                friendshipDao.updateFriendship(updated)
            } else {
                friendshipDao.insertFriendship(
                    FriendshipEntity(
                        senderId = sender.id,
                        receiverId = receiver.id,
                        status = FriendshipStatus.PENDING.name,
                        createdAt = System.currentTimeMillis()
                    )
                )
            }
            // Trigger Notification to recipient
            notificationDao.insertNotification(
                NotificationEntity(
                    recipientUserId = receiver.id,
                    senderUserId = sender.id,
                    senderName = sender.fullName,
                    senderRole = sender.role,
                    senderAvatarColor = sender.avatarColor,
                    senderAvatarIcon = sender.avatarIcon,
                    senderCustomPhotoUri = sender.customPhotoUri,
                    type = NotificationType.FRIEND_REQUEST.name,
                    title = "Permintaan Pertemanan",
                    message = "${sender.fullName} mengirimkan permintaan pertemanan kepada Anda.",
                    targetId = sender.id
                )
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun acceptFriendRequest(currentUser: UserEntity, requester: UserEntity): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val existing = friendshipDao.getFriendshipSync(currentUser.id, requester.id)
            if (existing != null) {
                val updated = existing.copy(
                    status = FriendshipStatus.ACCEPTED.name,
                    createdAt = System.currentTimeMillis()
                )
                friendshipDao.updateFriendship(updated)
            } else {
                friendshipDao.insertFriendship(
                    FriendshipEntity(
                        senderId = requester.id,
                        receiverId = currentUser.id,
                        status = FriendshipStatus.ACCEPTED.name,
                        createdAt = System.currentTimeMillis()
                    )
                )
            }
            // Trigger Notification to original requester
            notificationDao.insertNotification(
                NotificationEntity(
                    recipientUserId = requester.id,
                    senderUserId = currentUser.id,
                    senderName = currentUser.fullName,
                    senderRole = currentUser.role,
                    senderAvatarColor = currentUser.avatarColor,
                    senderAvatarIcon = currentUser.avatarIcon,
                    senderCustomPhotoUri = currentUser.customPhotoUri,
                    type = NotificationType.FRIEND_ACCEPTED.name,
                    title = "Pertemanan Diterima",
                    message = "${currentUser.fullName} telah menerima permintaan pertemanan Anda.",
                    targetId = currentUser.id
                )
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removeOrCancelFriendship(user1Id: Long, user2Id: Long): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            friendshipDao.deleteFriendshipBetween(user1Id, user2Id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getSubmissionsForQuiz(quizId: Long): Flow<List<QuizSubmissionEntity>> =
        quizSubmissionDao.getSubmissionsForQuiz(quizId)

    fun getSubmissionForQuizAndStudent(quizId: Long, studentId: Long): Flow<QuizSubmissionEntity?> =
        quizSubmissionDao.getSubmissionFlow(quizId, studentId)

    fun getJournalForStudent(studentId: Long): Flow<List<StudentJournalEntity>> =
        studentJournalDao.getJournalForStudent(studentId)

    suspend fun createQuiz(quiz: QuizEntity): Result<Long> = withContext(Dispatchers.IO) {
        try {
            val id = quizDao.insertQuiz(quiz)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteQuiz(quizId: Long): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            quizDao.deleteQuizById(quizId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun submitQuiz(submission: QuizSubmissionEntity): Result<Long> = withContext(Dispatchers.IO) {
        try {
            val id = quizSubmissionDao.insertSubmission(submission)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addJournalEntry(entry: StudentJournalEntity): Result<Long> = withContext(Dispatchers.IO) {
        try {
            val id = studentJournalDao.insertJournalEntry(entry)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteJournalEntry(id: Long): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            studentJournalDao.deleteJournalEntryById(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateJournalEntry(entry: StudentJournalEntity): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            studentJournalDao.updateJournalEntry(entry)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getAttendanceByDate(date: String): Flow<List<AttendanceEntity>> =
        attendanceDao.getAttendanceByDate(date)

    fun getAttendanceForStudent(studentId: Long): Flow<List<AttendanceEntity>> =
        attendanceDao.getAttendanceForStudent(studentId)

    suspend fun saveOrUpdateAttendance(attendance: AttendanceEntity): Result<Long> = withContext(Dispatchers.IO) {
        try {
            val existing = attendanceDao.getAttendanceForStudentOnDate(attendance.studentId, attendance.date)
            val toSave = if (existing != null) {
                attendance.copy(id = existing.id)
            } else {
                attendance
            }
            val id = attendanceDao.insertOrUpdateAttendance(toSave)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun markAllStudentsPresent(date: String, recorderName: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val userList = userDao.getAllUsers().first().filter { it.role != UserRole.WALI_KELAS.name }
            val list = userList.map { student ->
                val existing = attendanceDao.getAttendanceForStudentOnDate(student.id, date)
                AttendanceEntity(
                    id = existing?.id ?: 0,
                    date = date,
                    studentId = student.id,
                    studentName = student.fullName,
                    studentNumber = student.studentNumber,
                    avatarColor = student.avatarColor,
                    avatarIcon = student.avatarIcon,
                    status = AttendanceStatus.HADIR.name,
                    note = existing?.note ?: "",
                    recordedBy = recorderName,
                    recordedAt = System.currentTimeMillis()
                )
            }
            attendanceDao.insertAllAttendance(list)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getCommentsForPhoto(photoId: Long): Flow<List<CommentEntity>> =
        commentDao.getCommentsForPhoto(photoId)

    suspend fun getUserById(userId: Long): UserEntity? = withContext(Dispatchers.IO) {
        userDao.getUserById(userId)
    }

    suspend fun registerUser(
        username: String,
        password: String,
        fullName: String,
        role: UserRole,
        studentNumber: String,
        avatarColor: Long,
        avatarIcon: String,
        bio: String
    ): Result<UserEntity> = withContext(Dispatchers.IO) {
        val cleanUsername = username.trim().lowercase()
        if (cleanUsername.length < 3) {
            return@withContext Result.failure(Exception("Username minimal 3 karakter"))
        }
        if (password.trim().length < 3) {
            return@withContext Result.failure(Exception("Kata sandi minimal 3 karakter"))
        }
        if (fullName.trim().isEmpty()) {
            return@withContext Result.failure(Exception("Nama lengkap wajib diisi"))
        }

        val existing = userDao.getUserByUsername(cleanUsername)
        if (existing != null) {
            return@withContext Result.failure(Exception("Username '$cleanUsername' sudah digunakan siswa lain"))
        }

        val newUser = UserEntity(
            username = cleanUsername,
            password = password.trim(),
            fullName = fullName.trim(),
            role = role.name,
            studentNumber = studentNumber.trim(),
            avatarColor = avatarColor,
            avatarIcon = avatarIcon,
            bio = if (bio.isBlank()) "Warga SDN 4 Putrajawa" else bio.trim()
        )

        val id = userDao.insertUser(newUser)
        Result.success(newUser.copy(id = id))
    }

    suspend fun loginUser(username: String, password: String): Result<UserEntity> = withContext(Dispatchers.IO) {
        val cleanUsername = username.trim().lowercase()
        val cleanPassword = password.trim()

        var user = userDao.getUserByUsername(cleanUsername)

        // Special handling for admin Teten Kurniawan: login with user: admin & pass: qqq
        if (cleanUsername == "admin" && (cleanPassword == "qqq" || cleanPassword == "admin")) {
            if (user == null) {
                // Auto-create admin if not found
                val adminUser = UserEntity(
                    username = "admin",
                    password = "qqq",
                    fullName = "Teten Kurniawan",
                    role = UserRole.WALI_KELAS.name,
                    studentNumber = "NIP. 197604062010011010",
                    avatarColor = 0xFF1877F2,
                    avatarIcon = "teacher",
                    bio = "Wali Kelas & Admin Resmi Fourbook SDN 4 Putrajawa."
                )
                val id = userDao.insertUser(adminUser)
                return@withContext Result.success(adminUser.copy(id = id))
            } else {
                // Make sure password is qqq and details match Pak Teten Kurniawan
                if (user.password != cleanPassword || user.fullName != "Teten Kurniawan" || user.studentNumber.isBlank()) {
                    val updated = user.copy(
                        password = "qqq",
                        fullName = "Teten Kurniawan",
                        studentNumber = "NIP. 197604062010011010",
                        role = UserRole.WALI_KELAS.name
                    )
                    userDao.updateUser(updated)
                    return@withContext Result.success(updated)
                }
                return@withContext Result.success(user)
            }
        }

        if (user == null) {
            return@withContext Result.failure(Exception("Akun tidak ditemukan. Silakan periksa username atau daftar terlebih dahulu."))
        }

        if (user.password != cleanPassword) {
            return@withContext Result.failure(Exception("Kata sandi salah. Coba lagi."))
        }

        Result.success(user)
    }

    // Chat / Messenger Repository Methods
    fun getConversation(user1Id: Long, user2Id: Long): Flow<List<ChatMessageEntity>> =
        chatMessageDao.getConversation(user1Id, user2Id)

    fun getClassGroupMessages(): Flow<List<ChatMessageEntity>> =
        chatMessageDao.getClassGroupMessages()

    fun getRecentMessagesForUser(userId: Long): Flow<List<ChatMessageEntity>> =
        chatMessageDao.getRecentMessagesForUser(userId)

    fun getUnreadMessageCount(userId: Long): Flow<Int> =
        chatMessageDao.getUnreadCountForUser(userId)

    suspend fun sendChatMessage(message: ChatMessageEntity): Result<Long> = withContext(Dispatchers.IO) {
        try {
            val id = chatMessageDao.insertMessage(message)
            // Trigger Notification to recipient if direct message
            if (message.recipientId != 0L) {
                notificationDao.insertNotification(
                    NotificationEntity(
                        recipientUserId = message.recipientId,
                        senderUserId = message.senderId,
                        senderName = message.senderName,
                        senderRole = message.senderRole,
                        senderAvatarColor = message.senderAvatarColor,
                        senderAvatarIcon = message.senderAvatarIcon,
                        type = NotificationType.MESSAGE.name,
                        title = "Pesan Baru dari ${message.senderName}",
                        message = message.messageText,
                        targetId = message.senderId
                    )
                )
            }
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun markConversationAsRead(userId: Long, senderId: Long): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            chatMessageDao.markAsRead(userId, senderId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteChatMessage(id: Long): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            chatMessageDao.deleteMessageById(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUserProfile(user: UserEntity): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            userDao.updateUser(user)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createPost(
        postType: PostType,
        title: String,
        description: String,
        category: PhotoCategory,
        imageUri: String = "",
        presetKey: String = "",
        videoUri: String = "",
        videoPresetKey: String = "",
        videoDurationSec: Int = 0,
        audioUri: String = "",
        audioTitle: String = "",
        audioArtist: String = "",
        audioDurationSec: Int = 0,
        audioPresetKey: String = "",
        statusBackgroundKey: String = "",
        feelingOrActivity: String = "",
        uploader: UserEntity,
        taggedStudentNames: String = ""
    ): Result<Long> = withContext(Dispatchers.IO) {
        if (postType == PostType.TEXT_STATUS && description.trim().isEmpty() && title.trim().isEmpty()) {
            return@withContext Result.failure(Exception("Status teks tidak boleh kosong"))
        }
        if (postType == PostType.PHOTO && imageUri.isBlank() && presetKey.isBlank()) {
            return@withContext Result.failure(Exception("Pilih foto atau gambar terlebih dahulu"))
        }
        if (postType == PostType.VIDEO && videoUri.isBlank() && videoPresetKey.isBlank()) {
            return@withContext Result.failure(Exception("Pilih video atau klip video terlebih dahulu"))
        }
        if (postType == PostType.AUDIO && audioUri.isBlank() && audioPresetKey.isBlank()) {
            return@withContext Result.failure(Exception("Pilih file musik MP3 atau lagu sekolah terlebih dahulu"))
        }

        val post = PhotoEntity(
            title = title.trim(),
            description = description.trim(),
            category = category.name,
            postType = postType.name,
            imageUri = imageUri,
            presetKey = presetKey,
            videoUri = videoUri,
            videoPresetKey = videoPresetKey,
            videoDurationSec = videoDurationSec,
            audioUri = audioUri,
            audioTitle = if (audioTitle.isBlank()) title.trim() else audioTitle.trim(),
            audioArtist = if (audioArtist.isBlank()) uploader.fullName else audioArtist.trim(),
            audioDurationSec = audioDurationSec,
            audioPresetKey = audioPresetKey,
            statusBackgroundKey = statusBackgroundKey,
            feelingOrActivity = feelingOrActivity.trim(),
            uploaderId = uploader.id,
            uploaderName = uploader.fullName,
            uploaderRole = uploader.role,
            uploaderAvatarColor = uploader.avatarColor,
            uploaderAvatarIcon = uploader.avatarIcon,
            uploaderCustomPhotoUri = uploader.customPhotoUri,
            createdAt = System.currentTimeMillis(),
            likeCount = 0,
            viewCount = 1,
            likedByUserIds = "[]",
            reactionsJson = "{}",
            taggedStudentNames = taggedStudentNames.trim(),
            shareCount = 0
        )

        val id = photoDao.insertPhoto(post)
        // Broadcast notification for new post to class members
        notificationDao.insertNotification(
            NotificationEntity(
                recipientUserId = 0, // Broadcast to all
                senderUserId = uploader.id,
                senderName = uploader.fullName,
                senderRole = uploader.role,
                senderAvatarColor = uploader.avatarColor,
                senderAvatarIcon = uploader.avatarIcon,
                senderCustomPhotoUri = uploader.customPhotoUri,
                type = NotificationType.NEW_POST.name,
                title = "Postingan Baru",
                message = "${uploader.fullName} membuat postingan baru: ${if (title.isNotBlank()) title else description.take(45)}",
                targetId = id
            )
        )
        Result.success(id)
    }

    suspend fun incrementViewCount(photoId: Long): Result<Unit> = withContext(Dispatchers.IO) {
        val photo = photoDao.getPhotoById(photoId) ?: return@withContext Result.failure(Exception("Postingan tidak ditemukan"))
        val updated = photo.copy(viewCount = photo.viewCount + 1)
        photoDao.updatePhoto(updated)
        Result.success(Unit)
    }

    suspend fun saveAudioToInternalStorage(uri: Uri): String? = withContext(Dispatchers.IO) {
        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val fileName = "sdn4_audio_${UUID.randomUUID()}.mp3"
            val file = File(context.filesDir, fileName)
            val outputStream = FileOutputStream(file)
            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun addPhoto(
        title: String,
        description: String,
        category: PhotoCategory,
        imageUri: String,
        presetKey: String,
        uploader: UserEntity,
        taggedStudentNames: String
    ): Result<Long> = createPost(
        postType = PostType.PHOTO,
        title = title,
        description = description,
        category = category,
        imageUri = imageUri,
        presetKey = presetKey,
        uploader = uploader,
        taggedStudentNames = taggedStudentNames
    )

    suspend fun toggleReaction(photoId: Long, userId: Long, reaction: ReactionType = ReactionType.LIKE): Result<Boolean> = withContext(Dispatchers.IO) {
        val photo = photoDao.getPhotoById(photoId)
            ?: return@withContext Result.failure(Exception("Postingan tidak ditemukan"))

        val currentLikedIds = parseLikedUserIds(photo.likedByUserIds).toMutableList()
        val currentReactions = parseUserReactions(photo.reactionsJson).toMutableMap()
        val existingReaction = currentReactions[userId]

        val isNowActive: Boolean
        if (existingReaction != null) {
            if (existingReaction == reaction) {
                // Toggle off (un-react)
                currentLikedIds.remove(userId)
                currentReactions.remove(userId)
                isNowActive = false
            } else {
                // Switch reaction (e.g. from LIKE to LOVE)
                currentReactions[userId] = reaction
                if (!currentLikedIds.contains(userId)) {
                    currentLikedIds.add(userId)
                }
                isNowActive = true
            }
        } else {
            // New reaction
            currentLikedIds.add(userId)
            currentReactions[userId] = reaction
            isNowActive = true
        }

        val updatedJson = "[" + currentLikedIds.distinct().joinToString(",") + "]"
        val updatedReactionsJson = encodeUserReactions(currentReactions)
        val updatedPhoto = photo.copy(
            likeCount = currentLikedIds.distinct().size,
            likedByUserIds = updatedJson,
            reactionsJson = updatedReactionsJson
        )
        photoDao.updatePhoto(updatedPhoto)

        if (isNowActive && userId != photo.uploaderId) {
            val reactingUser = userDao.getUserById(userId)
            if (reactingUser != null) {
                notificationDao.insertNotification(
                    NotificationEntity(
                        recipientUserId = photo.uploaderId,
                        senderUserId = userId,
                        senderName = reactingUser.fullName,
                        senderRole = reactingUser.role,
                        senderAvatarColor = reactingUser.avatarColor,
                        senderAvatarIcon = reactingUser.avatarIcon,
                        senderCustomPhotoUri = reactingUser.customPhotoUri,
                        type = NotificationType.LIKE.name,
                        title = "Reaksi Postingan",
                        message = "${reactingUser.fullName} memberikan reaksi ${reaction.emoji} pada postingan Anda.",
                        targetId = photo.id
                    )
                )
            }
        }
        Result.success(isNowActive)
    }

    suspend fun toggleLike(photoId: Long, userId: Long): Result<Boolean> =
        toggleReaction(photoId, userId, ReactionType.LIKE)

    suspend fun incrementShare(photoId: Long): Result<Unit> = withContext(Dispatchers.IO) {
        val photo = photoDao.getPhotoById(photoId) ?: return@withContext Result.failure(Exception("Postingan tidak ditemukan"))
        val updated = photo.copy(shareCount = photo.shareCount + 1)
        photoDao.updatePhoto(updated)
        Result.success(Unit)
    }

    suspend fun saveVideoToInternalStorage(uri: Uri): String? = withContext(Dispatchers.IO) {
        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val fileName = "class4_video_${UUID.randomUUID()}.mp4"
            val file = File(context.filesDir, fileName)
            val outputStream = FileOutputStream(file)
            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun addComment(
        photoId: Long,
        user: UserEntity,
        text: String
    ): Result<Long> = withContext(Dispatchers.IO) {
        if (text.trim().isEmpty()) {
            return@withContext Result.failure(Exception("Komentar tidak boleh kosong"))
        }

        val comment = CommentEntity(
            photoId = photoId,
            userId = user.id,
            userName = user.fullName,
            userRole = user.role,
            userAvatarColor = user.avatarColor,
            userCustomPhotoUri = user.customPhotoUri,
            text = text.trim(),
            createdAt = System.currentTimeMillis()
        )

        val id = commentDao.insertComment(comment)
        val photo = photoDao.getPhotoById(photoId)
        if (photo != null && photo.uploaderId != user.id) {
            notificationDao.insertNotification(
                NotificationEntity(
                    recipientUserId = photo.uploaderId,
                    senderUserId = user.id,
                    senderName = user.fullName,
                    senderRole = user.role,
                    senderAvatarColor = user.avatarColor,
                    senderAvatarIcon = user.avatarIcon,
                    senderCustomPhotoUri = user.customPhotoUri,
                    type = NotificationType.COMMENT.name,
                    title = "Komentar Baru",
                    message = "${user.fullName} mengomentari: \"${text.take(50)}\"",
                    targetId = photoId
                )
            )
        }
        Result.success(id)
    }

    suspend fun deletePhoto(photoId: Long): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            commentDao.deleteCommentsForPhoto(photoId)
            photoDao.deletePhotoById(photoId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteComment(comment: CommentEntity): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            commentDao.deleteComment(comment)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateComment(comment: CommentEntity, newText: String): Result<Unit> = withContext(Dispatchers.IO) {
        if (newText.trim().isEmpty()) {
            return@withContext Result.failure(Exception("Komentar tidak boleh kosong"))
        }
        try {
            val updated = comment.copy(text = newText.trim())
            commentDao.updateComment(updated)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updatePostContent(
        photoId: Long,
        newTitle: String,
        newFeeling: String = ""
    ): Result<Unit> = withContext(Dispatchers.IO) {
        if (newTitle.trim().isEmpty()) {
            return@withContext Result.failure(Exception("Konten postingan tidak boleh kosong"))
        }
        try {
            val photo = photoDao.getPhotoById(photoId) ?: return@withContext Result.failure(Exception("Postingan tidak ditemukan"))
            val updated = photo.copy(
                title = newTitle.trim(),
                feelingOrActivity = if (newFeeling.isNotBlank()) newFeeling.trim() else photo.feelingOrActivity
            )
            photoDao.updatePhoto(updated)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveImageToInternalStorage(uri: Uri): String? = withContext(Dispatchers.IO) {
        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val fileName = "class4_photo_${UUID.randomUUID()}.jpg"
            val file = File(context.filesDir, fileName)
            val outputStream = FileOutputStream(file)
            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun saveBitmapToInternalStorage(bitmap: Bitmap): String? = withContext(Dispatchers.IO) {
        try {
            val fileName = "class4_cam_${UUID.randomUUID()}.jpg"
            val file = File(context.filesDir, fileName)
            val outputStream = FileOutputStream(file)
            outputStream.use { output ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, output)
            }
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    companion object {
        fun parseLikedUserIds(json: String): List<Long> {
            val clean = json.trim().removeSurrounding("[", "]")
            if (clean.isBlank()) return emptyList()
            return clean.split(",").mapNotNull { it.trim().toLongOrNull() }
        }

        fun parseUserReactions(reactionsJson: String): Map<Long, ReactionType> {
            if (reactionsJson.isBlank() || reactionsJson == "{}") return emptyMap()
            val clean = reactionsJson.trim().removeSurrounding("{", "}")
            if (clean.isBlank()) return emptyMap()
            val map = mutableMapOf<Long, ReactionType>()
            clean.split(",").forEach { entry ->
                val parts = entry.split(":")
                if (parts.size == 2) {
                    val uId = parts[0].trim().removeSurrounding("\"").removeSurrounding("'").toLongOrNull()
                    val rName = parts[1].trim().removeSurrounding("\"").removeSurrounding("'")
                    val rType = try { ReactionType.valueOf(rName) } catch (_: Exception) { ReactionType.LIKE }
                    if (uId != null) {
                        map[uId] = rType
                    }
                }
            }
            return map
        }

        fun encodeUserReactions(map: Map<Long, ReactionType>): String {
            val items = map.entries.joinToString(",") { "\"${it.key}\":\"${it.value.name}\"" }
            return "{$items}"
        }
    }
}
