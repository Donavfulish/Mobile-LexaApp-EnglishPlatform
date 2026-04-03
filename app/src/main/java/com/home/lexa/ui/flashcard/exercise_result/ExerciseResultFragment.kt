package com.home.lexa.ui.flashcard.exercise_result

import android.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.home.lexa.R
import com.home.lexa.core.base.BaseFragment
import com.home.lexa.databinding.FragmentExerciseResultBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class ExerciseResultFragment : BaseFragment<FragmentExerciseResultBinding>(FragmentExerciseResultBinding::inflate) {

    private val viewModel: ExerciseResultViewModel by viewModel()

    private var deckId: Long = -1
    private var rememberedCount = 0
    private var forgottenCount = 0
    private var totalCards = 0

    override fun setupViews() {
        deckId = arguments?.getLong("deckId") ?: -1L
        rememberedCount = arguments?.getInt("rememberedCount") ?: 0
        forgottenCount = arguments?.getInt("forgottenCount") ?: 0
        totalCards = arguments?.getInt("totalCards") ?: 1 // Tránh chia 0

        // Gắn data lên UI
        val percentage = (rememberedCount * 100) / totalCards
        binding.progressRing.setProgress(percentage)

        binding.tvTotalLearned.text = "$rememberedCount/$totalCards từ đã học"
        binding.tvResultRemembered.text = rememberedCount.toString()
        binding.tvResultForgotten.text = forgottenCount.toString()

        binding.btnPracticeForgotten.setOnClickListener { navigateBackToPractice(isRetryForgotten = true) }
        binding.btnRetryAll.setOnClickListener { showResetConfirmDialog() }
        binding.btnExit.setOnClickListener { showExitConfirmDialog() }
    }

    override fun observeData() {}

    private fun navigateBackToPractice(isRetryForgotten: Boolean) {
        val bundle = bundleOf(
            "deckId" to deckId,
            "rememberedCount" to if (isRetryForgotten) rememberedCount else 0,
            "forgottenCount" to if (isRetryForgotten) forgottenCount else totalCards,
            "totalCards" to totalCards,
            "isRetryForgotten" to isRetryForgotten,
            "isRetryAll" to !isRetryForgotten
        )
        findNavController().navigate(R.id.action_exerciseResultFragment_to_exerciseModeFragment, bundle)
    }

    private fun showResetConfirmDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Luyện lại từ đầu?")
            .setMessage("Tiến độ của bộ từ vựng này sẽ bị reset về 0%. Bạn có chắc chắn không?")
            .setPositiveButton("Đồng ý") { _, _ -> navigateBackToPractice(isRetryForgotten = false) }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun showExitConfirmDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Lưu và thoát")
            .setMessage("Bạn có muốn lưu lại kết quả luyện tập không?")
            .setPositiveButton("Lưu & Thoát") { _, _ ->
                viewModel.saveProgressToApi(deckId) {
                    // Pop ngược về màn hình chi tiết bộ từ vựng
                    findNavController().popBackStack(R.id.vocabularyFlashcardFragment, false)
                }
            }
            .setNegativeButton("Thoát không lưu") { _, _ ->
                findNavController().popBackStack(R.id.vocabularyFlashcardFragment, false)
            }
            .setNeutralButton("Hủy", null)
            .show()
    }
}