package com.home.lexa.ui.intro

import android.view.View
import android.widget.Toast
import androidx.core.view.forEach
import androidx.lifecycle.viewModelScope
import com.home.lexa.core.base.BaseFragment
import com.home.lexa.data.repository.VocabularyRepository
import com.home.lexa.databinding.FragmentIntroBinding
import com.home.lexa.domain.models.ColorLabel
import com.home.lexa.domain.models.Vocabulary
import com.home.lexa.ui.components.FlashcardMini
import com.home.lexa.ui.components.PrimaryButton
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class IntroFragment : BaseFragment<FragmentIntroBinding>(FragmentIntroBinding::inflate) {

    // Inject ViewModel bằng Koin (giữ nguyên)
    private val viewModel: IntroViewModel by viewModel()

    private var isFlashcardEditable = false

    override fun setupViews() {
        // 1. Gắn sự kiện (Thay thế cho setupActions)
       /* binding.btnNext.setOnClickAction {
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
        viewModel.loadIntro()*/
        binding.mainFlashcard.setData(Vocabulary(
            ColorLabel("C1", "#6366F1"),
            0,
            "Ephemeral",
            "",
            "/əˈfem(ə)rəl/",
            ColorLabel("adjective", "#6366F1"),
            "lasting for a very short time and until dawn.",
            "The autumnal colors are beautiful but ephemeral, are beautiful but ephemeral."
        ))

        binding.btnSwitch.setOnClickListener {
            isFlashcardEditable = !isFlashcardEditable
            binding.cardGrid.forEach { item ->
                (item as? FlashcardMini)?.setIsEditable(isFlashcardEditable)
            }
        }
    }

    override fun observeData() {
        // Lưu ý: Trong Fragment, BẮT BUỘC dùng 'viewLifecycleOwner' thay cho 'this'
        /*viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnNext.visibility = if (isLoading) View.GONE else View.VISIBLE
        }

        viewModel.introData.observe(viewLifecycleOwner) { data ->
            binding.tvIntroTitle.text = data.title
            binding.tvIntroDesc.text = data.description
            binding.btnNext.setText(data.buttonText, null)
        }*/
    }
}