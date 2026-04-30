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
import androidx.compose.foundation.layout.Row
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorLong
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
import com.home.lexa.ui.auth.verify_email.VERIFY_PURPOSE
import com.home.lexa.ui.utils.DateUtils
import com.home.lexa.ui.utils.MediaUtils
import com.home.lexa.ui.utils.StringUtils

enum class CertType { LANGUAGE, PEDAGOGY }
class SignupFragment : BaseFragment<FragmentSignupBinding>(FragmentSignupBinding::inflate) {
    private val viewModel: AuthViewModel by activityViewModel()

    private val colorPrimary by lazy(LazyThreadSafetyMode.NONE) { ContextCompat.getColor(requireContext(), R.color.c_6200ea) }
    private val colorLightPrimary by lazy(LazyThreadSafetyMode.NONE) { ContextCompat.getColor(requireContext(), R.color.c_f8f4ff) } // Màu nền tím nhạt khi được chọn
    private val colorTextDark by lazy(LazyThreadSafetyMode.NONE) { ContextCompat.getColor(requireContext(), R.color.c_333333) }
    private val colorBorder by lazy(LazyThreadSafetyMode.NONE) { ContextCompat.getColor(requireContext(), R.color.c_e0e0e0) }
    private val colorInactiveText by lazy(LazyThreadSafetyMode.NONE) { ContextCompat.getColor(requireContext(), R.color.c_888888) }

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
            binding.tvTitle.text = getString(R.string.signup_teacher_title)
            binding.tvSubtitle.text = getString(R.string.signup_teacher_desc)

            // --- GIÁO VIÊN ACTIVE ---
            binding.btnRoleTeacher.setTextColor(colorPrimary)
            binding.btnRoleTeacher.iconTint = ColorStateList.valueOf(colorPrimary) //R.color.lexa_primary)
            binding.btnRoleTeacher.strokeColor = ColorStateList.valueOf(colorPrimary)
            binding.btnRoleTeacher.backgroundTintList = ColorStateList.valueOf(colorLightPrimary) // Đổi nền tím nhạt

            // --- HỌC SINH INACTIVE ---
            binding.btnRoleStudent.setTextColor(colorInactiveText)
            binding.btnRoleStudent.setIconTintResource(R.color.gray_888888)
            binding.btnRoleStudent.strokeColor = ContextCompat.getColorStateList(requireContext(), R.color.gray_E0E0E5)
            binding.btnRoleStudent.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.c_ffffff)) // Đổi nền trắng

            binding.llTeacherFields.visibility = View.VISIBLE

        } else {
            binding.tvTitle.text = getString(R.string.signup_title)
            binding.tvSubtitle.text = getString(R.string.signup_desc)

            // --- HỌC SINH ACTIVE ---
            binding.btnRoleStudent.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.lexa_primary)
            )
            binding.btnRoleStudent.setIconTintResource(R.color.lexa_primary)
            binding.btnRoleStudent.strokeColor = ContextCompat.getColorStateList(requireContext(), R.color.lexa_primary)
            binding.btnRoleStudent.backgroundTintList = ColorStateList.valueOf(colorLightPrimary) // Đổi nền tím nhạt

            // --- GIÁO VIÊN INACTIVE ---
            binding.btnRoleTeacher.setTextColor(colorInactiveText)
            binding.btnRoleTeacher.setIconTintResource(R.color.gray_888888)
            binding.btnRoleTeacher.strokeColor = ContextCompat.getColorStateList(requireContext(), R.color.gray_E0E0E5)
            binding.btnRoleTeacher.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.c_ffffff)) // Đổi nền trắng

            binding.llTeacherFields.visibility = View.GONE
        }
    }

    private fun updateMediaUI(uri: Uri, container: LinearLayout) {
        val imgPreview = container.findViewById<ImageView>(R.id.imgPreview)
        val txtFileName = container.findViewById<TextView>(R.id.txtFileName)
        val icUpload = container.findViewById<ImageView>(R.id.icUpload)
        val txtInstruction = container.findViewById<TextView>(R.id.txtStatus)
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
            setText(getString(R.string.signup_with_google), colorTextDark)
            setBackground(ContextCompat.getColor(requireContext(), R.color.c_ffffff))
            setStroke(1, colorBorder)
            setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_google))
            setOnClickAction {
                val intent = Intent(Intent.ACTION_VIEW, GoogleUrls.loginUri)
                startActivity(intent)
            }
        }

        binding.btnFacebook.apply {
            setText("Facebook", colorTextDark)
            setBackground(ContextCompat.getColor(requireContext(), R.color.c_ffffff))
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
            setHorizontalScroll()
            setMaxLength(255)
        }

        binding.inputName.apply {
            setLabel(getString(R.string.name) + " *")
            setPlaceHolderText(getString(R.string.example_name))
            setIcon(R.drawable.user_profile)
            setHorizontalScroll()
            setMaxLength(255)

        }

        binding.inputDob.apply {
            setLabel(getString(R.string.birth_date))
            setPlaceHolderText("dd/mm/yyyy")
            setIcon(R.drawable.ic_calendar)
            setHorizontalScroll()

            // TODO: Mở DatePickerDialog khi click
        }

        binding.inputAddress.apply {
            setMultipleLines(true)
            setLabel(getString(R.string.address))
            setPlaceHolderText(getString(R.string.enter_your_address))
            setIcon(R.drawable.ic_location)
            setHorizontalScroll()

        }

        binding.inputPassword.apply {
            setLabel(getString(R.string.password) + " *")
            setPlaceHolderText("........")
            setHorizontalScroll()

        }

        binding.inputConfirmPassword.apply {
            setLabel(getString(R.string.confirm_password) + " *")
            setPlaceHolderText("........")
            setHorizontalScroll()

        }
    }

    private fun setupSignupButton() {
        binding.btnSignup.apply {
            setText(getString(R.string.signup), Color.WHITE)
            setBackground(colorPrimary)
            setOnClickAction {
                val name = binding.inputName.getText()
                val role = if (isTeacherRoleSelected) UserRole.TEACHER else UserRole.STUDENT
                val password = binding.inputPassword.getText()
                val confirmPassword = binding.inputConfirmPassword.getText()
                val email = binding.inputEmail.getText()
                val date_of_birth = binding.inputDob.getText()
                val address = binding.inputAddress.getText()
                val isGuaranteed = binding.cbCommitment.isChecked

                if (!StringUtils.isValidEmail(email)) {
                    Toast.makeText(requireContext(), getString(R.string.toast_wrong_email_format), Toast.LENGTH_SHORT).show()
                    return@setOnClickAction
                }

                if (!password.isEmpty() && password != confirmPassword) {
                    Toast.makeText(requireContext(), getString(R.string.toast_confirm_password_not_match), Toast.LENGTH_SHORT).show()
                    return@setOnClickAction
                }

                if (!date_of_birth.isEmpty() && !DateUtils.isValidDate(date_of_birth)) {
                    Toast.makeText(requireContext(), getString(R.string.toast_wrong_birthday_format), Toast.LENGTH_SHORT).show()
                    return@setOnClickAction
                }

                if (role == UserRole.TEACHER) {
                    if (binding.btnUploadLanguageCert.icUpload.isVisible) {
                        Toast.makeText(requireContext(), getString(R.string.toast_please_provide_language_cert), Toast.LENGTH_SHORT).show()
                        return@setOnClickAction
                    }
                    if (!isGuaranteed) {
                        Toast.makeText(requireContext(), getString(R.string.please_confirm_qualifications), Toast.LENGTH_SHORT).show()
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

                        viewModel.resetState()
                    } else {
                        Toast.makeText(requireContext(), getString(R.string.toast_please_enter_all_information), Toast.LENGTH_SHORT).show()
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
                        Toast.makeText(requireContext(), getString(R.string.toast_please_enter_all_information), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.btnUploadLanguageCert.root.setOnClickListener {
            Toast.makeText(requireContext(), getString(R.string.open_media_library), Toast.LENGTH_SHORT).show()
            certType = CertType.LANGUAGE
            pickMediaLauncher.launch("image/*")
        }

        binding.btnUploadPedagogyCert.root.setOnClickListener {
            Toast.makeText(requireContext(), getString(R.string.open_media_library), Toast.LENGTH_SHORT).show()
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
                        // TODO: Đổi hiệu ứng chữ Đang xử lý... thành vòng xoay"
                        binding.btnSignup.setEnabledState(false)
                    }
                    is AuthState.Success -> {
                        if (oauthProvider != null) {
                            Toast.makeText(requireContext(), getString(R.string.toast_signup_successfully), Toast.LENGTH_SHORT).show()
                            findNavController().navigate(R.id.action_signupFragment_to_homeFragment)
                        }

                        viewModel.resetOAuth()
                        viewModel.resetState()

                        val email = binding.inputEmail.getText()
                        viewModel.sendOTP(email)
                        navigateToOTPFragment(email)
                    }
                    is AuthState.Error -> {
                        binding.btnSignup.setEnabledState(true)
                        Toast.makeText(requireContext(), getString(R.string.toast_email_already_used), Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        viewModel.oauthGoogleResult.observe(viewLifecycleOwner) { data ->
            data?.let {
                if (data.registered) {
                    Toast.makeText(requireContext(), getString(R.string.toast_account_already_used), Toast.LENGTH_SHORT).show()
                    viewModel.resetOAuth()
                    viewModel.logout()
                    return@observe
                }
                // Vô hiệu hóa các nút đăng ký OAuth và các UI không cần thiết
                binding.btnGoogle.visibility = View.GONE
                binding.btnFacebook.visibility = View.GONE
                binding.inputPassword.visibility = View.GONE
                binding.inputConfirmPassword.visibility = View.GONE
                binding.tvSocialLabel.visibility = View.GONE
                binding.tvEnterInformation.text = getString(R.string.signup_with_google)

                binding.inputPassword.setText(null)
                binding.inputConfirmPassword.setText(null)

                // Tự động nhập liệu thông tin người dùng bằng dữ liệu bên thứ 3 (Google)
                binding.inputEmail.setText(it.user?.email ?: "")
                binding.inputEmail.setEnable(false)

                binding.inputName.setText(it.user?.name ?: "")

                viewModel.setAccessToken(it.accessToken?: "")
                this.oauthProvider = ProviderType.GOOGLE
            }
        }
    }

    private fun navigateToOTPFragment(email: String) {
        val action = SignupFragmentDirections.actionSignupFragmentToVerifyEmail(email, VERIFY_PURPOSE.TO_HOME.toString())

        findNavController().navigate(action)
    }
}