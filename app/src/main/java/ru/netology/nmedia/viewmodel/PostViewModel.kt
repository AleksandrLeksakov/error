package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.*
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.*
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

    val edited = MutableLiveData(empty)

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _data.postValue(FeedModel(loading = true))
            try {
                val posts = repository.getAll()
                _data.postValue(FeedModel(posts = posts, empty = posts.isEmpty()))
            } catch (e: Exception) {
                _data.postValue(FeedModel(error = e))
            }
        }
    }

    fun save() {
        edited.value?.let { post ->
            viewModelScope.launch {
                try {
                    repository.save(post)
                    load() // Обновляем данные после сохранения
                } catch (e: Exception) {
                    _data.postValue(FeedModel(error = e))
                }
            }
        }
        edited.value = empty
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun cancelEdit() {
        edited.value = empty
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }

    fun likeById(id: Long) {
        viewModelScope.launch {
            try {
                repository.likeById(id)
                load()
            } catch (e: Exception) {
                _data.postValue(FeedModel(error = e))
            }
        }
    }

    fun removeById(id: Long) {
        viewModelScope.launch {
            try {
                repository.removeById(id)
                load()
            } catch (e: Exception) {
                _data.postValue(FeedModel(error = e))
            }
        }
    }

    fun shareById(id: Long) {
        viewModelScope.launch {
            try {
                repository.shareById(id)
                load()
            } catch (e: Exception) {
                _data.postValue(FeedModel(error = e))
            }
        }
    }
}