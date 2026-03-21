package com.home.lexa

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.home.lexa.databinding.ActivityMainBinding
import android.view.LayoutInflater
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    //comment cho co
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupNavigation()

    }
    private fun setupNavigation() {

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentContainer) as NavHostFragment
        val navController = navHostFragment.navController


        binding.bottomNavigation.setOnItemSelectedListener { itemId ->
            navController.navigate(itemId)
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNavigation.setSelectedTab(destination.id)
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->

            val headerView = LayoutInflater.from(this).inflate(R.layout.logo_header, null)
            val tvTitle = headerView.findViewById<TextView>(R.id.tvHeaderTitle)

            when (destination.id) {
                R.id.homeFragment -> {
                    tvTitle.text = "Chào Alex!"
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