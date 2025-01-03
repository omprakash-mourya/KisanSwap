package com.kisanswap.kisanswap.product.sell.screen

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardDefaults.cardElevation
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.GeoPoint
import com.kisanswap.kisanswap.FullScreenVideoActivity
import com.kisanswap.kisanswap.PreferenceManager
import com.kisanswap.kisanswap.R
import com.kisanswap.kisanswap.common.components.CommonTopAppBar
import com.kisanswap.kisanswap.common.functions.checkAndRequestCameraPermission
import com.kisanswap.kisanswap.common.functions.hideProgressDialog
import com.kisanswap.kisanswap.common.functions.numberMask
import com.kisanswap.kisanswap.common.functions.reformatPrice
import com.kisanswap.kisanswap.common.functions.resizeBitmap
import com.kisanswap.kisanswap.common.functions.showProgressDialog
import com.kisanswap.kisanswap.dataClass.Category
import com.kisanswap.kisanswap.dataClass.Product
import com.kisanswap.kisanswap.dataClass.VideoItem
import com.kisanswap.kisanswap.home.screen.CategorySelectionBar
import com.kisanswap.kisanswap.home.screen.startVoiceRecognition
import com.kisanswap.kisanswap.product.sell.repository.SellFirebaseRepository
import com.kisanswap.kisanswap.product.sell.viewmodel.SellViewModel
import com.kisanswap.kisanswap.product.sell.viewmodel.viewModelFactory.SellViewModelFactory
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@SuppressLint("NewApi")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellScreen(navController: NavController,
               firebaseRepository: SellFirebaseRepository
) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val sellViewModel: SellViewModel = viewModel(
        factory = SellViewModelFactory(firebaseRepository)
    )
    val context = LocalContext.current
//    var price by remember { mutableStateOf("") }
    var price by remember { mutableStateOf(TextFieldValue("")) }

//    price = if (sellViewModel.price == 0) TextFieldValue("") else TextFieldValue(sellViewModel.price.toString())
    price = if (sellViewModel.price == "") TextFieldValue("") else TextFieldValue(sellViewModel.price.toString(), TextRange(sellViewModel.price.toString().length)) // Set cursor position
    val preferenceManager = PreferenceManager(context)
    val defaultLocation = GeoPoint(19.0760, 72.8777)
    var videoListItem by rememberSaveable { mutableStateOf<List<VideoItem>>(emptyList()) }
    val myLocation = preferenceManager.getLocation()
        ?.let { GeoPoint(it.first, it.second) }
        ?: GeoPoint(defaultLocation.latitude, defaultLocation.longitude)
    val isSignedInWithPhone = preferenceManager.isSignedInWithPhoneNumber()
    var isMicOn by remember { mutableStateOf(false) }
    var isVoicePopupVisible by remember { mutableStateOf(false) }
    var selectedLanguage by remember { mutableStateOf("en-US") }
    val scope = rememberCoroutineScope()
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                startVoiceRecognition(context, scope, selectedLanguage,
                    { result ->
                        sellViewModel.name = result
                    },
                    { isVoicePopupVisible = false })
            } else {
                Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    )

    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }

    var selectedCategory by rememberSaveable { mutableStateOf<Category?>(null) }
//    var selectedSubcategory by rememberSaveable { mutableStateOf<Category?>(null) }

    val coroutineScope = rememberCoroutineScope()

    var selectedPhotos by rememberSaveable { mutableStateOf<List<Uri>>(emptyList()) }
    var selectedVideos by rememberSaveable { mutableStateOf<List<Uri>>(emptyList()) }
    var primaryPhoto by rememberSaveable { mutableStateOf<Uri?>(null) }
    var selectedVideoUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var textFieldValue by remember { mutableStateOf("") }

    val textFieldFocusNode1 = remember { FocusRequester() }
    val textFieldFocusNode2 = remember { FocusRequester() }
    val textFieldFocusNode3 = remember { FocusRequester() }
    val textFieldFocusNode4 = remember { FocusRequester() }

    val selectPhotosLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        selectedPhotos = selectedPhotos + uris

        if (primaryPhoto == null && selectedPhotos.isNotEmpty()) {
            primaryPhoto = selectedPhotos[0]
        }
    }

    val selectVideosLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        selectedVideos += uris
    }

    var photoUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var videoUri by rememberSaveable { mutableStateOf<Uri?>(null) }

    val takePhotoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            photoUri?.let { uri ->
                selectedPhotos = selectedPhotos + uri
            }
            if (primaryPhoto == null && selectedPhotos.isNotEmpty()) {
                primaryPhoto = selectedPhotos[0]
            }
        }
    }

    fun createImageFile(name: String = ""): File {
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${System.currentTimeMillis()}_",
            ".jpg",
            storageDir
        ).apply {
            photoUri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", this)
        }
    }

    fun handleTakePhotoClick() {
        checkAndRequestCameraPermission(context) {
            val photoFile = createImageFile()
            photoUri?.let { takePhotoLauncher.launch(it) }
        }
    }

    val recordVideoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CaptureVideo()) { success ->
        if (success) {
            videoUri?.let { uri ->
                selectedVideos = selectedVideos + uri
            }
        }
    }

    fun generateThumbnailUriFromVideoUri(videoUri: Uri): Uri {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, videoUri)
        val bitmap = retriever.getFrameAtTime(0)
        val thumbnailFile = createImageFile()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 1, thumbnailFile.outputStream())
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", thumbnailFile)
    }

    fun createVideoFile(): File {
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_MOVIES)
        if (storageDir != null && !storageDir.exists()) {
            Log.d("SellScreen", "Creating directory: $storageDir")
            storageDir.mkdirs()
        } else if (storageDir == null) {
            Log.d("SellScreen", "External storage not available")
        } else {
            Log.d("SellScreen", "Directory already exists: $storageDir")
        }
        return File.createTempFile(
            "MP4_${System.currentTimeMillis()}_",
            ".mp4",
            storageDir
        ).apply {
            videoUri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", this)
        }
    }

    fun handleRecordVideoClick() {
        checkAndRequestCameraPermission(context) {
            val videoFile = createVideoFile()
            Log.d("SellScreen", "Video file created: $videoFile")
            videoUri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", videoFile)
            videoUri?.let {
                if (it.path == null) {
                    Log.d("SellScreen", "Video URI path is null")
                } else {
                    recordVideoLauncher.launch(it)
                }
            }
        }
    }

    var topAppBarHeight by remember { mutableStateOf(0.dp) }
    Scaffold(
        topBar = {
            CommonTopAppBar(
                navController = navController,
                screenName = "Sell Products",
                secondaryIcon = Icons.Default.Share,
                height = { topAppBarHeight = it }
            )
        }
    ) { padding->
        val a = padding
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .background(Color(0xFFF5F5F5)),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(topAppBarHeight))
            /*1st*/
            SellScreenCard {
                Column(
                    modifier = Modifier.padding(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Product Details",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.W600,
                            color = MaterialTheme.colorScheme.onBackground
                        ),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SellTextField(
                            value = sellViewModel.name,
                            onValueChange = { sellViewModel.name = it },
                            label = "Product name",
                            modifier = Modifier.weight(1f),
                            keyboardType = KeyboardType.Text
                        )
                        IconButton(
                            onClick = {
                                isMicOn = !isMicOn
                                isVoicePopupVisible = isMicOn
                                if(isMicOn){
                                    when (PackageManager.PERMISSION_GRANTED) {
                                        ContextCompat.checkSelfPermission(
                                            context,
                                            Manifest.permission.RECORD_AUDIO
                                        ) -> {
                                            isVoicePopupVisible = true
                                            startVoiceRecognition(
                                                context,
                                                coroutineScope,
                                                selectedLanguage,
                                                { result ->
                                                    sellViewModel.name = result
                                                },
                                                {

                                                    isVoicePopupVisible = isMicOn
                                                }
                                            )
                                        }

                                        else -> {
                                            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                        }
                                    }
                                }
                            },
                            modifier = Modifier
                                .size(50.dp)
                                .padding(start = 2.dp, top = 6.dp, end = 0.dp, bottom = 2.dp)
                                .background(Color(0xFFFFF8F8), shape = CircleShape)
                        ) {
                            Icon(
                                imageVector = if (isMicOn) ImageVector.vectorResource(R.drawable.baseline_mic_24) else ImageVector.vectorResource(
                                    R.drawable.baseline_mic_24),
                                contentDescription = "Mic",
                                tint = if (isMicOn) Color(0xFF4b39ef) else Color(0xFF57636C),
                                modifier = Modifier.size(38.dp)
                            )
                        }
                    }
                    CategorySelectionBar(
                        onPrimaryCategoryClick = {primaryCategory->

                        },
                        onSecondaryCategoryClick = {primary, secondary->
                        }
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(Color.White, RoundedCornerShape(8.dp))
                                .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Select Category",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(Color.White, RoundedCornerShape(8.dp))
                                .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Sub Category",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                    }
                    SellCategorySpecifications(
                        specification = "Power",
                        amount = 0,
                        onAmountChange = { },
                        unitOptions = listOf("HP", "KW"),
                        onUnitChange = { }
                    )
                }
            }
            /*2nd*/
            SellScreenCard {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Price and description",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.W600,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                    SellTextField(
                        textFieldValue = price,
                        onTextFieldValueChange = {newText ->
                            if (newText.text.length < 10){
                                val filteredText = newText.text.filter { it.isDigit() }
                                price = TextFieldValue(filteredText, TextRange(filteredText.length))
                                sellViewModel.price = reformatPrice(filteredText).toString()
                            } else {
                                Toast.makeText(context, "Price too long", Toast.LENGTH_SHORT).show()
                            }

                        },
                        label = "Price",
                        modifier = Modifier.fillMaxWidth(),
                        keyboardType = KeyboardType.Number,
                        prefix = {
                            Text("₹", style = MaterialTheme.typography.bodyMedium)
                        }
                    )
                    SellTextField(
                        value = sellViewModel.details,
                        label = "Description",
                        onValueChange = {
                            sellViewModel.details = it
                        },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        maxLines = 5
                    )
                }
            }
            /*3rd*/
            SellScreenCard {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Location",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.W600,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                    Button(
                        onClick = {
                            navController.navigate("map")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE8F5E9)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        elevation = ButtonDefaults.buttonElevation(2.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = null,
                                tint = Color(0xFF2E7D32),
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Change Location",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = Color(0xFF2E7D32)
                                )
                            )
                        }
                    }
                }
            }
            /*4th*/
            SellScreenCard {
                var expandAddPhotos by remember { mutableStateOf(false) }
                var expandAddVideos by remember { mutableStateOf(false) }
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Media",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.W600,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                    Text(
                        text = "Photos",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = MaterialTheme.colorScheme.secondary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Start)
                    )
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(12.dp)
                    ) {
                        items(selectedPhotos) { uri ->
                            var resizedBitmap by remember { mutableStateOf<Bitmap?>(null) }

                            LaunchedEffect(uri) {
                                withContext(Dispatchers.IO) {
                                    val inputStream = context.contentResolver.openInputStream(uri)
                                    val originalBitmap = BitmapFactory.decodeStream(inputStream)
                                    resizedBitmap = resizeBitmap(originalBitmap, 4096, 4096)
                                }
                            }

                            resizedBitmap?.let { bitmap ->
                                val width = 100 * bitmap.width / bitmap.height
                                ImageCard(
                                    painter = BitmapPainter(bitmap.asImageBitmap()),
                                    height = 100,
                                    width = width,
                                    onRemoveClick = {
                                        selectedPhotos = selectedPhotos.filter { it != uri }
                                    },
                                    isPrimary = primaryPhoto == uri,
                                    onPrimaryClick = { primaryPhoto = uri }
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                        }
                        item{
                            AddMediaItem(
                                icon = Icons.Default.Add,
                                label = "Add Photo",
                                onClick = { expandAddPhotos = !expandAddPhotos }
                            )
                        }
                    }
                    if(expandAddPhotos){
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            MediaButton(
                                text = "Take Photo",
                                icon = Icons.Default.ShoppingCart,
                                onClick = { handleTakePhotoClick() }
                            )
                            MediaButton(
                                text = "Upload Photos",
                                icon = Icons.Default.Add,
                                onClick = { selectPhotosLauncher.launch("image/*") }
                            )
                        }
                    }

                    Text(
                        text = "Videos",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = MaterialTheme.colorScheme.secondary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Start)
                    )
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(12.dp)
                    ) {
                        items(selectedVideos) { uri ->
                            // Display video thumbnail or icon
                            VideoThumbnailSeller(
                                uri = uri,
                                onDeleteClick = {
                                    selectedVideos = selectedVideos.filter { it != uri }
                                },
                                height = 100,
                                onClick = {
                                    context.startActivity(
                                        FullScreenVideoActivity.createIntent(
                                            context,
                                            uri
                                        )
                                    )
                                    Log.d("SellScreen", "Video selected: $uri")
                                }
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                        }
                        item{
                            AddMediaItem(
                                icon = Icons.Default.Add,
                                label = "Add Video",
                                onClick = { expandAddVideos = !expandAddVideos }
                            )
                        }
                    }
                    if(expandAddVideos){
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            MediaButton(
                                text = "Record Video",
                                icon = Icons.Default.AddCircle,
                                onClick = { handleRecordVideoClick() }
                            )
                            MediaButton(
                                text = "Upload Videos",
                                icon = Icons.Default.Place,
                                onClick = { selectVideosLauncher.launch("video/*") }
                            )
                        }
                    }
                }
            }
            /*5th*/
            Button(
                onClick = {
                    return@Button
                    showProgressDialog("Uploading...")

                    coroutineScope.launch(Dispatchers.IO) {
                        if (selectedPhotos.isEmpty() && selectedVideos.isEmpty()) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "Please select photos or videos to upload", Toast.LENGTH_SHORT).show()
                            }
                            return@launch
                        }

                        Log.d("SellScreen", "Selected photos: $selectedPhotos")
                        Log.d("SellScreen", "Selected videos: $selectedVideos")

                        val photoUploadTasks = selectedPhotos.map { uri ->
                            async {
                                suspendCancellableCoroutine { continuation ->
                                    sellViewModel.uploadPhoto(uri, "photos/${uri.lastPathSegment}", 80, context,
                                        onSuccess = { url -> continuation.resume(url) },
                                        onError = { e -> continuation.resumeWithException(e) }
                                    )
                                }
                            }
                        }

                        val videoItemUploadTasks = selectedVideos.map { videoUri ->
                            async {
                                val thumbnailUri = generateThumbnailUriFromVideoUri(videoUri)
                                val videoUrl = suspendCancellableCoroutine<String> { continuation ->
                                    sellViewModel.uploadVideo(videoUri, "videos/${videoUri.lastPathSegment}", context,
                                        onSuccess = { url -> continuation.resume(url) },
                                        onError = { e -> continuation.resumeWithException(e) }
                                    )
                                }
                                val thumbnailUrl = suspendCancellableCoroutine<String> { continuation ->
                                    sellViewModel.uploadPhoto(thumbnailUri, "thumbnails/${thumbnailUri.lastPathSegment}", 1, context,
                                        onSuccess = { url -> continuation.resume(url) },
                                        onError = { e -> continuation.resumeWithException(e) }
                                    )
                                }
                                VideoItem(videoUrl, thumbnailUrl)
                            }
                        }

                        /*val videoUploadTasks = selectedVideos.map { uri ->
                            async {
                                suspendCancellableCoroutine { continuation ->
                                    sellViewModel.uploadVideo(uri, "videos/${uri.lastPathSegment}", context,
                                        onSuccess = { url -> continuation.resume(url) },
                                        onError = { e -> continuation.resumeWithException(e) }
                                    )
                                }
                            }
                        }*/

                        val photoUrls = photoUploadTasks.awaitAll()
                        val videoUrls = emptyList<String>()
//                val videoUrls = videoUploadTasks.awaitAll()
                        val videoItemUrls = videoItemUploadTasks.awaitAll()

                        val primaryPhotoUrl = primaryPhoto?.let { uri ->
                            suspendCancellableCoroutine { continuation ->
                                sellViewModel.uploadPhoto(uri, "photos/${uri.lastPathSegment}", 1, context,
                                    onSuccess = { url -> continuation.resume(url) },
                                    onError = { e -> continuation.resumeWithException(e) }
                                )
                            }
                        }

                        val newProduct = Product(
                            id = "",
                            tertiaryCategory = sellViewModel.category,
//                    location = GeoPoint(myLocation.latitude, myLocation.longitude),
                            latitude = myLocation.latitude,
                            longitude = myLocation.longitude,
                            sellerBacklink = userId,
                            shortDetails = sellViewModel.name,
                            price = sellViewModel.price,
                            negotiable = true,
                            details = sellViewModel.details,
                            photos = photoUrls,
                            videos = videoUrls,
                            primaryPhoto = primaryPhotoUrl ?: "",
                            videosList = videoItemUrls
                        )

                        firebaseRepository.addProduct(newProduct) { success, productId ->
                            hideProgressDialog()
                            if (success) {
                                if (productId != null) {
                                    sellViewModel.saveProductToUser(userId, productId) { successs ->
                                        if (successs) {
                                            Toast.makeText(context, "Product saved to user", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(context, "Failed to save product to user", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                                Toast.makeText(context, "Product uploaded successfully", Toast.LENGTH_SHORT).show()
                                navController.navigate("home")
                            } else {
                                Toast.makeText(context, "Failed to upload product", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Green
                ),
                shape = RoundedCornerShape(28.dp),
                elevation = ButtonDefaults.buttonElevation(2.dp)
            ) {
                Text(
                    text = "List Product",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.W600,
                        color = Color.White,
                        letterSpacing = 0.sp
                    )
                )
            }
            Spacer(modifier = Modifier.height(110.dp))
        }
    }
}

@Composable
fun SellCategorySpecifications(
    specification: String,
    amount: Int,
    onAmountChange:(String) -> Unit,
    unitOptions: List<String>,
    onUnitChange:(String) ->Unit
){
    var expanded by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf(unitOptions[0]) }
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$specification:",
            style = MaterialTheme.typography.labelMedium.copy(
                fontSize = 18.sp
            )
        )
        SellTextField(
            textFieldValue = TextFieldValue(amount.toString()),
            onTextFieldValueChange = {
                onAmountChange(it.text)
            },
            modifier = Modifier.weight(1f),
            keyboardType = KeyboardType.Number
        )

        Box(
            modifier = Modifier
                .wrapContentWidth()
                .height(39.dp)
                .background(Color.White, shape = RoundedCornerShape(4.dp))
                .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                .clickable { expanded = true }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .wrapContentSize()
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = selected,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.wrapContentWidth()
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(24.dp).wrapContentWidth()
                )
            }
            DropdownMenu(
                modifier = Modifier
                    .wrapContentWidth()
                    .border(
                        1.dp,
                        Color.Gray,
                        RoundedCornerShape(4.dp)
                    ),
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                unitOptions.forEach { option->
                    DropdownMenuItem(
                        onClick = {
                            selected = option
                            expanded = false
                        }
                    ) {
                        Text(
                            text = option,
                            modifier = Modifier
                                .wrapContentWidth()
                            )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellTextField(
    value: String? = null,
    textFieldValue: TextFieldValue? = null,
    onTextFieldValueChange:(TextFieldValue)-> Unit = {},
    onValueChange:(String)-> Unit = {},
    label: String = "",
    leadingIcon: Int? = null,
    modifier: Modifier,
    minLines: Int? = null,
    maxLines: Int? = null,
    isDoneType: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    prefix: @Composable() (() -> Unit)? = null,
    suffix: @Composable() (() -> Unit)? = null
){
    val context = LocalContext.current
    var isFocused by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    if(keyboardType == KeyboardType.Number){
        if (textFieldValue != null) {
            OutlinedTextField(
                value = textFieldValue,
                onValueChange = {
                    onTextFieldValueChange(it)
                },
                label = {
                    Text(label)
                },
                shape = RoundedCornerShape(10.dp),
                modifier = modifier
                    .wrapContentHeight()
                    .clickable {
                        isFocused = true
                    }
                    .padding(2.dp),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color(0xFFFFFFFF)
                ),
                visualTransformation =  { annotatedString ->
                    numberMask(annotatedString.text)
                },
                leadingIcon = {
                    leadingIcon?.let {
                        Icon(
                            imageVector = ImageVector.vectorResource(it),
                            modifier = Modifier,
                            contentDescription = "",
                            tint = Color.DarkGray
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = keyboardType,
                    imeAction = if (isDoneType) ImeAction.Done else ImeAction.Unspecified
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                        isFocused = false
    //                Toast.makeText(context, "Searching '$searchQuery' ", Toast.LENGTH_SHORT).show()
                    }
                ),
                minLines = minLines?: 1,
                maxLines = maxLines?: 1,
                prefix = prefix,
                suffix = suffix
            )
        }
    } else {
        if (value != null) {
            OutlinedTextField(
                value = value,
                onValueChange = {
                    onValueChange(it)
                },
                label = {
                    Text(label)
                },
                shape = RoundedCornerShape(10.dp),
                modifier = modifier
                    .wrapContentHeight()
                    .clickable {
                        isFocused = true
                    }
                    .padding(2.dp),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color(0xFFFFFFFF)
                ),
                leadingIcon = {
                    leadingIcon?.let {
                        Icon(
                            imageVector = ImageVector.vectorResource(it),
                            modifier = Modifier,
                            contentDescription = "",
                            tint = Color.DarkGray
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = keyboardType,
                    imeAction = if (isDoneType) ImeAction.Done else ImeAction.Unspecified
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                        isFocused = false
    //                Toast.makeText(context, "Searching '$searchQuery' ", Toast.LENGTH_SHORT).show()
                    }
                ),
                minLines = minLines?: 1,
                maxLines = maxLines?: 1,
                prefix = prefix,
                suffix = suffix
            )
        }
    }
}

@Composable
fun SellScreenCard(
    content:@Composable () -> Unit
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 8.dp)
            .background(Color.White, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ){
        content()
    }
}

@Composable
fun MediaItem(
    url: String,
    height: Int = 250,
    width: Int = 150,
    onRemoveClick: () -> Unit,
    isPrimary: Boolean = false,
    isVideo: Boolean = false,
    onPrimaryClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .wrapContentSize(align = Alignment.TopEnd),
//            .height(height.dp),
        shape = RoundedCornerShape(15.dp),
        elevation = cardElevation(2.dp)
    ) {
        Box(
            modifier = Modifier
                .wrapContentSize()
                .clickable { onPrimaryClick() },
            contentAlignment = Alignment.TopEnd
        ) {
            Image(
                painter = rememberImagePainter(url),
                contentDescription = null,
                modifier = Modifier
                    .height(height = height.dp)
                    .wrapContentHeight(Alignment.CenterVertically)
                    .aspectRatio((width.toFloat() / height.toFloat()))
                    .border(
                        width = 2.dp,
                        color = Color(0xFF9B0000),
                        shape = RoundedCornerShape(15.dp)
                    )
            )
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 8.dp),
                contentAlignment = Alignment.TopEnd
            ){
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .align(Alignment.TopEnd)
                        .background(
                            Color(0xFFFAD2D2),
                            shape = CircleShape
                        )
                        .border(
                            width = 1.dp,
                            color = Color(0xFFD00000),
                            shape = CircleShape
                        )
                        .clickable {
                            onRemoveClick()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove",
                        tint = Color(0xFFD00000)
                    )
                }
            }
            if(isVideo){
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ){
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size((height / 5).dp) // Adjust the size as needed
                    )
                }
            }
            if (isPrimary) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black
                                ),
                                startY = 300f,
                                endY = 500f
                            )
                        ),
                    contentAlignment = Alignment.BottomCenter
                ){
                    Text(
                        text = "Main Photo",
                        style = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 20.sp),
                        modifier = Modifier
                            .wrapContentWidth()
                            .padding(2.dp)
                            .align(Alignment.BottomCenter),
                        maxLines = 2,
                        onTextLayout = {  }
                    )
                }
            }
        }
    }
}

@Composable
fun AddMediaItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    size: Int = 100
) {
    Box(
        modifier = Modifier
            .size(size.dp)
//            .clip(RoundedCornerShape(8.dp))
            .background(
                color = Color(0xFFE8F5E9),
                shape = RoundedCornerShape(20.dp)
            )
//            .clickable(onClick = onClick)
            .border(
                width = 1.dp,
                color = Color(0xFF00B200),
                shape = RoundedCornerShape(20.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.clickable { onClick() },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF2E7D32),
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFF2E7D32)
                )
            )
        }
    }
}

@Composable
fun MediaButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .width(130.dp)
            .height(40.dp)
            .border(
                width = 1.dp,
                color = Color(0xFF00B200),
                shape = RoundedCornerShape(8.dp)
            )
            .background(
                Color(0xFFE8F5E9),
                shape = RoundedCornerShape(8.dp)
            ),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFE8F5E9),
            disabledContentColor = Color(0xFFE8F5E9)
        )
//        shape = RoundedCornerShape(20.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF2E7D32),
            modifier = Modifier.size(15.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(
//                fontFamily = FontFamily(Font(R.font.inter)),
                color = Color(0xFF2E7D32)
            )
        )
    }
}

@Composable
fun ImageCard(
    painter: Painter,
    height: Int = 250,
    width: Int = 150,
    onRemoveClick: () -> Unit,
    isPrimary: Boolean = false,
    isVideo: Boolean = false,
    onPrimaryClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(width.dp)
            .height(height.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = cardElevation(5.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onPrimaryClick() },
            contentAlignment = Alignment.TopCenter
        ) {
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .aspectRatio((width.toFloat() / height.toFloat()))
            )
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(top = 2.dp, end = 2.dp),
                contentAlignment = Alignment.TopEnd
            ){
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .align(Alignment.TopEnd)
                        .background(
                            Color(0xFFFAD2D2),
                            shape = CircleShape
                        )
                        .border(
                            width = 1.dp,
                            color = Color(0xFFD00000),
                            shape = CircleShape
                        )
                        .clickable {
                            onRemoveClick()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove",
                        tint = Color(0xFFD00000)
                    )
                }
            }
            if(isVideo){
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ){
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size((height / 5).dp) // Adjust the size as needed
                    )
                }
            }
            if (isPrimary) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black
                                ),
                                startY = 300f,
                                endY = 500f
                            )
                        ),
                    contentAlignment = Alignment.BottomCenter
                ){
                    Text(
                        text = "Main Photo",
                        style = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 20.sp),
                        modifier = Modifier
                            .wrapContentWidth()
                            .padding(2.dp)
                            .align(Alignment.BottomCenter),
                        maxLines = 2,
                        onTextLayout = {  }
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun VideoThumbnail(uri: Uri, thumbnail: Painter?, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val context = LocalContext.current
    var thumbnailBitmap by remember { mutableStateOf<Bitmap?>(null) }

    var videoWidth by remember { mutableIntStateOf(0) }
    var videoHeight by remember { mutableIntStateOf(0) }
    var aspectRatio by remember { androidx.compose.runtime.mutableFloatStateOf(0f) }
    var orientation by remember { mutableStateOf("Unknown") }

    LaunchedEffect(uri) {
        withContext(Dispatchers.IO) {
            val baseFilePath = uri.toString().split("?")[0]
            var fileDescriptor: ParcelFileDescriptor? = null
            try {
//                fileDescriptor = context.contentResolver.openFileDescriptor(uri, "r")
                fileDescriptor = context.contentResolver.openFileDescriptor(baseFilePath.toUri(), "r")
                if (fileDescriptor != null) {
                    val fd = fileDescriptor.fileDescriptor
                    val retriever = MediaMetadataRetriever()
                    retriever.setDataSource(fd)

                    val rotation = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)?.toInt() ?: 1000
                    orientation = if (rotation == 0 || rotation == 180) "Horizontal" else if(rotation==1000) "Null" else "Vertical"

                    videoWidth = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toInt() ?: 0
                    videoHeight = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toInt() ?: 0
                    aspectRatio = if (videoHeight != 0) videoWidth.toFloat() / videoHeight else 0f

                    val thumb = retriever.getFrameAtTime(0)
                    retriever.release()
                    if (thumb == null) {
                        Log.w("VideoThumbnail", "Thumb is null for URI: $uri")
                    } else {
                        thumbnailBitmap = thumb
                        Log.d("VideoThumbnail", "Thumbnail generated for URI: $uri")
                    }
                } else {
                    Log.e("VideoThumbnail", "File descriptor is null for URI: $uri")
                }
                fileDescriptor?.close()
            } catch (e: Exception) {
                Log.e("VideoThumbnail", "Error generating thumbnail for URI: $uri", e)
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()

    ) {
        // Display the video thumbnail
        if (thumbnail != null) {
            Image(
                painter = thumbnail,
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        }
        thumbnailBitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Overlay the play icon
        Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = "Play",
            tint = Color.White.copy(alpha = 0.7f),
            modifier = Modifier
                .clickable { onClick() }
                .align(Alignment.Center)
                .size(56.dp) // Adjust the size as needed
        )
    }
}

//@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun VideoThumbnailSeller(
    uri: Uri,
    modifier: Modifier = Modifier,
    onDeleteClick:() -> Unit,
    onClick: () -> Unit,
    height: Int = 100
) {
    val context = LocalContext.current
    var thumbnail by remember { mutableStateOf<Bitmap?>(null) }

    var videoWidth by remember { mutableIntStateOf(0) }
    var videoHeight by remember { mutableIntStateOf(0) }
    var aspectRatio by remember { androidx.compose.runtime.mutableFloatStateOf(0f) }
    var orientation by remember { mutableStateOf("Unknown") }

    LaunchedEffect(uri) {
        withContext(Dispatchers.IO) {
            val baseFilePath = uri.toString().split("?")[0]
            var fileDescriptor: ParcelFileDescriptor? = null
            try {
//                fileDescriptor = context.contentResolver.openFileDescriptor(uri, "r")
                fileDescriptor = context.contentResolver.openFileDescriptor(baseFilePath.toUri(), "r")
                if (fileDescriptor != null) {
                    val fd = fileDescriptor.fileDescriptor
                    val retriever = MediaMetadataRetriever()
                    retriever.setDataSource(fd)

                    val rotation = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)?.toInt() ?: 1000
                    orientation = if (rotation == 0 || rotation == 180) "Horizontal" else if(rotation==1000) "Null" else "Vertical"

                    videoWidth = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toInt() ?: 0
                    videoHeight = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toInt() ?: 0
                    aspectRatio = if (videoHeight != 0) videoWidth.toFloat() / videoHeight else 0f

                    val thumb = retriever.getFrameAtTime(0)
                    retriever.release()
                    if (thumb == null) {
                        Log.w("VideoThumbnail", "Thumb is null for URI: $uri")
                    } else {
                        thumbnail = thumb
                        Log.d("VideoThumbnail", "Thumbnail generated for URI: $uri")
                    }
                } else {
                    Log.e("VideoThumbnail", "File descriptor is null for URI: $uri")
                }
                fileDescriptor?.close()
            } catch (e: Exception) {
                Log.e("VideoThumbnail", "Error generating thumbnail for URI: $uri", e)
            }
        }
    }
    val width = (height * aspectRatio).toInt()

    thumbnail?.let {
        ImageCard(
            painter = BitmapPainter(it.asImageBitmap()),
            height = height,
            width = width,
            onRemoveClick = { onDeleteClick() },
            isVideo = true,
            onPrimaryClick = { onClick() }
        )
    }
}

@Composable
fun CategoryDropdownMenu(
    categories: List<Category>,
    selectedCategory: String?,
    onCategorySelected: (Category) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        OutlinedButton(onClick = { expanded = true }) {
            Text(text = selectedCategory ?: "Select Category")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            categories.forEach { category ->
                DropdownMenuItem(onClick = {
                    onCategorySelected(category)
                    expanded = false
                }) {
                    Text(text = category.name)
                }
            }
        }
    }
}


//YouTube player
/*@Composable
fun YouTubeVideoPlayer(videoId: String, modifier: Modifier = Modifier) {
    AndroidView(
        factory = { context ->
            YouTubePlayerView(context).apply {
                addYouTubePlayerListener(object : YouTubePlayerListener {
                    override fun onReady(youTubePlayer: YouTubePlayer) {
                        youTubePlayer.loadVideo(videoId, 0f)
                    }

                    override fun onStateChange(
                        youTubePlayer: YouTubePlayer,
                        state: PlayerConstants.PlayerState
                    ) {
                        TODO("Not yet implemented")
                    }

                    override fun onStateChange(youTubePlayer: YouTubePlayer, state: Int) {
                        // Handle state change
                    }

                    override fun onPlaybackQualityChange(youTubePlayer: YouTubePlayer, playbackQuality: String) {
                        // Handle playback quality change
                    }

                    override fun onPlaybackRateChange(youTubePlayer: YouTubePlayer, rate: Double) {
                        // Handle playback rate change
                    }

                    override fun onError(youTubePlayer: YouTubePlayer, error: Int) {
                        // Handle error
                    }

                    override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
                        // Handle current second
                    }

                    override fun onError(
                        youTubePlayer: YouTubePlayer,
                        error: PlayerConstants.PlayerError
                    ) {
                        TODO("Not yet implemented")
                    }

                    override fun onPlaybackQualityChange(
                        youTubePlayer: YouTubePlayer,
                        playbackQuality: PlayerConstants.PlaybackQuality
                    ) {
                        TODO("Not yet implemented")
                    }

                    override fun onPlaybackRateChange(
                        youTubePlayer: YouTubePlayer,
                        playbackRate: PlayerConstants.PlaybackRate
                    ) {
                        TODO("Not yet implemented")
                    }

                    override fun onVideoDuration(youTubePlayer: YouTubePlayer, duration: Float) {
                        // Handle video duration
                    }

                    override fun onVideoLoadedFraction(youTubePlayer: YouTubePlayer, loadedFraction: Float) {
                        // Handle video loaded fraction
                    }

                    override fun onVideoId(youTubePlayer: YouTubePlayer, videoId: String) {
                        // Handle video ID
                    }

                    override fun onApiChange(youTubePlayer: YouTubePlayer) {
                        // Handle API change
                    }
                    // Implement other methods if needed
                }
                )
            }
        },
        modifier = modifier
    )
}*/

@Composable
fun YoutubePlayer(
    videoId: String,
    lifecycleOwner: LifecycleOwner
){
    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(RoundedCornerShape(16.dp)),
        factory = { context ->
            YouTubePlayerView(context).apply {
                lifecycleOwner.lifecycle.addObserver(this)
                addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                    override fun onReady(youTubePlayer: YouTubePlayer) {
                        youTubePlayer.loadVideo(videoId, 0f)
                    }
                }
                )
            }
        }
    )
}