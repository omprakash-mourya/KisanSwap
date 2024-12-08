package com.kisanswap.kisanswap.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import com.kisanswap.kisanswap.dataClass.MediaMetaData
import com.kisanswap.kisanswap.repositories.FirestoreRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.io.IOException

class SellViewModel(private val firestoreRepository: FirestoreRepository) : ViewModel() {
    var name by mutableStateOf("")
    var details by mutableStateOf("")
    var price by mutableStateOf(0)
    var category by mutableStateOf("")
    var selectedPhotos by mutableStateOf<List<Uri>>(emptyList())
    var selectedVideos by mutableStateOf<List<Uri>>(emptyList())
    var primaryPhoto by mutableStateOf<Uri?>(null)
    var selectedVideoUri by mutableStateOf<Uri?>(null)
//    var selectedLocation by mutableStateOf<LatLng?>(null)

    @RequiresApi(Build.VERSION_CODES.R)
    fun uploadPhoto(uri: Uri, path: String, quality: Int, context: Context, onSuccess: (String) -> Unit, onError: (Exception) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
//            var fileDescriptor: ParcelFileDescriptor? = null
            try {
                val storageRef = FirebaseStorage.getInstance().reference.child(path)
                val inputStream = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                    ?: throw IOException("Failed to decode bitmap from URI: $uri")

                val outputStream = ByteArrayOutputStream()
                if(quality < 20){
                    try {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
                    } catch (e: Exception){
                        Log.w("SellViewModel","error compressing image")
                        e.printStackTrace()
                    }
                }
                val compressedData = outputStream.toByteArray()

                storageRef.putBytes(compressedData).await()
                val downloadUrl = storageRef.downloadUrl.await().toString()

                val parcelFileDescriptor: ParcelFileDescriptor? = context.contentResolver.openFileDescriptor(uri, "r")
                parcelFileDescriptor?.use {
                    val exifInterface = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        ExifInterface(it.fileDescriptor)
                    } else {
                        ExifInterface(uri.path!!)
                    }

                    val width = exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, 0)
                    val height = exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, 0)
                    val rotation = when (exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)) {
                        ExifInterface.ORIENTATION_ROTATE_90 -> 90
                        ExifInterface.ORIENTATION_ROTATE_180 -> 180
                        ExifInterface.ORIENTATION_ROTATE_270 -> 270
                        else -> 0
                    }

                    val metadata = MediaMetaData(downloadUrl, width, height, rotation)
                    val metadataJson = Gson().toJson(metadata)
                    val storageMetadata = com.google.firebase.storage.StorageMetadata.Builder()
                        .setCustomMetadata("metadata", metadataJson)
                        .build()
                    storageRef.updateMetadata(storageMetadata).await()
                }
                onSuccess(downloadUrl)
//                fileDescriptor?.close()
            } catch (e: RuntimeException) {
                // Handle the exception, e.g., log the error or display a message to the user
                Log.e("MediaMetadataRetriever", "Error setting data source, image", e)
            } catch (e: Exception) {
                e.printStackTrace()
//                onError(e)
            }
        }
    }

    fun uploadVideo(uri: Uri, path: String, context: Context, onSuccess: (String) -> Unit, onError: (Exception) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            var fileDescriptor: ParcelFileDescriptor? = null
            try {
                val storageRef = FirebaseStorage.getInstance().reference.child(path)
                val inputStream = context.contentResolver.openInputStream(uri)
                val videoData = inputStream?.readBytes()
                    ?: throw IOException("Failed to read video data from URI: $uri")

                storageRef.putBytes(videoData).await()
                val downloadUrl = storageRef.downloadUrl.await().toString()

                fileDescriptor = context.contentResolver.openFileDescriptor(uri, "r")
                if (fileDescriptor != null){
                    val fd = fileDescriptor.fileDescriptor
                    val retriever = MediaMetadataRetriever()
                    retriever.setDataSource(fd)

                    val width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toInt() ?: 0
                    val height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toInt() ?: 0
                    val rotation = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)?.toInt() ?: 0

                    val metadata = MediaMetaData(downloadUrl, width, height, rotation)
                    val metadataJson = Gson().toJson(metadata)
                    val storageMetadata = com.google.firebase.storage.StorageMetadata.Builder()
                        .setCustomMetadata("metadata", metadataJson)
                        .build()
                    storageRef.updateMetadata(storageMetadata).await()
                } else {
                    Log.e("MediaMetadataRetriever", "File descriptor is null")
                }

                onSuccess(downloadUrl)
                fileDescriptor?.close()
            } catch (e: RuntimeException) {
                // Handle the exception, e.g., log the error or display a message to the user
                Log.e("MediaMetadataRetriever", "Error setting data source,video", e)
            } catch (e: Exception) {
                e.printStackTrace()
//                onError(e)
            }
        }
    }

    fun saveProductToUser(userId: String, productId: String, onComplete: (Boolean) -> Unit) {
        firestoreRepository.addProductToUser(userId, productId, onComplete)
    }

    companion object {
        lateinit var selectedLocation: LatLng
    }

}