package ru.netology.nmedia.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostViewHolder
import ru.netology.nmedia.databinding.FragmentOnePostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.fragment.OnePostFragment.Companion.idArg
import ru.netology.nmedia.util.LongArg
import ru.netology.nmedia.viewmodel.PostViewModel
import kotlin.apply
import kotlin.collections.find
import kotlin.let

class OnePostFragment : Fragment() {

    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentOnePostBinding.inflate(inflater, container, false)
        val id = arguments?.idArg ?: -1
        val holder = PostViewHolder(binding.post, object : OnInteractionListener {

            override fun onEdit(post: Post) {
                viewModel.edit(post)
                val text = post.content
                val bundle = Bundle().apply {
                    putString("content", text)
                }
                findNavController().navigate(R.id.newPostFragment, bundle)

            }

            override fun onLike(post: Post) {
                viewModel.likeById(post.id)
            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
                findNavController().popBackStack()
            }


            override fun onShare(post: Post) {
                viewModel.shareById(post.id)
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }

                val shareIntent =
                    Intent.createChooser(intent, "Share post")
                startActivity(shareIntent)
            }
        })
        viewModel.data.observe(viewLifecycleOwner) {    }
        return binding.root
    }

    companion object {
        var Bundle.idArg: Long by LongArg
    }
}