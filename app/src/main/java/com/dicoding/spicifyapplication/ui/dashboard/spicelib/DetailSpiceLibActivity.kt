package com.dicoding.spicifyapplication.ui.dashboard.spicelib

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.spicifyapplication.databinding.ActivityDetailSpiceLibBinding

class DetailSpiceLibActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailSpiceLibBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailSpiceLibBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { onBackPressed() }


    }
}