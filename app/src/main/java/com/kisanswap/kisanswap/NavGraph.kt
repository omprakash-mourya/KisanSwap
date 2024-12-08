package com.kisanswap.kisanswap

import android.os.Build
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import com.kisanswap.kisanswap.repositories.AuthRepository
import com.kisanswap.kisanswap.repositories.FirestoreRepository
import com.kisanswap.kisanswap.screen.AccountScreen
import com.kisanswap.kisanswap.screen.AuthScreen
import com.kisanswap.kisanswap.screen.BuyScreen
import com.kisanswap.kisanswap.screen.EditProductScreen
import com.kisanswap.kisanswap.screen.HomeScreen
import com.kisanswap.kisanswap.screen.IntroScreen
import com.kisanswap.kisanswap.screen.MapScreen
import com.kisanswap.kisanswap.screen.MyProductScreen
import com.kisanswap.kisanswap.screen.PhoneAuthScreen
import com.kisanswap.kisanswap.screen.SellScreen
import com.kisanswap.kisanswap.viewmodel.SellViewModel

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun NavGraph(navController: NavHostController, startDestination: String = "home") {
    val context = LocalContext.current
    NavHost(navController = navController, startDestination = startDestination) {
        navigation(startDestination = "intro-screen", route = "intro") {
            composable("intro-screen") {
                FullPage {
                    IntroScreen(navController)
                }
            }
        }
        navigation(startDestination = "login", route = "auth"){
            composable("login") {
                FullPage {
                    AuthScreen(navController, AuthRepository(activity = context as ComponentActivity))
                }
            }
            composable("auth-detail"){
                FullPage {
                    AuthScreen(navController, AuthRepository(activity = context as ComponentActivity))
                }
            }
            composable("phone-auth"){
                FullPage {
                    PhoneAuthScreen(navController, AuthRepository(activity = context as ComponentActivity))
                }
            }
        }
        navigation(startDestination = "sell", route = "home"){
            composable("home-page") {
                MyApp(navController) { HomeScreen(navController, FirestoreRepository())  }
            }
            composable("sell"){
                MyApp(navController) { SellScreen(navController,FirestoreRepository()) }
            }
            composable("map") {
                MyApp(navController) { MapScreen(navController){ selectedLocation ->
                    // Handle the selected location
                    SellViewModel.selectedLocation = selectedLocation
                } }
            }
            composable("my-products"){
                MyApp(navController) { MyProductScreen(navController, FirestoreRepository()) }
            }
            composable("edit-product/{productId}"){ backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                MyApp(navController) { EditProductScreen(navController, FirestoreRepository(), productId) }
            }
            composable("account"){
                MyApp(navController) { AccountScreen(navController) }
            }
            composable(
                "buy/{productId}",
                deepLinks = listOf(navDeepLink {
                    uriPattern = "https://www.kisanswap.com/buy/{productId}"
                })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                MyApp(navController) {
                        BuyScreen(
                            navController = navController,
                            productId = productId,
                            firestoreRepository = FirestoreRepository()
                        )
                    }
            }
        }
    }
}