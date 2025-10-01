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
import com.baek.untitledproject.domain.utils.TermsType
import com.baek.untitledproject.ui.MainActivity
import com.baek.untitledproject.ui.setting.dialog.TermSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
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
            TermSheetDialogFragment.newInstance(TermsType.COMMUNITY_RULES)
                .show(parentFragmentManager, "terms_sheet")
        }
    }

    private fun setServiceGuideLinesFieldBtn() {
        binding.contactUsBtn.setOnClickListener {
        }
        binding.termsOfServiceBtn.setOnClickListener {
            TermSheetDialogFragment.newInstance(TermsType.SERVICE)
                .show(parentFragmentManager, "terms_sheet")
        }
        binding.privacyPolicyBtn.setOnClickListener {
            TermSheetDialogFragment.newInstance(TermsType.PRIVACY)
                .show(parentFragmentManager, "terms_sheet")
        }
        binding.youthProtectionPolicyBtn.setOnClickListener {
            TermSheetDialogFragment.newInstance(TermsType.YOUTH_POLICY)
                .show(parentFragmentManager, "terms_sheet")
        }
        binding.openSourceLicensesBtn.setOnClickListener {
            TermSheetDialogFragment.newInstance(TermsType.OPEN_SOURCE)
                .show(parentFragmentManager, "terms_sheet")
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