package com.home.lexa.ui.auth.reset_password

import android.graphics.Color
import android.widget.Toast
import androidx.navigation.NavArgs
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.common.api.internal.LifecycleFragment
import com.google.firebase.auth.actionCodeSettings
import com.home.lexa.R
import com.home.lexa.core.base.BaseFragment
import com.home.lexa.databinding.FragmentResetPasswordBinding
import com.home.lexa.domain.models.ResetPasswordRequest
import com.home.lexa.ui.auth.AuthViewModel
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import kotlin.getValue

class ResetPasswordFragment : BaseFragment<FragmentResetPasswordBinding>(FragmentResetPasswordBinding::inflate) {
    private val args: ResetPasswordFragmentArgs by navArgs()
    private val viewModel: AuthViewModel by activityViewModel()
    private val colorPrimary = Color.parseColor("#6200EA")

    override fun setupViews() {
        binding.inputPassword.apply {
            setLabel(getString(R.string.new_password))
            setPlaceHolderText("........")
            setHorizontalScroll()
        }

        binding.inputConfirmPassword.apply {
            setLabel(getString(R.string.verify_new_password))
            setPlaceHolderText("........")
            setHorizontalScroll()
        }

        binding.btnResetPassword.apply {
            setText(getString(R.string.reset_password_title), Color.WHITE)
            setBackground(colorPrimary)
            setOnClickAction {
                val password = binding.inputPassword.getText()
                val passwordConfirmed = binding.inputConfirmPassword.getText()

                if (password.isEmpty() || passwordConfirmed.isEmpty()) {
                    Toast.makeText(requireContext(), getString(R.string.toast_please_enter_all_information), Toast.LENGTH_SHORT).show()
                    return@setOnClickAction
                }

                if (password != passwordConfirmed) {
                    Toast.makeText(requireContext(), getString(R.string.toast_confirm_password_not_match), Toast.LENGTH_SHORT).show()
                    return@setOnClickAction
                }

                viewModel.resetPassword(ResetPasswordRequest(args.email, password))
            }
        }
    }

    override fun observeData() {
        viewModel.resetPasswordResult.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess == null) return@observe

            if (isSuccess) {
                Toast.makeText(requireContext(), getString(R.string.toast_reset_password_successfully), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), getString(R.string.toast_reset_password_fail), Toast.LENGTH_SHORT).show()
            }

            findNavController().navigate(R.id.action_resetPasswordFragment_to_loginFragment)
        }
    }
}