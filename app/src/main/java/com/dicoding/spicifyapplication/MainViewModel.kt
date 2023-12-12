package com.dicoding.spicifyapplication

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.spicifyapplication.data.model.UserModel
import com.dicoding.spicifyapplication.data.repository.UserRepository
import kotlinx.coroutines.launch

class MainViewModel (
    private val userRepository: UserRepository,
) : ViewModel() {
    fun getSession(): LiveData<UserModel> = userRepository.getSession().asLiveData()
    fun setlogin(user: UserModel) {
        viewModelScope.launch {
            userRepository.saveSession(user)
        }
    }
    fun deleteLogin() { viewModelScope.launch { userRepository.logout() } }
}