package com.home.lexa.ui.course.vocabulary_flashcard

import android.util.Log
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.home.lexa.R
import com.home.lexa.core.base.BaseFragment
import com.home.lexa.databinding.FragmentVocabularyFlashcardBinding
import com.home.lexa.domain.models.ColorLabel
import com.home.lexa.domain.models.DeckDto
import com.home.lexa.domain.models.DetailFlashcardWithResult
import com.home.lexa.domain.models.Topic
import com.home.lexa.domain.models.UpdateDeckRequest
import com.home.lexa.domain.models.Vocabulary
import com.home.lexa.ui.components.FlashcardMini
import com.home.lexa.ui.components.Popup
import kotlinx.coroutines.selects.select
import org.koin.androidx.viewmodel.ext.android.viewModel

class VocabularyFlashcardFragment : BaseFragment<FragmentVocabularyFlashcardBinding>(FragmentVocabularyFlashcardBinding::inflate) {
    private val viewModel: VocabularyFlashcardViewModel by viewModel()

    private var vocabLearning = 0
    private val deckId by lazy { arguments?.getLong("DECK_ID_KEY") }
    private val deckTitle by lazy { arguments?.getString("DECK_TITLE_KEY") }
    private val deckVocabNum by lazy { arguments?.getInt("DECK_VOCAB_NUMBER_KEY") }
    private val deckTopicName by lazy { arguments?.getString("DECK_TOPIC_NAME_KEY") }

    private var deckTopics: List<Topic> = listOf()
    private var topicColorMap: MutableMap<String, String> = mutableMapOf<String, String>()

    override fun setupViews() {

        if(deckId == null || deckId == 0L) return

        binding.deckTitle.text = deckTitle

        binding.diTopic.apply {
            setTile("Chủ đề")
            onItemSelected =  { topicName ->
                setFrameColor(topicColorMap[topicName] ?: "#FFFFFF")
                viewModel.updateDeck(UpdateDeckRequest(
                    deckId = deckId!!,
                    topicName = binding.diTopic.getSelection()
                ))
            }
        }

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
        binding.vocabularyIconBtn.setOnClickAction {
            val bundle = Bundle().apply {
                putBoolean("IS_EDIT_KEY", false)
                putLong("DECK_ID_KEY", deckId!!)

            }
            findNavController().navigate(R.id.action_vocabularyFlashcardFragment_to_flashcardAddEditFragment,bundle)
        }


        viewModel.loadFlashcardsWithResult(deckId!!)
        viewModel.loadTopics()
    }

    override fun onResume() {
        super.onResume()
        if (deckId == null) return

        viewModel.loadFlashcardDetail(deckId!!);
    }
    private fun navigateToExerciseMode() {
        Log.d("LEXA_DEBUG", "Hàm navigateToExerciseMode được gọi")

        try {
            val forgotten = deckVocabNum?.minus(vocabLearning)
            Log.d("LEXA_DEBUG", "Dữ liệu chuẩn bị chuyển: deckId=${deckId}, rem=$vocabLearning, forg=$forgotten, total=${deckVocabNum}")

            val bundle = bundleOf(
                "deckId" to deckId,
                "rememberedCount" to vocabLearning,
                "forgottenCount" to forgotten,
                "totalCards" to deckVocabNum
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
        viewModel.flashcardWithResultData.observe(viewLifecycleOwner) { list ->
            renderFlashcards(list)
            updateProgress(list.size)
        }

        viewModel.deckResultData.observe(viewLifecycleOwner){result ->
            if (result != null){
                vocabLearning = result.rememberedCount!!
            }
        }

        viewModel.topicData.observe(viewLifecycleOwner) { topics ->
            deckTopics = topics
            binding.diTopic.setUpOptions(topics.map { it ->
                topicColorMap[it.name] = it.colorHex
                it.name
            })
            binding.diTopic.setSelection(deckTopicName ?: "None")
            binding.diTopic.setFrameColor(topicColorMap[deckTopicName] ?: "#FFFFFF")
        }

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>("RELOAD_DATA")
            ?.observe(viewLifecycleOwner) { shouldReload ->
                if (shouldReload) {

                    viewModel.loadFlashcardDetail(deckId!!)
                    findNavController().currentBackStackEntry?.savedStateHandle?.remove<Boolean>("RELOAD_DATA")
                }
            }
    }
    private fun renderFlashcards(list: List<DetailFlashcardWithResult>) {
        binding.flashcardNum.text = "${list.size}"
        binding.vocabularyGrid.removeAllViews()


        vocabLearning = list.count { it.result == "REMEMBER" }

        list.forEach { item ->
            val card = FlashcardMini(requireContext())
            val vocab = Vocabulary(
                level = ColorLabel(item.flashCard.type, "#E0E0E5"),
                word = item.flashCard.word,
                pronunciation_url = item.flashCard.audioUrl ?: "",
                transciption = item.flashCard.transcription,
                part_of_speech = ColorLabel(item.flashCard.partOfSpeech, "#636AE8"),
                definition = item.flashCard.meaning,
                example = item.flashCard.example ?: "",
                image = 0,
            )
            card.setData(vocab)
            card.setIsEditable(binding.editToggle.isChecked)

            card.onDeleteClick = {
                Popup(requireContext()).showDialog(
                    title = "Xóa từ vựng",
                    subTitle = "Bạn có chắc muốn xóa '${item.flashCard.word}'?",
                    isWarning = true,
                    confirmText = "Xóa",
                    onConfirm = { viewModel.deleteFlashcard(item.flashCard.id, deckId!!) }
                )
            }

            card.onEditClick = {
                val bundle = Bundle().apply {
                    putBoolean("IS_EDIT_KEY", true)
                    putString("WORD_KEY", item.flashCard.word)
                    putString("TRANS_KEY", item.flashCard.transcription)
                    putString("MEANING", item.flashCard.meaning)
                    putString("EXAMPLE_KEY", item.flashCard.example)
                    putString("POS_KEY", item.flashCard.partOfSpeech)
                    putLong("FLASHCARD_ID_KEY", item.flashCard.id)
                    putString("IMAGE_URL_KEY", item.flashCard.imageUrl)
                    putString("TYPE_KEY", item.flashCard.type)
                    putLong("DECK_ID_KEY", deckId!!)

                }
                findNavController().navigate(R.id.action_vocabularyFlashcardFragment_to_flashcardAddEditFragment, bundle)
            }

            val params = androidx.gridlayout.widget.GridLayout.LayoutParams().apply {
                width = 0
                columnSpec = androidx.gridlayout.widget.GridLayout.spec(androidx.gridlayout.widget.GridLayout.UNDEFINED, 1f)
                setMargins(16, 16, 16, 16)
            }
            card.layoutParams = params
            binding.vocabularyGrid.addView(card)
        }
    }

    private fun updateProgress(total: Int) {
        if (total > 0) {
            val percentage = (vocabLearning * 100) / total
            binding.progressText.text = "Tiến độ: $percentage%"
            binding.progress.setProgressVocabulary(percentage, total, vocabLearning)
        } else {
            binding.progressText.text = "Tiến độ: 0%"
            binding.progress.setProgressVocabulary(0, 0, 0)
        }
    }
}