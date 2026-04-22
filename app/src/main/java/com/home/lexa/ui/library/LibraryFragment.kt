package com.home.lexa.ui.library

import com.google.android.material.tabs.TabLayoutMediator
import com.home.lexa.R
import com.home.lexa.core.base.BaseFragment
import com.home.lexa.databinding.FragmentLibraryBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class LibraryFragment : BaseFragment<FragmentLibraryBinding>(FragmentLibraryBinding::inflate) {

    private val viewModel: LibraryModel by viewModel()
    
    override fun setupViews() {
        val adapter = LibraryPagerAdapter(this)
        binding.viewPager.adapter = adapter

        // Đảm bảo không load lại khi chuyển qua lại
        binding.viewPager.offscreenPageLimit = 2

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = if (position == 0) getString(R.string.favorite) else getString(R.string.personal)
        }.attach()

        binding.searchbarFilter.apply {
            onSearchAction { q ->
                viewModel.updateSearch(q)
            }
            onTextChanged { q ->
                if (q.isNotEmpty()) {
                    viewModel.getSuggestions(q)
                }
            }
            setOnSortOptionChanged { options ->
                viewModel.updateFilter(options)
            }
        }
    }

    override fun observeData() {
        viewModel.suggestions.observe(viewLifecycleOwner) { list ->
            binding.searchbarFilter.setSuggestions(list)
        }
    }

    fun navigateToTab(page: Int) {
        binding.viewPager.currentItem = page
    }
}
