package com.kisanswap.kisanswap.user.buyer.model

import androidx.compose.ui.unit.Dp
import com.google.firebase.firestore.GeoPoint

data class User(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val address: String = "",
    val location: GeoPoint = GeoPoint(0.0,0.0),
    val savedProducts: List<String> = emptyList(),
    val myProducts: List<String> = emptyList()
)

data class UserInfo(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val address: String = "",
    val location: GeoPoint = GeoPoint(0.0,0.0),
    val savedProducts: List<String> = emptyList(),
    val myProducts: List<String> = emptyList()
)

data class DeviceInfo(
    var width:Dp,
    var height: Dp,
)

enum class UserType(

)