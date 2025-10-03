package com.baek.untitledproject.ui.setting.dialog

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.os.bundleOf
import com.baek.untitledproject.R
import com.baek.untitledproject.databinding.FragmentTermSheetDialogBinding
import com.baek.untitledproject.domain.utils.TermsType
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class TermSheetDialogFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentTermSheetDialogBinding? = null
    private val binding get() = _binding!!


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(
            requireContext(),
            R.style.ThemeOverlay_Untitled_BottomSheet
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTermSheetDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val typeName = requireArguments().getString(ARG_TYPE) ?: TermsType.SERVICE.name
        val type = TermsType.valueOf(typeName)
        binding.titleTxt.text = type.title

        val content = requireContext().resources.openRawResource(type.resId)
            .bufferedReader()
            .use { it.readText() }
        Log.d("TermSheetDialogFragment","${type.title}-${type.resId}:\n$content")
        binding.contentTxt.text = content
        binding.closeBtn.setOnClickListener { dismiss() }
    }

    companion object {
        private const val ARG_TYPE = "arg_type"

        fun newInstance(type: TermsType): TermSheetDialogFragment {
            return TermSheetDialogFragment().apply {
                arguments = bundleOf(ARG_TYPE to type.name)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog as? BottomSheetDialog ?: return

        dialog.behavior.apply {
            state = BottomSheetBehavior.STATE_EXPANDED
            skipCollapsed = true
            isFitToContents = true
            isDraggable = false
        }

        val bottomSheet =
            dialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
        bottomSheet?.requestLayout()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}