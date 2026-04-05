package com.home.lexa

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.home.lexa.databinding.ActivityMainBinding
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import com.home.lexa.core.network.AuthEvent
import com.home.lexa.core.network.AuthEventBus
import com.home.lexa.data.local.UserManager
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import kotlin.getValue
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.home.lexa.data.local.TokenManager
import com.home.lexa.data.remote.AuthApiService
import com.home.lexa.data.repository.AuthRepositoryImpl
import com.home.lexa.di.AppMemoryCache
import com.home.lexa.domain.models.GoogleUserInfo
import com.home.lexa.domain.models.OAuthGoogleResult
import com.home.lexa.ui.auth.AuthState
import com.home.lexa.ui.auth.AuthViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    private val authViewModel: AuthViewModel by viewModel()
    private var isFirstAuthenticated: Boolean = false

    // binding: tuơng tác giao diện thay vì dùng findViewById
    private val userManager: UserManager by inject()
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition { !isFirstAuthenticated }

        authViewModel.getMe()

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

        observeData()

        handleIntent(intent)

        listenToLogout()

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
            if (destination.id == R.id.loginFragment || destination.id == R.id.signUpFragment || destination.id == R.id.verifyEmail) {
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
            val registered = data.getQueryParameter("registered").toBoolean()

            // Đẩy dữ liệu vào ViewModel
            authViewModel.oauthGoogleResult.value = OAuthGoogleResult(
                accessToken = token,
                user = GoogleUserInfo(
                    email = email,
                    name = name
                ),
                registered = registered
            )
        }
    }

    private fun listenToLogout() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                AuthEventBus.events.collect { event ->
                    if (event == AuthEvent.LOGOUT || event == AuthEvent.TOKEN_EXPIRED) {
                        AppMemoryCache.clearAll()
                        authViewModel.logout()

                        val navHostFragment = supportFragmentManager
                            .findFragmentById(R.id.fragmentContainer) as? NavHostFragment
                        val navController = navHostFragment?.navController

                        navController?.let {
                            // Sửa dòng navigate thành thế này để ép nó hiểu là dùng Resource ID
                            it.navigate(resId = R.id.loginFragment, args = null, navOptions = navOptions {
                                popUpTo(it.graph.id) {
                                    inclusive = true
                                }
                            })
                        }

                        if (event == AuthEvent.TOKEN_EXPIRED) {
                            Toast.makeText(
                                this@MainActivity,
                                "Phiên đăng nhập hết hạn.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private fun observeData() {
        this.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                authViewModel.loginState.collect { state ->
                    if (isFirstAuthenticated || state is AuthState.Idle || state is AuthState.Loading) return@collect

                    val navHostFragment = supportFragmentManager
                        .findFragmentById(R.id.fragmentContainer) as NavHostFragment
                    val navController = navHostFragment.navController

                    val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)

                    when (state) {
                        is AuthState.Success -> {
                            isFirstAuthenticated = true
                            authViewModel.resetState()
                            navGraph.setStartDestination(R.id.homeFragment)
                        }

                        is AuthState.Error -> {
                            isFirstAuthenticated = true
                            authViewModel.resetState()
                            navGraph.setStartDestination(R.id.loginFragment)
                        }

                        else -> {}
                    }

                    navController.graph = navGraph
                    navController.setGraph(navGraph, null)
                }
            }
        }
    }
}

