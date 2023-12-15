package com.dicoding.spicifyapplication.data.repository

import androidx.lifecycle.liveData
import com.dicoding.spicifyapplication.data.model.ChatModel
import com.dicoding.spicifyapplication.data.network.retrofit.ApiService
import com.dicoding.spicifyapplication.helper.ResultState
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import java.io.File
import java.io.IOException

class UploadRepository private constructor(
    private val apiService: ApiService
) {

    fun uploadImageSpice(file: File) = liveData {
        emit(ResultState.Loading)
        val requestImageFile = file.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "file",
            file.name,
            requestImageFile
        )
        try {
            val successResponse = apiService.uploadImageSpice(multipartBody)
            emit(ResultState.Success(successResponse))
        } catch (e: HttpException) {
            emit(ResultState.Error( "Terjadi kesalahan"))
        } catch (e: IOException) {
            emit(ResultState.Error("Terjadi kesalahan jaringan, Coba lagi"))
        }
    }

    fun chatWithBot(chat: String) = liveData {
        emit(ResultState.Loading)

        try {
            val successResponse = apiService.chatWithTheBit(chat)
            emit(ResultState.Success(ChatModel(successResponse.spiceBotReply,true)))
        } catch (e: HttpException) {
            emit(ResultState.Error( "Terjadi kesalahan"))
        } catch (e: IOException) {
            emit(ResultState.Error("Terjadi kesalahan jaringan"))
        }
    }

    companion object {
        @Volatile
        private var instance: UploadRepository? = null
        fun getInstance(apiService: ApiService) =
            instance ?: synchronized(this) {
                instance ?: UploadRepository(apiService)
            }.also { instance = it }
    }
}