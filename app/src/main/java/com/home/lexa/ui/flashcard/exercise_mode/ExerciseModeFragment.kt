package com.home.lexa.ui.flashcard.exercise_mode

import android.app.AlertDialog
import android.util.Log
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.home.lexa.R
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

        // Toast.makeText(requireContext(), "nhận được từ màn trước là:$rememberedCount $forgottenCount $totalCards", Toast.LENGTH_SHORT).show()
        // Cờ xác định xem đang luyện tiếp từ Cache hay là mới vào lần đầu
        val isRetryForgotten = arguments?.getBoolean("isRetryForgotten") ?: false
        val isRetryAll = arguments?.getBoolean("isRetryAll") ?: false

        // Khởi tạo ViewModel bằng data nhận được
        viewModel.initInitialData(deckId, rememberedCount, forgottenCount, totalCards, isRetryForgotten, isRetryAll)

        binding.btnStop.setOnClickListener { showExitConfirmDialog() }

        val swipeListener = SwipeTouchListener(
            onSwipeLeft = {
                viewModel.handleSwipe(isRemembered = false)
                resetFlashcardView()
            },
            onSwipeRight = {
                viewModel.handleSwipe(isRemembered = true)
                resetFlashcardView()
            }
        )

        // 2. Set listener cho FlashcardView
        binding.flashcardView.setOnTouchListener { view, event ->
            // A. XỬ LÝ CHỐNG NHIỄU SCROLLVIEW
            when (event.actionMasked) {
                android.view.MotionEvent.ACTION_DOWN -> {
                    // Khi vừa chạm ngón tay vào thẻ -> Cấm ScrollView cuộn lên xuống
                    view.parent.requestDisallowInterceptTouchEvent(true)
                }
                android.view.MotionEvent.ACTION_UP, android.view.MotionEvent.ACTION_CANCEL -> {
                    // Khi nhấc ngón tay lên -> Trả lại quyền cuộn cho ScrollView
                    view.parent.requestDisallowInterceptTouchEvent(false)
                }
            }

            // B. TRUYỀN SỰ KIỆN CHO SWIPE LISTENER XỬ LÝ TIẾP
            // Trả về true/false dựa vào kết quả xử lý của class SwipeTouchListener
            swipeListener.onTouch(view, event)
        }
    }

    override fun observeData() {
        viewModel.currentCard.observe(viewLifecycleOwner) { vocab ->
            if (vocab != null) {
                binding.flashcardView.setData(vocab)
            }
        }

        viewModel.rememberedCount.observe(viewLifecycleOwner) { count ->
            binding.tvRememberedCount.text = count.toString()
        }

        viewModel.forgottenCount.observe(viewLifecycleOwner) { count ->
            binding.tvForgottenCount.text = count.toString()
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

        // Lắng nghe sự kiện hoàn thành để chuyển sang màn hình ExerciseResultFragment
        viewModel.isFinished.observe(viewLifecycleOwner) { isFinished ->
            if (isFinished) {
                val actualDeckId = arguments?.getLong("deckId") ?: -1L
                val bundle = bundleOf(
                    "deckId" to actualDeckId,
                    "rememberedCount" to (viewModel.rememberedCount.value ?: 0),
                    "forgottenCount" to (viewModel.forgottenCount.value ?: 0),
                    "totalCards" to (viewModel.totalCards.value ?: 1)
                )
                findNavController().navigate(
                    R.id.action_exerciseModeFragment_to_exerciseResultFragment,
                    bundle
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()

        viewModel.fetchFlashcardsFromApi();
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

    private fun showExitConfirmDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Dừng luyện tập")
            .setMessage("Bạn có chắc chắn muốn dừng luyện tập không? Tiến độ hiện tại vẫn được lưu tạm.")
            .setPositiveButton("Thoát") { _, _ ->
                findNavController().popBackStack()
            }
            .setNegativeButton("Hủy", null)
            .show()
    }
}