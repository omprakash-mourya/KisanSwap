package com.kisanswap.kisanswap.savedProducts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kisanswap.kisanswap.roomDataBase.ProductDao
import com.kisanswap.kisanswap.roomDataBase.SavedProductEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SavedViewModel(private val productDao: ProductDao) : ViewModel() {

    private val _savedProducts = MutableStateFlow<List<SavedProductEntity>>(emptyList())
    val savedProducts: StateFlow<List<SavedProductEntity>> get() = _savedProducts

    init {
        fetchSavedProducts()
    }

    private fun fetchSavedProducts() {
        viewModelScope.launch {
            _savedProducts.value = productDao.getSavedProducts()
        }
    }
}