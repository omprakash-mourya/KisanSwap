package com.kisanswap.kisanswap.common.components

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
fun TractorLoadingAnimation(
    size: Dp = 200.dp
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("tractor-animation.json"))
    LottieAnimation(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        modifier = Modifier.size(size)
    )
}