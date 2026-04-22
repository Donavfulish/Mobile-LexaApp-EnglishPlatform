package com.home.lexa.ui.auth.forget_password

import android.graphics.Color
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.home.lexa.R
import com.home.lexa.core.base.BaseFragment
import com.home.lexa.databinding.FragmentForgetPasswordBinding
import com.home.lexa.ui.auth.AuthViewModel
import com.home.lexa.ui.auth.signup.SignupFragmentDirections
import com.home.lexa.ui.auth.verify_email.VERIFY_PURPOSE
import com.home.lexa.ui.utils.StringUtils
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import kotlin.getValue

class ForgetPasswordFragment : BaseFragment<FragmentForgetPasswordBinding>(FragmentForgetPasswordBinding::inflate) {
    private val viewModel: AuthViewModel by activityViewModel()
    private val colorPrimary by lazy(LazyThreadSafetyMode.NONE) { ContextCompat.getColor(requireContext(), R.color.c_6200ea) }

    override fun setupViews() {
        binding.inputEmail.apply {
            setLabel("Email *")
            setPlaceHolderText("example@gmail.com")
            setIcon(R.drawable.ic_email)
            setHorizontalScroll()
            setMaxLength(255)
        }

        binding.btnContinue.apply {
            setText("Tiếp tục", ContextCompat.getColor(requireContext(), R.color.c_ffffff))
            setBackground(colorPrimary)
            setOnClickAction {
                val email = binding.inputEmail.getText()

                if (email.isEmpty()) {
                    Toast.makeText(requireContext(), "Vui lòng nhập email tài khoản", Toast.LENGTH_SHORT).show()
                    return@setOnClickAction
                }

                if (!StringUtils.isValidEmail(email)) {
                    Toast.makeText(requireContext(), "Email không hợp lệ", Toast.LENGTH_SHORT).show()
                    return@setOnClickAction
                }

                viewModel.sendOTP(email)
                navigateToOTPFragment(email)
            }
        }

        binding.btnBack.apply {
            setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    override fun observeData() {}

    private fun navigateToOTPFragment(email: String) {
        val action = ForgetPasswordFragmentDirections.actionForgetPasswordFragmentToVerifyEmail(email, VERIFY_PURPOSE.RESET_PASSWORD.toString())

        findNavController().navigate(action)
    }
}