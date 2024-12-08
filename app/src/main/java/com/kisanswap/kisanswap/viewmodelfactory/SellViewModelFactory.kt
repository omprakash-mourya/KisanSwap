package com.kisanswap.kisanswap.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kisanswap.kisanswap.repositories.FirestoreRepository
import com.kisanswap.kisanswap.viewmodel.SellViewModel

class SellViewModelFactory(private val firestoreRepository: FirestoreRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SellViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SellViewModel(firestoreRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}