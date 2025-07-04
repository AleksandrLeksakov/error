package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Post
import java.lang.invoke.VarHandle.AccessMode.GET
import java.util.Locale

interface OnInteractionListener {
    fun onLike(post: Post) {}
    fun onEdit(post: Post) {}
    fun onRemove(post: Post) {}
    fun onShare(post: Post) {}
    fun onPlayVideo(post: Post) {}
    fun onView(post: Post) {}
}


class PostsAdapter(
    private val onInteractionListener: OnInteractionListener,
) : ListAdapter<Post, PostViewHolder>(PostViewHolder.PostDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(post: Post) {
        binding.apply {
            //Текстовый контент
            author.text = post.author
            published.text = post.published
            content.text = post.content
            // Кнопки лайков и шаринга
            share.isChecked = post.shareById
            share.text = "${post.shares.formatCount()}"
            like.isChecked = post.likedByMe
            like.text = "${post.likes.formatCount()}"


            // Загрузка аватарки
            post.authorAvatar?.let { avatarUrl ->
                val fullAvatarUrl = "http://10.0.2.2:9999/avatars/$avatarUrl"

                Glide.with(avatar.context)
                    .load(fullAvatarUrl)
                    .circleCrop()
                    .placeholder(R.drawable.ic_loading_100dp)
                    .error(R.drawable.ic_error_100dp)
                    .timeout(10_000)
                    .into(avatar)
            } ?: avatar.setImageResource(R.drawable.ic_launcher_background)

            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.menu_options)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onInteractionListener.onRemove(post)
                                true
                            }

                            R.id.edit -> {
                                onInteractionListener.onEdit(post)
                                true
                            }

                            else -> false
                        }
                    }
                }.show()
            }


            like.setOnClickListener {
                onInteractionListener.onLike(post)
            }

            share.setOnClickListener {
                onInteractionListener.onShare(post)
            }

            root.setOnClickListener {
                onInteractionListener.onView(post)
            }

            imageViewVideo.visibility =
                if (post.videoUrl.isNullOrEmpty()) View.GONE else View.VISIBLE
            imageViewVideo.setOnClickListener {
                onInteractionListener.onPlayVideo(post)
            }

        }
    }


    private fun Int.formatCount(): String {
        return when {
            this < 1000 -> this.toString()
            this < 10000 -> String.format(Locale.getDefault(), "%.1fK", this / 1000.0)
            this < 1000000 -> String.format(Locale.getDefault(), "%.0fK", this / 1000.0)
            else -> String.format(Locale.getDefault(), "%.1fM", this / 1000000.0)
        }
    }

    class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem == newItem
        }
    }
}




