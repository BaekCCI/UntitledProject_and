package com.baek.untitledproject.ui.setting.report

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.baek.untitledproject.R
import com.baek.untitledproject.databinding.FragmentReportListBinding
import com.baek.untitledproject.domain.utils.Result
import com.baek.untitledproject.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ReportListFragment : Fragment() {

    private var _binding: FragmentReportListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ReportListViewModel by viewModels()

    private lateinit var adapter: ReportRvAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSwipeRefresh()
        setupAdapter()
        observeReportsList()
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setColorSchemeResources(
            R.color.point_skyblue, R.color.gray_100, R.color.gray_black
        )

        // 당겨서 새로고침
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.getReports()
        }
    }

    private fun observeReportsList() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.reportList.collect { state ->
                    when (state) {
                        is Result.Success -> {

                            val list = state.data
                            setupUiState(list.isNotEmpty())
                            adapter.submitList(state.data)
                            binding.swipeRefresh.isRefreshing = false
                        }

                        is Result.Loading -> {
                            binding.swipeRefresh.isRefreshing = true
                        }

                        is Result.Error -> {
                            binding.swipeRefresh.isRefreshing = false
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    private fun setupUiState(hasReport: Boolean) {
        binding.reportRV.visibility = if (hasReport) View.VISIBLE else View.GONE
        binding.emptyTxt.visibility = if (hasReport) View.GONE else View.VISIBLE
    }

    private fun setupAdapter() {
        adapter = ReportRvAdapter()
        binding.reportRV.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.setToolbar(xToolbarVisible = true, title = "신고 내역")
    }
}