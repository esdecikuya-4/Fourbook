package com.example.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.*
import com.example.data.repository.GalleryRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.json.JSONArray

enum class MainTab {
    BERANDA,
    TEMAN,
    PESAN,
    NOTIFIKASI,
    PROFIL
}

enum class PostTypeFilter(val title: String, val iconName: String) {
    SEMUA("Semua", "all"),
    STATUS("Status Teks", "edit_note"),
    FOTO("Foto", "photo_camera"),
    VIDEO("Video", "videocam"),
    MUSIK("Musik & Lagu", "music_note"),
    KUIS("Kuis & Ulangan", "quiz")
}

enum class GalleryMediaType(val title: String, val iconName: String) {
    SEMUA("Semua Media", "all"),
    FOTO("Foto", "photo_camera"),
    VIDEO("Video", "videocam"),
    MUSIK("Musik MP3", "music_note")
}

enum class GallerySortOrder(val label: String) {
    POPULER_VIEWS("Paling Banyak Dilihat"),
    POPULER_LIKES("Paling Banyak Disukai"),
    TERBARU("Postingan Terakhir / Terbaru")
}

data class StoryItem(
    val id: String,
    val userId: Long,
    val userName: String,
    val userRole: String,
    val avatarColor: Long,
    val avatarIcon: String,
    val customPhotoUri: String = "",
    val title: String,
    val storyText: String,
    val bgGradientKey: String,
    val timeAgo: String,
    val isUnread: Boolean = true
)

data class AttendanceSummary(
    val totalStudents: Int = 0,
    val hadirCount: Int = 0,
    val izinCount: Int = 0,
    val sakitCount: Int = 0,
    val alpaCount: Int = 0,
    val percentage: Int = 100
)

class GalleryViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application, viewModelScope)
    private val repository = GalleryRepository(database, application)

    val allPhotos: StateFlow<List<PhotoEntity>> = repository.allPhotos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allUsers: StateFlow<List<UserEntity>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allQuizzes: StateFlow<List<QuizEntity>> = repository.allQuizzes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allJournals: StateFlow<List<StudentJournalEntity>> = repository.allJournals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSubmissions: StateFlow<List<QuizSubmissionEntity>> = repository.allSubmissions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _lastSyncTime = MutableStateFlow(System.currentTimeMillis())
    val lastSyncTime: StateFlow<Long> = _lastSyncTime.asStateFlow()

    init {
        startRealtimeSync()
    }

    fun triggerManualSync() {
        viewModelScope.launch {
            _isSyncing.value = true
            try {
                repository.syncAll()
                _lastSyncTime.value = System.currentTimeMillis()
            } catch (_: Exception) {}
            _isSyncing.value = false
        }
    }

    private fun startRealtimeSync() {
        viewModelScope.launch {
            // Initial sync immediately
            try {
                _isSyncing.value = true
                repository.syncAll()
                _lastSyncTime.value = System.currentTimeMillis()
            } catch (_: Exception) {}
            _isSyncing.value = false

            // Realtime polling loop every 5 seconds (synchronized with Web's initPolling)
            while (isActive) {
                delay(5000)
                try {
                    repository.syncRemotePosts()
                    repository.syncRemoteUsers()
                    _lastSyncTime.value = System.currentTimeMillis()
                } catch (_: Exception) {
                    // Continue polling on error
                }
            }
        }
    }

    private val _activeTab = MutableStateFlow(MainTab.BERANDA)
    val activeTab: StateFlow<MainTab> = _activeTab.asStateFlow()

    private val _postTypeFilter = MutableStateFlow(PostTypeFilter.SEMUA)
    val postTypeFilter: StateFlow<PostTypeFilter> = _postTypeFilter.asStateFlow()

    private val _composerInitialType = MutableStateFlow(PostType.TEXT_STATUS)
    val composerInitialType: StateFlow<PostType> = _composerInitialType.asStateFlow()

    private val _activeStory = MutableStateFlow<StoryItem?>(null)
    val activeStory: StateFlow<StoryItem?> = _activeStory.asStateFlow()

    val stories: StateFlow<List<StoryItem>> = MutableStateFlow(
        listOf(
            StoryItem(
                id = "story_1",
                userId = 1,
                userName = "Teten Kurniawan",
                userRole = "WALI_KELAS",
                avatarColor = 0xFF1877F2,
                avatarIcon = "teacher",
                title = "Pengumuman Sekolah",
                storyText = "Selamat pagi warga SDN 4 Putrajawa! Ulangan Harian IPAS & Kuis Matematika sudah dibuka di Fourbook. Semangat belajar ya 🌟",
                bgGradientKey = "gradient_ocean",
                timeAgo = "1 jam lalu"
            ),
            StoryItem(
                id = "story_2",
                userId = 2,
                userName = "Dimas Aditya",
                userRole = "KETUA_KELAS",
                avatarColor = 0xFFD97706,
                avatarIcon = "star",
                title = "Piket Kebersihan",
                storyText = "Teman-teman piket regu melati, mari rapikan perpustakaan dan ruang kelas sebelum bel berbunyi! 🧹✨",
                bgGradientKey = "gradient_sunset",
                timeAgo = "2 jam lalu"
            ),
            StoryItem(
                id = "story_3",
                userId = 4,
                userName = "Rizky Alfaridzi",
                userRole = "MURID",
                avatarColor = 0xFF3B82F6,
                avatarIcon = "sports",
                title = "Latihan Futsal",
                storyText = "Sore ini ada sparing tanding futsal di lapangan SDN 4 Putrajawa. Siap bawa sepatu bola! ⚽🔥",
                bgGradientKey = "gradient_ocean",
                timeAgo = "3 jam lalu"
            ),
            StoryItem(
                id = "story_4",
                userId = 3,
                userName = "Siti Nurhaliza",
                userRole = "MURID",
                avatarColor = 0xFFEC4899,
                avatarIcon = "palette",
                title = "Karya Lukis",
                storyText = "Alhamdulillah lukisan pemandangan sawah SDN 4 Putrajawa sudah selesai 100%! 🎨🌸",
                bgGradientKey = "gradient_purple",
                timeAgo = "4 jam lalu"
            ),
            StoryItem(
                id = "story_5",
                userId = 6,
                userName = "Farhan Maulana",
                userRole = "MURID",
                avatarColor = 0xFF10B981,
                avatarIcon = "camp",
                title = "Pramuka Siaga",
                storyText = "Regu Elang siap untuk jelajah alam dan sandi morse hari Sabtu besok! ⛺⚜️",
                bgGradientKey = "gradient_fire",
                timeAgo = "5 jam lalu"
            )
        )
    ).asStateFlow()

    private val _selectedCategory = MutableStateFlow(PhotoCategory.SEMUA)
    val selectedCategory: StateFlow<PhotoCategory> = _selectedCategory.asStateFlow()

    // Gallery Showcase Tab States (Filter by Media Type: Foto/Video/Musik, Sort by Views/Likes/Terbaru)
    private val _galleryMediaType = MutableStateFlow(GalleryMediaType.SEMUA)
    val galleryMediaType: StateFlow<GalleryMediaType> = _galleryMediaType.asStateFlow()

    private val _gallerySortOrder = MutableStateFlow(GallerySortOrder.POPULER_VIEWS)
    val gallerySortOrder: StateFlow<GallerySortOrder> = _gallerySortOrder.asStateFlow()

    val galleryMediaItems: StateFlow<List<PhotoEntity>> = combine(
        allPhotos,
        _galleryMediaType,
        _gallerySortOrder
    ) { photos, mediaType, sortOrder ->
        val mediaOnly = photos.filter { it.postType in listOf(PostType.PHOTO.name, PostType.VIDEO.name, PostType.AUDIO.name) }
        val filtered = when (mediaType) {
            GalleryMediaType.SEMUA -> mediaOnly
            GalleryMediaType.FOTO -> mediaOnly.filter { it.postType == PostType.PHOTO.name }
            GalleryMediaType.VIDEO -> mediaOnly.filter { it.postType == PostType.VIDEO.name }
            GalleryMediaType.MUSIK -> mediaOnly.filter { it.postType == PostType.AUDIO.name }
        }
        when (sortOrder) {
            GallerySortOrder.POPULER_VIEWS -> filtered.sortedWith(compareByDescending<PhotoEntity> { it.viewCount }.thenByDescending { it.likeCount })
            GallerySortOrder.POPULER_LIKES -> filtered.sortedWith(compareByDescending<PhotoEntity> { it.likeCount }.thenByDescending { it.viewCount })
            GallerySortOrder.TERBARU -> filtered.sortedByDescending { it.createdAt }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filterStudentId = MutableStateFlow<Long?>(null)
    val filterStudentId: StateFlow<Long?> = _filterStudentId.asStateFlow()

    private val _selectedPhoto = MutableStateFlow<PhotoEntity?>(null)
    val selectedPhoto: StateFlow<PhotoEntity?> = _selectedPhoto.asStateFlow()

    private val _photoComments = MutableStateFlow<List<CommentEntity>>(emptyList())
    val photoComments: StateFlow<List<CommentEntity>> = _photoComments.asStateFlow()

    val allComments: StateFlow<List<CommentEntity>> = repository.allComments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val commentsByPhotoId: StateFlow<Map<Long, List<CommentEntity>>> = allComments
        .map { list -> list.groupBy { it.photoId } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    private val _authSuccess = MutableStateFlow<String?>(null)
    val authSuccess: StateFlow<String?> = _authSuccess.asStateFlow()

    private val _showUploadSheet = MutableStateFlow(false)
    val showUploadSheet: StateFlow<Boolean> = _showUploadSheet.asStateFlow()

    private val _showQuickSwitchSheet = MutableStateFlow(false)
    val showQuickSwitchSheet: StateFlow<Boolean> = _showQuickSwitchSheet.asStateFlow()

    // Quiz Modals & Taking State
    private val _activeQuizToTake = MutableStateFlow<QuizEntity?>(null)
    val activeQuizToTake: StateFlow<QuizEntity?> = _activeQuizToTake.asStateFlow()

    private val _showCreateQuizSheet = MutableStateFlow(false)
    val showCreateQuizSheet: StateFlow<Boolean> = _showCreateQuizSheet.asStateFlow()

    private val _showCreateJournalSheet = MutableStateFlow(false)
    val showCreateJournalSheet: StateFlow<Boolean> = _showCreateJournalSheet.asStateFlow()

    private val _showAdminMenuSheet = MutableStateFlow(false)
    val showAdminMenuSheet: StateFlow<Boolean> = _showAdminMenuSheet.asStateFlow()

    private val _selectedQuizForSubmissions = MutableStateFlow<QuizEntity?>(null)
    val selectedQuizForSubmissions: StateFlow<QuizEntity?> = _selectedQuizForSubmissions.asStateFlow()

    // Attendance State
    private val _selectedAttendanceDate = MutableStateFlow(
        java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
    )
    val selectedAttendanceDate: StateFlow<String> = _selectedAttendanceDate.asStateFlow()

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val attendanceForSelectedDate: StateFlow<List<AttendanceEntity>> = _selectedAttendanceDate
        .flatMapLatest { date -> repository.getAttendanceByDate(date) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val attendanceSummary: StateFlow<AttendanceSummary> = combine(
        allUsers,
        attendanceForSelectedDate
    ) { users, attendanceList ->
        val students = users.filter { it.role != UserRole.WALI_KELAS.name }
        val total = if (students.isNotEmpty()) students.size else attendanceList.size
        val hadir = attendanceList.count { it.status == AttendanceStatus.HADIR.name }
        val izin = attendanceList.count { it.status == AttendanceStatus.IZIN.name }
        val sakit = attendanceList.count { it.status == AttendanceStatus.SAKIT.name }
        val alpa = attendanceList.count { it.status == AttendanceStatus.ALPA.name }
        val pct = if (total > 0) ((hadir.toFloat() / total.toFloat()) * 100).toInt() else 100
        AttendanceSummary(
            totalStudents = total,
            hadirCount = hadir,
            izinCount = izin,
            sakitCount = sakit,
            alpaCount = alpa,
            percentage = pct
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AttendanceSummary())

    // Messenger / Chat States
    private val _activeChatRecipient = MutableStateFlow<UserEntity?>(null) // null = General Class Broadcast / Forum Chat
    val activeChatRecipient: StateFlow<UserEntity?> = _activeChatRecipient.asStateFlow()

    private val _showChatConversationSheet = MutableStateFlow(false)
    val showChatConversationSheet: StateFlow<Boolean> = _showChatConversationSheet.asStateFlow()

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val activeConversationMessages: StateFlow<List<ChatMessageEntity>> = combine(
        _currentUser,
        _activeChatRecipient
    ) { user, recipient ->
        Pair(user, recipient)
    }.flatMapLatest { (user, recipient) ->
        if (user == null) {
            flowOf(emptyList())
        } else if (recipient == null) {
            // General group / class broadcast
            repository.getClassGroupMessages()
        } else {
            // Direct 1-on-1 chat
            repository.getConversation(user.id, recipient.id)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val recentMessages: StateFlow<List<ChatMessageEntity>> = _currentUser
        .flatMapLatest { user ->
            if (user != null) repository.getRecentMessagesForUser(user.id)
            else repository.getClassGroupMessages()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val unreadMessagesCount: StateFlow<Int> = _currentUser
        .flatMapLatest { user ->
            if (user != null) repository.getUnreadMessageCount(user.id)
            else flowOf(0)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Notifications State
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val notifications: StateFlow<List<NotificationEntity>> = _currentUser
        .flatMapLatest { user ->
            repository.getNotificationsForUser(user?.id ?: 0L)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val unreadNotificationsCount: StateFlow<Int> = _currentUser
        .flatMapLatest { user ->
            repository.getUnreadNotificationCount(user?.id ?: 0L)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Friendships State
    val allAcceptedFriendships: StateFlow<List<FriendshipEntity>> = repository.allAcceptedFriendships
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val myFriendships: StateFlow<List<FriendshipEntity>> = _currentUser
        .flatMapLatest { user ->
            if (user != null) repository.getAllFriendshipsForUser(user.id)
            else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val friendCount: StateFlow<Int> = _currentUser
        .flatMapLatest { user ->
            if (user != null) repository.getFriendCount(user.id)
            else flowOf(0)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun markAllNotificationsAsRead() {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.markAllNotificationsAsRead(user.id)
        }
    }

    fun markNotificationAsRead(notificationId: Long) {
        viewModelScope.launch {
            repository.markNotificationAsRead(notificationId)
        }
    }

    fun deleteNotification(notificationId: Long) {
        viewModelScope.launch {
            repository.deleteNotification(notificationId)
        }
    }

    fun clearAllNotifications() {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.clearAllNotifications(user.id)
        }
    }

    fun sendFriendRequest(receiver: UserEntity) {
        val user = _currentUser.value ?: return
        if (user.id == receiver.id) return
        viewModelScope.launch {
            repository.sendFriendRequest(user, receiver)
        }
    }

    fun acceptFriendRequest(requester: UserEntity) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.acceptFriendRequest(user, requester)
        }
    }

    fun acceptFriendRequestByUserId(requesterId: Long) {
        val user = _currentUser.value ?: return
        val users = allUsers.value
        val requester = users.find { it.id == requesterId } ?: return
        viewModelScope.launch {
            repository.acceptFriendRequest(user, requester)
        }
    }

    fun removeOrCancelFriendship(targetUserId: Long) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.removeOrCancelFriendship(user.id, targetUserId)
        }
    }

    fun selectPhotoById(photoId: Long) {
        val photo = allPhotos.value.find { it.id == photoId }
        if (photo != null) {
            selectPhoto(photo)
        }
    }

    fun openChatWithUserById(userId: Long) {
        val user = allUsers.value.find { it.id == userId }
        if (user != null) {
            openChatWith(user)
        }
    }

    // Dedicated Fullscreen Admin Managers for Quiz & Journal
    private val _showAdminQuizManager = MutableStateFlow(false)
    val showAdminQuizManager: StateFlow<Boolean> = _showAdminQuizManager.asStateFlow()

    private val _showAdminJournalManager = MutableStateFlow(false)
    val showAdminJournalManager: StateFlow<Boolean> = _showAdminJournalManager.asStateFlow()

    fun setShowAdminQuizManager(show: Boolean) {
        _showAdminQuizManager.value = show
    }

    fun setShowAdminJournalManager(show: Boolean) {
        _showAdminJournalManager.value = show
    }

    // Selected Member Profile for Add Friend & Profile Inspection Dialog
    private val _selectedMemberProfile = MutableStateFlow<UserEntity?>(null)
    val selectedMemberProfile: StateFlow<UserEntity?> = _selectedMemberProfile.asStateFlow()

    fun showMemberProfile(user: UserEntity?) {
        _selectedMemberProfile.value = user
    }

    fun showMemberProfileById(userId: Long) {
        val found = allUsers.value.find { it.id == userId }
        if (found != null) {
            _selectedMemberProfile.value = found
        } else {
            viewModelScope.launch {
                val user = repository.getUserById(userId)
                _selectedMemberProfile.value = user
            }
        }
    }

    fun dismissMemberProfile() {
        _selectedMemberProfile.value = null
    }

    fun openChatWith(recipient: UserEntity?) {
        _activeChatRecipient.value = recipient
        _showChatConversationSheet.value = true
        _activeTab.value = MainTab.PESAN
        // Mark as read if user is logged in
        val user = _currentUser.value
        if (user != null && recipient != null) {
            viewModelScope.launch {
                repository.markConversationAsRead(user.id, recipient.id)
            }
        }
    }

    fun closeChatConversation() {
        _showChatConversationSheet.value = false
    }

    fun sendChatMessage(text: String, attachmentType: String = "NONE") {
        val user = _currentUser.value ?: return
        if (text.isBlank()) return
        val recipient = _activeChatRecipient.value
        val msg = ChatMessageEntity(
            senderId = user.id,
            senderName = user.fullName,
            senderRole = user.role,
            senderAvatarColor = user.avatarColor,
            senderAvatarIcon = user.avatarIcon,
            senderCustomPhoto = user.customPhotoUri,
            recipientId = recipient?.id ?: 0L,
            recipientName = recipient?.fullName ?: "Semua Teman & Guru SDN 4",
            messageText = text.trim(),
            timestamp = System.currentTimeMillis(),
            isRead = false,
            attachmentType = attachmentType
        )
        viewModelScope.launch {
            repository.sendChatMessage(msg)
        }
    }

    fun sendWaveMessage(targetUser: UserEntity) {
        val user = _currentUser.value ?: return
        val senderFirstName = user.fullName.split(" ").firstOrNull() ?: user.fullName
        val targetFirstName = targetUser.fullName.split(" ").firstOrNull() ?: targetUser.fullName
        val text = "👋 Halo $targetFirstName! Salam kenal dari $senderFirstName!"
        val msg = ChatMessageEntity(
            senderId = user.id,
            senderName = user.fullName,
            senderRole = user.role,
            senderAvatarColor = user.avatarColor,
            senderAvatarIcon = user.avatarIcon,
            senderCustomPhoto = user.customPhotoUri,
            recipientId = targetUser.id,
            recipientName = targetUser.fullName,
            messageText = text,
            timestamp = System.currentTimeMillis(),
            isRead = false,
            attachmentType = "STICKER"
        )
        viewModelScope.launch {
            repository.sendChatMessage(msg)
        }
    }

    fun deleteChatMessage(id: Long) {
        viewModelScope.launch {
            repository.deleteChatMessage(id)
        }
    }

    // Tab & Navigation Handlers
    fun setActiveTab(tab: MainTab) {
        _activeTab.value = tab
    }

    fun setPostTypeFilter(filter: PostTypeFilter) {
        _postTypeFilter.value = filter
    }

    fun setSelectedCategory(category: PhotoCategory) {
        _selectedCategory.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setFilterStudent(studentId: Long?) {
        _filterStudentId.value = studentId
    }

    fun openComposer(type: PostType = PostType.TEXT_STATUS) {
        _composerInitialType.value = type
        _showUploadSheet.value = true
    }

    fun setShowUploadSheet(show: Boolean) {
        _showUploadSheet.value = show
    }

    fun setShowQuickSwitchSheet(show: Boolean) {
        _showQuickSwitchSheet.value = show
    }

    fun setShowAdminMenuSheet(show: Boolean) {
        _showAdminMenuSheet.value = show
    }

    fun setShowCreateQuizSheet(show: Boolean) {
        _showCreateQuizSheet.value = show
    }

    fun setShowCreateJournalSheet(show: Boolean) {
        _showCreateJournalSheet.value = show
    }

    fun setActiveQuizToTake(quiz: QuizEntity?) {
        _activeQuizToTake.value = quiz
    }

    fun setSelectedQuizForSubmissions(quiz: QuizEntity?) {
        _selectedQuizForSubmissions.value = quiz
    }

    fun openStory(story: StoryItem) {
        _activeStory.value = story
    }

    fun closeStory() {
        _activeStory.value = null
    }

    // Attendance Methods
    fun setSelectedAttendanceDate(date: String) {
        _selectedAttendanceDate.value = date
    }

    fun setStudentAttendance(
        student: UserEntity,
        status: AttendanceStatus,
        note: String = ""
    ) {
        val currentRecorder = _currentUser.value?.fullName ?: "Teten Kurniawan"
        val date = _selectedAttendanceDate.value
        viewModelScope.launch {
            val record = AttendanceEntity(
                date = date,
                studentId = student.id,
                studentName = student.fullName,
                studentNumber = student.studentNumber,
                avatarColor = student.avatarColor,
                avatarIcon = student.avatarIcon,
                customPhotoUri = student.customPhotoUri,
                status = status.name,
                note = note,
                recordedBy = currentRecorder,
                recordedAt = System.currentTimeMillis()
            )
            repository.saveOrUpdateAttendance(record)
        }
    }

    fun markAllStudentsPresent() {
        val recorder = _currentUser.value?.fullName ?: "Teten Kurniawan"
        val date = _selectedAttendanceDate.value
        viewModelScope.launch {
            repository.markAllStudentsPresent(date, recorder)
        }
    }

    fun exportAttendanceRecapText(): String {
        val date = _selectedAttendanceDate.value
        val summary = attendanceSummary.value
        val list = attendanceForSelectedDate.value
        val sb = StringBuilder()
        sb.append("📋 REKAPITULASI ABSENSI SISWA SDN 4 PUTRAJAWA\n")
        sb.append("Tanggal: $date\n")
        sb.append("Guru Pencatat: ${_currentUser.value?.fullName ?: "Teten Kurniawan"}\n")
        sb.append("-------------------------------------------\n")
        sb.append("Total Siswa: ${summary.totalStudents}\n")
        sb.append("Hadir: ${summary.hadirCount} | Sakit: ${summary.sakitCount} | Izin: ${summary.izinCount} | Alpa: ${summary.alpaCount}\n")
        sb.append("Tingkat Kehadiran: ${summary.percentage}%\n")
        sb.append("-------------------------------------------\n")
        sb.append("DAFTAR SISWA:\n")
        list.forEachIndexed { index, att ->
            val numStr = if (att.studentNumber.isNotBlank()) "(${att.studentNumber})" else ""
            val noteStr = if (att.note.isNotBlank()) " - Ket: ${att.note}" else ""
            sb.append("${index + 1}. ${att.studentName} $numStr : [${att.status}]$noteStr\n")
        }
        sb.append("-------------------------------------------\n")
        sb.append("Fourbook • SDN 4 Putrajawa")
        return sb.toString()
    }

    // Photo & Feed Selection
    fun selectPhoto(photo: PhotoEntity?) {
        _selectedPhoto.value = photo
        if (photo != null) {
            viewModelScope.launch {
                repository.getCommentsForPhoto(photo.id).collect { comments ->
                    _photoComments.value = comments
                }
            }
        } else {
            _photoComments.value = emptyList()
        }
    }

    // Auth & Profile
    fun clearAuthMessages() {
        _authError.value = null
        _authSuccess.value = null
    }

    fun login(username: String, pass: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _authError.value = null
            val result = repository.loginUser(username, pass)
            result.onSuccess { user ->
                _currentUser.value = user
                _authSuccess.value = "Selamat datang kembali, ${user.fullName}!"
                onSuccess()
            }.onFailure { error ->
                _authError.value = error.message ?: "Login gagal"
            }
        }
    }

    fun register(
        username: String,
        pass: String,
        fullName: String,
        role: UserRole,
        studentNumber: String,
        avatarColor: Long,
        avatarIcon: String,
        bio: String,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _authError.value = null
            val result = repository.registerUser(
                username = username,
                password = pass,
                fullName = fullName,
                role = role,
                studentNumber = studentNumber,
                avatarColor = avatarColor,
                avatarIcon = avatarIcon,
                bio = bio
            )
            result.onSuccess { user ->
                _currentUser.value = user
                _authSuccess.value = "Pendaftaran berhasil! Selamat datang, ${user.fullName}."
                onSuccess()
            }.onFailure { error ->
                _authError.value = error.message ?: "Pendaftaran gagal"
            }
        }
    }

    fun switchUser(user: UserEntity) {
        _currentUser.value = user
        _showQuickSwitchSheet.value = false
        viewModelScope.launch {
            repository.ensureRemoteSession(user)
        }
    }

    fun logout() {
        _currentUser.value = null
    }

    fun updateProfile(
        fullName: String,
        username: String = "",
        password: String = "",
        studentNumber: String,
        avatarColor: Long,
        avatarIcon: String,
        bio: String,
        customPhotoUri: String = ""
    ) {
        val current = _currentUser.value ?: return
        val updated = current.copy(
            fullName = fullName.trim(),
            username = if (username.isNotBlank()) username.trim().lowercase() else current.username,
            password = if (password.isNotBlank()) password.trim() else current.password,
            studentNumber = studentNumber.trim(),
            avatarColor = avatarColor,
            avatarIcon = avatarIcon,
            bio = bio.trim(),
            customPhotoUri = customPhotoUri
        )
        viewModelScope.launch {
            repository.updateUserProfile(updated)
            _currentUser.value = updated
        }
    }

    fun saveProfilePhotoUri(uri: Uri, onDone: (String) -> Unit) {
        viewModelScope.launch {
            val path = repository.saveImageToInternalStorage(uri)
            if (path != null) {
                onDone(path)
            }
        }
    }

    fun saveProfilePhotoBitmap(bitmap: Bitmap, onDone: (String) -> Unit) {
        viewModelScope.launch {
            val path = repository.saveBitmapToInternalStorage(bitmap)
            if (path != null) {
                onDone(path)
            }
        }
    }

    // Quiz Methods
    fun createQuiz(
        title: String,
        subject: String,
        description: String,
        durationMinutes: Int,
        questions: List<QuizQuestionItem>,
        dueDate: String,
        onSuccess: () -> Unit
    ) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val quiz = QuizEntity(
                title = title.trim(),
                subject = subject.trim(),
                description = description.trim(),
                durationMinutes = durationMinutes,
                questionsJson = QuizQuestionItem.questionsToJsonString(questions),
                totalPoints = questions.sumOf { it.points },
                isPublished = true,
                dueDate = dueDate.trim(),
                teacherName = user.fullName,
                createdAt = System.currentTimeMillis()
            )
            val id = repository.createQuiz(quiz)
            // Also create a Feed Post to notify all students
            repository.createPost(
                postType = PostType.TEXT_STATUS,
                title = "📝 Kuis / Soal Ulangan Baru: $title",
                description = "Pak ${user.fullName} telah mempublikasikan kuis interaktif mata pelajaran $subject: '$title'. $description\n\n📌 Durasi: $durationMinutes menit | Total Poin: ${quiz.totalPoints} | Tenggat: $dueDate",
                category = PhotoCategory.BELAJAR,
                statusBackgroundKey = "gradient_ocean",
                feelingOrActivity = "sedang memberikan tugas kuis 📚",
                uploader = user,
                taggedStudentNames = "Semua Siswa SDN 4 Putrajawa"
            )
            _showCreateQuizSheet.value = false
            onSuccess()
        }
    }

    fun deleteQuiz(quizId: Long) {
        viewModelScope.launch {
            repository.deleteQuiz(quizId)
        }
    }

    fun submitQuizAnswers(
        quiz: QuizEntity,
        selectedAnswers: Map<Int, Int>, // question index -> selected option index (0..3)
        onDone: (QuizSubmissionEntity) -> Unit
    ) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            var correctCount = 0
            var earnedScore = 0
            val answersArray = JSONArray()

            quiz.questions.forEachIndexed { index, q ->
                val chosen = selectedAnswers[index] ?: -1
                answersArray.put(chosen)
                if (chosen == q.correctOptionIndex) {
                    correctCount++
                    earnedScore += q.points
                }
            }

            val submission = QuizSubmissionEntity(
                quizId = quiz.id,
                quizTitle = quiz.title,
                subject = quiz.subject,
                studentId = user.id,
                studentName = user.fullName,
                studentNumber = user.studentNumber,
                studentAvatarColor = user.avatarColor,
                studentAvatarIcon = user.avatarIcon,
                studentCustomPhotoUri = user.customPhotoUri,
                selectedAnswersJson = answersArray.toString(),
                score = earnedScore,
                totalQuestions = quiz.questions.size,
                correctCount = correctCount,
                isCompleted = true,
                submittedAt = System.currentTimeMillis()
            )

            repository.submitQuiz(submission)
            _activeQuizToTake.value = null
            onDone(submission)
        }
    }

    // Student Journal Methods
    fun addJournalEntry(
        student: UserEntity?,
        category: JournalCategory,
        title: String,
        description: String,
        followUpAction: String,
        photoUri: String = "",
        onSuccess: () -> Unit
    ) {
        val user = _currentUser.value ?: return
        val todayDate = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
        viewModelScope.launch {
            val entry = StudentJournalEntity(
                date = todayDate,
                studentId = student?.id ?: 0L,
                studentName = student?.fullName ?: "Seluruh Siswa SDN 4 Putrajawa",
                studentNumber = student?.studentNumber ?: "Semua",
                studentAvatarColor = student?.avatarColor ?: 0xFF1877F2,
                studentAvatarIcon = student?.avatarIcon ?: "school",
                studentCustomPhotoUri = student?.customPhotoUri ?: "",
                category = category.name,
                title = title.trim(),
                description = description.trim(),
                followUpAction = followUpAction.trim(),
                teacherName = user.fullName,
                photoUri = photoUri,
                createdAt = System.currentTimeMillis()
            )
            repository.addJournalEntry(entry)
            _showCreateJournalSheet.value = false
            onSuccess()
        }
    }

    fun deleteJournalEntry(id: Long) {
        viewModelScope.launch {
            repository.deleteJournalEntry(id)
        }
    }

    // Feed Interactions
    fun toggleReaction(photoId: Long, reactionType: ReactionType = ReactionType.LIKE) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.toggleReaction(photoId, user.id, reactionType)
        }
    }

    fun toggleLike(photoId: Long) {
        toggleReaction(photoId, ReactionType.LIKE)
    }

    fun addComment(photoId: Long, text: String) {
        val user = _currentUser.value ?: return
        if (text.isBlank()) return
        viewModelScope.launch {
            repository.addComment(photoId, user, text)
        }
    }

    fun deletePhoto(photoId: Long, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            repository.deletePhoto(photoId)
            if (_selectedPhoto.value?.id == photoId) {
                _selectedPhoto.value = null
            }
            onSuccess()
        }
    }

    fun deleteComment(comment: CommentEntity) {
        viewModelScope.launch {
            repository.deleteComment(comment)
        }
    }

    fun updateComment(comment: CommentEntity, newText: String) {
        viewModelScope.launch {
            repository.updateComment(comment, newText)
        }
    }

    fun updatePost(photoId: Long, newTitle: String, newFeeling: String = "") {
        viewModelScope.launch {
            repository.updatePostContent(photoId, newTitle, newFeeling)
        }
    }

    fun sharePost(photo: PhotoEntity): String {
        viewModelScope.launch {
            repository.incrementShare(photo.id)
        }
        val typeLabel = when (photo.postType) {
            PostType.VIDEO.name -> "🎥 [Video SDN 4 Putrajawa]"
            PostType.TEXT_STATUS.name -> "📝 [Status SDN 4 Putrajawa]"
            else -> "📸 [Foto Galeri SDN 4 Putrajawa]"
        }
        val feelingText = if (photo.feelingOrActivity.isNotBlank()) " — ${photo.feelingOrActivity}" else ""
        return "$typeLabel\nOleh: ${photo.uploaderName}$feelingText\n\n${if (photo.title.isNotBlank()) "*${photo.title}*\n" else ""}${photo.description}\n\n🏫 Fourbook SDN 4 Putrajawa"
    }

    fun createStatusPost(
        text: String,
        bgGradientKey: String,
        feeling: String,
        category: PhotoCategory,
        taggedStudentNames: String,
        onSuccess: () -> Unit
    ) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.createPost(
                postType = PostType.TEXT_STATUS,
                title = "",
                description = text.trim(),
                category = category,
                statusBackgroundKey = bgGradientKey,
                feelingOrActivity = feeling.trim(),
                uploader = user,
                taggedStudentNames = taggedStudentNames
            )
            _showUploadSheet.value = false
            onSuccess()
        }
    }

    fun createVideoPost(
        title: String,
        description: String,
        category: PhotoCategory,
        videoUri: Uri?,
        videoPresetKey: String,
        videoDurationSec: Int,
        feeling: String,
        taggedStudentNames: String,
        onSuccess: () -> Unit
    ) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            var savedPath = ""
            if (videoUri != null) {
                savedPath = repository.saveVideoToInternalStorage(videoUri) ?: ""
            }
            repository.createPost(
                postType = PostType.VIDEO,
                title = title.trim(),
                description = description.trim(),
                category = category,
                videoUri = savedPath,
                videoPresetKey = videoPresetKey,
                videoDurationSec = if (videoDurationSec > 0) videoDurationSec else 45,
                feelingOrActivity = feeling.trim(),
                uploader = user,
                taggedStudentNames = taggedStudentNames
            )
            _showUploadSheet.value = false
            onSuccess()
        }
    }

    fun uploadPhotoWithPreset(
        title: String,
        description: String,
        category: PhotoCategory,
        presetKey: String,
        feeling: String = "",
        taggedStudentNames: String,
        onSuccess: () -> Unit
    ) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.createPost(
                postType = PostType.PHOTO,
                title = title.trim(),
                description = description.trim(),
                category = category,
                imageUri = "",
                presetKey = presetKey,
                feelingOrActivity = feeling.trim(),
                uploader = user,
                taggedStudentNames = taggedStudentNames
            )
            _showUploadSheet.value = false
            onSuccess()
        }
    }

    fun uploadPhotoWithUri(
        title: String,
        description: String,
        category: PhotoCategory,
        uri: Uri,
        feeling: String = "",
        taggedStudentNames: String,
        onSuccess: () -> Unit
    ) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val savedPath = repository.saveImageToInternalStorage(uri)
            if (savedPath != null) {
                repository.createPost(
                    postType = PostType.PHOTO,
                    title = title.trim(),
                    description = description.trim(),
                    category = category,
                    imageUri = savedPath,
                    presetKey = "",
                    feelingOrActivity = feeling.trim(),
                    uploader = user,
                    taggedStudentNames = taggedStudentNames
                )
                _showUploadSheet.value = false
                onSuccess()
            }
        }
    }

    fun uploadPhotoWithBitmap(
        title: String,
        description: String,
        category: PhotoCategory,
        bitmap: Bitmap,
        feeling: String = "",
        taggedStudentNames: String,
        onSuccess: () -> Unit
    ) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val savedPath = repository.saveBitmapToInternalStorage(bitmap)
            if (savedPath != null) {
                repository.createPost(
                    postType = PostType.PHOTO,
                    title = title.trim(),
                    description = description.trim(),
                    category = category,
                    imageUri = savedPath,
                    presetKey = "",
                    feelingOrActivity = feeling.trim(),
                    uploader = user,
                    taggedStudentNames = taggedStudentNames
                )
                _showUploadSheet.value = false
                onSuccess()
            }
        }
    }

    // Gallery Showcase tab setters & view count
    fun setGalleryMediaType(type: GalleryMediaType) {
        _galleryMediaType.value = type
    }

    fun setGallerySortOrder(order: GallerySortOrder) {
        _gallerySortOrder.value = order
    }

    fun incrementViewCount(photoId: Long) {
        viewModelScope.launch {
            repository.incrementViewCount(photoId)
        }
    }

    // Audio & Music Posts
    fun createAudioPost(
        title: String,
        description: String,
        audioArtist: String,
        category: PhotoCategory,
        audioUri: Uri?,
        audioPresetKey: String,
        audioDurationSec: Int,
        feeling: String,
        taggedStudentNames: String,
        onSuccess: () -> Unit
    ) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            var savedPath = ""
            if (audioUri != null) {
                savedPath = repository.saveAudioToInternalStorage(audioUri) ?: ""
            }
            val finalArtist = if (audioArtist.isNotBlank()) audioArtist.trim() else user.fullName
            val finalTitle = if (title.isNotBlank()) title.trim() else "Musik Karya SDN 4 Putrajawa"
            repository.createPost(
                postType = PostType.AUDIO,
                title = finalTitle,
                description = description.trim(),
                category = category,
                audioUri = savedPath,
                audioTitle = finalTitle,
                audioArtist = finalArtist,
                audioPresetKey = audioPresetKey,
                audioDurationSec = if (audioDurationSec > 0) audioDurationSec else 60,
                feelingOrActivity = feeling.trim(),
                uploader = user,
                taggedStudentNames = taggedStudentNames
            )
            _showUploadSheet.value = false
            onSuccess()
        }
    }

    fun uploadAudioWithUri(
        title: String,
        description: String,
        artist: String,
        category: PhotoCategory,
        uri: Uri,
        durationSec: Int = 60,
        feeling: String = "",
        taggedStudentNames: String = "",
        onSuccess: () -> Unit
    ) {
        createAudioPost(
            title = title,
            description = description,
            audioArtist = artist,
            category = category,
            audioUri = uri,
            audioPresetKey = "",
            audioDurationSec = durationSec,
            feeling = feeling,
            taggedStudentNames = taggedStudentNames,
            onSuccess = onSuccess
        )
    }
}
