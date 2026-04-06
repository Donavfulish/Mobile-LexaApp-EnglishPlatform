package com.home.lexa.ui.library.personal_library

import android.os.Bundle
import android.util.Log
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.home.lexa.R
import com.home.lexa.core.base.BaseFragment
import com.home.lexa.databinding.FragmentPersonalLibraryBinding
import com.home.lexa.ui.components.DeckInput
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class PersonalLibraryFragment : BaseFragment<FragmentPersonalLibraryBinding>(FragmentPersonalLibraryBinding::inflate) {
    private val viewModel: PersonalLibraryModel by viewModel()
    private val deckAdapter by lazy {
        PersonalLibraryAdapter(emptyList())
        { deck ->

            val bundle = Bundle().apply {
                putLong("DECK_ID_KEY", deck.id)
                putString("DECK_TITLE_KEY", deck.title)
                putInt("DECK_VOCAB_NUMBER_KEY", deck.vocabNumber)
                putString("DECK_TOPIC_NAME_KEY", deck.topic?.name ?: "")
            }

            findNavController().navigate(R.id.action_libraryFragment_to_vocabularyFlashcardFragment, bundle)
        }
    }
    override fun setupViews() {
        binding.rvDecks.apply {
            adapter = deckAdapter
            layoutManager = LinearLayoutManager(context)
        }

        viewModel.fetchAllDecks()

        binding.btnAdd.setOnClickAction {
            // Khi người dùng bấm nút +, khởi tạo và show Popup
            val popup = DeckInput(requireContext()) // Dùng 'this' nếu bạn đang ở Activity

            popup.showDialog(
                title = "Tạo bộ từ vựng mới",
                confirmText = "Tạo",
                onConfirm = { vocabName ->
                    viewModel.createDeck(vocabName)
                }
            )
        }
    }

    override fun onResume() {

        super.onResume()
        // Mỗi khi quay lại màn hình Profile (từ màn hình chỉnh sửa), gọi lại API
        Log.d("Di vao day cua personal", "Di bo");
        viewModel.fetchAllDecks()

    }

    override fun observeData() {
        viewModel.decks.observe(viewLifecycleOwner) { list ->
            deckAdapter.updateData(list)
        }
    }
}