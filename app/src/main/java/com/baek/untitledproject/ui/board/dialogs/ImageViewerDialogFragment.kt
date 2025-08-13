package com.baek.untitledproject.ui.board.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.baek.untitledproject.R
import com.baek.untitledproject.databinding.FragmentImageViewerDialogBinding

class ImageViewerDialogFragment : DialogFragment() {

    private var _binding: FragmentImageViewerDialogBinding? = null
    private val binding get() = _binding!!

    private val args: ImageViewerDialogFragmentArgs by navArgs()
    private var startIdx: Int = 0
    private var uris = emptyList<android.net.Uri>()

    private var pageCallback: ViewPager2.OnPageChangeCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.ThemeOverlay_Untitled_FullscreenDialog)
        startIdx = args.startIndex
        uris = args.imageUris.toList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImageViewerDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewPager()
        updateIndicator(startIdx)
        registerPagerCallback()
        setupCloseBtn()
    }

    //Viewpager 설정
    private fun setupViewPager() {
        val pager = binding.imgPager

        val adapter = ImageViewerAdapter(
            uris = uris,
            onZoomChanged = { zoomed ->
                //뷰 파괴 직전 늦게 오는 콜백에 대한 에러 방지
                if (_binding != null) pager.isUserInputEnabled = !zoomed
            }
        )
        pager.adapter = adapter
        pager.setCurrentItem(startIdx, false)
    }

    private fun updateIndicator(pos: Int) {

        binding.curPg.text = "${pos + 1}"
        binding.totalPg.text = uris.size.toString()
    }

    //페이지 변경 콜백
    private fun registerPagerCallback() {
        pageCallback?.let { binding.imgPager.unregisterOnPageChangeCallback(it) }
        pageCallback = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) = updateIndicator(position)
        }.also { binding.imgPager.registerOnPageChangeCallback(it) }
    }

    private fun setupCloseBtn() {
        binding.closeBtn.setOnClickListener {
            //callback 해제 후 동작
            pageCallback?.let { cb -> binding.imgPager.unregisterOnPageChangeCallback(cb) }
            pageCallback = null
            binding.imgPager.adapter = null
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        //FullScreen 적용
        dialog?.window?.apply {
            setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }

    override fun onDestroyView() {
        binding.imgPager.apply {
            pageCallback?.let { unregisterOnPageChangeCallback(it) }
            adapter = null
        }
        pageCallback = null
        _binding = null
        super.onDestroyView()
    }

}