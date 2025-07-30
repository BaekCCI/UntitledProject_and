package com.baek.untitledproject.ui.board

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.baek.untitledproject.R
import com.baek.untitledproject.databinding.FragmentBoardBinding
import com.baek.untitledproject.domain.utils.Result
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BoardFragment : Fragment() {

    private var _binding: FragmentBoardBinding? = null
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

        _binding = FragmentBoardBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
        observeBoardList()
        setToolbar()
        viewModel.loadBoardList()
    }

    private fun initAdapter() {
        //adapter 초기화 및 게시물 클릭 시 이벤트
        boardRVAdapter = BoardRVAdapter { board ->
            //클릭 시 상세 페이지 이동
            Log.d("BoardFragment", "${board.title} clicked!")
            val action = BoardFragmentDirections.actionBoardFragmentToBoardDetailFragment(board.id)
            findNavController().navigate(action)
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
                viewModel.boardList.collect { state ->
                    when (state) {
                        is Result.Success -> {
                            boardRVAdapter.submitList(state.data)
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

    //toolbar 설정
    private fun setToolbar() {
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "BoardFragment"

        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.menu_board, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_alert -> {
                        // TODO: 클릭 시 알림
                        Log.d("BoardFragment", "알림 버튼 클릭!")
                        true
                    }

                    R.id.action_search -> {
                        // TODO: 클릭 시 검색
                        Log.d("BoardFragment", "검색 버튼 클릭!")
                        true
                    }

                    else -> false
                }
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}