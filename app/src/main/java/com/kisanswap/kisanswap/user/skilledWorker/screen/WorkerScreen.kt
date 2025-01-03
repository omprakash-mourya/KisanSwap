package com.kisanswap.kisanswap.user.skilledWorker.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.kisanswap.kisanswap.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkerProfileScreen() {
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var skill by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var experience by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
}

