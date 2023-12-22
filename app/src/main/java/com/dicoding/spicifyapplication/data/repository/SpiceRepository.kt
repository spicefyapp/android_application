package com.dicoding.spicifyapplication.data.repository

import androidx.lifecycle.liveData
import com.dicoding.spicifyapplication.data.network.retrofit.ApiService
import com.dicoding.spicifyapplication.helper.ResultState
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File
import java.io.IOException

class SpiceRepository private constructor(
    private val apiService: ApiService
) {
    fun getSpices() = liveData {
        emit(ResultState.Loading)
        try {
            val responseBody = apiService.getSpices()
            emit(ResultState.Success(responseBody.rempah))
        } catch (e: HttpException) {
            emit(ResultState.Error( "Terjadi kesalahan"))
        } catch (e: IOException) {
            emit(ResultState.Error("Terjadi kesalahan jaringan. Gagal menghubungkan server. Coba lagi"))
        }
    }

    fun searchSpices(name: String) = liveData {
        emit(ResultState.Loading)
        try {
            val responseBody = apiService.searchSpices(name)
            emit(ResultState.Success(responseBody.rempah))
        } catch (e: HttpException) {
            emit(ResultState.Error( "Terjadi kesalahan"))
        } catch (e: IOException) {
            emit(ResultState.Error("Terjadi kesalahan jaringan. Gagal menghubungkan server. Coba lagi"))
        }
    }

    fun getProductSpice() = liveData {
        emit(ResultState.Loading)
        try {
            val responseBody = apiService.getProductSpice()
            emit(ResultState.Success(responseBody.rempah))
        } catch (e: HttpException) {
            emit(ResultState.Error( "Terjadi kesalahan"))
        } catch (e: IOException) {
            emit(ResultState.Error("Terjadi kesalahan jaringan. Gagal menghubungkan server. Coba lagi"))
        }
    }

    fun addProductSpice(name: String, price: String, noWa: String, description: String, imageFile: File, lat: String, lon: String) = liveData {
        emit(ResultState.Loading)
        val name = name.toRequestBody("text/plain".toMediaType())
        val price = price.toRequestBody("text/plain".toMediaType())
        val noWa = noWa.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val description = description.toRequestBody("text/plain".toMediaType())
        val latitude = lat.toRequestBody("text/plain".toMediaType())
        val longtitude = lon.toRequestBody("text/plain".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "image",
            imageFile.name,
            requestImageFile
        )
        try {
            val successResponse = apiService.uploadProductSpice(multipartBody,name,price,noWa,description,latitude,longtitude)
            emit(ResultState.Success(successResponse))
        } catch (e: HttpException) {
            emit(ResultState.Error( "Terjadi kesalahan"))
        } catch (e: IOException) {
            emit(ResultState.Error("Terjadi kesalahan jaringan. Gagal menghubungkan server. Coba lagi"))
        }
    }

    fun getSpicesWithLocation() = liveData {
        emit(ResultState.Loading)
        try {
            val responseBody = apiService.getSpiceWithLocation()
            emit(ResultState.Success(responseBody.rempah))
        } catch (e: HttpException) {
            emit(ResultState.Error( "Terjadi kesalahan"))
        } catch (e: IOException) {
            emit(ResultState.Error("Terjadi kesalahan jaringan. Gagal menghubungkan server. Coba lagi"))
        }
    }

    companion object {
        @Volatile
        private var instance: SpiceRepository? = null

        fun getInstance(
            apiService: ApiService
        ) : SpiceRepository =
            instance ?: synchronized(this) {
                instance ?: SpiceRepository(apiService)
            }.also { instance = it }
    }
}