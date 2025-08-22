package com.baek.untitledproject.ui.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.baek.untitledproject.R
import com.baek.untitledproject.databinding.FragmentJoinInfoBinding
import com.baek.untitledproject.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class JoinInfoFragment : Fragment() {

    private var _binding: FragmentJoinInfoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJoinInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.setToolbar(detailVisible = true, title = "회원가입")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}