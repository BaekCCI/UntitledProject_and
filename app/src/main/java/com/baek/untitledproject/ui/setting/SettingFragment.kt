package com.baek.untitledproject.ui.setting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.baek.untitledproject.databinding.FragmentSettingBinding
import com.baek.untitledproject.domain.data.User
import com.baek.untitledproject.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingFragment : Fragment() {

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    private val settingViewModel: SettingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeUserData()
        setCommunityFieldBtn()
        setServiceGuideLinesFieldBtn()
        setEtcFieldBtn()
        setupDialogs()

        binding.loginBtn.setOnClickListener {
            val action = SettingFragmentDirections.actionSettingFragmentToLoginNavGraph()
            findNavController().navigate(action)
        }

        binding.setAlarmBtn.setOnClickListener {
            val action = SettingFragmentDirections.actionSettingFragmentToAlertSettingFragment()
            findNavController().navigate(action)
        }
    }

    private fun observeUserData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                settingViewModel.userData.collect { user ->
                    if (user == null) {
                        showGuestUi()
                    } else {
                        showLoggedInUi(user)
                    }
                }
            }
        }
    }

    private fun showGuestUi() {
        binding.profileLayout.visibility = View.GONE
        binding.reportBlockLayout.visibility = View.GONE
        binding.etcLayout.visibility = View.GONE
        binding.loginBtn.visibility = View.VISIBLE
    }

    private fun showLoggedInUi(user: User) = with(binding) {
        binding.profileLayout.visibility = View.VISIBLE
        binding.reportBlockLayout.visibility = View.VISIBLE
        binding.etcLayout.visibility = View.VISIBLE
        binding.loginBtn.visibility = View.GONE

        nameTxt.text = user.name
        organizationTxt.text = user.department
        binding.studentIdTxt.text = user.studentId
    }


    private fun setCommunityFieldBtn() {
        binding.reportListBtn.setOnClickListener {
            val action = SettingFragmentDirections.actionSettingFragmentToReportListFragment()
            findNavController().navigate(action)
        }
        binding.blockListBtn.setOnClickListener {
            val action = SettingFragmentDirections.actionSettingFragmentToBlockListFragment()
            findNavController().navigate(action)
        }
        binding.communityGuidelineBtn.setOnClickListener {
            //TODO: 커뮤니티 이용 규칙 이동
        }
    }

    private fun setServiceGuideLinesFieldBtn() {
        binding.contactUsBtn.setOnClickListener {
            //TODO: 문의하기 이동
        }
        binding.termsOfServiceBtn.setOnClickListener {
            //TODO: 서비스 이용약관 이동
        }
        binding.privacyPolicyBtn.setOnClickListener {
            //TODO: 개인정보 처리 방침 이동
        }
        binding.youthProtectionPolicyBtn.setOnClickListener {
            //TODO: 청소년 보호 정책 이동
        }
        binding.openSourceLicensesBtn.setOnClickListener {
            //TODO: 오픈 소스 라이선스 이동
        }
    }

    private fun setEtcFieldBtn() {
        binding.privacySettingBtn.setOnClickListener {
            //TODO: 정보 동의 설정 이동
        }
        binding.logoutBtn.setOnClickListener {
            LogoutBottomSheetFragment().show(parentFragmentManager, "logout_dialog")
        }
        binding.deleteAccountBtn.setOnClickListener {
            val action = SettingFragmentDirections.actionSettingFragmentToDeleteAccountFragment()
            findNavController().navigate(action)
        }
    }

    private fun setupDialogs() {
        parentFragmentManager.setFragmentResultListener(
            "req_logout",
            viewLifecycleOwner
        ) { _, bundle ->
            if (bundle.getBoolean("confirmed", false)) {
                settingViewModel.logout()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.setToolbar(detailVisible = true, title = "설정")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}