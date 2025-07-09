package ru.netology.nmedia.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.fragment.OnePostFragment.Companion.idArg
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostViewModel
import kotlin.apply

//private val FeedFragment.swipeRefresh: Any

class FeedFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentFeedBinding.inflate(
            inflater,
            container,
            false
        )

        val viewModel: PostViewModel by viewModels(
            ownerProducer = ::requireParentFragment
        )


        val adapter = PostsAdapter(object : OnInteractionListener {
            override fun onEdit(post: Post) {
                viewModel.editPost(post)
                val text = post.content
                val bundle = Bundle().apply {
                    putString("content", text)
                }
                findNavController().navigate(R.id.action_feedFragment_to_newPostFragment, bundle)

            }


            override fun onLike(post: Post) {
                viewModel.likeById(post.id)
            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
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


            override fun onView(post: Post) {
                findNavController().navigate(
                    R.id.action_feedFragment_to_onePostFragment,
                    Bundle().apply { idArg = post.id })
            }
        })
        binding.list.adapter = adapter

        // Обработчик кнопки повтора
        binding.retryButton.setOnClickListener {
            viewModel.retryLoad()
        }

        // Обработчик обновления свайпом
       // binding.swipeRefresh.setOnRefreshListener {
      //      viewModel.loadPosts(refresh = true)
      //  }

        viewModel.data.observe(viewLifecycleOwner) { state ->
            adapter.submitList(state.posts)

            binding.errorGroup.isVisible = state.isError
            binding.errorText.text = state.errorTuString(requireContext())
            binding.loading.isVisible = state.loading
            binding.emptyText.isVisible = state.empty
        }


        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
        }

        return binding.root
    }
}

