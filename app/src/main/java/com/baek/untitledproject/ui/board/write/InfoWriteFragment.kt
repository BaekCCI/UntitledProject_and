package com.baek.untitledproject.ui.board.write

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.baek.untitledproject.R
import com.baek.untitledproject.databinding.FragmentInfoWriteBinding
import com.baek.untitledproject.domain.data.Post
import com.baek.untitledproject.domain.utils.toStringWithDayOfWeekAndSplitter
import com.baek.untitledproject.ui.board.BoardFragmentDirections
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class InfoWriteFragment : Fragment() {

    private var _binding: FragmentInfoWriteBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BoardWriteViewModel by hiltNavGraphViewModels(R.id.write_board_nav_graph)
    private val args: InfoWriteFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInfoWriteBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initField()
        setUpRecruitDateSelectBtn()
        observeEditingPost()
    }


    //수정으로 접근 시 해당 게시글 데이터 불러오기
    private fun initField() {
        val postId = args.postId
        if (postId != null) {
            viewModel.initField(postId)
        }

    }

    //수정 or 이전 버튼으로 접근 시 필드 초기화
    private fun observeEditingPost() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.editingPost.collect { post ->
                    bindPostData(post)

                }
            }
        }
    }

    private fun bindPostData(post: Post) {
        if (post.title != null) {
            binding.titleInput.setText(post.title)
        }
        if (post.organization != null) {
            binding.groupNameInput.setText(post.organization)
        }
        if (post.recruitmentStart != null && post.recruitmentEnd != null) {
            val startDate = post.recruitmentStart.toStringWithDayOfWeekAndSplitter()
            val endDate = post.recruitmentEnd.toStringWithDayOfWeekAndSplitter()
            binding.recruitDateSelectBtn.text = "$startDate ~ $endDate"
        }
        if (post.content != null) {
            binding.contentInput.setText(post.content)
        }
    }

    //모집 일정 버튼
    private fun setUpRecruitDateSelectBtn() {
        binding.recruitDateSelectBtn.setOnClickListener {
            findNavController().navigate(
                InfoWriteFragmentDirections.actionInfoWriteFragmentToRecruitDateSelectDialogFragment()
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}