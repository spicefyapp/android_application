package com.dicoding.spicifyapplication

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.spicifyapplication.data.model.UserModel
import com.dicoding.spicifyapplication.data.network.response.RempahItem
import com.dicoding.spicifyapplication.data.repository.UserRepository
import kotlinx.coroutines.launch

class MainViewModel (
    private val userRepository: UserRepository,
) : ViewModel() {

    fun getSession(): LiveData<UserModel> = userRepository.getSession().asLiveData()
    fun deleteLogin() {
        viewModelScope.launch { userRepository.logout() }
    }

    fun getStories() = userRepository.getSpices()

    fun searchSpices(name: String) = userRepository.searchSpices(name)

}