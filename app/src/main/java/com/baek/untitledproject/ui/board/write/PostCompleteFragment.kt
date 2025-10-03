package com.baek.untitledproject.ui.board.write

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.baek.untitledproject.R
import com.baek.untitledproject.databinding.FragmentPostCompleteBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class PostCompleteFragment : Fragment() {

    private var _binding: FragmentPostCompleteBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostCompleteBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rootNavController = requireActivity().findNavController(R.id.nav_host_fragment)

        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav)

        val clearGraph = navOptions {
            popUpTo(R.id.nav_graph) { inclusive = true }
            launchSingleTop = true
            anim { enter = 0; exit = 0; popEnter = 0; popExit = 0 }
        }
        binding.toHomeBtn.setOnClickListener {
            rootNavController.navigate(R.id.boardFragment, null, clearGraph)
            bottomNav?.selectedItemId = R.id.boardFragment
        }
        binding.toMyPostBtn.setOnClickListener {
            rootNavController.navigate(R.id.boardFragment, null, clearGraph)
            rootNavController.navigate(R.id.myRecruitsFragment)
            bottomNav?.selectedItemId = R.id.myRecruitsFragment
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}