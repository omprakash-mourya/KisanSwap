package com.kisanswap.kisanswap.product.sell.viewmodel.viewModelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kisanswap.kisanswap.product.sell.repository.SellFirebaseRepository
import com.kisanswap.kisanswap.product.sell.viewmodel.EditProductViewModel

class EditProductViewModelFactory(
    private val firebaseRepository: SellFirebaseRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditProductViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EditProductViewModel(firebaseRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}