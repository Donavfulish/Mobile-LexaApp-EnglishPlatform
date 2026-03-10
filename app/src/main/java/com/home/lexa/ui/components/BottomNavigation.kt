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

    // Khai báo một interface hoặc lambda để Fragment/Activity có thể lắng nghe
    private var onItemSelectedListener: ((Int) -> Unit)? = null

    init {
        setupNavigation()
    }

    private fun setupNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            // 1. Chuyển đổi trạng thái Icon (từ Viền sang Đặc)
            updateIcons(item.itemId)

            // 2. Trả sự kiện về cho màn hình đang chứa component này
            onItemSelectedListener?.invoke(item.itemId)

            true
        }
    }

    private fun updateIcons(selectedId: Int) {
        val menu = binding.bottomNavigation.menu

        // Reset tất cả về dạng viền (Outlined)
        menu.findItem(R.id.nav_home).setIcon(R.drawable.home)
        menu.findItem(R.id.nav_library).setIcon(R.drawable.book_2)
        menu.findItem(R.id.nav_courses).setIcon(R.drawable.course_menu)
        menu.findItem(R.id.nav_profile).setIcon(R.drawable.user_profile)

        // Set icon đặc (Filled) cho mục được chọn
        when (selectedId) {
            R.id.nav_home -> menu.findItem(R.id.nav_home).setIcon(R.drawable.home)
            R.id.nav_library -> menu.findItem(R.id.nav_library).setIcon(R.drawable.book_2)
            R.id.nav_courses -> menu.findItem(R.id.nav_courses).setIcon(R.drawable.course_menu)
            R.id.nav_profile -> menu.findItem(R.id.nav_profile).setIcon(R.drawable.user_profile)
        }
    }

    // Hàm để Activity/Fragment gọi vào lắng nghe sự kiện
    fun setOnItemSelectedListener(listener: (Int) -> Unit) {
        this.onItemSelectedListener = listener
    }

    // Hàm để chuyển tab bằng code (nếu cần)
    fun setSelectedTab(itemId: Int) {
        binding.bottomNavigation.selectedItemId = itemId
    }
}