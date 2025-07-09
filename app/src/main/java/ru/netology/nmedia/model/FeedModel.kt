package ru.netology.nmedia.model

import android.content.Context
import java.io.IOException
import ru.netology.nmedia.R
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.error.ApiError

data class FeedModel(
    val posts: List<Post> = emptyList(),
    val loading: Boolean = false,
    val error: Exception? = null,
    val empty: Boolean = false,
    val refreshing: Boolean = false,
    val showRetry: Boolean = false //  поле для отображения кнопки
) {

    val isError: Boolean = error != null

    fun errorTuString(context: Context): String = when (error) {
        is IOException -> context.getString(R.string.network_error)
        else -> context.getString(R.string.unknown_error)
    }
}