package com.dicoding.spicifyapplication.ui.profile.about

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dicoding.spicifyapplication.R
import com.dicoding.spicifyapplication.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAboutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        binding.btnBack.setOnClickListener { onBackPressed() }
    }
}