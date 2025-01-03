//package com.kisanswap.kisanswap.temp
//
//import android.Manifest
//import android.content.Context
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.os.Bundle
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.*
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Notifications
//import androidx.compose.material.icons.filled.Search
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.lifecycle.viewmodel.compose.viewModel
//import androidx.navigation.NavController
//import com.google.accompanist.pager.*
//import com.kisanswap.kisanswap.R
//import com.kisanswap.kisanswap.home.viewmodel.HomeViewModel
//import com.kisanswap.kisanswap.home.viewmodel.viewModelFactory.HomeViewModelFactory
//import android.speech.RecognitionListener
//import android.speech.RecognizerIntent
//import android.speech.SpeechRecognizer
//import android.util.Log
//import android.widget.Toast
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.compose.animation.AnimatedVisibility
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.border
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.gestures.Orientation
//import androidx.compose.foundation.gestures.scrollable
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.layout.wrapContentHeight
//import androidx.compose.foundation.layout.wrapContentSize
//import androidx.compose.foundation.layout.wrapContentWidth
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.LazyListState
//import androidx.compose.foundation.lazy.LazyRow
//import androidx.compose.foundation.lazy.grid.GridCells
//import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.lazy.rememberLazyListState
//import androidx.compose.foundation.pager.HorizontalPager
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.material.CircularProgressIndicator
//import androidx.compose.material.DropdownMenuItem
//import androidx.compose.material.TopAppBar
//import androidx.compose.material3.IconButton
//import androidx.compose.material.icons.filled.Notifications
//import androidx.compose.material.icons.filled.Search
//import androidx.compose.material3.Card
//import androidx.compose.material3.DropdownMenu
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.Icon
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.OutlinedTextField
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.livedata.observeAsState
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.compose.runtime.setValue
//import androidx.compose.runtime.snapshotFlow
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.painter.Painter
//import androidx.compose.ui.graphics.vector.ImageVector
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.res.vectorResource
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.core.content.ContextCompat
//import androidx.lifecycle.viewmodel.compose.viewModel
//import coil.compose.rememberImagePainter
//import com.google.accompanist.pager.ExperimentalPagerApi
//import com.google.accompanist.pager.HorizontalPagerIndicator
//import com.google.accompanist.pager.PagerState
//import com.google.firebase.firestore.GeoPoint
//import com.kisanswap.kisanswap.PreferenceManager
//import com.kisanswap.kisanswap.dataClass.Product
//import com.kisanswap.kisanswap.dataClass.Category
//import com.kisanswap.kisanswap.common.components.VoiceAnimationPopup
//import com.kisanswap.kisanswap.common.functions.calculateDistance
//import com.kisanswap.kisanswap.common.functions.formatPrice
//import com.kisanswap.kisanswap.common.functions.isPhotoVertical
//import com.kisanswap.kisanswap.common.functions.loadImageFromUrl
//import com.kisanswap.kisanswap.home.repository.HomeFirebaseRepository
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//
//@Composable
//fun HomeScreen(navController: NavController, firebaseRepository: HomeFirebaseRepository) {
//    val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(firebaseRepository))
//    val products by homeViewModel.products.observeAsState(emptyList())
//    var searchQuery by remember { mutableStateOf("") }
//    val context = LocalContext.current
//    val coroutineScope = rememberCoroutineScope()
//    var filteredProducts by remember { mutableStateOf(emptyList<Product>()) }
//    var isVoicePopupVisible by remember { mutableStateOf(false) }
//    var selectedLanguage by remember { mutableStateOf("en-US") }
//    var selectedCategory by remember { mutableStateOf<Category?>(null) }
//    val listState = rememberLazyListState()
//    var isHeaderVisible by remember { mutableStateOf(true) }
//    var previousScrollOffset by remember { mutableStateOf(0) }
//    val preferenceManager = PreferenceManager(context)
//    val userLocation =
//        preferenceManager.getLocation()
//            ?.let { GeoPoint(it.first, it.second) }
//
////    val listState = rememberLazyListState()
//
//    // Trigger initial product load
////    LaunchedEffect(Unit) {
////        homeViewModel.loadInitialProducts()
////    }
//
//    // Permission launcher for recording audio
//    val permissionLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.RequestPermission(),
//        onResult = { isGranted ->
//            if (isGranted) {
//                startVoiceRecognition(context, coroutineScope, selectedLanguage,
//                    { result ->
//                        searchQuery = result
//                    },
//                    { isVoicePopupVisible = false })
//            } else {
//                Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
//            }
//        }
//    )
//
//    LaunchedEffect(listState) {
//        snapshotFlow { listState.firstVisibleItemScrollOffset }
//            .collect { scrollOffset ->
//                Log.d("HomeScreen", "Scroll offset: $scrollOffset")
//                isHeaderVisible = scrollOffset < previousScrollOffset - 20 || scrollOffset < 30
//                previousScrollOffset = scrollOffset
//            }
//    }
//
//    Scaffold(
//        topBar = { MyTopAppBar() },
//        content = { Padding->
//            val a = Padding
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(top = 56.dp, start = 8.dp, end = 8.dp ),
//                horizontalAlignment = Alignment.CenterHorizontally,
//                verticalArrangement = Arrangement.Top
//            ) {
//                AnimatedVisibility(visible = isHeaderVisible){
//                    Column {
//                        SearchBar(
//                            searchQuery = searchQuery,
//                            onSearchQueryChange = { searchQuery = it },
//                            onVoiceSearchClick = {
//                                when (PackageManager.PERMISSION_GRANTED) {
//                                    ContextCompat.checkSelfPermission(
//                                        context,
//                                        Manifest.permission.RECORD_AUDIO
//                                    ) -> {
//                                        isVoicePopupVisible = true
//                                        startVoiceRecognition(
//                                            context,
//                                            coroutineScope,
//                                            selectedLanguage,
//                                            { result ->
//                                                searchQuery = result
//                                            },
//                                            { isVoicePopupVisible = false }
//                                        )
//                                    }
//
//                                    else -> {
//                                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
//                                    }
//                                }
//                            }
//                        )
//
//                        CategoriesSection(
//                            categories = categories,
//                            onCategoryClick = { selectedCategory = it }
//                        )
//                        Text("Number of products: ${products.size}")
//                        selectedCategory?.let { category ->
//                            SubcategoriesSection(
//                                subcategories = category.subcategories,
//                                onSubcategoryClick = {/*Handle subcategory click */ }
//                            )
//                        }
//                    }
//                }
//
//                filteredProducts = products.filter {
//                    it.shortDetails.contains(searchQuery, ignoreCase = true)
//                }
//
//
//                ProductsSection(
//                    products = filteredProducts,
//                    viewModel = homeViewModel,
//                    onProductClick = { productId ->
//                        navController.navigate("buy/$productId")
//                    },
//                    listState = listState,
//                    userLocation = userLocation
//                )
//            }
//            VoiceAnimationPopup(isVisible = isVoicePopupVisible)
//        }
//    )
//
//}
//
//@Composable
//fun MyTopAppBar() {
//    TopAppBar(
//        title = {
//            Image(
//                painter = painterResource(id = R.drawable.kisanswap_icon_new),
//                contentDescription = "App Logo",
//                modifier = Modifier.height(24.dp)
//            )
//        },
//        actions = {
//            IconButton(onClick = {
//
// }) {
//                Icon(imageVector = Icons.Default.Notifications, contentDescription = "Notifications")
//            }
//        },
//        backgroundColor = Color.White,
//        contentColor = Color.Black,
//        elevation = 0.dp
//    )
//}
//
//@Composable
//fun SearchBar(
//    searchQuery: String,
//    onSearchQueryChange: (String) -> Unit,
//    onVoiceSearchClick: () -> Unit
//) {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(8.dp),
//        horizontalArrangement = Arrangement.Center,
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        OutlinedTextField(
//            value = searchQuery,
//            onValueChange = onSearchQueryChange,
//            label = { Text("Search") },
//            modifier = Modifier.weight(1f),
//            leadingIcon = {
//                Icon(
//                    imageVector = Icons.Default.Search,
//                    contentDescription = "Search",
//                    tint = Color.Gray,
//                    modifier = Modifier.size(36.dp)
//                )
//            }
//        )
//        Icon(
//            imageVector = ImageVector.vectorResource(R.drawable.baseline_mic_24),
//            contentDescription = "Microphone",
//            tint = Color.Gray,
//            modifier = Modifier
//                .size(40.dp)
//                .clickable { onVoiceSearchClick() }
//        )
//    }
//}
//
//@Composable
//fun CategoriesSection(
//    categories: List<Category>,
//    onCategoryClick: (Category) -> Unit) {
//    LazyRow(
//        modifier = Modifier.fillMaxWidth(),
//        horizontalArrangement = Arrangement.spacedBy(8.dp)
//    ) {
//        items(categories) { category ->
//            CategoryItem(category, onClick = { onCategoryClick(category) })
//        }
//    }
//}
//
//@Composable
//fun SubcategoriesSection(subcategories: List<Category>, onSubcategoryClick: (Category) -> Unit) {
//    LazyRow(
//        modifier = Modifier.fillMaxWidth(),
//        horizontalArrangement = Arrangement.spacedBy(8.dp)
//    ) {
//        items(subcategories) { subcategory ->
//            CategoryItem(subcategory, onClick = { onSubcategoryClick(subcategory) })
//        }
//    }
//}
//
//@Composable
//fun ProductsSection(products: List<Product>,
//                    viewModel: HomeViewModel,
//                    onProductClick: (String) -> Unit,
//                    listState: LazyListState,
//                    userLocation: GeoPoint?
//) {
//    val context = LocalContext.current
//    val coroutineScope = rememberCoroutineScope()
//    var loading by remember { mutableStateOf(false) }
//    val loadingViewModel by viewModel.loading.observeAsState()
//    var loadingImage by remember { mutableStateOf(false) }
//    LazyColumn(
//        modifier = Modifier
//            .fillMaxSize()
//            .scrollable(
//            state = listState,
//            orientation = Orientation.Vertical
//        ),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center,
//        state = listState
//    ) {
//        if (products.isEmpty()) {
//            item { Text("No product with name: ${products.size}") }
//        }
//        items(products) { product ->
//            ProductItem(product, viewModel,
//                onClick = {
//                    onProductClick(product.id)
////                    viewModel.downloadAndCacheVideos(product, context)
//                },
//                userLocation = userLocation,
//                loadingImage = loadingImage,
//                onLoadingImageStateChange = {state->
//                    loadingImage = state
//                }
//            )
//        }
//        item {
//            if (!loadingImage && !loading){
//                Log.d("HomeScreen", "more products loading by bottom-item method")
//                loading = true
//                LaunchedEffect(Unit) {
//                    viewModel.loadMoreProducts(
//                        onComplete = { loading = false },
//                        onFailure = { loading = false }
//                    )
//                    Log.d("HomeScreen", "more products loaded by bottom-item method")
//                }
//                loading = false
//                Log.d("HomeScreen", "loading value set to loadingViewModel")
//            }
//            if(viewModel.productEnded.value == false
//&& (loading==true || loadingImage)
//){
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.Center,
//                    verticalAlignment = Alignment.CenterVertically
//                ){
//                    CircularProgressIndicator()
//                }
//            } else if(viewModel.productEnded.value == true
//&& !loadingImage && !loading
//){
//                Toast.makeText(
//                    context,
//                    "No more products to load",
//                    Toast.LENGTH_LONG
//                ).show()
//            }
//        }
//    }
//}
//
//@Composable
//fun ProductItem(product: Product,
//                homeViewModel: HomeViewModel,
//                onClick: (String) -> Unit,
//                userLocation: GeoPoint?,
//                loadingImage: Boolean,
//                onLoadingImageStateChange: (Boolean) -> Unit
//) {
//    var photo: Painter? by remember { mutableStateOf(null) }
//    val coroutineScope = rememberCoroutineScope()
//    val isPhotoVertical = photo?.let { isPhotoVertical(it) }
//    val goldenRatio = 1.618f
//    photo?.let {
//        Log.d("ProductItem", "Photo ratio is ${it.intrinsicSize.height/it.intrinsicSize.width}")
//    }
//    if(userLocation == null){
//        Log.w("ProductItem", "User location is not saved")
//    }
//
//    LaunchedEffect(product.primaryPhoto) {
//        val cachedPhoto = homeViewModel.getCachedPhoto(product.id)
//        if (cachedPhoto != null) {
//            photo = cachedPhoto
//        } else {
//            if (product.primaryPhoto.isNotEmpty()){
////                if(loadingImage){
////                    return@LaunchedEffect
////                }
//                withContext(Dispatchers.Main){
//                    onLoadingImageStateChange(true)
//                }
//                coroutineScope.launch {
//                    Log.d("ProductItem", "Launched effect executed")
//                    val downloadedPhoto = loadImageFromUrl(product.primaryPhoto)
//                    photo = downloadedPhoto
//                    homeViewModel.cachePhoto(product.id, downloadedPhoto)
//                    withContext(Dispatchers.Main){
//                        onLoadingImageStateChange(false)
//                    }
//                    if (downloadedPhoto == null) {
//                        Log.w("ProductItem", "Photo is null, in home screen")
//                    }
//                }
//            } else {
//                Log.w("ProductItem", "photo URL is null or empty")
//            }
//        }
//    }
//
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .wrapContentHeight()
//            .padding(8.dp),
//        onClick = { onClick(product.id)}
//    ) {
//        if(isPhotoVertical == true) {
//            Row(
//                modifier = Modifier.fillMaxWidth().padding(8.dp),
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.Start
//            ) {
////            Glide
////                .with(context)
////                .load(product.primaryPhoto)
////                .placeholder(R.drawable.baseline_handyman_24)
////                .error(R.drawable.baseline_error_outline_24)
////                .into(headerView)
//
//                if (photo != null) {
//                    Log.d("ProductItem", "PhotoUrl: ${product.primaryPhoto} photo is null")
//                }
//
//                Card(
//                    modifier = Modifier.wrapContentWidth()
//                        .height(180.dp),
//                    shape = RoundedCornerShape(10.dp),
//                ) {
//                    Image(
//                        painter = photo ?: painterResource(id = R.drawable.baseline_handyman_24),
//                        contentDescription = null,
////                contentScale = ContentScale.Crop,
//                        modifier = Modifier
//                            .wrapContentWidth()
//                            .height(180.dp)
//                    )
//                }
//Image(
//                    painter = photo ?: painterResource(id = R.drawable.baseline_handyman_24),
//                    contentDescription = null,
////                contentScale = ContentScale.Crop,
//                    modifier = Modifier
//                        .wrapContentWidth()
//                        .height(200.dp).border(0.dp, Color.Gray, RoundedCornerShape(8.dp))
//                )
//
//
//                Spacer(Modifier.padding(8.dp))
//
//                Column(
//                    modifier = Modifier.fillMaxWidth().weight(1f),
//                    horizontalAlignment = Alignment.Start,
//                    verticalArrangement = Arrangement.spacedBy(16.dp / goldenRatio)
//                ) {
//                    Text(product.shortDetails, fontWeight = FontWeight.Bold, fontSize = 15.sp * goldenRatio)
//                    Text("Price: ₹ ${formatPrice(product.price)}", color = Color.Gray, fontSize = 12.sp * goldenRatio)
//                    Text("Location: ${product.latitude}, ${product.longitude}")
//                    if(userLocation != null){
//                        val productLocation = GeoPoint(product.latitude, product.longitude)
//                        val distance = calculateDistance(productLocation, userLocation)
//                        Text("${formatPrice(distance)} Km away", fontSize = 10.sp * goldenRatio)
//                    }
////            Text("Loaded from cache: ${product.loadedFromCache}")
//                }
//            }
//        }
//        else {
//            Column(
//                modifier = Modifier.fillMaxWidth().padding(8.dp),
//                horizontalAlignment = Alignment.CenterHorizontally,
//                verticalArrangement = Arrangement.Center
//            ) {
////            Glide
////                .with(context)
////                .load(product.primaryPhoto)
////                .placeholder(R.drawable.baseline_handyman_24)
////                .error(R.drawable.baseline_error_outline_24)
////                .into(headerView)
//
//                if (photo != null) {
//                    Log.d("ProductItem", "PhotoUrl: ${product.primaryPhoto} photo is null")
//                }
//
//                Card(
//                    modifier = Modifier.wrapContentHeight()
//                        .width(320.dp),
//                    shape = RoundedCornerShape(10.dp),
//                ) {
//                    Image(
//                        painter = photo ?: painterResource(id = R.drawable.baseline_handyman_24),
//                        contentDescription = null,
////                contentScale = ContentScale.Crop,
//                        modifier = Modifier
//                            .wrapContentHeight()
//                            .width(320.dp)
//                    )
//                }
//
//                Spacer(Modifier.padding(4.dp))
//
//                Text(product.shortDetails, fontWeight = FontWeight.Bold, fontSize = 20.sp)
//                Text("Price: ₹ ${formatPrice(product.price)}", color = Color.Gray, fontSize = 18.sp)
//                Text("Location: ${product.latitude}, ${product.longitude}")
//                if(userLocation != null){
//                    val productLocation = GeoPoint(product.latitude, product.longitude)
//                    val distance = calculateDistance(productLocation, userLocation)
//                    Text("${formatPrice(distance)} Km away")
//                }
//
////            Text("Loaded from cache: ${product.loadedFromCache}")
//            }
//        }
//    }
//}
//
//@Composable
//fun CategoryItem(category: Category, onClick: () -> Unit) {
//    Column(
//        horizontalAlignment = Alignment.CenterHorizontally,
//        modifier = Modifier
//            .clickable { onClick() }
//            .padding(8.dp)
//    ) {
//        Icon(
//            painter = painterResource(id = category.icon),
//            contentDescription = category.name,
//            modifier = Modifier.size(40.dp)
//        )
//        Text(text = category.name)
//    }
//}
//
//@Composable
//fun LanguageSelector(onLanguageSelected: (String) -> Unit) {
//    val languages = listOf(
//        "English" to "en-US",
//        "Hindi" to "hi-IN",
//        "Bengali" to "bn-IN",
//        "Telugu" to "te-IN",
//        "Marathi" to "mr-IN",
//        "Tamil" to "ta-IN",
//        "Urdu" to "ur-IN",
//        "Gujarati" to "gu-IN",
//        "Kannada" to "kn-IN",
//        "Malayalam" to "ml-IN",
//        "Punjabi" to "pa-IN",
//        "Assamese" to "as-IN",
//        "Odia" to "or-IN"
//    )
//    var expanded by remember { mutableStateOf(false) }
//    var selectedLanguage by remember { mutableStateOf(languages[0].second) }
//
//    Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
//        Text(
//            text = languages.first { it.second == selectedLanguage }.first,
//            modifier = Modifier
//                .clickable { expanded = true }
//                .padding(16.dp)
//        )
//        DropdownMenu(
//            expanded = expanded,
//            onDismissRequest = { expanded = false }
//        ) {
//            languages.forEach { (name, code) ->
//                DropdownMenuItem(onClick = {
//                    selectedLanguage = code
//                    onLanguageSelected(code)
//                    expanded = false
//                }) {
//                    Text(text = name)
//                }
//            }
//        }
//    }
//}
