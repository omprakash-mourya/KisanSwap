package com.kisanswap.kisanswap.product.sell.repository

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.kisanswap.kisanswap.dataClass.Product
import java.util.concurrent.Executors

class SellFirebaseRepository {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val executor = Executors.newSingleThreadExecutor()

    fun addProductToUser(userId: String, productId: String, onComplete: (Boolean) -> Unit) {
        val userRef = db.collection("users").document(userId)
        userRef.update("myProducts", FieldValue.arrayUnion(productId))
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    fun addProduct(product: Product, onComplete: (Boolean, String?) -> Unit) {
        val productRef = db.collection("products").document()
        product.id = productRef.id
        productRef.set(product)
            .addOnSuccessListener {
                onComplete(true, productRef.id)
                Log.d("FirestoreRepository", "Product ${product.id} added successfully")
            }
            .addOnFailureListener {
                onComplete(false, null)
                Log.w("FirestoreRepository", "Error adding product", it)
            }
    }

    fun updateProduct(product: Product, onComplete: (Boolean) -> Unit) {
        db.collection("products").document(product.id)
            .set(product)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
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

    fun deleteProduct(productId: String, userId: String, photos: List<String>, videos: List<String>, onComplete: (Boolean) -> Unit) {
        // Delete images and videos from Firebase Storage
        val deleteTasks = mutableListOf<Task<Void>>()
        val userRef = db.collection("users").document(userId)
        photos.forEach { photoUrl ->
            val photoRef = storage.getReferenceFromUrl(photoUrl)
            deleteTasks.add(photoRef.delete())
        }
        videos.forEach { videoUrl ->
            val videoRef = storage.getReferenceFromUrl(videoUrl)
            deleteTasks.add(videoRef.delete())
        }

        Tasks.whenAllComplete(deleteTasks).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Delete product document from Firestore
                db.collection("products").document(productId).delete()
                    .addOnSuccessListener {
                        db.runTransaction { transaction ->
                            transaction.update(userRef, "myProducts", FieldValue.arrayRemove(productId))
                        }
                        onComplete(true)
                    }
                    .addOnFailureListener { onComplete(false) }
            } else {
                onComplete(false)
            }
        }
    }

    fun getProductDetails(productId: String, onComplete: (Product?) -> Unit) {
        db.collection("products").document(productId)
            .get()
            .addOnSuccessListener { document ->
                val product = document.toObject(Product::class.java)
                onComplete(product)
            }
            .addOnFailureListener {
                Log.w("SellRepository", "Error getting product details", it)
                onComplete(null)
            }
    }

    fun getUserProductIds(userId: String, onComplete: (List<String>) -> Unit) {
        Log.d("FirestoreRepository", "Fetching product IDs for user $userId")
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                val productIds = document.get("myProducts") as? List<String> ?: emptyList()
                onComplete(productIds)
                Log.d("FirestoreRepository", "Fetched ${productIds.size} product IDs for user $userId")
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreRepository", "Error getting product IDs", exception)
                onComplete(emptyList())
            }
    }

    fun getUserProducts(userId: String, onComplete: (List<Product>) -> Unit) {
        Log.d("FirestoreRepository", "Fetching products for user $userId")
        getUserProductIds(userId) { productIds ->
            if (productIds.isEmpty()) {
                onComplete(emptyList())
                Log.w("FirestoreRepository", "No products found for user $userId")
                return@getUserProductIds
            }

            db.collection("products").whereIn("id", productIds).get()
                .addOnSuccessListener { querySnapshot ->
                    val products = querySnapshot.documents.mapNotNull { it.toObject(Product::class.java) }
//                    val products = querySnapshot.toObjects(Product::class.java)
                    onComplete(products)
                    Log.d("FirestoreRepository", "Fetched ${products.size} products for user $userId")
                }
                .addOnFailureListener { exception ->
                    Log.e("FirestoreRepository", "Error getting products", exception)
                    onComplete(emptyList())
                }
        }
    }

    fun updateProductDetails(productId: String, updatedFields: Map<String, Any>, onComplete: (Boolean) -> Unit) {
        db.collection("products").document(productId).update(updatedFields)
            .addOnSuccessListener {
                onComplete(true)
            }
            .addOnFailureListener {
                onComplete(false)
            }
    }

    fun addPhotoToProduct(productId: String, photoUrl: String, onComplete: (Boolean) -> Unit) {
        db.collection("products").document(productId).update("photos", FieldValue.arrayUnion(photoUrl))
            .addOnSuccessListener {
                onComplete(true)
            }
            .addOnFailureListener {
                onComplete(false)
            }
    }

    fun deletePhotoFromProduct(productId: String, photoUrl: String, onComplete: (Boolean) -> Unit) {
        db.collection("products").document(productId).update("photos", FieldValue.arrayRemove(photoUrl))
            .addOnSuccessListener {
                onComplete(true)
            }
            .addOnFailureListener {
                onComplete(false)
            }
    }

    fun addVideoToProduct(productId: String, videoUrl: String, onComplete: (Boolean) -> Unit) {
        db.collection("products").document(productId).update("videos", FieldValue.arrayUnion(videoUrl))
            .addOnSuccessListener {
                onComplete(true)
            }
            .addOnFailureListener {
                onComplete(false)
            }
    }

    fun deleteVideoFromProduct(productId: String, videoUrl: String, onComplete: (Boolean) -> Unit) {
        db.collection("products").document(productId).update("videos", FieldValue.arrayRemove(videoUrl))
            .addOnSuccessListener {
                onComplete(true)
            }
            .addOnFailureListener {
                onComplete(false)
            }
    }

}