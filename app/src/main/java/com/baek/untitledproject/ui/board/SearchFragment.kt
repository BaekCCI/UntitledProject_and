package com.baek.untitledproject.ui.board

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.addCallback
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.baek.untitledproject.databinding.FragmentSearchBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BoardViewModel by activityViewModels()

    private lateinit var adapter: SearchRVAdapter
    private var searchJob: Job? = null

    private var isSearching: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        setupBackBtn()
        observeFilteredList()
        setupInput()
        restoreUi()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            popWithReset()
        }
    }


    private fun initAdapter() {
        adapter = SearchRVAdapter { post ->
            isSearching = false
            adapter.setSearchingMode(false)
            val action =
                SearchFragmentDirections.actionSearchFragmentToBoardDetailFragment(post.postId)
            findNavController().navigate(action)
            hideIme()
        }
        binding.searchRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = this@SearchFragment.adapter
        }
        binding.searchInput.requestFocus()
        showIme()
    }

    //필터링된 게시글 리스트를 adapter에 넘김
    private fun observeFilteredList() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                kotlinx.coroutines.flow.combine(
                    viewModel.filteredList,
                    viewModel.query
                ) { items, q -> items to q }
                    .collect { (items, q) ->
                        adapter.updateHighlightQuery(q)
                        adapter.setSearchingMode(isSearching)
                        adapter.submitList(items)
                    }
            }
        }
    }

    private fun setupInput() {
        binding.searchInput.doAfterTextChanged { s ->
            searchJob?.cancel()
            searchJob = viewLifecycleOwner.lifecycleScope.launch {
                viewModel.updateQuery(s?.toString().orEmpty())
                if (!isSearching) {
                    isSearching = true
                    adapter.setSearchingMode(true)
                }
            }
        }
        binding.searchInput.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                // 완료 → 결과 레이아웃 전환 (데이터는 그대로)
                isSearching = false
                adapter.setSearchingMode(false)
                hideIme()
                true
            } else false
        }
    }

    private fun setupBackBtn() {
        binding.backBtn.setOnClickListener {
            popWithReset()
        }
    }

    private fun restoreUi() {

        val text = viewModel.query.value
        binding.searchInput.setText(text)
        binding.searchInput.setSelection(text.length)
        adapter.setSearchingMode(isSearching)

        binding.searchInput.requestFocus()
        showIme()

    }

    private fun popWithReset() {
        hideIme()
        viewModel.updateQuery("")
        isSearching = true
        adapter.setSearchingMode(true)
        findNavController().popBackStack()
    }

    private fun showIme() {
        val imm = requireContext().getSystemService(InputMethodManager::class.java)
        imm?.showSoftInput(binding.searchInput, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun hideIme() {
        val imm = requireContext().getSystemService(InputMethodManager::class.java)
        imm?.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}