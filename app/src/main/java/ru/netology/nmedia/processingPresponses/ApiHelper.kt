package ru.netology.nmedia.processingPresponses

import ApiError
import com.google.firebase.appdistribution.gradle.ApiService
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

class ApiHelper(private val apiService: ApiService) {
    suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): Result<T> {
        return try {
            val response = apiCall()
            if (response.isSuccessful) {
                Result.Success(response.body()!!)
            } else {
                Result.Error(ErrorUtils.parseError(response))
            }
        } catch (e: HttpException) {
            Result.Error(ApiError(e))
        } catch (e: IOException) {
            Result.Error(ApiError(500, "Network error"))
        } catch (e: Exception) {
            Result.Error(ApiError(500, "Unknown error"))
        }
    }
}