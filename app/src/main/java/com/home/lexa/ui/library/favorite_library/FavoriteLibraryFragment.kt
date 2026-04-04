package com.home.lexa.ui.library.favorite_library

import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.home.lexa.R
import com.home.lexa.core.base.BaseFragment
import com.home.lexa.databinding.FragmentFavoriteLibraryBinding
import com.home.lexa.ui.library.LibraryFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class FavoriteLibraryFragment : BaseFragment<FragmentFavoriteLibraryBinding>(FragmentFavoriteLibraryBinding::inflate) {
    private val viewModel: FavoriteLibraryModel by viewModel()
    private val deckAdapter by lazy {
        FavoriteLibraryAdapter(emptyList())
        { course ->
            findNavController().navigate(R.id.courseDetailFragment)
        }
    }

    override fun setupViews() {
        binding.headerSection.setHeaderData(
            title = "BỘ TỪ VỰNG YÊU THÍCH",
            actionText = "Xem tất cả",
            onActionClick = {}
        )

        binding.tvGoToPersonal.setOnClickListener {
            (parentFragment as? LibraryFragment)?.let { libraryFragment ->
                libraryFragment.navigateToTab(1)
            }
        }

        binding.rvCourses.apply {
            adapter = deckAdapter
            layoutManager = LinearLayoutManager(context)
        }

        viewModel.fetchAllCourses()
    }

    override fun observeData() {
        viewModel.courses.observe(viewLifecycleOwner) { list ->
            deckAdapter.updateData(list)
        }
    }
}