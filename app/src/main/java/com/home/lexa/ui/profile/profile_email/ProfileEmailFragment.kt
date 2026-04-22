package com.home.lexa.ui.profile.profile_email

import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.home.lexa.MainActivity
import com.home.lexa.core.base.BaseFragment
import com.home.lexa.databinding.FragmentProfileEmailBinding
import com.home.lexa.ui.auth.AuthState
import com.home.lexa.ui.auth.AuthViewModel
import com.home.lexa.ui.auth.verify_email.VERIFY_PURPOSE
import com.home.lexa.ui.utils.StringUtils
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class ProfileEmailFragment : BaseFragment<FragmentProfileEmailBinding>(FragmentProfileEmailBinding::inflate) {
    private val viewModel: AuthViewModel by activityViewModel()

    private var currentEmail: String = ""

    override fun setupViews() {
        val activityBinding = (requireActivity() as MainActivity).binding
        activityBinding.appBarLayout.apply {
            removeCustomView()
            setText("Cập nhật Email")
            setBackButtonVisible(true)
            setOnClickBack()
        }

        currentEmail = viewModel.getUserEmail() ?: ""

        binding.inputEmail.apply {
            setLabel("EMAIL")
            setText(currentEmail)
            setPlaceHolderText("Nhập email tài khoản")
            setHorizontalScroll()
            setMaxLength(255)

            onTextChanged({
                val isSaveEnabled = (getText().trim() != currentEmail?.trim())
                binding.btnSave.setEnabledState(isSaveEnabled)
            })
        }

        val colorPink = 0xFF636AE8.toInt() // Dùng màu purple_paragraph của app
        val colorWhite = 0xFFFFFFFF.toInt()
        binding.btnSave.apply {
            setText("Lưu thay đổi", colorWhite)
            setBackground(colorPink)
            setEnabledState(false)

            setOnClickAction({
                val email = binding.inputEmail.getText().trim()

                if (!StringUtils.isValidEmail(email)) {
                    Toast.makeText(requireContext(), "Email không hợp lệ", Toast.LENGTH_SHORT).show()
                    return@setOnClickAction
                }

                val action = ProfileEmailFragmentDirections.actionProfileEmailFragmentToVerifyEmail(
                    email,
                    VERIFY_PURPOSE.CHANGE_EMAIL.toString()
                )

                viewModel.sendOTP(email)
                findNavController().navigate(action)
            })
        }
    }

    override fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.changeEmailState.collect { state ->
                when (state) {
                    is AuthState.Idle -> { /* Không làm gì */ }
                    is AuthState.Loading -> {
                        binding.inputEmail.setEnable(false)
                        binding.btnSave.setEnabledState(false)
                    }
                    is AuthState.Success -> {
                        currentEmail = viewModel.getUserEmail() ?: ""

                        binding.inputEmail.setText(currentEmail)
                        binding.inputEmail.setEnable(true)

                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                        viewModel.clearChangeEmailState()
                    }
                    is AuthState.Error -> {

                        currentEmail = viewModel.getUserEmail() ?: ""

                        binding.inputEmail.setText(currentEmail)
                        binding.inputEmail.setEnable(true)

                        Toast.makeText(requireContext(), state.error, Toast.LENGTH_SHORT).show()
                        viewModel.clearChangeEmailState()
                    }
                }
            }
        }
    }
}