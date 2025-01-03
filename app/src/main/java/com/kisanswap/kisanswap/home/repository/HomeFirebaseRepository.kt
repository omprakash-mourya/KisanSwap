package com.kisanswap.kisanswap.home.repository

import android.util.Log
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.kisanswap.kisanswap.dataClass.Product
import java.util.concurrent.Executors

class HomeFirebaseRepository {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val executor = Executors.newSingleThreadExecutor()

    fun getProductsBatch(lastVisibleProduct: Product?, limit: Int, onComplete: (List<Product>, Product?) -> Unit) {
        val query = if (lastVisibleProduct == null) {
            db.collection("products")
                .orderBy("id")
                .limit(limit.toLong())
        } else {
            db.collection("products")
                .orderBy("id")
                .startAfter(lastVisibleProduct.id)
                .limit(limit.toLong())
        }

        Tasks.call(executor){
            query.get()
                .addOnSuccessListener { result ->
                    val products = result.map { it.toObject(Product::class.java) }
                    val lastVisible = if (products.isNotEmpty()) products.last() else null
                    onComplete(products, lastVisible)
                    Log.d("FirestoreRepository", "Fetched ${products.size} products")
                }
                .addOnFailureListener {
                    onComplete(emptyList(), null)
                    Log.w("FirestoreRepository", "Error getting documents.", it)
                }
        }
    }

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

    fun saveProductToUser(
        userId: String,
        productId: String,
        onComplete: (Boolean) -> Unit
    ) {
        val userRef = db.collection("users").document(userId)
        userRef.update("savedProducts", FieldValue.arrayUnion(productId))
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }
}