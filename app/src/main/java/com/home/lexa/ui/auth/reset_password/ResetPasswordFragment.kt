package com.home.lexa.ui.auth.reset_password

import android.graphics.Color
import android.widget.Toast
import androidx.core.content.ContextCompat
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
    private val colorPrimary by lazy(LazyThreadSafetyMode.NONE) { ContextCompat.getColor(requireContext(), R.color.c_6200ea) }

    override fun setupViews() {
        binding.inputPassword.apply {
            setLabel("Mật khẩu mới")
            setPlaceHolderText("........")
            setHorizontalScroll()
        }

        binding.inputConfirmPassword.apply {
            setLabel("Xác nhận mật khẩu mới")
            setPlaceHolderText("........")
            setHorizontalScroll()
        }

        binding.btnResetPassword.apply {
            setText("Cập nhật mật khẩu", ContextCompat.getColor(requireContext(), R.color.c_ffffff))
            setBackground(colorPrimary)
            setOnClickAction {
                val password = binding.inputPassword.getText()
                val passwordConfirmed = binding.inputConfirmPassword.getText()

                if (password.isEmpty() || passwordConfirmed.isEmpty()) {
                    Toast.makeText(requireContext(), "Vui lòng nhập đủ dữ liệu", Toast.LENGTH_SHORT).show()
                    return@setOnClickAction
                }

                if (password != passwordConfirmed) {
                    Toast.makeText(requireContext(), "Mật khẩu xác nhận không trùng khớp", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(requireContext(), "Cập nhật mật khẩu thành công", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Cập nhật mật khẩu thất bại", Toast.LENGTH_SHORT).show()
            }

            findNavController().navigate(R.id.action_resetPasswordFragment_to_loginFragment)
        }
    }
}