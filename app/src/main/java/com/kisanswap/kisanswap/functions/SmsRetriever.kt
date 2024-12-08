package com.kisanswap.kisanswap.functions

import android.app.Activity
import android.content.Context
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.kisanswap.kisanswap.SmsBroadcastReceiver

@RequiresApi(Build.VERSION_CODES.O)
fun startSmsRetriever(activity: Activity) {
    val client = SmsRetriever.getClient(activity)
    val task = client.startSmsRetriever()
    task.addOnSuccessListener {
        // Successfully started retriever, expect broadcast intent
        Log.e("OTPReceiver", "startSMSRetrieverClient addOnSuccessListener")
    }
    task.addOnFailureListener { e ->
        Log.e("OTPReceiver", "startSMSRetrieverClient addOnFailureListener" + e.stackTrace)
        // Failed to start retriever, inspect Exception for more details
    }

//    val smsBroadcastReceiver = SmsBroadcastReceiver()
//    val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
//
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//        ContextCompat.registerReceiver(activity, smsBroadcastReceiver, intentFilter, ContextCompat.RECEIVER_NOT_EXPORTED)
//    } else {
//        activity.registerReceiver(smsBroadcastReceiver, intentFilter)
//    }
}