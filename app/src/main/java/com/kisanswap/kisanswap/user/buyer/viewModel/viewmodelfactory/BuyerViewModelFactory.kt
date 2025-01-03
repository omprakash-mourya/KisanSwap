package com.kisanswap.kisanswap.user.buyer.viewModel.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kisanswap.kisanswap.user.buyer.repository.BuyerUserFirebaseRepository
import com.kisanswap.kisanswap.user.buyer.viewModel.BuyerViewModel

class BuyerViewModelFactory(private val firebaseRepository: BuyerUserFirebaseRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BuyerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BuyerViewModel(firebaseRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}