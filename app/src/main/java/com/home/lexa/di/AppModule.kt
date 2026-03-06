package com.home.lexa.di

import com.home.lexa.data.repository.CourseRepository
import com.home.lexa.ui.home.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Khởi tạo Repository (1 instance duy nhất - Singleton)
    single { CourseRepository() }

    // Khởi tạo ViewModel, Koin sẽ tự tìm và nhét CourseRepository vào constructor
    viewModel { HomeViewModel(get()) }
}