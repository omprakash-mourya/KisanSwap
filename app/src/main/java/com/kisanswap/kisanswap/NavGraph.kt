package com.kisanswap.kisanswap

import android.os.Build
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import com.kisanswap.kisanswap.auth.repository.AuthRepository
import com.kisanswap.kisanswap.home.repository.HomeFirebaseRepository
import com.kisanswap.kisanswap.repositories.FirestoreRepository
import com.kisanswap.kisanswap.screen.AccountScreen
import com.kisanswap.kisanswap.screen.AuthScreen
import com.kisanswap.kisanswap.product.buy.screen.BuyScreen
import com.kisanswap.kisanswap.product.sell.screen.EditProductScreen
import com.kisanswap.kisanswap.home.screen.HomeScreen
import com.kisanswap.kisanswap.intro.screen.IntroScreen
import com.kisanswap.kisanswap.product.buy.repository.BuyFirebaseRepository
import com.kisanswap.kisanswap.product.sell.repository.SellFirebaseRepository
import com.kisanswap.kisanswap.user.buyer.screen.MapScreen
import com.kisanswap.kisanswap.product.sell.screen.MyProductScreen
import com.kisanswap.kisanswap.screen.PhoneAuthScreen
import com.kisanswap.kisanswap.product.sell.screen.SellScreen
import com.kisanswap.kisanswap.product.sell.viewmodel.SellViewModel
import com.kisanswap.kisanswap.savedProducts.screen.SavedProductsScreen

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
                MyApp(navController) { HomeScreen(navController, HomeFirebaseRepository())  }
            }
            composable("sell"){
                MyApp(navController) { SellScreen(navController, SellFirebaseRepository()) }
            }
            composable("map") {
                MyApp(navController) { MapScreen(navController){ selectedLocation ->
                    SellViewModel.selectedLocation = selectedLocation
                } }
            }
            composable("saved-products") {
                MyApp(navController) {
                    SavedProductsScreen(navController)
                }
            }
            composable("my-products"){
                MyApp(navController) { MyProductScreen(navController, HomeFirebaseRepository(), SellFirebaseRepository()) }
            }
            composable("edit-product/{productId}"){ backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                MyApp(navController) { EditProductScreen(navController, SellFirebaseRepository(), productId) }
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
                            firebaseRepository = BuyFirebaseRepository()
                        )
                    }
            }
        }
    }
}