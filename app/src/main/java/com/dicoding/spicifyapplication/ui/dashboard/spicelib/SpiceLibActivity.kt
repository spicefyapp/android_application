package com.dicoding.spicifyapplication.ui.dashboard.spicelib

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.spicifyapplication.databinding.ActivitySpiceLibBinding

class SpiceLibActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySpiceLibBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySpiceLibBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        binding.btnBack.setOnClickListener { onBackPressed() }


    }
}