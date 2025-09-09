package com.baek.untitledproject.ui.board.apply

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.navOptions
import com.baek.untitledproject.R
import com.baek.untitledproject.databinding.FragmentCompleteApplyBinding
import com.baek.untitledproject.ui.MainActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CompleteApplyFragment : Fragment() {

    private var _binding: FragmentCompleteApplyBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCompleteApplyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rootNavController = requireActivity().findNavController(R.id.nav_host_fragment)

        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav)

        val clearGraph = navOptions {
            popUpTo(R.id.nav_graph) { inclusive = true }
            launchSingleTop = true
        }
        binding.toHomeBtn.setOnClickListener {
            rootNavController.navigate(R.id.boardFragment, null, clearGraph)
            bottomNav?.selectedItemId = R.id.boardFragment
        }
        binding.toMyApplicationBtn.setOnClickListener {
            rootNavController.navigate(R.id.myRecruitsFragment, null, clearGraph)
            bottomNav?.selectedItemId = R.id.myRecruitsFragment
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.setToolbar(detailVisible = true, title = "신청서 작성")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}