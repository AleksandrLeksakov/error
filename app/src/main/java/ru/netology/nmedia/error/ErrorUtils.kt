package ru.netology.nmedia.error

import ApiError
import okio.buffer
import okio.source
import com.squareup.moshi.Moshi
import retrofit2.Response
import java.lang.Exception

object ErrorUtils {
    fun parseError(response: Response<*>): ApiError {
        val error = try {
            response.errorBody()?.source()?.let {
                val moshi = Moshi.Builder().build()
                val jsonAdapter = moshi.adapter(ApiError::class.java)
                jsonAdapter.fromJson(it.buffer())
            }
        } catch (e: Exception) {
            null
        }
        return error ?: ApiError(response.code(), response.message())
    }
}