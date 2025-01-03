package com.kisanswap.kisanswap.common.functions

import android.Manifest
import android.app.Activity
import android.os.Build
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

fun checkAndRequestCameraPermission(context: Context, onPermissionGranted: () -> Unit) {
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
        onPermissionGranted()
    } else {
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(Manifest.permission.CAMERA),
            REQUEST_CAMERA_PERMISSION
        )
    }
}

fun checkAndRequestCallPermission(context: Context, onPermissionGranted: () -> Unit) {
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
        onPermissionGranted()
    } else {
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(Manifest.permission.CALL_PHONE),
            REQUEST_CALL_PERMISSION
        )
    }
}

fun checkAndRequestStoragePermission(activity: Activity) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_WRITE_STORAGE_PERMISSION)
        }
    }
}

const val REQUEST_WRITE_STORAGE_PERMISSION = 100

const val REQUEST_CAMERA_PERMISSION = 1001
const val REQUEST_CALL_PERMISSION = 1001