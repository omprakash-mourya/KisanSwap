package com.kisanswap.kisanswap.home.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.ui.graphics.painter.Painter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.GeoPoint
import com.kisanswap.kisanswap.CacheManager
import com.kisanswap.kisanswap.common.functions.calculateDistance
import com.kisanswap.kisanswap.dataClass.Product
import com.kisanswap.kisanswap.common.functions.downloadVideoFromUrl
import com.kisanswap.kisanswap.home.repository.HomeFirebaseRepository
import com.kisanswap.kisanswap.roomDataBase.ProductDao
import com.kisanswap.kisanswap.roomDataBase.SavedProductEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(private val firebaseRepository: HomeFirebaseRepository) : ViewModel() {

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
            firebaseRepository.getProductsBatch(null, 2) { products, lastVisible ->
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

                firebaseRepository.getProductsBatch(lastProduct, 1) { newProducts, lastVisible ->
                    if(newProducts.isNotEmpty()){
                        productEnded.postValue(false)
                        val currentProducts = _products.value.orEmpty().toMutableList()
                        // only add newProducts to the currentProducts only if newProducts are not contained in currentProducts
                        newProducts.forEach(){
                            if(!currentProducts.contains(it)){
                                currentProducts.add(it)
                            }
                        }

//                        currentProducts.addAll(newProducts)
                        _products.value = currentProducts


                        Log.d("HomeViewModel", "Loaded more products: ${newProducts.size}")
                        lastVisibleProduct = lastVisible
                        newProducts.forEach {
//                            incrementImpressions(it.id)
                        }
//                        CacheManager.addProducts(newProducts)
//                        _products.value = CacheManager.getProducts()
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
                onComplete(cachedProduct)
            } else {
                firebaseRepository.getProduct(productId) { product ->
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
            firebaseRepository.getProducts { products ->
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
    fun saveProductToUser(productId: String, onComplete: (Boolean) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        firebaseRepository.saveProductToUser(userId, productId, onComplete)
    }

    /*private fun fetchProducts() {
        viewModelScope.launch {
            firebaseRepository.getProducts { fetchedProducts ->
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
            firebaseRepository.getProduct(productId) { product ->
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
            firebaseRepository.incrementClicks(productId)
        }
    }

    fun incrementImpressions(productId: String){
        viewModelScope.launch(Dispatchers.IO) {
            firebaseRepository.incrementImpressions(productId)
        }
    }

    fun saveProduct(dao: ProductDao, id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val savedProduct = SavedProductEntity(id = id)
            dao.insertSavedProduct(savedProduct)
        }
    }

    fun deleteFromSaved(dao: ProductDao, id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val savedProduct = SavedProductEntity(id = id)
            dao.deleteSavedProduct(savedProduct)
        }
    }

    // calculate distance
    fun calculateDistanceInKm(start:GeoPoint, end: GeoPoint): Int {
        Log.d("HomeViewModel","Calculate distance executed")
        var distance = 0
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("HomeViewModel","Calculate distance executed, and worked in IO dispatcher")
            distance = calculateDistance(start, end)
        }
        return distance
    }
}

