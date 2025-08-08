package com.baek.untitledproject.ui.board.write

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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
import com.bumptech.glide.Glide
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
        setupImageSelectBtn()
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

    /*
        이미지 선택 로직
     */
    private fun setupImageSelectBtn() {
        binding.selectImgBtn.setOnClickListener {
            val currentCount = binding.imgContainer.childCount - 1
            if (currentCount >= 5) {
                Toast.makeText(requireContext(), "이미지는 최대 5장까지 등록 가능합니다.", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            pickImageLauncher.launch("image/*")
        }
    }

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { addSelectedImage(it) }
        }

    private fun addSelectedImage(uri: Uri) {

        // item_selected_image.xml inflate
        val itemView = layoutInflater.inflate(
            R.layout.item_selected_image,
            binding.imgContainer,
            false
        )

        val thumb = itemView.findViewById<android.widget.ImageView>(R.id.selectedImg)
        val cancel = itemView.findViewById<android.widget.ImageView>(R.id.cancelBtn)

        // 이미지 로드 (Glide)
        Glide.with(this)
            .load(uri)
            .centerCrop()
            .into(thumb)

        // 삭제 버튼
        cancel.setOnClickListener {
            binding.imgContainer.removeView(itemView)
        }

        // selectImgBtn 오른쪽에 이미지 추가
        binding.imgContainer.addView(itemView)

        // 스크롤을 맨 오른쪽으로 이동
        binding.imageScrollView.post {
            binding.imageScrollView.smoothScrollTo(binding.imgContainer.width, 0)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}