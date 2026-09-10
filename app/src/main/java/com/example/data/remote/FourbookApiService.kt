package com.example.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

// Models for Fourbook Server API
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
    val avatarColor: Long,
    val avatarIcon: String,
    val bio: String
)

data class RemoteAuthResponse(
    val success: Boolean,
    val message: String? = null,
    val user: RemoteUser? = null
)

data class RemoteUser(
    val id: Long,
    val username: String,
    val fullName: String,
    val role: String,
    val studentNumber: String? = null,
    val avatarColor: Long = 0xFF1877F2,
    val avatarIcon: String = "face",
    val customPhotoUri: String? = null,
    val bio: String? = null,
    val friendCount: Int = 0
)

data class RemotePost(
    val id: Long,
    val uploaderId: Long,
    val uploaderName: String,
    val uploaderRole: String = "MURID",
    val title: String,
    val description: String,
    val feeling: String? = null,
    val postType: String = "FOTO",
    val mediaUri: String? = null,
    val likeCount: Int = 0,
    val commentCount: Int = 0,
    val viewCount: Int = 0,
    val timestamp: Long = 0L,
    val userLiked: Boolean = false
)

data class RemotePostsResponse(
    val success: Boolean,
    val posts: List<RemotePost> = emptyList()
)

data class RemoteCreatePostRequest(
    val uploaderId: Long,
    val uploaderName: String,
    val uploaderRole: String,
    val title: String,
    val description: String,
    val feeling: String? = null,
    val postType: String = "STATUS"
)

data class RemoteApiResponse(
    val success: Boolean,
    val message: String? = null
)

interface FourbookApiService {
    @POST("api/auth.php?action=login")
    suspend fun login(@Body request: RemoteAuthRequest): Response<RemoteAuthResponse>

    @POST("api/auth.php?action=register")
    suspend fun register(@Body request: RemoteRegisterRequest): Response<RemoteAuthResponse>

    @GET("api/posts.php")
    suspend fun getPosts(@Query("user_id") userId: Long = 0): Response<RemotePostsResponse>

    @POST("api/posts.php")
    suspend fun createPost(@Body request: RemoteCreatePostRequest): Response<RemoteApiResponse>

    @POST("api/posts.php?action=like")
    suspend fun toggleLike(
        @Query("post_id") postId: Long,
        @Query("user_id") userId: Long
    ): Response<RemoteApiResponse>
}

object FourbookApiClient {
    // Live Server Hosting SDN 4 Putrajawa
    var BASE_URL: String = "https://balallica.my.id/"
        private set

    fun updateBaseUrl(newUrl: String) {
        val formatted = if (newUrl.endsWith("/")) newUrl else "$newUrl/"
        BASE_URL = formatted
        retrofitInstance = null
    }

    private var retrofitInstance: Retrofit? = null

    private fun getClient(): Retrofit {
        return retrofitInstance ?: synchronized(this) {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }
            val okHttp = OkHttpClient.Builder()
                .connectTimeout(12, TimeUnit.SECONDS)
                .readTimeout(12, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .build()

            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttp)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                .also { retrofitInstance = it }
        }
    }

    val service: FourbookApiService
        get() = getClient().create(FourbookApiService::class.java)
}
