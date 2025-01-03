package com.kisanswap.kisanswap.product.sell.screen

import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.GeoPoint
import com.kisanswap.kisanswap.PreferenceManager
import com.kisanswap.kisanswap.R
import com.kisanswap.kisanswap.common.components.CommonTopAppBar
import com.kisanswap.kisanswap.common.components.TractorLoadingAnimation
import com.kisanswap.kisanswap.common.functions.calculateDistance
import com.kisanswap.kisanswap.common.functions.formatPrice
import com.kisanswap.kisanswap.dataClass.Product
import com.kisanswap.kisanswap.home.repository.HomeFirebaseRepository
import com.kisanswap.kisanswap.home.viewmodel.HomeViewModel
import com.kisanswap.kisanswap.home.viewmodel.viewModelFactory.HomeViewModelFactory
import com.kisanswap.kisanswap.product.sell.repository.SellFirebaseRepository
import com.kisanswap.kisanswap.product.sell.viewmodel.MyProductViewModel
import com.kisanswap.kisanswap.roomDataBase.AppDatabase
import com.kisanswap.kisanswap.roomDataBase.UploadedProductEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun MyProductScreen(
    navController: NavController,
    homeRepository: HomeFirebaseRepository,
    sellRepository: SellFirebaseRepository
){
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val preferenceManager = PreferenceManager(LocalContext.current)
    val viewModel = MyProductViewModel(sellRepository)
    val context = LocalContext.current
    val myProductIds = remember { mutableStateListOf<String>() }
    val loading by viewModel.loading.observeAsState(true)
    val db = AppDatabase.getDatabase(context)
    val productDao = db.productDao()
    var topPadding by remember { mutableStateOf(0.dp) }
    val lifecycleScope = rememberCoroutineScope()

    LaunchedEffect(true) {
//        myProductIds = productDao.getUploadedProducts().map { it.id }.toMutableList()
        viewModel.getUserProductIds(userId,{ids->
            myProductIds.addAll(ids)
            Log.d("MyProductScreen", "${myProductIds.size} Products loaded from Firebase")
            Log.d("MyProductScreen", "Products list fetched successfully")
        })
    }

    Scaffold(
        topBar = {
            CommonTopAppBar(
                navController = navController,
                screenName = "My Products",
                height = {height->
                    topPadding = height
                }
            )
        },
        content = { padding ->
            val a = padding
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(
                        color = Color(0xFFEAFFE7)
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(myProductIds){ productId->
                    var product by remember { mutableStateOf<Product?>(null) }
                    viewModel.loadProduct(productId){ loadedProduct ->
                        product = loadedProduct
                    }
                    product?.let {
                        MyProductItem(
                            product = it,
                            width = 350.dp,
                            onClick = { productId ->
                                navController.navigate("productDetails/$productId")
                            },
                            onEditClick = { productId ->
                                navController.navigate("editProduct/$productId")
                            },
                            onDeleteClick = { productId ->
                                viewModel.deleteProduct(it, userId) { success ->
                                    if (success) {
                                        myProductIds.remove(productId)
                                        viewModel.deleteFromMyProductRoom(productDao,productId)
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Failed to delete product",
                                            Toast.LENGTH_SHORT)
                                            .show()
                                    }
                                }
                            }
                        )
                    }
                    if (product == null){
                        Text("product couldn't be fetched")
                    }
                }
                item {
                    if (loading) {
                        TractorLoadingAnimation(
                            200.dp
                        )
                    }
                }
                item {
                    Text(text = "${myProductIds.size} items",
                        modifier = Modifier.fillMaxSize())
                }
                item {
                    Spacer(modifier = Modifier.height(120.dp))
                }
            }
        }
    )
}

@Composable
fun MyProductItem(
    product: Product,
    distance: Int? = null,
    width: Dp,
    onClick:(String) -> Unit = {},
    onEditClick:(String) -> Unit,
    onDeleteClick:(String) -> Unit
) {
    Box(
        modifier = Modifier
            .width(width - 28.dp)
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                BeautifulButton(
                    label = "Delete",
                    icon = R.drawable.baseline_delete_24,
                    onClick = {},
                    isGreen = false
                )
                BeautifulButton(
                    label = "Edit",
                    icon = R.drawable.baseline_edit_24,
                    onClick = {},
                    isGreen = true
                )
            }
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

@Composable
fun BeautifulButton(
    label: String,
    icon: Int,
    onClick: () -> Unit,
    isGreen: Boolean
){
    Button(
        onClick = onClick,
        modifier = Modifier
            .wrapContentSize()
            .padding(vertical = 4.dp, horizontal = 4.dp)
            .clip(RoundedCornerShape(25.dp))
            .background(
                color = if (isGreen) Color(0xFFCFFFCF) else Color(0xFFFFE8E8), // Replace with your primary color
                shape = RoundedCornerShape(25.dp)
            )
            .border(
                width = 1.dp,
                color = if (isGreen) Color(0xFF006700) else Color(0xFF830000), // Replace with your primary color
                shape = RoundedCornerShape(25.dp)
            ),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isGreen) Color(0xFFCFFFCF) else Color(0xFFFFE8E8),
            disabledContentColor = if (isGreen) Color(0xFFCFFFCF) else Color(0xFFFFE8E8)
        )
    ) {
        Row(
            modifier = Modifier.wrapContentSize().background(Color.Transparent),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(icon),
                contentDescription = null,
                tint = if (isGreen) Color(0xFF006700) else Color(0xFF830000), // Replace with your primary color
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                color = if (isGreen) Color(0xFF006700) else Color(0xFF830000), // Replace with your primary color
            )
        }
    }
}

class MyLifecycleObserver(
    private val coroutineScope: CoroutineScope,
    private val onStartAction: () -> Unit
) : LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        coroutineScope.launch(Dispatchers.Main) {
            onStartAction()
        }
    }
}