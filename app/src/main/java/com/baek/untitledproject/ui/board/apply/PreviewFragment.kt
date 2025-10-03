package com.baek.untitledproject.ui.board.apply

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.baek.untitledproject.R
import com.baek.untitledproject.databinding.FragmentPreviewBinding
import com.baek.untitledproject.domain.data.ApplicationRequirements
import com.baek.untitledproject.domain.utils.Result
import com.baek.untitledproject.domain.utils.toKoreanAge
import com.baek.untitledproject.domain.utils.toKoreanGender
import com.baek.untitledproject.ui.MainActivity
import com.google.android.material.divider.MaterialDividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PreviewFragment : Fragment() {

    private var _binding: FragmentPreviewBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ApplicationViewModel by hiltNavGraphViewModels(R.id.submit_application_nav)

    private lateinit var previewAdapter: PreviewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPreviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeState()
        bindInfo()
        setAdapter()
        bindPreview()
        setBottomAction()
        observeSubmitState()
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.applicationRequirement.collect { state ->
                    when (state) {
                        is Result.Success -> {
                            setUiVisiblity(state.data)
                        }

                        else -> {} //None일 때는 아무 처리도 하지 않음
                    }
                }
            }
        }
    }

    private fun setUiVisiblity(data: ApplicationRequirements) = with(binding) {
        if (data.requiresGender) {
            genderTxt.visibility = View.VISIBLE
        }
        if (data.requiresAge) {
            ageTxt.visibility = View.VISIBLE
        }
        if (data.requiresGender && data.requiresAge) {
            comma.visibility = View.VISIBLE
        }

        if (data.requiresStudentId) {
            studentIdLayout.visibility = View.VISIBLE
        }
        if (data.requiresDepartment) {
            departmentLayout.visibility = View.VISIBLE
        }

        if(!data.requiresStudentId && !data.requiresDepartment){
            space.visibility = View.GONE
        }

    }

    private fun bindInfo() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.user.collect { state ->
                    when (state) {
                        is Result.Success -> {
                            val user = state.data
                            binding.name.text = user.name
                            binding.genderTxt.text = user.gender.toKoreanGender()
                            binding.ageTxt.text = user.birthYear.toKoreanAge()
                            binding.studentId.text = user.studentId
                            binding.department.text = user.department

                        }

                        else -> {}
                    }
                }
            }
        }
    }

    private fun setAdapter() {
        previewAdapter = PreviewAdapter()

        binding.previewRv.apply {
            adapter = this@PreviewFragment.previewAdapter
            if (itemDecorationCount == 0) {
                val divider = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
                ContextCompat.getDrawable(context, R.drawable.divider_line)
                    ?.let(divider::setDrawable)
                addItemDecoration(divider)
            }
        }
    }

    private fun bindPreview() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.answers.collect { answers ->
                    previewAdapter.submitList(answers)
                }
            }
        }
    }

    private fun setBottomAction() {
        binding.editBtn.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.submitBtn.setOnClickListener {
            viewModel.submitApplication()
        }
    }

    private fun observeSubmitState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.submitState.collect { state ->
                    when (state) {
                        is Result.Success -> {
                            val action =
                                PreviewFragmentDirections.actionPreviewFragmentToCompleteApplyFragment()

                            findNavController().navigate(action)

                        }

                        is Result.Loading -> {

                        }

                        is Result.Error -> {

                        }

                        else -> {}
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.setToolbar(detailVisible = true, title = "신청서 미리보기")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}