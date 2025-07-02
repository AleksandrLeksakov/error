package ru.netology.nmedia.repository


import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.processingPresponses.Result

interface PostRepository {
    suspend fun getAll(): Result< List<Post>>
    suspend fun likeById(id: Long): Result<Post>
    suspend fun save(post: Post): Result<Post>
    suspend fun removeById(id: Long): Result<Unit>
    suspend fun shareById(id: Long): Result<Unit>
    suspend fun unlikeById(id: Long): Result<Post>

   // fun getAllAsync(callback: GetAllCallback)

  //  interface GetAllCallback {
    //    fun onSuccess(posts: List<Post>) {}
      //  fun onError(e: Throwable) {}
  //  }
}

