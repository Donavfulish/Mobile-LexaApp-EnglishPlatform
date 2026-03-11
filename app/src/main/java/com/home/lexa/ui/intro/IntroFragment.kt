package com.home.lexa.ui.intro

import android.view.View
import android.widget.Toast
import com.home.lexa.core.base.BaseFragment
import com.home.lexa.databinding.FragmentIntroBinding
import com.home.lexa.ui.components.PrimaryButton
import org.koin.androidx.viewmodel.ext.android.viewModel

class IntroFragment : BaseFragment<FragmentIntroBinding>(FragmentIntroBinding::inflate) {

    // Inject ViewModel bằng Koin (giữ nguyên)
    private val viewModel: IntroViewModel by viewModel()

    override fun setupViews() {
        // 1. Gắn sự kiện (Thay thế cho setupActions)
        binding.btnNext.setOnClickAction {
            // Lưu ý: Trong Fragment, dùng requireContext() thay cho 'this'
            Toast.makeText(requireContext(), "Chuyển sang màn Login!", Toast.LENGTH_SHORT).show()
            // Xử lý navigate sang LoginFragment ở đây
        }

        val newButton = PrimaryButton(requireContext())
        newButton.id = View.generateViewId()
        newButton.setText("Hello", null)
        newButton.setOnClickAction {
            Toast.makeText(requireContext(), "Chào thế giới nhé", Toast.LENGTH_SHORT).show()
        }

        binding.topBar.insertCustomeView(newButton)

        // 2. Gọi API lấy dữ liệu khi mở màn hình
        viewModel.loadIntro()
    }

    override fun observeData() {
        // Lưu ý: Trong Fragment, BẮT BUỘC dùng 'viewLifecycleOwner' thay cho 'this'
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnNext.visibility = if (isLoading) View.GONE else View.VISIBLE
        }

        viewModel.introData.observe(viewLifecycleOwner) { data ->
            binding.tvIntroTitle.text = data.title
            binding.tvIntroDesc.text = data.description
            binding.btnNext.setText(data.buttonText, null)
        }
    }
}