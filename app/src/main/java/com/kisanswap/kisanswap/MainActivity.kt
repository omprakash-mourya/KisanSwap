package com.kisanswap.kisanswap

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.GoogleAuthProvider
import com.kisanswap.kisanswap.components.BottomNavigationBar
import com.kisanswap.kisanswap.functions.LocationPromptDialog
import com.kisanswap.kisanswap.functions.LocationState
import com.kisanswap.kisanswap.functions.ProgressDialog
import com.kisanswap.kisanswap.functions.REQUEST_CAMERA_PERMISSION
import com.kisanswap.kisanswap.ui.theme.KisanSwapTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
//    private lateinit var smsBroadcastReceiver: SmsBroadcastReceiver
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var navController: NavHostController

    private var pendingDeepLink: String? = null

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIntent(intent)
        // Set the emulator environment variable
//        System.setProperty("firebase.emulator.auth", "localhost:9099");

        FirebaseApp.initializeApp(this)
//         Initialize Firebase App Check
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance()
//            DebugAppCheckProviderFactory.getInstance()
        )
        auth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        enableEdgeToEdge()
        preferenceManager = PreferenceManager(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setContent {
            KisanSwapTheme {
                navController = rememberNavController()
                val deepLinkUri = intent?.data
                val coroutineScope = rememberCoroutineScope()
                LaunchedEffect(navController) {
                    pendingDeepLink?.let { deepLink ->
                        coroutineScope.launch {
                            intent.data?.let { uri ->
                                val productId = uri.getQueryParameter("productId")
                                if (productId != null) {
                                    navController.navigate("buy/$productId")
                                }
                            }
//                            navController.navigate(deepLink)
                            pendingDeepLink = null
                        }
                    }
                }
                NavGraph(navController = navController, startDestination = if (preferenceManager.isLoggedIn()) "home" else "auth")
                ProgressDialog()
                if(!isLocationEnabled()){
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
//                val navController = rememberNavController()
                /*intent?.data?.let { uri ->
                    val productId = uri.getQueryParameter("productId")
                    if (productId != null) {
                        navController.navigate("buy/$productId")
                    }
                }*/

                LaunchedEffect(deepLinkUri) {
                    deepLinkUri?.let { uri ->
                        val productId = uri.getQueryParameter("productId")
                        if (productId != null) {
                            navController.navigate("buy/$productId")
                        }
                    }
                }

//                MainScreen()
            }
        }
        checkAndRequestLocationPermission()

//                startSmsRetriever()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
        /*setIntent(intent)
        intent.data?.let { uri ->
            val productId = uri.getQueryParameter("productId")
            if (productId != null) {
                navController.navigate("buy/$productId")
            }
        }*/
    }

    private fun handleIntent(intent: Intent?) {
        intent?.data?.let { uri ->
            val deepLink = uri.toString()
            if (::navController.isInitialized) {
                intent.data?.let { uri ->
                    val productId = uri.getQueryParameter("productId")
                    if (productId != null) {
                        navController.navigate("buy/$productId")
                    }
                }
//                navController.navigate(deepLink)
            } else {
                pendingDeepLink = deepLink
            }
        }
    }

    private fun checkAndRequestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION)
            Log.d("MainActivity", "Location permission granting")
            getLocation()
        } else {
            if (isLocationEnabled()) {
                getLocation()
                Log.d("MainActivity", "Location enabled")
            } else {
                Log.d("MainActivity", "Location disabled")
//                onLocationDisabled()
            }
        }
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                auth.signInWithCredential(credential)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success
                            Log.d("MainActivity", "Google sign-in successful")
                        } else {
                            // Sign in failed
                            Log.w("MainActivity", "Google sign-in failed")
                            if (task.exception is FirebaseAuthInvalidUserException) {
                                promptUserToSelectAnotherAccount()
                            }
                        }
                    }.addOnFailureListener(this) { e ->
                        if (e is FirebaseAuthInvalidUserException) {
                            Log.d("MainActivity", "Google sign-in failed, user disabled")
                            promptUserToSelectAnotherAccount()
                        } else {
                            Log.w("MainActivity", "Google sign-in failed", e)
                            // Handle other failures
                        }
                    }
            } catch (e: ApiException) {
                // Handle error
                Log.w("MainActivity", "Google sign-in failed", e)
            }
        }
    }

    private fun promptUserToSelectAnotherAccount() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)} passing\n      in a {@link RequestMultiplePermissions} object for the {@link ActivityResultContract} and\n      handling the result in the {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Permission granted, proceed with taking the photo
                handleTakePhotoClick()
            } else {
                // Permission denied, show a message to the user
                Toast.makeText(this, "Camera permission is required to take photos", Toast.LENGTH_SHORT).show()
            }
        }
        else if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                if (isLocationEnabled()) {
                    getLocation()
                    Log.d("MainActivity", "Getting Location")
                } else {
                    promptEnableLocation()
                    Log.d("MainActivity", "Location enabling PermissionLauncher")
                    getLocation()
                }
            } else {
                Toast.makeText(this, "Location permission is required to get your location", Toast.LENGTH_SHORT).show()
            }
        } else if (requestCode == REQUEST_CALL_PERMISSION) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                handleMakingCall()
            } else {
                Toast.makeText(this, "Call permission is required to make a call", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun promptEnableLocation() {
        Toast.makeText(this, "Please enable location services", Toast.LENGTH_SHORT).show()
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }

    private fun getLocation() {
        Log.d("MainActivity", "GetLocation executed")
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        if(location.latitude == 0.0 && location.longitude == 0.0){
                            Log.d("MainActivity", "Location is 0.0, 0.0")
                            Toast.makeText(this, "Location not enabled", Toast.LENGTH_SHORT).show()
                            promptEnableLocation()
                        } else{
                            preferenceManager.setLocation(it.latitude, it.longitude)
                            Log.d("MainActivity", "Location set to: ${it.latitude}, ${it.longitude}")
                            Toast.makeText(this, "Location updated!", Toast.LENGTH_SHORT).show()
                        }
                    } ?: run {
                        Log.w("MainActivity", "Location is null")
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("MainActivity", "Failed to get location", e)
                }
        } else{
            Log.w("MainActivity", "Location permission not granted, getLocation executed")
        }
    }

    private fun handleTakePhotoClick() {
        // Implementation for handling the take photo click
        Log.d("MainActivity", "Camera permission granted, taking photo")
    }

    private fun handleMakingCall() {
        // Implementation for handling the take photo click
        Log.d("MainActivity", "Call permission granted, making call")
    }

    companion object {
        const val RC_SIGN_IN = 9001
        private const val REQUEST_LOCATION_PERMISSION = 1002
        private const val REQUEST_CALL_PERMISSION = 1001
    }

    override fun onDestroy() {
        super.onDestroy()
        CacheManager.clearCache()
//        unregisterReceiver(smsBroadcastReceiver)
    }
}

@Composable
fun MyApp(navController: NavController, innerPage: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()  // Add padding for the status bar
            .navigationBarsPadding()  // Add padding for the navigation bar
            .background(color = MaterialTheme.colorScheme.background)
    ){
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ){
            innerPage()
        }
        BottomNavigationBar(navController)
    }
}

@Composable
fun FullPage(innerPage: @Composable () -> Unit){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()  // Add padding for the status bar
            .navigationBarsPadding()  // Add padding for the navigation bar
            .background(color = MaterialTheme.colorScheme.background)
    ){
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ){
            innerPage()
        }
    }
}