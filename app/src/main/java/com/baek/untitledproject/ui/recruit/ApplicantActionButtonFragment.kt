package com.baek.untitledproject.ui.recruit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.baek.untitledproject.databinding.FragmentApplicantActionButtonBinding

class ApplicantActionButtonFragment : Fragment() {

    private var _binding: FragmentApplicantActionButtonBinding? = null
    private val binding get() = _binding!!

    private var buttons: List<ApplicantActionButton> = emptyList()
    private var onActionClick: ((ApplicantActionButton) -> Unit)? = null

    companion object {
        fun newInstance(
            buttons: List<ApplicantActionButton>,
            onActionClick: (ApplicantActionButton) -> Unit
        ): ApplicantActionButtonFragment {
            return ApplicantActionButtonFragment().apply {
                this.buttons = buttons
                this.onActionClick = onActionClick
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentApplicantActionButtonBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupButtons()
    }

    private fun setupButtons() {
        when (buttons.size) {
            1 -> {
                // 단일 버튼
                binding.singleButton.apply {
                    visibility = View.VISIBLE
                    val button = buttons[0]
                    text = button.text

                    if (button.isDangerous) {
                        backgroundTintList = requireContext().getColorStateList(android.R.color.holo_red_light)
                        setTextColor(requireContext().getColor(android.R.color.white))
                    } else {
                        backgroundTintList = requireContext().getColorStateList(android.R.color.black)
                        setTextColor(requireContext().getColor(android.R.color.white))
                    }

                    setOnClickListener { onActionClick?.invoke(button) }
                }

                binding.buttonContainer.visibility = View.GONE
            }

            2 -> {
                // 좌우 버튼
                binding.buttonContainer.visibility = View.VISIBLE
                binding.singleButton.visibility = View.GONE

                val leftButton = buttons[0]
                val rightButton = buttons[1]

                binding.leftButton.apply {
                    text = leftButton.text

                    if (leftButton.isDangerous) {
                        backgroundTintList = requireContext().getColorStateList(android.R.color.darker_gray)
                        setTextColor(requireContext().getColor(android.R.color.black))
                    } else {
                        backgroundTintList = null
                        setTextColor(requireContext().getColor(android.R.color.black))
                    }

                    setOnClickListener { onActionClick?.invoke(leftButton) }
                }

                binding.rightButton.apply {
                    text = rightButton.text
                    backgroundTintList = requireContext().getColorStateList(android.R.color.black)
                    setTextColor(requireContext().getColor(android.R.color.white))
                    setOnClickListener { onActionClick?.invoke(rightButton) }
                }
            }

            else -> {
                // 버튼 없음
                binding.root.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}