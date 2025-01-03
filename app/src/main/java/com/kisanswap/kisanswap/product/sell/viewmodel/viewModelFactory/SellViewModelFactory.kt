package com.kisanswap.kisanswap.product.sell.viewmodel.viewModelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kisanswap.kisanswap.product.sell.repository.SellFirebaseRepository
import com.kisanswap.kisanswap.product.sell.viewmodel.SellViewModel

class SellViewModelFactory(private val firebaseRepository: SellFirebaseRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SellViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SellViewModel(firebaseRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}