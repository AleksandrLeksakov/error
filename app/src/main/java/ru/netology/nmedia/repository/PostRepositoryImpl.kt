package ru.netology.nmedia.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.netology.nmedia.api.RetrofitInstance
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.processingPresponses.ApiError
import ru.netology.nmedia.processingPresponses.ApiHelper
import ru.netology.nmedia.processingPresponses.Result

class PostRepositoryImpl(
    private val apiHelper: ApiHelper = RetrofitInstance.apiHelper,
    private val apiService: RetrofitInstance.PostApi = RetrofitInstance.apiService
) : PostRepository {

    override suspend fun getAll(): Result<List<Post>> = withContext(Dispatchers.IO) {
        apiHelper.safeApiCall {
            apiService.getAll()
        }
    }

    override suspend fun likeById(id: Long): Result<Post> = withContext(Dispatchers.IO) {
        apiHelper.safeApiCall {
            apiService.likeById(id)
        }
    }

    override suspend fun save(post: Post): Result<Post> = withContext(Dispatchers.IO) {
        apiHelper.safeApiCall {
            apiService.save(post)
        }
    }

    override suspend fun removeById(id: Long): Result<Unit> = withContext(Dispatchers.IO) {
        apiHelper.safeApiCall {
            apiService.removeById(id)
        }
    }

    override suspend fun shareById(id: Long): Result<Unit> = withContext(Dispatchers.IO) {
        // TODO: реализовать при наличии соответствующего API
        Result.Error(ApiError(code = 501, message = "Not implemented"))
    }

    override suspend fun unlikeById(id: Long): Result<Post> = withContext(Dispatchers.IO) {
        apiHelper.safeApiCall {
            apiService.unlikeById(id)
        }
    }
}