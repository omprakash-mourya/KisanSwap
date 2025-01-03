package com.kisanswap.kisanswap.savedProducts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kisanswap.kisanswap.roomDataBase.ProductDao

class SavedProductViewModelFactory(private val productDao: ProductDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SavedViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SavedViewModel(productDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}