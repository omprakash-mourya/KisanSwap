package com.kisanswap.kisanswap.screen

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.google.accompanist.pager.*
import com.google.firebase.auth.FirebaseAuth
import com.kisanswap.kisanswap.PreferenceManager
import com.kisanswap.kisanswap.R
import com.kisanswap.kisanswap.common.components.CommonTopAppBar
import com.kisanswap.kisanswap.common.components.TractorLoadingAnimation


@Composable
fun AccountScreen(navController: NavController){
    val context = LocalContext.current
    val preferenceManager = PreferenceManager(context)
    var topPadding by remember { mutableStateOf(0.dp) }
    val email = preferenceManager.getEmail()
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val gmail = user?.email
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = { CommonTopAppBar(
            screenName = "Account",
            navController = navController,
            height = {height->
                topPadding = height
            }
        )
        }
    ) { paddding ->
        val a = paddding
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .background(
                    color = Color(0xFFE3F1E3)
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            ProfileImageCard()
            UserInfoCard{
                UserInfoRow(
                    icon = R.drawable.baseline_account_box_24, // Replace with your person icon resource
                    text = "Rajesh Kumar",
                    onEditClick = { println("Edit name clicked") }
                )
                UserInfoRow(
                    icon = R.drawable.baseline_call_24, // Replace with your phone icon resource
                    text = "+91 98765 43210",
                    onEditClick = { println("Edit phone clicked") }
                )
                UserInfoRow(
                    icon = R.drawable.baseline_email_24, // Replace with your email icon resource
                    text = gmail?: "Email"
                )
                UserInfoRow(
                    icon = R.drawable.baseline_location_pin_24, // Replace with your location icon resource
                    text = "Village Mehrauli, Delhi",
                    onEditClick = { println("Edit location clicked") }
                )
            }
            UserInfoCard {
                ScreenNavigationRow(
                    icon = R.drawable.baseline_favorite_24,
                    text = "Saved Items",
                    onClick = { navController.navigate(route = "saved-products") }
                )
                ScreenNavigationRow(
                    icon = R.drawable.baseline_shopping_cart_24, // Replace with your inventory icon resource
                    text = "Your Products",
                    onClick = { println("Saved Items clicked") }
                )
                ScreenNavigationRow(
                    icon = R.drawable.baseline_history_24, // Replace with your inventory icon resource
                    text = "History",
                    onClick = { println("Saved Items clicked") }
                )
            }
            UserInfoCard {
                AccountOptionRow(
                    iconRes = R.drawable.baseline_logout_24, // Replace with your logout icon resource
                    text = "Sign Out"
                )
                AccountOptionRow(
                    iconRes = R.drawable.baseline_delete_forever_24, // Replace with your delete account icon resource
                    text = "Delete Account"
                )
            }
            Spacer(modifier = Modifier.height(130.dp))
        }
    }
}

@Composable
fun ProfileImageCard() {
    Box(
        modifier = Modifier
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .size(170.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            backgroundColor = Color.White,
            shape = CircleShape,
            elevation = 4.dp,
            modifier = Modifier
                .size(160.dp)
                .border(
                    width = 1.dp,
                    color = Color(0xFFFFFFFF),
                    shape = CircleShape
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                /*Image(
                    painter = painterResource(R.drawable.baseline_account_circle_24),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                )*/
                Icon(
                    painter = painterResource(R.drawable.baseline_account_circle_24),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    tint = Color(0x66232C23)
                )
            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(48.dp)
                .background(Color.Transparent, CircleShape)
        ) {
            IconButton(
                onClick = { println("IconButton pressed ...") },
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFF3EFF5C), CircleShape)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_edit_24), // Replace with your edit icon resource
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun UserInfoCard(
    content: @Composable () -> Unit
) {
    Card(
        backgroundColor = Color(0xFFF0F5F9),
        shape = RoundedCornerShape(16.dp),
        elevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            content()
        }
    }
}

@Composable
fun UserInfoRow(
    icon: Int,
    text: String,
    onEditClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = Color(0xFF027703),
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.body1.copy(
                    color = Color(0xFF161C24),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W500
                )
            )
        }
        if (onEditClick != null) {
            IconButton(
                onClick = onEditClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_edit_24), // Replace with your edit icon resource
                    contentDescription = null,
                    tint = Color(0xFF027703),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun ScreenNavigationRow(
    icon: Int,
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = Color(0xFF027703),
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.body1.copy(
                    color = Color(0xFF161C24),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W500
                )
            )
        }
        Icon(
            painter = painterResource(id = R.drawable.baseline_arrow_forward_ios_24), // Replace with your edit icon resource
            contentDescription = null,
            tint = Color(0xFF027703),
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable
fun AccountOptionRow(
    iconRes: Int,
    text: String,
    onClick: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .clickable { onClick() },
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            tint = Color(0xFFFF0000),
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.body1.copy(
                color = Color(0xFF161C24),
                fontSize = 16.sp,
                fontWeight = FontWeight.W500
            )
        )
    }
}