package com.kisanswap.kisanswap.repositories

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.kisanswap.kisanswap.dataClass.Product
import com.kisanswap.kisanswap.dataClass.User
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

    fun updateProduct(product: Product, onComplete: (Boolean) -> Unit) {
        db.collection("products").document(product.id)
            .set(product)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    fun addProductToUser(userId: String, productId: String, onComplete: (Boolean) -> Unit) {
        val userRef = db.collection("users").document(userId)
        userRef.update("myProducts", FieldValue.arrayUnion(productId))
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    fun saveProductToUser(userId: String, productId: String, onComplete: (Boolean) -> Unit) {
        val userRef = db.collection("users").document(userId)
        userRef.update("savedProducts", FieldValue.arrayUnion(productId))
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
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

//    fun deleteProduct(productId: String, onComplete: (Boolean) -> Unit) {
//        db.collection("products").document(productId)
//            .delete()
//            .addOnSuccessListener { onComplete(true) }
//            .addOnFailureListener { onComplete(false) }
//    }

    fun getProductDetails(productId: String, onComplete: (Product?) -> Unit) {
        db.collection("products").document(productId)
            .get()
            .addOnSuccessListener { document ->
                val product = document.toObject(Product::class.java)
                onComplete(product)
            }
            .addOnFailureListener { onComplete(null) }
    }

    private fun getUserProductIds(userId: String, onComplete: (List<String>) -> Unit) {
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

   /* fun addProduct(product: Product, onComplete: (Boolean) -> Unit) {
        db.collection("products").add(product)
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
                Log.d("FirestoreRepository", "Product added successfully")
            }
    } */

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

    fun saveProduct(productId: String, onComplete: (Boolean) -> Unit) {
        db.collection("products").document(productId)
            .set(productId)
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }
}