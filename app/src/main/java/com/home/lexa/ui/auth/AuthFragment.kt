package com.home.lexa.ui.auth

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.home.lexa.core.base.BaseFragment
import com.home.lexa.databinding.FragmentAuthBinding

class AuthFragment : BaseFragment<FragmentAuthBinding>(FragmentAuthBinding::inflate) {

    override fun setupViews() {
        // Tự động mở phím khi vào màn hình
        binding.otpBar.post {
            binding.otpBar.focusAndShowKeyboard()
        }

        binding.loginBtn.setOnClickListener{
            Toast.makeText(requireContext(), "Đăng nhập", Toast.LENGTH_SHORT).show()
        }

        binding.toggleNotification.onCheckedChangeListener = {isChecked->
            if (isChecked) {
                Toast.makeText(requireContext(), "Bật", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Tắt", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun observeData() {
        // chưa có ViewModel nên để trống
    }
}