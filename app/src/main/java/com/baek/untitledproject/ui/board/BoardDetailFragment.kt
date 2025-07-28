package com.baek.untitledproject.ui.board

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.baek.untitledproject.databinding.FragmentBoardDetailBinding
import com.baek.untitledproject.domain.data.Board
import com.baek.untitledproject.domain.utils.Result
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BoardDetailFragment : Fragment() {

    private var _binding: FragmentBoardDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BoardDetailViewModel by viewModels()
    private val args: BoardDetailFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBoardDetailBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()
        observeBoard()
    }


    fun initData() {
        //boardFragment에서 받아온 id
        val id = args.id
        viewModel.loadBoardData(id)
    }

    private fun observeBoard() {

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.board.collect { state ->
                    when (state) {
                        is Result.Success -> {
                            bindBoardData(state.data)
                        }

                        is Result.Loading -> {
                            //TODO: loading ui 적용
                        }

                        is Result.Error -> {
                            //TODO: Error 처리
                        }
                    }
                }
            }
        }
    }

    private fun bindBoardData(board: Board) {
        binding.recruitDateRangeTxt.text =
            "${board.recruitStartDate}-${board.recruitEndDate}"
        binding.recruitStateTxt.text = board.recruitStatus
        binding.interviewFlagTxt.text = if (board.interviewFlag) "면접진행" else "면접없음"
        binding.groupNameTxt.text = board.category
        binding.imageSlider.submitList(board.imgUrl)
        binding.contentTxt.text = board.content
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}