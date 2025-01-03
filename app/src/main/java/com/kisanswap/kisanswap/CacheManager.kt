package com.kisanswap.kisanswap

import android.graphics.Bitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kisanswap.kisanswap.dataClass.Product
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object CacheManager {
    private val productsCache = mutableMapOf<String, List<Product>>()
    private val photoCache = mutableMapOf<String, Painter?>()
    private val productCache = mutableMapOf<String, Product?>()
    private val productListCache = mutableListOf<Product>()
    private val userProductListCache = mutableMapOf<String, List<Product>>()
    private val videoCache = mutableMapOf<String, String>()

    private val cacheBuyPhoto = mutableMapOf<String, Painter?>()
    private val cacheEditPhoto = mutableMapOf<String, Bitmap?>()

    private val scope = CoroutineScope(Dispatchers.Default)

    fun setVideo(url: String, path: String) {
        videoCache[url] = path
    }

    fun getVideo(url: String): String? {
        return videoCache[url]
    }

    fun getProduct(productId: String): Product? {
        return productCache[productId]
    }

    fun setProduct(productId: String, product: Product?) {
        scope.launch { productCache[productId] = product }
    }

    fun getProducts(): List<Product> {
//        return productsCache[key]
        return productListCache
    }

    fun addProducts(newProducts: List<Product>) {
        scope.launch { productListCache.addAll(newProducts) }
//        _cachedProducts.value = productListCache
    }

    fun setProducts(newProducts: List<Product>) {
//        productsCache[key] = products
//        productListCache.clear()
        scope.launch {
            productListCache.addAll(newProducts)
        }
    }

    fun getPhoto(productId: String): Painter? {
        return photoCache[productId]
    }

    fun setPhoto(productId: String, photo: Painter?) {
        scope.launch { photoCache[productId] = photo }
    }
    fun getBuyPhoto(productId: String): Painter? {
        return cacheBuyPhoto[productId]
    }

    fun setBuyPhoto(productId: String, photo: Painter?) {
        scope.launch { cacheBuyPhoto[productId] = photo }
    }

    fun getEditPhoto(productId: String): Bitmap? {
        return cacheEditPhoto[productId]
    }

    fun setEditPhoto(productId: String, photo: Bitmap?) {
        scope.launch { cacheEditPhoto[productId] = photo }
    }

    fun clearCache() {
        scope.launch {
            productCache.clear()
            productsCache.clear()
            photoCache.clear()
            productListCache.clear()
            videoCache.clear()
            userProductListCache.clear()
            cacheBuyPhoto.clear()
            cacheEditPhoto.clear()

        }
//        _cachedProducts.value = emptyList()
    }

    fun getUserProducts(key: String): List<Product>? {
        return userProductListCache[key]
    }
    fun setUserProducts(key: String, products: List<Product>) {
        scope.launch { userProductListCache[key] = products }
    }
}