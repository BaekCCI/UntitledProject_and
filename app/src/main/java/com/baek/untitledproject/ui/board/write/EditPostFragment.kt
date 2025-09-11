package com.baek.untitledproject.ui.board.write

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navOptions
import com.baek.untitledproject.R
import com.baek.untitledproject.domain.utils.Result
import com.baek.untitledproject.ui.board.BoardDetailFragmentArgs
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditPostFragment : InfoWriteBaseFragment() {

    private val editPostViewModel: EditPostViewModel by viewModels()
    override val vm: BaseWriteViewModel get() = editPostViewModel

    private val args: EditPostFragmentArgs by navArgs()

    override val toolbarTitle: String = "수정하기"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()
        observePostState()
        setContentVisibility()
        observeSubmitState()
    }

    private fun initData() {
        val id = args.postId
        editPostViewModel.init(id)
        binding.nextBtn.text = "수정 저장하기"
    }

    private fun observePostState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                editPostViewModel.postState.collect { state ->
                    when (state) {
                        is Result.Success -> {
                            setContentVisibility(state.data.hasInterview ?: false)
                        }

                        is Result.Loading -> {}
                        is Result.Error -> {}
                        else -> {}
                    }
                }
            }
        }
    }

    private fun setContentVisibility(hasInterview: Boolean = false) {
        binding.pageTitle.visibility = View.GONE
        binding.pageIndicator.visibility = View.GONE
        binding.interviewLocLayout.isVisible = hasInterview
    }

    private fun observeSubmitState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                editPostViewModel.submitState.collect { state ->
                    when (state) {
                        is Result.Success -> {
                            Toast.makeText(requireContext(), "공고가 수정 되었어요", Toast.LENGTH_SHORT)
                                .show()
                            onExitConfirmed()
                        }

                        is Result.Loading -> {}
                        is Result.Error -> {}
                        else -> {}
                    }
                }
            }
        }
    }

    override fun setupNextBtn() {
        binding.nextBtn.setOnClickListener {
            editPostViewModel.submit(
                binding.titleInput.text.toString(),
                binding.groupNameInput.text.toString(),
                binding.contentInput.text.toString(),
                binding.interviewLocInput.text.toString()
            )
        }
    }
}