package com.dicoding.spicifyapplication.ui.profile.about

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dicoding.spicifyapplication.R

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        supportActionBar?.hide()
    }
}