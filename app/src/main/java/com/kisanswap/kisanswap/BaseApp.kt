package com.kisanswap.kisanswap

import androidx.compose.runtime.Composable
import androidx.core.location.LocationManagerCompat.isLocationEnabled
import com.kisanswap.kisanswap.functions.LocationPromptDialog
import com.kisanswap.kisanswap.functions.LocationState
import com.kisanswap.kisanswap.functions.ProgressDialog
import com.kisanswap.kisanswap.ui.theme.KisanSwapTheme

/*
@Composable
fun KisanSwapApp(preferenceManager: PreferenceManager) {
    KisanSwapTheme {
        NavGraph(startDestination = if (preferenceManager.isLoggedIn()) "home" else "auth")
        ProgressDialog()
        if (!isLocationEnabled()) {
            LocationState.enablePopup()
            LocationPromptDialog(
                onConfirm = {
                    promptEnableLocation()
                    checkAndRequestLocationPermission()
                },
                onDismiss = {
                    LocationState.disablePopup()
                }
            )
        }
        // Handle deep link
        val navController = rememberNavController()
        val context = LocalContext.current
        LaunchedEffect(context) {
            (context as? MainActivity)?.intent?.data?.let { uri ->
                val productId = uri.getQueryParameter("productId")
                if (productId != null) {
                    navController.navigate("buy/$productId")
                }
            }
        }
    }
}
*/
