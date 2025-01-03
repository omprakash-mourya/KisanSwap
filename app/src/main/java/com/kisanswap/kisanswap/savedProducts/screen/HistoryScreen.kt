//package com.kisanswap.kisanswap.savedProducts.screen
//
//import android.util.Log
//import android.widget.Toast
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.grid.GridCells
//import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
//import androidx.compose.foundation.lazy.grid.items
//import androidx.compose.foundation.lazy.grid.rememberLazyGridState
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.livedata.observeAsState
//import androidx.compose.runtime.mutableIntStateOf
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.runtime.snapshotFlow
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.layout.onGloballyPositioned
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.unit.dp
//import androidx.lifecycle.viewmodel.compose.viewModel
//import androidx.navigation.NavController
//import coil.compose.rememberAsyncImagePainter
//import com.google.firebase.firestore.GeoPoint
//import com.kisanswap.kisanswap.common.components.TractorLoadingAnimation
//import com.kisanswap.kisanswap.common.functions.calculateDistance
//import com.kisanswap.kisanswap.common.functions.toDp
//import com.kisanswap.kisanswap.home.viewmodel.HomeViewModel
//import com.kisanswap.kisanswap.roomDataBase.AppDatabase
//import com.kisanswap.kisanswap.roomDataBase.SavedProductEntity
//import com.kisanswap.kisanswap.savedProducts.SavedViewModel
//
//@Composable
//fun SavedProductScreen(navController: NavController) {
//    val context = LocalContext.current
//    val db = AppDatabase.getDatabase(context)
//    val productDao = db.productDao()
//    val viewModel = SavedViewModel(productDao)
//    val savedProducts by viewModel.savedProducts.collectAsState()
//
//    LazyVerticalGrid(
//        columns = GridCells.Fixed(2),
//        modifier = Modifier.fillMaxSize(),
//        contentPadding = PaddingValues(16.dp),
//        verticalArrangement = Arrangement.spacedBy(16.dp),
//        horizontalArrangement = Arrangement.spacedBy(16.dp)
//    ) {
//        items(savedProducts) { product ->
//            ProductItem(product)
//        }
//    }
//}
//
//@Composable
//fun HistoryProductScreen(navController: NavController) {
//    val context = LocalContext.current
//    val db = AppDatabase.getDatabase(context)
//    val productDao = db.productDao()
//    val viewModel = SavedViewModel(productDao)
//    val savedProducts by viewModel.savedProducts.collectAsState()
//
//    LazyVerticalGrid(
//        columns = GridCells.Fixed(2),
//        modifier = Modifier.fillMaxSize(),
//        contentPadding = PaddingValues(16.dp),
//        verticalArrangement = Arrangement.spacedBy(16.dp),
//        horizontalArrangement = Arrangement.spacedBy(16.dp)
//    ) {
//        items(savedProducts) { product ->
//            ProductItem(product)
//        }
//    }
//}
//
//@Composable
//fun ProductItem(product: SavedProductEntity) {
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(8.dp)
//    ) {
//        Image(
//            painter = rememberAsyncImagePainter(product.photos.firstOrNull()),
//            contentDescription = null,
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(150.dp)
//        )
//        Spacer(modifier = Modifier.height(8.dp))
//        Text(
//            text = product.shortDetails,
//            style = MaterialTheme.typography.bodyLarge
//        )
//        Text(
//            text = "Price: ₹${product.price}",
//            style = MaterialTheme.typography.bodyMedium
//        )
//    }
//}
//
//@Composable
//fun SavedProductGrid(
//    viewModel: SavedViewModel,
//    userLocation: GeoPoint?,
//    onProductClick: (String) -> Unit
//) {
//    val products by viewModel.products.observeAsState(emptyList())
//    var width by remember { mutableIntStateOf(0) }
//    val gridState = rememberLazyGridState()
//    var productsEnded by remember { mutableStateOf(false) }
//    var isLoading by remember { mutableStateOf(false) }
//    val context = LocalContext.current
//    val viewModelLoading by viewModel.loading.observeAsState()
//    // Initialize the database and DAO
//    val db = AppDatabase.getDatabase(context)
//    val productDao = db.productDao()
//
//    LaunchedEffect(gridState) {
//        snapshotFlow { gridState.layoutInfo.visibleItemsInfo }
//            .collect { visibleItems ->
//                if (visibleItems.isNotEmpty() && products.isNotEmpty() && visibleItems.last().index > (products.size - 2)) {
//                    isLoading = productsEnded.not()
//                    if (viewModelLoading == false && productsEnded.not()) {
//                        Log.d("ProductGrid", "Loading more products")
//                        homeViewModel.loadMoreProducts(
//                            onComplete = { isLoading = false },
//                            onFailure = {
//                                isLoading = false
//                                productsEnded = true
//                                Toast.makeText(
//                                    context,
//                                    "No more products to load",
//                                    Toast.LENGTH_LONG
//                                ).show()
//                            }
//                        )
//                    }
//                }
//            }
//    }
//
//    LazyVerticalGrid(
//        columns = GridCells.Fixed(2),
//        state = gridState,
//        modifier = Modifier
//            .fillMaxWidth()
//            .wrapContentHeight()
//            .padding(top = 16.dp)
//            .background(
//                color = Color(0xFFD8E8D8), // Replace with your secondaryBackground color
//                shape = RoundedCornerShape(
//                    topStart = 16.dp,
//                    topEnd = 16.dp
//                )
//            )
//            .onGloballyPositioned { coordinates ->
//                width = coordinates.size.width
////                Log.d("BeautifulContainer", "Width of lazy row:  ${width.toDp()} dp")
//            }
//    ) {
//        if (products.isEmpty()) {
//            item {
//                Column(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .wrapContentHeight()
//                        .padding(16.dp),
//                    verticalArrangement = Arrangement.Center,
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    TractorLoadingAnimation(200.dp)
//                    Text("Loading your products")
//                }
//            }
//        }
//        items(products) { product ->
//            val distance = userLocation?.let {
//                calculateDistance(it, GeoPoint(product.latitude, product.longitude))
//            }
//            if(distance != 0){
//                Log.d("HomeScreen","distance calculated")
//            } else{
//                Log.d("HomeScreen","distance not calculated")
//            }
//            val isSaved = remember {
//                mutableStateOf(false)
//            }
//            LaunchedEffect(product.id) {
//                isSaved.value = productDao.getSavedProducts().any { it.id == product.id }
//            }
//            com.kisanswap.kisanswap.home.screen.ProductItem(
//                product = product,
//                distance = distance,
//                width = width.toDp(),
//                onClick = {
//                    onProductClick(it)
//                },
//                isFavorite = isSaved.value,
//                onFavoriteClick = {
//                    if (!isSaved.value) {
//                        homeViewModel.saveProduct(dao = productDao, id = product.id)
//                        Toast.makeText(
//                            context,
//                            "Product saved successfully",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    } else {
//                        homeViewModel.deleteFromSaved(dao = productDao, id = product.id)
//                        Toast.makeText(
//                            context,
//                            "Product successfully deleted from saved products",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                    isSaved.value = !isSaved.value
//                }
//            )
//        }
//        if (isLoading) {
////            loadProducts = true
//            item {
//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(100.dp)
//                        .padding(16.dp),
//                    contentAlignment = Alignment.Center
//                ){
//                    TractorLoadingAnimation(150.dp)
//                }
//            }
//        }
//    }
//}