package com.dicoding.spicifyapplication.ui.scan.detailscan

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.dicoding.spicifyapplication.data.local.Spice
import com.dicoding.spicifyapplication.data.repository.SpiceDetectRepository

class DetailScanViewModel(private val repository: SpiceDetectRepository) : ViewModel() {
    fun getBatikRandom(id: Int): LiveData<Spice?> = repository.getSpiceDetail(id)
}