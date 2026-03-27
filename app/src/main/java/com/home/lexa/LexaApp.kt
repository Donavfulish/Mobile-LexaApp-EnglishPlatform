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
            /*
            androidContext: Cung cấp Context cuả toàn bộ ứng dụng. Nhờ vậy các class bên dưới như
            TokenManager hay UserManager mới dùng chung cho toàn bộ app đc
             */
            androidContext(this@LexaApp)

            modules(appModule)
        }
    }
}