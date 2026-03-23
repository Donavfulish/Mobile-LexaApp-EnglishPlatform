package com.home.lexa.ui.library

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.home.lexa.ui.library.favorite_library.FavoriteLibraryFragment
import com.home.lexa.ui.library.personal_library.PersonalLibraryFragment

class LibraryPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    // Tổng số lượng tab
    override fun getItemCount(): Int = 2

    // Khởi tạo Fragment tương ứng với vị trí
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FavoriteLibraryFragment()
            else -> PersonalLibraryFragment()
        }
    }
}