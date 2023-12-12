package com.dicoding.spicifyapplication.data.repository

import androidx.lifecycle.liveData
import com.dicoding.spicifyapplication.data.datastore.UserPreference
import com.dicoding.spicifyapplication.data.model.UserModel
import com.dicoding.spicifyapplication.data.network.retrofit.ApiService
import com.dicoding.spicifyapplication.helper.ResultState
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException
import java.io.IOException

class UserRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference
){
    fun register(name: String, email: String, password: String) = liveData {
        emit(ResultState.Loading)
        try {
            val responseBody = apiService.register(name, email, password)
            emit(ResultState.Success(responseBody))
        } catch (e: HttpException) {
            emit(ResultState.Error( "Terjadi kesalahan"))
        } catch (e: IOException) {
            emit(ResultState.Error("Terjadi kesalahan jaringan"))
        }
    }

    fun login(email: String, password: String) = liveData {
        emit(ResultState.Loading)
        try {
            val responseBody = apiService.login(email,password)
            emit(ResultState.Success(responseBody))
        } catch (e: HttpException) {
            emit(ResultState.Error( "Terjadi kesalahan"))
        } catch (e: IOException) {
            emit(ResultState.Error("Terjadi kesalahan jaringan"))
        }
    }

    suspend fun saveSession(user: UserModel){
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout(){
        userPreference.logout()
    }


    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(apiService: ApiService,userPreference: UserPreference ) : UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(apiService,userPreference)
            }.also { instance = it }
    }

}