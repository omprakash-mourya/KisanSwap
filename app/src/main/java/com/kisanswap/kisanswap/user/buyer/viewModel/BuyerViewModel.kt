package com.kisanswap.kisanswap.user.buyer.viewModel

import androidx.lifecycle.ViewModel
import com.kisanswap.kisanswap.user.buyer.model.User
import com.kisanswap.kisanswap.user.buyer.repository.BuyerUserFirebaseRepository

class BuyerViewModel(private val firebaseRepository: BuyerUserFirebaseRepository) : ViewModel() {

    fun createUser(user: User, onComplete: (Boolean) -> Unit) {
        firebaseRepository.createUser(user, onComplete)
    }

    fun updateUser(user: User, onComplete: (Boolean) -> Unit) {
        firebaseRepository.updateUser(user, onComplete)
    }
}