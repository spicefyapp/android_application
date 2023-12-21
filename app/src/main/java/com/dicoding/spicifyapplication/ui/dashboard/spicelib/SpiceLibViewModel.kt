package com.dicoding.spicifyapplication.ui.dashboard.spicelib

import androidx.lifecycle.ViewModel
import com.dicoding.spicifyapplication.data.repository.SpiceRepository
import java.io.File

class SpiceLibViewModel(private val spiceRepository: SpiceRepository) : ViewModel() {
    fun getSpices() = spiceRepository.getSpices()
    fun searchSpices(name: String) = spiceRepository.searchSpices(name)

}