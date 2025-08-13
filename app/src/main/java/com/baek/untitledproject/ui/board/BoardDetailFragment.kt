package com.baek.untitledproject.ui.board

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.baek.untitledproject.databinding.FragmentBoardDetailBinding
import com.baek.untitledproject.domain.data.Post
import com.baek.untitledproject.domain.utils.DateUiStyle
import com.baek.untitledproject.domain.utils.Result
import com.baek.untitledproject.domain.utils.toDateRange
import com.baek.untitledproject.ui.MainActivity
import com.baek.untitledproject.ui.board.dialogs.BoardDialogKeys.ACTION_DELETE
import com.baek.untitledproject.ui.board.dialogs.BoardDialogKeys.ACTION_EDIT
import com.baek.untitledproject.ui.board.dialogs.BoardDialogKeys.KEY_ACTION
import com.baek.untitledproject.ui.board.dialogs.BoardDialogKeys.KEY_CONFIRMED
import com.baek.untitledproject.ui.board.dialogs.BoardDialogKeys.KEY_REPORTED
import com.baek.untitledproject.ui.board.dialogs.BoardDialogKeys.REQ_CONFIRM_DELETE
import com.baek.untitledproject.ui.board.dialogs.BoardDialogKeys.REQ_MORE_ACTION
import com.baek.untitledproject.ui.board.dialogs.BoardDialogKeys.REQ_REPORT
import com.baek.untitledproject.ui.board.dialogs.ConfirmDeleteFragment
import com.baek.untitledproject.ui.board.dialogs.MoreActionBottomSheetFragment
import com.baek.untitledproject.ui.board.dialogs.ReportBottomSheetFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDate

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
        setupDialogs(

        )
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

        //작성자이면
        if (viewModel.isWriter) {
            submitInfoView.visibility = View.VISIBLE
            chipgruopView.nameChip.isChecked = post.requiresName
            chipgruopView.departmentChip.isChecked = post.requiresDepartment
            chipgruopView.ageChip.isChecked = post.requiresAge
            chipgruopView.phoneChip.isChecked = post.requiresPhone
            chipgruopView.studentIdChip.isChecked = post.requiresStudentId
            chipgruopView.genderChip.isChecked = post.requiresGender
        }
    }

    private fun setupImageSlider(uris: List<Uri>) {
        //이미지가 없으면
        if(uris.isEmpty()){
            sliderAdapter = ImageSliderAdapter(emptyList())
            binding.imagePager.adapter = sliderAdapter
            binding.pagerBadge.visibility = View.GONE
            return
        }

        sliderAdapter = ImageSliderAdapter(uris) { pos ->
            openImageViewer(uris, pos)
        }
        binding.imagePager.adapter = sliderAdapter
        binding.imagePager.setCurrentItem(0, false)
    }
    private fun openImageViewer(uris:List<Uri>,startIdx : Int){
        if(uris.isEmpty()) return

        val action = BoardDetailFragmentDirections.actionBoardDetailFragmentToImageViewerDialogFragment(
            imageUris = uris.toTypedArray(),
            startIndex = startIdx
        )
        findNavController().navigate(action)
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

    private fun setupMoreBtn() {

        binding.moreBtn.setOnClickListener {
            if(viewModel.isWriter){
                MoreActionBottomSheetFragment().show(parentFragmentManager, "more_action_dialog")
            }else{
                ReportBottomSheetFragment().show(parentFragmentManager, "repost_dialog")
            }
        }
    }

    private fun setupDialogs() {
        parentFragmentManager.setFragmentResultListener(
            REQ_REPORT,
            viewLifecycleOwner
        ) { _, bundle ->
            if (bundle.getBoolean(KEY_REPORTED, false)) {
                // 신고 화면으로 이동
            }
        }
        parentFragmentManager.setFragmentResultListener(
            REQ_MORE_ACTION,
            viewLifecycleOwner
        ) { _, bundle ->
            when (bundle.getString(KEY_ACTION)) {
                ACTION_EDIT -> {
                    findNavController().navigate(
                        BoardDetailFragmentDirections.actionBoardDetailFragmentToWriteBoardNavGraph().actionId,
                        bundleOf("postId" to args.id)
                    )
                }

                ACTION_DELETE -> {
                    ConfirmDeleteFragment().show(parentFragmentManager, "confirm_dialog")
                }
            }
        }
        parentFragmentManager.setFragmentResultListener(
            REQ_CONFIRM_DELETE,
            viewLifecycleOwner
        ) { _, bundle ->
            if (bundle.getBoolean(KEY_CONFIRMED, false)) {
                //삭제 로직
            }
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