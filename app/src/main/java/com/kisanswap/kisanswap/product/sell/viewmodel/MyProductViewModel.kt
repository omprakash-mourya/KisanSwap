package com.kisanswap.kisanswap.product.sell.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kisanswap.kisanswap.CacheManager
import com.kisanswap.kisanswap.dataClass.Product
import com.kisanswap.kisanswap.product.sell.repository.SellFirebaseRepository
import com.kisanswap.kisanswap.roomDataBase.ProductDao
import com.kisanswap.kisanswap.roomDataBase.SavedProductEntity
import com.kisanswap.kisanswap.roomDataBase.UploadedProductEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyProductViewModel(private val firebaseRepository: SellFirebaseRepository) : ViewModel() {

    private var _userProducts = MutableLiveData<List<Product>?>()
    val userProducts: MutableLiveData<List<Product>?> get() = _userProducts

    var loading = MutableLiveData<Boolean>(true)
    private var isProductLoaded = false

    fun updateProduct(product: Product, onComplete: (Boolean) -> Unit) {
        firebaseRepository.updateProduct(product, onComplete)
    }

    fun deleteProduct(product: Product,  userId: String, onComplete: (Boolean) -> Unit) {
        firebaseRepository.deleteProduct(product.id, userId, product.photos, product.videos) { success ->
            if (success) {
//                _userProducts.value = _userProducts.value?.filter { it.id != product.id }
            }
            onComplete(success)
        }
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
            firebaseRepository.getUserProducts(userId) { products ->
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

    fun getUserProductIds(userId: String, onComplete: (List<String>) -> Unit) {
        loading.value = true
        firebaseRepository.getUserProductIds(userId, onComplete)
        loading.value = false
    }

    fun loadProduct(productId: String, onComplete: (Product?) -> Unit) {
        loading.value = true
        firebaseRepository.getProductDetails(productId) { product ->
            onComplete(product)
        }
        loading.value = false
    }

    fun addMyProductRoom(dao: ProductDao, id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val uploadedProduct = UploadedProductEntity(id = id)
            dao.insertUploadedProduct(uploadedProduct)
        }
    }

    fun deleteFromMyProductRoom(dao: ProductDao, id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val uploadedProduct = UploadedProductEntity(id = id)
            dao.deleteUploadedProduct(uploadedProduct)
        }
    }
}