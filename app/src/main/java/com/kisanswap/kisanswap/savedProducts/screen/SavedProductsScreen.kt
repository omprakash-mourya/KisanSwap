package com.kisanswap.kisanswap.savedProducts.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.kisanswap.kisanswap.common.components.CommonTopAppBar
import com.kisanswap.kisanswap.dataClass.SpecificationForProduct
import com.kisanswap.kisanswap.product.buy.screen.ContactButtons
import com.kisanswap.kisanswap.product.buy.screen.MinorDetails
import com.kisanswap.kisanswap.product.buy.screen.ProductDetails

@Composable
fun SavedProductsScreen(navController: NavController) {
    val scrollState = rememberScrollState()
    Scaffold(
        topBar = {
            CommonTopAppBar(
                navController = navController,
                screenName = "Saved Products",
                height = {_ -> }
            )
        },
        content = {paddding->
            val a = paddding
            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(scrollState).padding(paddding),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MinorDetails(
                    shortDetails = "Mahindra 735 DI tractor",
                    price = 675000,
                    isNegotiable = false,
                    location = "Kolkata, West Bengal",
                    distance = 1235
                )
                ProductDetails(
                    description = "2018 model Mahindra 575 DI tractor in excellent condition. Regular maintenance done. New tires fitted 6 months ago. All papers complete and up to date.",
                    specifications = listOf(
                        SpecificationForProduct(
                            name = "Power",
                            specificationRoute = "power",
                            amount = "42 HP",
                            unit = null
                        ),
                        SpecificationForProduct(
                            name = "Fuel",
                            specificationRoute = "Fuel",
                            amount = "Diesel",
                            unit = null
                        ),
                        SpecificationForProduct(
                            name = "Transmission",
                            specificationRoute = "transmission",
                            amount = "8 Forward + 2 Reverse",
                            unit = null
                        )
                    )
                )
                ContactButtons(
                    onMapClick = {},
                    onCallClick = {},
                    onWhatsAppClick = {}
                )
                Spacer(
                    modifier = Modifier.fillMaxWidth().height(150.dp)
                )
            }
        }
    )
}

// new ui
@Composable
fun First(){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Mahindra 575 DI Tractor",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        letterSpacing = 0.sp
                    )
                )
                Text(
                    text = "₹5,50,000",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color.Green,
                        letterSpacing = 0.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .background(Color(0xFFE8F5E9), shape = RoundedCornerShape(20.dp))
                    .padding(8.dp)
            ) {
                Text(
                    text = "Negotiable",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.Green,
                        letterSpacing = 0.sp
                    )
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Place,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Amritsar, Punjab • ",
                style = MaterialTheme.typography.titleSmall.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    letterSpacing = 0.sp
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "45 km away",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.Green,
                    letterSpacing = 0.sp
                )
            )
        }
    }
}

@Composable
fun Second(){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .clip(RoundedCornerShape(16.dp))
            .padding(20.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Product Details",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = MaterialTheme.colorScheme.onPrimary,
                    letterSpacing = 0.sp
                )
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Power",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.secondary,
                            letterSpacing = 0.sp
                        )
                    )
                    Text(
                        text = "47 HP",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            letterSpacing = 0.sp
                        )
                    )
                }
                Column {
                    Text(
                        text = "Engine",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.secondary,
                            letterSpacing = 0.sp
                        )
                    )
                    Text(
                        text = "4 Cylinder",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            letterSpacing = 0.sp
                        )
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Year",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.secondary,
                            letterSpacing = 0.sp
                        )
                    )
                    Text(
                        text = "2020",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            letterSpacing = 0.sp
                        )
                    )
                }
                Column {
                    Text(
                        text = "Hours Used",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.secondary,
                            letterSpacing = 0.sp
                        )
                    )
                    Text(
                        text = "1200 hrs",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            letterSpacing = 0.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun Third(){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .clip(RoundedCornerShape(16.dp))
            .padding(20.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Description",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = MaterialTheme.colorScheme.onPrimary,
                    letterSpacing = 0.sp
                )
            )
            Text(
                text = "Well-maintained Mahindra 575 DI tractor with power steering and all original parts. Perfect for farming operations. Regular service history available. Includes new tires and battery.",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.secondary,
                    letterSpacing = 0.sp
                )
            )
        }
    }
}

@Composable
fun Forth(){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .clip(RoundedCornerShape(16.dp))
            .padding(20.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Seller Details",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = MaterialTheme.colorScheme.onPrimary,
                    letterSpacing = 0.sp
                )
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(MaterialTheme.colorScheme.secondary)
                        .clip(CircleShape)
                ) {
                    Image(
                        painter = rememberAsyncImagePainter("https://images.unsplash.com/photo-1533241242276-46a506b40d66?w=500&h=500"),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Gurpreet Singh",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            letterSpacing = 0.sp
                        )
                    )
                    Text(
                        text = "Member since 2021",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.secondary,
                            letterSpacing = 0.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun Fifth(){
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(
            onClick = { println("Button pressed ...") },
            modifier = Modifier
                .width(160.dp)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF128C7E),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(25.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Call,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Call Seller",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color.White,
                    letterSpacing = 0.sp
                )
            )
        }
        Button(
            onClick = { println("Button pressed ...") },
            modifier = Modifier
                .width(160.dp)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
            shape = RoundedCornerShape(25.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Chat on WhatsApp",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color.White,
                    letterSpacing = 0.sp
                )
            )
        }
    }
}

@Composable
fun Sixth(){
    Card(
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF0F5F9))
                .clip(RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Mahindra 575 DI Tractor",
                    style = MaterialTheme.typography. bodyLarge.copy(
                        color = Color(0xFF161C24),
                        letterSpacing = 0.sp,
                        fontWeight = FontWeight.W600
                    )
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "₹4,50,000",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color(0xFF2E7D32),
                            letterSpacing = 0.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFE8F5E9), shape = RoundedCornerShape(20.dp))
                            .padding(vertical = 16.dp, horizontal = 8.dp)
                    ) {
                        Text(
                            text = "45 km away",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = Color(0xFF2E7D32),
                                letterSpacing = 0.sp
                            )
                        )
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color(0xFF2797FF),
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Village Mehrauli, Delhi",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Color(0xFF161C24),
                            letterSpacing = 0.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun Seventh(){
    Card(
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF0F5F9))
                .clip(RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Product Details",
                    style = MaterialTheme.typography. bodyLarge.copy(
                        color = Color(0xFF161C24),
                        letterSpacing = 0.sp,
                        fontWeight = FontWeight.W600
                    )
                )
                Text(
                    text = "2018 model Mahindra 575 DI tractor in excellent condition. Regular maintenance done. New tires fitted 6 months ago. All papers complete and up to date.",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color(0xFF161C24),
                        letterSpacing = 0.sp
                    )
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .clip(RoundedCornerShape(8.dp))
                        .padding(16.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Power",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = Color(0xFF161C24),
                                    letterSpacing = 0.sp
                                )
                            )
                            Text(
                                text = "47 HP",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = Color(0xFF2797FF),
                                    letterSpacing = 0.sp,
                                    fontWeight = FontWeight.W600
                                )
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Engine Type",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = Color(0xFF161C24),
                                    letterSpacing = 0.sp
                                )
                            )
                            Text(
                                text = "4 Cylinder",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = Color(0xFF2797FF),
                                    letterSpacing = 0.sp,
                                    fontWeight = FontWeight.W600
                                )
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Hours Used",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = Color(0xFF161C24),
                                    letterSpacing = 0.sp
                                )
                            )
                            Text(
                                text = "3,500 hrs",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = Color(0xFF2797FF),
                                    letterSpacing = 0.sp,
                                    fontWeight = FontWeight.W600
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Eighth(){
    Card(
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF0F5F9))
                .clip(RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Contact Seller",
                    style = MaterialTheme.typography. bodyLarge.copy(
                        color = Color(0xFF161C24),
                        letterSpacing = 0.sp,
                        fontWeight = FontWeight.W600
                    )
                )
                Button(
                    onClick = { println("Button pressed ...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Call Seller",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = Color.White,
                            letterSpacing = 0.sp
                        )
                    )
                }
                Button(
                    onClick = { println("Button pressed ...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Chat on WhatsApp",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = Color.White,
                            letterSpacing = 0.sp
                        )
                    )
                }
                Button(
                    onClick = { println("Button pressed ...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2797FF)),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "View on Map",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = Color.White,
                            letterSpacing = 0.sp
                        )
                    )
                }
            }
        }
    }
}