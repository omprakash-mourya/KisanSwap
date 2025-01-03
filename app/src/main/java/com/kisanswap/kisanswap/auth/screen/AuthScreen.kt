package com.kisanswap.kisanswap.screen

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.kisanswap.kisanswap.PreferenceManager
import com.kisanswap.kisanswap.common.functions.hideProgressDialog
import com.kisanswap.kisanswap.common.functions.showProgressDialog
import com.kisanswap.kisanswap.auth.repository.AuthRepository
import com.kisanswap.kisanswap.viewmodel.AuthViewModel
import com.kisanswap.kisanswap.viewmodelfactory.AuthViewModelFactory

@Composable
fun AuthScreen(navController: NavController, authRepository: AuthRepository) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(authRepository))
    var phoneNumber by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    val verificationId by authViewModel.verificationId.observeAsState()
    val preferenceManager = PreferenceManager(LocalContext.current)

    val context = LocalContext.current
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Log.d("AuthScreen", "Google sign-in result code: ${result.resultCode}")
        if (result.resultCode == Activity.RESULT_OK) {
            showProgressDialog("Signing in with Google")
            val data: Intent? = result.data
            authViewModel.handleGoogleSignInResult(data) { success ->
                if (success) {
                    Log.d("AuthScreen", "Google sign-in successful")
                    navController.navigate("home")
                    preferenceManager.setLoggedIn(true)
                    hideProgressDialog()
                } else {
                    // Handle sign-in failure
                    hideProgressDialog()
                    Log.w("AuthScreen", "Google sign-in failed")
                }
            }
        } else if (result.resultCode == Activity.RESULT_CANCELED) {
            // Handle cancellation
            Log.w("AuthScreen", "Google sign-in canceled by user")
        } else{
            Log.w("AuthScreen", "Google sign-in failed, wrong activity code ${result.resultCode}")
        }
    }

    Column(
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
    ) {

//        TextField(value = phoneNumber, onValueChange = { phoneNumber = it }, label = { Text("Phone Number") })
//        Button(onClick = { authViewModel.sendVerificationCode(phoneNumber) }) {
//            Text("Send OTP")
//        }

        Button(onClick = {
            Log.d("AuthScreen", "Launching Google sign-in intent")
            googleSignInLauncher.launch(authRepository.googleSignInClient.signInIntent)
//            authViewModel.signInWithGoogle()
        }) {
            Text("Sign in with Google")
        }

        if (verificationId != null) {
            TextField(value = otp, onValueChange = { otp = it }, label = { Text("OTP") })
            Button(onClick = { authViewModel.verifyCode(otp) }) {
                Text("Verify OTP")
            }
        }
    }
}