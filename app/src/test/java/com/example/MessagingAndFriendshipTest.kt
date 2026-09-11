package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.model.ChatMessageEntity
import com.example.data.model.FriendshipEntity
import com.example.data.model.FriendshipStatus
import com.example.data.model.NotificationType
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import com.example.data.repository.GalleryRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class MessagingAndFriendshipTest {

    private lateinit var db: AppDatabase
    private lateinit var repository: GalleryRepository

    private val user1 = UserEntity(
        id = 1L,
        username = "admin",
        password = "qqq",
        fullName = "Teten Kurniawan, S.Pd.",
        role = UserRole.WALI_KELAS.name,
        studentNumber = "198501012010011001",
        avatarColor = 0xFF1877F2,
        avatarIcon = "teacher"
    )

    private val user2 = UserEntity(
        id = 2L,
        username = "dimas",
        password = "123",
        fullName = "Dimas Aditya Pratama",
        role = UserRole.KETUA_KELAS.name,
        studentNumber = "20240401",
        avatarColor = 0xFFD97706,
        avatarIcon = "star"
    )

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = GalleryRepository(db, context)

        runBlocking {
            db.userDao().insertUser(user1)
            db.userDao().insertUser(user2)
        }
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun testSendMessageAndReplyFlow() = runBlocking {
        // 1. User 2 (Dimas) sends a message to User 1 (Pak Teten)
        val msg1 = ChatMessageEntity(
            senderId = user2.id,
            senderName = user2.fullName,
            senderRole = user2.role,
            recipientId = user1.id,
            recipientName = user1.fullName,
            messageText = "Assalamualaikum Pak Teten, tugas IPA sudah selesai.",
            timestamp = 1000L
        )
        val sendRes1 = repository.sendChatMessage(msg1)
        assertTrue(sendRes1.isSuccess)

        // Verify message stored and unread count for Pak Teten is 1
        val unreadForUser1 = repository.getUnreadMessageCount(user1.id).first()
        assertEquals(1, unreadForUser1)

        // Verify notification for Pak Teten was created
        val notifsUser1 = repository.getNotificationsForUser(user1.id).first()
        assertEquals(1, notifsUser1.size)
        assertEquals(NotificationType.MESSAGE.name, notifsUser1[0].type)
        assertEquals("Pesan Baru dari ${user2.fullName}", notifsUser1[0].title)

        // 2. User 1 (Pak Teten) replies back to User 2 (Dimas)
        val msg2 = ChatMessageEntity(
            senderId = user1.id,
            senderName = user1.fullName,
            senderRole = user1.role,
            recipientId = user2.id,
            recipientName = user2.fullName,
            messageText = "Waalaikumsalam Dimas, bagus sekali!",
            timestamp = 2000L
        )
        val sendRes2 = repository.sendChatMessage(msg2)
        assertTrue(sendRes2.isSuccess)

        // Verify conversation history has both messages in order
        val conversation = repository.getConversation(user1.id, user2.id).first()
        assertEquals(2, conversation.size)
        assertEquals(user2.id, conversation[0].senderId)
        assertEquals(user1.id, conversation[1].senderId)
        assertEquals("Assalamualaikum Pak Teten, tugas IPA sudah selesai.", conversation[0].messageText)
        assertEquals("Waalaikumsalam Dimas, bagus sekali!", conversation[1].messageText)

        // 3. Pak Teten reads the messages
        repository.markConversationAsRead(user1.id, user2.id)
        val unreadAfterRead = repository.getUnreadMessageCount(user1.id).first()
        assertEquals(0, unreadAfterRead)
    }

    @Test
    fun testFriendRequestAndAcceptFlow() = runBlocking {
        // Dimas sends friend request to Pak Teten
        val sendRes = repository.sendFriendRequest(user2, user1)
        assertTrue(sendRes.isSuccess)

        // Check friendship is PENDING
        val friendship = repository.getFriendshipBetween(user2.id, user1.id).first()
        assertNotNull(friendship)
        assertEquals(FriendshipStatus.PENDING.name, friendship?.status)
        assertEquals(user2.id, friendship?.senderId)
        assertEquals(user1.id, friendship?.receiverId)

        // Pak Teten accepts the friend request
        val acceptRes = repository.acceptFriendRequest(user1, user2)
        assertTrue(acceptRes.isSuccess)

        // Check friendship is ACCEPTED
        val updatedFriendship = repository.getFriendshipBetween(user2.id, user1.id).first()
        assertEquals(FriendshipStatus.ACCEPTED.name, updatedFriendship?.status)

        // Check friend count for both users
        val user1Friends = repository.getFriendCount(user1.id).first()
        val user2Friends = repository.getFriendCount(user2.id).first()
        assertEquals(1, user1Friends)
        assertEquals(1, user2Friends)
    }
}
