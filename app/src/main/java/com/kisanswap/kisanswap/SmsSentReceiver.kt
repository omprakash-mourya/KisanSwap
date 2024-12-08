package com.kisanswap.kisanswap

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.kisanswap.kisanswap.functions.startSmsRetriever
import android.app.PendingIntent
import android.telephony.SmsManager

class SmsSentReceiver : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "SMS_SENT_ACTION") {
            Log.d("SmsSentReceiver", "SMS sent, starting SMS retriever")
            if (context is Activity) {
                startSmsRetriever(context)
            }
        }
    }
}

fun sendSms(context: Context, phoneNumber: String, message: String) {
    val sentIntent = Intent("SMS_SENT_ACTION")
    val sentPendingIntent = PendingIntent.getBroadcast(context, 0, sentIntent, PendingIntent.FLAG_UPDATE_CURRENT)

    val smsManager = SmsManager.getDefault()
    smsManager.sendTextMessage(phoneNumber, null, message, sentPendingIntent, null)
}