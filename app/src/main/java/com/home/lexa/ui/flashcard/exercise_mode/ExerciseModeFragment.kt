package com.home.lexa.ui.flashcard.exercise_mode

import android.app.AlertDialog
import android.view.View
import androidx.navigation.fragment.findNavController
import com.home.lexa.core.base.BaseFragment
import com.home.lexa.core.utils.SwipeTouchListener
import com.home.lexa.databinding.FragmentExerciseModeBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class ExerciseModeFragment : BaseFragment<FragmentExerciseModeBinding>(FragmentExerciseModeBinding::inflate) {

    private val viewModel: ExerciseModeViewModel by viewModel()

    override fun setupViews() {
        // Hứng data từ Navigation Bundle
        val deckId = arguments?.getLong("deckId") ?: -1L
        val rememberedCount = arguments?.getInt("rememberedCount") ?: 0
        val forgottenCount = arguments?.getInt("forgottenCount") ?: 0
        val totalCards = arguments?.getInt("totalCards") ?: 0

        // Khởi tạo ViewModel bằng data nhận được
        viewModel.initInitialData(deckId, rememberedCount, forgottenCount, totalCards)

        binding.btnStop.setOnClickListener { showExitConfirmDialog() }
        binding.btnExit.setOnClickListener { showExitConfirmDialog() }
        binding.btnPracticeForgotten.setOnClickListener { viewModel.practiceForgottenWords() }
        binding.btnRetryAll.setOnClickListener { showResetConfirmDialog() }

        binding.flashcardView.setOnTouchListener(SwipeTouchListener(
            onSwipeLeft = {
                viewModel.handleSwipe(isRemembered = false)
                resetFlashcardView()
            },
            onSwipeRight = {
                viewModel.handleSwipe(isRemembered = true)
                resetFlashcardView()
            }
        ))
    }

    override fun observeData() {
        viewModel.currentCard.observe(viewLifecycleOwner) { vocab ->
            if (vocab != null) {
                binding.flashcardView.setData(vocab)
            }
        }

        viewModel.rememberedCount.observe(viewLifecycleOwner) { count ->
            binding.tvRememberedCount.text = count.toString()
            binding.tvResultRemembered.text = count.toString()
            updateResultPercentage()
        }

        viewModel.forgottenCount.observe(viewLifecycleOwner) { count ->
            binding.tvForgottenCount.text = count.toString()
            binding.tvResultForgotten.text = count.toString()
        }

        viewModel.progress.observe(viewLifecycleOwner) { currentProgress ->
            val total = viewModel.totalCards.value ?: 1
            val percentage = if (total > 0) (currentProgress * 100) / total else 0

            binding.customProgressBar.setProgressVocabulary(
                percentage = percentage,
                vocabTotal = total,
                vocabLearnning = currentProgress
            )
        }

        viewModel.isFinished.observe(viewLifecycleOwner) { isFinished ->
            if (isFinished) {
                binding.layoutPractice.visibility = View.GONE
                binding.layoutResult.visibility = View.VISIBLE
            } else {
                binding.layoutPractice.visibility = View.VISIBLE
                binding.layoutResult.visibility = View.GONE
            }
        }
    }

    private fun updateResultPercentage() {
        val rem = viewModel.rememberedCount.value ?: 0
        val total = viewModel.totalCards.value ?: 1
        val percent = if (total > 0) (rem * 100) / total else 0

        binding.tvPercentage.text = "$percent%"
        binding.tvTotalLearned.text = "$rem/$total từ đã học"
    }

    private fun resetFlashcardView() {
        binding.flashcardView.animate()
            .x(0f)
            .y(0f)
            .rotation(0f)
            .alpha(1f)
            .setDuration(0)
            .start()

        binding.flashcardView.flipFront()
    }

    private fun showResetConfirmDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Luyện lại từ đầu?")
            .setMessage("Tiến độ của bộ từ vựng này sẽ bị reset về 0%. Bạn có chắc chắn không?")
            .setPositiveButton("Đồng ý") { _, _ -> viewModel.resetAndPracticeAll() }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun showExitConfirmDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Lưu và thoát")
            .setMessage("Bạn có muốn lưu lại kết quả luyện tập không?")
            .setPositiveButton("Lưu & Thoát") { _, _ ->
                viewModel.saveProgressToApi { success ->
                    findNavController().popBackStack()
                }
            }
            .setNegativeButton("Thoát không lưu") { _, _ ->
                findNavController().popBackStack()
            }
            .setNeutralButton("Hủy", null)
            .show()
    }
}