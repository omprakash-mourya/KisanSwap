//package com.kisanswap.kisanswap
//
//import android.content.Intent
//import android.os.Bundle
//import android.window.SplashScreen
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.compose.animation.core.tween
//import androidx.compose.animation.defaultDecayAnimationSpec
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Surface
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.alpha
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.core.content.ContextCompat
//import com.kisanswap.kisanswap.ui.theme.KisanSwapTheme
//import kotlinx.coroutines.delay
//
//class SplashActivity : ComponentActivity() {
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            KisanSwapTheme {
//                // A surface container using the 'background' color from the theme
//                Surface(color = MaterialTheme.colorScheme.background) {
//                    MySplashScreen()
//                }
//            }
//        }
//    }
//
//    @Preview
//    @Composable
//    private fun MySplashScreen() {
//        val alpha = remember {
//            androidx.compose.animation.core.Animatable(0f)
//        }
//        LaunchedEffect(true) {
//            // Show splash screen for 2 seconds
//            alpha.animateTo(
//                1f,
//                animationSpec = tween(1000)
//            )
////            Thread.sleep(2000)
//            // Navigate to MainActivity
//            startActivity(Intent(
//                this@SplashActivity,
//                MainActivity::class.java).apply {
//                data = this@SplashActivity.intent.data
//            }
//            )
//            finish()
//        }
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(
//                Color(0xFFCFFFD2)
//            ),
//            contentAlignment = Alignment.Center
//        ){
//            Image(
//                painter = painterResource(id = R.drawable.kisanswap_icon_new),
//                contentDescription = "KisanSwap Logo",
//                modifier = Modifier.fillMaxSize().alpha(alpha.value)
//            )
//        }
//    }
//}
//
///*
//@Preview
//@Composable
//fun MySplashScreen() {
//    val alpha = remember {
//        androidx.compose.animation.core.Animatable(0f)
//    }
//    LaunchedEffect(true) {
//        // Show splash screen for 2 seconds
//        alpha.animateTo(
//            1f,
//            animationSpec = tween(1000)
//        )
////            Thread.sleep(2000)
//        delay(2000)
//        // Navigate to MainActivity
//        ContextCompat.startActivity(Intent(this@SplashActivity, MainActivity::class.java), null)
//        finish()
//    }
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(
//                Color(0xFFCFFFD2)
//            ),
//        contentAlignment = Alignment.Center
//    ){
//        Image(
//            painter = painterResource(id = R.drawable.kisanswap_icon_new),
//            contentDescription = "KisanSwap Logo",
//            modifier = Modifier
//                .fillMaxSize()
//                .alpha(alpha.value)
//        )
//    }
//}*/
