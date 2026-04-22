package com.home.lexa.ui.profile.profile_change_password

import android.widget.Toast
import androidx.core.view.get
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.home.lexa.MainActivity
import com.home.lexa.R
import com.home.lexa.core.base.BaseFragment
import com.home.lexa.databinding.FragmentProfileChangePasswordBinding
import com.home.lexa.domain.models.ChangePasswordRequest
import com.home.lexa.ui.auth.AuthState
import com.home.lexa.ui.auth.AuthViewModel
import com.home.lexa.ui.auth.verify_email.VERIFY_PURPOSE
import com.home.lexa.ui.profile.profile_email.ProfileEmailFragmentDirections
import com.home.lexa.ui.utils.StringUtils
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class ProfileChangePasswordFragment : BaseFragment<FragmentProfileChangePasswordBinding>(FragmentProfileChangePasswordBinding::inflate) {
    private val viewModel: AuthViewModel by activityViewModel()

    override fun setupViews() {
        val activityBinding = (requireActivity() as MainActivity).binding
        activityBinding.appBarLayout.apply {
            removeCustomView()
            setText(getString(R.string.change_password))
            setBackButtonVisible(true)
            setOnClickBack()
        }

        binding.inputOldPassword.apply {
            setLabel(getString(R.string.old_password))
            setPlaceHolderText(getString(R.string.enter_old_password))
            setHorizontalScroll()
            hideLockIcon()
        }

        binding.inputNewPassword.apply {
            setLabel(getString(R.string.new_password))
            setPlaceHolderText(getString(R.string.enter_new_password))
            setHorizontalScroll()
            hideLockIcon()
        }

        binding.inputConfirmPassword.apply {
            setPlaceHolderText(getString(R.string.enter_confirm_new_password))
            setHorizontalScroll()
            hideLockIcon()
        }

        val colorPink = 0xFF636AE8.toInt() // Dùng màu purple_paragraph của app
        val colorWhite = 0xFFFFFFFF.toInt()
        binding.btnSave.apply {
            setText(getString(R.string.save_changes), colorWhite)
            setBackground(colorPink)

            setOnClickAction({
                handleChangePassword()
            })
        }
    }

    override fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.changePasswordState.collect { state ->
                when (state) {
                    is AuthState.Idle -> { /* Không làm gì */ }
                    is AuthState.Loading -> {
                        binding.inputOldPassword.setEnable(false)
                        binding.inputNewPassword.setEnable(false)
                        binding.inputConfirmPassword.setEnable(false)

                        binding.btnSave.setEnabledState(false)
                    }
                    is AuthState.Success -> {
                        binding.inputOldPassword.setEnable(true)
                        binding.inputNewPassword.setEnable(true)
                        binding.inputConfirmPassword.setEnable(true)
                        binding.btnSave.setEnabledState(true)

                        binding.inputOldPassword.setText(null)
                        binding.inputNewPassword.setText(null)
                        binding.inputConfirmPassword.setText(null)

                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                        viewModel.clearChangeEmailState()
                    }
                    is AuthState.Error -> {
                        binding.inputOldPassword.setEnable(true)
                        binding.inputNewPassword.setEnable(true)
                        binding.inputConfirmPassword.setEnable(true)
                        binding.btnSave.setEnabledState(true)

                        Toast.makeText(requireContext(), state.error, Toast.LENGTH_SHORT).show()
                        viewModel.clearChangeEmailState()
                    }
                }
            }
        }
    }

    private fun handleChangePassword() {
        val oldPassword = binding.inputOldPassword.getText()
        val newPassword = binding.inputNewPassword.getText()
        val confirmPassword = binding.inputConfirmPassword.getText()

        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.toast_please_enter_all_information), Toast.LENGTH_SHORT).show()
            return
        }

        if (newPassword != confirmPassword) {
            Toast.makeText(requireContext(), getString(R.string.toast_confirm_password_not_match), Toast.LENGTH_SHORT).show()
            return
        }

        val request = ChangePasswordRequest(oldPassword, newPassword)
        viewModel.changePassword(request)
    }
}