package com.home.lexa.ui.intro

import android.view.View
import android.widget.Toast
import com.home.lexa.R
import com.home.lexa.core.base.BaseFragment
import com.home.lexa.databinding.FragmentIntroBinding
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
            binding.btnNext.setText(data.buttonText)

            // Âu test --------------------

            binding.textInput.setIcon(R.drawable.baseline_lock_24)
            binding.textInput.setLabel("Tên khóa học")
            binding.textInput.setInputHeight(120)
            binding.textInput.setPlaceHolderText("Nhập tên khóa học...")



            // ----------------------------
        }
    }
}