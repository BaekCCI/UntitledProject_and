package com.baek.untitledproject.ui.board

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.baek.untitledproject.databinding.FragmentSearchBinding
import com.baek.untitledproject.domain.utils.Result
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModels()

    private lateinit var adapter: SearchRVAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
        initSearchBar()
        handleSearchAction()
        observeSearchResult()
    }

    private fun initRecyclerView() {
        adapter = SearchRVAdapter { item ->
            // 클릭 시 상세 화면 이동
            val action = SearchFragmentDirections.actionSearchFragmentToBoardDetailFragment(item.postId)
            findNavController().navigate(action)
        }

        binding.searchResultRecyclerView.adapter = adapter
        binding.searchResultRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun initSearchBar() {

        (requireActivity() as AppCompatActivity).supportActionBar?.hide()

        binding.searchInput.requestFocus()

        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.searchInput, InputMethodManager.SHOW_IMPLICIT)

        binding.cancelBtn.setOnClickListener {
            findNavController().popBackStack()
        }

    }

    private fun handleSearchAction() {
        binding.searchInput.setOnEditorActionListener { _, i, _ ->
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                val input = binding.searchInput.text.toString()

                viewModel.searchBoard(input)

                Log.d("SearchFragment", "Search: $input")
                val imm =
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.searchInput.windowToken, 0)

                binding.searchInput.clearFocus()
                true
            } else {
                false
            }
        }
    }

    //검색결과 observe
    private fun observeSearchResult() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.searchedBoards.collect { state ->
                    when (state) {
                        is Result.Success -> {
                            val boardList = state.data
                            adapter.submitList(boardList)

                            if (boardList.isEmpty()) {
                                binding.emptyResultText.visibility = View.VISIBLE
                                binding.searchResultRecyclerView.visibility = View.GONE
                            } else {
                                binding.emptyResultText.visibility = View.GONE
                                binding.searchResultRecyclerView.visibility = View.VISIBLE
                            }

                        }

                        is Result.Loading -> {
                            //TODO: loading ui 적용
                        }

                        is Result.Error -> {
                            //TODO: Error 처리
                        }

                        else -> {}//None일 때는 아무 처리도 하지 않음
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}