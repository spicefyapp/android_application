package com.dicoding.spicifyapplication.ui.dashboard.spicemart

import androidx.lifecycle.ViewModel
import com.dicoding.spicifyapplication.data.repository.SpiceRepository
import java.io.File

class SpiceMartViewModel(private val spiceRepository: SpiceRepository) : ViewModel() {
    fun getProductSpice() = spiceRepository.getProductSpice()
    fun uploadProductSpice(name: String, price: String, noWa: String, description: String, imageFile: File, lat: String, lon: String) = spiceRepository.addProductSpice(name, price, noWa, description, imageFile, lat, lon)

    fun getStoriesWithLocation() = spiceRepository.getSpicesWithLocation()
}