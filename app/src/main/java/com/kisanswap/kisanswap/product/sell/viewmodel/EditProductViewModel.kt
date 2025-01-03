package com.kisanswap.kisanswap.product.sell.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.painter.Painter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kisanswap.kisanswap.CacheManager
import com.kisanswap.kisanswap.dataClass.Product
import com.kisanswap.kisanswap.common.functions.downloadVideoFromUrl
import com.kisanswap.kisanswap.common.functions.loadImageFromUrlAsBitmap
import com.kisanswap.kisanswap.product.sell.repository.SellFirebaseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class EditProductViewModel(private val firebaseRepository: SellFirebaseRepository) : ViewModel() {
//    var product by mutableStateOf<Product?>(null)
    var name by mutableStateOf("")
    var details by mutableStateOf("")
    var price by mutableStateOf("")
    var category by mutableStateOf("")
    var selectedPhotos by mutableStateOf<List<Uri>>(emptyList())
    var selectedVideos by mutableStateOf<List<Uri>>(emptyList())
    var primaryPhoto by mutableStateOf<Uri?>(null)

    private val _cachedVideoUri = MutableLiveData<MutableList<Set<Pair<String, String>>>>()
    val cachedVideoUri: LiveData<MutableList<Set<Pair<String, String>>>> get() = _cachedVideoUri

    var previousProduct: Product? = null
    
    private val _currentProduct = MutableLiveData<Product?>()
    val currentProduct: LiveData<Product?> get() = _currentProduct

    var loading = MutableLiveData<Boolean>(false)
    private val videoDownloadMutex = Mutex()
    private val photoDownloadMutex = Mutex()

    fun loadProduct(context: Context, productId: String) {
        loading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val cachedProduct = CacheManager.getProduct(productId)
            if (cachedProduct != null) {
                _currentProduct.postValue(cachedProduct)
                previousProduct = cachedProduct
                downloadAndCachePhotosAndVideos(cachedProduct, context)
                loading.postValue(false)
            } else {
                firebaseRepository.getProduct(productId) { fetchedProduct ->
                    _currentProduct.postValue(fetchedProduct)
                    previousProduct = fetchedProduct
                    if (fetchedProduct != null) {
                        downloadAndCachePhotosAndVideos(fetchedProduct, context)
                    }
                    CacheManager.setProduct(productId, fetchedProduct)
                    loading.postValue(false)
                }
            }
        }
    }

    fun downloadAndCachePhotosAndVideos(product: Product, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            for (photoUrl in product.photos) {
                photoDownloadMutex.withLock {
                    val cachedPhoto = CacheManager.getEditPhoto(photoUrl)
                    if (cachedPhoto == null) {
                        val downloadedPhoto = loadImageFromUrlAsBitmap(photoUrl)
                        CacheManager.setEditPhoto(photoUrl, downloadedPhoto)
                    }
                }
            }
            for (videoUrl in product.videos) {
                videoDownloadMutex.withLock {
                    val cachedVideoPath = CacheManager.getVideo(videoUrl)
                    if (cachedVideoPath == null) {
                        val downloadedVideoPath = downloadVideoFromUrl(context, videoUrl)
                        if (downloadedVideoPath != null) {
                            CacheManager.setVideo(videoUrl, downloadedVideoPath)
                        }
                    }
                }
            }
        }
    }

    fun updateProductDetails(productId: String, updatedFields: Map<String, Any>, onComplete: (Boolean) -> Unit) {
        firebaseRepository.updateProductDetails(productId, updatedFields) { success ->
            if (success) {
                _currentProduct.value?.let { product ->
                    updatedFields.forEach { (key, value) ->
                        when (key) {
                            "shortDetails" -> product.shortDetails = value as String
                            "details" -> product.details = value as String
                            "price" -> product.price = value as String
                            "category" -> product.tertiaryCategory = value as String
                        }
                    }
                    CacheManager.setProduct(productId, product)
                }
            }
            if (previousProduct != null && currentProduct.value != null && previousProduct != currentProduct.value) {
                viewModelScope.launch(Dispatchers.IO) {
                    currentProduct.value?.let {
//                        val successs = firebaseRepository.updateProduct(currentProduct.value)
                    }
                    withContext(Dispatchers.Main) {
                        onComplete(success)
                    }
                }
            } else {
                onComplete(false)
            }
            previousProduct = currentProduct.value
            onComplete(success)
        }
    }
    fun addPhotoToProduct(productId: String, photoUrl: String, onComplete: (Boolean) -> Unit) {
        firebaseRepository.addPhotoToProduct(productId, photoUrl) { success ->
            if (success) {
                _currentProduct.value?.photos?.plus(photoUrl)
                CacheManager.setPhoto(photoUrl, null)
            }
            onComplete(success)
        }
    }

    fun deletePhotoFromProduct(productId: String, photoUrl: String, onComplete: (Boolean) -> Unit) {
        firebaseRepository.deletePhotoFromProduct(productId, photoUrl) { success ->
            if (success) {
                _currentProduct.value?.photos?.minus(photoUrl)
//                CacheManager.removePhoto(photoUrl)
            }
            onComplete(success)
        }
    }

    fun addVideoToProduct(productId: String, videoUrl: String, onComplete: (Boolean) -> Unit) {
        firebaseRepository.addVideoToProduct(productId, videoUrl) { success ->
            if (success) {
                _currentProduct.value?.videos?.plus(videoUrl)
                CacheManager.setVideo(videoUrl, null.toString())
            }
            onComplete(success)
        }
    }

    fun deleteVideoFromProduct(productId: String, videoUrl: String, onComplete: (Boolean) -> Unit) {
        firebaseRepository.deleteVideoFromProduct(productId, videoUrl) { success ->
            if (success) {
                _currentProduct.value?.videos?.minus(videoUrl)
//                CacheManager.removeVideo(videoUrl)
            }
            onComplete(success)
        }
    }

    fun getCachedPhoto(photoUrl: String): Painter? {
        return CacheManager.getBuyPhoto(photoUrl)
    }

    fun cachePhoto(photoUrl: String, photo: Painter?) {
        CacheManager.setBuyPhoto(photoUrl, photo)
    }

    fun updateField(fieldName: String, value: Any) {
        currentProduct.value?.let {
            when (fieldName) {
                "shortDetails" -> it.shortDetails = value as String
                "price" -> it.price = value as String
                "details" -> it.details = value as String
                "latitude" -> it.latitude = value as Double
                "longitude" -> it.longitude = value as Double
                "category" -> it.tertiaryCategory = value as String
                "contactNumber" -> it.contactNumber = value as String
                "primaryPhoto" -> it.primaryPhoto = value as String
                // Add other fields as needed
            }
        }
    }

    fun addPhoto(uri: Uri) {
        _currentProduct.value?.photos?.plus(uri.toString())
    }

    fun addVideo(uri: Uri) {
        _currentProduct.value?.videos?.plus(uri.toString())
    }

    fun removePhoto(uri: Uri) {
        _currentProduct.value?.photos?.minus(uri.toString())
    }

    fun removeVideo(uri: Uri) {
        _currentProduct.value?.videos?.minus(uri.toString())
    }

    fun updateProduct(onComplete: (Boolean) -> Unit) {
        val changes = mutableMapOf<String, Any>()
        val newPhotos = mutableListOf<Uri>()
        val removedPhotos = mutableListOf<String>()
        val newVideos = mutableListOf<Uri>()
        val removedVideos = mutableListOf<String>()

        currentProduct.value?.let { current ->
            previousProduct?.let { previous ->
                if (current.shortDetails != previous.shortDetails) {
                    changes["shortDetails"] = current.shortDetails
                }
                if (current.price != previous.price) {
                    changes["price"] = current.price
                }
                // Add other fields as needed

                newPhotos.addAll(current.photos.filter { !previous.photos.contains(it) }.map { Uri.parse(it) })
                removedPhotos.addAll(previous.photos.filter { !current.photos.contains(it) })

                newVideos.addAll(current.videos.filter { !previous.videos.contains(it) }.map { Uri.parse(it) })
                removedVideos.addAll(previous.videos.filter { !current.videos.contains(it) })
            }
        }

        viewModelScope.launch {
            // Upload new photos and videos
            newPhotos.forEach { uri ->
                // Upload photo and get URL
                val url = uploadToFirebaseStorage(uri)
                changes["photos"] = (changes["photos"] as? List<String> ?: currentProduct.value?.photos ?: emptyList()) + url
            }
            newVideos.forEach { uri ->
                // Upload video and get URL
                val url = uploadToFirebaseStorage(uri)
                changes["videos"] = (changes["videos"] as? List<String> ?: currentProduct.value?.videos ?: emptyList()) + url
            }

            // Delete removed photos and videos
            removedPhotos.forEach { url ->
                deleteFromFirebaseStorage(url)
            }
            removedVideos.forEach { url ->
                deleteFromFirebaseStorage(url)
            }

            // Update firebase document
            firebaseRepository.updateProductDetails(currentProduct.value?.id ?: "", changes) { success ->
                onComplete(success)
            }
        }
    }

    private suspend fun uploadToFirebaseStorage(uri: Uri): String {
        // Implement upload logic and return the URL
        return ""
    }

    private suspend fun deleteFromFirebaseStorage(url: String) {
        // Implement delete logic
    }

    /*fun fetchProductDetails(productId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            firebaseRepository.getProductDetails(productId) { fetchedProduct ->
                _product = fetchedProduct
                fetchedProduct?.let { fetchedProductDetail ->
                    name = fetchedProductDetail.shortDetails
                    details = fetchedProductDetail.details
                    price = fetchedProductDetail.price.toString()
                    category = fetchedProductDetail.category
                    selectedPhotos = fetchedProductDetail.photos.map { Uri.parse(it) }
                    selectedVideos = fetchedProductDetail.videos.map { Uri.parse(it) }
                    primaryPhoto = Uri.parse(fetchedProductDetail.primaryPhoto)
                }
            }
        }
    }*/

    /*fun updateProduct(context: Context, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val updatedProduct = product?.copy(
                shortDetails = name,
                details = details,
                price = price.toIntOrNull() ?: 0,
                category = category,
                photos = selectedPhotos.map { it.toString() },
                videos = selectedVideos.map { it.toString() },
                primaryPhoto = primaryPhoto?.toString() ?: ""
            )
            updatedProduct?.let {
                firebaseRepository.updateProduct(it) { success ->
                    onComplete(success)
                }
            }
        }
    }*/
}