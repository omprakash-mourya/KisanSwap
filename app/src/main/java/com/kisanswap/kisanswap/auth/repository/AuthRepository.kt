package com.kisanswap.kisanswap.auth.repository

import android.content.Intent
import android.util.Log
import androidx.activity.ComponentActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseException
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.kisanswap.kisanswap.MainActivity
import com.kisanswap.kisanswap.R
import com.kisanswap.kisanswap.user.buyer.model.User
import com.kisanswap.kisanswap.common.functions.hideProgressDialog
import com.kisanswap.kisanswap.common.functions.showProgressDialog
import java.util.concurrent.TimeUnit

class AuthRepository(private val activity: ComponentActivity) {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val googleSignInClient: GoogleSignInClient
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    init {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(activity.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(activity, gso)

        Log.d("AuthRepository", "AuthRepository initialized")

    }

    fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        Log.d("AuthRepository", "Launching Google sign-in intent")
        activity.startActivityForResult(signInIntent, MainActivity.RC_SIGN_IN)
    }

    fun handleGoogleSignInResult(data: Intent?, onComplete: (Boolean) -> Unit) {
        Log.d("AuthRepository", "Handling Google sign-in result")
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            auth.signInWithCredential(credential)
                .addOnCompleteListener(activity) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        user?.let {
                            checkUserExists(it.uid) { exists ->
                                if (!exists) {
                                    createUserInFirestore(it.uid, it.displayName ?: "", it.email ?: "")
                                }
                                onComplete(true)
                            }
                        }
                        Log.d("AuthRepository", "Google Sign In Successful")
                    } else {
                        onComplete(false)
                        Log.w("AuthRepository", "Google Sign In Failed")
                    }
                }.addOnFailureListener(activity) { e ->
                    if (e is FirebaseAuthInvalidUserException) {
                        Log.w("AuthRepository", "Google Sign In Failed, User Disabled", e)
                        onComplete(false)
                    } else {
                        Log.w("AuthRepository", "Google Sign In Failed, FailureListener", e)
                        onComplete(false)
                    }
                }.addOnCanceledListener(activity) {
                    Log.w("AuthRepository", "Google Sign In Failed, CanceledListener")
                    onComplete(false)
                }
        } catch (e: ApiException) {
            Log.w("AuthRepository","Google Sign In Failed, onComplete = false",e)
            onComplete(false)
        }
    }

    private fun checkUserExists(userId: String, onComplete: (Boolean) -> Unit) {
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                onComplete(document.exists())
            }
            .addOnFailureListener {
                onComplete(false)
            }
    }

    private fun createUserInFirestore(userId: String, name: String, email: String) {
        val user = User(name = name, email = email)
        db.collection("users").document(userId).set(user)
    }

    fun sendVerificationCode(phoneNumber: String, activity: ComponentActivity, callback: (String) -> Unit) {

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    showProgressDialog("Verification Completed")
                    val code = credential.smsCode
                    if (code != null) {
                        callback(code)
                    }
//                    hideProgressDialog()
                }

                override fun onVerificationFailed(exception: FirebaseException) {
                    // Handle error
                    showProgressDialog("Verification Failed \n $exception")
                    Log.w("Auth", "Verification failed", exception)
//                    hideProgressDialog()
                }

                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    showProgressDialog("Code Sent")
                    callback(verificationId)
//                    hideProgressDialog()
                }
            })
            .build()

//        PhoneAuthProvider.verifyPhoneNumber(options)

        FirebaseAppCheck.getInstance().getToken(true)
            .addOnSuccessListener { token ->

                // Include the token in the request headers
                // ... (Your code to send the token)
                showProgressDialog("App Check Success")
                PhoneAuthProvider.verifyPhoneNumber(options)
                hideProgressDialog()
            }
            .addOnFailureListener { exception ->
                // Handle App Check token generation error
                showProgressDialog("App Check Failed")
                Log.w("Auth", "App Check token generation failed", exception)
                hideProgressDialog()
            }
    }

    fun verifyCode(verificationId: String, code: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId, code)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success
                } else {
                    // Sign in failed
                }
            }
    }

    fun linkPhoneNumberWithGoogleAccount(verificationId: String, code: String, activity: ComponentActivity, callback: (Boolean) -> Unit) {
    val credential = PhoneAuthProvider.getCredential(verificationId, code)
    val user = FirebaseAuth.getInstance().currentUser

    user?.linkWithCredential(credential)
        ?.addOnCompleteListener(activity) { task ->
            if (task.isSuccessful) {
                // Phone number linked successfully
                callback(true)
            } else {
                // Handle error
                callback(false)
            }
        }
    }
}