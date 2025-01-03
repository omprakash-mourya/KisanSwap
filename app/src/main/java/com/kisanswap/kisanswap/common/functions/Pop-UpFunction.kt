package com.kisanswap.kisanswap.common.functions

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
fun LocationPromptDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    if (LocationState.isPopupDisplayed.value) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(text = "Enable Location") },
            text = { Text(text = "Location services are required for this app. Enable location in settings") },
            confirmButton = {
                Button(onClick = {
                    LocationState.disablePopup()
                    onConfirm()
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                Button(onClick = {
                    onDismiss()
                    LocationState.disablePopup()
                }) {
                    Text("Cancel")
                }
            }
        )
    }
}

object LocationState {
    val isPopupDisplayed = mutableStateOf(false)

    fun enablePopup() {
        isPopupDisplayed.value = true
    }
    fun disablePopup() {
        isPopupDisplayed.value = false
    }
}