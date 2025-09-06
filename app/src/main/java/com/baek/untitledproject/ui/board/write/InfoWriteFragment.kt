package com.baek.untitledproject.ui.board.write

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.baek.untitledproject.R
import com.baek.untitledproject.databinding.FragmentInfoWriteBinding
import com.baek.untitledproject.domain.data.Post
import com.baek.untitledproject.domain.data.PostWrite
import com.baek.untitledproject.domain.utils.DateUiStyle
import com.baek.untitledproject.domain.utils.toLocalDate
import com.baek.untitledproject.domain.utils.toUiString
import com.baek.untitledproject.ui.MainActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import kotlin.math.roundToInt

@AndroidEntryPoint
class InfoWriteFragment : Fragment() {

    private var _binding: FragmentInfoWriteBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BoardWriteViewModel by hiltNavGraphViewModels(R.id.write_board_nav_graph)

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

        observeEditingPost()
        observeEditingImages()

        setupDateBtn()
        setupImageSelect()
        setupTextWatchers()
        validateInputs()
        setupNextBtn()

        setupBackPressHandler()
        setupDialogs()
    }

    private fun observeEditingPost() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.post.collect { post ->
                    bindPostData(post)
                }
            }
        }
    }

    //이미지는 따로 관리
    private fun observeEditingImages() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.images.collect { uris ->
                    renderImages(uris)
                }
            }
        }
    }

    private fun bindPostData(post: PostWrite) {
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
        val endMillis = viewModel.endMillis
        if (endMillis != null) {
            val endDate = endMillis.toLocalDate().toUiString(DateUiStyle.YMD_WITH_WEEKDAY)
            binding.recruitDateSelectBtn.text = endDate
        }
        post.content?.let {
            if (it != binding.contentInput.text.toString()) {
                binding.contentInput.setText(it)
            }
        }
        validateInputs()
    }

    private fun setupDateBtn() {
        binding.recruitDateSelectBtn.setOnClickListener {
            //오늘 이후만 선택 가능하도록
            val todayMillis = MaterialDatePicker.todayInUtcMilliseconds()

            val constraints = CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.from(todayMillis))
                .build()

            //TODO: Style 적용하기 ->.setTheme(R.style.~~)
            val builder = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
                .setCalendarConstraints(constraints)

            // 이전에 선택한 날짜 유지

            viewModel.endMillis?.let { builder.setSelection(it) }

            val picker = builder.build()

            picker.addOnPositiveButtonClickListener { selection ->
                viewModel.endMillis = selection
                val date = selection.toLocalDate()
                binding.recruitDateSelectBtn.text = date.toUiString(DateUiStyle.YMD_WITH_WEEKDAY)
            }

            picker.show(parentFragmentManager, "Select date")
        }
    }


    /*
        이미지 선택 로직
     */
    private fun setupImageSelect() {
        binding.selectImgBtn.setOnClickListener {
            if (viewModel.images.value.size >= 5) {
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
                if (viewModel.images.value.size >= 5) {
                    Toast.makeText(requireContext(), "이미지는 최대 5장까지 등록 가능합니다.", Toast.LENGTH_SHORT)
                        .show()
                    return@registerForActivityResult
                }
                viewModel.addImage(it)
            }
        }

    private fun renderImages(uris: List<Uri>) {
        val radiusPx = (10f * binding.root.resources.displayMetrics.density).roundToInt()

        while (binding.imgContainer.childCount > 1) {
            binding.imgContainer.removeViewAt(1)
        }
        uris.forEachIndexed { idx, uri ->
            val item =
                layoutInflater.inflate(R.layout.item_selected_image, binding.imgContainer, false)
            val thumb = item.findViewById<ImageView>(R.id.selectedImg)
            val cancel = item.findViewById<ImageView>(R.id.cancelBtn)

            Glide.with(this)
                .load(uri)
                .centerCrop()
                .transform(CenterCrop(), RoundedCorners(radiusPx))
                .into(thumb)
            thumb.tag = uri
            cancel.setOnClickListener { viewModel.removeImage(idx) }

            binding.imgContainer.addView(item)
        }
        // 스크롤을 맨 오른쪽으로 이동
        binding.imageScrollView.post {
            binding.imageScrollView.smoothScrollTo(binding.imgContainer.width, 0)
        }
        setImageCount(binding.imgContainer.childCount - 1)
    }

    private fun setImageCount(count: Int) {
        binding.curImgCount.text = "$count"
        if (count > 0) {
            binding.curImgCount.setTextColor(
                ContextCompat.getColor(binding.root.context, R.color.point_purple)
            )
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
        binding.nextBtn.isEnabled =
            titleNotNull && groupNameNotNull && contentNotNull
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

        viewModel.updateInfo(title, groupName, content)
    }

    private fun setupBackPressHandler() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            ExitConfirmDialogFragment().show(parentFragmentManager, "exit_dialog")
        }
    }

    private fun setupDialogs() {
        parentFragmentManager.setFragmentResultListener(
            "req_exit",
            viewLifecycleOwner
        ) { _, bundle ->
            if (bundle.getBoolean("confirmed", false)) {
                findNavController().popBackStack()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)
            ?.setToolbar(detailVisible = true, title = "모임 올리기")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}