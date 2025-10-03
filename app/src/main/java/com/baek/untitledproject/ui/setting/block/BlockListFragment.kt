package com.baek.untitledproject.ui.setting.block

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.baek.untitledproject.R
import com.baek.untitledproject.databinding.FragmentBlockListBinding
import com.baek.untitledproject.domain.utils.Result
import com.baek.untitledproject.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BlockListFragment : Fragment() {

    private var _binding: FragmentBlockListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BlockListViewModel by viewModels()

    private lateinit var adapter: BlockRvAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBlockListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSwipeRefresh()
        setupAdapter()
        observeBlockList()
        observeUnBlockState()
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setColorSchemeColors(
            requireContext().getColor(R.color.point_skyblue),
            requireContext().getColor(R.color.gray_100),
            requireContext().getColor(R.color.gray_black)
        )


        // 당겨서 새로고침
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.getBlockList()
        }
    }

    private fun observeBlockList() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.blockList.collect { state ->
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
                            setupUiState(false)
                            binding.swipeRefresh.isRefreshing = false
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    private fun observeUnBlockState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.unblockEvents.collect { state ->
                    when (state) {
                        is Result.Success -> {
                            viewModel.getBlockList()
                        }

                        is Result.Error -> {
                            //TODO: 실패 알림
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    private fun setupAdapter() {
        adapter = BlockRvAdapter { blockId ->
            viewModel.unBlockUser(blockId)
        }
        binding.blockRV.apply {
            adapter = this@BlockListFragment.adapter
            if (itemDecorationCount == 0) {
                val divider = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
                ContextCompat.getDrawable(context, R.drawable.divider_line)
                    ?.let(divider::setDrawable)
                addItemDecoration(divider)
            }
        }

    }

    private fun setupUiState(hasReport: Boolean) {
        binding.blockRV.visibility = if (hasReport) View.VISIBLE else View.GONE
        binding.emptyTxt.visibility = if (hasReport) View.GONE else View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.setToolbar(xToolbarVisible = true, title = "차단 내역")
    }
}