package com.baek.untitledproject.ui.login

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.os.bundleOf
import com.baek.untitledproject.databinding.DialogTermsSheetBinding
import com.google.android.material.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class TermSheet : BottomSheetDialogFragment() {

    companion object{
        private const val ARG_TITLE = "arg_title"
        private const val ARG_CONTENT = "arg_content"

        fun newInstance(title:String, content:String):TermSheet{
            return TermSheet().apply {
                arguments = bundleOf(
                    ARG_TITLE to title,
                    ARG_CONTENT to content
                )
            }
        }
    }

    //확인 클릭 콜백
    var onConfirm : (()->Unit)?=null

    private var _binding : DialogTermsSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(
            requireContext(),
            com.baek.untitledproject.R.style.ThemeOverlay_Untitled_BottomSheet
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogTermsSheetBinding.inflate(inflater,container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val title = requireArguments().getString(ARG_TITLE).orEmpty()
        val content = requireArguments().getString(ARG_CONTENT).orEmpty()

        binding.titleTxt.text = title
        binding.contentTxt.text = content

        binding.closeBtn.setOnClickListener { dismiss() }
        binding.confirmBtn.setOnClickListener {
            onConfirm?.invoke()
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog as? BottomSheetDialog ?:return

        dialog.behavior.apply {
            state = BottomSheetBehavior.STATE_EXPANDED
            skipCollapsed = true
            isFitToContents = true
            isDraggable = true
        }

        val bottomSheet = dialog.findViewById<FrameLayout>(R.id.design_bottom_sheet)
        bottomSheet?.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
        bottomSheet?.requestLayout()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}