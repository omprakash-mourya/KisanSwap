package com.kisanswap.kisanswap.product.buy.viewmodel.viewModelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kisanswap.kisanswap.product.buy.repository.BuyFirebaseRepository
import com.kisanswap.kisanswap.product.buy.viewmodel.BuyViewModel

class BuyViewModelFactory(private val firebaseRepository: BuyFirebaseRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BuyViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BuyViewModel(firebaseRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}