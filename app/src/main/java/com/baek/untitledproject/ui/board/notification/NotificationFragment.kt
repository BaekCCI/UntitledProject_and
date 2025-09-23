package com.baek.untitledproject.ui.board.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.baek.untitledproject.R
import com.baek.untitledproject.databinding.FragmentNotificationBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import com.baek.untitledproject.domain.utils.Result
import com.baek.untitledproject.ui.MainActivity

@AndroidEntryPoint
class NotificationFragment : Fragment() {
    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NotificationViewModel by viewModels()

    private val adapter = NotificationAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadNotifications()
        observeNotification()
        initAdapter()
    }

    private fun loadNotifications() {
        viewModel.load()
    }

    private fun observeNotification() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.notifications.collect { result ->
                    when (result) {
                        is Result.Loading -> {
                            binding.emptyState.isVisible = false
                            binding.notificationList.isVisible = false
                            //TODO: 로딩 UI 적용
                        }

                        is Result.Success -> {
                            val list = result.data
                            binding.emptyState.isVisible = list.isEmpty()
                            binding.notificationList.isVisible = list.isNotEmpty()

                            adapter.nowMillis = System.currentTimeMillis()
                            adapter.submitList(list)
                        }

                        is Result.Error -> {
                            binding.emptyState.isVisible = true
                            binding.notificationList.isVisible = false
                            //TODO: ERROR 처리
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    private fun initAdapter() {
        binding.notificationList.adapter = adapter
        binding.notificationList.layoutManager = LinearLayoutManager(requireContext())
        if (binding.notificationList.itemDecorationCount == 0) {
            val dec = com.google.android.material.divider.MaterialDividerItemDecoration(
                requireContext(),
                LinearLayoutManager.VERTICAL
            ).apply {
                dividerThickness =
                    maxOf(1, (0.5f * resources.displayMetrics.density).roundToInt()) // 0.5dp
                setDividerColor(ContextCompat.getColor(requireContext(), R.color.gray_300))
                isLastItemDecorated = false
            }
            binding.notificationList.addItemDecoration(dec)
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.setToolbar(detailVisible = true, title = "알림")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}