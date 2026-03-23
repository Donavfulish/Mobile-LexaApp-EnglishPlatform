package com.home.lexa.ui.auth.signup

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.home.lexa.R
import com.home.lexa.core.base.BaseFragment
import com.home.lexa.databinding.FragmentSignupBinding
import com.home.lexa.domain.models.LoginRequest
import com.home.lexa.domain.models.SignUpRequest
import com.home.lexa.domain.models.UserRole
import com.home.lexa.ui.auth.login.AuthState
import com.home.lexa.ui.auth.login.AuthViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SignupFragment : BaseFragment<FragmentSignupBinding>(FragmentSignupBinding::inflate) {

    private val viewModel: AuthViewModel by viewModel()

    private val colorPrimary = Color.parseColor("#6200EA")
    private val colorLightPrimary = Color.parseColor("#F8F4FF") // Màu nền tím nhạt khi được chọn
    private val colorTextDark = Color.parseColor("#333333")
    private val colorBorder = Color.parseColor("#E0E0E0")
    private val colorInactiveText = Color.parseColor("#888888")

    private var isTeacherRoleSelected = false

    override fun setupViews() {
        setupSocialButtons()
        setupInputs()
        setupRoleToggle()
        setupSignupButton()

        binding.tvLoginAction.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupRoleToggle() {
        updateRoleUI()

        binding.btnRoleStudent.setOnClickListener {
            if (isTeacherRoleSelected) {
                isTeacherRoleSelected = false
                updateRoleUI()
            }
        }

        binding.btnRoleTeacher.setOnClickListener {
            if (!isTeacherRoleSelected) {
                isTeacherRoleSelected = true
                updateRoleUI()
            }
        }
    }

    private fun updateRoleUI() {
        if (isTeacherRoleSelected) {
            // FIX LỖI SỐ: Dùng getString() để lấy text thực sự
            binding.tvTitle.text = getString(R.string.signup_teacher_title)
            binding.tvSubtitle.text = getString(R.string.signup_teacher_desc)

            // --- GIÁO VIÊN ACTIVE ---
            binding.btnRoleTeacher.setTextColor(colorPrimary)
            binding.btnRoleTeacher.setIconTintResource(R.color.lexa_primary)
            binding.btnRoleTeacher.strokeColor = ContextCompat.getColorStateList(requireContext(), R.color.lexa_primary)
            binding.btnRoleTeacher.backgroundTintList = ColorStateList.valueOf(colorLightPrimary) // Đổi nền tím nhạt

            // --- HỌC SINH INACTIVE ---
            binding.btnRoleStudent.setTextColor(colorInactiveText)
            binding.btnRoleStudent.setIconTintResource(R.color.gray_888888)
            binding.btnRoleStudent.strokeColor = ContextCompat.getColorStateList(requireContext(), R.color.gray_E0E0E5)
            binding.btnRoleStudent.backgroundTintList = ColorStateList.valueOf(Color.WHITE) // Đổi nền trắng

            binding.llTeacherFields.visibility = View.VISIBLE

        } else {
            // FIX LỖI SỐ: Dùng getString()
            binding.tvTitle.text = getString(R.string.signup_title)
            binding.tvSubtitle.text = getString(R.string.signup_desc)

            // --- HỌC SINH ACTIVE ---
            binding.btnRoleStudent.setTextColor(colorPrimary)
            binding.btnRoleStudent.setIconTintResource(R.color.lexa_primary)
            binding.btnRoleStudent.strokeColor = ContextCompat.getColorStateList(requireContext(), R.color.lexa_primary)
            binding.btnRoleStudent.backgroundTintList = ColorStateList.valueOf(colorLightPrimary) // Đổi nền tím nhạt

            // --- GIÁO VIÊN INACTIVE ---
            binding.btnRoleTeacher.setTextColor(colorInactiveText)
            binding.btnRoleTeacher.setIconTintResource(R.color.gray_888888)
            binding.btnRoleTeacher.strokeColor = ContextCompat.getColorStateList(requireContext(), R.color.gray_E0E0E5)
            binding.btnRoleTeacher.backgroundTintList = ColorStateList.valueOf(Color.WHITE) // Đổi nền trắng

            binding.llTeacherFields.visibility = View.GONE
        }
    }

    private fun setupSocialButtons() {
        binding.btnGoogle.apply {
            setText("Google", colorTextDark)
            setBackground(Color.WHITE)
            setStroke(1, colorBorder)
            setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_google))
            setOnClickAction {
                Toast.makeText(requireContext(),"GG", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnFacebook.apply {
            setText("Facebook", colorTextDark)
            setBackground(Color.WHITE)
            setStroke(1, colorBorder)
            setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_facebook))
            setOnClickAction {
                Toast.makeText(requireContext(),"FB", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupInputs() {
        binding.inputEmail.apply {
            setLabel("Email *")
            setPlaceHolderText("example@lingua.com")
            setIcon(R.drawable.ic_email)
        }

        binding.inputDob.apply {
            setLabel("Ngày sinh")
            setPlaceHolderText("dd/mm/yyyy")
            setIcon(R.drawable.ic_calendar)
            // TODO: Mở DatePickerDialog khi click
        }

        binding.inputAddress.apply {
            setLabel("Địa chỉ")
            setPlaceHolderText("Nhập địa chỉ của bạn...")
            setIcon(R.drawable.ic_location)
        }

        binding.inputPassword.apply {
            setLabel("Mật khẩu *")
            setPlaceHolderText("........")
        }

        binding.inputConfirmPassword.apply {
            setLabel("Xác nhận mật khẩu *")
            setPlaceHolderText("........")
        }
    }

    private fun setupSignupButton() {
        binding.btnSignup.apply {
            setText("Đăng ký", Color.WHITE)
            setBackground(colorPrimary)
            setOnClickAction {
                // Xử lý lc gọi ViewModel tùy theo isTeacherRoleSelected
                val name = "Đỗ Văn Hà"
                val role = if (isTeacherRoleSelected) UserRole.TEACHER else UserRole.STUDENT
                val password = binding.inputPassword.getText()
                val email = binding.inputEmail.getText()
                val date_of_birth = binding.inputDob.getText()
                val address = binding.inputAddress.getText()

                if (email.isNotEmpty() && password.isNotEmpty() && name.isNotEmpty()) {
                    viewModel.signup(SignUpRequest(email, password, date_of_birth, address, name, role))
                } else {
                    Toast.makeText(requireContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Sự kiện click để chọn file cho Giáo viên
        binding.btnUploadLanguageCert.setOnClickListener {
            Toast.makeText(requireContext(), "Mở thư viện ảnh/file", Toast.LENGTH_SHORT).show()
        }

        binding.btnUploadPedagogyCert.setOnClickListener {
            Toast.makeText(requireContext(), "Mở thư viện ảnh/file", Toast.LENGTH_SHORT).show()
        }
    }

    override fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.signupState.collect { state ->
                when (state) {
                    is AuthState.Idle -> { /* Không làm gì */ }
                    is AuthState.Loading -> {
                        // TODO: Hiện ProgressDialog hoặc đổi text nút thành "Đang đăng nhập..."
                        binding.btnSignup.setText("Đang xử lý...", Color.WHITE)
                    }
                    is AuthState.Success -> {
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_signupFragment_to_homeFragment)
                        viewModel.resetState()
                    }
                    is AuthState.Error -> {
                        binding.btnSignup.setText("Đăng Ký", Color.WHITE) // Khôi phục nút
                        Toast.makeText(requireContext(), state.error, Toast.LENGTH_LONG).show()
                    }

                    else -> {}
                }
            }
        }
    }
}