package com.baek.untitledproject.ui.board

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsCompat.Type
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.baek.untitledproject.R
import com.baek.untitledproject.databinding.FragmentBoardDetailBinding
import com.baek.untitledproject.domain.data.Board
import com.baek.untitledproject.domain.data.Post
import com.baek.untitledproject.domain.utils.DateUiStyle
import com.baek.untitledproject.domain.utils.Result
import com.baek.untitledproject.domain.utils.toDateRange
import com.baek.untitledproject.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Locale

@AndroidEntryPoint
class BoardDetailFragment : Fragment() {

    private var _binding: FragmentBoardDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BoardDetailViewModel by viewModels()
    private val args: BoardDetailFragmentArgs by navArgs()

    private lateinit var sliderAdapter: ImageSliderAdapter
    private var pageCallback: ViewPager2.OnPageChangeCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBoardDetailBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()
        observeBoard()
        setupMoreBtn()

        //toolbar 적용
        val navController = findNavController()

        binding.detailToolbar.setNavigationOnClickListener {
            navController.popBackStack()
        }
    }


    private fun initData() {
        //boardFragment에서 받아온 id
        val id = args.id
        viewModel.loadBoardData(id)
    }

    private fun observeBoard() {

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.board.collect { state ->
                    when (state) {
                        is Result.Success -> {
                            bindBoardData(state.data)
                        }

                        is Result.Loading -> {
                            //TODO: loading ui 적용
                        }

                        is Result.Error -> {
                            //TODO: Error 처리
                        }

                        else -> {} //None일 때는 아무 처리도 하지 않음
                    }
                }
            }
        }
    }

    private fun bindBoardData(post: Post) = with(binding) {

        organizationTxt.text = post.organization
        titleTxt.text = post.title

        if (post.hasInterview == true) {
            interviewChip.visibility = View.VISIBLE
            if (post.interviewStart != null && post.interviewEnd != null) {
                interviewScheduleTxt.text =
                    toDateRange(post.interviewStart, post.interviewEnd, DateUiStyle.MD_KR)
            } else {
                interviewScheduleTxt.text = "추후 공지 예정"
            }
        } else {
            interviewScheduleTxt.text = "면접 없음"
        }

        if (post.recruitmentStart != null && post.recruitmentEnd != null) {
            recruitDateTxt.text =
                toDateRange(post.recruitmentStart, post.recruitmentEnd, DateUiStyle.MD_WITH_WEEKDAY)
            if (LocalDate.now() in post.recruitmentStart..post.recruitmentEnd) {
                recruitOpenChip.visibility = View.VISIBLE
                recruitingBottomBar.visibility = View.VISIBLE
            } else {
                closedBottomBar.visibility = View.VISIBLE
            }
        }

        contentTxt.text = post.content

        //TODO: post.imageUris == empty -> 기본 이미지로 설정
        val uris = post.imageUris
        setupImageSlider(uris)
        setupPagerBadge(uris.size)
        registerPagerCallback(uris.size)
    }

    private fun setupImageSlider(uris: List<Uri>) {
        sliderAdapter = ImageSliderAdapter(uris) { pos, uri, imageView ->
            //TODO: 클릭시 디테일 이미지 뷰
        }
        binding.imagePager.adapter = sliderAdapter
        binding.imagePager.setCurrentItem(0, false)
    }

    private fun setupPagerBadge(total: Int) {
        if (total > 0) {
            updatePagerBadge(1, total)
            binding.pagerBadge.visibility = View.VISIBLE
        } else {
            binding.pagerBadge.visibility = View.GONE
        }
    }

    private fun updatePagerBadge(current: Int, total: Int) {
        binding.curPg.text = "$current"
        binding.totalPg.text = "$total"
    }

    private fun registerPagerCallback(total: Int) {
        pageCallback?.let { binding.imagePager.unregisterOnPageChangeCallback(it) }

        pageCallback = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updatePagerBadge(position + 1, total)
            }
        }.also { binding.imagePager.registerOnPageChangeCallback(it) }
    }

    private fun cleanupPager() {
        pageCallback?.let { binding.imagePager.unregisterOnPageChangeCallback(it) }
        pageCallback = null
        binding.imagePager.adapter = null
    }

    private fun setupMoreBtn(){
        binding.moreBtn.setOnClickListener {
            ReportBottomSheetFragment().show(parentFragmentManager, "ReportBottomSheet")
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.setToolbar()
    }

    override fun onDestroyView() {
        cleanupPager()
        super.onDestroyView()
        _binding = null
    }

}