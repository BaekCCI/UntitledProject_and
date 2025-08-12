package com.baek.untitledproject.ui.board.write

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.baek.untitledproject.R
import com.baek.untitledproject.databinding.FragmentPostCompleteBinding

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

        binding.toHomeBtn.setOnClickListener {
            findNavController().navigate(
                R.id.boardFragment,
                null,
                navOptions {
                    popUpTo(R.id.write_board_nav_graph) { inclusive = true }
                    launchSingleTop = true
                }
            )
        }
        binding.toMyPostBtn.setOnClickListener {
            findNavController().navigate(
                R.id.myRecruitsFragment,
                null,
                navOptions {
                    popUpTo(R.id.boardFragment) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}