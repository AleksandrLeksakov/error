package ru.netology.nmedia.processingPresponses

import ru.netology.nmedia.error.ApiError

import okio.IOException
import retrofit2.HttpException
import retrofit2.Response

import ru.netology.nmedia.error.ErrorUtils


// Result.kt
sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val apiError: ApiError) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

class ApiHelper(
    private val errorParser: (Response<*>) -> ApiError
) {
    suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): Result<T> {
        return try {
            val response = apiCall()
            if (response.isSuccessful) {
                Result.Success(response.body()!!)
            } else {
                Result.Error(errorParser(response))
            }
        } catch (e: HttpException) {
            Result.Error(ApiError(e.code(), e.message()))
        } catch (e: IOException) {
            Result.Error(ApiError(500, "Network error"))
        } catch (e: Exception) {
            Result.Error(ApiError(500, "Unknown error"))
        }
    }
}