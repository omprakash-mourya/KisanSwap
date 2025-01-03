package com.kisanswap.kisanswap.common.components

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import com.kisanswap.kisanswap.PreferenceManager
import com.kisanswap.kisanswap.R
import com.kisanswap.kisanswap.common.functions.toDp

@Composable
fun CommonTopAppBar(
    navController: NavController,
    screenName: String,
    secondaryIcon: ImageVector? = null,
    onSecondaryIconClick: (() -> Unit)? = null,
    height:(Dp) -> Unit
) {
    val context = LocalContext.current
    val activity = context as Activity
    val density = LocalDensity.current
    val statusBarHeight = with(density) {
        ViewCompat.getRootWindowInsets(activity.window.decorView)
            ?.getInsets(WindowInsetsCompat.Type.statusBars())?.top?.toDp() ?: 0.dp
    }
    Log.d("BeautifulContainer", "Status bar height:  ${statusBarHeight.value.toInt()} dp")

    val preferenceManager = PreferenceManager(context)
    preferenceManager.setStatusBarPadding(statusBarHeight.value.toInt())
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFC4FFC7),
                        Color(0xFF48F55A)
                    ),
                    startY = 0f,
                    endY = 150f
                )
            )
            .padding(top = statusBarHeight)
            .onGloballyPositioned { coordinates ->
                height(coordinates.size.height.toDp()+statusBarHeight)
//                Log.d("BeautifulContainer", "Width of lazy row:  ${width.toDp()} dp")
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .size(36.dp)
                    .background(Color.White, shape = CircleShape)
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.baseline_arrow_back_24),
                    contentDescription = "Back",
                    tint = Color.Black
                )
            }
            Text(
                text = screenName,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            if (secondaryIcon != null && onSecondaryIconClick != null) {
                IconButton(
                    onClick = onSecondaryIconClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = secondaryIcon,
                        contentDescription = "Secondary Icon",
                        tint = Color.Black
                    )
                }
            } else {
                Spacer(modifier = Modifier.size(36.dp))
            }
        }
    }
}