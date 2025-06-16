package ru.netology.nmedia.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.nmedia.dto.Post
import java.util.concurrent.TimeUnit

class PostRepositoryImpl : PostRepository {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()
    private val typeToken = object : TypeToken<List<Post>>() {}

    companion object {
        private const val BASE_URL = "http://10.0.2.2:9999"
        private val jsonType = "application/json".toMediaType()
    }

    override suspend fun getAll(): List<Post> = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts")
            .build()

        client.newCall(request)
            .execute()
            .use { response ->
                if (!response.isSuccessful) {
                    throw RuntimeException("Failed to get posts: ${response.code}")
                }

                val body = response.body?.string()
                    ?: throw RuntimeException("Response body is null")

                gson.fromJson(body, typeToken.type)
            }
    }

    override suspend fun likeById(id: Long): Post = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .post("".toRequestBody(jsonType))
            .url("${BASE_URL}/api/posts/$id/likes")  // Уберите /slow/ для нормальной работы
            .build()

        client.newCall(request)
            .execute()
            .use { response ->
                if (!response.isSuccessful) {
                    throw RuntimeException("Failed to like post: ${response.code}")
                }

                val body = response.body?.string() ?: throw RuntimeException("Empty response")
                gson.fromJson(body, Post::class.java)  // Сервер должен возвращать ОБНОВЛЁННЫЙ пост
            }
    }

    override suspend fun unlikeById(id: Long): Post = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/posts/$id/likes")  // Уберите /slow/ для нормальной работы
            .build()

        client.newCall(request)
            .execute()
            .use { response ->
                if (!response.isSuccessful) {
                    throw RuntimeException("Failed to unlike post: ${response.code}")
                }

                val body = response.body?.string() ?: throw RuntimeException("Empty response")
                gson.fromJson(body, Post::class.java)  // Сервер должен возвращать ОБНОВЛЁННЫЙ пост
            }
    }

    override suspend fun save(post: Post) = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .post(gson.toJson(post).toRequestBody(jsonType))
            .url("${BASE_URL}/api/slow/posts")
            .build()

        client.newCall(request)
            .execute()
            .use { response ->
                if (!response.isSuccessful) {
                    throw RuntimeException("Failed to save post: ${response.code}")
                }
            }
    }

    override suspend fun removeById(id: Long) = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/slow/posts/$id")
            .build()

        client.newCall(request)
            .execute()
            .use { response ->
                if (!response.isSuccessful) {
                    throw RuntimeException("Failed to delete post: ${response.code}")
                }
            }
    }

    override suspend fun shareById(id: Long) = withContext(Dispatchers.IO) {
        // Реализация аналогична другим методам
        // В реальном приложении здесь был бы вызов API для "поделиться"
    }
}