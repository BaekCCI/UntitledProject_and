package com.baek.untitledproject.ui.board.write

import android.os.Bundle
import android.view.View
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.baek.untitledproject.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InfoWriteFragment : InfoWriteBaseFragment() {

    private val boardViewModel: BoardWriteViewModel by hiltNavGraphViewModels(R.id.write_board_nav_graph)
    override val vm: BaseWriteViewModel get() = boardViewModel

    override val toolbarTitle: String = "모임 올리기"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setContentVisibility()

    }

    override fun onNext() {
        val action = InfoWriteFragmentDirections
            .actionInfoWriteFragmentToInterviewSettingFragment()
        findNavController().navigate(action)
    }

     private fun setContentVisibility() {
        binding.pageTitle.visibility = View.VISIBLE
        binding.pageIndicator.visibility = View.VISIBLE
        binding.interviewLocLayout.visibility = View.GONE
    }

    override fun setupNextBtn(){
        binding.nextBtn.setOnClickListener {
            vm.updateInfo(
                binding.titleInput.text.toString(),
                binding.groupNameInput.text.toString(),
                binding.contentInput.text.toString()
            )
            onNext()
        }
    }

    override fun onExitConfirmed() {
        findNavController().popBackStack()
    }
}