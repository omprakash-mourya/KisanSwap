package com.kisanswap.kisanswap.common.functions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kisanswap.kisanswap.common.components.TractorLoadingAnimation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun ProgressDialog() {
    if (ProgressDialogState.isDisplayed) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE4ECE4),
                    disabledContainerColor = MaterialTheme.colorScheme.onBackground,
                ),
                modifier = Modifier.padding(16.dp).wrapContentSize(),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    TractorLoadingAnimation(size = 100.dp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = ProgressDialogState.text ?: "Loading...",
                        color = Color.Black,
                        fontSize = 16.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}

object ProgressDialogState {
    var isDisplayed by mutableStateOf(false)
    var text by mutableStateOf<String?>(null)
}

fun showProgressDialog(customText: String? = null) {
    ProgressDialogState.isDisplayed = true
    ProgressDialogState.text = customText
}

fun hideProgressDialog() {
    ProgressDialogState.isDisplayed = false
    ProgressDialogState.text = null
}