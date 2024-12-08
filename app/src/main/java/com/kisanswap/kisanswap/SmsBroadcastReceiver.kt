package com.kisanswap.kisanswap

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes

class SmsBroadcastReceiver : BroadcastReceiver() {

    private var otpReceiveListener: OTPReceiveListener? = null

    fun init(otpReceiveListener: OTPReceiveListener?) {
        this.otpReceiveListener = otpReceiveListener
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
            val extras = intent.extras
            val status = extras?.get(SmsRetriever.EXTRA_STATUS) as com.google.android.gms.common.api.Status

            when (status.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    val message = extras.get(SmsRetriever.EXTRA_SMS_MESSAGE) as String
                    // Extract the OTP and set it to the ViewModel
                    Log.e("OTPReceiver", "SMS Received in OTPReceiver: $message")

                    // extract the 6-digit code from the SMS
                    val smsCode = message.let { "[0-9]{6}".toRegex().find(it) }
                    Log.e("OTPReceiver", "OTP fetched from SMS in OTPReceiver: $smsCode")
//                    Log.d("SmsBroadcastReceiver", "SMS Retrieved: $message")
                    otpReceiveListener?.onOTPReceived(smsCode?.value)
                }
                CommonStatusCodes.TIMEOUT -> {
                    // Handle timeout
                    otpReceiveListener?.onOTPTimeOut()
                    Log.d("SmsBroadcastReceiver", "SMS Retriever Timeout")
                }
            }
        }
    }
    interface OTPReceiveListener {
        fun onOTPReceived(otp: String?)
        fun onOTPTimeOut()
    }
}