package com.example.ui.screens

import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PhotoCategory
import com.example.data.model.PhotoEntity
import com.example.data.model.PostType
import com.example.data.model.UserRole
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.GalleryViewModel
import com.example.ui.viewmodel.MainTab
import com.example.ui.viewmodel.PostTypeFilter
import com.example.ui.viewmodel.StoryItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryFeedScreen(
    viewModel: GalleryViewModel,
    modifier: Modifier = Modifier
) {
    val photos by viewModel.allPhotos.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val allQuizzes by viewModel.allQuizzes.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val postTypeFilter by viewModel.postTypeFilter.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filterStudentId by viewModel.filterStudentId.collectAsState()
    val showUploadSheet by viewModel.showUploadSheet.collectAsState()
    val composerInitialType by viewModel.composerInitialType.collectAsState()
    val activeStory by viewModel.activeStory.collectAsState()
    val stories by viewModel.stories.collectAsState()
    val activeQuizToTake by viewModel.activeQuizToTake.collectAsState()
    val commentsByPhotoId by viewModel.commentsByPhotoId.collectAsState()

    val isTeacher = currentUser?.role == UserRole.WALI_KELAS.name
    var isSearchExpanded by remember { mutableStateOf(false) }

    // Filter posts
    val filteredPosts = remember(photos, selectedCategory, postTypeFilter, searchQuery, filterStudentId) {
        photos.filter { post ->
            val matchCategory = selectedCategory == PhotoCategory.SEMUA || post.category == selectedCategory.name
            val matchType = when (postTypeFilter) {
                PostTypeFilter.SEMUA -> true
                PostTypeFilter.STATUS -> post.postType == PostType.TEXT_STATUS.name
                PostTypeFilter.FOTO -> post.postType == PostType.PHOTO.name
                PostTypeFilter.VIDEO -> post.postType == PostType.VIDEO.name
                PostTypeFilter.MUSIK -> post.postType == PostType.AUDIO.name
                PostTypeFilter.KUIS -> post.title.contains("Kuis", ignoreCase = true) || post.title.contains("Ulangan", ignoreCase = true)
            }
            val matchSearch = searchQuery.isBlank() ||
                    post.title.contains(searchQuery, ignoreCase = true) ||
                    post.description.contains(searchQuery, ignoreCase = true) ||
                    post.uploaderName.contains(searchQuery, ignoreCase = true) ||
                    post.taggedStudentNames.contains(searchQuery, ignoreCase = true)
            val matchStudent = filterStudentId == null || post.uploaderId == filterStudentId
            matchCategory && matchType && matchSearch && matchStudent
        }
    }

    val filterStudentUser = remember(filterStudentId, allUsers) {
        allUsers.find { it.id == filterStudentId }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Column {
                // Official School Header with Realistic Indonesian SD Building and Centered Fourbook Logo
                Sdn4SchoolHeaderBanner(
                    currentUser = currentUser,
                    onAvatarClick = { viewModel.setShowQuickSwitchSheet(true) },
                    onAdminMenuClick = { viewModel.setShowAdminMenuSheet(true) },
                    isSearchActive = isSearchExpanded || searchQuery.isNotBlank(),
                    onSearchToggle = {
                        isSearchExpanded = !isSearchExpanded
                        if (!isSearchExpanded) {
                            viewModel.setSearchQuery("")
                        }
                    }
                )

                // Clean animated Search Bar (only appears when search icon tapped or search is active)
                AnimatedVisibility(
                    visible = isSearchExpanded || searchQuery.isNotBlank(),
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Surface(
                        color = FbCardBg,
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, Color(0xFFE2E8F0)),
                        shadowElevation = 2.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { viewModel.setSearchQuery(it) },
                                placeholder = {
                                    Text(
                                        "Cari di Fourbook...",
                                        fontSize = 13.sp,
                                        color = FbTextSecondary
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "Cari",
                                        tint = FbBluePrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                trailingIcon = {
                                    IconButton(
                                        onClick = {
                                            if (searchQuery.isNotEmpty()) {
                                                viewModel.setSearchQuery("")
                                            } else {
                                                isSearchExpanded = false
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Tutup",
                                            tint = FbTextSecondary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = FbBg,
                                    unfocusedContainerColor = FbBg,
                                    focusedBorderColor = FbBluePrimary,
                                    unfocusedBorderColor = Color(0xFFE2E8F0)
                                ),
                                shape = RoundedCornerShape(20.dp),
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("gallery_search_input")
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(FbBg),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // 1. Stories Carousel Tray (Facebook style)
            item {
                StoriesRow(
                    currentUser = currentUser,
                    stories = stories,
                    onCreateStoryClick = { viewModel.openComposer(PostType.TEXT_STATUS) },
                    onStoryClick = { story -> viewModel.openStory(story) }
                )
            }

            // 2. Facebook Quick Post Composer Bar
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = FbCardBg),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        // "Apa yang sedang kamu pikirkan?" Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            MemberAvatar(
                                fullName = currentUser?.fullName ?: "Warga Sekolah",
                                avatarColor = currentUser?.avatarColor ?: 0xFF1877F2,
                                avatarIcon = currentUser?.avatarIcon ?: "school",
                                customPhotoUri = currentUser?.customPhotoUri ?: "",
                                size = 40.dp
                            )

                            Surface(
                                onClick = { viewModel.openComposer(PostType.TEXT_STATUS) },
                                shape = RoundedCornerShape(20.dp),
                                color = FbBg,
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                            ) {
                                Box(
                                    contentAlignment = Alignment.CenterStart,
                                    modifier = Modifier.padding(horizontal = 14.dp)
                                ) {
                                    Text(
                                        text = "Apa yang Anda pikirkan, ${currentUser?.fullName?.split(" ")?.firstOrNull() ?: "teman"}?",
                                        fontSize = 12.5.sp,
                                        color = FbTextSecondary
                                    )
                                }
                            }
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            thickness = 0.5.dp,
                            color = Color(0xFFE2E8F0)
                        )

                        // 3 Action Buttons: Status | Foto | Video
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            TextButton(
                                onClick = { viewModel.openComposer(PostType.TEXT_STATUS) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    Icons.Default.EditNote,
                                    contentDescription = null,
                                    tint = Color(0xFFF97316),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Status", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = FbTextPrimary)
                            }

                            TextButton(
                                onClick = { viewModel.openComposer(PostType.PHOTO) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    Icons.Default.PhotoCamera,
                                    contentDescription = null,
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Foto", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = FbTextPrimary)
                            }

                            TextButton(
                                onClick = { viewModel.openComposer(PostType.VIDEO) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    Icons.Default.Videocam,
                                    contentDescription = null,
                                    tint = Color(0xFFEF4444),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Video", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = FbTextPrimary)
                            }
                        }

                        // For Admin / Wali Kelas (Pak Teten Kurniawan), show additional quick action bar
                        if (isTeacher) {
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 4.dp),
                                thickness = 0.5.dp,
                                color = Color(0xFFE2E8F0)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                FilledTonalButton(
                                    onClick = { viewModel.setShowCreateQuizSheet(true) },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.filledTonalButtonColors(
                                        containerColor = FbBlueLight,
                                        contentColor = FbBluePrimary
                                    ),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Quiz, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text("Bikin Kuis", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                FilledTonalButton(
                                    onClick = { viewModel.setShowCreateJournalSheet(true) },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.filledTonalButtonColors(
                                        containerColor = Color(0xFFDCFCE7),
                                        contentColor = Color(0xFF15803D)
                                    ),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.PostAdd, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text("Jurnal", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                FilledTonalButton(
                                    onClick = { viewModel.setActiveTab(MainTab.TEMAN) },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.filledTonalButtonColors(
                                        containerColor = Color(0xFFFEF3C7),
                                        contentColor = Color(0xFFB45309)
                                    ),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.People, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text("Teman", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // 2b. Live Realtime Sync Status Bar with balallica.my.id
            item {
                val isSyncing by viewModel.isSyncing.collectAsState()

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 3.dp)
                        .clickable { viewModel.triggerManualSync() },
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSyncing) Color(0xFFEFF6FF) else Color(0xFFF0FDF4),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSyncing) Color(0xFFBFDBFE) else Color(0xFFBBF7D0)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (isSyncing) Color(0xFF3B82F6) else Color(0xFF16A34A))
                            )
                            Column {
                                Text(
                                    text = if (isSyncing) "Menyinkronkan dengan web balallica.my.id..." else "Online Realtime • balallica.my.id",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isSyncing) Color(0xFF1D4ED8) else Color(0xFF15803D)
                                )
                                Text(
                                    text = if (isSyncing) "Mengambil postingan & data member terbaru" else "Ketuk untuk segarkan data secara manual",
                                    fontSize = 10.sp,
                                    color = if (isSyncing) Color(0xFF2563EB) else Color(0xFF16A34A).copy(alpha = 0.85f)
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.Sync,
                            contentDescription = "Sinkronkan",
                            tint = if (isSyncing) Color(0xFF1D4ED8) else Color(0xFF15803D),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // 3. Featured Quiz Announcement Banner (if quizzes exist)
            if (allQuizzes.isNotEmpty()) {
                val latestQuiz = allQuizzes.first()
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F2B48)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFFFBBF24),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Quiz,
                                            contentDescription = null,
                                            tint = Color(0xFF1E293B),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Ulangan Harian & Kuis Dibuka",
                                        color = Color(0xFFFDE047),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = latestQuiz.title,
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    if (isTeacher) {
                                        viewModel.setShowAdminQuizManager(true)
                                    } else {
                                        viewModel.setActiveQuizToTake(latestQuiz)
                                    }
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = FbBluePrimary),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(if (isTeacher) "Lihat Soal" else "Kerjakan", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // 4. Post Type Filter Chips (Semua | Status | Foto | Video | Kuis)
            item {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(PostTypeFilter.entries) { filter ->
                        val isSelected = postTypeFilter == filter
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setPostTypeFilter(filter) },
                            label = {
                                Text(
                                    text = filter.title,
                                    fontSize = 11.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = FbBluePrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            // 5. Category Filter Chips
            item {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(PhotoCategory.entries) { cat ->
                        val isSelected = selectedCategory == cat
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setSelectedCategory(cat) },
                            label = { Text(cat.title, fontSize = 11.sp) }
                        )
                    }
                }
            }

            // Active Filter Student Banner
            if (filterStudentUser != null) {
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = FbBlueLight
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                MemberAvatar(
                                    fullName = filterStudentUser.fullName,
                                    avatarColor = filterStudentUser.avatarColor,
                                    avatarIcon = filterStudentUser.avatarIcon,
                                    customPhotoUri = filterStudentUser.customPhotoUri,
                                    size = 28.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Postingan oleh: ${filterStudentUser.fullName}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = FbBluePrimary
                                )
                            }
                            IconButton(
                                onClick = { viewModel.setFilterStudent(null) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Hapus filter",
                                    tint = FbBluePrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            // 6. Post List
            if (filteredPosts.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = FbCardBg)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.Feed,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = FbTextSecondary
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Belum Ada Postingan",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = FbTextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Jadilah yang pertama membuat status, berbagi foto kegiatan, atau kuis di Fourbook SDN 4 Putrajawa!",
                                fontSize = 12.sp,
                                color = FbTextSecondary,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Button(
                                onClick = { viewModel.openComposer(PostType.TEXT_STATUS) },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = FbBluePrimary)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Buat Postingan Baru")
                            }
                        }
                    }
                }
            } else {
                items(filteredPosts, key = { it.id }) { post ->
                    FacebookPostCard(
                        photo = post,
                        currentUser = currentUser,
                        comments = commentsByPhotoId[post.id] ?: emptyList(),
                        allUsers = allUsers,
                        viewModel = viewModel,
                        onPhotoClick = { viewModel.selectPhoto(it) }
                    )
                }
            }
        }
    }

    // Story Viewer Fullscreen Dialog
    if (activeStory != null) {
        StoryViewerDialog(
            story = activeStory!!,
            allStories = stories,
            onDismiss = { viewModel.closeStory() },
            onStoryChange = { newStory -> viewModel.openStory(newStory) }
        )
    }

    // Post Composer Fullscreen Dialog (Status / Foto / Video)
    if (showUploadSheet) {
        PostComposerDialog(
            currentUser = currentUser,
            initialPostType = composerInitialType,
            viewModel = viewModel,
            onDismiss = { viewModel.setShowUploadSheet(false) }
        )
    }

    // Active Quiz Modal from Banner
    activeQuizToTake?.let { quiz ->
        QuizRunnerDialog(
            quiz = quiz,
            viewModel = viewModel,
            onDismiss = { viewModel.setActiveQuizToTake(null) }
        )
    }
}
