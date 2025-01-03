package com.kisanswap.kisanswap.home.viewmodel.viewModelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kisanswap.kisanswap.home.repository.HomeFirebaseRepository
import com.kisanswap.kisanswap.home.viewmodel.HomeViewModel

class HomeViewModelFactory(private val firebaseRepository: HomeFirebaseRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(firebaseRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}