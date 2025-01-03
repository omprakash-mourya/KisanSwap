package com.kisanswap.kisanswap.roomDataBase

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "seen_products")
data class SeenProductEntity(
    @PrimaryKey val id: String
)

@Entity(tableName = "saved_products")
data class SavedProductEntity(
    @PrimaryKey val id: String
//    val contactNumber: String,
//    val primaryCategory: String,
//    val secondaryCategory: String,
//    val tertiaryCategory: String,
//    val latitude: Double,
//    val longitude: Double,
//    val sellerBacklink: String,
//    val shortDetails: String,
//    val price: Int,
//    val negotiable: Boolean,
//    val details: String,
//    val photos: List<String>,
//    val videos: List<String>
)

@Entity(tableName = "uploaded_products")
data class UploadedProductEntity(
    @PrimaryKey val id: String
//    val contactNumber: String,
//    val primaryCategory: String,
//    val secondaryCategory: String,
//    val tertiaryCategory: String,
//    val latitude: Double,
//    val longitude: Double,
//    val sellerBacklink: String,
//    val shortDetails: String,
//    val price: Int,
//    val negotiable: Boolean,
//    val details: String,
//    val photos: List<String>,
//    val videos: List<String>
)