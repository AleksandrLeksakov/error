package ru.netology.nmedia.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class PostEntity (
@PrimaryKey(autoGenerate = true)
    val id: Long,
    val author: String,
    val content: String,
    val published: String,
    val likedByMe: Boolean,
    val likes: Int,
    val shareById: Boolean,
    var shares: Int,
    val videoUrl: String? = null
)