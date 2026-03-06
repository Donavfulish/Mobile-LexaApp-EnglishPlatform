package com.home.lexa

import android.app.Application
import com.home.lexa.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class LexaApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Kích hoạt Koin DI khi app vừa mở lên
        startKoin {
            androidContext(this@LexaApp)
            modules(appModule)
        }
    }
}