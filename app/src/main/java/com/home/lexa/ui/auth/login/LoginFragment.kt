package com.home.lexa.ui.auth.login

import android.content.Intent
import android.graphics.Color
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.home.lexa.R
import com.home.lexa.core.base.BaseFragment
import com.home.lexa.databinding.FragmentLoginBinding
import com.home.lexa.domain.models.LoginRequest
import com.home.lexa.ui.auth.AuthState
import com.home.lexa.ui.auth.AuthViewModel
import com.home.lexa.ui.auth.GoogleUrls
import com.home.lexa.ui.auth.verify_email.VERIFY_PURPOSE
import kotlinx.coroutines.flow.observeOn
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {

    private val viewModel: AuthViewModel by activityViewModel()

    private val colorPrimary = Color.parseColor("#6200EA")
    private val colorTextDark = Color.parseColor("#333333")
    private val colorBorder = Color.parseColor("#E0E0E0")

    override fun setupViews() {
        viewModel.getRememberedLoginRequest()
        setupInputs()
        setupButtons()
    }

    private fun setupInputs() {
        binding.inputEmail.apply {
            setLabel("Email")
            setIcon(R.drawable.ic_email)
            setPlaceHolderText("example@lingua.com")
            setHorizontalScroll()
            setMaxLength(255)

        }

        binding.inputPassword.apply {
            setLabel("Mật khẩu")
            setPlaceHolderText("........")
            setHorizontalScroll()
        }
    }

    private fun setupButtons() {
        // Nút Đăng Nhập
        binding.btnLogin.apply {
            setText("Đăng Nhập", Color.WHITE)
            setBackground(colorPrimary)
            setOnClickAction {
                val email = binding.inputEmail.getText().trim()
                val password = binding.inputPassword.getText().trim()

                if (email.isNotEmpty() && password.isNotEmpty()) {
                    val loginRequest = LoginRequest(email, password)

                    viewModel.login(loginRequest)
                } else {
                    Toast.makeText(requireContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Quên mật khẩu
        binding.tvForgotPassword.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_forgetPasswordFragment)
        }

        // Nút Google
        binding.btnGoogle.apply {
            setText(" Đăng nhập bằng Google", colorTextDark)
            setBackground(Color.WHITE)
            setStroke(1, colorBorder)
            // Thay R.drawable.ic_google bằng icon thực tế của bạn
            setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_google))
            setOnClickAction {
                val intent = Intent(Intent.ACTION_VIEW, GoogleUrls.loginUri)
                startActivity(intent)
            }
        }

        // Click Đăng ký ngay
        binding.tvSignUpAction.setOnClickListener {
            Toast.makeText(requireContext(), "Chuyển sang màn Đăng ký", Toast.LENGTH_SHORT).show()
            viewModel.resetOAuth()
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }
    }

    override fun observeData() {
        // Lắng nghe trạng thái từ ViewModel
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loginState.collect { state ->
                when (state) {
                    is AuthState.Idle -> { /* Không làm gì */ }
                    is AuthState.Loading -> {
                        // TODO: Hiện ProgressDialog hoặc đổi text nút thành "Đang đăng nhập..."
                        binding.btnLogin.setText("Đang xử lý...", Color.WHITE)
                    }
                    is AuthState.Success -> {
                        val currentRequest = LoginRequest(
                            binding.inputEmail.getText().trim(),
                            binding.inputPassword.getText().trim()
                        )

                        if (binding.cbRememberMe.isChecked) {
                            viewModel.rememberLoginRequest(currentRequest)
                        } else {
                            viewModel.forgetLoginRequest()
                        }

                        viewModel.resetState()

                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()

                        if (!viewModel.isEmailVerified()) {
                            val email = binding.inputEmail.getText().trim()
                            viewModel.sendOTP(email)

                            val action = LoginFragmentDirections.actionSignupFragmentToVerifyEmail(email,
                                VERIFY_PURPOSE.TO_HOME.toString())
                            findNavController().navigate(action)
                        } else {
                            findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                        }
                    }
                    is AuthState.Error -> {
                        binding.btnLogin.setText("Đăng Nhập", Color.WHITE) // Khôi phục nút
                        Toast.makeText(requireContext(), "Tài khoản chưa được đăng ký", Toast.LENGTH_LONG).show()
                    }

                    else -> {}
                }
            }
        }

        viewModel.rememberedLoginRequest.observe(viewLifecycleOwner) { value ->
            if (value == null) return@observe

            binding.inputEmail.setText(value.email)
            binding.inputPassword.setText(value.password)
            binding.cbRememberMe.isChecked = true
        }


        viewModel.oauthGoogleResult.observe(viewLifecycleOwner) { data ->
            data?.let {
                viewModel.setAccessToken(it.accessToken?: "")
                viewModel.loginGoogle()
                viewModel.resetOAuth()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Xóa trạng thái cũ để không bị trigger lại logic Success
        viewModel.resetState()
    }
}