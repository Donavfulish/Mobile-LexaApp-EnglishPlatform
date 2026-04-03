package com.home.lexa.ui.library.personal_library



import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.home.lexa.R
import com.home.lexa.core.base.BaseFragment
import com.home.lexa.databinding.FragmentLoginBinding
import com.home.lexa.databinding.FragmentPersonalLibraryBinding
import com.home.lexa.domain.models.DeckDto
import com.home.lexa.ui.home.HomeViewModel
import kotlinx.serialization.json.Json
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue
import kotlinx.serialization.encodeToString

class PersonalLibraryFragment : BaseFragment<FragmentPersonalLibraryBinding>(FragmentPersonalLibraryBinding::inflate) {
    private val viewModel: PersonalLibraryModel by viewModel()
    private val deckAdapter by lazy {
        PersonalLibraryAdapter(emptyList())
        { deck ->

            val bundle = Bundle().apply {
                putLong("DECK_ID_KEY", deck.id)
                putString("DECK_TITLE_KEY", deck.title)
                putInt("DECK_VOCAB_NUMBER_KEY", deck.vocabNumber)
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
    }

    override fun observeData() {
        viewModel.decks.observe(viewLifecycleOwner) { list ->
            deckAdapter.updateData(list)
        }
    }
}