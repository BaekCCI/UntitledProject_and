package com.baek.untitledproject.ui.board.report

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.baek.untitledproject.R
import com.baek.untitledproject.databinding.FragmentReportWriteBinding
import com.baek.untitledproject.domain.utils.Result
import com.baek.untitledproject.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ReportWriteFragment : Fragment() {

    private var _binding: FragmentReportWriteBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ReportViewModel by hiltNavGraphViewModels(R.id.report_nav_graph)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportWriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cancelBtn.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.reportBtn.setOnClickListener {
            val content = binding.contentInput.text.toString()
            viewModel.sendReport(content)
        }

        observeReportState()

    }

    private fun observeReportState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.reportState.collect { state ->
                    when (state) {
                        is Result.Success -> {
                            findNavController().navigate(
                                ReportWriteFragmentDirections
                                    .actionReportWriteFragmentToReportCompleteFragment(),
                                navOptions {
                                    popUpTo(R.id.report_nav_graph) { inclusive = true }
                                    launchSingleTop = true
                                }
                            )
                        }

                        is Result.Loading -> {
                            //TODO: loading ui 적용
                        }

                        is Result.Error -> {
                            //TODO: Error 처리
                        }

                        else -> {} //None일 때는 아무 처리도 하지 않음
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.setToolbar()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}