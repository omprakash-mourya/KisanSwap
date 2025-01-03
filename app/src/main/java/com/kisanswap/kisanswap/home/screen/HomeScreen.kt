package com.kisanswap.kisanswap.home.screen

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.google.firebase.firestore.GeoPoint
import com.kisanswap.kisanswap.PreferenceManager
import com.kisanswap.kisanswap.R
import com.kisanswap.kisanswap.common.components.TractorLoadingAnimation
import com.kisanswap.kisanswap.common.functions.calculateDistance
import com.kisanswap.kisanswap.common.functions.formatPrice
import com.kisanswap.kisanswap.common.functions.toDp
import com.kisanswap.kisanswap.dataClass.Category
import com.kisanswap.kisanswap.dataClass.Product
import com.kisanswap.kisanswap.dataClass.productCategories
import com.kisanswap.kisanswap.home.repository.HomeFirebaseRepository
import com.kisanswap.kisanswap.home.viewmodel.HomeViewModel
import com.kisanswap.kisanswap.home.viewmodel.viewModelFactory.HomeViewModelFactory
import com.kisanswap.kisanswap.roomDataBase.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(navController: NavController, firebaseRepository: HomeFirebaseRepository) {
    val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(firebaseRepository))
    val products by homeViewModel.products.observeAsState(emptyList())
    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var filteredProducts by remember { mutableStateOf(emptyList<Product>()) }
    var isVoicePopupVisible by remember { mutableStateOf(false) }
    var selectedLanguage by remember { mutableStateOf("en-US") }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    val listState = rememberLazyListState()
    var isHeaderVisible by remember { mutableStateOf(true) }
    var previousScrollOffset by remember { mutableStateOf(0) }
    val preferenceManager = PreferenceManager(context)
    var isMicOn by remember { mutableStateOf(false) }
    val userLocation =
        preferenceManager.getLocation()
            ?.let { GeoPoint(it.first, it.second) }
    val pagerState = rememberPagerState()

    // Permission launcher for recording audio
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                startVoiceRecognition(context, coroutineScope, selectedLanguage,
                    { result ->
                        searchQuery = result
                    },
                    { isVoicePopupVisible = false })
            } else {
                Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    )

    Scaffold(
        topBar = {
            CustomTopAppBar(
                statusBarPadding = preferenceManager.getStatusBarPadding()
            )
                 },
        backgroundColor = Color(0xFFCCD3DD)
    ) { padding ->
        val scrollState = rememberScrollState()
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    color = Color(0xFFAACEAE), // Replace with your secondaryBackground color
                    shape = RectangleShape
                )
        ) {
            item {
                SearchBar(
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    isMicOn = isMicOn,
                    onMicToggle = {
                        isMicOn = !isMicOn
                        isVoicePopupVisible = isMicOn
                        if(isMicOn){
                            when (PackageManager.PERMISSION_GRANTED) {
                                ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.RECORD_AUDIO
                                ) -> {
                                    isVoicePopupVisible = true
                                    /*TODO*/
                                    startVoiceRecognition(
                                        context,
                                        coroutineScope,
                                        selectedLanguage,
                                        { result ->
                                            searchQuery = result
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
                    }
                )
            }
            item {
                CategorySelectionBar(
                    onPrimaryCategoryClick = {primaryCategory->

                    },
                    onSecondaryCategoryClick = {primary, secondary->
                    }
                )
            }
            item {
                ImageSlider()
            }
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(top = 6.dp)
                        .height(600.dp) // Set a fixed height for the LazyVerticalGrid
                        .background(
                            color = Color(0xFFE3E3E3), // Replace with your secondaryBackground color
                            shape = RoundedCornerShape(
                                topStart = 16.dp,
                                topEnd = 16.dp
                            )
                        )
                ) {
                    ProductGrid(homeViewModel, userLocation){ productId ->
                        navController.navigate("buy/$productId")
                    }
                }
            }
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp) // Set a fixed height for the LazyVerticalGrid
                        .background(
                            color = Color(0xFFD8E8D8),
                            shape = RectangleShape
                        )
                ) {
                    Spacer(modifier = Modifier.height(130.dp))
                }
            }
        }
    }
}

@Composable
fun CustomTopAppBar(
    statusBarPadding:Int = 32
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFC4FFC7),
                        Color(0xFF48F55A)
                    ),
                    startY = 0f,
                    endY = 150f
                )
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = statusBarPadding.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .background(Color.White, shape = CircleShape)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.kisanswap_icon_new),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                )
            }
            Text(
                text = "KisanSwap",
                fontSize = 28.sp,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notifications",
                tint = Color.Black,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}

@Composable
fun SearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    isMicOn: Boolean,
    onMicToggle: () -> Unit
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val words = listOf("Motor 7.5HP", "Pump", "Tractor")
    var currentWordIndex by remember { mutableStateOf(0) }
    var currentCharIndex by remember { mutableStateOf(0) }
    var isFocused by remember { mutableStateOf(false) }
    var animatedPlaceholder by remember { mutableStateOf("") }

    LaunchedEffect(isFocused) {
        if (!isFocused) {
            while (true) {
                delay(100) // Adjust the delay to control typing speed
                if (currentCharIndex < words[currentWordIndex].length) {
                    animatedPlaceholder = words[currentWordIndex].substring(0, currentCharIndex + 1)
                    currentCharIndex++
                } else {
                    delay(1000) // Pause before moving to the next word
                    currentCharIndex = 0
                    currentWordIndex = (currentWordIndex + 1) % words.size
                }
            }
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(Color(0xFFE3E3E3))
            .padding(bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                if (it.length <= 49) {
                    onSearchQueryChange(it)
                }else{
                    Toast.makeText(context,"Search query too long",Toast.LENGTH_SHORT).show()
                }
            },
            label = {
                Text(
                    text = if (!isFocused) animatedPlaceholder else "Search",
                    modifier = Modifier
                        .wrapContentSize()
                )
                    },
            placeholder = {
                Text(
                    text = if (!isFocused) animatedPlaceholder else "Motor 5Hp",
                    modifier = Modifier
                        .wrapContentSize(),
                    color = Color.LightGray
                )
                          },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
            },
            modifier = Modifier
                .weight(1f)
                .wrapContentHeight()
                .padding(start = 2.dp, top = 2.dp, end = 2.dp, bottom = 4.dp)
                .background(Color(0x00BABABA), shape = RoundedCornerShape(44.dp))
                .onFocusChanged { focusState ->
                    isFocused = focusState.isFocused
                },
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color(0xFFFFFFFF),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = Color(0xFF000872)
            ),
            maxLines = 2,
            shape = RoundedCornerShape(44.dp),
            textStyle = LocalTextStyle.current.copy(
                fontSize = 18.sp,
                letterSpacing = 0.sp
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    // Handle the action when the "Done" button is pressed
                    keyboardController?.hide()
                    Toast.makeText(context, "Searching '$searchQuery' ", Toast.LENGTH_SHORT).show()
                }
            )
        )
        IconButton(
            onClick = onMicToggle,
            modifier = Modifier
                .size(60.dp)
                .padding(start = 0.dp, top = 6.dp, end = 3.dp, bottom = 0.dp)
                .background(Color(0xFFFFF8F8), shape = CircleShape)
        ) {
            Icon(
                imageVector = if (isMicOn) ImageVector.vectorResource(R.drawable.baseline_mic_24) else ImageVector.vectorResource(R.drawable.baseline_mic_24),
                contentDescription = "Mic",
                tint = if (isMicOn) Color(0xFF4b39ef) else Color(0xFF57636c),
                modifier = Modifier.size(38.dp)
            )
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun CategorySelectionBar(
    onPrimaryCategoryClick:(String) -> Unit,
    onSecondaryCategoryClick:(String, String) -> Unit
) {
//    val tabTitles = listOf("Old Items", "New Items", "Land", "Services")
    val tabTitles = productCategories.map { it.name }
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()
    var isTabClicked by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .wrapContentHeight()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFDBFFE3), Color(0xFFA9E8B8)),
                    startY = 0f,
                    endY = 109f
                )
            )
    ) {
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            backgroundColor = Color.Transparent,
            contentColor = Color.Black,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.pagerTabIndicatorOffset(pagerState, tabPositions),
                    color = Color(0xFF0055FF),
                    height = 3.dp
                )
            }
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    text = {
                        Text(text = title,
                            style = if (index==3) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.wrapContentWidth(align = Alignment.CenterHorizontally),
                            textAlign = TextAlign.Center,
                            overflow = TextOverflow.Ellipsis
                        )
                           },
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch {
                            isTabClicked = true
                            pagerState.animateScrollToPage(index)
                            onPrimaryCategoryClick(title)
                        }
                    }
                )
            }
        }
        AnimatedVisibility(visible = isTabClicked) {
            Log.d("CategorySelectionBar", "tab clicked")
            HorizontalPager(
                count = tabTitles.size,
                state = pagerState
            ) { page ->
                CategoryContent(
                    categories = productCategories[page].subcategories,
                    onClick = { secondaryCategory ->
                        onSecondaryCategoryClick(tabTitles[pagerState.currentPage], secondaryCategory)
                    }
                )
            }
        }
    }
}

@Composable
fun CategoryContent(
    categories: List<Category>,
    onClick:(String) -> Unit
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(4.dp)
    ) {
        /*LazyRow(
            modifier = Modifier.fillMaxWidth(),
            state = scrollState,
            horizontalArrangement = Arrangement.Start
        ) {
            items(categories){ category ->
                CategoryItem(
                    category = category.name,
                    icon = ImageVector.vectorResource(category.icon),
                    onClick = onClick
                )
            }
        }*/
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .horizontalScroll(scrollState),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Divider(
                color = Color.DarkGray,
                modifier = Modifier.height(60.dp).width(2.dp)
            )
            categories.forEachIndexed { _, category ->
                CategoryItem(
                    category = category.name,
                    icon = category.icon,
                    onClick = onClick
                )
            }
            Spacer(Modifier.width(90.dp))
        }
    }
}

@Composable
fun CategoryItem(
    category: String,
    icon: Int,
    onClick:(String) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 4.dp, vertical = 4.dp)
            .background(Color(0xFFE3E3E3), shape = RoundedCornerShape(8.dp))
            .padding(8.dp)
            .clickable {
                onClick(category)
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = category,
            tint = Color.Black,
            modifier = Modifier.size(32.dp)
        )
        Text(
            text = category,
            style = MaterialTheme.typography.bodySmall.copy(
                letterSpacing = 0.sp
            ),
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.wrapContentHeight()
        )
    }
}

@Composable
fun DropDownMenuBar(
    sortOptions: List<String>,
    filterOptions: List<String>,
    selectedSortOption: String,
    onSortOptionSelected: (String) -> Unit,
    selectedFilterOptions: List<String>,
    onFilterOptionsSelected: (List<String>) -> Unit
) {
    Column(
        modifier = Modifier
            .height(46.dp)
            .background(Color(0xFFF0FFF4))
    ) {
        Divider(
            color = Color(0xFF006800),
            thickness = 1.dp
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp, horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            DropdownMenu(
                options = sortOptions,
                selectedOption = selectedSortOption,
                onOptionSelected = onSortOptionSelected,
                hint = "Sort by..."
            )
            DropdownMenu(
                expanded = false,
                onDismissRequest = { }
            ){
                filterOptions.forEach { option ->
                    DropdownMenuItem(onClick = {
                        val newSelected = if (selectedFilterOptions.contains(option)) {
                            selectedFilterOptions - option
                        } else {
                            selectedFilterOptions + option
                        }
                        onFilterOptionsSelected(newSelected)
                    }) {
                        Text(text = option)
                    }
                }
            }
        }
    }
}

@Composable
fun DropdownMenu(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    hint: String?,
    isMultiSelect: Boolean = false,
    selectedOptions: List<String> = emptyList(),
    onOptionsSelected: (List<String>) -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf(selectedOption) }
    var selectedMulti by remember { mutableStateOf(selectedOptions) }

    Box(
        modifier = Modifier
            .width(139.dp)
            .height(39.dp)
            .background(Color.White, shape = RoundedCornerShape(4.dp))
            .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
            .clickable { expanded = true }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if (isMultiSelect) selectedMulti.joinToString(", ") else selected,
                style = MaterialTheme.typography.bodyMedium
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = Color.Gray
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(onClick = {
                    if (isMultiSelect) {
                        val newSelected = if (selectedMulti.contains(option)) {
                            selectedMulti - option
                        } else {
                            selectedMulti + option
                        }
                        selectedMulti = newSelected
                        onOptionsSelected(newSelected)
                    } else {
                        selected = option
                        onOptionSelected(option)
                    }
                    expanded = false
                }) {
                    Text(text = option)
                }
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ImageSlider() {
    val adList = listOf(
        "https://picsum.photos/seed/208/600",
        "https://picsum.photos/seed/209/600",
        "https://picsum.photos/seed/210/600"
    )
    val pagerState = rememberPagerState()
    //make  the image slider automatically sliding after 3 seconds
    LaunchedEffect(pagerState) {
        while (true) {
            pagerState.animateScrollToPage((pagerState.currentPage + 1) % adList.size)
            delay(3000)
        }
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(185.dp)
            .background(
                color = Color(0xFFE3E3E3),
                shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
            )
    ) {
        HorizontalPager(
            count = adList.size,
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            Image(
                painter = rememberImagePainter(adList[page.toInt()].toString()),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(163.dp)
                    .padding(bottom = 4.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
        }
        HorizontalPagerIndicator(
            pagerState = pagerState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(4.dp),
            activeColor = Color(0xFF07FF00),
            inactiveColor = Color(0xFF9A9A9A),
            indicatorWidth = 8.dp,
            indicatorHeight = 8.dp,
            spacing = 8.dp
        )
    }
}

@Composable
fun ProductGrid(
    homeViewModel: HomeViewModel,
    userLocation: GeoPoint?,
    onProductClick: (String) -> Unit
) {
    val products by homeViewModel.products.observeAsState(emptyList())
    var width by remember { mutableIntStateOf(0) }
    val gridState = rememberLazyGridState()
    var productsEnded by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val viewModelLoading by homeViewModel.loading.observeAsState()
    // Initialize the database and DAO
    val db = AppDatabase.getDatabase(context)
    val productDao = db.productDao()

    LaunchedEffect(gridState) {
        snapshotFlow { gridState.layoutInfo.visibleItemsInfo }
            .collect { visibleItems ->
                if (visibleItems.isNotEmpty() && products.isNotEmpty() && visibleItems.last().index > (products.size - 2)) {
                    isLoading = productsEnded.not()
                    if (viewModelLoading == false && productsEnded.not()) {
                        Log.d("ProductGrid", "Loading more products")
                        homeViewModel.loadMoreProducts(
                            onComplete = { isLoading = false },
                            onFailure = {
                                isLoading = false
                                productsEnded = true
                                Toast.makeText(
                                    context,
                                    "No more products to load",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        )
                    }
                }
            }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        state = gridState,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 16.dp)
            .background(
                color = Color(0xFFD8E8D8), // Replace with your secondaryBackground color
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp
                )
            )
            .onGloballyPositioned { coordinates ->
                width = coordinates.size.width
//                Log.d("BeautifulContainer", "Width of lazy row:  ${width.toDp()} dp")
            }
    ) {
        if (products.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TractorLoadingAnimation(200.dp)
                    Text("Loading your products")
                }
            }
        }
        items(products) { product ->
            val distance = userLocation?.let {
                calculateDistance(it, GeoPoint(product.latitude, product.longitude))
            }
            if(distance != 0){
                Log.d("HomeScreen","distance calculated")
            } else{
                Log.d("HomeScreen","distance not calculated")
            }
            val isSaved = remember {
                mutableStateOf(false)
            }
            LaunchedEffect(product.id) {
                isSaved.value = productDao.getSavedProducts().any { it.id == product.id }
            }
            ProductItem(
                product = product,
                distance = distance,
                width = width.toDp(),
                onClick = {
                    onProductClick(it)
                },
                isFavorite = isSaved.value,
                onFavoriteClick = {
                    if(!isSaved.value){
                        homeViewModel.saveProduct(dao = productDao, id = product.id )
                        Toast.makeText(
                            context,
                            "Product saved successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        homeViewModel.deleteFromSaved(dao = productDao, id = product.id)
                        Toast.makeText(
                            context,
                            "Product successfully deleted from saved products",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    isSaved.value = !isSaved.value
                }
            )
        }
        if (isLoading) {
//            loadProducts = true
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ){
                    TractorLoadingAnimation(150.dp)
                }
            }
        }
    }
}

@Composable
fun ProductItem(
    product: Product,
    distance: Int? = null,
    width: Dp,
    isFavorite: Boolean = false,
    onClick:(String) -> Unit = {},
    onFavoriteClick: (String) -> Unit ={}
) {
    Box(
        modifier = Modifier
            .width(width / 2 - 8.dp)
            .wrapContentHeight()
            .padding(4.dp)
            .clickable {
                onClick(product.id)
            }
            .background(
                color = Color(0xFFF7FFF7), // Replace with your secondaryBackground color
                shape = RoundedCornerShape(12.dp)
            )
            .border(width = 1.dp, color = Color(0xFF20331E), shape = RoundedCornerShape(12.dp))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .height(150.dp)
                    .padding(bottom = 4.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
            ) {
                Image(
                    painter = rememberImagePainter(product.primaryPhoto),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = product.shortDetails,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.W600,
                        fontSize = 18.sp
                    )
                )
                Text(
                    text = "Price: ₹${formatPrice(product.price.toInt())}",
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = Color(0xFF0E8500), // Replace with your primary color
                        fontWeight = FontWeight.W600
                    )
                )
                Row(
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "${product.latitude}, ${product.longitude}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.Gray
                        )
                    )

                }
                if (distance != null){
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .background(Color.Gray, shape = CircleShape)
                        )
                        Text(
                            text = "(${formatPrice(distance)} km away)",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.Gray
                            )
                        )
                    }
                }
            }
        }
        IconButton(
            onClick = {onFavoriteClick(product.id)},
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 6.dp, top = 6.dp)
                .size(40.dp)
                .background(Color(0xFFB5FFB7), shape = CircleShape)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = if (isFavorite) R.drawable.baseline_favorite_24 else R.drawable.baseline_favorite_border_24),
                contentDescription = null,
                tint = Color.Black
            )
        }
        if (product.negotiable) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .wrapContentWidth()
                    .height(24.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF37FF53),
                                Color(0xFF72C4FF)
                            ) // Replace with your gradient colors
//                            start = Offset(0f, -1f),
//                            end = Offset(0f, 100f)
                        ),
                        shape = RoundedCornerShape(topStart = 12.dp)
                    )
                    .shadow(4.dp, RoundedCornerShape(topStart = 12.dp))
            ) {
                Text(
                    text = "Negotiable",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 4.dp)
                )
            }
        }
    }
}

fun startVoiceRecognition(context: Context, coroutineScope: CoroutineScope, language: String, onResult: (String) -> Unit, onPopupDisabled:() -> Unit) {
    val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, language)
        putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...")
    }

    speechRecognizer.setRecognitionListener(object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {}
        override fun onBeginningOfSpeech() {}
        override fun onRmsChanged(rmsdB: Float) {}
        override fun onBufferReceived(buffer: ByteArray?) {}
        override fun onEndOfSpeech() {}
        override fun onError(error: Int) {
            Toast.makeText(context, "Recognition Error: $error", Toast.LENGTH_SHORT).show()
            onPopupDisabled()
        }
        override fun onResults(results: Bundle?) {
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            matches?.let {
                if (it.isNotEmpty()) {
                    coroutineScope.launch {
                        onResult(it[0])
                        onPopupDisabled()
                    }
                }
            }
        }
        override fun onPartialResults(partialResults: Bundle?) {}
        override fun onEvent(eventType: Int, params: Bundle?) {}
    })

    speechRecognizer.startListening(intent)
}
