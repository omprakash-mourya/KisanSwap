package com.kisanswap.kisanswap.common.functions

import android.util.Log
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import com.google.firebase.firestore.GeoPoint
import kotlin.math.*
import java.text.NumberFormat
import java.util.Locale

fun formatPrice(price: Int): String {
//    val priceStr = reformatPrice(price)
    val reversed = price.toString().reversed()
    val formatted = StringBuilder()

    for (i in reversed.indices) {
        if (i == 3 || (i > 3 && (i - 3) % 2 == 0)) {
            formatted.append(',')
        }
        formatted.append(reversed[i])
    }
    return formatted.reverse().toString()
}

fun reformatPrice(price: String): Int {
    val notNullPrice = if(price != "") price else "0"
    val filteredPrice = notNullPrice.filter { it.isDigit() || it == '.' }

    var length = 0
    // Ensure only one dot is present
    val dotIndex = filteredPrice.indexOf('.')
    val formattedPrice = if (dotIndex != -1) {
        val beforeDot = filteredPrice.substring(0, dotIndex).filter { it.isDigit() }
        val afterDot = filteredPrice.substring(dotIndex + 1).filter { it.isDigit() }
        length = afterDot.length + beforeDot.length
        "$beforeDot.$afterDot"
    } else {
        filteredPrice
    }
    /*if (length < 11) {
        Log.w("reformatPrice", "Price has more than 2 decimal places")
        return formattedPrice.toInt()
    } else {
        Log.w("reformatPrice", "Price has more than 10 digits")
        return formattedPrice.toLong()
    }*/
    //can't enter more than 10 digits, because Int can only store 10 digits at max
    return formattedPrice.toInt()

//    val formattedPrice = notNullPrice.replace(Regex("[^0-9]"), "")

}

fun String.withThousands(separator: Char = ','): String {
    val original = this
    return buildString {
        original.indices.forEach { position ->
            val realPosition = original.lastIndex - position
            val character = original[realPosition]
            insert(0, character)
            if (position != 0 && realPosition != 0 && position % 3 == 2) {
                insert(0, separator)
            }
        }
    }
}

fun String.withIndianNumberSystem(): String {
    val priceStr = reformatPrice(this).toString()
    val reversed = priceStr.reversed()
    val formatted = StringBuilder()

    for (i in reversed.indices) {
        if (i == 3 || (i > 3 && (i - 3) % 2 == 0)) {
            formatted.append(',')
        }
        formatted.append(reversed[i])
    }
    return formatted.reverse().toString()
}

fun numberMask(
    text: String,
    thousandSeparator: (String) -> String = { thisText ->
        thisText.withIndianNumberSystem()
                                            },
): TransformedText {
    if(text.isEmpty()) return TransformedText(AnnotatedString(""), OffsetMapping.Identity)
    val out = thousandSeparator(text)
    val offsetMapping = object : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int {
            /*if (text.isEmpty()) return offset// Handle empty text
            val rightOffset = text.lastIndex - offset
            val commasToTheRight = rightOffset / 3
            return out.lastIndex - rightOffset - commasToTheRight*/
            var transformedOffset = 0
            var originalOffset = 0
            while (originalOffset < offset) {
                if (out[transformedOffset] != ',') {
                    originalOffset++
                }
                transformedOffset++
            }
            return transformedOffset
        }

        override fun transformedToOriginal(offset: Int): Int {
            /*if (out.isEmpty()) return offset // Handle empty transformed text
            val totalCommas = ((text.length - 1) / 3).coerceAtLeast(0)
            val rightOffset = out.length - offset
            val commasToTheRight = rightOffset / 4
            return (offset - (totalCommas - commasToTheRight))*/

            var transformedOffset = 0
            var originalOffset = 0
            while (transformedOffset < offset) {
                if (out[transformedOffset] != ',') {
                    originalOffset++
                }
                transformedOffset++
            }
            return originalOffset
        }
    }
    return TransformedText(AnnotatedString(out), offsetMapping)
}

fun noTransFormation(
    text: String,
): TransformedText {
    val offsetMapping = object : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int {
            return offset
        }
        override fun transformedToOriginal(offset: Int): Int {
            return offset
        }
    }
    return TransformedText(AnnotatedString(text), offsetMapping)
}

fun calculateDistance(start: GeoPoint, end: GeoPoint): Int {
    val earthRadiusKm = 6371.0

    val dLat = Math.toRadians(end.latitude - start.latitude)
    val dLon = Math.toRadians(end.longitude - start.longitude)

    val lat1 = Math.toRadians(start.latitude)
    val lat2 = Math.toRadians(end.latitude)

    val a = sin(dLat / 2) * sin(dLat / 2) +
            sin(dLon / 2) * sin(dLon / 2) * cos(lat1) * cos(lat2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    return (earthRadiusKm * c).toInt()
}

//fun GetLocation(preferenceManager: PreferenceManager) {
//    val locationManager = getSystemService() as LocationManager
//    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//        val location: Location? = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
//        location?.let {
//            preferenceManager.setLocation(it.latitude, it.longitude)
//            val location = preferenceManager.getLocation()
//            Log.d("MainActivity", "Location set to: ${location.toString()}")
//        }
//    } else {
//        Log.w("MainActivity", "Location permission not granted, getLocation exeecuted")
//    }
//}

