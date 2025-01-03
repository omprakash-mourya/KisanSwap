package com.kisanswap.kisanswap

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

class FullScreenYouTubeVideoStreamActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val videoUrl = intent.getStringExtra("videoUrl")

        videoUrl?.let {
            if (it.isNotEmpty()) {
                val videoId = extractVideoId(it)
                val isShortVideo = it.contains("/shorts/")
                val orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

                setContent {
                    FullScreenYouTubePlayer(videoId)
                }

                requestedOrientation = orientation
            } else {
                finish() // Close the activity if videoUrl is empty
            }
        } ?: run {
            finish() // Close the activity if videoUrl is null
        }
    }

    private fun extractVideoId(youtubeVideoUrl: String): String {
        return when {
            youtubeVideoUrl.contains("v=") -> youtubeVideoUrl.substringAfter("v=").substringBefore("&")
            youtubeVideoUrl.contains("/shorts/") -> youtubeVideoUrl.substringAfter("/shorts/").substringBefore("?si=").ifEmpty { youtubeVideoUrl.substringAfter("/shorts/") }
            else -> ""
        }
    }

    companion object {
        fun createStreamIntent(context: Context, videoUrl: String): Intent {
            return Intent(context, FullScreenYouTubeVideoStreamActivity::class.java).apply {
                putExtra("videoUrl", videoUrl)
            }
        }
    }
}

@Composable
fun FullScreenYouTubePlayer(
    videoId: String
    ) {
    val context = LocalContext.current

    DisposableEffect(videoId) {
        val youTubePlayerView = YouTubePlayerView(context).apply {
            addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    youTubePlayer.loadVideo(videoId, 0f)
                }
            })
        }

        onDispose {
            youTubePlayerView.release()
        }
    }

    AndroidView(
        factory = { context ->
            YouTubePlayerView(context).apply {
                addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                    override fun onReady(youTubePlayer: YouTubePlayer) {
                        youTubePlayer.loadVideo(videoId, 0f)
                    }
                })
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}
