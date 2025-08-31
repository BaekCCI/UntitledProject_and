package com.baek.untitledproject.ui.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.baek.untitledproject.R
import com.baek.untitledproject.databinding.FragmentStartLoginBinding
import com.baek.untitledproject.ui.MainActivity


class StartLoginFragment : Fragment() {

    private var _binding: FragmentStartLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStartLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginBtn.setOnClickListener {
            val action =
                StartLoginFragmentDirections.actionStartLoginFragmentToLoginFragment()
            findNavController().navigate(action)
        }

        binding.joinBtn.setOnClickListener {
            val action =
                StartLoginFragmentDirections.actionStartLoginFragmentToJoinFragment()
            findNavController().navigate(action)
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.setToolbar(detailVisible = true, title = "시작하기")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}