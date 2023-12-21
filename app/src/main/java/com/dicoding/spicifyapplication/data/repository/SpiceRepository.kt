package com.dicoding.spicifyapplication.data.repository

import androidx.lifecycle.liveData
import com.dicoding.spicifyapplication.data.network.retrofit.ApiService
import com.dicoding.spicifyapplication.helper.ResultState
import com.google.gson.Gson
import retrofit2.HttpException
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