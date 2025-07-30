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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.baek.untitledproject.R
import com.baek.untitledproject.databinding.FragmentSearchBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

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

    }

    private fun initRecyclerView() {
        adapter = SearchRVAdapter { item ->
            // 클릭 시 상세 화면 이동
            val action = SearchFragmentDirections.actionSearchFragmentToBoardDetailFragment(item.id)
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

                //TODO: 검색 기능
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}