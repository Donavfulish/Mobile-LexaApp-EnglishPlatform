package com.home.lexa.ui.auth.verify_email

import android.os.CountDownTimer
import android.widget.Toast
import androidx.core.graphics.toColorInt
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.home.lexa.R
import com.home.lexa.core.base.BaseFragment
import com.home.lexa.databinding.FragmentVerifyEmailBinding
import com.home.lexa.domain.models.ChangeEmailRequest
import com.home.lexa.ui.auth.AuthState
import com.home.lexa.ui.auth.AuthViewModel
import com.home.lexa.ui.utils.StringUtils
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import kotlin.getValue

enum class VERIFY_PURPOSE {
    TO_HOME, RESET_PASSWORD, CHANGE_EMAIL
}

class VerifyEmailFragment : BaseFragment<FragmentVerifyEmailBinding>(FragmentVerifyEmailBinding::inflate) {
    private val args: VerifyEmailFragmentArgs by navArgs()
    private val viewModel: AuthViewModel by activityViewModel()
    private val RESEND_OTP_TIMEGAP: Long = 2 * 60000 // 2 phút

    override fun setupViews() {
        val email = args.email
        val maskedEmail = StringUtils.maskEmail(email)

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.tvSubtitle.text = getString(R.string.otp_desc, maskedEmail)

        binding.tvResendCode.setOnClickListener {
            startResendTimer()

            viewModel.sendOTP(email)
            Toast.makeText(requireContext(), getString(R.string.toast_otp_resent), Toast.LENGTH_SHORT).show()
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

                        handleSuccess()
                        viewModel.resetOTPState()
                    }
                    is AuthState.Error -> {
                        binding.otpInputs.setInputEnabled(true)
                        binding.otpInputs.showError()

                        Toast.makeText(requireContext(), getString(R.string.toast_otp_wrong_or_expired), Toast.LENGTH_LONG).show()
                        viewModel.resetOTPState()
                    }
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
                    text = getString(R.string.otp_resend_enabled_after_seconds, secondsRemaining)
                    setTextColor(android.graphics.Color.GRAY)
                }
            }

            override fun onFinish() {
                binding.tvResendCode.apply {
                    isEnabled = true
                    text = getString(R.string.otp_resend)
                    setTextColor("#4A69FF".toColorInt())
                }
            }
        }
        timer.start()
    }

    private fun handleSuccess() {
        when (args.purpose) {
            VERIFY_PURPOSE.TO_HOME.toString() -> {
                val action = VerifyEmailFragmentDirections.actionVerifyEmailToHomeFragment()
                findNavController().navigate(action)
            }

            VERIFY_PURPOSE.RESET_PASSWORD.toString() -> {
                val action = VerifyEmailFragmentDirections.actionVerifyEmailToResetPasswordFragment(args.email)
                findNavController().navigate(action)
            }

            VERIFY_PURPOSE.CHANGE_EMAIL.toString() -> {
                viewModel.changeEmail(ChangeEmailRequest(email = args.email))
                val action = VerifyEmailFragmentDirections.actionVerifyEmailToProfileEmailFragment()
                findNavController().navigate(action)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        viewModel.resetState()
    }
}