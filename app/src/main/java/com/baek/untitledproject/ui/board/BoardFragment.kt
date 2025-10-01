package com.baek.untitledproject.ui.board

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.baek.untitledproject.R
import com.baek.untitledproject.databinding.FragmentBoardBinding
import com.baek.untitledproject.domain.utils.Result
import com.baek.untitledproject.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BoardFragment : Fragment() {

    private var _binding: FragmentBoardBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BoardViewModel by activityViewModels()

    private lateinit var boardAdapter: BoardRVAdapter

    private var lastBackPressed = 0L

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

        setupSwipeRefresh()
        initAdapter()
        observeBoardList()
        setToolbar()
        setupWriteBoardBtn()
        setupDoubleBackToExit()
    }

    private fun setupDoubleBackToExit(){
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val now = System.currentTimeMillis()
                    if (now - lastBackPressed <= 2000L) {
                        requireActivity().finish()
                    } else {
                        lastBackPressed = now
                    }
                }
            }
        )
    }

    //위로 당겨서 새로고침
    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setColorSchemeResources(
            R.color.point_skyblue, R.color.gray_100, R.color.gray_black
        )

        // 당겨서 새로고침
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadBoardList()
        }
    }

    private fun initAdapter() {
        //adapter 초기화 및 게시물 클릭 시 이벤트
        boardAdapter = BoardRVAdapter { board ->
            //클릭 시 상세 페이지 이동
            val action =
                BoardFragmentDirections.actionBoardFragmentToBoardDetailFragment(board.postId)
            findNavController().navigate(action)
        }
        binding.boardRv.apply {
            adapter = boardAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    //게시글 로드 상태 관찰
    private fun observeBoardList() {
        //boardList 수집하여 adapter에 넘김
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.boardList.collect { state ->
                    when (state) {
                        is Result.Success -> {
                            boardAdapter.submitList(state.data)
                            binding.swipeRefresh.isRefreshing = false
                        }

                        is Result.Loading -> {
                            //TODO: loading ui 적용
                            binding.swipeRefresh.isRefreshing = true
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

    //toolbar 설정
    private fun setToolbar() {
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.menu_board, menu)

                val alertItem = menu.findItem(R.id.action_alert)
                val hasNew = viewModel.hasNewNoti.value
                alertItem.setIcon(
                    if (hasNew) R.drawable.ic_alarm_on
                    else R.drawable.ic_alarm_off
                )
            }

            override fun onPrepareMenu(menu: Menu) {
                val alertItem = menu.findItem(R.id.action_alert)
                val hasNew = viewModel.hasNewNoti.value
                alertItem.setIcon(
                    if (hasNew) R.drawable.ic_alarm_on
                    else R.drawable.ic_alarm_off
                )
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_alert -> {
                        val action =
                            BoardFragmentDirections.actionBoardFragmentToNotificationFragment()
                        findNavController().navigate(action)
                        true
                    }

                    R.id.action_search -> {
                        val action =
                            BoardFragmentDirections.actionBoardFragmentToSearchFragment()
                        findNavController().navigate(action)
                        true
                    }

                    R.id.action_setting -> {
                        val action = BoardFragmentDirections.actionBoardFragmentToSettingFragment()
                        findNavController().navigate(action)
                        true
                    }

                    else -> false
                }
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    //공고 작성 버튼
    private fun setupWriteBoardBtn() {
        binding.writeBoardBtn.setOnClickListener {
            val action = BoardFragmentDirections.actionBoardFragmentToWriteBoardNavGraph()
            findNavController().navigate(action)
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.setToolbar(rootVisible = true, title = "전북대학교 구인공고")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}