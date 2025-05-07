package ru.netology.nmedia.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Post


@Entity
data class PostEntity (
@PrimaryKey(autoGenerate = true)
    val id: Long,
    val author: String,
    val content: String,
    val published: String,
    val likedByMe: Boolean,
    val likes: Int,
    val shareByMy: Boolean,
    var shares: Int,
    val videoUrl: String? = null
) {
    fun toDto() = Post(id, author, content, published, likedByMe, likes, shareByMy, shares, videoUrl )
companion object {
    fun fromDto(post: Post) = PostEntity(
        post.id,
        post.author,
        post.content,
        post.published,
        post.likedByMe,
        post.likes,
        post.shareById,
        post.shares,
        post.videoUrl
    )
}
}