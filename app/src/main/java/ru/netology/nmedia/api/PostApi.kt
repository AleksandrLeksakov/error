
package ru.netology.nmedia.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import ru.netology.nmedia.dto.Post

object RetrofitInstance {
    private const val BASE_URL = "http://10.0.2.2:9999/api/slow/"

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: PostApi by lazy {
        retrofit.create(PostApi::class.java)
    }

    interface PostApi {
        @GET("posts")
        suspend fun getAll(): Response<List<Post>>

        @POST("posts")
        suspend fun save(@Body post: Post): Response<Post>

        @DELETE("posts/{id}")
        suspend fun removeById(@Path("id") id: Long): Response<Unit>

        @POST("posts/{id}/likes")
        suspend fun likeById(@Path("id") id: Long): Response<Post>

        @DELETE("posts/{id}/likes")
        suspend fun unlikeById(@Path("id") id: Long): Response<Post>

        @POST("posts/{id}/shares")
        suspend fun shareById(@Path("id") id: Long): Response<Post>
    }
}