package com.dicoding.spicifyapplication.ui.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.spicifyapplication.data.model.UserModel
import com.dicoding.spicifyapplication.data.repository.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel (
    private val userRepository: UserRepository,
) : ViewModel() {

    fun login(email: String, password: String) = userRepository.login( email, password)
    fun setlogin(user: UserModel) {
        viewModelScope.launch {
            userRepository.saveSession(user)
        }
    }
}