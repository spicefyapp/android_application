package com.dicoding.spicifyapplication.ui.scan.detailscan

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dicoding.spicifyapplication.data.model.SpicePredict
import com.dicoding.spicifyapplication.databinding.ActivityDetailScanBinding

class DetailScanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailScanBinding

    private lateinit var detailViewModel: DetailScanViewModel
    override fun onCreate(savedInstanceState: Bundle?) {

        binding = ActivityDetailScanBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        val predictResult = intent.getParcelableExtra<SpicePredict>(PREDICT_RESULT) as SpicePredict
        detailViewModel = ViewModelProvider(this, DetailScanViewModelFactory.getInstance(application))[DetailScanViewModel::class.java]

        detailViewModel.getBatikRandom(predictResult.id).observe(this) {
            if (it != null) {
                binding.ivDetailImage.setImageBitmap(predictResult.image)
                binding.tvDetailNameContent.text = it.name
                binding.tvDetailScoreContent.text = "${predictResult.confidence}%"
                binding.tvDetailDescriptionContent.text = it.description
//
            }
        }

    }

    companion object {
        const val PREDICT_RESULT = "predict_result"
    }
}