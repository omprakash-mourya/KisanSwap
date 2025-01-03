package com.kisanswap.kisanswap.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kisanswap.kisanswap.user.buyer.repository.BuyerUserFirebaseRepository
import com.kisanswap.kisanswap.user.buyer.viewModel.AccountViewModel

class AccountViewModelFactory(
    private val firebaseRepository: BuyerUserFirebaseRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AccountViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AccountViewModel(firebaseRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}