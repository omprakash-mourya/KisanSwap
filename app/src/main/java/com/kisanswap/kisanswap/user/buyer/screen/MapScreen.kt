package com.kisanswap.kisanswap.user.buyer.screen

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapScreen(navController: NavController, onLocationSelected: (LatLng) -> Unit) {
    val context = LocalContext.current
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    userLocation = LatLng(it.latitude, it.longitude)
                } ?: run {
                    userLocation = LatLng(19.0760, 72.8777)
                }
            }
        }
    }
    if(userLocation == null){
        userLocation = LatLng(19.0760, 72.8777)
    }else{
        Text("User Location: $userLocation", modifier = Modifier.padding(16.dp))
    }

    Text("MapScreen", modifier = Modifier.padding(16.dp))

    userLocation?.let { location ->
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(location, 15f)
        }
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onMapClick = { latLng ->
                selectedLocation = latLng
            }
        ) {
            selectedLocation?.let {
                Marker(
                    state = MarkerState(position = it),
                    title = "Selected Location"
                )
            }
        }

        Button(
            onClick = {
                selectedLocation?.let {
                    onLocationSelected(it)
                    navController.popBackStack()
                }
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Confirm Location")
        }
    }
}