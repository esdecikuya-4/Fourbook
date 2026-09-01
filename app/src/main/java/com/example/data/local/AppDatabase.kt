package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        PhotoEntity::class,
        CommentEntity::class,
        AttendanceEntity::class,
        QuizEntity::class,
        QuizSubmissionEntity::class,
        StudentJournalEntity::class,
        ChatMessageEntity::class,
        FriendshipEntity::class,
        NotificationEntity::class
    ],
    version = 8,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun photoDao(): PhotoDao
    abstract fun commentDao(): CommentDao
    abstract fun attendanceDao(): AttendanceDao
    abstract fun quizDao(): QuizDao
    abstract fun quizSubmissionDao(): QuizSubmissionDao
    abstract fun studentJournalDao(): StudentJournalDao
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun friendshipDao(): FriendshipDao
    abstract fun notificationDao(): NotificationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "fourbook_sdn4_db"
                )
                .fallbackToDestructiveMigration()
                .addCallback(AppDatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class AppDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database)
                    }
                }
            }
        }

        suspend fun populateInitialData(database: AppDatabase) {
            val userDao = database.userDao()
            val photoDao = database.photoDao()
            val commentDao = database.commentDao()
            val quizDao = database.quizDao()
            val journalDao = database.studentJournalDao()
            val attendanceDao = database.attendanceDao()

            if (userDao.getUserCount() > 0) return

            // Seed Users - Teten Kurniawan (Wali Kelas / Admin SDN 4 Putrajawa)
            val guruId = userDao.insertUser(
                UserEntity(
                    username = "admin",
                    password = "qqq",
                    fullName = "Teten Kurniawan",
                    role = UserRole.WALI_KELAS.name,
                    studentNumber = "NIP. 197604062010011010",
                    avatarColor = 0xFF1877F2,
                    avatarIcon = "teacher",
                    bio = "Wali Kelas & Admin Resmi Fourbook SDN 4 Putrajawa. Semangat belajar dan berprestasi!",
                    customPhotoUri = "",
                    joinedAt = System.currentTimeMillis() - 86400000L * 30
                )
            )

            val ketuaId = userDao.insertUser(
                UserEntity(
                    username = "dimas_aditya",
                    password = "123",
                    fullName = "Dimas Aditya Pratama",
                    role = UserRole.KETUA_KELAS.name,
                    studentNumber = "Absen 01 / NISN 0142991823",
                    avatarColor = 0xFFD97706,
                    avatarIcon = "star",
                    bio = "Ketua Kelas SDN 4 Putrajawa. Siap membantu teman-teman dan menjaga ketertiban sekolah!",
                    joinedAt = System.currentTimeMillis() - 86400000L * 25
                )
            )

            val sitiId = userDao.insertUser(
                UserEntity(
                    username = "siti_nur",
                    password = "123",
                    fullName = "Siti Nurhaliza",
                    role = UserRole.MURID.name,
                    studentNumber = "Absen 02 / NISN 0142991845",
                    avatarColor = 0xFFEC4899,
                    avatarIcon = "palette",
                    bio = "Suka menggambar dan melukis. Cita-cita jadi seniman hebat SDN 4 Putrajawa!",
                    joinedAt = System.currentTimeMillis() - 86400000L * 24
                )
            )

            val rizkyId = userDao.insertUser(
                UserEntity(
                    username = "rizky_al",
                    password = "123",
                    fullName = "Rizky Alfaridzi",
                    role = UserRole.MURID.name,
                    studentNumber = "Absen 03 / NISN 0142991888",
                    avatarColor = 0xFF3B82F6,
                    avatarIcon = "sports",
                    bio = "Pemain tim futsal SDN 4 Putrajawa. Salam olahraga!",
                    joinedAt = System.currentTimeMillis() - 86400000L * 22
                )
            )

            val zahraId = userDao.insertUser(
                UserEntity(
                    username = "zahra_aulia",
                    password = "123",
                    fullName = "Zahra Aulia Putri",
                    role = UserRole.MURID.name,
                    studentNumber = "Absen 04 / NISN 0142991901",
                    avatarColor = 0xFF8B5CF6,
                    avatarIcon = "book",
                    bio = "Gemar membaca buku cerita dan sains di perpustakaan SDN 4 Putrajawa.",
                    joinedAt = System.currentTimeMillis() - 86400000L * 20
                )
            )

            val farhanId = userDao.insertUser(
                UserEntity(
                    username = "farhan_m",
                    password = "123",
                    fullName = "Farhan Maulana",
                    role = UserRole.MURID.name,
                    studentNumber = "Absen 05 / NISN 0142991950",
                    avatarColor = 0xFF10B981,
                    avatarIcon = "camp",
                    bio = "Regu Elang Pramuka SDN 4 Putrajawa. Pantang menyerah!",
                    joinedAt = System.currentTimeMillis() - 86400000L * 18
                )
            )

            // Seed Quizzes created by Teten Kurniawan
            val sampleQuestions1 = listOf(
                QuizQuestionItem(
                    id = 1,
                    questionText = "Proses pembuatan makanan pada tumbuhan hijau dengan bantuan cahaya matahari disebut...",
                    optionA = "Respirasi",
                    optionB = "Fotosintesis",
                    optionC = "Adaptasi",
                    optionD = "Metamorfosis",
                    correctOptionIndex = 1,
                    points = 25,
                    explanation = "Fotosintesis adalah proses tumbuhan hijau membuat makanan sendiri menggunakan klorofil dan sinar matahari."
                ),
                QuizQuestionItem(
                    id = 2,
                    questionText = "Bagian tumbuhan yang berfungsi menyerap air dan mineral dari dalam tanah adalah...",
                    optionA = "Akar",
                    optionB = "Batang",
                    optionC = "Daun",
                    optionD = "Bunga",
                    correctOptionIndex = 0,
                    points = 25,
                    explanation = "Akar bertugas menyerap air dan zat hara dari tanah serta memperkokoh tanaman."
                ),
                QuizQuestionItem(
                    id = 3,
                    questionText = "Hewan pemakan tumbuhan dalam rantai makanan tergolong sebagai...",
                    optionA = "Produsen",
                    optionB = "Konsumen Tingkat I (Herbivora)",
                    optionC = "Pengurai / Dekomposer",
                    optionD = "Konsumen Puncak",
                    correctOptionIndex = 1,
                    points = 25,
                    explanation = "Herbivora memakan produsen (tumbuhan) sehingga disebut konsumen tingkat 1."
                ),
                QuizQuestionItem(
                    id = 4,
                    questionText = "Simbiosis mutualisme yang saling menguntungkan terjadi antara...",
                    optionA = "Kutu dengan kepala manusia",
                    optionB = "Benalu dengan pohon inang",
                    optionC = "Lebah dengan bunga",
                    optionD = "Ikan remora dengan hiu",
                    correctOptionIndex = 2,
                    points = 25,
                    explanation = "Lebah mendapat nektar madu dan bunga terbantu proses penyerbukannya."
                )
            )

            val sampleQuestions2 = listOf(
                QuizQuestionItem(
                    id = 1,
                    questionText = "Hasil dari 125 + (15 x 6) adalah...",
                    optionA = "215",
                    optionB = "205",
                    optionC = "840",
                    optionD = "225",
                    correctOptionIndex = 0,
                    points = 33,
                    explanation = "Kerjakan perkalian dulu: 15 x 6 = 90, kemudian 125 + 90 = 215."
                ),
                QuizQuestionItem(
                    id = 2,
                    questionText = "Pecahan senilai dari 3/4 adalah...",
                    optionA = "6/8",
                    optionB = "5/10",
                    optionC = "9/15",
                    optionD = "4/3",
                    correctOptionIndex = 0,
                    points = 33,
                    explanation = "Jika pembilang dan penyebut dikali 2: 3x2=6, 4x2=8 (6/8)."
                ),
                QuizQuestionItem(
                    id = 3,
                    questionText = "Sebuah persegi memiliki panjang sisi 12 cm. Luas persegi tersebut adalah...",
                    optionA = "48 cm²",
                    optionB = "144 cm²",
                    optionC = "120 cm²",
                    optionD = "24 cm²",
                    correctOptionIndex = 1,
                    points = 34,
                    explanation = "Luas persegi = sisi x sisi = 12 x 12 = 144 cm²."
                )
            )

            val quiz1Id = quizDao.insertQuiz(
                QuizEntity(
                    title = "Ulangan Harian IPAS: Tumbuhan & Ekosistem",
                    subject = "IPAS",
                    description = "Wajib dikerjakan seluruh siswa SDN 4 Putrajawa. 4 soal pilihan ganda berbatas waktu.",
                    durationMinutes = 15,
                    questionsJson = QuizQuestionItem.questionsToJsonString(sampleQuestions1),
                    totalPoints = 100,
                    isPublished = true,
                    dueDate = "Besok, 16.00 WIB",
                    teacherName = "Teten Kurniawan",
                    createdAt = System.currentTimeMillis() - 3600000L * 3
                )
            )

            val quiz2Id = quizDao.insertQuiz(
                QuizEntity(
                    title = "Kuis Kilat Matematika: Hitung Cepat",
                    subject = "Matematika",
                    description = "Uji kecepatan logika berhitung penjumlahan, perkalian, dan pecahan senilai.",
                    durationMinutes = 10,
                    questionsJson = QuizQuestionItem.questionsToJsonString(sampleQuestions2),
                    totalPoints = 100,
                    isPublished = true,
                    dueDate = "Jumat, 17.00 WIB",
                    teacherName = "Teten Kurniawan",
                    createdAt = System.currentTimeMillis() - 3600000L * 10
                )
            )

            // Seed Student Journals
            val todayDateStr = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
            journalDao.insertJournalEntry(
                StudentJournalEntity(
                    date = todayDateStr,
                    studentId = sitiId,
                    studentName = "Siti Nurhaliza",
                    studentNumber = "Absen 02",
                    studentAvatarColor = 0xFFEC4899,
                    studentAvatarIcon = "palette",
                    category = JournalCategory.PRESTASI.name,
                    title = "Juara 1 Lomba Melukis Poster Lingkungan",
                    description = "Siti berhasil meraih Juara 1 dalam Festival Lomba Seni Siswa (FLS2N) tingkat kecamatan dengan karya bertema 'Hijaukan Sekolahku'.",
                    followUpAction = "Diusulkan mewakili SDN 4 Putrajawa ke tingkat Kabupaten.",
                    teacherName = "Teten Kurniawan"
                )
            )

            journalDao.insertJournalEntry(
                StudentJournalEntity(
                    date = todayDateStr,
                    studentId = ketuaId,
                    studentName = "Dimas Aditya Pratama",
                    studentNumber = "Absen 01",
                    studentAvatarColor = 0xFFD97706,
                    studentAvatarIcon = "star",
                    category = JournalCategory.SIKAP_POSITIF.name,
                    title = "Teladan Kedisiplinan & Pemimpin Barisan",
                    description = "Dimas memimpin barisan upacara bendera dan mengorganisir jadwal piket kebersihan kelas dengan sangat rapi dan tertib.",
                    followUpAction = "Diberikan apresiasi bintang teladan pekan ini.",
                    teacherName = "Teten Kurniawan"
                )
            )

            journalDao.insertJournalEntry(
                StudentJournalEntity(
                    date = todayDateStr,
                    studentId = 0,
                    studentName = "Seluruh Siswa SDN 4 Putrajawa",
                    studentNumber = "Semua",
                    studentAvatarColor = 0xFF1877F2,
                    studentAvatarIcon = "school",
                    category = JournalCategory.KEGIATAN_HARIAN.name,
                    title = "Kegiatan Senam Pagi & Literasi Bersama",
                    description = "Pelaksanaan senam pagi sehat di lapangan SDN 4 Putrajawa dilanjutkan 15 menit membaca buku cerita di pojok baca.",
                    followUpAction = "Seluruh siswa berpartisipasi aktif dan gembira.",
                    teacherName = "Teten Kurniawan"
                )
            )

            // Seed Feed Posts (PhotoEntity)
            photoDao.insertPhoto(
                PhotoEntity(
                    title = "Pengumuman Ulangan Harian & Kuis Interaktif",
                    description = "Pemberitahuan resmi dari Pak Teten Kurniawan: Telah dibuka Ulangan Harian IPAS Bab Ekosistem & Kuis Matematika. Seluruh siswa SDN 4 Putrajawa silakan mengerjakan melalui kartu kuis atau menu kuis di bawah ini ya! Semangat belajar 💪✨",
                    category = PhotoCategory.BELAJAR.name,
                    postType = PostType.TEXT_STATUS.name,
                    statusBackgroundKey = "gradient_ocean",
                    feelingOrActivity = "sedang membagikan kuis ulangan 📝",
                    uploaderId = guruId,
                    uploaderName = "Teten Kurniawan",
                    uploaderRole = UserRole.WALI_KELAS.name,
                    uploaderAvatarColor = 0xFF1877F2,
                    uploaderAvatarIcon = "teacher",
                    createdAt = System.currentTimeMillis() - 3600000L * 1,
                    likeCount = 6,
                    likedByUserIds = "[$guruId,$ketuaId,$sitiId,$rizkyId,$zahraId,$farhanId]",
                    reactionsJson = "{\"LIKE\": 3, \"LOVE\": 2, \"FIRE\": 1}",
                    taggedStudentNames = "Semua Siswa SDN 4 Putrajawa",
                    shareCount = 4
                )
            )

            val photo1 = photoDao.insertPhoto(
                PhotoEntity(
                    title = "Video Praktik Rangkaian Listrik IPA",
                    description = "Dokumentasi video seru percobaan membuat rangkaian listrik seri & paralel serta lampu senter mini SDN 4 Putrajawa. Semua lampu menyala terang!",
                    category = PhotoCategory.BELAJAR.name,
                    postType = PostType.VIDEO.name,
                    videoPresetKey = "video_science",
                    videoDurationSec = 45,
                    feelingOrActivity = "sedang bereksperimen di Lab IPA 🔬",
                    uploaderId = guruId,
                    uploaderName = "Teten Kurniawan",
                    uploaderRole = UserRole.WALI_KELAS.name,
                    uploaderAvatarColor = 0xFF1877F2,
                    uploaderAvatarIcon = "teacher",
                    createdAt = System.currentTimeMillis() - 3600000L * 2,
                    likeCount = 6,
                    likedByUserIds = "[$guruId,$ketuaId,$sitiId,$rizkyId,$zahraId,$farhanId]",
                    reactionsJson = "{\"LIKE\": 3, \"LOVE\": 2, \"CARE\": 1}",
                    taggedStudentNames = "Dimas Aditya Pratama, Siti Nurhaliza, Farhan Maulana",
                    shareCount = 3
                )
            )

            val photo2 = photoDao.insertPhoto(
                PhotoEntity(
                    title = "Semangat Belajar SDN 4 Putrajawa",
                    description = "Semangat hari ini teman-teman SDN 4 Putrajawa! Jangan lupa jadwal piket kebersihan kelompok 2 dan latihan pramuka sore nanti. Tetap kompak dan saling bantu! 💪✨🌟",
                    category = PhotoCategory.BELAJAR.name,
                    postType = PostType.TEXT_STATUS.name,
                    statusBackgroundKey = "gradient_sunset",
                    feelingOrActivity = "merasa bersemangat 🌟",
                    uploaderId = ketuaId,
                    uploaderName = "Dimas Aditya Pratama",
                    uploaderRole = UserRole.KETUA_KELAS.name,
                    uploaderAvatarColor = 0xFFD97706,
                    uploaderAvatarIcon = "star",
                    createdAt = System.currentTimeMillis() - 3600000L * 6,
                    likeCount = 5,
                    likedByUserIds = "[$guruId,$sitiId,$zahraId,$farhanId,$ketuaId]",
                    reactionsJson = "{\"LIKE\": 2, \"LOVE\": 2, \"FIRE\": 1}",
                    taggedStudentNames = "Semua Siswa SDN 4 Putrajawa",
                    shareCount = 2
                )
            )

            val photo3 = photoDao.insertPhoto(
                PhotoEntity(
                    title = "Cuplikan Gol Latihan Futsal SDN 4 Putrajawa",
                    description = "Video cuplikan gol tendangan jarak jauh Rizky saat sparing futsal di lapangan sekolah SDN 4 Putrajawa kemarin sore!",
                    category = PhotoCategory.OLAHRAGA.name,
                    postType = PostType.VIDEO.name,
                    videoPresetKey = "video_futsal",
                    videoDurationSec = 30,
                    feelingOrActivity = "sedang bermain futsal ⚽",
                    uploaderId = rizkyId,
                    uploaderName = "Rizky Alfaridzi",
                    uploaderRole = UserRole.MURID.name,
                    uploaderAvatarColor = 0xFF3B82F6,
                    uploaderAvatarIcon = "sports",
                    createdAt = System.currentTimeMillis() - 86400000L * 1,
                    likeCount = 5,
                    likedByUserIds = "[$guruId,$rizkyId,$ketuaId,$farhanId,$zahraId]",
                    reactionsJson = "{\"FIRE\": 3, \"LIKE\": 2}",
                    taggedStudentNames = "Rizky Alfaridzi, Dimas Aditya Pratama, Farhan Maulana",
                    shareCount = 4
                )
            )

            val photo4 = photoDao.insertPhoto(
                PhotoEntity(
                    title = "Pameran Karya Seni Kolase & Origami",
                    description = "Hasil karya tugas Seni Rupa siswa SDN 4 Putrajawa. Teman-teman membuat kolase burung cendrawasih dan motif batik!",
                    category = PhotoCategory.KARYA_SENI.name,
                    postType = PostType.PHOTO.name,
                    presetKey = "preset_art",
                    feelingOrActivity = "merasa bangga 🎨",
                    uploaderId = sitiId,
                    uploaderName = "Siti Nurhaliza",
                    uploaderRole = UserRole.MURID.name,
                    uploaderAvatarColor = 0xFFEC4899,
                    uploaderAvatarIcon = "palette",
                    createdAt = System.currentTimeMillis() - 86400000L * 2,
                    likeCount = 5,
                    viewCount = 142,
                    likedByUserIds = "[$guruId,$sitiId,$zahraId,$ketuaId,$farhanId]",
                    reactionsJson = "{\"LOVE\": 3, \"CARE\": 2}",
                    taggedStudentNames = "Zahra Aulia Putri, Siti Nurhaliza",
                    shareCount = 1
                )
            )

            val photo5 = photoDao.insertPhoto(
                PhotoEntity(
                    title = "Lagu Mars SDN 4 Putrajawa",
                    description = "Lagu mars resmi kebanggaan seluruh warga dan siswa-siswi SDN 4 Putrajawa. Mari dengarkan bersama dan hafalkan liriknya!",
                    category = PhotoCategory.KEGIATAN_BERSAMA.name,
                    postType = PostType.AUDIO.name,
                    audioPresetKey = "audio_mars_sdn4",
                    audioTitle = "Mars SDN 4 Putrajawa",
                    audioArtist = "Paduan Suara SDN 4 Putrajawa",
                    audioDurationSec = 90,
                    feelingOrActivity = "sedang mendengarkan musik 🎵",
                    uploaderId = guruId,
                    uploaderName = "Teten Kurniawan",
                    uploaderRole = UserRole.WALI_KELAS.name,
                    uploaderAvatarColor = 0xFF1877F2,
                    uploaderAvatarIcon = "teacher",
                    createdAt = System.currentTimeMillis() - 86400000L * 3,
                    likeCount = 6,
                    viewCount = 280,
                    likedByUserIds = "[$guruId,$ketuaId,$sitiId,$rizkyId,$zahraId,$farhanId]",
                    reactionsJson = "{\"LOVE\": 4, \"FIRE\": 2}",
                    taggedStudentNames = "Semua Siswa SDN 4 Putrajawa",
                    shareCount = 8
                )
            )

            val photo6 = photoDao.insertPhoto(
                PhotoEntity(
                    title = "Musik Akustik Ceria Belajar & Berkarya",
                    description = "Alunan musik instrumental petikan gitar santai untuk menemani waktu belajar mandiri, membaca di perpustakaan, atau menggambar!",
                    category = PhotoCategory.KARYA_SENI.name,
                    postType = PostType.AUDIO.name,
                    audioPresetKey = "audio_study_acoustic",
                    audioTitle = "Semangat Belajar Ceria",
                    audioArtist = "Fourbook Studio SDN 4",
                    audioDurationSec = 75,
                    feelingOrActivity = "merasa rileks & fokus 🎧",
                    uploaderId = sitiId,
                    uploaderName = "Siti Nurhaliza",
                    uploaderRole = UserRole.MURID.name,
                    uploaderAvatarColor = 0xFFEC4899,
                    uploaderAvatarIcon = "palette",
                    createdAt = System.currentTimeMillis() - 86400000L * 3 - 3600000L * 4,
                    likeCount = 4,
                    viewCount = 195,
                    likedByUserIds = "[$guruId,$sitiId,$zahraId,$farhanId]",
                    reactionsJson = "{\"LOVE\": 2, \"LIKE\": 2}",
                    taggedStudentNames = "Zahra Aulia Putri",
                    shareCount = 3
                )
            )

            val photo7 = photoDao.insertPhoto(
                PhotoEntity(
                    title = "Juara 1 Lomba Cerdas Cermat Sains",
                    description = "Selamat kepada tim perwakilan SDN 4 Putrajawa atas prestasi gemilang membawa pulang piala juara 1 tingkat kecamatan!",
                    category = PhotoCategory.PRESTASI.name,
                    postType = PostType.PHOTO.name,
                    presetKey = "preset_trophy",
                    feelingOrActivity = "merasa bersyukur 🏆",
                    uploaderId = guruId,
                    uploaderName = "Teten Kurniawan",
                    uploaderRole = UserRole.WALI_KELAS.name,
                    uploaderAvatarColor = 0xFF1877F2,
                    uploaderAvatarIcon = "teacher",
                    createdAt = System.currentTimeMillis() - 86400000L * 4,
                    likeCount = 6,
                    viewCount = 310,
                    likedByUserIds = "[$guruId,$ketuaId,$sitiId,$rizkyId,$zahraId,$farhanId]",
                    reactionsJson = "{\"LOVE\": 4, \"CARE\": 2}",
                    taggedStudentNames = "Zahra Aulia Putri, Dimas Aditya Pratama",
                    shareCount = 6
                )
            )

            // Seed Comments
            commentDao.insertComment(
                CommentEntity(
                    photoId = photo1,
                    userId = ketuaId,
                    userName = "Dimas Aditya Pratama",
                    userRole = UserRole.KETUA_KELAS.name,
                    userAvatarColor = 0xFFD97706,
                    text = "Percobaannya seru sekali Pak Teten, saklarnya bekerja bagus!",
                    createdAt = System.currentTimeMillis() - 86400000L * 1 + 3600000L
                )
            )

            commentDao.insertComment(
                CommentEntity(
                    photoId = photo1,
                    userId = zahraId,
                    userName = "Zahra Aulia Putri",
                    userRole = UserRole.MURID.name,
                    userAvatarColor = 0xFF8B5CF6,
                    text = "Aku jadi paham cara arus listrik mengalir dari baterai 👍",
                    createdAt = System.currentTimeMillis() - 86400000L * 1 + 7200000L
                )
            )

            commentDao.insertComment(
                CommentEntity(
                    photoId = photo2,
                    userId = guruId,
                    userName = "Teten Kurniawan",
                    userRole = UserRole.WALI_KELAS.name,
                    userAvatarColor = 0xFF1877F2,
                    text = "Bagus sekali kekompakan anak-anak SDN 4 Putrajawa! Pertahankan ya 👏",
                    createdAt = System.currentTimeMillis() - 86400000L * 3
                )
            )

            // Seed Initial Attendance for Today
            val students = listOf(
                Triple(ketuaId, "Dimas Aditya Pratama", "Absen 01"),
                Triple(sitiId, "Siti Nurhaliza", "Absen 02"),
                Triple(rizkyId, "Rizky Alfaridzi", "Absen 03"),
                Triple(zahraId, "Zahra Aulia Putri", "Absen 04"),
                Triple(farhanId, "Farhan Maulana", "Absen 05")
            )

            val studentMeta = mapOf(
                ketuaId to Pair(0xFFD97706, "star"),
                sitiId to Pair(0xFFEC4899, "palette"),
                rizkyId to Pair(0xFF3B82F6, "sports"),
                zahraId to Pair(0xFF8B5CF6, "book"),
                farhanId to Pair(0xFF10B981, "camp")
            )

            students.forEachIndexed { index, (sId, name, num) ->
                val meta = studentMeta[sId] ?: Pair(0xFF1877F2, "face_1")
                val status = when (index) {
                    1 -> AttendanceStatus.IZIN.name
                    else -> AttendanceStatus.HADIR.name
                }
                val note = when (index) {
                    1 -> "Izin mewakili SDN 4 Putrajawa lomba melukis FLS2N"
                    else -> ""
                }
                attendanceDao.insertOrUpdateAttendance(
                    AttendanceEntity(
                        date = todayDateStr,
                        studentId = sId,
                        studentName = name,
                        studentNumber = num,
                        avatarColor = meta.first,
                        avatarIcon = meta.second,
                        status = status,
                        note = note,
                        recordedBy = "Teten Kurniawan"
                    )
                )
            }

            // Seed Initial Messenger Chats
            val chatDao = database.chatMessageDao()
            // 1. Group Broadcast from Pak Teten
            chatDao.insertMessage(
                ChatMessageEntity(
                    senderId = guruId,
                    senderName = "Teten Kurniawan",
                    senderRole = UserRole.WALI_KELAS.name,
                    senderAvatarColor = 0xFF1877F2,
                    senderAvatarIcon = "teacher",
                    recipientId = 0, // Broadcast to all
                    recipientName = "Semua Teman & Guru SDN 4",
                    messageText = "Halo anak-anak SDN 4 Putrajawa! Selamat datang di fitur Pesan Fourbook. Silakan saling menyapa dan bertanya tugas di sini ya! 😊📚",
                    timestamp = System.currentTimeMillis() - 1000L * 60 * 60 * 3,
                    isRead = true
                )
            )
            // 2. Dimas reply in group
            chatDao.insertMessage(
                ChatMessageEntity(
                    senderId = ketuaId,
                    senderName = "Dimas Aditya Pratama",
                    senderRole = UserRole.KETUA_KELAS.name,
                    senderAvatarColor = 0xFFD97706,
                    senderAvatarIcon = "star",
                    recipientId = 0,
                    recipientName = "Semua Teman & Guru SDN 4",
                    messageText = "Siap Pak Teten! Teman-teman jangan lupa besok jadwal piket kelas yaa 👍",
                    timestamp = System.currentTimeMillis() - 1000L * 60 * 60 * 2,
                    isRead = true
                )
            )
            // 3. Direct message from Dimas to Pak Teten
            chatDao.insertMessage(
                ChatMessageEntity(
                    senderId = ketuaId,
                    senderName = "Dimas Aditya Pratama",
                    senderRole = UserRole.KETUA_KELAS.name,
                    senderAvatarColor = 0xFFD97706,
                    senderAvatarIcon = "star",
                    recipientId = guruId,
                    recipientName = "Teten Kurniawan",
                    messageText = "Selamat siang Pak Teten, untuk kuis IPA apakah dikerjakan hari ini atau besok?",
                    timestamp = System.currentTimeMillis() - 1000L * 60 * 45,
                    isRead = false
                )
            )
            // 4. Pak Teten reply to Dimas
            chatDao.insertMessage(
                ChatMessageEntity(
                    senderId = guruId,
                    senderName = "Teten Kurniawan",
                    senderRole = UserRole.WALI_KELAS.name,
                    senderAvatarColor = 0xFF1877F2,
                    senderAvatarIcon = "teacher",
                    recipientId = ketuaId,
                    recipientName = "Dimas Aditya Pratama",
                    messageText = "Bisa dikerjakan hari ini sampai jam 5 sore ya Dimas. Semangat! 💪✨",
                    timestamp = System.currentTimeMillis() - 1000L * 60 * 30,
                    isRead = true
                )
            )
            // 5. Direct message from Siti to Pak Teten
            chatDao.insertMessage(
                ChatMessageEntity(
                    senderId = sitiId,
                    senderName = "Siti Nurhaliza",
                    senderRole = UserRole.MURID.name,
                    senderAvatarColor = 0xFFEC4899,
                    senderAvatarIcon = "palette",
                    recipientId = guruId,
                    recipientName = "Teten Kurniawan",
                    messageText = "Pak Teten, lukisan untuk lomba FLS2N sudah selesai saya upload di galeri ya Pak 🙏🎨",
                    timestamp = System.currentTimeMillis() - 1000L * 60 * 15,
                    isRead = false
                )
            )

            // Seed Initial Friendships (Facebook-style)
            val friendshipDao = database.friendshipDao()
            friendshipDao.insertFriendship(
                FriendshipEntity(
                    senderId = ketuaId,
                    receiverId = guruId,
                    status = FriendshipStatus.ACCEPTED.name,
                    createdAt = System.currentTimeMillis() - 86400000L * 10
                )
            )
            friendshipDao.insertFriendship(
                FriendshipEntity(
                    senderId = sitiId,
                    receiverId = guruId,
                    status = FriendshipStatus.ACCEPTED.name,
                    createdAt = System.currentTimeMillis() - 86400000L * 8
                )
            )
            friendshipDao.insertFriendship(
                FriendshipEntity(
                    senderId = rizkyId,
                    receiverId = guruId,
                    status = FriendshipStatus.PENDING.name,
                    createdAt = System.currentTimeMillis() - 3600000L * 2
                )
            )
            friendshipDao.insertFriendship(
                FriendshipEntity(
                    senderId = ketuaId,
                    receiverId = sitiId,
                    status = FriendshipStatus.ACCEPTED.name,
                    createdAt = System.currentTimeMillis() - 86400000L * 5
                )
            )

            // Seed Initial Notifications
            val notifDao = database.notificationDao()
            notifDao.insertNotification(
                NotificationEntity(
                    recipientUserId = 0, // Broadcast to all
                    senderUserId = guruId,
                    senderName = "Teten Kurniawan",
                    senderRole = UserRole.WALI_KELAS.name,
                    senderAvatarColor = 0xFF1877F2,
                    senderAvatarIcon = "teacher",
                    type = NotificationType.NEW_POST.name,
                    title = "Postingan Baru dari Wali Kelas",
                    message = "Teten Kurniawan memposting: \"Lagu Mars SDN 4 Putrajawa\"",
                    targetId = photo5,
                    createdAt = System.currentTimeMillis() - 3600000L * 2,
                    isRead = false
                )
            )
            notifDao.insertNotification(
                NotificationEntity(
                    recipientUserId = guruId,
                    senderUserId = sitiId,
                    senderName = "Siti Nurhaliza",
                    senderRole = UserRole.MURID.name,
                    senderAvatarColor = 0xFFEC4899,
                    senderAvatarIcon = "palette",
                    type = NotificationType.COMMENT.name,
                    title = "Komentar Baru",
                    message = "Siti Nurhaliza mengomentari postingan Anda: \"Wah senangnya praktikum IPA bareng teman-teman...\"",
                    targetId = photo1,
                    createdAt = System.currentTimeMillis() - 3600000L * 4,
                    isRead = false
                )
            )
            notifDao.insertNotification(
                NotificationEntity(
                    recipientUserId = guruId,
                    senderUserId = ketuaId,
                    senderName = "Dimas Aditya Pratama",
                    senderRole = UserRole.KETUA_KELAS.name,
                    senderAvatarColor = 0xFFD97706,
                    senderAvatarIcon = "star",
                    type = NotificationType.LIKE.name,
                    title = "Suka pada Postingan",
                    message = "Dimas Aditya Pratama menyukai foto praktikum IPA kelas.",
                    targetId = photo1,
                    createdAt = System.currentTimeMillis() - 3600000L * 5,
                    isRead = false
                )
            )
            notifDao.insertNotification(
                NotificationEntity(
                    recipientUserId = guruId,
                    senderUserId = rizkyId,
                    senderName = "Rizky Alfaridzi",
                    senderRole = UserRole.MURID.name,
                    senderAvatarColor = 0xFF3B82F6,
                    senderAvatarIcon = "sports",
                    type = NotificationType.FRIEND_REQUEST.name,
                    title = "Permintaan Pertemanan",
                    message = "Rizky Alfaridzi mengirimkan permintaan pertemanan kepada Anda.",
                    targetId = rizkyId,
                    createdAt = System.currentTimeMillis() - 3600000L * 2,
                    isRead = false
                )
            )
            notifDao.insertNotification(
                NotificationEntity(
                    recipientUserId = guruId,
                    senderUserId = sitiId,
                    senderName = "Siti Nurhaliza",
                    senderRole = UserRole.MURID.name,
                    senderAvatarColor = 0xFFEC4899,
                    senderAvatarIcon = "palette",
                    type = NotificationType.MESSAGE.name,
                    title = "Pesan Baru",
                    message = "Siti Nurhaliza: \"Pak Teten, lukisan untuk lomba FLS2N sudah selesai saya upload...\"",
                    targetId = sitiId,
                    createdAt = System.currentTimeMillis() - 1000L * 60 * 15,
                    isRead = false
                )
            )
        }
    }
}
