package com.kisanswap.kisanswap.screen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.GeoPoint
import com.kisanswap.kisanswap.dataClass.Product
import com.kisanswap.kisanswap.PreferenceManager
import com.kisanswap.kisanswap.repositories.FirestoreRepository
import com.kisanswap.kisanswap.viewmodel.HomeViewModel
import com.kisanswap.kisanswap.viewmodel.MyProductViewModel
import com.kisanswap.kisanswap.viewmodelfactory.HomeViewModelFactory

@Composable
fun MyProductScreen(
    navController: NavController,
    firestoreRepository: FirestoreRepository
) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
//    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    val preferenceManager = PreferenceManager(LocalContext.current)
    val location = preferenceManager.getLocation()
    val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(firestoreRepository))
    val viewModel = MyProductViewModel(firestoreRepository)
    val myProductsList by viewModel.userProducts.observeAsState(emptyList())
    val products = remember{ mutableStateListOf<Product>()}
    val context = LocalContext.current
    val loading by viewModel.loading.observeAsState(false)
    val userLocation =
        preferenceManager.getLocation()
            ?.let { GeoPoint(it.first, it.second) }
    val scrollState = rememberScrollState()

//    LaunchedEffect(userId) {
//        viewModel.getUserProducts(userId) { userProducts ->
//            products = userProducts
//        }
//    }

    LaunchedEffect(userId) {
        viewModel.getUserProducts(userId){
            Log.w("MyProductScreen","Products failed to load")
        }
    }

    LaunchedEffect(myProductsList){
        Log.d("MyProductScreen","second launched effect executed")
        while(myProductsList == null){
            Log.d("MyProductScreen","Waiting for products to load")
            Thread.sleep(2000)
        }
        myProductsList?.let {
            Log.w("MyProductScreen","Products loaded, list is not empty")
            products.addAll(it)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Products", fontSize = 20.sp, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                backgroundColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            )
        },
        content = {paddingg->
            val a = paddingg
            Column(
                modifier = Modifier,
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
            ){
                Text("My Products")
                Text("${products.size} no. of product fetched")
                if (loading) {
                    Log.d("MyProductScreen","Loading Product")
                    CircularProgressIndicator()
                } else {
                    if (products.isEmpty() && loading == false) {
                        Text("No products uploaded by you")
                    } else {
                        Text("${products.size} no. of product fetched")
                    }
                    if (products.isNotEmpty()){
                        Log.d("MyProductScreen","Products list is not null")
                        products.let { fetchedProducts ->
                            selectedProduct?.let { product ->
                                val productId = product.id
                                Column {
                                    Text("Product Details")
                                    Text("Clicks: ${product.clicks}")
                                    Text("Impressions: ${product.impressions}")
                                    Text("Calls: ${product.call}")
                                    Text("WhatsApp: ${product.whatsappDM}")
                                    Text("Map Searches: ${product.mapSearched}")

                                    Button(onClick = {
                                        navController.navigate("edit-product/$productId")
                                        /*viewModel.updateProduct(product) { success ->
                                        if (success) {
                                            Toast.makeText(context, "Product updated", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(context, "Failed to update product", Toast.LENGTH_SHORT).show()
                                        }
                                    }*/
                                    }) {
                                        Text("Update Product")
                                    }

                                    Button(onClick = {
                                        viewModel.deleteProduct(product, userId) { success ->
                                            if (success) {
                                                Toast.makeText(context, "Product deleted", Toast.LENGTH_SHORT)
                                                    .show()
                                                selectedProduct = null
                                                viewModel.getUserProducts(userId){
                                                    Log.w("MyProductScreen","failed to load products again")
                                                }
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "Failed to delete product",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    }) {
                                        Text("Delete Product")
                                    }
                                }
                            }

                            LazyColumn(
                                modifier = Modifier.fillMaxHeight(0.7f)
                            ) {
                                items(fetchedProducts) { product ->
                                    ProductItem(
                                        product,
                                        homeViewModel = homeViewModel,
                                        onClick = { selectedProduct = product },
                                        userLocation = userLocation,
                                        loadingImage = false,
                                        onLoadingImageStateChange = {})
                                }
                            }
                        }

                    } else {
                        viewModel.getUserProducts(userId){
                            Log.w("MyProductScreen","Products failed to load")
                        }
                        if (loading) {
                            Log.d("MyProductScreen","Loading Product")
                            CircularProgressIndicator()
                        }
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Product not found")
                            Button(onClick = {
                                viewModel.getUserProducts(userId){
                                    Log.w("MyProductScreen","Products failed to load")
                                }
                            },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            ) {
                                Text("Retry")
                            }
                        }
                        Log.d("BuyScreen", "Loading products again")
                    }
                    Text("Location: $location")

                    Button(onClick = {
                    }) {
                        Text("Reset location")
                    }
                }
            }
        }
    )
}