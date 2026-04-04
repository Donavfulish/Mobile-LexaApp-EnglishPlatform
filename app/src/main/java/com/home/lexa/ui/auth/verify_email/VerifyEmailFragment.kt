package com.home.lexa.ui.auth.verify_email

import android.os.CountDownTimer
import android.widget.Toast
import androidx.core.graphics.toColorInt
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.home.lexa.core.base.BaseFragment
import com.home.lexa.databinding.FragmentVerifyEmailBinding
import com.home.lexa.ui.auth.AuthState
import com.home.lexa.ui.auth.AuthViewModel
import com.home.lexa.ui.utils.StringUtils
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import kotlin.getValue

val IS_EMAIL_VERIFY_STRING = "is_email_verify"

class VerifyEmailFragment : BaseFragment<FragmentVerifyEmailBinding>(FragmentVerifyEmailBinding::inflate) {
    private val args: VerifyEmailFragmentArgs by navArgs()
    private val viewModel: AuthViewModel by activityViewModel()
    private val RESEND_OTP_TIMEGAP: Long = 2 * 60000 // 2 phút

    override fun setupViews() {
        val email = args.email
        val maskedEmail = StringUtils.maskEmail(email)

        binding.btnBack.setOnClickListener {
            val navController = findNavController()
            navController.previousBackStackEntry?.savedStateHandle?.set(IS_EMAIL_VERIFY_STRING, false)
            navController.popBackStack()
        }

        binding.tvSubtitle.text = "Mở hộp thư ${maskedEmail} để lấy mã"

        binding.tvResendCode.setOnClickListener {
            startResendTimer()

            viewModel.sendOTP(email)
            Toast.makeText(requireContext(), "Đã gửi lại mã!", Toast.LENGTH_SHORT).show()
        }

        binding.otpInputs.onOtpCompletionListener = { otpCode ->
            viewModel.verifyOTP(email, otpCode)
        }
    }

    override fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.OTPState.collect { state ->
                when (state) {
                    is AuthState.Idle -> { /* Không làm gì */ }
                    is AuthState.Loading -> {
                        binding.otpInputs.setInputEnabled(false)
                    }
                    is AuthState.Success -> {
                        viewModel.commitEmailVerified()

                        val action = VerifyEmailFragmentDirections.actionVerifyEmailToHomeFragment()
                        findNavController().navigate(action)
                    }
                    is AuthState.Error -> {
                        binding.otpInputs.showError()
                        binding.otpInputs.setInputEnabled(true)
                        Toast.makeText(requireContext(), "OTP sai hoặc đã hết hạn", Toast.LENGTH_LONG).show()
                    }

                    else -> {}
                }
            }
        }
    }

    private fun startResendTimer() {
        val timer = object : CountDownTimer(RESEND_OTP_TIMEGAP, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                binding.tvResendCode.apply {
                    isEnabled = false
                    text = "Gửi lại sau (${secondsRemaining}s)"
                    setTextColor(android.graphics.Color.GRAY)
                }
            }

            override fun onFinish() {
                binding.tvResendCode.apply {
                    isEnabled = true
                    text = "Gửi lại mã OTP"
                    setTextColor("#4A69FF".toColorInt())
                }
            }
        }
        timer.start()
    }
}