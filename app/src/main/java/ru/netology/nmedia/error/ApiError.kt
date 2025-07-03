package ru.netology.nmedia.error

import com.squareup.moshi.Moshi
import retrofit2.HttpException
import retrofit2.Response
import java.lang.Exception



data class ApiError(
    val code: Int,
    val message: String
) {
    constructor(exception: HttpException) : this(
        code = exception.code(),
        message = exception.message()
    )
}

object ErrorUtils {
    fun parseError(response: Response<*>): ApiError {
        val error = try {
            response.errorBody()?.source()?.let {
                val moshi = Moshi.Builder().build()
                val jsonAdapter = moshi.adapter(ApiError::class.java)
                jsonAdapter.fromJson(it.buffer())
            }
        } catch (_: Exception) {
            null
        }
        return error ?: ApiError(response.code(), response.message())
    }
}