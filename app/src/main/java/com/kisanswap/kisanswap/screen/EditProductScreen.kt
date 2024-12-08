package com.kisanswap.kisanswap.screen

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ContentAlpha
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.kisanswap.kisanswap.CacheManager
import com.kisanswap.kisanswap.FullScreenVideoActivity
import com.kisanswap.kisanswap.R
import com.kisanswap.kisanswap.functions.checkAndRequestCameraPermission
import com.kisanswap.kisanswap.functions.formatPrice
import com.kisanswap.kisanswap.functions.loadImageFromUrl
import com.kisanswap.kisanswap.functions.priceFilter
import com.kisanswap.kisanswap.functions.reformatPrice
import com.kisanswap.kisanswap.functions.resizeBitmap
import com.kisanswap.kisanswap.repositories.FirestoreRepository
import com.kisanswap.kisanswap.viewmodel.EditProductViewModel
import com.kisanswap.kisanswap.viewmodelfactory.EditProductViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun EditProductScreen(
    navController: NavController,
    firestoreRepository: FirestoreRepository,
    productId: String
) {
    val editProductViewModel: EditProductViewModel = viewModel(factory = EditProductViewModelFactory(firestoreRepository))
    val context = LocalContext.current
    val productState by editProductViewModel.currentProduct.observeAsState()
    val loading by editProductViewModel.loading.observeAsState()
    val coroutineScope = rememberCoroutineScope()

    var newSelectedPhotos by rememberSaveable { mutableStateOf<List<Uri>>(emptyList()) }
    var oldPhotos by rememberSaveable { mutableStateOf<List<Uri>>(emptyList()) }
    val videosToBeDeleted = mutableListOf<String>()
    var photosToBeDeleted = mutableListOf<String>()
    var oldVideos by rememberSaveable { mutableStateOf<List<Uri>>(emptyList()) }
    var newSelectedVideos by rememberSaveable { mutableStateOf<List<Uri>>(emptyList()) }
    var primaryPhoto by rememberSaveable { mutableStateOf<Uri?>(null) }
    var selectedVideoUri by rememberSaveable { mutableStateOf<Uri?>(null) }

    val selectPhotosLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        newSelectedPhotos = newSelectedPhotos + uris

        if (primaryPhoto == null && newSelectedPhotos.isNotEmpty()) {
            primaryPhoto = newSelectedPhotos[0]
        }
    }

    val selectVideosLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        newSelectedVideos += uris
    }

    var photoUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var videoUri by rememberSaveable { mutableStateOf<Uri?>(null) }

    val takePhotoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            photoUri?.let { uri ->
                newSelectedPhotos = newSelectedPhotos + uri
            }
            if (primaryPhoto == null && newSelectedPhotos.isNotEmpty()) {
                primaryPhoto = newSelectedPhotos[0]
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
                newSelectedVideos = newSelectedVideos + uri
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

    LaunchedEffect(productId) {
        editProductViewModel.loadProduct(context, productId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Product", fontSize = 20.sp, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                backgroundColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.Black
            )
        },
        content = { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(padding)
            ) {
                if (loading == true) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    productState?.let { product ->
//                        editProductViewModel.downloadAndCachePhotosAndVideos(product, context)

                        var price by remember { mutableStateOf(TextFieldValue("")) }
                        price = if (product.price == 0) TextFieldValue("") else TextFieldValue(product.price.toString(), TextRange(product.price.toString().length))

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Top
                        ) {
                            // Display and edit product details
                            OutlinedTextField(
                                value = product.shortDetails,
                                onValueChange = { newValue ->
                                    editProductViewModel.updateField("shortDetails", newValue)
                                },
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
                                value = product.details,
                                onValueChange = { newValue ->
                                    editProductViewModel.updateField("details", newValue)
                                },
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
//                                    sellViewModel.price = reformatPrice(filteredText)

//                                    price = it.copy(it.text.filter { it.isDigit() })
                                    val newValue = reformatPrice(filteredText)
                                    editProductViewModel.updateField("price", newValue )
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

                            OutlinedTextField(
                                value = product.category,
                                onValueChange = { newValue ->
                                    editProductViewModel.updateField("category", newValue)
                                },
                                label = { Text("Category") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Display and manage photos
                            Text("Photos", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(product.photos) { photoUrl ->

                                    val bitmap = CacheManager.getEditPhoto(photoUrl)

                                    bitmap?.let {
                                        val width = 250 * it.width / it.height
                                        ImageCard(
                                            painter = BitmapPainter(it.asImageBitmap()),
                                            height = 250,
                                            width = width,
                                            onRemoveClick = {
                                                photosToBeDeleted.add(photoUrl)
                                            },
                                            isPrimary = photoUrl == product.originalPrimaryPhoto,
                                            onPrimaryClick = {  }
                                        )
                                    }
                                }
                                items(newSelectedPhotos){uri->
                                    var resizedBitmap by remember { mutableStateOf<Bitmap?>(null) }

                                    LaunchedEffect(uri) {
                                        withContext(Dispatchers.IO) {
                                            val inputStream = context.contentResolver.openInputStream(uri)
                                            val originalBitmap = BitmapFactory.decodeStream(inputStream)
                                            resizedBitmap = resizeBitmap(originalBitmap, 4096, 4096)
                                        }
                                    }

                                    resizedBitmap?.let { bitmap ->
                                        val width = 250 * bitmap.width / bitmap.height
                                        ImageCard(
                                            painter = BitmapPainter(bitmap.asImageBitmap()),
                                            height = 250,
                                            width = width,
                                            onRemoveClick = {
                                                newSelectedPhotos =
                                                    newSelectedPhotos.filter { it != uri }
                                            },
                                            isPrimary = primaryPhoto == uri,
                                            onPrimaryClick = { primaryPhoto = uri }
                                        )
                                    }

                                }
                            }

                            // Display and manage videos
                            Text("Videos", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(product.videos) { videoUrl ->
                                    val path = CacheManager.getVideo(videoUrl)?.toUri()
                                    path?.let {
                                        VideoThumbnailSeller(uri = path, onDeleteClick = {
                                            videosToBeDeleted.add(videoUrl)
//                                        newSelectedVideos = newSelectedVideos.filter { it != uri }
                                        }) {
                                            context.startActivity(FullScreenVideoActivity.createIntent(context, path))
                                            Log.d("SellScreen", "Video selected: $path")
                                        }
                                    }
                                }
                                items(newSelectedVideos){uri->
                                    VideoThumbnailSeller(uri = uri, onDeleteClick = {
                                        newSelectedVideos = newSelectedVideos.filter { it != uri }
                                    }) {
                                        context.startActivity(FullScreenVideoActivity.createIntent(context, uri))
                                        Log.d("SellScreen", "Video selected: $uri")
                                    }
                                }
                            }

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

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(onClick = {
                                // Handle saving changes
                                navController.popBackStack()
                            }) {
                                Text("Save Changes")
                            }
                        }
                    } ?: run {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Product not found")
                        }
                    }
                }
            }
        }
    )
}

/*val editProductViewModel: EditProductScreenViewModel = viewModel(
        factory = EditProductScreenViewModelFactory(firestoreRepository)
    )
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    var productName by remember { mutableStateOf("") }
    var productPrice by remember { mutableStateOf("") }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(productId) {
//        editProductViewModel.fetchProductDetails(productId)
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
        Text("Edit Your Product", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)

        OutlinedTextField(
            value = editProductViewModel.name,
            onValueChange = { editProductViewModel.name = it },
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
            value = editProductViewModel.details,
            onValueChange = { editProductViewModel.details = it },
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
            value = editProductViewModel.price,
            onValueChange = { editProductViewModel.price = it },
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

        CategoryDropdownMenu(
            categories = categories,
            selectedCategory = editProductViewModel.category,
            onCategorySelected = { category ->
                editProductViewModel.category = category.name
            }
        )

        LazyRow(
            contentPadding = PaddingValues(20.dp)
        ) {
            items(editProductViewModel.selectedPhotos) { uri ->
                var resizedBitmap by remember { mutableStateOf<Bitmap?>(null) }

                LaunchedEffect(uri) {
                    withContext(Dispatchers.IO) {
                        val inputStream = context.contentResolver.openInputStream(uri)
                        val originalBitmap = BitmapFactory.decodeStream(inputStream)
                        resizedBitmap = resizeBitmap(originalBitmap, 4096, 4096)
                    }
                }

                resizedBitmap?.let { bitmap ->
                    Card(
                        modifier = Modifier
                            .size(250.dp)
                            .clickable { editProductViewModel.primaryPhoto = uri },
                        shape = RoundedCornerShape(15.dp),
                        elevation = cardElevation(5.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillParentMaxSize()
                        ){
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize()
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(Color.Transparent, Color.Black),
                                            startY = 0f,
                                            endY = 1000f
                                        )
                                    )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove",
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(8.dp)
                                        .clickable {
                                            editProductViewModel.selectedPhotos = editProductViewModel.selectedPhotos - uri
                                            if (editProductViewModel.primaryPhoto == uri) {
                                                editProductViewModel.primaryPhoto = editProductViewModel.selectedPhotos.firstOrNull()
                                            }
                                        },
                                    tint = Color.White
                                )
                            }
                            if(editProductViewModel.primaryPhoto == uri){
                                Box(modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                Color.Black
                                            ),
                                            startY = 0f,
                                            endY = 1000f
                                        )
                                    )
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(2.dp),
                                    contentAlignment = Alignment.BottomCenter
                                ){
                                    Text(
                                        text = "Main Photo",
                                        style = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 24.sp),
                                        modifier = Modifier
                                            .align(Alignment.BottomCenter),
                                        onTextLayout = {  }
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
            }
        }

        LazyRow(
            contentPadding = PaddingValues(20.dp)
        ) {
            items(editProductViewModel.selectedVideos) { uri ->
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .padding(4.dp)
                        .background(Color.Gray)
                ) {
                    VideoThumbnail(uri = uri) {
                        context.startActivity(FullScreenVideoActivity.createIntent(context, uri))
                        Log.d("EditProductScreen", "Video selected: $uri")
                    }
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove",
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .clickable {
                                editProductViewModel.selectedVideos = editProductViewModel.selectedVideos - uri
                            },
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
            }
        }

        Button(onClick = {
            coroutineScope.launch(Dispatchers.IO) {
                editProductViewModel.updateProduct(context) { success ->
                    if (success) {
                        Toast.makeText(context, "Product updated successfully", Toast.LENGTH_SHORT).show()
                        navController.navigate("home")
                    } else {
                        Toast.makeText(context, "Failed to update product", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }) {
            Icon(imageVector = ImageVector.vectorResource(R.drawable.baseline_check_circle_outline_24), contentDescription = "Update")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Update/Save Product")
        }
    }*/