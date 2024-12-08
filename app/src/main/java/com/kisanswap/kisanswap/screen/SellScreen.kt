package com.kisanswap.kisanswap.screen

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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ContentAlpha
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardElevation
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.GeoPoint
import com.kisanswap.kisanswap.FullScreenVideoActivity
import com.kisanswap.kisanswap.PreferenceManager
import com.kisanswap.kisanswap.R
import com.kisanswap.kisanswap.dataClass.Category
import com.kisanswap.kisanswap.dataClass.Product
import com.kisanswap.kisanswap.dataClass.VideoItem
import com.kisanswap.kisanswap.dataClass.categories
import com.kisanswap.kisanswap.functions.checkAndRequestCameraPermission
import com.kisanswap.kisanswap.functions.hideProgressDialog
import com.kisanswap.kisanswap.functions.priceFilter
import com.kisanswap.kisanswap.functions.reformatPrice
import com.kisanswap.kisanswap.functions.resizeBitmap
import com.kisanswap.kisanswap.functions.showProgressDialog
import com.kisanswap.kisanswap.repositories.FirestoreRepository
import com.kisanswap.kisanswap.viewmodel.SellViewModel
import com.kisanswap.kisanswap.viewmodelfactory.SellViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterial3Api::class
)
@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun SellScreen(navController: NavController, firestoreRepository: FirestoreRepository) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val sellViewModel: SellViewModel = viewModel(
        factory = SellViewModelFactory(firestoreRepository)
    )
    val context = LocalContext.current
//    var price by remember { mutableStateOf("") }
    var price by remember { mutableStateOf(TextFieldValue("")) }

//    price = if (sellViewModel.price == 0) TextFieldValue("") else TextFieldValue(sellViewModel.price.toString())
    price = if (sellViewModel.price == 0) TextFieldValue("") else TextFieldValue(sellViewModel.price.toString(), TextRange(sellViewModel.price.toString().length)) // Set cursor position
    val preferenceManager = PreferenceManager(context)
    val defaultLocation = GeoPoint(19.0760, 72.8777)
    var videoListItem by rememberSaveable { mutableStateOf<List<VideoItem>>(emptyList()) }
    val myLocation = preferenceManager.getLocation()
        ?.let { GeoPoint(it.first, it.second) }
        ?: GeoPoint(defaultLocation.latitude, defaultLocation.longitude)
    val isSignedInWithPhone = preferenceManager.isSignedInWithPhoneNumber()

    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }

    var selectedCategory by rememberSaveable { mutableStateOf<Category?>(null) }
//    var selectedSubcategory by rememberSaveable { mutableStateOf<Category?>(null) }

    val coroutineScope = rememberCoroutineScope()

    var selectedPhotos by rememberSaveable { mutableStateOf<List<Uri>>(emptyList()) }
    var selectedVideos by rememberSaveable { mutableStateOf<List<Uri>>(emptyList()) }
    var primaryPhoto by rememberSaveable { mutableStateOf<Uri?>(null) }
    var selectedVideoUri by rememberSaveable { mutableStateOf<Uri?>(null) }

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

    fun createImageFile(): File {
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

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Sell Your Product", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)

        OutlinedTextField(
            value = sellViewModel.name,
            onValueChange = { sellViewModel.name = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Product Name") },
            leadingIcon = { Icon(imageVector = Icons.Default.Create, contentDescription = "Product Name") },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = ContentAlpha.disabled),
                cursorColor = MaterialTheme.colorScheme.primary
            )
        )

        OutlinedTextField(
            value = sellViewModel.details,
            onValueChange = { sellViewModel.details = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Product Details") },
            leadingIcon = { Icon(imageVector = Icons.Default.Info, contentDescription = "Product Details") },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = ContentAlpha.disabled),
                cursorColor = MaterialTheme.colorScheme.primary
            )
        )

        OutlinedTextField(
            value = price,
            onValueChange = {newText ->
                val filteredText = newText.text.filter { it.isDigit() }
                price = TextFieldValue(filteredText, TextRange(filteredText.length))
                sellViewModel.price = reformatPrice(filteredText)
//                sellViewModel.price = reformatPrice(price.text)

//                price = it.copy(it.text.filter { it.isDigit() })
//                sellViewModel.price = reformatPrice(price.text)
            },
            prefix = { Text("₹") },
            visualTransformation = {annotatedString->
                priceFilter(annotatedString.text)
            },
            maxLines = 1,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Product Price") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            leadingIcon = { Icon(imageVector = ImageVector.vectorResource(R.drawable.baseline_sell_24), contentDescription = "Product Price") },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = ContentAlpha.disabled),
                cursorColor = MaterialTheme.colorScheme.primary
            )
        )

        /*OutlinedTextField(
            value = sellViewModel.details,
            onValueChange = { sellViewModel.details = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Product category") }
        )*/

        CategoryDropdownMenu(
            categories = categories,
            selectedCategory = selectedCategory?.name,
            onCategorySelected = { category ->
                selectedCategory = category
//                selectedSubcategory = null
            }
        )

        /*selectedCategory?.subcategories?.let { subcategories ->
            if (subcategories.isNotEmpty()) {
                CategoryDropdownMenu(
                    categories = subcategories,
                    selectedCategory = selectedSubcategory,
                    onCategorySelected = { subcategory ->
                        selectedSubcategory = subcategory
                    }
                )
            }
        }*/

        Button(onClick = {
            navController.navigate("map")
        }) {
            Icon(imageVector = ImageVector.vectorResource(R.drawable.baseline_add_location_24), contentDescription = "Select Location")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Select Location")
        }

        Button(onClick = { selectPhotosLauncher.launch("image/*") }) {
            Icon(imageVector = ImageVector.vectorResource(R.drawable.baseline_add_photo_alternate_24), contentDescription = "Select Photos")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Select Photos")
        }

        Button(onClick = { handleTakePhotoClick() }) {
            Icon(imageVector = ImageVector.vectorResource(R.drawable.baseline_camera_alt_24), contentDescription = "Take Photo")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Take Photo")
        }

        Button(onClick = { selectVideosLauncher.launch("video/*") }) {
            Icon(imageVector = ImageVector.vectorResource(R.drawable.baseline_folder_24), contentDescription = "Select Videos")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Select Videos")
        }

        Button(onClick = { handleRecordVideoClick() }) {
            Icon(imageVector = ImageVector.vectorResource(R.drawable.baseline_camera_indoor_24), contentDescription = "Record Video")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Record Video")
        }

        LazyRow(
            contentPadding = PaddingValues(20.dp)
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
                    val width = 250 * bitmap.width/bitmap.height
                    ImageCard(
                        painter = BitmapPainter(bitmap.asImageBitmap()),
                        height = 250,
                        width = width,
                        onRemoveClick = { selectedPhotos = selectedPhotos.filter { it != uri } },
                        isPrimary = primaryPhoto == uri,
                        onPrimaryClick = { primaryPhoto = uri }
                    )
                    /*Card(
                        modifier = Modifier
                            .width(width.dp)
                            .height(250.dp),
                        shape = RoundedCornerShape(15.dp),
                        elevation = cardElevation(5.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable { primaryPhoto = uri },
                            contentAlignment = Alignment.TopCenter
                        ){
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .aspectRatio(bitmap.width.toFloat() / bitmap.height)
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
                                        .background(Color.Red, shape = CircleShape)
                                        .clickable {
                                            selectedPhotos = selectedPhotos.filter { it != uri }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove",
                                        tint = Color.White
                                    )
                                }
                            }
                            if(primaryPhoto == uri){
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
                    }*/
                }
                Spacer(modifier = Modifier.width(10.dp))

            }
        }

        LazyRow(
            contentPadding = PaddingValues(20.dp)
        ) {
            items(selectedVideos) { uri ->
                // Display video thumbnail or icon
                VideoThumbnailSeller(uri = uri, onDeleteClick = {
                    selectedVideos = selectedVideos.filter { it != uri }
                }) {
                    context.startActivity(FullScreenVideoActivity.createIntent(context, uri))
                    Log.d("SellScreen", "Video selected: $uri")
                }
                /*Card(
                    modifier = Modifier
                        .wrapContentHeight()
                        .wrapContentWidth()
                        .padding(4.dp)
                        .background(Color.Gray),
                    shape = RoundedCornerShape(5.dp),
                    elevation = cardElevation(5.dp)
                ) {
                }*/
                Spacer(modifier = Modifier.width(10.dp))
            }
        }

        /*selectedVideoUri?.let { uri ->
            VideoPlayer(uri = uri)
        }*/

        Button(onClick = {
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

                val videoUploadTasks = selectedVideos.map { uri ->
                    async {
                        suspendCancellableCoroutine { continuation ->
                            sellViewModel.uploadVideo(uri, "videos/${uri.lastPathSegment}", context,
                                onSuccess = { url -> continuation.resume(url) },
                                onError = { e -> continuation.resumeWithException(e) }
                            )
                        }
                    }
                }

                val photoUrls = photoUploadTasks.awaitAll()
                val videoUrls = videoUploadTasks.awaitAll()

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
                    category = sellViewModel.category,
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
                    primaryPhoto = primaryPhotoUrl ?: ""
                )

                firestoreRepository.addProduct(newProduct) { success, productId ->
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
        }) {
            Icon(imageVector = ImageVector.vectorResource(R.drawable.baseline_check_circle_outline_24), contentDescription = "Upload")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Upload/List Now")
        }
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
        shape = RoundedCornerShape(15.dp),
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
                    .aspectRatio((width.toFloat()/height.toFloat()))
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
                        .background(Color.Red, shape = CircleShape)
                        .clickable {
                            onRemoveClick()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove",
                        tint = Color.White
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
                            .size((height/5).dp) // Adjust the size as needed
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

/*@Composable
fun VideoPlayer(uri: Uri) {
    val context = LocalContext.current
    var exoPlayer by remember { mutableStateOf<ExoPlayer?>(null) }

    DisposableEffect(uri) {
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
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            update = { view ->
                view.player = player
            }
        )
    }
}*/

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun VideoThumbnail(uri: Uri, modifier: Modifier = Modifier, onClick: () -> Unit) {
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

    Box(
        modifier = modifier
            .fillMaxSize()

    ) {
        // Display the video thumbnail
        thumbnail?.let {
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

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun VideoThumbnailSeller(uri: Uri, modifier: Modifier = Modifier, onDeleteClick:() -> Unit, onClick: () -> Unit) {
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
    val width = (150 * aspectRatio).toInt()

    thumbnail?.let {
        ImageCard(
            painter = BitmapPainter(it.asImageBitmap()),
            height = 150,
            width = width,
            onRemoveClick = { onDeleteClick() },
            isVideo = true,
            onPrimaryClick = { onClick() }
        )
    }

    /*Card(
        modifier = Modifier
            .height(150.dp)
            .width(width.dp),
        shape = RoundedCornerShape(10.dp),
        elevation = cardElevation(5.dp)
    ){
        Box(
            modifier = modifier
                .fillMaxSize()
                .clickable { onClick() }
        ) {
            // Display the video thumbnail
            thumbnail?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 4.dp, vertical = 4.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .align(Alignment.TopEnd)
                        .background(Color.Red, shape = CircleShape)
                        .clickable {
                            onDeleteClick()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove",
                        tint = Color.White
                    )
                }
            }
            // Overlay the play icon
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Play",
                tint = Color.White.copy(alpha = 0.7f),
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(56.dp) // Adjust the size as needed
            )
        }
    }*/
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