package com.home.lexa.ui.flashcard.flashcard_edit_add

import android.net.Uri
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import coil.load
import com.home.lexa.core.base.BaseFragment
import com.home.lexa.databinding.FragmentAddEditFlashcardBinding
import com.home.lexa.di.AppMemoryCache
import com.home.lexa.domain.models.ColorLabel
import com.home.lexa.domain.models.CreateFlashcardRequest
import com.home.lexa.domain.models.UpdateFlashcardRequest
import com.home.lexa.domain.models.Vocabulary
import com.home.lexa.ui.components.FlashcardMini
import org.koin.androidx.viewmodel.ext.android.viewModel

class FlashcardEditAddFragment : BaseFragment<FragmentAddEditFlashcardBinding>(FragmentAddEditFlashcardBinding::inflate) {

    private val viewModel: FlashcardEditAddViewModel by viewModel()

    private val isEditMode by lazy { arguments?.getBoolean("IS_EDIT_KEY") }
    private val word by lazy { arguments?.getString("WORD_KEY") }
    private val transcription by lazy { arguments?.getString("TRANS_KEY") }
    private val meaning by lazy { arguments?.getString("MEANING") }
    private val example by lazy { arguments?.getString("EXAMPLE_KEY") }
    private val partOfSpeech by lazy { arguments?.getString("POS_KEY") }
    private val flashcardId by lazy { arguments?.getLong("FLASHCARD_ID_KEY") }
    private val imageUrl by lazy { arguments?.getString("IMAGE_URL_KEY") }
    private val type by lazy { arguments?.getString("TYPE_KEY") }

    private val deckId by lazy { arguments?.getLong("DECK_ID_KEY") }


    private var selectedLocalImageUri: Uri? = null


    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            selectedLocalImageUri = uri
            binding.ivFlashcard.load(uri) {
                crossfade(true)
            }
        }
    }

    override fun setupViews() {
        if(flashcardId == null) return
        setupInputs()
        if(isEditMode == true){
            fillData();
        }

        setupButtons()
    }





    private fun setupInputs() {

        binding.apply {
            inputVocab.setPlaceHolderText("Ví dụ: Ephemeral")

            inputPronunciation.setPlaceHolderText("Ví dụ: əˈfem(ə)rəl")

            inputDefinition.setPlaceHolderText("Nhập định nghĩa...")

            inputExample.setPlaceHolderText("Nhập câu ví dụ...")
            inputExample.setMaxLength(100)
            inputExample.setInputHeight(100)
            inputExample.setMultipleLines(true)

            dropdownWordType.apply {
                setTile("Loại từ")
                setSelection("Chọn loại từ")
                setUpOptions(listOf("Danh từ", "Động từ", "Tính từ", "Trạng từ"))
            }

            dropdownLevel.apply {
                setTile("Level")
                setSelection("C1")
                setUpOptions(listOf("A1", "A2", "B1", "B2", "C1", "C2"))

            }
        }
    }

    private fun fillData() {
        binding.apply {
            inputVocab.setText(word)
            inputPronunciation.setText(transcription)
            inputDefinition.setText(meaning)
            inputExample.setText(example)

            if(partOfSpeech == null){
                dropdownWordType.setSelection("Chưa có")
            }else{
                dropdownWordType.setSelection(partOfSpeech!!)
            }

            ivFlashcard.load(imageUrl) {
                crossfade(true)
            }
            if(type == null){
                dropdownLevel.setSelection("Chưa có")
            }else{
                dropdownLevel.setSelection(type!!)
            }

        }
    }

    private fun setupButtons() {
        binding.btnSave.setOnClickListener {
            saveFlashcard()
        }

        binding.btnCancel.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnPreview.setOnClickListener {
            showPreview()
        }

        binding.fabAddImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    private fun saveFlashcard() {

        val inputWord = binding.inputVocab.getText().trim()
        val inputTrans = binding.inputPronunciation.getText().trim()
        val inputMeaning = binding.inputDefinition.getText().trim()
        val inputExample = binding.inputExample.getText().trim()

        val selectedLevelStr = binding.dropdownLevel.getSelection()
        val selectedPosStr = binding.dropdownWordType.getSelection()

        if (inputWord.isEmpty() || inputMeaning.isEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng nhập từ vựng và định nghĩa", Toast.LENGTH_SHORT).show()
            return
        }

        //  Map String sang ID
        val levelId = mapLevelToId(selectedLevelStr)
        val posId = mapPartOfSpeechToId(selectedPosStr)


        if (isEditMode == true) {
            val request = UpdateFlashcardRequest(
                flashcardId = flashcardId!!,
                word = inputWord,
                transcription = inputTrans,
                typeId = levelId,
                imageUrl = imageUrl,
                audioUrl = null,
                meaning = inputMeaning,
                example = inputExample,
                partOfSpeechId = posId,
                deckId = deckId!!,

            )
            viewModel.updateFlashcard(request, selectedLocalImageUri)

        } else {
            val request = CreateFlashcardRequest(
                word = inputWord,
                transcription = inputTrans,
                typeId = levelId,
                deckId = deckId!!,
                imageUrl = null,
                meaning = inputMeaning,
                example = inputExample,
                partOfSpeechId = posId
            )
            viewModel.createFlashcard(request, selectedLocalImageUri)
        }
    }

    override fun observeData() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.btnSave.isEnabled = !isLoading
            binding.btnSave.text = if (isLoading) "Đang lưu..." else "Lưu"
        }


        viewModel.saveSuccess.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                Toast.makeText(requireContext(), "Lưu thành công!", Toast.LENGTH_SHORT).show()
                AppMemoryCache.remove("getAllFlashcard_${deckId}")
                findNavController().previousBackStackEntry?.savedStateHandle?.set("RELOAD_DATA", true)
                findNavController().navigateUp()
            } else {
                Toast.makeText(requireContext(), "Có lỗi xảy ra, vui lòng thử lại", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showPreview() {

        val inputWord = binding.inputVocab.getText().trim()
        val inputTrans = binding.inputPronunciation.getText().trim()
        val inputMeaning = binding.inputDefinition.getText().trim()
        val inputExample = binding.inputExample.getText().trim()

        val selectedLevelStr = binding.dropdownLevel.getSelection()
        val selectedPosStr = binding.dropdownWordType.getSelection()


        if (inputWord.isEmpty()) {
            Toast.makeText(requireContext(), "Hãy nhập từ vựng để xem trước!", Toast.LENGTH_SHORT).show()
            return
        }


        val previewVocab = Vocabulary(
            level = ColorLabel(
                selectedLevelStr,
                "#E0E0E5"
            ),
            imageUrl = imageUrl ?: "",
            word = inputWord,
            pronunciation_url = "",
            transciption = inputTrans,
            part_of_speech = ColorLabel(selectedPosStr, "#636AE8"),
            definition = inputMeaning,
            example = inputExample
        )


        val previewCard = FlashcardMini(requireContext())
        previewCard.setData(previewVocab)
        previewCard.zoom()
    }
    //TODO: Chinh lai map
    private fun mapPartOfSpeechToId(posText: String): Int {
        return when (posText) {
            "Danh từ" -> 1
            "Động từ" -> 2
            "Tính từ" -> 3
            "Trạng từ" -> 4
            else -> 1
        }
    }

    private fun mapLevelToId(levelText: String): Int {
        return when (levelText) {
            "A1" -> 1
            "A2" -> 2
            "B1" -> 3
            "B2" -> 4
            "C1" -> 5
            "C2" -> 6
            else -> 1
        }
    }
}