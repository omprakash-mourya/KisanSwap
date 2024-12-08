package com.kisanswap.kisanswap.screen

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.session.CommandButton
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import com.google.firebase.firestore.GeoPoint
import com.kisanswap.kisanswap.CacheManager
import com.kisanswap.kisanswap.FullScreenVideoStreamActivity
import com.kisanswap.kisanswap.PreferenceManager
import com.kisanswap.kisanswap.R
import com.kisanswap.kisanswap.functions.calculateDistance
import com.kisanswap.kisanswap.functions.checkAndRequestCallPermission
import com.kisanswap.kisanswap.functions.formatPrice
import com.kisanswap.kisanswap.functions.loadImageFromUrl
import com.kisanswap.kisanswap.repositories.FirestoreRepository
import com.kisanswap.kisanswap.viewmodel.BuyViewModel
import com.kisanswap.kisanswap.viewmodelfactory.BuyViewModelFactory
import kotlinx.coroutines.launch
import java.io.File

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun BuyScreen(navController: NavController, productId: String, firestoreRepository: FirestoreRepository) {
    val buyViewModel:BuyViewModel = viewModel(factory = BuyViewModelFactory(firestoreRepository))
    val context = LocalContext.current
    var photo: Painter? by remember { mutableStateOf(null) }
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState()
    val productState by buyViewModel.product.observeAsState()
    var product = productState
//    var product by remember { mutableStateOf<Product?>(null) }
    val videoThumbnails = remember { mutableStateListOf<Bitmap?>() }
    var selectedVideoUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var selectedVideoUrl by rememberSaveable { mutableStateOf<String?>(null) }
    val scrollState = rememberScrollState()

    val preferenceManager = PreferenceManager(context)
    val userLocation =
        preferenceManager.getLocation()
            ?.let { GeoPoint(it.first, it.second) }

    val loading by buyViewModel.loading.observeAsState()
    val cachedVideoUri by buyViewModel.cachedVideoUri.observeAsState()

    LaunchedEffect(productId) {
        Log.d("BuyScreen", "Launched effect executed")
        buyViewModel.loadProduct(productId)
        /*firestoreRepository.getProduct(productId) { fetchedProduct ->
            product = fetchedProduct
            Log.d("BuyScreen", "Fetched product: $fetchedProduct")
            coroutineScope.launch {
                fetchedProduct?.photos?.firstOrNull()?.let { photoUrl ->
                    Log.d("BuyScreen", "Loading image from URL: $photoUrl")
                    photo = loadImageFromUrl(photoUrl)
                    if (photo == null) {
                        Log.d("BuyScreen", "Photo is null after loading from URL: $photoUrl")
                    } else {
                        Log.d("BuyScreen", "Photo loaded successfully from URL: $photoUrl")
                    }
                }
            }
        } */
    }

    fun makeCall(context: Context, phoneNumber: String) {
        checkAndRequestCallPermission(context) {
            val intent = Intent(Intent.ACTION_CALL).apply {
                data = Uri.parse("tel:$phoneNumber")
            }
            context.startActivity(intent)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Product Details", fontSize = 20.sp, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                backgroundColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            )
        },
        content = {paddding->
            val a = paddding
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                if (loading == true) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                    Log.d("BuyScreen", "Loading product: $productId")
                } else if (loading == null) {
                    Log.d("BuyScreen", "Loading = null")
                } else {
                    if (product != null) {
                        product?.let { fetchedProduct ->
                            product = fetchedProduct
                            coroutineScope.launch {
                                fetchedProduct.photos.firstOrNull()?.let { photoUrl ->
                                    val cachedPhoto = buyViewModel.getCachedPhoto(photoUrl)
                                    if (cachedPhoto != null) {
                                        photo = cachedPhoto
                                    } else {
                                        photo = loadImageFromUrl(photoUrl)
                                        buyViewModel.cachePhoto(photoUrl, photo)
                                    }
                                }
                                fetchedProduct.let {
                                    //                buyViewModel.downloadAndCacheVideos(it, context)
                                }
                            }
                        }

                        product?.let { productDetail ->
                            val photos = productDetail.photos
                            val pageInt = remember { mutableIntStateOf(0) }
                            var thisPhoto: Painter? by remember { mutableStateOf(null) }

                            LaunchedEffect(photos[pageInt.intValue]) {
                                val cachedPhoto = buyViewModel.getCachedPhoto(photos[pageInt.intValue])
                                if (cachedPhoto != null) {
                                    thisPhoto = cachedPhoto
                                } else {
                                    coroutineScope.launch {
                                        val downloadedPhoto = loadImageFromUrl(photos[pageInt.intValue])
                                        thisPhoto = downloadedPhoto
                                        buyViewModel.cachePhoto(photos[pageInt.intValue], downloadedPhoto)
                                    }
                                }
                            }

                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(scrollState),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Top
                            ) {

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(300.dp)
                                        .background(MaterialTheme.colorScheme.surface),
                                    contentAlignment = Alignment.Center
                                ){
                                    HorizontalPager(
                                        count = productDetail.photos.size + productDetail.videos.size,
                                        state = pagerState,
                                        itemSpacing = 10.dp,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(300.dp)
                                            .clip(RoundedCornerShape(16.dp))

                                    ) { page ->

                                        if (page < productDetail.photos.size) {

                                            val photoUrl = productDetail.photos[page]
                                            pageInt.intValue = page

                                            if (thisPhoto == null) {
                                                Log.d("BuyScreen", "thisPhoto is null")
                                            }

                                            Image(
                                                painter = thisPhoto ?: rememberAsyncImagePainter(photoUrl),
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .clip(RoundedCornerShape(16.dp))
                                                    .clickable {
                                                        Log.d("BuyScreen", "Photo clicked: $photoUrl")
                                                    }
                                            )
                                        } else {
                                            val videoIndex = page - productDetail.photos.size
                                            val uriString =
                                                CacheManager.getVideo(productDetail.videos[videoIndex])
                                                    ?: buyViewModel.getCachedVideoUri(productDetail.videos[videoIndex])
                                            val file = uriString?.let { File(it) }
                                            /*val uri = file?.let {
                                                FileProvider.getUriForFile(
                                                    context, "com.kisanswap.kisanswap.fileprovider",
                                                    it
                                                )
                                            }*/
                                            val uri = Uri.parse(productDetail.videos[videoIndex])
                                            /*("enable this to download videos")*/
//                                        buyViewModel.downloadAndCacheVideos(productDetail, context)

                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(4.dp)
                                                    .background(Color.Gray)
                                                    .clip(RoundedCornerShape(16.dp))
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.PlayArrow,
                                                    contentDescription = "Play",
                                                    tint = Color.White.copy(alpha = 0.9f),
                                                    modifier = Modifier
                                                        .align(Alignment.Center)
                                                        .clickable {  }
                                                        .size(56.dp) // Adjust the size as needed
                                                )
                                                if (uri != null) {
                                                    VideoThumbnail(uri = uri) {
                                                        selectedVideoUri = uri
                                                        selectedVideoUrl = productDetail.videos[videoIndex]
                                                        Log.d("BuyScreen", "Video selected: $uri")
                                                        context.startActivity(
                                                            FullScreenVideoStreamActivity.createStreamIntent(
                                                                context,
                                                                productDetail.videos[videoIndex]
                                                            )
                                                        )
                                                        Log.d("BuyScreen", "Video: $uri launched")
                                                    }
                                                } else {
                                                    Log.w("BuyScreen", "Video URI is null")
                                                }
                                                /*selectedVideoUrl?.let {
                                                    VideoPlayer(
                                                        videoUrl = productDetail.videos[videoIndex]
                                                    ){
                                                        navController.navigate("home")
                                                    }
                                                }*/
                                            }
                                        }

                                        Box(
                                            modifier = Modifier.fillMaxSize()
                                        ) {
                                            HorizontalPagerIndicator(
                                                pagerState = pagerState,
                                                pageCount = pagerState.pageCount,
                                                pageIndexMapping = { it },
                                                modifier = Modifier.align(Alignment.BottomCenter)
                                                    .padding(16.dp),
                                                activeColor = Color.White,
                                                inactiveColor = Color.DarkGray
                                            )
                                        }
                                    }
                                    Box(){
                                        IconButton(
                                            onClick = {
                                                if (pagerState.currentPage == 0) return@IconButton
                                                coroutineScope.launch {
                                                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                                }
                                            },
                                            enabled = !(pagerState.currentPage == 0),
                                            modifier = Modifier
                                                .align(Alignment.Center)
                                                .padding(16.dp),
                                            content = {
                                                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                                            }
                                        )
                                        IconButton(
                                            onClick = {
                                                if (pagerState.currentPage == pagerState.pageCount - 1) return@IconButton
                                                coroutineScope.launch {
                                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                                }
                                            },
                                            enabled = !(pagerState.currentPage == pagerState.pageCount - 1),
                                            modifier = Modifier
                                                .align(Alignment.Center)
                                                .padding(16.dp),
                                            content = {
                                                Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Next")
                                            }
                                        )
                                    }
                                    /*Button(
                                        onClick = {
                                            if (pagerState.currentPage == 0) return@Button
                                            coroutineScope.launch {
                                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                            }
                                        },
                                        modifier = Modifier.align(Alignment.CenterStart)
                                    ) {
                                        Text(
                                            text = "<",
                                            modifier = Modifier.clip(CircleShape)
                                                .size(20.dp)
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            if (pagerState.currentPage == pagerState.pageCount - 1) return@Button
                                            coroutineScope.launch {
                                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                            }
                                        },
                                        modifier = Modifier.align(Alignment.CenterEnd)
                                    ) {
                                        Text(
                                            text = ">",
                                            modifier = Modifier
                                                .clip(shape = CircleShape)
                                                .size(20.dp)
                                        )
                                    }*/
                                }

                                // draw an horizontal line
                                HorizontalDivider(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    thickness = 1.dp,
                                    color = MaterialTheme.colorScheme.onBackground
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Button(onClick = {
                                        buyViewModel.saveProductToUser(productId) { success ->
                                        if (success) {
                                            Toast.makeText(
                                                context,
                                                "Product saved successfully",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "Failed to save product",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        }
                                    } ) {
                                        Icon(imageVector = Icons.Default.Favorite, contentDescription = "Save")
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Save")
                                    }
                                    IconButton(onClick = {
                                        shareProduct(
                                            context = context,
                                            productId = productId
                                        )
                                    }) {
                                        Icon(imageVector = Icons.Default.Share, contentDescription = "Share")
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = productDetail.shortDetails,
                                    style = MaterialTheme.typography.headlineSmall,
                                    modifier = Modifier.padding(8.dp)
                                )
                                Text(
                                    text = "Price: ₹ ${formatPrice(productDetail.price)}",
                                    style = MaterialTheme.typography.headlineMedium,
                                    modifier = Modifier.padding(8.dp)
                                )
                                Text(
                                    text = "Detail: ${productDetail.details} ",
                                    style = MaterialTheme.typography.labelLarge,
                                    modifier = Modifier.padding(8.dp)
                                )
                                Text(
                                    text = "Location: ${productDetail.latitude}, ${productDetail.longitude}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(8.dp)
                                )
                                if(userLocation != null){
                                    val productLocation = GeoPoint(productDetail.latitude, productDetail.longitude)
                                    val distance = calculateDistance(productLocation, userLocation)
                                    Text(
                                        text = "${formatPrice(distance)} Km away",
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }

                                HorizontalDivider(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    thickness = 1.dp,
                                    color = MaterialTheme.colorScheme.onBackground
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(onClick = {
                                    /*buyViewModel.incrementCall(productId)
                                    val intent = Intent(Intent.ACTION_DIAL).apply {
                                        data = Uri.parse("tel:${productDetail.contactNumber}")
                                    }
                                    context.startActivity(intent)*/

                                    makeCall(context, productDetail.contactNumber)
                                },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                ) {
                                    Icon(imageVector = ImageVector.vectorResource(R.drawable.baseline_call_24), contentDescription = "Record Video")
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Call")
                                }

                                Button(onClick = {
                                    buyViewModel.incrementWhatsappDm(productId)
                                    val intent = Intent(Intent.ACTION_VIEW).apply {
//                    data = Uri.parse("https://wa.me/?text=I'm%20interested%20in%20your%20product%20${it.shortDetails}")
                                        data =
                                            Uri.parse("https://wa.me/${productDetail.contactNumber}?&text=I'm%20interested%20in%20your%20product%20${productDetail.shortDetails}")
                                    }
                                    context.startActivity(intent)
                                },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.wa_whatsapp_icon),
                                        modifier = Modifier.size(24.dp),
                                        contentDescription = "WhatsApp",
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Chat on WhatsApp")
                                }
                                Button(onClick = {
                                    buyViewModel.incrementMapSearch(productId)
                                    val intent = Intent(Intent.ACTION_VIEW).apply {
                                        data =
                                            Uri.parse("https://www.google.com/maps?q=${productDetail.latitude},${productDetail.longitude}")
                                    }
                                    context.startActivity(intent)
                                },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.google_map_icon),
                                        modifier = Modifier.size(24.dp),
                                        contentDescription = "Google Map",
                                        )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("See on Google Map")
                                }
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Product not found")
                            Button(onClick = {
                                buyViewModel.loadProduct(productId)
                            },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            ) {
                                Text("Retry")
                            }
                        }
                        Log.d("BuyScreen", "Loading product: $productId again")
                    }
                }
            }
        }
    )
}

fun shareProduct(context: Context, productId: String) {
    val deepLink = generateDeepLink(productId)
    val shareIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, "Check out this product: $deepLink")
        type = "text/plain"
    }
    context.startActivity(Intent.createChooser(shareIntent, "Share via"))
}

fun generateDeepLink(productId: String): String {
    return "https://www.kisanswap.com/buy?productId=$productId"
}


// 5-dec-2024
/*
@Composable
fun VideoPlayer(videoUrl: String, onClose: () -> Unit) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(Uri.parse(videoUrl)))
            prepare()
            play()
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f))
    ) {
        AndroidView(
            factory = { context ->
                StyledPlayerView(context).apply {
                    player = exoPlayer
                }
            },
            modifier = Modifier.fillMaxSize()
        )
        IconButton(
            onClick = { onClose() },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = Color.White
            )
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }
}*/
