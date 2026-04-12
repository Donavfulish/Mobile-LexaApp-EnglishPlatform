package com.home.lexa.ui.library.personal_library

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.home.lexa.R
import com.home.lexa.core.base.BaseFragment
import com.home.lexa.databinding.FragmentPersonalLibraryBinding
import com.home.lexa.domain.models.SearchInfo
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

        if(viewModel.decks.value.isNullOrEmpty()){
            viewModel.fetchAllDecks(false,
                SearchInfo(
                query= "",
                sortBy= "",
                order= "",
                limit = 10
            ), null)
        }

        val layoutManager = LinearLayoutManager(context)
        binding.rvDecks.apply {
            adapter = deckAdapter
            this.layoutManager = layoutManager

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    if (dy > 0) {
                        val visibleItemCount = layoutManager.childCount
                        val totalItemCount = layoutManager.itemCount
                        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                        val threshold = 5
                        if (viewModel.isLoading.value == false && !viewModel.isLastPage) {
                            if ((visibleItemCount + firstVisibleItemPosition) >= (totalItemCount - threshold)) {

                                viewModel.fetchAllDecks(
                                    isLoadMore = true,
                                    searchInfo = SearchInfo(limit = 10),
                                    nextCursor = viewModel.lastId
                                )
                            }
                        }
                    }
                }
            })
        }

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
        viewModel.fetchAllDecks(true, SearchInfo(
            query= "",
            sortBy= "",
            order= "",
            limit = 10
        ), null)

    }

    override fun observeData() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.decks.observe(viewLifecycleOwner) { list ->
            deckAdapter.updateData(list)
        }
    }
}