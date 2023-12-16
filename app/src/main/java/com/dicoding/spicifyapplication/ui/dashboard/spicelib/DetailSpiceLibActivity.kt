package com.dicoding.spicifyapplication.ui.dashboard.spicelib

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dicoding.spicifyapplication.data.network.response.RempahItem
import com.dicoding.spicifyapplication.databinding.ActivityDetailSpiceLibBinding

class DetailSpiceLibActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailSpiceLibBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailSpiceLibBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val spices = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_SPICE,RempahItem::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_SPICE)

        }
        if (spices != null) {
            setData(spices)
        }

        binding.btnBack.setOnClickListener { onBackPressed() }

    }

    private fun setData(spice: RempahItem) {
        binding.apply {
            Glide
                .with(this@DetailSpiceLibActivity)
                .load(spice.image)
                .into(ivDetailLibImage)
            tvDetailName.text = spice.name
            tvDetailDesc.text = spice.description
        }

    }

    companion object {
        const val EXTRA_SPICE = "extra_spice"
    }
}