package ru.netology.nmedia.repository

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.dto.Post


class PostRepositoryImpl : PostRepository {


    override suspend fun getAll(): List<Post>  {

        return ApiService.service.getAll()
            .execute()
            .let { it.body() ?: throw RuntimeException("body is null") }
            }

    override fun getAllAsync(callback: PostRepository.GetAllCallback) {
        ApiService.service.getAll()
            .execute()
        object : Callback<List<Post>> {
            override fun onResponse(
                call: Call<List<Post>>,
                response: Response<List<Post>?>
            ) {
                val body = response.body() ?: run {
                    callback.onError(RuntimeException("body is null"))
                }
            }

            override fun onFailure(
                call: Call<List<Post>>,
                throwable: Throwable
            ) {
                callback.onError(throwable)
            }

        }
    }

    override suspend fun likeById(id: Long): Post {
        TODO("Not yet implemented")
    }

    override suspend fun save(post: Post) {
        ApiService.service.save(post)
            .execute()
    }

    override suspend fun removeById(id: Long) {
        ApiService.service.deleteById(id)
            .execute()
    }

    override suspend fun shareById(id: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun unlikeById(id: Long): Post {
        TODO("Not yet implemented")
    }


}

