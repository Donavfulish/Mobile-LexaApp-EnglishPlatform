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

        binding.dropdownLevel.setSelection("C1")
        binding.dropdownType.setSelection("Tính từ")

        binding.dropdownLevel.setUpOptions(listOf("A1", "A2", "B1", "B2", "C1", "C2"))
        binding.dropdownType.setUpOptions(listOf("Tính từ", "Động từ", "Danh từ", "Trạng từ"))

        binding.dropdownLevel.onItemSelected={selected ->
            // Xử lý khi người dùng chọn một mục trong dropdownLevel
        }

        binding.menuLanguage.setOnClickListener{
            //
        }
    }

    override fun observeData() {
        // chưa có ViewModel nên để trống
    }
}