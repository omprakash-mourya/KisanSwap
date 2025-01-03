package com.kisanswap.kisanswap.roomDataBase

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSeenProduct(seenProduct: SeenProductEntity)

    @Query("SELECT * FROM seen_products")
    suspend fun getSeenProducts(): List<SeenProductEntity>

    @Delete
    suspend fun deleteSeenProduct(seenProduct: SeenProductEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedProduct(savedProduct: SavedProductEntity)

    @Query("SELECT * FROM saved_products")
    suspend fun getSavedProducts(): List<SavedProductEntity>

    @Delete
    suspend fun deleteSavedProduct(savedProduct: SavedProductEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUploadedProduct(uploadedProduct: UploadedProductEntity)

    @Query("SELECT * FROM uploaded_products")
    suspend fun getUploadedProducts(): List<UploadedProductEntity>

    @Delete
    suspend fun deleteUploadedProduct(uploadedProduct: UploadedProductEntity)
}