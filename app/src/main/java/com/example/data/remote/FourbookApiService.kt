package com.example.data.remote

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

// ----------------------------------------------------
// Models for Fourbook Server API (https://balallica.my.id)
// ----------------------------------------------------

data class RemoteAuthRequest(
    val username: String,
    val password: String
)

data class RemoteRegisterRequest(
    val username: String,
    val password: String,
    val fullName: String,
    val role: String,
    val studentNumber: String,
    val avatarColor: String = "0xFF1877F2",
    val avatarIcon: String = "student",
    val bio: String = ""
)

data class RemoteSwitchUserRequest(
    val userId: Long
)

data class RemoteAuthResponse(
    val status: Boolean = false,
    val success: Boolean = false,
    val message: String? = null,
    val user: RemoteUserItem? = null,
    val data: RemoteAuthData? = null
)

data class RemoteAuthData(
    val user: RemoteUserItem? = null
)

data class RemoteUsersResponse(
    val status: Boolean = false,
    val success: Boolean = false,
    val message: String? = null,
    val data: RemoteUsersData? = null
)

data class RemoteUsersData(
    val all_users: List<RemoteUserItem>? = null,
    val user: RemoteUserItem? = null
)

data class RemoteUserItem(
    val id: Long = 0,
    val username: String = "",
    val password: String = "",
    val fullName: String = "",
    val role: String = "MURID",
    val studentNumber: String? = null,
    val avatarColor: String? = null,
    val avatarIcon: String? = null,
    val bio: String? = null,
    val customPhotoUri: String? = null
)

data class RemoteComment(
    val id: Long = 0,
    val userId: Long = 0,
    val userName: String = "",
    val userRole: String = "MURID",
    val userAvatarColor: String? = null,
    val userPhotoUri: String? = null,
    val commentText: String? = null,
    val text: String? = null,
    val timestamp: Long = 0L
)

data class RemotePost(
    val id: Long = 0,
    val uploaderId: Long = 0,
    val uploaderName: String = "",
    val uploaderRole: String = "MURID",
    val uploaderAvatarColor: String? = null,
    val uploaderAvatarIcon: String? = null,
    val uploaderPhotoUri: String? = null,
    val title: String? = null,
    val description: String? = null,
    val category: String? = null,
    val feeling: String? = null,
    val postType: String? = null,
    val mediaUri: String? = null,
    val mediaType: String? = null,
    val likeCount: Int = 0,
    val timestamp: Long = 0L,
    val comments: List<RemoteComment>? = null
)

data class RemotePostsResponse(
    val status: Boolean = false,
    val success: Boolean = false,
    val message: String? = null,
    val posts: List<RemotePost>? = null,
    val data: RemotePostsData? = null
)

data class RemotePostsData(
    val posts: List<RemotePost>? = null
)

data class RemoteCreatePostRequest(
    val title: String,
    val description: String,
    val category: String = "Umum",
    val postType: String = "TEXT_STATUS",
    val mediaUri: String = "",
    val uploaderId: Long = 0,
    val uploaderName: String = "",
    val uploaderRole: String = "MURID",
    val uploaderAvatarColor: String = "0xFF1877F2",
    val uploaderAvatarIcon: String = "student"
)

data class RemoteCreatePostResponse(
    val status: Boolean = false,
    val success: Boolean = false,
    val message: String? = null,
    val data: RemoteCreatePostData? = null
)

data class RemoteCreatePostData(
    val post: RemotePost? = null
)

data class RemoteToggleReactionRequest(
    val postId: Long,
    val reaction: String = "LIKE",
    val userId: Long = 0
)

data class RemoteAddCommentRequest(
    val postId: Long,
    val commentText: String,
    val userId: Long = 0
)

data class RemoteDeletePostRequest(
    val postId: Long,
    val userId: Long = 0
)

data class RemoteApiResponse(
    val status: Boolean = false,
    val success: Boolean = false,
    val message: String? = null
)

// ----------------------------------------------------
// Retrofit Service Interface
// ----------------------------------------------------

interface FourbookApiService {
    @POST("api/auth.php?action=login")
    suspend fun login(@Body request: RemoteAuthRequest): Response<RemoteAuthResponse>

    @POST("api/auth.php?action=register")
    suspend fun register(@Body request: RemoteRegisterRequest): Response<RemoteAuthResponse>

    @POST("api/auth.php?action=switch_user")
    suspend fun switchUser(@Body request: RemoteSwitchUserRequest): Response<RemoteAuthResponse>

    @GET("api/auth.php?action=get_current")
    suspend fun getAllUsers(): Response<RemoteUsersResponse>

    @GET("api/posts.php?action=get_posts")
    suspend fun getPosts(): Response<RemotePostsResponse>

    @POST("api/posts.php?action=create_post")
    suspend fun createPost(@Body request: RemoteCreatePostRequest): Response<RemoteCreatePostResponse>

    @POST("api/posts.php?action=toggle_reaction")
    suspend fun toggleReaction(@Body request: RemoteToggleReactionRequest): Response<RemoteApiResponse>

    @POST("api/posts.php?action=add_comment")
    suspend fun addComment(@Body request: RemoteAddCommentRequest): Response<RemoteApiResponse>

    @POST("api/posts.php?action=delete_post")
    suspend fun deletePost(@Body request: RemoteDeletePostRequest): Response<RemoteApiResponse>
}

// ----------------------------------------------------
// Persistent In-Memory CookieJar for PHP Session (PHPSESSID)
// ----------------------------------------------------

class FourbookCookieJar : CookieJar {
    private val cookieStore = mutableListOf<Cookie>()

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        synchronized(cookieStore) {
            cookieStore.removeAll { existing ->
                cookies.any { it.name == existing.name && it.matches(url) }
            }
            cookieStore.addAll(cookies)
        }
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        return synchronized(cookieStore) {
            cookieStore.filter { it.matches(url) }
        }
    }

    fun hasSession(): Boolean {
        return synchronized(cookieStore) {
            cookieStore.any { it.name == "PHPSESSID" }
        }
    }

    fun clear() {
        synchronized(cookieStore) {
            cookieStore.clear()
        }
    }
}

// ----------------------------------------------------
// API Client Singleton
// ----------------------------------------------------

object FourbookApiClient {
    // Live Server Hosting SDN 4 Putrajawa
    var BASE_URL: String = "https://balallica.my.id/"
        private set

    val cookieJar = FourbookCookieJar()

    fun updateBaseUrl(newUrl: String) {
        val formatted = if (newUrl.endsWith("/")) newUrl else "$newUrl/"
        BASE_URL = formatted
        cookieJar.clear()
        retrofitInstance = null
    }

    private var retrofitInstance: Retrofit? = null

    private fun getClient(): Retrofit {
        return retrofitInstance ?: synchronized(this) {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }
            val okHttp = OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .build()

            val moshi = Moshi.Builder()
                .addLast(KotlinJsonAdapterFactory())
                .build()

            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttp)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .also { retrofitInstance = it }
        }
    }

    val service: FourbookApiService
        get() = getClient().create(FourbookApiService::class.java)
}
