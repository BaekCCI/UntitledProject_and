package com.baek.untitledproject.ui.recruit

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.baek.untitledproject.databinding.FragmentMyRecruitsBinding
import com.baek.untitledproject.ui.recruit.adapter.AppliedRecruitPagerAdapter
import com.baek.untitledproject.ui.recruit.adapter.MyRecruitPagerAdapter
import com.baek.untitledproject.ui.recruit.adapter.ScheduleGroupAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyRecruitsFragment : Fragment() {

    private var _binding: FragmentMyRecruitsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MyRecruitsViewModel by viewModels()

    private lateinit var scheduleGroupAdapter: ScheduleGroupAdapter
    private lateinit var myRecruitPagerAdapter: MyRecruitPagerAdapter
    private lateinit var appliedRecruitPagerAdapter: AppliedRecruitPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyRecruitsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapters()
        setupViews()
        observeData()

        // 데이터 로드
        viewModel.loadAllData()
    }

    private fun initAdapters() {
        // 일정 그룹 어댑터
        scheduleGroupAdapter = ScheduleGroupAdapter { groupId ->
            viewModel.toggleScheduleGroup(groupId)
        }

        // 내 공고 ViewPager2 어댑터
        myRecruitPagerAdapter = MyRecruitPagerAdapter(
            onInterviewManageClick = { recruitId ->
                viewModel.onInterviewManageClick(recruitId)
            },
            onApplicantManageClick = { recruitId ->
                val intent = Intent(requireContext(), ApplicantManagementActivity::class.java)
                intent.putExtra("recruitId", recruitId)
                startActivity(intent)
            },
            onPostManageClick = { recruitId ->
                viewModel.onPostManageClick(recruitId)
            },
            onCardClick = { recruitId ->
                // TODO: 상세 페이지로 이동
            }
        )

        appliedRecruitPagerAdapter = AppliedRecruitPagerAdapter(
            onInterviewReserveClick = { recruitId ->

                val appliedRecruit = viewModel.appliedRecruits.value.find { it.id == recruitId }

                if (appliedRecruit != null) {
                    val intent = Intent(requireContext(), InterviewReservationActivity::class.java).apply {
                        putExtra("postId", appliedRecruit.postId)
                        putExtra("applicationId", appliedRecruit.id)
                    }

                    startActivityForResult(intent, REQUEST_CODE_INTERVIEW_RESERVATION)
                } else {
                    Toast.makeText(requireContext(), "지원서 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            },
            onViewPostClick = { recruitId ->
                viewModel.onViewPostClick(recruitId)
            },
            onCardClick = { recruitId ->
                // TODO: 상세 페이지로 이동
            }
        )
    }

    companion object {
        private const val REQUEST_CODE_INTERVIEW_RESERVATION = 1001
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_INTERVIEW_RESERVATION && resultCode == Activity.RESULT_OK) {
            viewModel.loadAllData()
            Toast.makeText(requireContext(), "면접 예약이 완료되었습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupViews() {
        binding.scheduleRecyclerView.apply {
            adapter = scheduleGroupAdapter
            layoutManager = LinearLayoutManager(requireContext())
            isNestedScrollingEnabled = false
        }

        // 내 공고 ViewPager2
        binding.myRecruitViewPager.apply {
            adapter = myRecruitPagerAdapter
            orientation = ViewPager2.ORIENTATION_HORIZONTAL

            // 부드러운 페이지 전환을 위한 설정
            offscreenPageLimit = 1

            // 페이지 변경 리스너 등록
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    updateMyRecruitIndicator(position)
                }
            })
        }

        // 지원한 공고 ViewPager2
        binding.appliedRecruitViewPager.apply {
            adapter = appliedRecruitPagerAdapter
            orientation = ViewPager2.ORIENTATION_HORIZONTAL

            // 부드러운 페이지 전환을 위한 설정
            offscreenPageLimit = 1

            // 페이지 변경 리스너 등록
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    updateAppliedRecruitIndicator(position)
                }
            })
        }
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // 일정 그룹 관찰 (기존 유지)
                launch {
                    viewModel.scheduleGroups.collect { scheduleGroups ->
                        scheduleGroupAdapter.submitList(scheduleGroups)
                    }
                }

                // 내 공고 관찰
                launch {
                    viewModel.myRecruits.collect { myRecruits ->
                        myRecruitPagerAdapter.submitList(myRecruits)
                        // 개수 업데이트
                        binding.myRecruitCountTxt.text = myRecruits.size.toString()
                        // 인디케이터 설정
                        setupMyRecruitIndicator(myRecruits.size)
                    }
                }

                // 지원한 공고 관찰
                launch {
                    viewModel.appliedRecruits.collect { appliedRecruits ->
                        appliedRecruitPagerAdapter.submitList(appliedRecruits)
                        // 개수 업데이트
                        binding.appliedRecruitCountTxt.text = appliedRecruits.size.toString()
                        // 인디케이터 설정
                        setupAppliedRecruitIndicator(appliedRecruits.size)
                    }
                }

                // 로딩 상태
                launch {
                    viewModel.isLoading.collect { isLoading ->
                        if (isLoading) {
                            Log.d("MyRecruitsFragment", "데이터 로딩 중...")
                        } else {
                            Log.d("MyRecruitsFragment", "데이터 로딩 완료")
                        }
                    }
                }
            }
        }
    }

    // 내 공고 인디케이터 설정
    private fun setupMyRecruitIndicator(itemCount: Int) {
        if (itemCount <= 1) {
            binding.myRecruitIndicator.visibility = View.GONE
            return
        }

        binding.myRecruitIndicator.visibility = View.VISIBLE
        binding.myRecruitIndicator.removeAllViews()

        // 인디케이터 점
        for (i in 0 until itemCount) {
            val indicator = createIndicatorDot(i == 0)
            binding.myRecruitIndicator.addView(indicator)
        }
    }

    // 지원한 공고 인디케이터 설정
    private fun setupAppliedRecruitIndicator(itemCount: Int) {
        if (itemCount <= 1) {
            binding.appliedRecruitIndicator.visibility = View.GONE
            return
        }

        binding.appliedRecruitIndicator.visibility = View.VISIBLE
        binding.appliedRecruitIndicator.removeAllViews()

        // 인디케이터 점들 추가
        for (i in 0 until itemCount) {
            val indicator = createIndicatorDot(i == 0)
            binding.appliedRecruitIndicator.addView(indicator)
        }
    }

    // 내 공고 인디케이터 업데이트
    private fun updateMyRecruitIndicator(position: Int) {
        for (i in 0 until binding.myRecruitIndicator.childCount) {
            val indicator = binding.myRecruitIndicator.getChildAt(i)
            updateIndicatorDot(indicator, i == position)
        }
    }

    // 지원한 공고 인디케이터 업데이트
    private fun updateAppliedRecruitIndicator(position: Int) {
        for (i in 0 until binding.appliedRecruitIndicator.childCount) {
            val indicator = binding.appliedRecruitIndicator.getChildAt(i)
            updateIndicatorDot(indicator, i == position)
        }
    }

    // 인디케이터 점 생성
    private fun createIndicatorDot(isSelected: Boolean): View {
        val dot = View(requireContext())

        val dotSize = (8 * resources.displayMetrics.density).toInt()
        val dotMargin = (4 * resources.displayMetrics.density).toInt()

        val params = ViewGroup.MarginLayoutParams(dotSize, dotSize)
        params.setMargins(dotMargin, 0, dotMargin, 0)
        dot.layoutParams = params

        updateIndicatorDot(dot, isSelected)
        return dot
    }

    // 인디케이터 점 상태 업데이트
    private fun updateIndicatorDot(dot: View, isSelected: Boolean) {
        if (isSelected) {
            dot.setBackgroundResource(com.baek.untitledproject.R.drawable.indicator_dot_selected)
        } else {
            dot.setBackgroundResource(com.baek.untitledproject.R.drawable.indicator_dot_unselected)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}