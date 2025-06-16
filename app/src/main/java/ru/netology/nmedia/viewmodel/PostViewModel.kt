package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.*
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import kotlinx.coroutines.launch
import ru.netology.nmedia.dto.Post

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
            _data.value = _data.value?.copy(loading = true)
            try {
                val posts = repository.getAll()
                _data.value = FeedModel(posts = posts, empty = posts.isEmpty())
            } catch (e: Exception) {
                _data.value = _data.value?.copy(error = e) ?: FeedModel(error = e)
            }
        }
    }

    fun savePost() {
        _edited.value?.let { post ->
            viewModelScope.launch {
                try {
                    repository.save(post)
                    _edited.value = empty
                    loadPosts()
                } catch (e: Exception) {
                    _data.value = _data.value?.copy(error = e) ?: FeedModel(error = e)
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
                // 1. Получаем текущий список постов
                val currentPosts = _data.value?.posts ?: return@launch

                // 2. Находим нужный пост
                val post = currentPosts.find { it.id == id } ?: return@launch
                val wasLiked = post.likedByMe

                // 3. Оптимистичное обновление UI
                _data.value = _data.value?.copy(
                    posts = currentPosts.map {
                        if (it.id == id) {
                            it.copy(
                                likedByMe = !wasLiked,
                                likes = if (wasLiked) it.likes - 1 else it.likes + 1
                            )
                        } else it
                    }
                )

                // 4. Реальный запрос к серверу
                val updatedPost = if (wasLiked) {
                    repository.unlikeById(id)
                } else {
                    repository.likeById(id)
                }

                // 5. Обновляем данные с сервера (на случай, если другие данные изменились)
                _data.value = _data.value?.copy(
                    posts = _data.value?.posts?.map {
                        if (it.id == updatedPost.id) updatedPost else it
                    } ?: emptyList()
                )
                loadPosts()
            } catch (e: Exception) {
                // 6. В случае ошибки - показываем ошибку и перезагружаем данные
                _data.value = _data.value?.copy(error = e)
                loadPosts()
            }
        }
    }

    fun removeById(id: Long) {
        viewModelScope.launch {
            try {
                // Оптимистичное удаление
                _data.value?.posts?.let { currentPosts ->
                    _data.value = _data.value?.copy(
                        posts = currentPosts.filter { it.id != id }
                    )
                }
                repository.removeById(id)
            } catch (e: Exception) {
                _data.value = _data.value?.copy(error = e) ?: FeedModel(error = e)
                loadPosts()
            }
        }
    }

    fun shareById(id: Long) {
        viewModelScope.launch {
            try {
                repository.shareById(id)
                loadPosts()
            } catch (e: Exception) {
                _data.value = _data.value?.copy(error = e) ?: FeedModel(error = e)
            }
        }
    }
}