package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post

interface PostRepository {
    suspend fun getAll(): List<Post>
    suspend fun likeById(id: Long)
    suspend fun save(post: Post)
    suspend fun removeById(id: Long)
    suspend fun shareById(id: Long)
}
