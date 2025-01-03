package com.kisanswap.kisanswap.repositories

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.kisanswap.kisanswap.dataClass.Product
import com.kisanswap.kisanswap.user.buyer.model.User
import java.util.concurrent.Executors

class FirestoreRepository {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val executor = Executors.newSingleThreadExecutor()

    fun addUser(user: User, onComplete: (Boolean) -> Unit) {
        db.collection("users").document(user.email)
            .set(user)
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }

    fun getUser(email: String, onComplete: (User?) -> Unit) {
        db.collection("users").document(email)
            .get()
            .addOnSuccessListener { document ->
                onComplete(document.toObject(User::class.java))
            }
            .addOnFailureListener {
                onComplete(null)
            }
    }
//    fun deleteProduct(productId: String, onComplete: (Boolean) -> Unit) {
//        db.collection("products").document(productId)
//            .delete()
//            .addOnSuccessListener { onComplete(true) }
//            .addOnFailureListener { onComplete(false) }
//    }

   /* fun addProduct(product: Product, onComplete: (Boolean) -> Unit) {
        db.collection("products").add(product)
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
                Log.d("FirestoreRepository", "Product added successfully")
            }
    } */

    fun sortProductsByPrice(){
        db.collection("products")
            .orderBy("price")
            .get()
            .addOnSuccessListener { result ->
                val products = result.map { it.toObject(Product::class.java) }
                Log.d("FirestoreRepository", "Fetched ${products.size} products")
            }
            .addOnFailureListener {
                Log.w("FirestoreRepository", "Error getting documents.", it)
            }
    }

    fun filterProductByDistance(lat:Int, lon:Int, distance: Int){ // sort the items on the basis of distance from buyer
        val delta = distance*0.00899320363724538
        db.collection("products").whereGreaterThanOrEqualTo("latitude", lat-delta).whereLessThanOrEqualTo("latitude", lat+delta)
            .whereGreaterThanOrEqualTo("longitude", lon-delta).whereLessThanOrEqualTo("longitude", lon+delta)
            .get()
            .addOnSuccessListener { result ->
                val products = result.map { it.toObject(Product::class.java) }
                Log.d("FirestoreRepository", "Fetched ${products.size} products")
            }
            .addOnFailureListener {
                Log.w("FirestoreRepository", "Error getting documents.", it)
            }
    }

    fun filterProductsByCategory(category: String){ // filter product on basis of category
        db.collection("products").whereEqualTo("category", category)
            .get()
            .addOnSuccessListener { result ->
                val products = result.map { it.toObject(Product::class.java) }
                Log.d("FirestoreRepository", "Fetched ${products.size} products")
            }
            .addOnFailureListener {
                Log.w("FirestoreRepository", "Error getting documents.", it)
            }
    }

    fun sortProductByDistance(lat:Int, lon:Int){
        db.collection("products")
    }

    fun getProducts(onComplete: (List<Product>) -> Unit) {
        db.collection("products")
            .get()
            .addOnSuccessListener { result ->
                val products = result.map { it.toObject(Product::class.java) }
                onComplete(products)
                Log.d("FirestoreRepository", "Fetched ${products.size} products")
            }
            .addOnFailureListener {
                onComplete(emptyList())
                Log.w("FirestoreRepository", "Error getting documents.", it)
            }
    }
}