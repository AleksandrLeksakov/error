package ru.netology.nmedia.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.dto.Post


class PostRepositoryImpl : PostRepository {
    override suspend fun getAll(): List<Post> = withContext(Dispatchers.IO) {
        try {
            val response = ApiService.service.getAll().execute()
            if (response.isSuccessful) {
                response.body() ?: throw RuntimeException("Response body is null")
            } else {
                throw RuntimeException("Server error: ${response.code()}")
            }
        } catch (e: Exception) {
            throw RuntimeException("Network error", e)
        }
    }

    override fun getAllAsync(callback: PostRepository.GetAllCallback) {
        ApiService.service.getAll().enqueue(object : Callback<List<Post>> {
            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                if (response.isSuccessful) {
                    callback.onSuccess(response.body() ?: emptyList())
                } else {
                    callback.onError(RuntimeException("Server error: ${response.code()}"))
                }
            }

            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                callback.onError(t)
            }
        })
    }

    override suspend fun likeById(id: Long): Post {
        return try {
            val response = ApiService.service.likeById(id).execute()
            if (response.isSuccessful) {
                response.body() ?: throw RuntimeException("Response body is null")
            } else {
                throw RuntimeException("Server error: ${response.code()}")
            }
        } catch (e: Exception) {
            throw RuntimeException("Network error", e)
        }
    }

    override suspend fun save(post: Post) {
        try {
            val response = ApiService.service.save(post).execute()
            if (!response.isSuccessful) {
                throw RuntimeException("Server error: ${response.code()}")
            }
        } catch (e: Exception) {
            throw RuntimeException("Network error", e)
        }
    }

    override suspend fun removeById(id: Long) {
        try {
            val response = ApiService.service.deleteById(id).execute()
            if (!response.isSuccessful) {
                throw RuntimeException("Server error: ${response.code()}")
            }
        } catch (e: Exception) {
            throw RuntimeException("Network error", e)
        }
    }

    override suspend fun shareById(id: Long) {
        // TODO: реализовать при наличии соответствующего API
        throw UnsupportedOperationException("Not implemented")
    }

    override suspend fun unlikeById(id: Long): Post {
        return try {
            val response = ApiService.service.unlikeById(id).execute()
            if (response.isSuccessful) {
                response.body() ?: throw RuntimeException("Response body is null")
            } else {
                throw RuntimeException("Server error: ${response.code()}")
            }
        } catch (e: Exception) {
            throw RuntimeException("Network error", e)
        }
    }
}