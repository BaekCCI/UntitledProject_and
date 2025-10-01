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
import com.baek.untitledproject.R
import com.baek.untitledproject.databinding.FragmentBoardDetailBinding
import com.baek.untitledproject.domain.data.Post
import com.baek.untitledproject.domain.data.PostRead
import com.baek.untitledproject.domain.utils.DateUiStyle
import com.baek.untitledproject.domain.utils.ReportTopic
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
        setupDialogs()
        setupBottomBtn()
        observeDeleteState()

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
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
                            setupMoreBtn(state.data)
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

    private fun bindBoardData(post: PostRead) = with(binding) {

        organizationTxt.text = post.organization
        titleTxt.text = post.title

        //면접 여부에 따른 ui 설정
        if (post.hasInterview) {
            interviewChip.visibility = View.VISIBLE
            //면접 일정 UI 설정
            if (post.interviewStart != null && post.interviewEnd != null) {
                interviewScheduleTxt.text =
                    toDateRange(post.interviewStart, post.interviewEnd, DateUiStyle.MD_KR)
            } else {
                interviewScheduleTxt.text = "추후 공지 예정"
            }
            //면접 장소
            binding.interviewLoc.text = post.interviewLocation ?: "추후 공지 예정"
        } else {
            binding.interviewLayout.visibility = View.GONE
        }

        recruitDateTxt.text = post.recruitmentEnd?.let {
            toDateRange(
                post.recruitmentStart,
                post.recruitmentEnd,
                DateUiStyle.MD_WITH_WEEKDAY
            )
        }

        //작성자 여부에 따른 바텀바 설정
        if (post.isAuthor) {
            bottomBarContainer.visibility = View.GONE
        } else {
            bottomBarContainer.visibility = View.VISIBLE
        }

        //모집상태에 따른 ui 설정
        if (post.status == "recruiting") {
            recruitOpenChip.visibility = View.VISIBLE
            recruitClosedChip.visibility = View.GONE

            closedBottomBar.visibility = View.GONE
            recruitingBottomBar.visibility = View.VISIBLE

            if (post.isApplied) {
                recruitBtn.isEnabled = false
                recruitBtn.text = "지원완료"
            }
        } else {
            recruitOpenChip.visibility = View.GONE
            recruitClosedChip.visibility = View.VISIBLE

            closedBottomBar.visibility = View.VISIBLE
            recruitingBottomBar.visibility = View.GONE
        }
        contentTxt.text = post.content


        //TODO: post.imageUris == empty -> 기본 이미지로 설정
        val uris = post.imageUris
        setupImageSlider(uris)
        setupPagerBadge(uris.size)
        registerPagerCallback(uris.size)

        //작성자이면
        if (post.isAuthor) {
            submitInfoView.visibility = View.VISIBLE
            chipGroupView.nameChip.isChecked = post.requiresName
            chipGroupView.departmentChip.isChecked = post.requiresDepartment
            chipGroupView.ageChip.isChecked = post.requiresAge
            //chipGroupView.phoneChip.isChecked = post.requiresPhone
            chipGroupView.studentIdChip.isChecked = post.requiresStudentId
            chipGroupView.genderChip.isChecked = post.requiresGender
        }
    }

    private fun setupImageSlider(uris: List<Uri>) {
        //이미지가 없으면
        if (uris.isEmpty()) {
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

    private fun openImageViewer(uris: List<Uri>, startIdx: Int) {
        if (uris.isEmpty()) return

        val action =
            BoardDetailFragmentDirections.actionBoardDetailFragmentToImageViewerDialogFragment(
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

    private fun setupBottomBtn() {
        binding.recruitBtn.setOnClickListener {
            findNavController().navigate(
                R.id.submit_application_nav,
                bundleOf("postId" to args.id)
            )
        }
    }

    private fun setupMoreBtn(post: PostRead) {

        binding.moreBtn.setOnClickListener {
            if (post.isAuthor) {
                MoreActionBottomSheetFragment().show(parentFragmentManager, "more_action_dialog")
            } else {
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
                val action = BoardDetailFragmentDirections
                    .actionBoardDetailFragmentToReportNavGraph(
                        reportTopic = ReportTopic.POST,
                        targetId = args.id,
                        reportedUserId = viewModel.authorId
                    )
                findNavController().navigate(action)
            }
        }
        parentFragmentManager.setFragmentResultListener(
            REQ_MORE_ACTION,
            viewLifecycleOwner
        ) { _, bundle ->
            when (bundle.getString(KEY_ACTION)) {
                ACTION_EDIT -> {
                    val action =
                        BoardDetailFragmentDirections.actionBoardDetailFragmentToEditPostFragment(
                            args.id
                        )
                    findNavController().navigate(action)
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
                viewModel.deletePost(args.id)

            }
        }
    }

    private fun observeDeleteState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.deleteState.collect { state ->
                    when (state) {
                        is Result.Success -> {
                            findNavController().popBackStack()
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