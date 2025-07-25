package com.baek.untitledproject.ui.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.baek.untitledproject.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BoardViewModel by viewModels()

    private lateinit var boardRVAdapter: BoardRVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
        observeBoardList()
        viewModel.loadBoardList()
    }

    private fun initAdapter() {
        //adapter 초기화 및 게시물 클릭 시 이벤트
        boardRVAdapter = BoardRVAdapter { board ->
            // TODO: 클릭 시 상세 페이지 이동 처리
            Log.d("HomeFragment", "${board.title} clicked!")
        }

        //recyclerView 설정
        binding.recyclerView.apply {
            adapter = boardRVAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observeBoardList() {
        //boardList 수집하여 adapter에 넘김
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.boardList.collect { list ->
                    boardRVAdapter.submitList(list)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}