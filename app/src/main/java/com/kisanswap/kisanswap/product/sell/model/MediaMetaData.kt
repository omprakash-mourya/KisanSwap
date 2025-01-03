package com.kisanswap.kisanswap.dataClass

import androidx.compose.ui.graphics.painter.Painter

data class MediaMetaData(
    val url: String,
    val width: Int,
    val height: Int,
    val rotation: Int
)

data class ImageMetaData(
    val rotation: Int,
    val isPrimary: Boolean = false
)

data class DownloadedImage(
    val painter: Painter,
    val metadata: ImageMetaData
)
