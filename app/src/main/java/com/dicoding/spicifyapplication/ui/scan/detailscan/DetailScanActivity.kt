package com.dicoding.spicifyapplication.ui.scan.detailscan

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.spicifyapplication.databinding.ActivityDetailScanBinding

class DetailScanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailScanBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val imageUri = Uri.parse(intent.getStringExtra(EXTRA_IMAGE_URI))
        val responseLabel = intent.getStringExtra(EXTRA_RESPONSE_LABEL)
        val responseDescription = intent.getStringExtra(EXTRA_RESPONSE_DESCRIPTION)
        val responseAccuracy = intent.getStringExtra(EXTRA_RESPONSE_ACCURACY)

        binding.ivDetailImage.setImageURI(imageUri)
        binding.tvDetailNameContent.text = responseLabel
        binding.tvDetailScoreContent.text = responseAccuracy
        binding.tvDetailDescriptionContent.text = responseDescription

    }

    companion object {
        const val EXTRA_IMAGE_URI = "image"
        const val EXTRA_RESPONSE_LABEL = "label"
        const val EXTRA_RESPONSE_DESCRIPTION = "description"
        const val EXTRA_RESPONSE_ACCURACY = "accuracy"
    }
}