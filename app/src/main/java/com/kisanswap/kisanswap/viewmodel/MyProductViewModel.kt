package com.kisanswap.kisanswap.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kisanswap.kisanswap.CacheManager
import com.kisanswap.kisanswap.dataClass.Product
import com.kisanswap.kisanswap.repositories.FirestoreRepository

class MyProductViewModel(private val firestoreRepository: FirestoreRepository) : ViewModel() {

    private val _userProducts = MutableLiveData<List<Product>?>()
    val userProducts: MutableLiveData<List<Product>?> get() = _userProducts

    var loading = MutableLiveData<Boolean>(false)
    private var isProductLoaded = false

    fun updateProduct(product: Product, onComplete: (Boolean) -> Unit) {
        firestoreRepository.updateProduct(product, onComplete)
    }

    fun deleteProduct(product: Product,  userId: String, onComplete: (Boolean) -> Unit) {
        firestoreRepository.deleteProduct(product.id, userId, product.photos, product.videos) { success ->
            if (success) {
//                _userProducts.value = _userProducts.value?.filter { it.id != product.id }
            }
            onComplete(success)
        }
    }

    fun getProductDetails(productId: String, onComplete: (Product?) -> Unit) {
        firestoreRepository.getProductDetails(productId, onComplete)
    }

    fun getUserProducts(userId: String, onLoadingFailed:() -> Unit) {
        if (isProductLoaded) {
            Log.d("BuyViewModel", "Product already loaded")
            return
        }
        loading.value = true
        Log.d("MyProductViewModel", "getUserProducts started")
        val cachedProducts = CacheManager.getUserProducts("myProducts_$userId")
        if (cachedProducts != null) {
            _userProducts.value = cachedProducts
            if(_userProducts.value!!.isEmpty()){
                Log.d("MyProductViewModel", "cachedProducts are empty")
            } else {
                Log.d("MyProductViewModel", "Products loaded from caches")
                isProductLoaded = true
            }
        } else {
            firestoreRepository.getUserProducts(userId) { products ->
                while (products == null) {
                    Log.d("BuyViewModel", "Waiting, Product not fetched yet")
                    Thread.sleep(1000)
                    // Wait until the product is fetched
                }
                _userProducts.value = products
                while (_userProducts.value == null) {
                    Log.d("BuyViewModel", "Waiting, Products not assigned yet")
                    Thread.sleep(1000)
                    // Wait until the product is fetched
                }
                if(_userProducts.value!!.isEmpty()){
                    Log.d("MyProductViewModel", "fetchedProducts are empty")
                    onLoadingFailed()
                } else {
                    Log.d("MyProductViewModel", "Products loaded from firebase")
                    isProductLoaded = true
                }
                CacheManager.setUserProducts("myProducts_$userId", products)
            }
        }
        loading.value = false
    }
}