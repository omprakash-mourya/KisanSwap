package com.kisanswap.kisanswap

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView

class FullScreenVideoActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        val videoUrl = intent.getStringExtra("videoUrl")
        val videoUri = intent.getParcelableExtra<Uri>("videoUri")

        /*videoUrl?.let {
            Log.d("FullScreenVideoActivity", "onCreate: $videoUrl executed")
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(it, HashMap())
            val width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toInt() ?: 0
            val height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toInt() ?: 0
            val rotation = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)?.toInt() ?: 0
            retriever.release()

            val aspectRatio = width.toDouble() / height
            val orientation = if (rotation == 90 || rotation == 270) "Vertical" else "Horizontal"

            if(width == 0 && height == 0 && rotation == 0){
                Log.w("FullScreenVideoActivity", "MediaMetadataRetriever initialization failed")
            } else {
                Log.d("FullScreenVideoActivity", "MediaMetadataRetriever initialization successful")
            }
            Log.d("FullScreenVideoActivity", "onCreate: aspectRatio: $aspectRatio, orientation: $orientation")
            setContent {
                Log.d("FullScreenVideoActivity", "setContent: $videoUrl executed")
//                FullScreenVideoStreamPlayer(videoUrl, orientation, aspectRatio)
                Log.d("FullScreenVideoActivity", "setContent: FullScreenVideoStreamPlayer executed")
            }
            Log.d("FullScreenVideoActivity", "onCreate: setContent executed")

            requestedOrientation = if ((orientation == "Vertical" && aspectRatio > 1) || (orientation == "Horizontal" && aspectRatio < 1)) {
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            } else {
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }
            Log.d("FullScreenVideoActivity", "onCreate finished")
        } ?: run {
            finish() // Close the activity if videoUrl is null
            Log.d("FullScreenVideoActivity", "onCreate: videoUrl is null")
        }*/

        videoUri?.let {
            Log.d("FullScreenVideoActivity", "onCreate: videoUri executed")
            val retriever = MediaMetadataRetriever()
            try {
                retriever.setDataSource(this, it)
                val videoWidth = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toInt() ?: 0
                val videoHeight = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toInt() ?: 0
                val aspectRatio = videoWidth.toDouble() / videoHeight.toDouble()

                val rotation = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)?.toInt() ?: 0
                val orientation = if (rotation == 0 || rotation == 180) "Horizontal" else "Vertical"

                requestedOrientation = if ((orientation == "Vertical" && aspectRatio > 1) || (orientation == "Horizontal" && aspectRatio < 1)) {
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                } else {
                    ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                }

                setContent {
                    FullScreenVideoPlayer(uri = it, orientation = orientation, aspectRatio = aspectRatio)
                }
            } catch (e: IllegalArgumentException) {
                // Handle the case where the retriever could not be initialized
                Log.w("FullScreenVideoActivity", "MediaMetadataRetriever initialization failed, illegal arguments exception")
                e.printStackTrace()
                finish() // Close the activity if retriever initialization fails
            } catch (e: RuntimeException){
                Log.w("FullScreenVideoActivity", "MediaMetadataRetriever initialization failed, runtime exception")
                e.printStackTrace()
                finish()
            } catch (e: Exception) {
                Log.w("FullScreenVideoActivity", "MediaMetadataRetriever initialization failed, general exception")
                e.printStackTrace()
                finish()
            }
            finally {
                retriever.release()
            }
        } ?: run {
            Log.d("FullScreenVideoActivity", "onCreate: videoUri is null")
            finish() // Close the activity if videoUri is null
        }
    }

    companion object {
        //        const val EXTRA_VIDEO_URI = "video_uri"
        fun createIntent(context: Context, videoUri: Uri): Intent {
            return Intent(context, FullScreenVideoActivity::class.java).apply {
                putExtra("videoUri", videoUri)
            }
        }
        /*fun createStreamIntent(context: Context, videoUrl: String): Intent {
            Log.d("FullScreenVideoActivity", "createStreamIntent: $videoUrl executed")
            return Intent(context, FullScreenVideoActivity::class.java).apply {
                putExtra("videoUrl", videoUrl)
            }
        }*/
    }
}

/*@OptIn(UnstableApi::class)
@Composable
fun FullScreenVideoStreamPlayer(url: String, orientation: String, aspectRatio: Double) {
    Log.d("FullScreenVideoStreamPlayer", "FullScreenVideoStreamPlayer: $url executed")
    val context = LocalContext.current
    var exoPlayer by remember { mutableStateOf<com.google.android.exoplayer2.ExoPlayer?>(null) }
    var resizeMode by remember { mutableIntStateOf(AspectRatioFrameLayout.RESIZE_MODE_FIT) }
//    val chunkSize = 2 * 1024 * 1024 // 5 MB
//    var currentChunkStart by remember { mutableStateOf(0L) }
//    var currentChunkEnd by remember { mutableStateOf(chunkSize.toLong()) }
    val uri = Uri.parse(url)

    Log.d("FullScreenVideoStreamPlayer", "uri is null: ${uri==null}")

    DisposableEffect(uri) {
        resizeMode = if ((orientation == "Vertical" && aspectRatio > 1) || (orientation == "Horizontal" && aspectRatio < 1)) {
            AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT
        } else {
            AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH
        }

        val player = com.google.android.exoplayer2.ExoPlayer.Builder(context).build().apply {
            setMediaItem(com.google.android.exoplayer2.MediaItem.fromUri(Uri.parse(url)))
            prepare()
            play()
            }
        exoPlayer = player

        *//*if(player.isLoading){
            Log.d("FullScreenVideoStreamPlayer", "player is loading")
        }
        if(player.isReleased){
            Log.d("FullScreenVideoStreamPlayer", "player is released")
        }
        if (player.isSleepingForOffload){
            Log.d("FullScreenVideoStreamPlayer", "player is sleeping for offload")
        }*//*

        *//*val downloadAndPlayNextChunk: suspend () -> Unit = {
            val chunkPath = downloadVideoChunk(context, url.toString(), currentChunkStart, currentChunkEnd)
            if (chunkPath == null){
                Log.w("FullScreenVideoStreamPlayer", "chunk path is null")
            }
            chunkPath?.let {
                Log.d("FullScreenVideoStreamPlayer", "chunk path is not empty")
                val mediaItem = MediaItem.fromUri(Uri.parse(it))
                player.addMediaItem(mediaItem)
                player.prepare()
                player.playWhenReady = true
                currentChunkStart = currentChunkEnd + 1
                currentChunkEnd += chunkSize
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            downloadAndPlayNextChunk()
        }
        LaunchedEffect(currentChunkStart) {
            downloadAndPlayNextChunk()
        }*//*


        *//*player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_ENDED) {
                    LaunchedEffect(currentChunkStart) {
                        downloadAndPlayNextChunk()
                    }

                    Log.d("FullScreenVideoStreamPlayer", "onPlaybackStateChanged: executed")
                    CoroutineScope(Dispatchers.IO).launch {
                        downloadAndPlayNextChunk()
                        Log.d("FullScreenVideoStreamPlayer", "onPlaybackStateChanged: downloadAndPlayNextChunk executed")
                    }
                }
            }
        })*//*

        onDispose {
            Log.d("FullScreenVideoStreamPlayer", "onDispose: executed")
            player.release()
        }
    }


    exoPlayer?.let { thisPlayer ->
        AndroidView(
            factory = {context ->
                *//*PlayerView(context).apply {
                    this.player = player
                    this.resizeMode = resizeMode
                    this.useController = true // Enable playback controls
                }*//*
                StyledPlayerView(context).apply {
                    player = thisPlayer
                    this.resizeMode = resizeMode
                    this.useController = true // Enable playback controls
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}*/


/*
@OptIn(UnstableApi::class)
@Composable
fun FullScreenVideoStreamPlayer(url: String, orientation: String, aspectRatio: Double) {
    Log.d("FullScreenVideoStreamPlayer", "FullScreenVideoStreamPlayer: $url executed")
    val context = LocalContext.current
    var exoPlayer by remember { mutableStateOf<ExoPlayer?>(null) }
    var resizeMode by remember { mutableIntStateOf(AspectRatioFrameLayout.RESIZE_MODE_FIT) }
    val chunkSize = 2 * 1024 * 1024 // 5 MB
    var currentChunkStart by remember { mutableStateOf(0L) }
    var currentChunkEnd by remember { mutableStateOf(chunkSize.toLong()) }
    val uri = Uri.parse(url)

    Log.d("FullScreenVideoStreamPlayer", "uri is null: ${uri==null}")

    DisposableEffect(uri) {
        resizeMode = if ((orientation == "Vertical" && aspectRatio > 1) || (orientation == "Horizontal" && aspectRatio < 1)) {
            AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT
        } else {
            AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH
        }

        val player = ExoPlayer.Builder(context).build().apply {
            setMediaItem(androidx.media3.common.MediaItem.fromUri(uri))
            prepare()
            play()
        }
        exoPlayer = player

        if(player.isLoading){
            Log.d("FullScreenVideoStreamPlayer", "player is loading")
        }
        if(player.isReleased){
            Log.d("FullScreenVideoStreamPlayer", "player is released")
        }
        if (player.isSleepingForOffload){
            Log.d("FullScreenVideoStreamPlayer", "player is sleeping for offload")
        }

        val downloadAndPlayNextChunk: suspend () -> Unit = {
            val chunkPath = downloadVideoChunk(context, url.toString(), currentChunkStart, currentChunkEnd)
            if (chunkPath == null){
                Log.w("FullScreenVideoStreamPlayer", "chunk path is null")
            }
            chunkPath?.let {
                Log.d("FullScreenVideoStreamPlayer", "chunk path is not empty")
                val mediaItem = MediaItem.fromUri(Uri.parse(it))
                player.addMediaItem(mediaItem)
                player.prepare()
                player.playWhenReady = true
                currentChunkStart = currentChunkEnd + 1
                currentChunkEnd += chunkSize
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            downloadAndPlayNextChunk()
        }
LaunchedEffect(currentChunkStart) {
            downloadAndPlayNextChunk()
        }


        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_ENDED) {
LaunchedEffect(currentChunkStart) {
                        downloadAndPlayNextChunk()
                    }

                    Log.d("FullScreenVideoStreamPlayer", "onPlaybackStateChanged: executed")
                    CoroutineScope(Dispatchers.IO).launch {
                        downloadAndPlayNextChunk()
                        Log.d("FullScreenVideoStreamPlayer", "onPlaybackStateChanged: downloadAndPlayNextChunk executed")
                    }
                }
            }
        })

        onDispose {
            Log.d("FullScreenVideoStreamPlayer", "onDispose: executed")
            player.release()
        }
    }

    exoPlayer?.let { player ->
        AndroidView(
            factory = {
                PlayerView(context).apply {
                    this.player = player
                    this.resizeMode = resizeMode
                    this.useController = true // Enable playback controls
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}
*/



@OptIn(UnstableApi::class)
@Composable
fun FullScreenVideoPlayer(uri: Uri, orientation:String, aspectRatio:Double) {
    val context = LocalContext.current
    var exoPlayer by remember { mutableStateOf<ExoPlayer?>(null) }
    var resizeMode by remember { mutableIntStateOf(AspectRatioFrameLayout.RESIZE_MODE_FIT) }

    DisposableEffect(uri) {
        resizeMode = if ((orientation=="Vertical" && aspectRatio > 1) || (orientation=="Horizontal" && aspectRatio < 1)) {
            AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT
        } else {
            AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH
        }
        val player = ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(uri))
            prepare()
        }
        exoPlayer = player

        onDispose {
            player.release()
        }
    }

    exoPlayer?.let { player ->
        AndroidView(
            factory = {
                PlayerView(context).apply {
                    this.player = player
                    // Ensure the video is displayed in portrait mode
                    this.resizeMode = resizeMode
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}

