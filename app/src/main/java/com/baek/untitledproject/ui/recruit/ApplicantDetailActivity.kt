package com.baek.untitledproject.ui.recruit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.baek.untitledproject.databinding.ActivityApplicantDetailBinding
import com.baek.untitledproject.databinding.BottomSheetConfirmBinding
import com.baek.untitledproject.domain.data.ApplicantSummary
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ApplicantDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityApplicantDetailBinding
    private val viewModel: ApplicantDetailViewModel by viewModels()

    private var applicantId: String = ""
    private var isMotivationExpanded = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityApplicantDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        applicantId = intent.getStringExtra("applicantId") ?: ""

        setupUI()
        setupClickListeners()
        observeData()

        viewModel.loadApplicantDetail(applicantId)
    }

    private fun setupUI() {
        updateMotivationState()
    }

    private fun setupClickListeners() {
        // 닫기 버튼
        binding.closeBtn.setOnClickListener {
            finish()
        }

        // 쪽지 보내기 버튼
        binding.sendMessageBtn.setOnClickListener {
            viewModel.sendMessage(applicantId)
        }

        // 지원동기 펼침/접힘
        binding.motivationHeader.setOnClickListener {
            isMotivationExpanded = !isMotivationExpanded
            updateMotivationState()
        }
    }

    private fun observeData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.applicantDetail.collect { applicant ->
                        applicant?.let { updateUI(it) }
                    }
                }

                launch {
                    viewModel.actionButtons.collect { buttons ->
                        setupActionButtons(buttons)
                    }
                }

                launch {
                    viewModel.isLoading.collect { isLoading ->
                        // TODO: 로딩 UI 표시/숨김
                    }
                }
            }
        }
    }

    private fun updateUI(applicant: ApplicantSummary) {
        binding.apply {
            nameTxt.text = applicant.name
            genderAgeTxt.text = "${applicant.gender}, ${applicant.age}세"
            phoneNumberTxt.text = applicant.phoneNumber ?: "연락처 없음"
            studentIdTxt.text = applicant.studentId ?: "학번 없음"
            departmentTxt.text = applicant.department
            motivationTxt.text = applicant.motivation ?: "지원동기가 없습니다."

            updateStatusDisplay(applicant.status)
        }
    }

    private fun updateStatusDisplay(status: String) {
        val (statusText, statusColor) = when (status) {
            "submitted" -> "지원서 제출 완료" to "#2196F3"
            "interview_scheduled" -> "면접 확정" to "#2196F3"
            "interview_completed" -> "면접 완료" to "#FF9800"
            "passed" -> "합격" to "#4CAF50"
            "failed" -> "불합격" to "#F44336"
            else -> "알 수 없음" to "#666666"
        }

        binding.statusTxt.text = statusText
    }

    private fun updateMotivationState() {
        if (isMotivationExpanded) {
            binding.motivationContent.visibility = View.VISIBLE
            binding.motivationArrow.rotation = 180f
        } else {
            binding.motivationContent.visibility = View.GONE
            binding.motivationArrow.rotation = 0f
        }
    }

    private fun setupActionButtons(buttonGroups: List<List<ApplicantActionButton>>) {
        if (buttonGroups.isEmpty()) {
            binding.actionButtonsPager.visibility = View.GONE
            return
        }

        binding.actionButtonsPager.visibility = View.VISIBLE

        val adapter = ApplicantActionPagerAdapter(this, buttonGroups) { action ->
            handleActionClick(action)
        }
        binding.actionButtonsPager.adapter = adapter
    }

    private fun handleActionClick(action: ApplicantActionButton) {
        showConfirmBottomSheet(
            message = action.confirmMessage,
            confirmText = action.text,
            isDangerous = action.isDangerous,
            onConfirm = {
                viewModel.performAction(applicantId, action.actionType)
            }
        )
    }

    private fun showConfirmBottomSheet(
        message: String,
        confirmText: String,
        isDangerous: Boolean,
        onConfirm: () -> Unit
    ) {
        val confirmBinding = BottomSheetConfirmBinding.inflate(LayoutInflater.from(this))
        val confirmDialog = BottomSheetDialog(this)

        confirmBinding.apply {
            confirmMessageTxt.text = message
            confirmBtn.text = confirmText

            if (isDangerous) {
                confirmBtn.backgroundTintList = getColorStateList(android.R.color.holo_red_dark)
                confirmBtn.setTextColor(getColor(android.R.color.white))
            } else {
                confirmBtn.backgroundTintList = getColorStateList(android.R.color.black)
                confirmBtn.setTextColor(getColor(android.R.color.white))
            }

            confirmBtn.setOnClickListener {
                onConfirm()
                confirmDialog.dismiss()
            }

            cancelBtn.setOnClickListener {
                confirmDialog.dismiss()
            }
        }

        confirmDialog.setContentView(confirmBinding.root)
        confirmDialog.show()
    }

    // ViewPager2 어댑터
    private class ApplicantActionPagerAdapter(
        fragmentActivity: FragmentActivity,
        private val buttonGroups: List<List<ApplicantActionButton>>,
        private val onActionClick: (ApplicantActionButton) -> Unit
    ) : FragmentStateAdapter(fragmentActivity) {

        override fun getItemCount(): Int = buttonGroups.size

        override fun createFragment(position: Int): Fragment {
            return ApplicantActionButtonFragment.newInstance(buttonGroups[position], onActionClick)
        }
    }
}

data class ApplicantActionButton(
    val text: String,
    val actionType: String,
    val confirmMessage: String,
    val isDangerous: Boolean = false
)