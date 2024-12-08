package com.kisanswap.kisanswap.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kisanswap.kisanswap.R

@Composable
fun AccountScreen(navController: NavController){
    /*Column(
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
    ){
        Text("This is account screen")
    }*/
    InstagramHomePage(navController = navController)
}

@Composable
fun InstagramHomePage(navController: NavController) {
    Scaffold(
        topBar = { InstagramTopAppBar() },
        bottomBar = { InstagramBottomNavigationBar() }
    ) { padding ->
        val a= padding
        Column(modifier = Modifier.fillMaxSize()) {
            StoriesSection()
            FeedSection()
        }
    }
}

@Composable
fun InstagramTopAppBar() {
    TopAppBar(
        title = {
            Image(
                painter = painterResource(id = R.drawable.baseline_agriculture_24),
                contentDescription = "Instagram Logo",
                modifier = Modifier.height(24.dp)
            )
        },
        actions = {
            IconButton(onClick = { /*   */ }) {
                Icon(imageVector = Icons.Default.FavoriteBorder, contentDescription = "Favorite")
            }
            IconButton(onClick = {  }) {
                Icon(imageVector = Icons.Default.Send, contentDescription = "Send")
            }
        },
        backgroundColor = Color.White,
        contentColor = Color.Black,
        elevation = 0.dp
    )
}

@Composable
fun StoriesSection() {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        items(10) { index ->
            StoryItem()
        }
    }
}

@Composable
fun StoryItem() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(70.dp)
                .background(Color.Gray, shape = CircleShape)
        ) {
            // Placeholder for story image
        }
        Text(
            text = "Story",
            fontSize = 12.sp,
            color = Color.Black,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun FeedSection() {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(10) { index ->
            FeedItem()
        }
    }
}

@Composable
fun FeedItem() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.Gray, shape = CircleShape)
            ) {
                // Placeholder for profile image
            }
            Text(
                text = "Username",
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(Color.LightGray)
        ) {
            // Placeholder for post image
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            IconButton(onClick = { /*   */ }) {
                Icon(imageVector = Icons.Default.FavoriteBorder, contentDescription = "Like")
            }
            IconButton(onClick = { /*   */ }) {
                Icon(imageVector = Icons.Default.Email, contentDescription = "Comment")
            }
            IconButton(onClick = { /*   */ }) {
                Icon(imageVector = Icons.Default.Send, contentDescription = "Send")
            }
        }
        Text(
            text = "Liked by user1 and others",
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        Text(
            text = "View all comments",
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun InstagramBottomNavigationBar() {
    BottomNavigation(
        backgroundColor = Color.White,
        contentColor = Color.Black
    ) {
        BottomNavigationItem(
            icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "Home") },
            selected = true,
            onClick = { /*   */ }
        )
        BottomNavigationItem(
            icon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search") },
            selected = false,
            onClick = { /*   */ }
        )
        BottomNavigationItem(
            icon = { Icon(imageVector = Icons.Default.Add, contentDescription = "Add") },
            selected = false,
            onClick = { /*   */ }
        )
        BottomNavigationItem(
            icon = { Icon(imageVector = Icons.Default.FavoriteBorder, contentDescription = "Favorite") },
            selected = false,
            onClick = { /*   */ }
        )
        BottomNavigationItem(
            icon = { Icon(imageVector = Icons.Default.Person, contentDescription = "Profile") },
            selected = false,
            onClick = { /*   */ }
        )
    }
}