package com.home.lexa.ui.library

import com.google.android.material.tabs.TabLayoutMediator
import com.home.lexa.core.base.BaseFragment
import com.home.lexa.databinding.FragmentLibraryBinding

class LibraryFragment : BaseFragment<FragmentLibraryBinding>(FragmentLibraryBinding::inflate) {

    override fun setupViews() {
        val adapter = LibraryPagerAdapter(this)
        binding.viewPager.adapter = adapter

        // Đảm bảo không load lại khi chuyển qua lại
        binding.viewPager.offscreenPageLimit = 2

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = if (position == 0) "Yêu thích" else "Cá nhân"
        }.attach()
    }

    override fun observeData() {
    }

    fun navigateToTab(page: Int) {
        binding.viewPager.currentItem = page
    }
}