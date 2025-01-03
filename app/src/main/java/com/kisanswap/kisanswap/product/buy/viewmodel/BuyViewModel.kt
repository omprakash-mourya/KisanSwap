package com.kisanswap.kisanswap.product.buy.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.ui.graphics.painter.Painter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.kisanswap.kisanswap.CacheManager
import com.kisanswap.kisanswap.dataClass.Product
import com.kisanswap.kisanswap.common.functions.downloadVideoFromUrl
import com.kisanswap.kisanswap.roomDataBase.ProductDao
import com.kisanswap.kisanswap.roomDataBase.SavedProductEntity
import com.kisanswap.kisanswap.product.buy.repository.BuyFirebaseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class BuyViewModel(private val firebaseRepository: BuyFirebaseRepository) : ViewModel() {

    private val _product = MutableLiveData<Product?>()
    val product: LiveData<Product?> get() = _product

    var loading = MutableLiveData<Boolean>(false)
    private var isProductLoaded = false

    private val _cachedVideoUri = MutableLiveData<MutableList<Set<Pair<String, String>>>>()
    val cachedVideoUri: LiveData<MutableList<Set<Pair<String, String>>>> get() = _cachedVideoUri

    private val videoDownloadMutex = Mutex()

    fun loadProduct(productId: String) {
        if (isProductLoaded) {
            Log.d("BuyViewModel", "Product already loaded")
            return
        }
        loading.value = true
        val cachedProduct = CacheManager.getProduct(productId)
        if (cachedProduct != null) {
            _product.value = cachedProduct
            isProductLoaded = true
            loading.value = false
        } else {
            firebaseRepository.getProduct(productId) { fetchedProduct ->
                while (fetchedProduct == null) {
                    Log.d("BuyViewModel", "Waiting, Product not fetched yet")
                    // Wait until the product is fetched
                }
                _product.value = fetchedProduct
                while (product.value==null) {
                    Log.d("BuyViewModel", "Waiting, Product not assigned yet")
                    // Wait until the product is fetched
                }
                CacheManager.setProduct(productId, fetchedProduct)
                incrementClicks(productId)
                isProductLoaded = true
                loading.value = false
            }
        }
    }

    fun downloadAndCacheVideos(product: Product, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            for (videoUrl in product.videos) {
                videoDownloadMutex.withLock {
                    val cachedVideoPath = CacheManager.getVideo(videoUrl)
                    if (cachedVideoPath == null) {
                        val downloadedVideoPath = downloadVideoFromUrl(context, videoUrl)
                        downloadedVideoPath?.let {
                            Log.d("BuyViewModel", "Caching video: $videoUrl, at $it")
//                            saveVideoMetadata(context, it)
                            CacheManager.setVideo(videoUrl, it)
//                        val currentList = _cachedVideoUri.value ?: mutableListOf()
//                        currentList.add(setOf(Pair(videoUrl, it)))
//                        _cachedVideoUri.value = currentList

                            _cachedVideoUri.postValue(mutableListOf(setOf(Pair(videoUrl,it))))
                        }
                    } else {
                        Log.d("BuyViewModel", "Video already cached: $videoUrl")
                    }
                }
            }
        }
    }

    fun getCachedVideoUri(url: String): String? {
        return _cachedVideoUri.value?.flatten()?.find { it.first == url }?.second
    }

    fun getCachedPhoto(photoUrl: String): Painter? {
        return CacheManager.getBuyPhoto(photoUrl)
    }

    fun cachePhoto(photoUrl: String, photo: Painter?) {
        CacheManager.setBuyPhoto(photoUrl, photo)
    }

    fun saveProductToUser(productId: String, onComplete: (Boolean) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        firebaseRepository.saveProductToUser(userId, productId, onComplete)
    }

        fun getProduct(productId: String, onComplete: (Boolean) -> Unit) {
            firebaseRepository.getProduct(productId) { fetchedProduct ->
                onComplete(fetchedProduct != null)
            }
        }

        fun addProductToCart(productId: String, onComplete: (Boolean) -> Unit) {
            firebaseRepository.saveProduct(productId) { success ->
                onComplete(success)
            }
        }

    fun incrementWhatsappDm(productId: String){
        firebaseRepository.incrementWhatsappDM(productId)
    }

    fun incrementCall(productId: String){
        firebaseRepository.incrementCall(productId)
    }

    fun incrementMapSearch(productId: String){
        firebaseRepository.incrementMapSearched(productId)
    }

    fun incrementClicks(productId: String){
        firebaseRepository.incrementClicks(productId)
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
}