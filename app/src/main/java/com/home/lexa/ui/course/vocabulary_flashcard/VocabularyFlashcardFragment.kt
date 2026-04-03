package com.home.lexa.ui.course.vocabulary_flashcard



import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.home.lexa.R
import com.home.lexa.core.base.BaseFragment
import com.home.lexa.databinding.FragmentVocabularyFlashcardBinding
import com.home.lexa.domain.models.ColorLabel
import com.home.lexa.domain.models.DeckDto
import com.home.lexa.domain.models.Vocabulary
import com.home.lexa.ui.components.FlashcardMini
import org.koin.androidx.viewmodel.ext.android.viewModel


class VocabularyFlashcardFragment : BaseFragment<FragmentVocabularyFlashcardBinding>(FragmentVocabularyFlashcardBinding::inflate) {
    private val viewModel: VocabularyFlashcardViewModel by viewModel()
    val deck = DeckDto(
        id= 1,
        title= "TOEFL Essentials",
        vocabNumber = 5,
        createdAt = "2 Ngày trước"
    )
    private var vocabLearning = 0
    override fun setupViews() {
        binding.deckTitle.text = deck.title

        binding.startBtn.apply {
            setIconPadding(10)
            setTextSize(18f)
            setText("Bắt đầu luyện tập", ContextCompat.getColor(requireContext(), R.color.white))
            setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_play_circle))
            setIconColor(ContextCompat.getColor(requireContext(), R.color.white))
            setBackground(ContextCompat.getColor(requireContext(), R.color.purple_paragraph))

            setOnClickAction {
                Log.d("LEXA_DEBUG", "Đã ấn nút startBtn 1")
                navigateToExerciseMode()
            }
        }
        binding.startBtn2.apply {
            setIconPadding(10)
            setTextSize(18f)
            setText("Bắt đầu luyện tập", ContextCompat.getColor(requireContext(), R.color.white))
            setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_play_circle))
            setIconColor(ContextCompat.getColor(requireContext(), R.color.white))
            setBackground(ContextCompat.getColor(requireContext(), R.color.purple_paragraph))


            setOnClickAction {
                Log.d("LEXA_DEBUG", "Đã ấn nút startBtn 2")
                navigateToExerciseMode()
            }
        }
        binding.vocabularyIconBtn.apply {
            setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_book)!!)
            setBackground(ContextCompat.getColor(requireContext(), R.color.purple_paragraph))
        }
        binding.searchBarVocabulary.apply {
            setIconColor(ContextCompat.getColor(requireContext(), R.color.purple_paragraph))
            setTextSearch("Tìm kiếm từ vựng...")
        }
        binding.editToggle.onCheckedChangeListener = { isChecked ->
            for (i in 0 until binding.vocabularyGrid.childCount) {
                val child = binding.vocabularyGrid.getChildAt(i)
                if (child is FlashcardMini) {
                    child.setIsEditable(isChecked)
                }
            }
        }
        viewModel.loadFlashcardDetail(deck.id)
    }

    private fun navigateToExerciseMode() {
        Log.d("LEXA_DEBUG", "Hàm navigateToExerciseMode được gọi")

        try {
            val forgotten = deck.vocabNumber - vocabLearning
            Log.d("LEXA_DEBUG", "Dữ liệu chuẩn bị chuyển: deckId=${deck.id}, rem=$vocabLearning, forg=$forgotten, total=${deck.vocabNumber}")

            val bundle = bundleOf(
                "deckId" to deck.id,
                "rememberedCount" to vocabLearning,
                "forgottenCount" to forgotten,
                "totalCards" to deck.vocabNumber
            )

            Log.d("LEXA_DEBUG", "Bắt đầu gọi findNavController().navigate...")
            findNavController().navigate(
                R.id.action_vocabularyFlashcardFragment_to_exerciseModeFragment,
                bundle
            )
            Log.d("LEXA_DEBUG", "Chuyển trang thành công!")

        } catch (e: Exception) {
            // Nếu có lỗi do Nav Graph chưa mapping đúng, nó sẽ văng vào đây
            Log.e("LEXA_DEBUG", "LỖI CHUYỂN TRANG: ${e.message}", e)
            Toast.makeText(requireContext(), "Lỗi chuyển trang: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun observeData() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.shimmerLayout.startShimmer()
                binding.shimmerLayout.visibility = View.VISIBLE
                binding.contentScroll.visibility = View.GONE
            } else {
                binding.shimmerLayout.stopShimmer()
                binding.shimmerLayout.visibility = View.GONE
                binding.contentScroll.visibility = View.VISIBLE
            }
        }

        viewModel.flashcardDetailData.observe(viewLifecycleOwner) { flashcards ->
            if (flashcards.isNullOrEmpty()) return@observe
            val vocabNumber = deck.vocabNumber
            val percentage = vocabLearning * 100 / vocabNumber
            binding.progressText.text = "Tiến độ: ${percentage}%"
            binding.progress.setProgressVocabulary(percentage, vocabNumber, vocabLearning)
            binding.flashcardNum.text = "${flashcards.size}"
            binding.vocabularyGrid.removeAllViews()
            flashcards.forEach { item ->
                val card = FlashcardMini(requireContext())
                val vocab = Vocabulary(
                    level = ColorLabel(item.type, "#E0E0E5"),
                    image = 0,
                    word = item.word,
                    pronunciation_url = item.audioUrl ?: "",
                    transciption = item.transcription,
                    part_of_speech = ColorLabel(item.partOfSpeech, "#636AE8"),
                    definition = item.meaning,
                    example = item.example ?: ""
                )
                card.setData(vocab)

                val params = androidx.gridlayout.widget.GridLayout.LayoutParams().apply {
                    width = 0
                    height = androidx.gridlayout.widget.GridLayout.LayoutParams.WRAP_CONTENT
                    columnSpec = androidx.gridlayout.widget.GridLayout.spec(androidx.gridlayout.widget.GridLayout.UNDEFINED, 1f)
                    setMargins(16, 16, 16, 16)
                }
                card.layoutParams = params
                binding.vocabularyGrid.addView(card)
            }
        }

        viewModel.deckResultData.observe(viewLifecycleOwner){result ->
            if (result != null){
                vocabLearning = result.rememberedCount!!
            }
        }
    }
}