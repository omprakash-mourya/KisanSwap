package com.kisanswap.kisanswap.viewmodel

import androidx.lifecycle.ViewModel
import com.kisanswap.kisanswap.dataClass.User
import com.kisanswap.kisanswap.repositories.FirestoreRepository

class UserViewModel(private val firestoreRepository: FirestoreRepository) : ViewModel() {

    fun createUser(user: User, onComplete: (Boolean) -> Unit) {
        firestoreRepository.createUser(user, onComplete)
    }

    fun updateUser(user: User, onComplete: (Boolean) -> Unit) {
        firestoreRepository.updateUser(user, onComplete)
    }
}