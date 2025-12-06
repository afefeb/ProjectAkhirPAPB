package com.example.konekumkm.view.ui.home

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.konekumkm.ui.theme.KonekumkmTheme
import com.example.konekumkm.view.components.UMKMListItem
import com.example.konekumkm.view.navigation.Screen
import com.example.konekumkm.view.viewmodel.*
import kotlinx.coroutines.launch

class HomeActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KonekumkmTheme {
                val navController = rememberNavController()
                val viewModel: HomeViewModel = viewModel()
                val viewModelAuth: AuthViewModel = viewModel()
                val umkmList by viewModel.umkmList.collectAsState()
                val isLoading by viewModel.isLoading.collectAsState()

                val authState by viewModelAuth.authState.collectAsState()
                
                var currentRoute by remember { mutableStateOf(Screen.Splash.route) }
                
                val routesWithoutBottomNav = listOf(
                    Screen.Splash.route,
                    Screen.Onboarding.route,
                    Screen.Login.route,
                    Screen.Register.route
                )
                
                val showBottomNav = currentRoute !in routesWithoutBottomNav

                fun navigateTo(route: String) {
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }

                Scaffold(
                    topBar = {
                        if (showBottomNav) {
                            CenterAlignedTopAppBar(
                                title = {
                                    androidx.compose.foundation.Image(
                                        painter = androidx.compose.ui.res.painterResource(id = com.example.konekumkm.R.drawable.logo),
                                        contentDescription = "Logo",
                                        modifier = androidx.compose.ui.Modifier.height(40.dp)
                                    )
                                },
                                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                    containerColor = androidx.compose.ui.graphics.Color.White
                                )
                            )
                        }
                    },
                    bottomBar = {
                        if (showBottomNav) {
                            NavigationBar {
                                NavigationBarItem(
                                    selected = currentRoute == Screen.Home.route,
                                    onClick = { navigateTo(Screen.Home.route) },
                                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                                    label = { Text("Home") }
                                )
                                NavigationBarItem(
                                    selected = currentRoute == Screen.Map.route,
                                    onClick = { navigateTo(Screen.Map.route) },
                                    icon = { Icon(Icons.Default.LocationOn, contentDescription = "Map") },
                                    label = { Text("Map") }
                                )
                                NavigationBarItem(
                                    selected = currentRoute == Screen.Produk.route,
                                    onClick = { navigateTo(Screen.Produk.route) },
                                    icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Produk") },
                                    label = { Text("Produk") }
                                )
                                NavigationBarItem(
                                    selected = currentRoute == Screen.AddUmkm.route,
                                    onClick = { navigateTo(Screen.AddUmkm.route) },
                                    icon = { Icon(Icons.Default.Add, contentDescription = "Gabung") },
                                    label = { Text("Gabung") }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    LaunchedEffect(navController) {
                        navController.currentBackStackEntryFlow.collect { backStackEntry ->
                            currentRoute = backStackEntry.destination.route ?: Screen.Splash.route
                        }
                    }
                    
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Splash.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                            composable(Screen.Splash.route) {
                                com.example.konekumkm.view.ui.splash.SplashScreen(
                                    navController = navController,
                                    authViewModel = viewModelAuth
                                )
                            }
                            
                            composable(Screen.Onboarding.route) {
                                com.example.konekumkm.view.ui.onboarding.OnboardingScreen(navController)
                            }
                            
                            composable(Screen.Home.route) {
                                HomeScreen(
                                    navController = navController,
                                    homeViewModel = viewModel,
                                    authViewModel = viewModelAuth
                                )
                            }
                            
                            composable(Screen.Search.route) {
                                com.example.konekumkm.view.ui.search.SearchScreen(
                                    navController = navController,
                                    homeViewModel = viewModel
                                )
                            }

                            composable(Screen.Map.route) {
                                com.example.konekumkm.view.ui.map.MapScreen(navController, viewModel)
                            }

                            composable(Screen.Detail.route) { backStackEntry ->
                                val umkmId = backStackEntry.arguments?.getString("umkmId") ?: ""
                                com.example.konekumkm.view.ui.detail.DetailScreen(umkmId, navController)
                            }
                            composable(Screen.Produk.route) {
                                com.example.konekumkm.view.ui.product.ProductListScreen(
                                    navController = navController
                                )
                            }

                            composable(Screen.Blog.route) { PlaceholderScreen("Halaman Blog") }
                            composable(Screen.About.route) { PlaceholderScreen("Halaman About") }
                            composable(Screen.Login.route) {
                                com.example.konekumkm.view.ui.auth.LoginScreen(navController)
                            }
                            composable(Screen.Register.route) {
                                com.example.konekumkm.view.ui.auth.RegisterScreen(navController)
                            }
                            
                            composable(Screen.Cart.route) {
                                com.example.konekumkm.view.ui.cart.CartScreen(navController)
                            }
                            composable(Screen.PaymentMethod.route) {
                                com.example.konekumkm.view.ui.cart.PaymentMethodScreen(navController)
                            }
                            composable(Screen.Payment.route) { backStackEntry ->
                                val paymentMethod = backStackEntry.arguments?.getString("paymentMethod") ?: ""
                                com.example.konekumkm.view.ui.cart.PaymentScreen(navController, paymentMethod)
                            }
                            composable(Screen.OrderSuccess.route) { backStackEntry ->
                                val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
                                com.example.konekumkm.view.ui.cart.OrderSuccessScreen(navController, orderId)
                            }
                            
                            composable("admin_dashboard") {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text("Selamat Datang ADMIN!", style = MaterialTheme.typography.headlineLarge)
                                    Text("Dashboard: Top Selling & Requests")
                                    Button(onClick = {
                                        navController.navigate(Screen.Login.route) { popUpTo("admin_dashboard") { inclusive = true } }
                                    }) {
                                        Text("Logout Admin")
                                    }
                                }
                            }
                            composable(Screen.AddUmkm.route) {
                                com.example.konekumkm.view.ui.umkm.AddUmkmScreen(navController)
                            }
                        }
                    }
                }
            }
        }
    }

@Composable
fun PlaceholderScreen(title: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = title, style = MaterialTheme.typography.headlineMedium)
    }
}