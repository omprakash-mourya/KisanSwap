package com.kisanswap.kisanswap.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kisanswap.kisanswap.CacheManager
import com.kisanswap.kisanswap.dataClass.Product
import com.kisanswap.kisanswap.functions.downloadVideoFromUrl
import com.kisanswap.kisanswap.repositories.FirestoreRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class HomeViewModel(private val firestoreRepository: FirestoreRepository) : ViewModel() {

    private val _products = MutableLiveData<List<Product>>()
    val products: MutableLiveData<List<Product>> get() = _products

    private val _productMap = mutableMapOf<String, Product>()
    val productMap: Map<String, Product> get() = _productMap

    private var lastVisibleProduct: Product? = null
    var loading = MutableLiveData(false)
    var productEnded = MutableLiveData<Boolean>(false)

    init {
        loadInitialProducts()
    }

    fun loadInitialProducts() {
        if(loading.value == true){
            return
        }
        loading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            firestoreRepository.getProductsBatch(null, 2) { products, lastVisible ->
                _products.value = products
                lastVisibleProduct = lastVisible
                products.forEach {
                    incrementImpressions(it.id)
                }
                CacheManager.addProducts(products)
                _products.value = CacheManager.getProducts()
            }
            withContext(Dispatchers.Main){
                loading.value = false
            }
        }
        loading.value = false
    }

    fun loadMoreProducts(onComplete:() -> Unit, onFailure:() -> Unit) {
        if(loading.value == true){
            Log.w("HomeViewModel", "already loading a product")
            return
        }
        Log.w("HomeViewModel", "loading a new product")
        loading.value = true
        lastVisibleProduct?.let { lastProduct ->
            viewModelScope.launch(Dispatchers.IO) {
                withContext(Dispatchers.Main){
                    loading.value = true
                }
                firestoreRepository.getProductsBatch(lastProduct, 2) { newProducts, lastVisible ->
                    if(newProducts.isNotEmpty()){
                        productEnded.postValue(false)
                        val currentProducts = _products.value.orEmpty().toMutableList()
                        currentProducts.addAll(newProducts)
                        _products.value = currentProducts


                        Log.d("HomeViewModel", "Loaded more products: ${newProducts.size}")
                        lastVisibleProduct = lastVisible
                        newProducts.forEach {
                            incrementImpressions(it.id)
                        }
                        CacheManager.addProducts(newProducts)
//                        _products.value = CacheManager.getProducts()
                        _products.value = CacheManager.getProducts()
                        onComplete()
                    } else {
                        productEnded.postValue(true)
                        onFailure()
                        Log.d("HomeViewModel", "No more products to load")
                    }
                }
                withContext(Dispatchers.Main){
                    loading.value = false
                }
            }
        } ?: run {
            Log.d("HomeViewModel", "No products to load")
        }
        loading.value = false
        if(loading.value == false){
            Log.d("HomeViewModel", "Loading value set to false")
        }
    }

    fun downloadAndCacheVideos(product: Product, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            product.videos.forEach { videoUrl ->
                val cachedVideoPath = downloadVideoFromUrl(context, videoUrl)
                cachedVideoPath?.let {
                    CacheManager.setVideo(videoUrl, it)
                }
            }
        }
    }

    fun getCachedProducts(): List<Product> {
            return CacheManager.getProducts() ?: emptyList()
    }

    fun fetchProductById(productId: String, onComplete: (Product?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val cachedProduct = CacheManager.getProduct(productId)
            if (cachedProduct != null) {
                cachedProduct.loadedFromCache = true
                onComplete(cachedProduct)
            } else {
                firestoreRepository.getProduct(productId) { product ->
                    product?.let {
                        CacheManager.setProduct(productId, it)
                        _productMap[productId] = it
                    }
                    onComplete(product)
                }
            }
        }
    }

    /*private fun loadProducts() {
        val cachedProducts = CacheManager.getProducts("home")
        if (cachedProducts != null) {
            cachedProducts.forEach { it.loadedFromCache = true }
            _products.value = cachedProducts!!
        } else {
            firestoreRepository.getProducts { products ->
                _products.value = products
                CacheManager.setProducts("home", products)
                products.forEach { product ->
                    incrementImpressions(product.id)
                }
            }
        }
    }*/

    fun getCachedPhoto(productId: String): Painter? {
        return CacheManager.getPhoto(productId)
    }

    fun cachePhoto(productId: String, photo: Painter?) {
        CacheManager.setPhoto(productId, photo)
    }

    /*private fun fetchProducts() {
        viewModelScope.launch {
            firestoreRepository.getProducts { fetchedProducts ->
                _products.value = fetchedProducts
            }
        }
    }

    fun fetchProductById(productId: String, onComplete: (Product?) -> Unit) {
        val cachedProduct = CacheManager.getProduct(productId)
        if (cachedProduct != null) {
            cachedProduct.loadedFromCache = true
            onComplete(cachedProduct)
        } else {
            firestoreRepository.getProduct(productId) { product ->
                product?.let {
                    CacheManager.setProduct(productId, it)
                    _productMap[productId] = it
                }
                onComplete(product)
            }
        }
    }*/

    fun incrementClicks(productId: String){
        viewModelScope.launch(Dispatchers.IO) {
            firestoreRepository.incrementClicks(productId)
        }
    }

    fun incrementImpressions(productId: String){
        viewModelScope.launch(Dispatchers.IO) {
            firestoreRepository.incrementImpressions(productId)
        }
    }
}

