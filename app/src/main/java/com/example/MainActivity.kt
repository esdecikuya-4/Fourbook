package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.screens.*
import com.example.ui.theme.FbBluePrimary
import com.example.ui.theme.FbCardBg
import com.example.ui.theme.FbTextPrimary
import com.example.ui.theme.FbTextSecondary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.GalleryViewModel
import com.example.ui.viewmodel.MainTab

class MainActivity : ComponentActivity() {
    private val viewModel: GalleryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GalleryApp(viewModel = viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryApp(
    viewModel: GalleryViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val activeTab by viewModel.activeTab.collectAsState()
    val selectedPhoto by viewModel.selectedPhoto.collectAsState()
    val filterStudentId by viewModel.filterStudentId.collectAsState()
    val showQuickSwitchSheet by viewModel.showQuickSwitchSheet.collectAsState()
    val showAdminMenuSheet by viewModel.showAdminMenuSheet.collectAsState()
    val showCreateQuizSheet by viewModel.showCreateQuizSheet.collectAsState()
    val showCreateJournalSheet by viewModel.showCreateJournalSheet.collectAsState()
    val activeQuizToTake by viewModel.activeQuizToTake.collectAsState()

    val showAdminQuizManager by viewModel.showAdminQuizManager.collectAsState()
    val showAdminJournalManager by viewModel.showAdminJournalManager.collectAsState()
    val unreadMessagesCount by viewModel.unreadMessagesCount.collectAsState()
    val unreadNotificationsCount by viewModel.unreadNotificationsCount.collectAsState()

    // Handle system back press intelligently
    BackHandler(enabled = selectedPhoto != null || filterStudentId != null || showAdminQuizManager || showAdminJournalManager || activeTab != MainTab.BERANDA) {
        when {
            selectedPhoto != null -> viewModel.selectPhoto(null)
            filterStudentId != null -> viewModel.setFilterStudent(null)
            showAdminQuizManager -> viewModel.setShowAdminQuizManager(false)
            showAdminJournalManager -> viewModel.setShowAdminJournalManager(false)
            activeTab != MainTab.BERANDA -> viewModel.setActiveTab(MainTab.BERANDA)
        }
    }

    if (currentUser == null) {
        // Auth / Welcome Screen
        AuthScreen(
            viewModel = viewModel,
            modifier = modifier
        )
    } else {
        // Photo Detail Fullscreen view or Main Screen with Bottom Nav
        if (selectedPhoto != null) {
            PhotoDetailScreen(
                photo = selectedPhoto!!,
                viewModel = viewModel,
                onBack = { viewModel.selectPhoto(null) },
                modifier = modifier
            )
        } else {
            Scaffold(
                modifier = modifier.fillMaxSize(),
                bottomBar = {
                    Surface(
                        color = FbCardBg,
                        tonalElevation = 6.dp,
                        shadowElevation = 10.dp,
                        border = BorderStroke(0.5.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                    ) {
                        NavigationBar(
                            containerColor = FbCardBg,
                            tonalElevation = 0.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp)
                                .padding(top = 2.dp, bottom = 4.dp)
                        ) {
                            // 1. BERANDA (Feed Utama)
                            NavigationBarItem(
                                selected = activeTab == MainTab.BERANDA,
                                onClick = { viewModel.setActiveTab(MainTab.BERANDA) },
                                icon = {
                                    Icon(
                                        imageVector = if (activeTab == MainTab.BERANDA) Icons.Filled.Home else Icons.Outlined.Home,
                                        contentDescription = "Beranda"
                                    )
                                },
                                label = {
                                    Text(
                                        "Beranda",
                                        fontWeight = if (activeTab == MainTab.BERANDA) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 10.sp
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = FbBluePrimary,
                                    selectedTextColor = FbBluePrimary,
                                    indicatorColor = FbBluePrimary.copy(alpha = 0.12f),
                                    unselectedIconColor = FbTextSecondary,
                                    unselectedTextColor = FbTextSecondary
                                ),
                                modifier = Modifier.testTag("nav_item_beranda")
                            )

                            // 2. TEMAN (Daftar Teman & Guru & Pertemanan)
                            NavigationBarItem(
                                selected = activeTab == MainTab.TEMAN,
                                onClick = { viewModel.setActiveTab(MainTab.TEMAN) },
                                icon = {
                                    Icon(
                                        imageVector = if (activeTab == MainTab.TEMAN) Icons.Filled.PeopleAlt else Icons.Outlined.PeopleOutline,
                                        contentDescription = "Teman"
                                    )
                                },
                                label = {
                                    Text(
                                        "Teman",
                                        fontWeight = if (activeTab == MainTab.TEMAN) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 10.sp
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = FbBluePrimary,
                                    selectedTextColor = FbBluePrimary,
                                    indicatorColor = FbBluePrimary.copy(alpha = 0.12f),
                                    unselectedIconColor = FbTextSecondary,
                                    unselectedTextColor = FbTextSecondary
                                ),
                                modifier = Modifier.testTag("nav_item_teman")
                            )

                            // 3. PESAN / INBOX (Messenger SDN 4)
                            NavigationBarItem(
                                selected = activeTab == MainTab.PESAN,
                                onClick = { viewModel.setActiveTab(MainTab.PESAN) },
                                icon = {
                                    BadgedBox(badge = {
                                        if (unreadMessagesCount > 0) {
                                            Badge(containerColor = Color(0xFFEF4444)) {
                                                Text("$unreadMessagesCount", fontSize = 9.sp, color = Color.White)
                                            }
                                        }
                                    }) {
                                        Icon(
                                            imageVector = if (activeTab == MainTab.PESAN) Icons.Filled.ChatBubble else Icons.Outlined.ChatBubbleOutline,
                                            contentDescription = "Pesan"
                                        )
                                    }
                                },
                                label = {
                                    Text(
                                        "Pesan",
                                        fontWeight = if (activeTab == MainTab.PESAN) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 10.sp
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = FbBluePrimary,
                                    selectedTextColor = FbBluePrimary,
                                    indicatorColor = FbBluePrimary.copy(alpha = 0.12f),
                                    unselectedIconColor = FbTextSecondary,
                                    unselectedTextColor = FbTextSecondary
                                ),
                                modifier = Modifier.testTag("nav_item_pesan")
                            )

                            // 4. NOTIFIKASI (Notifikasi Post, Like, Komen, Pertemanan)
                            NavigationBarItem(
                                selected = activeTab == MainTab.NOTIFIKASI,
                                onClick = { viewModel.setActiveTab(MainTab.NOTIFIKASI) },
                                icon = {
                                    BadgedBox(badge = {
                                        if (unreadNotificationsCount > 0) {
                                            Badge(containerColor = Color(0xFFEF4444)) {
                                                Text("$unreadNotificationsCount", fontSize = 9.sp, color = Color.White)
                                            }
                                        }
                                    }) {
                                        Icon(
                                            imageVector = if (activeTab == MainTab.NOTIFIKASI) Icons.Filled.Notifications else Icons.Outlined.NotificationsNone,
                                            contentDescription = "Notifikasi"
                                        )
                                    }
                                },
                                label = {
                                    Text(
                                        "Notifikasi",
                                        fontWeight = if (activeTab == MainTab.NOTIFIKASI) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 10.sp
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = FbBluePrimary,
                                    selectedTextColor = FbBluePrimary,
                                    indicatorColor = FbBluePrimary.copy(alpha = 0.12f),
                                    unselectedIconColor = FbTextSecondary,
                                    unselectedTextColor = FbTextSecondary
                                ),
                                modifier = Modifier.testTag("nav_item_notifikasi")
                            )

                            // 5. PROFIL SAYA (Profil User / Admin)
                            NavigationBarItem(
                                selected = activeTab == MainTab.PROFIL,
                                onClick = { viewModel.setActiveTab(MainTab.PROFIL) },
                                icon = {
                                    Icon(
                                        imageVector = if (activeTab == MainTab.PROFIL) Icons.Filled.AccountCircle else Icons.Outlined.AccountCircle,
                                        contentDescription = "Profil"
                                    )
                                },
                                label = {
                                    Text(
                                        "Profil",
                                        fontWeight = if (activeTab == MainTab.PROFIL) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 10.sp
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = FbBluePrimary,
                                    selectedTextColor = FbBluePrimary,
                                    indicatorColor = FbBluePrimary.copy(alpha = 0.12f),
                                    unselectedIconColor = FbTextSecondary,
                                    unselectedTextColor = FbTextSecondary
                                ),
                                modifier = Modifier.testTag("nav_item_profil")
                            )
                        }
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    when (activeTab) {
                        MainTab.BERANDA -> {
                            GalleryFeedScreen(
                                viewModel = viewModel,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        MainTab.TEMAN -> {
                            MembersDirectoryScreen(
                                viewModel = viewModel,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        MainTab.PESAN -> {
                            MessagesScreen(
                                viewModel = viewModel,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        MainTab.NOTIFIKASI -> {
                            NotificationsScreen(
                                viewModel = viewModel,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        MainTab.PROFIL -> {
                            ProfileScreen(
                                viewModel = viewModel,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }

        // Fullscreen Admin Quiz Manager
        if (showAdminQuizManager) {
            Dialog(
                onDismissRequest = { viewModel.setShowAdminQuizManager(false) },
                properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Kelola Kuis & Ulangan (Admin)", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
                            navigationIcon = {
                                IconButton(onClick = { viewModel.setShowAdminQuizManager(false) }) {
                                    Icon(Icons.Default.ArrowBack, contentDescription = "Tutup")
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = FbCardBg,
                                titleContentColor = FbTextPrimary
                            )
                        )
                    }
                ) { pad ->
                    QuizScreen(
                        viewModel = viewModel,
                        modifier = Modifier.fillMaxSize().padding(pad)
                    )
                }
            }
        }

        // Fullscreen Admin Journal Manager
        if (showAdminJournalManager) {
            Dialog(
                onDismissRequest = { viewModel.setShowAdminJournalManager(false) },
                properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Jurnal Pembelajaran Kelas (Admin)", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
                            navigationIcon = {
                                IconButton(onClick = { viewModel.setShowAdminJournalManager(false) }) {
                                    Icon(Icons.Default.ArrowBack, contentDescription = "Tutup")
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = FbCardBg,
                                titleContentColor = FbTextPrimary
                            )
                        )
                    }
                ) { pad ->
                    JournalScreen(
                        viewModel = viewModel,
                        modifier = Modifier.fillMaxSize().padding(pad)
                    )
                }
            }
        }

        // Quick Switch User Modal Bottom Sheet
        if (showQuickSwitchSheet) {
            QuickSwitchUserSheet(
                viewModel = viewModel,
                onDismiss = { viewModel.setShowQuickSwitchSheet(false) },
                onNavigateToAuth = {
                    viewModel.logout()
                }
            )
        }

        // Admin Menu Portal Sheet
        if (showAdminMenuSheet) {
            AdminMenuSheet(
                viewModel = viewModel,
                onDismiss = { viewModel.setShowAdminMenuSheet(false) }
            )
        }

        // Create Quiz Sheet
        if (showCreateQuizSheet) {
            CreateQuizSheet(
                viewModel = viewModel,
                onDismiss = { viewModel.setShowCreateQuizSheet(false) }
            )
        }

        // Create Journal Sheet
        if (showCreateJournalSheet) {
            CreateJournalSheet(
                viewModel = viewModel,
                onDismiss = { viewModel.setShowCreateJournalSheet(false) }
            )
        }

        // Quiz Runner Dialog (for students to take quizzes)
        activeQuizToTake?.let { quiz ->
            QuizRunnerDialog(
                quiz = quiz,
                viewModel = viewModel,
                onDismiss = { viewModel.setActiveQuizToTake(null) }
            )
        }
    }
}
