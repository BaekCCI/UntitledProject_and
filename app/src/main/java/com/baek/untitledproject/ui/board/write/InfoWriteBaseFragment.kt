package com.baek.untitledproject.ui.board.write

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.baek.untitledproject.R
import com.baek.untitledproject.databinding.FragmentInfoWriteBinding
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
import kotlin.math.roundToInt

@AndroidEntryPoint
abstract class InfoWriteBaseFragment : Fragment() {

    private var _binding: FragmentInfoWriteBinding? = null
    protected val binding get() = _binding!!

    protected abstract val vm: BaseWriteViewModel

    /** 화면별 커스터마이징 훅 */
    protected open val toolbarTitle: String = "모임 올리기"
    protected open val maxImageCount: Int = 5
    protected open fun onNext() {}                 // 다음 화면 이동
    protected open fun onExitConfirmed() {         // 뒤로가기 confirm 후
        findNavController().popBackStack()
    }
    protected open fun setupNextBtn(){}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInfoWriteBinding.inflate(inflater, container, false)
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

        setupBackPressHandler()
        setupDialogs()
        setupNextBtn()
    }

    private fun observeEditingPost() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.post.collect { post ->
                    bindPostData(post)
                }
            }
        }
    }

    private fun observeEditingImages() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.images.collect { uris ->
                    Log.d("InfoWriteBaseFragment",uris.toString())
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
        vm.endMillis?.let {
            binding.recruitDateSelectBtn.text =
                it.toLocalDate().toUiString(DateUiStyle.YMD_WITH_WEEKDAY)
        }
        post.interviewLocation?.let {
            binding.interviewLocInput.setText(it)
        }
        post.content?.let {
            if (it != binding.contentInput.text.toString()) binding.contentInput.setText(
                it
            )
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
            vm.endMillis?.let { builder.setSelection(it) }
            val picker = builder.build()

            picker.addOnPositiveButtonClickListener { selection ->
                vm.endMillis = selection
                binding.recruitDateSelectBtn.text =
                    selection.toLocalDate().toUiString(DateUiStyle.YMD_WITH_WEEKDAY)
            }
            picker.show(parentFragmentManager, "Select date")
        }
    }

    private fun setupImageSelect() {
        binding.selectImgBtn.setOnClickListener {
            if (vm.images.value.size >= maxImageCount) {
                Toast.makeText(
                    requireContext(),
                    "이미지는 최대 ${maxImageCount}장까지 등록 가능합니다.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            pickImageLauncher.launch("image/*")
        }
    }

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                if (vm.images.value.size >= maxImageCount) {
                    Toast.makeText(
                        requireContext(),
                        "이미지는 최대 ${maxImageCount}장까지 등록 가능합니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@registerForActivityResult
                }
                vm.addImage(it)
            }
        }

    private fun renderImages(uris: List<Uri>) {
        val radiusPx = (10f * resources.displayMetrics.density).roundToInt()
        while (binding.imgContainer.childCount > 1) binding.imgContainer.removeViewAt(1)

        uris.forEachIndexed { idx, uri ->
            val item =
                layoutInflater.inflate(R.layout.item_selected_image, binding.imgContainer, false)
            val thumb = item.findViewById<ImageView>(R.id.selectedImg)
            val cancel = item.findViewById<ImageView>(R.id.cancelBtn)

            Glide.with(this)
                .load(uri)
                .transform(CenterCrop(), RoundedCorners(radiusPx))
                .into(thumb)

            cancel.setOnClickListener { vm.removeImage(idx) }
            binding.imgContainer.addView(item)
        }
        binding.imageScrollView.post {
            binding.imageScrollView.smoothScrollTo(binding.imgContainer.width, 0)
        }
        setImageCount(uris.size)
    }

    private fun setImageCount(count: Int) {
        binding.curImgCount.text = "$count"
        if (count > 0) {
            binding.curImgCount.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.point_purple)
            )
        }
    }

    private fun setupTextWatchers() {
        binding.titleInput.doAfterTextChanged { validateInputs() }
        binding.groupNameInput.doAfterTextChanged { validateInputs() }
        binding.contentInput.doAfterTextChanged { validateInputs() }
    }

    private fun validateInputs() {
        val ok = !binding.titleInput.text.isNullOrBlank() &&
                !binding.groupNameInput.text.isNullOrBlank() &&
                !binding.contentInput.text.isNullOrBlank()
        binding.nextBtn.isEnabled = ok
    }

    // ---------- Exit confirm ----------
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
            if (bundle.getBoolean("confirmed", false)) onExitConfirmed()
        }
    }


    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.setToolbar(detailVisible = true, title = toolbarTitle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}