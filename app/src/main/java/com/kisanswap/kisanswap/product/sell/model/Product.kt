package com.kisanswap.kisanswap.dataClass

import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.PropertyName


data class Product(
    var id: String = "",
    var contactNumber: String = "918905821860",
    var primaryCategory: String = "",
    var secondaryCategory: String = "",
    var tertiaryCategory: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
//    val location: GeoPoint = GeoPoint(0.0,0.0),
    val sellerBacklink: String = "",
    var shortDetails: String = "",
    var price: String = "",
    val negotiable: Boolean = false,
    var details: String = "",
    val photos: List<String> = emptyList(),
    val videos: List<String> = emptyList(),
    var videosList : List<VideoItem> = emptyList(),
    var primaryPhoto: String = "",
    var originalPrimaryPhoto: String = "",
    var impressions: Int = 0,
    var clicks: Int = 0,
    var mapSearched:Int = 0,
    var whatsappDM:Int = 0,
    var call:Int = 0,
    var specifications: List<SpecificationForProduct> = emptyList(),
    var productType: ProductType = ProductType.OLDFREE // New parameter
)

enum class ProductType {
    OLDFREE,
    OLDPAID,
    STOREFREE,
    STOREPAID,
    WORKERFREE,
    WORKERPAID
}

data class SpecificationForProduct(
    var name: String,
    var specificationRoute: String,
    var amount: String,
    var unit: String?
)

data class VideoItem(
    val thumbnail: String?,
    val video: String?
)