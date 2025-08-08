package com.baek.untitledproject.ui.board.write

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
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
        observeEditingImages()
        setupImageSelect()
        setupTextWatchers()
        validateInputs()
        setupNextBtn()
    }


    //수정으로 접근 시 해당 게시글 데이터 불러오기
    private fun initField() {
        val postId = args.postId
        if (postId != null) {
            viewModel.initPostData(postId)
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

    //이미지는 따로 관리
    private fun observeEditingImages() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.editingImages.collect { uris ->
                    renderImages(uris)
                }
            }
        }
    }

    private fun bindPostData(post: Post) {
        post.title?.let {
            if (it != binding.titleInput.text.toString()) {
                binding.titleInput.setText(it)
            }
        }
        post.organization?.let {
            if (it != binding.groupNameInput.text.toString()) {
                binding.groupNameInput.setText(it)
            }
        }
        if (post.recruitmentStart != null && post.recruitmentEnd != null) {
            val startDate = post.recruitmentStart.toStringWithDayOfWeekAndSplitter()
            val endDate = post.recruitmentEnd.toStringWithDayOfWeekAndSplitter()
            binding.recruitDateSelectBtn.text = "$startDate ~ $endDate"
        }
        post.content?.let {
            if (it != binding.contentInput.text.toString()) {
                binding.contentInput.setText(it)
            }
        }
        validateInputs()
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
    private fun setupImageSelect() {
        binding.selectImgBtn.setOnClickListener {
            if (viewModel.editingImages.value.size >= 5) {
                Toast.makeText(requireContext(), "이미지는 최대 5장까지 등록 가능합니다.", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            pickImageLauncher.launch("image/*")
        }
    }

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                if (viewModel.editingImages.value.size >= 5) {
                    Toast.makeText(requireContext(), "이미지는 최대 5장까지 등록 가능합니다.", Toast.LENGTH_SHORT)
                        .show()
                    return@registerForActivityResult
                }
                viewModel.addUiImage(it)
            }
        }

    private fun renderImages(uris: List<Uri>) {
        while (binding.imgContainer.childCount > 1) {
            binding.imgContainer.removeViewAt(1)
        }
        uris.forEachIndexed { idx, uri ->
            val item =
                layoutInflater.inflate(R.layout.item_selected_image, binding.imgContainer, false)
            val thumb = item.findViewById<ImageView>(R.id.selectedImg)
            val cancel = item.findViewById<ImageView>(R.id.cancelBtn)

            Glide.with(this).load(uri).centerCrop().into(thumb)
            thumb.tag = uri
            cancel.setOnClickListener { viewModel.removeUiImage(idx) }

            binding.imgContainer.addView(item)

        }
        // 스크롤을 맨 오른쪽으로 이동
        binding.imageScrollView.post {
            binding.imageScrollView.smoothScrollTo(binding.imgContainer.width, 0)
        }
    }

    // 텍스트 변경 감지 -> 버튼 활성화 여부 검증
    private fun setupTextWatchers() {
        binding.titleInput.doAfterTextChanged { validateInputs() }
        binding.groupNameInput.doAfterTextChanged { validateInputs() }
        binding.contentInput.doAfterTextChanged { validateInputs() }
    }

    //다음 버튼 활성화 검증: 이미지를 제외한 필드가 입력되어 있으면
    private fun validateInputs() {
        val titleNotNull = !binding.titleInput.text.isNullOrBlank()
        val groupNameNotNull = !binding.groupNameInput.text.isNullOrBlank()
        val contentNotNull = !binding.contentInput.text.isNullOrBlank()
        val isRecruitDateSelected = viewModel.editingPost.value.run {
            recruitmentStart != null && recruitmentEnd != null
        }
        binding.nextBtn.isEnabled =
            titleNotNull && groupNameNotNull && contentNotNull && isRecruitDateSelected
    }

    //작성 내용 viewModel에 업데이트 후 이동
    private fun setupNextBtn() {
        binding.nextBtn.setOnClickListener {
            updateInfoWrite()
            val action =
                InfoWriteFragmentDirections.actionInfoWriteFragmentToInterviewSettingFragment()
            findNavController().navigate(action)
        }
    }

    //viewModel에 저장
    private fun updateInfoWrite() {
        val title = binding.titleInput.text.toString()
        val groupName = binding.groupNameInput.text.toString()
        val content = binding.contentInput.text.toString()

        viewModel.updateInfoWrite(title, groupName, content)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}