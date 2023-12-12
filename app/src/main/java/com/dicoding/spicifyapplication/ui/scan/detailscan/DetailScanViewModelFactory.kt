package com.dicoding.spicifyapplication.ui.scan.detailscan

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.spicifyapplication.data.repository.SpiceDetectRepository

class DetailScanViewModelFactory private constructor(private val repository: SpiceDetectRepository) :
    ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        when {
            modelClass.isAssignableFrom(DetailScanViewModel::class.java) -> {
                DetailScanViewModel(repository) as T
            }
            else -> throw Throwable("Unknown ViewModel class: " + modelClass.name)
        }

    companion object {
        @Volatile
        private var INSTANCE: DetailScanViewModelFactory? = null

        fun getInstance(application: Application): DetailScanViewModelFactory =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: DetailScanViewModelFactory(
                    SpiceDetectRepository.getInstance(application)
                )
            }
    }
}