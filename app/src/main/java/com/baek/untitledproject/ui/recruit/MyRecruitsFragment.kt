package com.baek.untitledproject.ui.recruit

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.baek.untitledproject.databinding.FragmentMyRecruitsBinding
import com.baek.untitledproject.ui.recruit.adapter.AppliedRecruitAdapter
import com.baek.untitledproject.ui.recruit.adapter.MyRecruitAdapter
import com.baek.untitledproject.ui.recruit.adapter.ScheduleGroupAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyRecruitsFragment : Fragment() {

    private var _binding: FragmentMyRecruitsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MyRecruitsViewModel by viewModels()

    // 어댑터들
    private lateinit var scheduleGroupAdapter: ScheduleGroupAdapter
    private lateinit var myRecruitAdapter: MyRecruitAdapter
    private lateinit var appliedRecruitAdapter: AppliedRecruitAdapter

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
        setupRecyclerViews()
        observeData()

        // 데이터 로드
        viewModel.loadAllData()
    }

    private fun initAdapters() {
        // 일정 그룹 어댑터
        scheduleGroupAdapter = ScheduleGroupAdapter { groupId ->
            viewModel.toggleScheduleGroup(groupId)
        }

        // 내 공고 어댑터
        myRecruitAdapter = MyRecruitAdapter(
            onInterviewManageClick = { recruitId ->
                Log.d("MyRecruitsFragment", "면접 예약 관리 클릭: $recruitId")
                viewModel.onInterviewManageClick(recruitId)
            },
            onApplicantManageClick = { recruitId ->
                Log.d("MyRecruitsFragment", "지원자 관리 클릭: $recruitId")
                viewModel.onApplicantManageClick(recruitId)
            },
            onPostManageClick = { recruitId ->
                Log.d("MyRecruitsFragment", "작성글 관리 클릭: $recruitId")
                viewModel.onPostManageClick(recruitId)
            },
            onCardClick = { recruitId ->
                Log.d("MyRecruitsFragment", "내 공고 카드 클릭: $recruitId")
                // TODO: 상세 페이지로 이동
            }
        )

        // 지원한 공고 어댑터
        appliedRecruitAdapter = AppliedRecruitAdapter(
            onInterviewReserveClick = { recruitId ->
                Log.d("MyRecruitsFragment", "면접 예약 클릭: $recruitId")
                viewModel.onInterviewReserveClick(recruitId)
            },
            onViewPostClick = { recruitId ->
                Log.d("MyRecruitsFragment", "공고글 보기 클릭: $recruitId")
                viewModel.onViewPostClick(recruitId)
            },
            onCardClick = { recruitId ->
                Log.d("MyRecruitsFragment", "지원한 공고 카드 클릭: $recruitId")
                // TODO: 상세 페이지로 이동
            }
        )
    }

    private fun setupRecyclerViews() {
        // 일정 RecyclerView
        binding.scheduleRecyclerView.apply {
            adapter = scheduleGroupAdapter
            layoutManager = LinearLayoutManager(requireContext())
            isNestedScrollingEnabled = false
        }

        // 내 공고 RecyclerView
        binding.myRecruitRecyclerView.apply {
            adapter = myRecruitAdapter
            layoutManager = LinearLayoutManager(requireContext())
            isNestedScrollingEnabled = false
        }

        // 지원한 공고 RecyclerView
        binding.appliedRecruitRecyclerView.apply {
            adapter = appliedRecruitAdapter
            layoutManager = LinearLayoutManager(requireContext())
            isNestedScrollingEnabled = false
        }
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // 일정 그룹 관찰
                launch {
                    viewModel.scheduleGroups.collect { scheduleGroups ->
                        scheduleGroupAdapter.submitList(scheduleGroups)
                    }
                }

                // 내 공고 관찰
                launch {
                    viewModel.myRecruits.collect { myRecruits ->
                        myRecruitAdapter.submitList(myRecruits)
                        // 개수 업데이트
                        binding.myRecruitCountTxt.text = myRecruits.size.toString()
                    }
                }

                // 지원한 공고 관찰
                launch {
                    viewModel.appliedRecruits.collect { appliedRecruits ->
                        appliedRecruitAdapter.submitList(appliedRecruits)
                        // 개수 업데이트
                        binding.appliedRecruitCountTxt.text = appliedRecruits.size.toString()
                    }
                }

                // 로딩 상태
                launch {
                    viewModel.isLoading.collect { isLoading ->
                        // TODO: 로딩 UI 표시/숨김
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}