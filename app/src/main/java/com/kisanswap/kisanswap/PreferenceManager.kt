package com.kisanswap.kisanswap

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.ui.unit.Dp

class PreferenceManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "user_preferences"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_LATITUDE = "latitude"
        private const val KEY_LONGITUDE = "longitude"
    }

    fun setStatusBarPadding(value: Int) {
        sharedPreferences.edit().putInt("status_bar_padding", value).apply()
    }

    fun getStatusBarPadding(): Int {
        return sharedPreferences.getInt("status_bar_padding", 0)
    }

    fun setNavigationBarPadding(value: Int) {
        sharedPreferences.edit().putInt("navigation_bar_padding", value).apply()
    }

    fun getNavigationBarPadding(): Int {
        return sharedPreferences.getInt("navigation_bar_padding", 0)
    }

    fun setEmail(email: String) {
        sharedPreferences.edit().putString("email", email).apply()
    }

    fun getEmail(): String? {
        return sharedPreferences.getString("email", null)
    }

    fun setLoggedIn(value: Boolean) {
        sharedPreferences.edit().putBoolean("logged_in", value).apply()
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("logged_in", false)
    }

    fun setSignedInWithPhoneNumber(value: Boolean) {
        sharedPreferences.edit().putBoolean("signed_in_with_phone_number", value).apply()
    }

    fun isSignedInWithPhoneNumber(): Boolean {
        return sharedPreferences.getBoolean("signed_in_with_phone_number", false)
    }

    fun setPhoneNumber(phoneNumber: String) {
        sharedPreferences.edit().putString("phone_number", phoneNumber).apply()
    }

    fun getPhoneNumber(): String? {
        return sharedPreferences.getString("phone_number", null)
    }

    fun setUserName(userName: String) {
        sharedPreferences.edit().putString(KEY_USER_NAME, userName).apply()
    }

    fun getUserName(): String? {
        return sharedPreferences.getString(KEY_USER_NAME, null)
    }

    fun setLocation(latitude: Double, longitude: Double) {
        sharedPreferences.edit().putString(KEY_LATITUDE, latitude.toString()).apply()
        sharedPreferences.edit().putString(KEY_LONGITUDE, longitude.toString()).apply()
    }

    fun getLocation(): Pair<Double, Double>? {
        val latitude = sharedPreferences.getString(KEY_LATITUDE, null)?.toDoubleOrNull()
        val longitude = sharedPreferences.getString(KEY_LONGITUDE, null)?.toDoubleOrNull()
        return if (latitude != null && longitude != null) {
            Pair(latitude, longitude)
        } else {
            null
        }
    }
}