package com.dicoding.spicifyapplication.ui.dashboard.spicemart

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.spicifyapplication.databinding.ActivitySpiceMartBinding

class SpiceMartActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySpiceMartBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySpiceMartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this,AddProductActivity::class.java))
        }

    }
}