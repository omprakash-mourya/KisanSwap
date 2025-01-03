package com.kisanswap.kisanswap.screen

import android.app.Activity
import android.content.Context
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.kisanswap.kisanswap.PreferenceManager
import com.kisanswap.kisanswap.SmsBroadcastReceiver
import com.kisanswap.kisanswap.common.functions.hideProgressDialog
import com.kisanswap.kisanswap.common.functions.showProgressDialog
import com.kisanswap.kisanswap.common.functions.startSmsRetriever
import com.kisanswap.kisanswap.auth.repository.AuthRepository
import com.kisanswap.kisanswap.viewmodel.AuthViewModel
import com.kisanswap.kisanswap.viewmodelfactory.AuthViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PhoneAuthScreen(navController: NavController, authRepository: AuthRepository) {
    var phoneNumber by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(authRepository))
    val verificationId by authViewModel.verificationId.observeAsState()
    val context = LocalContext.current
    val preferenceManager = PreferenceManager(context)
    var isOtpFilled by remember { mutableStateOf(false) }
//    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

//    OtpReceiverEffect(
//        activity = context as Activity,
//        context = context,
//        onOtpReceived = { otpR ->
//            otp = otpR
//            if (otp.length == 6) {
//                keyboardController?.hide()
//                isOtpFilled = true
//            }
//        }
//    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        TextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            prefix = {Text("+91")},
            label = { Text("Phone Number") },
            modifier = Modifier.fillMaxWidth()
        )
        Text(phoneNumber)
        Button(
            onClick = {
                showProgressDialog("Sending OTP, AuthScreen")
                authViewModel.sendVerificationCode(phoneNumber)
                hideProgressDialog()
                      },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        ) {
            Text("Send OTP")
        }

        if (verificationId != null) {
            TextField(
                value = otp,
                onValueChange = { otp = it },
                label = { Text("OTP") },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )
            Button(
                onClick = {
                    authViewModel.verifyCode(otp)
                    authViewModel.linkPhoneNumberWithGoogleAccount(otp, ComponentActivity()) { success ->
                        if(success){
                            preferenceManager.setPhoneNumber(phoneNumber)
                            preferenceManager.setSignedInWithPhoneNumber(true)
                            Log.d("PhoneAuthScreen", "Phone number $phoneNumber linked with Google account.")
                            navController.navigate("sell")
                        }
                    }
                          },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                Text("Verify OTP")
            }
        }
    }

//    Scaffold(
//        topBar = {
//            CenterAlignedTopAppBar(
//                modifier = Modifier
//                    .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
//                    .drawWithContent {
//                        drawContent()
//                    },
//                navigationIcon = {
//                    Box(
//                        Modifier
//                            .size(48.dp)
//                            .clickable { navController.popBackStack() }) {
//                        Icon(
//                            painter = painterResource(id = R.drawable.baseline_arrow_back_24),
//                            tint = Color.DarkGray,
//                            contentDescription = "Back",
//                            modifier = Modifier.align(Alignment.Center)
//                        )
//                    }
//                },
//                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = Color.White,
//                    titleContentColor = Color.DarkGray,
//                    actionIconContentColor = Color.DarkGray
//                ),
//                title = { Text(text = "Enter One Time Password") },
//                windowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Horizontal)
//            )
//        },
//        bottomBar = {
//            Button(
//                onClick = {
//                    authViewModel.sendVerificationCode(phoneNumber)
//                },
//                enabled = phoneNumber.isNotEmpty(),
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(24.dp),
//            ) {
//                Text(text = "Send OTP")
//            }
//        }
//    ) { innerPadding ->
//        Surface(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(Color.White)
//                .padding(24.dp),
//            color = Color.White
//        ) {
//            Column(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(innerPadding)
//            ) {
//                TextField(
//                    value = phoneNumber,
//                    onValueChange = { phoneNumber = it },
//                    label = { Text("Phone Number") },
//                    modifier = Modifier.fillMaxWidth()
//                )
//                Spacer(modifier = Modifier.height(16.dp))
//                TextField(
//                    value = otp,
//                    onValueChange = { otp = it },
//                    label = { Text("OTP") },
//                    modifier = Modifier.fillMaxWidth()
//                )
//                Spacer(modifier = Modifier.height(16.dp))
//                Button(
//                    onClick = {
//                        verificationId?.let {
////                            authViewModel.verifyCode(otp)
//                            authViewModel.linkPhoneNumberWithGoogleAccount(otp, ComponentActivity()) { success ->
//                                if(success){
//                                    preferenceManager.setPhoneNumber(phoneNumber)
//                                    preferenceManager.setSignedInWithPhoneNumber(true)
//                                    Log.d("PhoneAuthScreen", "Phone number $phoneNumber linked with Google account.")
//                                    navController.navigate("sell")
//                                }
//                            }
//                        }
//                    },
//                    enabled = isOtpFilled,
//                    modifier = Modifier.fillMaxWidth()
//                ) {
//                    Text(text = "Verify OTP")
//                }
//            }
//        }
//    }

//    LaunchedEffect(Unit) {
//        focusRequester.requestFocus()
//        keyboardController?.show()
//    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OtpReceiverEffect(
    activity: Activity,
    context: Context,
    onOtpReceived: (String) -> Unit
) {
    val otpReceiver = remember { SmsBroadcastReceiver() }
    val lifecycleOwner = LocalLifecycleOwner.current

    /**
     * This function should not be used to listen for Lifecycle.Event.ON_DESTROY because Compose
     * stops recomposing after receiving a Lifecycle.Event.ON_STOP and will never be aware of an
     * ON_DESTROY to launch onEvent.
     *
     * This function should also not be used to launch tasks in response to callback events by way
     * of storing callback data as a Lifecycle.State in a MutableState. Instead, see currentStateAsState
     * to obtain a State that may be used to launch jobs in response to state changes.
     */
    DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            when (event) {
                androidx.lifecycle.Lifecycle.Event.ON_RESUME -> {
                    Log.e("OTPReceiverEffect", "SMS retrieval has been started.")
                    startSmsRetriever(activity)
                    otpReceiver.init(object : SmsBroadcastReceiver.OTPReceiveListener {
                        override fun onOTPReceived(otp: String?) {
                            Log.e("OTPReceiverEffect ", "OTP Received: $otp")
                            otp?.let { onOtpReceived(it) }
                            try {
                                Log.e("OTPReceiverEffect ", "Unregistering receiver")
                                context.unregisterReceiver(otpReceiver)
                            } catch (e: IllegalArgumentException) {
                                Log.e("OTPReceiverEffect ", "Error in registering receiver: ${e.message}}")
                            }
                        }

                        override fun onOTPTimeOut() {
                            Log.e("OTPReceiverEffect ", "Timeout")
                        }
                    })
                    try {
                        Log.e("OTPReceiverEffect ", "Lifecycle.Event.ON_RESUME")
                        Log.e("OTPReceiverEffect ", "Registering receiver")
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            context.registerReceiver(otpReceiver, IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION), Context.RECEIVER_NOT_EXPORTED)
                        } else {
                            activity.registerReceiver(otpReceiver, IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION))
                        }
                    } catch (e: IllegalArgumentException) {
                        Log.e("OTPReceiverEffect ", "Error in registering receiver: ${e.message}}")
                    }
                }
                androidx.lifecycle.Lifecycle.Event.ON_PAUSE -> {
                    try {
                        Log.e("OTPReceiverEffect ", "Lifecycle.Event.ON_PAUSE")
                        Log.e("OTPReceiverEffect ", "Unregistering receiver")
                        context.unregisterReceiver(otpReceiver)
                    } catch (e: IllegalArgumentException) {
                        Log.e("OTPReceiverEffect ", "Error in unregistering receiver: ${e.message}}")
                    }
                }
                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}
