package com.home.lexa.ui.auth.signup

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.home.lexa.R
import com.home.lexa.core.base.BaseFragment
import com.home.lexa.databinding.FragmentSignupBinding
import com.home.lexa.domain.models.OAuthRegisterRequest
import com.home.lexa.domain.models.ProviderType
import com.home.lexa.domain.models.SignUpRequest
import com.home.lexa.domain.models.UserRole
import com.home.lexa.ui.auth.GoogleUrls
import com.home.lexa.ui.auth.AuthState
import com.home.lexa.ui.auth.AuthViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import androidx.core.view.isVisible
import coil.load
import coil.size.ViewSizeResolver
import com.google.firebase.auth.OAuthProvider
import com.home.lexa.ui.auth.verify_email.IS_EMAIL_VERIFY_STRING
import com.home.lexa.ui.utils.DateUtils
import com.home.lexa.ui.utils.MediaUtils
import com.home.lexa.ui.utils.StringUtils

enum class CertType { LANGUAGE, PEDAGOGY }
class SignupFragment : BaseFragment<FragmentSignupBinding>(FragmentSignupBinding::inflate) {

//    private lateinit var viewModel: AuthViewModel
    private val viewModel: AuthViewModel by activityViewModel()

    private val colorPrimary = Color.parseColor("#6200EA")
    private val colorLightPrimary = Color.parseColor("#F8F4FF") // Màu nền tím nhạt khi được chọn
    private val colorTextDark = Color.parseColor("#333333")
    private val colorBorder = Color.parseColor("#E0E0E0")
    private val colorInactiveText = Color.parseColor("#888888")

    private var isTeacherRoleSelected = false
    private var oauthProvider: ProviderType? = null

    private var certType: CertType? = null
    private val pickMediaLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            if (certType == CertType.LANGUAGE) {
                updateMediaUI(it, binding.btnUploadLanguageCert.root)
                viewModel.setLanguageUri(it)
            } else {
                updateMediaUI(it, binding.btnUploadPedagogyCert.root)
                viewModel.setPedagogyUri(it)
            }
        }
    }

    override fun setupViews() {
        setupSocialButtons()
        setupInputs()
        setupRoleToggle()
        setupSignupButton()

        binding.tvLoginAction.setOnClickListener {
            viewModel.resetOAuth()
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

    private fun updateMediaUI(uri: Uri, container: LinearLayout) {
        val imgPreview = container.findViewById<ImageView>(R.id.imgPreview)
        val txtFileName = container.findViewById<TextView>(R.id.txtFileName)
        val icUpload = container.findViewById<ImageView>(R.id.icUpload) // Icon gốc
        val txtInstruction = container.findViewById<TextView>(R.id.txtStatus) // Text hướng dẫn gốc
        val txtFileType = container.findViewById<TextView>(R.id.txtFileType)

        // Lấy tên file từ Uri
        val fileName = MediaUtils.getFileName(requireContext(), uri)

        // Ẩn các thành phần cũ
        icUpload.visibility = View.GONE
        txtInstruction.visibility = View.GONE
        txtFileType.visibility = View.GONE

        // Hiển thị phần preview
        txtFileName.visibility = View.VISIBLE
        txtFileName.text = fileName

        imgPreview.visibility = View.VISIBLE
        imgPreview.load(uri)
    }

    private fun setupSocialButtons() {
        binding.btnGoogle.apply {
            setText("Google", colorTextDark)
            setBackground(Color.WHITE)
            setStroke(1, colorBorder)
            setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_google))
            setOnClickAction {
                val intent = Intent(Intent.ACTION_VIEW, GoogleUrls.loginUri)
                startActivity(intent)
            }
        }

        binding.btnFacebook.apply {
            setText("Facebook", colorTextDark)
            setBackground(Color.WHITE)
            setStroke(1, colorBorder)
            setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_facebook))
            setOnClickAction {
                val intent = Intent(Intent.ACTION_VIEW, GoogleUrls.loginUri)
                startActivity(intent)
            }
        }
    }

    private fun setupInputs() {
        binding.inputEmail.apply {
            setLabel("Email *")
            setPlaceHolderText("example@lingua.com")
            setIcon(R.drawable.ic_email)
        }

        binding.inputName.apply {
            setLabel("Họ và tên *")
            setPlaceHolderText("Nguyễn Văn A")
            setIcon(R.drawable.user_profile)
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
                val name = binding.inputName.getText()
                val role = if (isTeacherRoleSelected) UserRole.TEACHER else UserRole.STUDENT
                val password = binding.inputPassword.getText()
                val confirmPassword = binding.inputConfirmPassword.getText()
                val email = binding.inputEmail.getText()
                val date_of_birth = binding.inputDob.getText()
                val address = binding.inputAddress.getText()
                val isGuaranteed = binding.cbCommitment.isChecked

                if (!StringUtils.isValidEmail(email)) {
                    Toast.makeText(requireContext(), "Email sai định dạng", Toast.LENGTH_SHORT).show()
                    return@setOnClickAction
                }

                if (!password.isEmpty() && password != confirmPassword) {
                    Toast.makeText(requireContext(), "Mật khẩu không trùng khớp", Toast.LENGTH_SHORT).show()
                    return@setOnClickAction
                }

                if (!date_of_birth.isEmpty() && !DateUtils.isValidDate(date_of_birth)) {
                    Toast.makeText(requireContext(), "Ngày sinh sai định dạng", Toast.LENGTH_SHORT).show()
                    return@setOnClickAction
                }

                if (role == UserRole.TEACHER) {
                    if (binding.btnUploadLanguageCert.icUpload.isVisible) {
                        Toast.makeText(requireContext(), "Vui lòng cung cấp bằng ngoại ngữ", Toast.LENGTH_SHORT).show()
                        return@setOnClickAction
                    }
                    if (!isGuaranteed) {
                        Toast.makeText(requireContext(), "Vui lòng xác nhận bằng cấp không qua chỉnh sửa", Toast.LENGTH_SHORT).show()
                        return@setOnClickAction
                    }
                }

                if (oauthProvider == null) {
                    if (email.isNotEmpty() && password.isNotEmpty() && name.isNotEmpty()) {
                        viewModel.signup(
                            SignUpRequest(
                                email = email,
                                password = password,
                                date_of_birth = DateUtils.convertToBackendFormat(date_of_birth),
                                address = address,
                                name = name,
                                role = role
                            )
                        )

                        viewModel.sendOTP(email)

                        viewModel.resetState()

                        navigateToOTPFragment(email)
                    } else {
                        Toast.makeText(requireContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                    }
                } else if (oauthProvider == ProviderType.GOOGLE) {
                    if (name.isNotEmpty()) {
                        viewModel.signupGoogle(
                            OAuthRegisterRequest(
                                provider = ProviderType.GOOGLE,
                                name = name,
                                email = email,
                                address = address,
                                role = role
                            )
                        )
                    } else {
                        Toast.makeText(requireContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // Sự kiện click để chọn file cho Giáo viên
        binding.btnUploadLanguageCert.root.setOnClickListener {
            Toast.makeText(requireContext(), "Mở thư viện ảnh/file", Toast.LENGTH_SHORT).show()
            certType = CertType.LANGUAGE
            pickMediaLauncher.launch("image/*")
        }

        binding.btnUploadPedagogyCert.root.setOnClickListener {
            Toast.makeText(requireContext(), "Mở thư viện ảnh/file", Toast.LENGTH_SHORT).show()
            certType = CertType.PEDAGOGY
            pickMediaLauncher.launch("image/*")
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
                        if (oauthProvider != null) {
                            Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                            findNavController().navigate(R.id.action_signupFragment_to_homeFragment)
                        }
                        viewModel.resetState()
                    }
                    is AuthState.Error -> {
                        binding.btnSignup.setText("Đăng Ký", Color.WHITE) // Khôi phục nút
                        Toast.makeText(requireContext(), "Tài khoản đã tồn tại hoặc không hợp lệ", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        viewModel.oauthGoogleResult.observe(viewLifecycleOwner) { data ->
            data?.let {
                if (data.registered) {
                    Toast.makeText(requireContext(), "Tài khoản không hợp lệ hoặc đã được sử dụng", Toast.LENGTH_SHORT).show()
                    return@observe
                }
                // Vô hiệu hóa các nút đăng ký OAuth và các UI không cần thiết
                binding.btnGoogle.visibility = View.GONE
                binding.btnFacebook.visibility = View.GONE
                binding.inputPassword.visibility = View.GONE
                binding.inputConfirmPassword.visibility = View.GONE
                binding.tvSocialLabel.visibility = View.GONE
                binding.tvEnterInformation.text = "Register with Google"

                // Tự động nhập liệu thông tin người dùng bằng dữ liệu bên thứ 3
                binding.inputEmail.setText(it.user?.email ?: "")
                binding.inputEmail.setEnable(false)

                binding.inputName.setText(it.user?.name ?: "")

                viewModel.setAccessToken(it.accessToken?: "")
                this.oauthProvider = ProviderType.GOOGLE
            }
        }
    }

    private fun navigateToOTPFragment(email: String) {
        val action = SignupFragmentDirections.actionSignupFragmentToVerifyEmail(email)

        findNavController().navigate(action)
    }
}