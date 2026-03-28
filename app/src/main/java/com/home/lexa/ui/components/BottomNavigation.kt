package com.home.lexa.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.home.lexa.R
import com.home.lexa.databinding.BottomNavigationBinding

class BottomNavigation @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = BottomNavigationBinding.inflate(LayoutInflater.from(context), this, true)

    private var onItemSelectedListener: ((Int) -> Unit)? = null

    init {
        setupNavigation()
    }

    private fun setupNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            updateIcons(item.itemId)
            onItemSelectedListener?.invoke(item.itemId)

            true
        }
    }

    private fun updateIcons(selectedId: Int) {
        val menu = binding.bottomNavigation.menu


        menu.findItem(R.id.homeFragment).setIcon(R.drawable.ic_home)
        menu.findItem(R.id.libraryFragment).setIcon(R.drawable.ic_book_2)
        menu.findItem(R.id.courseFragment).setIcon(R.drawable.ic_course_menu)
        menu.findItem(R.id.profileFragment).setIcon(R.drawable.user_profile)


        val selectedItem = menu.findItem(selectedId)
        when (selectedId) {
            R.id.homeFragment -> selectedItem.setIcon(R.drawable.ic_home)
            R.id.libraryFragment -> selectedItem.setIcon(R.drawable.ic_book_2)
            R.id.courseFragment -> selectedItem.setIcon(R.drawable.ic_course_menu)
            R.id.profileFragment -> selectedItem.setIcon(R.drawable.user_profile)
        }
    }

    // Hàm để Activity/Fragment gọi vào lắng nghe sự kiện
    fun setOnItemSelectedListener(listener: (Int) -> Unit) {
        this.onItemSelectedListener = listener
    }


    fun setSelectedTab(itemId: Int) {

        binding.bottomNavigation.setOnItemSelectedListener(null)
        binding.bottomNavigation.selectedItemId = itemId
        updateIcons(itemId)

        setupNavigation()
    }
}