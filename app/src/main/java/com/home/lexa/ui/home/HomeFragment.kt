package com.home.lexa.ui.home

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.home.lexa.core.base.BaseFragment
import com.home.lexa.databinding.FragmentHomeBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlinx.coroutines.launch

class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    // Koin tự động tiêm ViewModel vào đây cực gọn
    private val viewModel: HomeViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Tương tác với Custom Component LexaButton
        binding.btnLoadCourses.setText("Tải danh sách khóa học")

        binding.btnLoadCourses.setOnLexaClickListener {
            viewModel.getCourses() // Bắn event cho ViewModel xử lý
        }

        // 2. Lắng nghe State thay đổi từ ViewModel để update UI (giống useEffect)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { stateText ->
                binding.txtStatus.text = stateText
            }
        }
    }

    override fun setupViews() {
        TODO("Not yet implemented")
    }

    override fun observeData() {
        TODO("Not yet implemented")
    }
}