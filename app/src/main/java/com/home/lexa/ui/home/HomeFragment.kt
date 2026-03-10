package com.home.lexa.ui.home

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.home.lexa.R
import com.home.lexa.core.base.BaseFragment
import com.home.lexa.databinding.FragmentHomeBinding
import com.home.lexa.ui.components.Popup
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlinx.coroutines.launch

class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    // Koin tự động tiêm ViewModel vào đây cực gọn
    private val viewModel: HomeViewModel by viewModel()

    override fun setupViews() {
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

        binding.progressBar.setProgress(80);
        binding.tag.setTagData("Ngày 04", "#6A65E9", true)
        binding.smallRing.setProgress(40)
        binding.headerSection.setHeaderData("Danh sách khóa học", R.drawable.ic_language)
        binding.cardKhoaHoc.setCardData(R.drawable.ic_language, R.drawable.ic_language, "ccccc")

    }

    override fun observeData() {
        //
    }
}