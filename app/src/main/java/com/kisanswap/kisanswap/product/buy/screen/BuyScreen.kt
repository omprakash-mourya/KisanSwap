package com.kisanswap.kisanswap.product.buy.screen

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
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import com.google.firebase.firestore.GeoPoint
import com.kisanswap.kisanswap.FullScreenVideoStreamActivity
import com.kisanswap.kisanswap.PreferenceManager
import com.kisanswap.kisanswap.R
import com.kisanswap.kisanswap.common.components.CommonTopAppBar
import com.kisanswap.kisanswap.common.components.TractorLoadingAnimation
import com.kisanswap.kisanswap.common.functions.calculateDistance
import com.kisanswap.kisanswap.common.functions.checkAndRequestCallPermission
import com.kisanswap.kisanswap.common.functions.formatPrice
import com.kisanswap.kisanswap.common.functions.loadImageFromUrl
import com.kisanswap.kisanswap.dataClass.SpecificationForProduct
import com.kisanswap.kisanswap.product.buy.repository.BuyFirebaseRepository
import com.kisanswap.kisanswap.product.buy.viewmodel.BuyViewModel
import com.kisanswap.kisanswap.product.buy.viewmodel.viewModelFactory.BuyViewModelFactory
import com.kisanswap.kisanswap.product.sell.screen.VideoThumbnail
import com.kisanswap.kisanswap.roomDataBase.AppDatabase
import kotlinx.coroutines.launch
import kotlin.math.max

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun BuyScreen(navController: NavController, productId: String, firebaseRepository: BuyFirebaseRepository) {
    val buyViewModel: BuyViewModel = viewModel(factory = BuyViewModelFactory(firebaseRepository))
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

    // Initialize the database and DAO
    val db = AppDatabase.getDatabase(context)
    val productDao = db.productDao()

    val isSaved = remember { mutableStateOf(false) }

    LaunchedEffect(productId) {
        isSaved.value = productDao.getSavedProducts().any { it.id == productId }
    }

    LaunchedEffect(productId) {
        Log.d("BuyScreen", "Launched effect executed")
        buyViewModel.loadProduct(productId)
        /*firebaseRepository.getProduct(productId) { fetchedProduct ->
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
            CommonTopAppBar(
                navController = navController,
                screenName = "Product details",
                secondaryIcon = ImageVector.vectorResource(R.drawable.baseline_share_24),
                onSecondaryIconClick = {
                    shareProduct(
                        context = context,
                        productId = productId
                    )
                },
                height = {height->

                }
            )
        },
        content = {paddding->
            val a = paddding
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(paddding)
                    .onGloballyPositioned { coordinates->
                        Log.d("BuyScreen", "width: ${coordinates.size.width}, height: ${coordinates.size.height}")
                    }
            ) {
                if (loading == true) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        TractorLoadingAnimation(200.dp)
                    }
                    Log.d("BuyScreen", "Loading product: $productId")
                } else if (loading == null) {
                    Log.d("BuyScreen", "Loading = null")
                } else {
                    if (product != null) {
                        val videoSize = max(product!!.videos.size, product!!.videosList.size)
                        val videoIn = if(product!!.videos.size > product!!.videosList.size) 0 else 1
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
                                if(fetchedProduct.videosList.isNotEmpty()){
                                    fetchedProduct.videosList.firstOrNull()?.let { videoItem ->
                                        if(videoItem.thumbnail != null){
                                            val cachedPhoto = buyViewModel.getCachedPhoto(videoItem.thumbnail)
                                            if (cachedPhoto != null) {
                                                photo = cachedPhoto
                                            } else {
                                                photo = loadImageFromUrl(videoItem.thumbnail)
                                                buyViewModel.cachePhoto(videoItem.thumbnail, photo)
                                            }
                                        }
                                        //                buyViewModel.downloadAndCacheVideos(it, context)
                                    }
                                }
                            }
                        }

                        product?.let { productDetail ->
                            val photos = productDetail.photos
                            val pageInt = remember { mutableIntStateOf(0) }
                            var thisPhoto: Painter? by remember { mutableStateOf(null) }
                            var thumbnailPhoto: Painter? by remember { mutableStateOf(null) }

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
                            if(productDetail.videosList.isNotEmpty()){
                                LaunchedEffect(productDetail.videosList[pageInt.intValue]) {
                                    if (productDetail.videosList[pageInt.intValue].thumbnail != null) {
                                        val cachedPhoto =
                                            productDetail.videosList[pageInt.intValue].thumbnail?.let {
                                                buyViewModel.getCachedPhoto(
                                                    it
                                                )
                                            }
                                        if (cachedPhoto != null) {
                                            thumbnailPhoto = cachedPhoto
                                        } else {
                                            coroutineScope.launch {
                                                val downloadedPhoto =
                                                    productDetail.videosList[pageInt.intValue].thumbnail?.let {
                                                        loadImageFromUrl(
                                                            it
                                                        )
                                                    }
                                                thumbnailPhoto = downloadedPhoto
                                                productDetail.videosList[pageInt.intValue].thumbnail?.let {
                                                    buyViewModel.cachePhoto(
                                                        it, downloadedPhoto
                                                    )
                                                }
                                            }
                                        }
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
                                        count = productDetail.photos.size + videoSize,
                                        state = pagerState,
//                                        itemSpacing = 10.dp,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(300.dp)

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
                                            val videoIndex = page - videoSize
//                                            val uriString =
//                                                CacheManager.getVideo(productDetail.videos[videoIndex])
//                                                    ?: buyViewModel.getCachedVideoUri(productDetail.videos[videoIndex])
//                                            val file = uriString?.let { File(it) }
                                            val thumbnailUrl = if(productDetail.videosList.isNotEmpty()) productDetail.videosList[videoIndex].thumbnail else ""
//                                            val thumbnailUrl = productDetail.videosList[videoIndex].thumbnail ?: ""
                                            val url = if (videoIn==0) productDetail.videos[videoIndex] else if(productDetail.videosList.isNotEmpty()) productDetail.videosList[videoIndex].video else ""
                                            val uri = Uri.parse(url)
                                            /*("enable this to download videos")*/
//                                        buyViewModel.downloadAndCacheVideos(productDetail, context)

                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
//                                                    .padding(4.dp)
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
                                                    VideoThumbnail(
                                                        uri = uri,
                                                        thumbnail = thumbnailPhoto ?: rememberAsyncImagePainter(thumbnailUrl)
                                                    ) {
                                                        selectedVideoUri = uri
                                                        selectedVideoUrl = productDetail.videos[videoIndex]
                                                        Log.d("BuyScreen", "Video selected: $uri")
                                                        context.startActivity(
                                                            FullScreenVideoStreamActivity.createStreamIntent(
                                                                context,
                                                                url ?: ""
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
                                                activeColor = Color(0xFF00B200),
                                                inactiveColor = Color(0xFFDADADA)
                                            )
                                        }
                                    }
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .wrapContentHeight(),
//                                            .border(width = 1.dp, color = Color.Black, shape = RectangleShape),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ){
                                        IconButton(
                                            onClick = {
                                                if (pagerState.currentPage == 0) return@IconButton
                                                coroutineScope.launch {
                                                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                                }
                                            },
                                            enabled = pagerState.currentPage != 0,
                                            modifier = Modifier
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
                                            enabled = pagerState.currentPage != pagerState.pageCount - 1,
                                            modifier = Modifier
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
                                Spacer(modifier = Modifier.height(4.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    Button(
                                        onClick = {
                                        if(!isSaved.value){
                                            buyViewModel.saveProduct(dao = productDao, id = productDetail.id )
                                            Toast.makeText(
                                                context,
                                                "Product saved successfully",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            buyViewModel.deleteFromSaved(dao = productDao, id = productDetail.id)
                                            Toast.makeText(
                                                context,
                                                "Product successfully deleted from saved products",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        isSaved.value = !isSaved.value
                                    },
                                        modifier = Modifier
                                            .wrapContentSize()
                                            .border(
                                                width = 1.dp,
                                                color = Color(0xFF00B200),
                                                shape = RoundedCornerShape(20.dp)
                                            )
                                            .background(
                                                Color(0xFFE8F5E9),
                                                shape = RoundedCornerShape(20.dp)
                                            ),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFFE8F5E9),
                                            contentColor = Color(0xFF0A7500)
                                        )
                                    ) {
                                        Icon(
                                            imageVector = ImageVector.vectorResource(
                                                id = if (isSaved.value) R.drawable.baseline_favorite_24 else R.drawable.baseline_favorite_border_24
                                            ),
                                            tint = Color(0xFF0A7500),
                                            contentDescription = "Save"
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Save",
                                            color = Color(0xFF0A7500),
                                            fontSize = 20.sp,
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.SemiBold
                                            )
                                    }
                                }
                                MinorDetails(
                                    shortDetails = productDetail.shortDetails,
                                    price = productDetail.price.toInt(),
                                    isNegotiable = productDetail.negotiable,
                                    location = "${productDetail.latitude}, ${productDetail.longitude}",
                                    distance = userLocation?.let {
                                        calculateDistance(
                                            GeoPoint(productDetail.latitude, productDetail.longitude),
                                            it
                                        )
                                    }
                                )
                                ProductDetails(
                                    description = productDetail.details,
                                    specifications = productDetail.specifications
                                )
                                ContactButtons(
                                    onCallClick = {
                                        makeCall(context, productDetail.contactNumber)
                                    },
                                    onWhatsAppClick = {
                                        buyViewModel.incrementWhatsappDm(productId)
                                        val intent = Intent(Intent.ACTION_VIEW).apply {
                                            data = Uri.parse("https://wa.me/${productDetail.contactNumber}?&text=I'm%20interested%20in%20your%20product%20${productDetail.shortDetails}")
                                        }
                                        context.startActivity(intent)
                                    },
                                    onMapClick = {
                                        buyViewModel.incrementMapSearch(productId)
                                        val intent = Intent(Intent.ACTION_VIEW).apply {
                                            data = Uri.parse("https://www.google.com/maps?q=${productDetail.latitude},${productDetail.longitude}")
                                        }
                                        context.startActivity(intent)
                                    }
                                )
                                Spacer(
                                    modifier = Modifier.fillMaxWidth().height(150.dp)
                                )
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

@Composable
fun BuyScreenCard(
    content: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5F5)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            content()
        }
    }
}

@Composable
fun MinorDetails(
    shortDetails: String,
    price: Int,
    isNegotiable: Boolean = true,
    location: String,
    distance: Int? = null,
){
    BuyScreenCard {
        Text(
            text = shortDetails,
            style = MaterialTheme.typography.titleLarge.copy(
                color = Color.Black
            ),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.fillMaxWidth().height(6.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(start = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Price: ₹${formatPrice(price)}",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = Color(0xFF247C1A),
                    fontWeight = FontWeight.Bold
                )
            )
            Box(
                modifier = Modifier
                    .background(
                        color = Color(0xFFF4FFF3),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = Color(0xFF00B200),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(start = 4.dp)
            ) {
                Text(
                    text = if(isNegotiable) "Negotiable" else "Not negotiable",
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = Color.White
                    ),
                    color = Color(0xFF007A08),
                    modifier = Modifier.padding(4.dp)
                )
            }
        }

        Spacer(Modifier.fillMaxWidth().height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = Color(0xFF175B10),
                modifier = Modifier.size(26.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = location,
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 20.sp
            )
        }
        if (distance != null) {
            Row(
                modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                //add a dot before the distance
                Text(
                    text = "•",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.DarkGray
                    ),
                    fontSize = 38.sp
                )
                Text(
                    text = "${formatPrice(distance)}  km away",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.DarkGray
                    ),
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Composable
fun ProductDetails(
    description:String,
    specifications: List<SpecificationForProduct>
){
    BuyScreenCard {
        Text(
            text = "Details",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(4.dp)
                .background(
                    color = Color(0xFFFFFFFF),
                    shape = RoundedCornerShape(10.dp)
                ),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            specifications.forEach {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp)
                        .wrapContentHeight(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${it.name} ",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Color(0xFF1D231D)
                        ),
                        fontSize = 20.sp
                    )
                    Text(
                        text = "${it.amount} ${it.unit ?: ""}",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Color(0xFF008F00)
                        ),
                        fontSize = 20.sp,
                        modifier = Modifier.align(Alignment.CenterVertically),
                        textAlign = TextAlign.End,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                // draw a dashed horizontal divider between each specification but not below the last specification
                if (it != specifications.last()) {
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .height(1.dp),
                        thickness = 1.dp,
                        color = Color(0xFF9D9D9D)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 18.sp
        )
    }
}

@Composable
fun ContactButtons(
    onCallClick:() -> Unit,
    onWhatsAppClick:() -> Unit,
    onMapClick:() -> Unit
){
    BuyScreenCard {
        Text(
            text = "Contact",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Row(
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onWhatsAppClick,
                modifier = Modifier
                    .wrapContentSize(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00810A),
                    disabledContentColor = Color(0xFFE8F5E9)
                )
            ) {
                Image(
                    painter = painterResource(id = R.drawable.wa_whatsapp_icon),
                    modifier = Modifier.size(24.dp),
                    contentDescription = "WhatsApp",
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text("Chat on WhatsApp")
            }
            Spacer(modifier = Modifier.width(2.dp))
            Button(
                onClick = onCallClick,
                modifier = Modifier
                    .wrapContentHeight()
                    .weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0039CB),
                    disabledContentColor = Color(0xFFE8F5E9)
                )
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.baseline_call_24),
                    modifier = Modifier.size(24.dp),
                    tint = Color.White,
                    contentDescription = "call"
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Call")
            }
        }
        Button(
            onClick = onMapClick,
            modifier = Modifier
                .wrapContentHeight(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF054947),
                disabledContentColor = Color(0xFFE8F5E9)
            )
        ) {
            Image(
                painter = painterResource(id = R.drawable.google_map_icon),
                modifier = Modifier.size(24.dp),
                contentDescription = "Call",
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text("See on Google map")
        }
    }
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
