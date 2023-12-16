package com.dicoding.spicifyapplication.di

import android.content.Context
import com.dicoding.spicifyapplication.data.datastore.UserPreference
import com.dicoding.spicifyapplication.data.datastore.dataStore
import com.dicoding.spicifyapplication.data.network.retrofit.ApiConfig
import com.dicoding.spicifyapplication.data.repository.SpiceRepository
import com.dicoding.spicifyapplication.data.repository.UploadRepository
import com.dicoding.spicifyapplication.data.repository.UserRepository

object Injection {
    fun provideRepository(): UploadRepository {
        val apiService = ApiConfig.getApiService1()
        return UploadRepository.getInstance(apiService)
    }

    fun provideSpiceRepository(): SpiceRepository {
        val apiService = ApiConfig.getApiService2()
        return SpiceRepository.getInstance(apiService)
    }

    fun provideUserRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        return UserRepository.getInstance(apiService,pref)
    }
}