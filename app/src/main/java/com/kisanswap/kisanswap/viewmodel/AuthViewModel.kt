package com.kisanswap.kisanswap.viewmodel

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kisanswap.kisanswap.repositories.AuthRepository

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _verificationId = MutableLiveData<String>()
    val verificationId: LiveData<String> get() = _verificationId

    private val _otp = MutableLiveData<String>()
    val otp: LiveData<String> get() = _otp

    fun sendVerificationCode(phoneNumber: String) {
        authRepository.sendVerificationCode(phoneNumber, activity = ComponentActivity()) { verificationId ->
            _verificationId.value = verificationId
        }
    }

    fun verifyCode(code: String) {
        _otp.value = code
        authRepository.verifyCode(_verificationId.value!!, code)
    }

    fun signInWithGoogle() {
        authRepository.signInWithGoogle()
    }

    fun handleGoogleSignInResult(data: Intent?, onComplete: (Boolean) -> Unit) {
        authRepository.handleGoogleSignInResult(data) { success ->
            onComplete(success)
        }
    }

    fun linkPhoneNumberWithGoogleAccount( code: String, activity: ComponentActivity, callback:(Boolean) -> Unit){
        authRepository.linkPhoneNumberWithGoogleAccount(verificationId.value!!, code, activity){ success ->
            callback(success)
        }
    }
}