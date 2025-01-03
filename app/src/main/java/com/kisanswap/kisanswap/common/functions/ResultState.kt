package com.kisanswap.kisanswap.common.functions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.LiveData

sealed class ResultState<out T> {
    data class Loading(val message: String? = null) : ResultState<Nothing>()
    data class Success<out T>(val data: T) : ResultState<T>()
    data class Error(val exception: Throwable) : ResultState<Nothing>()
}

@Composable
fun <T> HandleResultState(
    resultState: LiveData<ResultState<T>>,
    onSuccess: @Composable (T) -> Unit,
    onError: @Composable (Throwable) -> Unit
) {
    val state by resultState.observeAsState(ResultState.Loading())

    when (state) {
        is ResultState.Loading -> {
            val message = (state as ResultState.Loading).message
            if (message != null) {
                showProgressDialog(message)
            } else {
                hideProgressDialog()
            }
        }
        is ResultState.Success -> {
            hideProgressDialog()
            onSuccess((state as ResultState.Success<T>).data)
        }
        is ResultState.Error -> {
            hideProgressDialog()
            onError((state as ResultState.Error).exception)
        }
    }
}