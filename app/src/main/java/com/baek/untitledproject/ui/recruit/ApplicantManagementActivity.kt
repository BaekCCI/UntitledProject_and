package com.baek.untitledproject.ui.recruit

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.baek.untitledproject.databinding.ActivityApplicantManagementBinding
import com.baek.untitledproject.databinding.BottomSheetConfirmBinding
import com.baek.untitledproject.domain.data.ApplicantSummary
import com.baek.untitledproject.ui.recruit.adapter.ApplicantListAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ApplicantManagementActivity : AppCompatActivity() {

    private lateinit var binding: ActivityApplicantManagementBinding
    private val viewModel: ApplicantManagementViewModel by viewModels()

    private lateinit var applicantAdapter: ApplicantAdapter
    private lateinit var applicantListAdapter: ApplicantListAdapter
    private var isSelectionMode = false
    private var currentFilter = "all"
    private var currentTab = 0
    private var isSearchMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityApplicantManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupRecyclerView()
        setupClickListeners()
        observeData()

        val recruitId = intent.getStringExtra("recruitId") ?: ""
        viewModel.loadApplicants(recruitId)
    }

    private fun setupUI() {
        binding.titleTxt.text = "댄스 동아리 OO 모집합니다"
        binding.recruitPeriodTxt.text = "8월 1일 - 9월 30일"
        updateTabIndicator(0)
    }

    private fun setupRecyclerView() {
        applicantAdapter = ApplicantAdapter(
            onItemClick = { applicant ->
                // 선택 모드일 때만 선택/해제 동작
                if (isSelectionMode) {
                    applicantAdapter.toggleSelection(applicant.id)
                    updateSelectionActionBar()
                }
                // 선택 모드가 아닐 때는 아무것도 안함
            },
            onItemLongClick = { applicant ->
                if (isSelectionMode) {
                    // 선택 모드에서는 기존 동작
                    applicantAdapter.toggleSelection(applicant.id)
                    updateSelectionActionBar()
                } else {
                    // 선택 모드가 아닐 때는 되돌리기 AlertDialog 표시
                    showRevertAlertDialog(applicant)
                }
            },
            onSelectionChanged = {
                // 선택이 변경될 때마다 액션바 업데이트
                if (isSelectionMode) {
                    updateSelectionActionBar()
                }
            }
        )

        applicantListAdapter = ApplicantListAdapter(
            onItemClick = { applicant ->
                // 지원자 상세 화면으로 이동
                val intent = Intent(this@ApplicantManagementActivity, ApplicantDetailActivity::class.java)
                intent.putExtra("applicantId", applicant.id)
                startActivity(intent)
            }
        )

        binding.applicantRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@ApplicantManagementActivity)
        }

        switchToTab(0)
    }

    private fun setupClickListeners() {
        binding.backBtn.setOnClickListener {
            if (isSelectionMode) {
                disableSelectionMode()
            } else {
                finish()
            }
        }

        // 탭 전환 시 선택 모드 해제
        binding.applicantManageTab.setOnClickListener {
            // 다른 탭에서 관리 탭으로 전환 시 선택 모드 해제
            if (isSelectionMode && currentTab != 0) {
                disableSelectionMode()
            }
            // 탭 전환 시 검색 모드 해제
            if (isSearchMode) {
                exitSearchMode()
            }
            switchToTab(0)
        }

        binding.applicantListTab.setOnClickListener {
            // 관리 탭에서 리스트 탭으로 전환 시 선택 모드 해제
            if (isSelectionMode && currentTab != 1) {
                disableSelectionMode()
            }
            // 탭 전환 시 검색 모드 해제
            if (isSearchMode) {
                exitSearchMode()
            }
            switchToTab(1)
        }

        // 필터 버튼들 - 필터 변경 시 선택 모드 해제
        binding.filterAllBtn.setOnClickListener {
            // 필터 변경 시 선택 모드 해제
            if (isSelectionMode) {
                disableSelectionMode()
            }
            // 검색 모드도 해제
            if (isSearchMode) {
                exitSearchMode()
            }
            updateFilterButtons("all")
            viewModel.filterApplicants("all")
        }

        binding.filterInterviewBtn.setOnClickListener {
            // 필터 변경 시 선택 모드 해제
            if (isSelectionMode) {
                disableSelectionMode()
            }
            // 검색 모드도 해제
            if (isSearchMode) {
                exitSearchMode()
            }
            updateFilterButtons("interview")
            viewModel.filterApplicants("interview")
        }

        binding.filterReviewBtn.setOnClickListener {
            // 필터 변경 시 선택 모드 해제
            if (isSelectionMode) {
                disableSelectionMode()
            }
            // 검색 모드도 해제
            if (isSearchMode) {
                exitSearchMode()
            }
            updateFilterButtons("review")
            viewModel.filterApplicants("review")
        }

        binding.filterCompleteBtn.setOnClickListener {
            // 필터 변경 시 선택 모드 해제
            if (isSelectionMode) {
                disableSelectionMode()
            }
            // 검색 모드도 해제
            if (isSearchMode) {
                exitSearchMode()
            }
            updateFilterButtons("complete")
            viewModel.filterApplicants("complete")
        }

        // 선택 모드 버튼들
        binding.selectAllBtn.setOnClickListener {
            val currentSelectedCount = applicantAdapter.getSelectedCount()
            val totalCount = applicantAdapter.itemCount

            if (currentSelectedCount == totalCount) {
                // 모두 선택된 상태면 전체 해제
                applicantAdapter.clearSelection()
            } else {
                // 일부만 선택되었거나 아무것도 선택 안 된 상태면 전체 선택
                applicantAdapter.selectAll()
            }
            updateSelectionActionBar()
        }

        binding.selectModeBtn.setOnClickListener {
            enableSelectionMode()
            showSelectionActionBar()
        }

        binding.cancelSelectionBtn.setOnClickListener {
            disableSelectionMode()
        }

        binding.notifyAllBtn.setOnClickListener {
            viewModel.notifyAllApplicants()
        }

        // 검색 아이콘 클릭
        binding.searchIcon.setOnClickListener {
            toggleSearchMode()
        }

        // 선택 모드 액션바 버튼들
        binding.leftActionButton.setOnClickListener {
            val action = binding.leftActionButton.text.toString()
            showConfirmBottomSheet(action, action.contains("불합격") || action.contains("취소"))
        }

        binding.rightActionButton.setOnClickListener {
            val action = binding.rightActionButton.text.toString()
            showConfirmBottomSheet(action, false)
        }

        binding.singleActionButton.setOnClickListener {
            val action = binding.singleActionButton.text.toString()
            showConfirmBottomSheet(action, false)
        }
    }

    private fun observeData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.applicants.collect { applicants ->
                        applicantAdapter.submitList(applicants)
                        applicantListAdapter.submitList(applicants)
                    }
                }

                launch {
                    viewModel.isLoading.collect { isLoading ->
                        // TODO: 로딩 UI 표시/숨김
                    }
                }

                launch {
                    viewModel.errorMessage.collect { errorMessage ->
                        errorMessage?.let {
                            showErrorBottomSheet(it)
                            viewModel.clearErrorMessage()
                        }
                    }
                }
            }
        }
    }

    private fun toggleSearchMode() {
        if (isSearchMode) {
            // 검색 모드 해제
            exitSearchMode()
        } else {
            // 검색 모드 진입
            enterSearchMode()
        }
    }

    private fun enterSearchMode() {
        isSearchMode = true

        // 기존 UI 숨김
        binding.searchLabel.visibility = View.GONE
        binding.selectModeBtn.visibility = View.GONE

        // 검색 아이콘을 닫기 아이콘으로 변경
        binding.searchIcon.setImageResource(android.R.drawable.ic_menu_close_clear_cancel)

        // 검색 입력 필드를 동적으로 추가
        createSearchEditText()
    }

    private fun createSearchEditText() {
        // EditText 생성
        val searchEditText = android.widget.EditText(this)
        searchEditText.apply {
            id = View.generateViewId()
            hint = "이름, 학과, 학번으로 검색"
            background = null
            textSize = 16f
            setPadding(0, 0, 0, 0)

            // 텍스트 변경 리스너
            addTextChangedListener(object : android.text.TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: android.text.Editable?) {
                    viewModel.updateSearchQuery(s?.toString() ?: "")
                }
            })

            // 엔터키 처리
            setOnEditorActionListener { _, actionId, _ ->
                if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                    hideKeyboard()
                    true
                } else {
                    false
                }
            }
        }

        // 기존 레이아웃에 추가 (searchLabel 위치에)
        val layoutParams = binding.searchLabel.layoutParams
        (binding.searchLabel.parent as ViewGroup).addView(searchEditText, layoutParams)

        // 포커스 주고 키보드 열기
        searchEditText.requestFocus()
        showKeyboard(searchEditText)
    }

    private fun exitSearchMode() {
        isSearchMode = false

        // 검색어 초기화
        viewModel.clearSearch()

        // 동적으로 추가된 EditText 제거
        removeSearchEditText()

        // UI 복원
        binding.searchLabel.visibility = View.VISIBLE
        if (!isSelectionMode) {
            binding.selectModeBtn.visibility = View.VISIBLE
        }
        binding.searchIcon.setImageResource(android.R.drawable.ic_menu_search)

        // 키보드 숨김
        hideKeyboard()
    }

    private fun removeSearchEditText() {
        val searchHeader = binding.searchLabel.parent as ViewGroup
        // EditText 찾아서 제거
        for (i in 0 until searchHeader.childCount) {
            val child = searchHeader.getChildAt(i)
            if (child is android.widget.EditText) {
                searchHeader.removeView(child)
                break
            }
        }
    }

    private fun showKeyboard(view: View) {
        val imm = getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
        imm.showSoftInput(view, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT)
    }

    private fun hideKeyboard() {
        val imm = getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
        currentFocus?.let {
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    private fun showSearchDialog() {
        // 이제 사용하지 않는 메서드
    }

    private fun enableSelectionMode() {
        isSelectionMode = true

        // UI 변경
        binding.searchLabel.visibility = View.GONE
        binding.searchIcon.visibility = View.GONE
        binding.selectModeBtn.visibility = View.GONE

        binding.selectAllBtn.visibility = View.VISIBLE
        binding.cancelSelectionBtn.visibility = View.VISIBLE

        // 하단 버튼들 숨김
        binding.notifyAllBtn.visibility = View.GONE

        applicantAdapter.setSelectionMode(true)
    }

    private fun disableSelectionMode() {
        // 이미 해제된 상태면 중복 실행 방지
        if (!isSelectionMode) return

        isSelectionMode = false

        // 상단 UI 요소들 복원
        binding.selectAllBtn.visibility = View.GONE
        binding.cancelSelectionBtn.visibility = View.GONE
        binding.searchLabel.visibility = View.VISIBLE
        binding.searchIcon.visibility = View.VISIBLE
        binding.selectModeBtn.visibility = View.VISIBLE

        // 하단 버튼 상태 복원 (필터에 따라)
        updateBottomButton()

        // 어댑터 선택 상태 완전히 초기화
        applicantAdapter.setSelectionMode(false)
        applicantAdapter.clearSelection()

        // 선택 모드 액션바 숨김
        binding.selectionActionBar.visibility = View.GONE
    }

    private fun showSelectionActionBar() {
        binding.selectionActionBar.visibility = View.VISIBLE
        updateSelectionActionBar()
    }

    private fun updateSelectionActionBar() {
        val selectedCount = applicantAdapter.getSelectedCount()
        binding.selectedCountText.text = "${selectedCount}명의 지원자가 선택되었습니다"

        if (selectedCount == 0) {
            hideAllActionButtons()
            return
        }

        val selectedIds = applicantAdapter.getSelectedIds()
        val selectedApplicants = viewModel.applicants.value.filter { it.id in selectedIds }
        val statusGroups = selectedApplicants.groupBy { it.status }

        hideAllActionButtons()

        when {
            statusGroups.containsKey("지원서 제출됨") -> {
                showTwoButtons("불합격", "면접 제안", true, true)
            }
            statusGroups.containsKey("면접 대기 중") -> {
                showTwoButtons("면접 취소", "면접 완료", true, true)
            }
            statusGroups.containsKey("심사 대기 중") -> {
                showTwoButtons("불합격", "합격", true, true)
            }
            statusGroups.containsKey("심사 완료됨") -> {
                showSingleButton("심사 결과 알리기")
            }
        }
    }

    private fun hideAllActionButtons() {
        binding.leftActionButton.visibility = View.GONE
        binding.rightActionButton.visibility = View.GONE
        binding.singleActionButton.visibility = View.GONE
    }

    private fun showTwoButtons(
        leftText: String,
        rightText: String,
        leftIsDangerous: Boolean,
        rightIsPrimary: Boolean
    ) {
        binding.leftActionButton.apply {
            visibility = View.VISIBLE
            text = leftText

            if (leftIsDangerous && leftText.contains("불합격")) {
                // 부정적 액션 - 연한 회색 배경, 테두리 없음
                backgroundTintList = getColorStateList(android.R.color.darker_gray)
                setTextColor(getColor(android.R.color.black))
            } else {
                // 일반 액션 - 기본 스타일
                backgroundTintList = null
                setTextColor(getColor(android.R.color.black))
            }
        }

        binding.rightActionButton.apply {
            visibility = View.VISIBLE
            text = rightText
            backgroundTintList = if (rightIsPrimary) {
                getColorStateList(android.R.color.black)
            } else {
                null
            }
            setTextColor(
                if (rightIsPrimary) getColor(android.R.color.white)
                else getColor(android.R.color.black)
            )
        }
    }

    private fun showSingleButton(text: String) {
        binding.singleActionButton.apply {
            visibility = View.VISIBLE
            this.text = text
        }
    }

    private fun showThreeButtons(leftText: String, centerText: String, rightText: String) {
        // 기존 버튼들 숨김
        hideAllActionButtons()

        // 왼쪽: 이전 단계로 (중립 색상)
        binding.leftActionButton.apply {
            visibility = View.VISIBLE
            text = leftText
            backgroundTintList = null
            setTextColor(getColor(android.R.color.black))
        }

        // 오른쪽: 주요 액션 (검은색)
        binding.rightActionButton.apply {
            visibility = View.VISIBLE
            text = rightText
            backgroundTintList = getColorStateList(android.R.color.black)
            setTextColor(getColor(android.R.color.white))
        }

        // 가운데는 singleActionButton을 재활용 (위험한 액션용)
        binding.singleActionButton.apply {
            visibility = View.VISIBLE
            text = centerText
            if (centerText.contains("취소") || centerText.contains("불합격")) {
                backgroundTintList = getColorStateList(android.R.color.darker_gray)
                setTextColor(getColor(android.R.color.black))
            } else {
                backgroundTintList = null
                setTextColor(getColor(android.R.color.black))
            }
        }
    }

    private fun showConfirmBottomSheet(action: String, isDangerous: Boolean) {
        val confirmBinding = BottomSheetConfirmBinding.inflate(LayoutInflater.from(this))
        val confirmDialog = BottomSheetDialog(this)

        // 메시지 설정
        val message = when (action) {
            "면접 제안" -> "선택한 지원자에게\n면접을 제안하시겠어요?"
            "면접 완료" -> "선택한 지원자의\n면접을 완료 처리하시겠어요?"
            "합격" -> "선택한 지원자를\n합격 처리하시겠어요?"
            "불합격" -> "선택한 지원자를\n불합격 처리하시겠어요?"
            "면접 취소" -> "선택한 지원자의\n면접을 취소하시겠어요?"
            "심사 결과 알리기" -> "선택한 지원자에게\n심사 결과를 알리시겠어요?"
            else -> "해당 작업을 진행하시겠어요?"
        }

        confirmBinding.confirmMessageTxt.text = message
        confirmBinding.confirmBtn.text = action

        // 바텀시트에서는 위험한 액션이면 항상 빨간색
        if (isDangerous) {
            confirmBinding.confirmBtn.backgroundTintList = getColorStateList(android.R.color.holo_red_dark)
            confirmBinding.confirmBtn.setTextColor(getColor(android.R.color.white))
        } else {
            confirmBinding.confirmBtn.backgroundTintList = getColorStateList(android.R.color.black)
            confirmBinding.confirmBtn.setTextColor(getColor(android.R.color.white))
        }

        confirmBinding.confirmBtn.setOnClickListener {
            performAction(action)
            confirmDialog.dismiss()
        }

        confirmBinding.cancelBtn.setOnClickListener {
            confirmDialog.dismiss()
        }

        confirmDialog.setContentView(confirmBinding.root)
        confirmDialog.show()
    }

    private fun showErrorBottomSheet(errorMessage: String) {
        val errorBinding = BottomSheetConfirmBinding.inflate(LayoutInflater.from(this))
        val errorDialog = BottomSheetDialog(this)

        errorBinding.confirmMessageTxt.text = errorMessage
        errorBinding.confirmBtn.text = "확인"
        errorBinding.cancelBtn.visibility = View.GONE  // 취소 버튼 숨김

        errorBinding.confirmBtn.setOnClickListener {
            errorDialog.dismiss()
        }

        errorDialog.setContentView(errorBinding.root)
        errorDialog.show()
    }

    private fun showRevertAlertDialog(applicant: ApplicantSummary) {
        android.util.Log.d("Activity", "showRevertAlertDialog 호출: ${applicant.name}, 상태: ${applicant.status}")

        // 되돌릴 수 없는 상태 체크
        if (applicant.status == "지원서 제출됨") {
            android.util.Log.d("Activity", "첫 번째 단계라서 되돌리기 불가")
            return // 첫 번째 단계는 되돌릴 수 없음
        }

        val previousStage = when (applicant.status) {
            "면접 대기 중" -> "지원서 제출 단계"
            "심사 대기 중" -> "면접 대기 단계"
            "심사 완료됨" -> "심사 대기 단계"
            else -> {
                android.util.Log.d("Activity", "알 수 없는 상태: ${applicant.status}")
                return
            }
        }

        android.util.Log.d("Activity", "AlertDialog 표시: ${applicant.name} -> $previousStage")

        android.app.AlertDialog.Builder(this)
            .setTitle("이전 단계로 되돌리기")
            .setMessage("${applicant.name} 지원자를\n${previousStage}로 되돌리시겠습니까?")
            .setPositiveButton("되돌리기") { _, _ ->
                android.util.Log.d("Activity", "되돌리기 확인됨: ${applicant.id}")
                viewModel.revertToPreviousStage(listOf(applicant.id))
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun performAction(action: String) {
        val selectedIds = applicantAdapter.getSelectedIds()

        when (action) {
            "면접 제안" -> viewModel.scheduleInterviews(selectedIds)
            "면접 완료" -> viewModel.completeInterviews(selectedIds)
            "합격" -> viewModel.passApplicants(selectedIds)
            "불합격" -> viewModel.failApplicants(selectedIds)
            "면접 취소" -> {/* TODO: 면접 취소 로직 */}
            "심사 결과 알리기" -> viewModel.notifyResults(selectedIds)
        }

        disableSelectionMode()
    }

    private fun updateFilterButtons(selectedFilter: String) {
        currentFilter = selectedFilter
        resetFilterButtonStyles()

        when (selectedFilter) {
            "all" -> activateFilterButton(binding.filterAllBtn)
            "interview" -> activateFilterButton(binding.filterInterviewBtn)
            "review" -> activateFilterButton(binding.filterReviewBtn)
            "complete" -> activateFilterButton(binding.filterCompleteBtn)
        }

        if (!isSelectionMode) {
            updateBottomButton()
        }
    }

    private fun updateBottomButton() {
        binding.notifyAllBtn.visibility = if (currentFilter == "complete") {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun resetFilterButtonStyles() {
        listOf(
            binding.filterAllBtn,
            binding.filterInterviewBtn,
            binding.filterReviewBtn,
            binding.filterCompleteBtn
        ).forEach { button ->
            button.setBackgroundTintList(null)
            button.setTextColor(getColor(android.R.color.black))
        }
    }

    private fun activateFilterButton(button: androidx.appcompat.widget.AppCompatButton) {
        button.setBackgroundTintList(getColorStateList(android.R.color.black))
        button.setTextColor(getColor(android.R.color.white))
    }

    private fun updateTabIndicator(position: Int) {
        val tabWidth = binding.applicantManageTab.width
        val indicatorParams = binding.tabIndicator.layoutParams
        indicatorParams.width = tabWidth
        binding.tabIndicator.layoutParams = indicatorParams

        if (position == 0) {
            binding.applicantManageTab.setTextColor(getColor(android.R.color.black))
            binding.applicantListTab.setTextColor(getColor(android.R.color.darker_gray))
        } else {
            binding.applicantManageTab.setTextColor(getColor(android.R.color.darker_gray))
            binding.applicantListTab.setTextColor(getColor(android.R.color.black))
        }
    }

    private fun switchToTab(tabIndex: Int) {
        currentTab = tabIndex

        when (tabIndex) {
            0 -> {
                binding.applicantRecyclerView.adapter = applicantAdapter

                binding.filterAllBtn.visibility = View.VISIBLE
                binding.filterInterviewBtn.visibility = View.VISIBLE
                binding.filterReviewBtn.visibility = View.VISIBLE
                binding.filterCompleteBtn.visibility = View.VISIBLE

                binding.searchHeader.visibility = View.VISIBLE

                // 선택 모드가 아닐 때만 선택 버튼 표시
                if (!isSelectionMode) {
                    binding.selectModeBtn.visibility = View.VISIBLE
                    binding.searchLabel.visibility = View.VISIBLE
                    binding.searchIcon.visibility = View.VISIBLE
                    updateBottomButton()
                }
            }
            1 -> {
                binding.applicantRecyclerView.adapter = applicantListAdapter

                // 리스트 탭에서는 선택 모드 강제 해제
                if (isSelectionMode) {
                    disableSelectionMode()
                }

                binding.filterAllBtn.visibility = View.GONE
                binding.filterInterviewBtn.visibility = View.GONE
                binding.filterReviewBtn.visibility = View.GONE
                binding.filterCompleteBtn.visibility = View.GONE

                binding.searchLabel.visibility = View.VISIBLE
                binding.searchIcon.visibility = View.VISIBLE
                binding.selectModeBtn.visibility = View.GONE
                binding.selectAllBtn.visibility = View.GONE
                binding.cancelSelectionBtn.visibility = View.GONE

                binding.notifyAllBtn.visibility = View.GONE
                binding.selectionActionBar.visibility = View.GONE
            }
        }

        updateTabIndicator(tabIndex)
    }

    override fun onBackPressed() {
        if (isSelectionMode) {
            disableSelectionMode()
        } else {
            super.onBackPressed()
        }
    }
}