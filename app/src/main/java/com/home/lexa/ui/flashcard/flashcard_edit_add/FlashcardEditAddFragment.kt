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
import com.home.lexa.domain.models.WordUiState
import com.home.lexa.ui.components.FlashcardMini
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch
import com.home.lexa.BuildConfig
import com.home.lexa.MainActivity
import com.home.lexa.R
import com.home.lexa.domain.models.PartOfSpeech
import com.home.lexa.domain.models.VocabType

class FlashcardEditAddFragment : BaseFragment<FragmentAddEditFlashcardBinding>(FragmentAddEditFlashcardBinding::inflate) {

    private val viewModel: FlashcardEditAddViewModel by viewModel()

    private val isEditMode by lazy { arguments?.getBoolean("IS_EDIT_KEY") }
    private val word by lazy { arguments?.getString("WORD_KEY") }
    private val transcription by lazy { arguments?.getString("TRANS_KEY") }
    private val meaning by lazy { arguments?.getString("MEANING") }
    private val example by lazy { arguments?.getString("EXAMPLE_KEY") }
    private val partOfSpeechId by lazy { arguments?.getInt("POS_ID_KEY") }
    private val flashcardId by lazy { arguments?.getLong("FLASHCARD_ID_KEY") }
    private val imageUrl by lazy { arguments?.getString("IMAGE_URL_KEY") }
    private val type by lazy { arguments?.getString("TYPE_KEY") }

    private val deckId by lazy { arguments?.getLong("DECK_ID_KEY") }
    private val activityBinding by lazy { (requireActivity() as MainActivity).binding }

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
        val topBarTitle = if (isEditMode == true) getString(R.string.edit_flashcard) else getString(
            R.string.create_flashcard
        )
        activityBinding.appBarLayout.apply {
            setText(topBarTitle);
        }
        setupButtons()
        setupIpaKeyboard()
    }





    private fun setupInputs() {

        binding.apply {
            inputVocab.setPlaceHolderText(getString(R.string.example)+": Ephemeral")

            inputPronunciation.setPlaceHolderText(getString(R.string.example)+": əˈfem(ə)rəl")

            inputDefinition.setPlaceHolderText(getString(R.string.enter_defination))

            inputExample.setPlaceHolderText(getString(R.string.enter_example))
            inputExample.setMaxLength(100)
            inputExample.setInputHeight(100)
            inputExample.setMultipleLines(true)

            dropdownWordType.apply {
                setTile(getString(R.string.word_type))
                val options = PartOfSpeech.getLocalizedNames(requireContext())
                setUpOptions(options)
                setSelection(options.first())
            }

            dropdownLevel.apply {
                setTile(context.getString(R.string.level))
                val options = VocabType.getLocalizedNames(requireContext())
                setUpOptions(options)
                setSelection(options.first())
            }
        }

    }

    private fun fillData() {
        binding.apply {
            inputVocab.setText(word)
            inputPronunciation.setText(transcription)
            inputDefinition.setText(meaning)
            inputExample.setText(example)

            if(partOfSpeechId == null){
                dropdownWordType.setSelection(getString(R.string.pos_none))
            }else{
                val posEnum = PartOfSpeech.fromId(partOfSpeechId!!)
                if (posEnum != null) {
                    dropdownWordType.setSelection(getString(posEnum.nameRes))
                }
            }

            ivFlashcard.load(imageUrl) {
                crossfade(true)
            }
            if (type == null) {
                dropdownLevel.setSelection(getString(VocabType.NONE.nameRes))
            } else {

                val enumValue = try {
                    VocabType.valueOf(type!!)
                } catch (e: Exception) {
                    VocabType.NONE
                }
                dropdownLevel.setSelection(getString(enumValue.nameRes))
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



        binding.btnAiSuggest.setOnClickListener {
            val word = binding.inputVocab.getText().trim()
            val meaning = binding.inputDefinition.getText().trim()
            val pos = binding.dropdownWordType.getSelection()
            if (word.isEmpty() || meaning.isEmpty()) {
                Toast.makeText(requireContext(),
                    getString(R.string.content_toast_enter_flashcard), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            viewModel.fetchAiExampleSuggestion(word, meaning, pos)
        }
        binding.btnPhoneticSuggest.setOnClickListener {
            val word = binding.inputVocab.getText().trim()
            val pos = binding.dropdownWordType.getSelection()
            if (word.isEmpty() || pos.isEmpty()) {
                Toast.makeText(requireContext(),
                    getString(R.string.enter_vocabulary), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            viewModel.fetchPhonetic(word, pos)
        }
    }

    private fun saveFlashcard() {

        val inputWord = binding.inputVocab.getText().trim()
        var inputTrans = binding.inputPronunciation.getText().trim()

        if (inputTrans.isNotEmpty()) {
            if (!inputTrans.startsWith("/")) {
                inputTrans = "/$inputTrans"
            }
            if (!inputTrans.endsWith("/")) {
                inputTrans = "$inputTrans/"
            }
        }
        val inputMeaning = binding.inputDefinition.getText().trim()
        val inputExample = binding.inputExample.getText().trim()

        val selectedPosStr = binding.dropdownWordType.getSelection()

        if (inputWord.isEmpty() || inputMeaning.isEmpty()) {
            Toast.makeText(requireContext(),
                getString(R.string.enter_vocabulary_defination), Toast.LENGTH_SHORT).show()
            return
        }


        val selectedLevelStr = binding.dropdownLevel.getSelection()
        val vocabTypeEnum = VocabType.fromLocalizedName(requireContext(), selectedLevelStr)
        val levelId = vocabTypeEnum.ordinal
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
            binding.btnSave.text = if (isLoading) getString(R.string.saving) else getString(R.string.save)
        }


        viewModel.saveSuccess.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                Toast.makeText(requireContext(),
                    getString(R.string.save_successfully), Toast.LENGTH_SHORT).show()
                AppMemoryCache.remove("getAllFlashcard_${deckId}")
                findNavController().previousBackStackEntry?.savedStateHandle?.set("RELOAD_DATA", true)
                findNavController().navigateUp()
            } else {
                Toast.makeText(requireContext(), getString(R.string.error_retry), Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.uiState.observe(viewLifecycleOwner){ state ->
            when (state) {
                is WordUiState.Loading -> {
                    binding.btnAiSuggest.isEnabled = false
                    binding.loadingOverlay.visibility = android.view.View.VISIBLE
                }
                is WordUiState.Success -> {
                    binding.loadingOverlay.visibility = android.view.View.GONE
                    binding.btnAiSuggest.isEnabled = true
                    val data  = state.data
                    binding.inputExample.setText(data)
                }

                is WordUiState.Error -> {
                    binding.btnAiSuggest.isEnabled = true
                    binding.loadingOverlay.visibility = android.view.View.GONE
                    Toast.makeText(context, getString(R.string.error_retry), Toast.LENGTH_SHORT).show()

                }
                else -> {
                    binding.loadingOverlay.visibility = android.view.View.GONE
                }
            }
        }
        viewModel.phoneticState.observe(viewLifecycleOwner) { result ->
            when (result) {
                "ERROR" -> {
                    Toast.makeText(requireContext(), getString(R.string.error_retry), Toast.LENGTH_SHORT).show()
                }
                null -> { /* Idle */ }
                else -> {
                    binding.inputPronunciation.setText(result)
                }
            }
        }
    }

    private fun showPreview() {

        val inputWord = binding.inputVocab.getText().trim()

        var inputTrans = binding.inputPronunciation.getText().trim()

        if (inputTrans.isNotEmpty()) {
            if (!inputTrans.startsWith("/")) inputTrans = "/$inputTrans"
            if (!inputTrans.endsWith("/")) inputTrans = "$inputTrans/"
        }
        val inputMeaning = binding.inputDefinition.getText().trim()
        val inputExample = binding.inputExample.getText().trim()

        val selectedLevelStr = binding.dropdownLevel.getSelection()
        val selectedPosStr = binding.dropdownWordType.getSelection()


        if (inputWord.isEmpty()) {
            Toast.makeText(requireContext(),
                getString(R.string.enter_word_to_preview), Toast.LENGTH_SHORT).show()
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

    private fun mapPartOfSpeechToId(posText: String): Int {
        return PartOfSpeech.getIdFromLocalizedName(requireContext(), posText)
    }

    private fun setupIpaKeyboard() {

        val ipaSymbols = listOf("ˈ", "ˌ", "ː", "ə", "æ", "ʌ", "ɒ", "ɪ", "ʊ", "ɔ", "ɜ", "ɑ", "ɛ",
            "θ", "ð", "ʃ", "ʒ", "ŋ", "dʒ", "tʃ")

        binding.layoutIpaKeyboard.removeAllViews()


        for (symbol in ipaSymbols) {
            val button = layoutInflater.inflate(R.layout.item_ipa_button, binding.layoutIpaKeyboard, false) as MaterialButton
            button.text = symbol
            button.setOnClickListener {
                binding.inputPronunciation.insertTextAtCursor(symbol)
            }
            binding.layoutIpaKeyboard.addView(button)
        }
    }
}