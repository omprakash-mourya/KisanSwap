package com.kisanswap.kisanswap.functions

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.MediaMetadataRetriever
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toComposeRect
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.exifinterface.media.ExifInterface
import com.google.gson.Gson
import com.kisanswap.kisanswap.CacheManager
import com.kisanswap.kisanswap.dataClass.ImageMetaData
import com.kisanswap.kisanswap.dataClass.MediaMetaData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.io.InputStream
import java.io.RandomAccessFile

suspend fun downloadVideoChunk(context: Context, url: String, startByte: Long, endByte: Long): String? = withContext(Dispatchers.IO) {
    if (url.isEmpty()) {
        Log.e("MediaFunctions", "URL is empty")
        return@withContext null
    }

    Log.d("MediaFunctions", "Downloading video chunk from url: $url, bytes: $startByte-$endByte")

    return@withContext try {
        val link = URL(url)
        val connection = link.openConnection() as HttpURLConnection
        connection.setRequestProperty("Range", "bytes=$startByte-$endByte")
        connection.doInput = true
        connection.connect()

        if (connection.responseCode != HttpURLConnection.HTTP_PARTIAL) {
            Log.e("MediaFunctions", "Failed to connect to url: $url")
            return@withContext null
        }

        val input: InputStream = connection.inputStream
        val file = File(context.cacheDir, "downloaded_video_chunk_$startByte-$endByte.mp4")
        val output = RandomAccessFile(file, "rw")
        output.seek(startByte)

        val buffer = ByteArray(1024)
        var bytesRead: Int
        while (input.read(buffer).also { bytesRead = it } != -1) {
            output.write(buffer, 0, bytesRead)
        }
        if(file.exists()){
            Log.d("MediaFunctions", "Video chunk file exists")
            if(file.absolutePath.isNotEmpty()){
                Log.d("MediaFunctions", "Video chunk file path exists")
            }
        } else {
            Log.d("MediaFunctions", "Video chunk file don't exist")
        }

        input.close()
        output.close()

        Log.d("MediaFunctions", "Video chunk downloaded and cached: $file")
        file.absolutePath
    } catch (e: Exception) {
        Log.e("MediaFunctions", "Failed to download video chunk from url: $url")
        e.printStackTrace()
        null
    }
}

suspend fun loadImageFromUrlAsBitmap(url: String): Bitmap? = withContext(Dispatchers.IO) {

    if (url.isEmpty()) {
        Log.e("MediaFunctions", "URL is empty")
        return@withContext null
    }

    Log.d("MediaFunctions", "Loading image from url: $url")

    return@withContext try {
        // Get reference to the image in Firebase Storage
//        val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(url)
//        // Generate a download URL with a query parameter to request a lower resolution version
//        val downloadUrl = storageRef.child("photos/${storageRef.name}?alt=media&token=${storageRef.downloadUrl.await()}&sz=w200-h200")

        val link = URL(url)
        val connection = link.openConnection() as HttpURLConnection
        connection.doInput = true
        connection.connect()

        Log.d("MediaFunctions", "Connection established")
        val input = connection.inputStream

        val bitmap = BitmapFactory.decodeStream(input)
//        val bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream())

        input.close()

        if (bitmap == null) {
            Log.e("MediaFunctions", "Failed to decode image from url: $url")
        }

        bitmap


    } catch (e: Exception) {
        Log.e("MediaFunctions", "Failed to load image from url: $url")
        e.printStackTrace()
        null
    } finally {
        // Close the connection

    }

}

suspend fun loadImageFromUrl(url: String): Painter? = withContext(Dispatchers.IO) {

    if (url.isEmpty()) {
        Log.e("MediaFunctions", "URL is empty")
        return@withContext null
    }

    Log.d("MediaFunctions", "Loading image from url: $url")

    return@withContext try {
        // Get reference to the image in Firebase Storage
//        val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(url)
//        // Generate a download URL with a query parameter to request a lower resolution version
//        val downloadUrl = storageRef.child("photos/${storageRef.name}?alt=media&token=${storageRef.downloadUrl.await()}&sz=w200-h200")

        val link = URL(url)
        val connection = link.openConnection() as HttpURLConnection
        connection.doInput = true
        connection.connect()

        Log.d("MediaFunctions", "Connection established")
        val input = connection.inputStream

        var bitmap = BitmapFactory.decodeStream(input)
//        val bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream())

        input.close()

        if (bitmap == null) {
            Log.e("MediaFunctions", "Failed to decode image from url: $url")
        }

        // Resize the bitmap if it exceeds the maximum texture size
        val maxTextureSize = 4096
        if (bitmap.width > maxTextureSize || bitmap.height > maxTextureSize) {
            val aspectRatio = bitmap.width.toFloat() / bitmap.height.toFloat()
            val newWidth: Int
            val newHeight: Int
            if (bitmap.width > bitmap.height) {
                newWidth = maxTextureSize
                newHeight = (maxTextureSize / aspectRatio).toInt()
            } else {
                newHeight = maxTextureSize
                newWidth = (maxTextureSize * aspectRatio).toInt()
            }
            bitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
            Log.d("MediaFunctions", "Bitmap resized to: ${bitmap.width}x${bitmap.height}")
        }

        Log.d("MediaFunctions", "Image loaded successfully from url: $url")
        BitmapPainter(bitmap.asImageBitmap())


    } catch (e: Exception) {
        Log.e("MediaFunctions", "Failed to load image from url: $url")
        e.printStackTrace()
        null
    } finally {
        // Close the connection

    }

}

suspend fun downloadVideoFromUrl(context: Context, url: String): String? = withContext(Dispatchers.IO) {
    if (url.isEmpty()) {
        Log.e("MediaFunctions", "URL is empty")
        return@withContext null
    }

    Log.d("MediaFunctions", "Downloading video from url: $url")

    return@withContext try {
        val link = URL(url)
        val connection = link.openConnection() as HttpURLConnection
        connection.doInput = true
        connection.connect()

        if (connection.responseCode != HttpURLConnection.HTTP_OK) {
            Log.e("MediaFunctions", "Failed to connect to url: $url")
            return@withContext null
        }

        /*val input: InputStream = connection.inputStream
        val file = File(context.cacheDir, "downloaded_video.mp4")
        val output = FileOutputStream(file)*/

        val fileName = url.substring(url.lastIndexOf('/') + 1)/*.replace("%2F", "_").replace("%3A", "_")*/
//        val fileName = URLDecoder.decode(url.substring(url.lastIndexOf('/') + 1), "UTF-8")
        val file = File(context.cacheDir, fileName)
        val input = connection.inputStream
        val output = FileOutputStream(file)

        val buffer = ByteArray(1024)
        var bytesRead: Int
        while (input.read(buffer).also { bytesRead = it } != -1) {
            output.write(buffer, 0, bytesRead)
        }
        output.close()
        input.close()

        Log.d("MediaFunctions", "Video downloaded successfully from url: $url and saved at ${file.absolutePath}")
        connection.disconnect()
        file.absolutePath
    } catch (e: Exception) {
        Log.e("MediaFunctions", "Failed to download video from url: $url", e)
        null
    }
}

fun saveVideoMetadata(context: Context, videoPath: String) {
    val retriever = MediaMetadataRetriever()
    try {
        val file = File(videoPath)
        if(file.exists()){
            retriever.setDataSource(videoPath)
            val width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toInt() ?: 0
            val height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toInt() ?: 0
            val rotation = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)?.toInt() ?: 0

            val metadata = MediaMetaData(
                url = videoPath,
                width = width,
                height = height,
                rotation = rotation
            )

            val metadataFile = File(videoPath.replace(".mp4", ".json"))
            metadataFile.writeText(Gson().toJson(metadata))
            Log.d("BuyViewModel", "Video metadata saved successfully at ${metadataFile.absolutePath}")
        } else {
            Log.e("MediaFunctions", "File does not exist: $videoPath")
            return
        }

    } catch (e: Exception) {
        Log.e("BuyViewModel", "Error saving video metadata", e)
    } finally {
        retriever.release()
    }
}

fun getVideoMetadata(videoUrl: String): MediaMetaData? {
    val videoPath = CacheManager.getVideo(videoUrl) ?: return null
    val metadataFile = File(videoPath.replace(".mp4", ".json"))
    return if (metadataFile.exists()) {
        Gson().fromJson(metadataFile.readText(), MediaMetaData::class.java)
    } else {
        null
    }
}

suspend fun resizeBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
    val width = bitmap.width
    val height = bitmap.height
    val scaleWidth = maxWidth.toFloat() / width
    val scaleHeight = maxHeight.toFloat() / height
    val scaleFactor = Math.min(scaleWidth, scaleHeight)

    val matrix = Matrix()
    matrix.postScale(scaleFactor, scaleFactor)

    return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
}

// In MediaFunctions.kt
suspend fun generateVideoThumbnail(videoPath: String): Bitmap? = withContext(Dispatchers.IO) {
    val retriever = MediaMetadataRetriever()
    return@withContext try {
        retriever.setDataSource(videoPath)
        retriever.getFrameAtTime(0)
    } catch (e: Exception) {
        null
    } finally {
        retriever.release()
    }
}

fun isPhotoVertical(painter: Painter): Boolean {
    val intrinsicSize = painter.intrinsicSize
    return intrinsicSize.height > intrinsicSize.width
}

fun cropToGoldenRatio(painter: Bitmap, goldenRatio: Float): Bitmap {
    val bitmap = painter
    val width = bitmap.width
    val height = bitmap.height
    val newWidth: Int
    val newHeight: Int

    if (height.toFloat() / width > goldenRatio) {
        // Crop height
        newHeight = (width * goldenRatio).toInt()
        newWidth = width
    } else {
        // Crop width
        newWidth = (height / goldenRatio).toInt()
        newHeight = height
    }

    val xOffset = (width - newWidth) / 2
    val yOffset = (height - newHeight) / 2

    return Bitmap.createBitmap(bitmap, xOffset, yOffset, newWidth, newHeight)
}

/*
fun DrawScope.cropPainterWithGoldenRatio(
    painter: Painter,
    goldenRatio: Float = 1.618f,
    colorFilter: ColorFilter? = null
) {
    val painterIntrinsicSize = painter.intrinsicSize
    val width = size.width
    val height = size.height

    val targetWidth = if (width / height > goldenRatio) height * goldenRatio else width
    val targetHeight = if (width / height > goldenRatio) height else width / goldenRatio

    val scale = minOf(width / targetWidth, height / targetHeight)

    val offsetX = (width - targetWidth * scale) / 2
    val offsetY = (height - targetHeight * scale) / 2

    withTransform({
        scale(scale)
        translate(offsetX, offsetY)
    }) {
        drawIntoCanvas { canvas ->
                painter.draw(
                    canvas,
                    Size(targetWidth, targetHeight),
                    colorFilter
                )
        }
    }
}

fun painterToBitmap(painter: Painter, width: Int, height: Int, density: Density): Bitmap {
    val imageBitmap = ImageBitmap(width, height)
    val canvas = androidx.compose.ui.graphics.Canvas(imageBitmap.asAndroidBitmap())
    val paint = android.graphics.Paint().apply {
        isAntiAlias = true
        isFilterBitmap = true
    }
    with(density) {
        painter.apply {
            draw(canvas, android.graphics.Rect(0, 0, width, height).toComposeRect(), paint)
        }
    }
    return imageBitmap.asAndroidBitmap()
}
*/

fun cropBitmap(bitmap: Bitmap, x: Int, y: Int, width: Int, height: Int): Bitmap {
    return Bitmap.createBitmap(bitmap, x, y, width, height)
}

/*
@Composable
fun CroppedImage(painter: Painter) {
    Canvas(modifier = Modifier.size(200.dp)) {
        cropPainterWithGoldenRatio(painter)
    }
}*/
