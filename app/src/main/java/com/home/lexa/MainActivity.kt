package com.home.lexa

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.home.lexa.ui.home.HomeFragment
import com.home.lexa.ui.intro.IntroFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}