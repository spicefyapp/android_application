package com.dicoding.spicifyapplication.ui.scan

import androidx.lifecycle.ViewModel
import com.dicoding.spicifyapplication.data.repository.UploadRepository
import java.io.File

class ScanViewModel(private val repository: UploadRepository) : ViewModel() {
    fun uploadImageSpice(file: File) = repository.uploadImageSpice(file)
}