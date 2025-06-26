package ru.netology.nmedia.repository

import com.google.gson.Gson

import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.dto.Post
import java.io.IOException


class PostRepositoryImpl : PostRepository {


    override suspend fun getAll(): List<Post>  {

        return ApiService.service.getAll()
            .execute()
            .let { it.body() ?: throw RuntimeException("body is null") }.body().orEmpty()
            }

    override suspend fun likeById(id: Long): Post {
        TODO("Not yet implemented")
    }

    override suspend fun save(post: Post) {
        TODO("Not yet implemented")
    }

    override suspend fun removeById(id: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun shareById(id: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun unlikeById(id: Long): Post {
        TODO("Not yet implemented")
    }
}

