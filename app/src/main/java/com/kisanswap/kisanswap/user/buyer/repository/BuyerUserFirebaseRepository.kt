package com.kisanswap.kisanswap.user.buyer.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.kisanswap.kisanswap.user.buyer.model.User
import java.util.concurrent.Executors

class BuyerUserFirebaseRepository {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val executor = Executors.newSingleThreadExecutor()

    fun createUser(user: User, onComplete: (Boolean) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        db.collection("users").document(userId)
            .set(user)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    fun updateUser(user: User, onComplete: (Boolean) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        db.collection("users").document(userId)
            .set(user)
//            .update(user.toMap)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }
}