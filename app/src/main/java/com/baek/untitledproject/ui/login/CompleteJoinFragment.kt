package com.baek.untitledproject.ui.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.baek.untitledproject.R
import com.baek.untitledproject.databinding.FragmentCompleteJoinBinding
import com.baek.untitledproject.domain.utils.Result
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CompleteJoinFragment : Fragment() {

    private var _binding: FragmentCompleteJoinBinding? = null
    private val binding get() = _binding!!

    private val loginViewModel: LoginViewModel by hiltNavGraphViewModels(R.id.login_nav_graph)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCompleteJoinBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindNameTxt()
        setBtn()
    }

    private fun bindNameTxt(){
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                loginViewModel.joinState.collect { state ->
                    when (state) {
                        is Result.Success -> {
                            binding.welcomeTxt.text = "${state.data.name}님 환영해요!"
                        }

                        else -> {
                        }
                    }
                }
            }
        }
    }
    private fun setBtn(){
        val navController = findNavController()

        val navOptions = navOptions {
            popUpTo(R.id.login_nav_graph) { inclusive = true }
            launchSingleTop = true
        }
        binding.closeBtn.setOnClickListener {
            navController.navigate(R.id.settingFragment, null, navOptions)
        }
        binding.completeBtn.setOnClickListener {
            navController.navigate(R.id.settingFragment, null, navOptions)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}