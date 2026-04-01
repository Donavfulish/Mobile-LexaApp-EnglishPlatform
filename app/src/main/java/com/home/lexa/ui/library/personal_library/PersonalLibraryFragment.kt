package com.home.lexa.ui.library.personal_library



import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.home.lexa.R
import com.home.lexa.core.base.BaseFragment
import com.home.lexa.databinding.FragmentLoginBinding
import com.home.lexa.databinding.FragmentPersonalLibraryBinding
import com.home.lexa.domain.models.DeckDto
import com.home.lexa.ui.home.HomeViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class PersonalLibraryFragment : BaseFragment<FragmentPersonalLibraryBinding>(FragmentPersonalLibraryBinding::inflate) {
    private val viewModel: PersonalLibraryModel by viewModel()
    private val deckAdapter by lazy {
        PersonalLibraryAdapter(emptyList())
        { course ->
            findNavController().navigate(R.id.vocabularyFlashcardFragment)
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