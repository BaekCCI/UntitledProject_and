package com.baek.untitledproject.ui.board.report

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.baek.untitledproject.R
import com.baek.untitledproject.ReportNavGraphArgs
import com.baek.untitledproject.databinding.FragmentReportSelectBinding
import com.baek.untitledproject.domain.utils.ReportType
import com.baek.untitledproject.domain.utils.Result
import com.baek.untitledproject.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ReportSelectFragment : Fragment() {

    private var _binding: FragmentReportSelectBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ReportViewModel by hiltNavGraphViewModels(R.id.report_nav_graph)

    private val args: ReportNavGraphArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportSelectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.initInfo(args.reportTopic, args.targetId, args.reportedUserId)
        binding.cancelBtn.setOnClickListener {
            findNavController().popBackStack()
        }
        setupBtn()

        observeState()
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    when (state) {
                        is Result.Error -> {
                            //TODO: 에러 띄우기
                        }

                        else -> {} //None일 때는 아무 처리도 하지 않음
                    }
                }
            }
        }
    }

    private fun setupBtn() = with(binding) {
        insultBtn.setOnClickListener {
            viewModel.saveType(ReportType.INSULT)
            navigate()
        }
        hateBtn.setOnClickListener {
            viewModel.saveType(ReportType.HATE_SPEECH)
            navigate()
        }

        sexualBtn.setOnClickListener {
            viewModel.saveType(ReportType.SEXUAL)
            navigate()
        }
        gamblingBtn.setOnClickListener {
            viewModel.saveType(ReportType.GAMBLING)
            navigate()
        }
        spamBtn.setOnClickListener {
            viewModel.saveType(ReportType.SPAM)
            navigate()
        }
        personalBtn.setOnClickListener {
            viewModel.saveType(ReportType.PERSONAL)
            navigate()
        }

        impersonationBtn.setOnClickListener {
            viewModel.saveType(ReportType.IMPERSONATION)
            navigate()
        }
        otherBtn.setOnClickListener {
            viewModel.saveType(ReportType.OTHER)
            navigate()
        }
    }

    private fun navigate() {
        val action =
            ReportSelectFragmentDirections.actionReportSelectFragmentToReportWriteFragment()
        findNavController().navigate(action)
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