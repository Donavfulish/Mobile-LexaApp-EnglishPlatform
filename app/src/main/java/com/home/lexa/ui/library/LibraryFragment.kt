package com.home.lexa.ui.library

import com.google.android.material.tabs.TabLayoutMediator
import com.home.lexa.core.base.BaseFragment
import com.home.lexa.databinding.FragmentLibraryBinding
import com.home.lexa.ui.library.favorite_library.FavoriteLibraryModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class LibraryFragment : BaseFragment<FragmentLibraryBinding>(FragmentLibraryBinding::inflate) {

    private val viewModel: LibraryModel by viewModel()
    
    override fun setupViews() {
        val adapter = LibraryPagerAdapter(this)
        binding.viewPager.adapter = adapter

        // Đảm bảo không load lại khi chuyển qua lại
        binding.viewPager.offscreenPageLimit = 2

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = if (position == 0) "Yêu thích" else "Cá nhân"
        }.attach()

        binding.searchbarFilter.apply {
            onSearchAction { q ->
                viewModel.updateSearch(q)
            }
            setOnSortOptionChanged { options ->
                viewModel.updateFilter(options)
            }
        }
    }

    override fun observeData() {
    }

    fun navigateToTab(page: Int) {
        binding.viewPager.currentItem = page
    }
}
