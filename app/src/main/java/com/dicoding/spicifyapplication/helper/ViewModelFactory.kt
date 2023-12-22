package com.dicoding.spicifyapplication.helper

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.spicifyapplication.MainViewModel
import com.dicoding.spicifyapplication.data.repository.SpiceRepository
import com.dicoding.spicifyapplication.data.repository.UploadRepository
import com.dicoding.spicifyapplication.data.repository.UserRepository
import com.dicoding.spicifyapplication.di.Injection
import com.dicoding.spicifyapplication.ui.auth.login.LoginViewModel
import com.dicoding.spicifyapplication.ui.auth.register.RegisterViewModel
import com.dicoding.spicifyapplication.ui.chatbot.ChatBotViewModel
import com.dicoding.spicifyapplication.ui.dashboard.spicelib.SpiceLibViewModel
import com.dicoding.spicifyapplication.ui.scan.ScanViewModel

class ViewModelFactory (private val repository: UploadRepository, private val userRepository: UserRepository,private val spiceRepository: SpiceRepository) :
    ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        when {
            modelClass.isAssignableFrom(ScanViewModel::class.java) -> {
                ScanViewModel(repository) as T
            }
            modelClass.isAssignableFrom(ChatBotViewModel::class.java) -> {
                ChatBotViewModel(repository) as T
            }
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(SpiceLibViewModel::class.java) -> {
                SpiceLibViewModel(spiceRepository) as T
            }
            else -> throw Throwable("Unknown ViewModel class: " + modelClass.name)
        }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        fun getInstance(context: Context): ViewModelFactory =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: ViewModelFactory(
                    Injection.provideRepository(),
                    Injection.provideUserRepository(context),
                    Injection.provideSpiceRepository()
                    )
            }
    }
}