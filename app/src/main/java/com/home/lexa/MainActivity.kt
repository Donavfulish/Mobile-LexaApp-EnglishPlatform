package com.home.lexa

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.home.lexa.databinding.ActivityMainBinding
import android.view.LayoutInflater
import android.widget.TextView
import androidx.activity.viewModels
import com.home.lexa.domain.models.GoogleUserInfo
import com.home.lexa.domain.models.OAuthGoogleResult
import com.home.lexa.ui.auth.login.AuthViewModel

class MainActivity : AppCompatActivity() {
    private val authViewModel: AuthViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupNavigation()

        // Kiểm tra nếu app được mở lần đầu bằng link (cold start)
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent) // Cực kỳ quan trọng để update intent mới
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        val data: Uri? = intent.data
        if (data != null && data.scheme == "lexa" && data.host == "auth-success") {
            val token = data.getQueryParameter("token") ?: ""
            val name = data.getQueryParameter("name") ?: ""
            val email = data.getQueryParameter("email") ?: ""

            // Đẩy dữ liệu vào ViewModel
            authViewModel.oauthGoogleResult.value = OAuthGoogleResult(
                accessToken = token,
                user = GoogleUserInfo(
                    email = email,
                    name = name
                )
            )
        }
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