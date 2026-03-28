package com.home.lexa

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.home.lexa.databinding.ActivityMainBinding
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.home.lexa.data.local.UserManager
import org.koin.android.ext.android.inject
import kotlin.getValue

class MainActivity : AppCompatActivity() {

    // binding: tuơng tác giao diện thay vì dùng findViewById
    private val userManager: UserManager by inject()
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*
        + ActivityMainBinding: là một class tự động sinh ra dựa trêm file activity_main.xml
        + inflate: hàm dịch file XML tĩnh thành các View động
        + layoutInflater: là cách mà nó thực hiện việc dịch
        + ActivityMainBinding.inflate(layoutInflater): chứa mọi thành phần giao diện dưới dạng động của file main_activity.xml
         */
        binding = ActivityMainBinding.inflate(layoutInflater)


        /*
        binding.root: thẻ gốc -> thẻ <androidx.constraintlayout.widget.ConstraintLayout
        setContentView(View view): Hiển thị giao diện view lên màn hình điện thoại
         */
        setContentView(binding.root)
        setupNavigation()

    }
    private fun setupNavigation() {

        /*
        + navHostFragment: là container chứa các Fragment của app.
        + navController: dùng để di chuyển giữa các Fragment theo nav graph.
         */
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentContainer) as NavHostFragment
        val navController = navHostFragment.navController


        /*
        + bindding.bottomNavigation: lấy thành phần bottomNavigation trong file main_activity.xml
        + setOnItemSelectedListener: hàm lắng nghe sự kiện khi ấn một item trong bottomNavigation
        + itemId: ID của tab vừa bấm
        +  navController.navigate(itemId): điều hướng chuyển đến fragment có id là itemId
         */
        binding.bottomNavigationMain.setOnItemSelectedListener { itemId ->
            navController.navigate(itemId)
        }

        /*
        + addOnDestinationChangedListener: hàm bắt sự kiện khi app chuyển sang màn hình mới
        + binding.bottomNavigation.setSelectedTab(destination.id): set lại id của tab được chọn hiện tại
         */
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNavigationMain.setSelectedTab(destination.id)
            if (destination.id == R.id.loginFragment || destination.id == R.id.signUpFragment) {
                binding.bottomNavigationMain.visibility = View.GONE
                binding.appBarLayout.visibility = View.GONE
            } else {
                binding.bottomNavigationMain.visibility = View.VISIBLE
                binding.appBarLayout.visibility = View.VISIBLE
            }
        }
        navController.addOnDestinationChangedListener { _, destination, _ ->

            val headerView = LayoutInflater.from(this).inflate(R.layout.logo_header, null)
            val tvTitle = headerView.findViewById<TextView>(R.id.tvHeaderTitle)

            when (destination.id) {
                R.id.homeFragment -> {
                    tvTitle.text = "Chào ${userManager.getUserName() ?: "Alex"}"
                    binding.appBarLayout.setBackButtonVisible(false)
                }

                R.id.libraryFragment -> {
                    tvTitle.text = "Thư viện"
                    binding.appBarLayout.setBackButtonVisible(false)
                }

                R.id.courseFragment -> {
                    tvTitle.text = "Khóa học"
                    binding.appBarLayout.setBackButtonVisible(false)
                    binding.appBarLayout.setOnClickBack()
                }

                R.id.profileFragment -> {
                    tvTitle.text = "Hồ sơ cá nhân"

                    binding.appBarLayout.setBackButtonVisible(false)
                    binding.appBarLayout.setOnClickBack()
                }
            }


            binding.appBarLayout.insertCustomeView(headerView)
        }
    }

    }

