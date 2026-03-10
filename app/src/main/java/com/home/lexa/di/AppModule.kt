package com.home.lexa.di

import com.home.lexa.data.repository.CourseRepository
import com.home.lexa.data.repository.IntroRepositoryImpl
import com.home.lexa.domain.repository.IntroRepository
import com.home.lexa.ui.home.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import com.home.lexa.ui.intro.IntroViewModel
import org.koin.dsl.module

val appModule = module {
    // Khởi tạo Repository (1 instance duy nhất - Singleton)
    single { CourseRepository() }
    single<IntroRepository> {
        IntroRepositoryImpl()
    }
    //single<Arepo>{
    //    ArepoImpl()
    //}

    // Khởi tạo ViewModel, Koin sẽ tự tìm và nhét CourseRepository vào constructor
    viewModel { HomeViewModel(get()) }
    viewModel { IntroViewModel(get()) }
}