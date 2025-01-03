package com.kisanswap.kisanswap.dataClass

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