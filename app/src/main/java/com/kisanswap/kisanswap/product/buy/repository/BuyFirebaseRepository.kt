package com.kisanswap.kisanswap.product.buy.repository

import android.util.Log
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.kisanswap.kisanswap.dataClass.Product
import java.util.concurrent.Executors

class BuyFirebaseRepository {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val executor = Executors.newSingleThreadExecutor()

    fun getProduct(productId: String, onComplete: (Product?) -> Unit) {
        db.collection("products").document(productId)
            .get()
            .addOnSuccessListener { document ->
                onComplete(document.toObject(Product::class.java))
                Log.d("FirestoreRepository", "Fetched product with id $productId")
            }
            .addOnFailureListener {
                onComplete(null)
                Log.w("FirestoreRepository", "Error getting product with id $productId", it)
            }
    }

    fun saveProductToUser(userId: String, productId: String, onComplete: (Boolean) -> Unit) {
        val userRef = db.collection("users").document(userId)
        userRef.update("savedProducts", FieldValue.arrayUnion(productId))
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    fun saveProduct(productId: String, onComplete: (Boolean) -> Unit) {
        db.collection("products").document(productId)
            .set(productId)
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }

    fun incrementWhatsappDM(productId: String) {
        val productRef = db.collection("products").document(productId)
        db.runTransaction { transaction ->
            val snapshot = transaction.get(productRef)
            val newWhatsappDM = snapshot.getLong("whatsappDM")?.plus(1) ?: 1
            transaction.update(productRef, "whatsappDM", newWhatsappDM)
        }.addOnSuccessListener {
            Log.d("FirestoreRepository", "WhatsappDM incremented for product $productId")
        }.addOnFailureListener {
            Log.w("FirestoreRepository", "Error incrementing whatsappDM for product $productId", it)
        }
    }

    fun incrementCall(productId: String) {
        val productRef = db.collection("products").document(productId)
        db.runTransaction { transaction ->
            val snapshot = transaction.get(productRef)
            val newCall = snapshot.getLong("call")?.plus(1) ?: 1
            transaction.update(productRef, "call", newCall)
        }.addOnSuccessListener {
            Log.d("FirestoreRepository", "Call incremented for product $productId")
        }.addOnFailureListener {
            Log.w("FirestoreRepository", "Error incrementing call for product $productId", it)
        }
    }

    fun incrementMapSearched(productId: String) {
        val productRef = db.collection("products").document(productId)
        db.runTransaction { transaction ->
            val snapshot = transaction.get(productRef)
            val newMapSearched = snapshot.getLong("mapSearched")?.plus(1) ?: 1
            transaction.update(productRef, "mapSearched", newMapSearched)
        }.addOnSuccessListener {
            Log.d("FirestoreRepository", "MapSearched incremented for product $productId")
        }.addOnFailureListener {
            Log.w("FirestoreRepository", "Error incrementing mapSearched for product $productId", it)
        }
    }

    fun incrementImpressions(productId: String) {
        Tasks.call(executor) {
            db.collection("products").document(productId)
                .update("impressions", FieldValue.increment(1))
        }
//        val productRef = db.collection("products").document(productId)
//        db.runTransaction { transaction ->
//            val snapshot = transaction.get(productRef)
//            val newImpressions = snapshot.getLong("impressions")?.plus(1) ?: 1
//            transaction.update(productRef, "impressions", newImpressions)
//        }.addOnSuccessListener {
//            Log.d("FirestoreRepository", "Impressions incremented for product $productId")
//        }.addOnFailureListener {
//            Log.w("FirestoreRepository", "Error incrementing impressions for product $productId", it)
//        }
    }

    fun incrementClicks(productId: String) {
        Tasks.call(executor) {
            db.collection("products").document(productId)
                .update("clicks", FieldValue.increment(1))
        }
//        val productRef = db.collection("products").document(productId)
//        db.runTransaction { transaction ->
//            val snapshot = transaction.get(productRef)
//            val newClicks = snapshot.getLong("clicks")?.plus(1) ?: 1
//            transaction.update(productRef, "clicks", newClicks)
//        }.addOnSuccessListener {
//            Log.d("FirestoreRepository", "Clicks incremented for product $productId")
//        }.addOnFailureListener {
//            Log.w("FirestoreRepository", "Error incrementing clicks for product $productId", it)
//        }
    }
}