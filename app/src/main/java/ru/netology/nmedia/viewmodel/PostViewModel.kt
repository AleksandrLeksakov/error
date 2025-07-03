package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.processingPresponses.Result

private val empty = Post(
    id = 0,
    content = "",
    author = "",
    likedByMe = false,
    likes = 0,
    published = "",
    shareById = false,
    shares = 0,
    videoUrl = null
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositoryImpl()
    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel>
        get() = _data

    private val _edited = MutableLiveData(empty)
    val edited: LiveData<Post>
        get() = _edited

    init {
        loadPosts()
    }

    fun loadPosts() {
        viewModelScope.launch {
            _data.value = FeedModel(loading = true)
            when (val result = repository.getAll()) {
                is Result.Success -> {
                    _data.value = FeedModel(
                        posts = result.data,
                        empty = result.data.isEmpty()
                    )
                }
                is Result.Error -> {
                    _data.value = FeedModel(error = Exception(result.apiError.message))
                }
                Result.Loading -> {
                    _data.value = FeedModel(loading = true)
                }
            }
        }
    }

    fun savePost() {
        _edited.value?.let { post ->
            viewModelScope.launch {
                _data.value = _data.value?.copy(loading = true) ?: FeedModel(loading = true)
                when (val result = repository.save(post)) {
                    is Result.Success -> {
                        _edited.value = empty
                        loadPosts()
                    }
                    is Result.Error -> {
                        _data.value = _data.value?.copy(
                            loading = false,
                            error = Exception(result.apiError.message)
                        )
                    }
                    Result.Loading -> Unit // Уже обработано выше
                }
            }
        }
    }

    fun editPost(post: Post) {
        _edited.value = post
    }

    fun cancelEdit() {
        _edited.value = empty
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (_edited.value?.content == text) return
        _edited.value = _edited.value?.copy(content = text)
    }

    fun likeById(id: Long) {
        viewModelScope.launch {
            try {
                // Оптимистичное обновление
                _data.value?.posts?.let { currentPosts ->
                    val post = currentPosts.find { it.id == id } ?: return@launch
                    val newPosts = currentPosts.map {
                        if (it.id == id) {
                            it.copy(
                                likedByMe = !it.likedByMe,
                                likes = if (it.likedByMe) it.likes - 1 else it.likes + 1
                            )
                        } else it
                    }
                    _data.value = _data.value?.copy(posts = newPosts)
                }

                // Реальный запрос
                when (val result = repository.likeById(id)) {
                    is Result.Success -> {
                        // Обновляем данные с сервера
                        loadPosts()
                    }
                    is Result.Error -> {
                        // Откатываем изменения при ошибке
                        loadPosts()
                        _data.value = _data.value?.copy(error = Exception(result.apiError.message))
                    }
                    Result.Loading -> Unit
                }
            } catch (e: Exception) {
                _data.value = _data.value?.copy(error = e)
                loadPosts()
            }
        }
    }

    fun removeById(id: Long) {
        viewModelScope.launch {
            // Оптимистичное удаление
            _data.value?.posts?.let { currentPosts ->
                _data.value = _data.value?.copy(
                    posts = currentPosts.filter { it.id != id }
                )
            }

            when (val result = repository.removeById(id)) {
                is Result.Success -> Unit // Уже обновили оптимистично
                is Result.Error -> {
                    loadPosts() // Откатываем изменения при ошибке
                    _data.value = _data.value?.copy(error = Exception(result.apiError.message))
                }
                Result.Loading -> Unit
            }
        }
    }

    fun shareById(id: Long) {
        viewModelScope.launch {
            when (val result = repository.shareById(id)) {
                is Result.Success -> loadPosts()
                is Result.Error -> {
                    _data.value = _data.value?.copy(error = Exception(result.apiError.message))
                }
                Result.Loading -> Unit
            }
        }
    }
}