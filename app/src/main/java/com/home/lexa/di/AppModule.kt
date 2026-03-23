package com.home.lexa.di

import com.home.lexa.core.network.AuthInterceptor
import com.home.lexa.data.local.TokenManager
import com.home.lexa.data.remote.AuthApiService
import com.home.lexa.data.remote.CourseApiService
import com.home.lexa.data.repository.AuthRespositoryImpl
import com.home.lexa.data.repository.CourseRepositoryImpl
import com.home.lexa.data.repository.DeckRepositoryImpl
import com.home.lexa.data.repository.IntroRepositoryImpl
import com.home.lexa.domain.repository.AuthRespository
import com.home.lexa.domain.repository.CourseRepository
import com.home.lexa.domain.repository.DeckRepository
import com.home.lexa.domain.repository.IntroRepository
import com.home.lexa.ui.auth.login.AuthViewModel
import com.home.lexa.ui.home.HomeViewModel
import com.home.lexa.ui.intro.IntroViewModel
import com.home.lexa.ui.library.favorite_library.FavoriteLibraryModel
import com.home.lexa.ui.library.personal_library.PersonalLibraryModel
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {

    // 1. Khởi tạo Retrofit (Cốt lõi mạng)
    single {TokenManager(androidContext())}

    single{AuthInterceptor(get())}
    single{
        OkHttpClient.Builder()
            .addInterceptor(get<AuthInterceptor>())
            .build()
    }
    single {
        Retrofit.Builder()
            // IP này trỏ về localhost:8081 của máy tính từ máy ảo Android
            .baseUrl("http://10.0.2.2:8081/")
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // 2. Khởi tạo Api Service (Koin gọi get() để lấy cái Retrofit ở trên nhét vào)
    single {
        get<Retrofit>().create(CourseApiService::class.java)
    }
    single {
        get<Retrofit>().create(AuthApiService::class.java)
    }
    single {
        get<Retrofit>().create(com.home.lexa.data.remote.DeckApiService::class.java)
    }

    // 3. Khởi tạo Repository
    // Koin dùng get() để lấy CourseApiService nhét vào CourseRepositoryImpl
    single<CourseRepository> { CourseRepositoryImpl(get()) }
    single<IntroRepository> { IntroRepositoryImpl() }
    single<AuthRespository>{ AuthRespositoryImpl(get()) }
    single<DeckRepository>{ DeckRepositoryImpl(get()) }

    // 4. Khởi tạo ViewModel (Koin lấy Repository tương ứng nhét vào)
    viewModel { HomeViewModel(get()) }
    viewModel { IntroViewModel(get()) }
    viewModel { AuthViewModel(get(), get()) }
    viewModel { FavoriteLibraryModel(get()) }
    viewModel { PersonalLibraryModel(get()) }


}