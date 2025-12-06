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
                // Setup Navigasi & ViewModel
                val navController = rememberNavController()
                val viewModel: HomeViewModel = viewModel()
                val viewModelAuth: AuthViewModel = viewModel()
                val umkmList by viewModel.umkmList.collectAsState()
                val isLoading by viewModel.isLoading.collectAsState()

                val authState by viewModelAuth.authState.collectAsState()
                
                // Track current route untuk mengontrol bottom nav
                var currentRoute by remember { mutableStateOf(Screen.Splash.route) }
                
                // Daftar route yang TIDAK boleh menampilkan bottom navigation
                val routesWithoutBottomNav = listOf(
                    Screen.Splash.route,
                    Screen.Onboarding.route,
                    Screen.Login.route,
                    Screen.Register.route
                )
                
                // Cek apakah bottom nav harus ditampilkan
                val showBottomNav = currentRoute !in routesWithoutBottomNav

                // Fungsi helper untuk pindah halaman
                fun navigateTo(route: String) {
                    navController.navigate(route) {
                        // Prevent multiple copies of the same destination
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }

                // --- KONTEN UTAMA APLIKASI ---
                Scaffold(
                    topBar = {
                        // Tampilkan TopBar hanya jika bottom nav ditampilkan
                        if (showBottomNav) {
                            CenterAlignedTopAppBar(
                                title = {
                                    Text(
                                        "UMKMConnect",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                },
                                actions = {
                                    // Tombol Logout di TopBar
                                    if (authState is AuthState.Success) {
                                        IconButton(onClick = {
                                            viewModelAuth.logout()
                                            val prefManager = com.example.konekumkm.utils.PreferenceManager(this@HomeActivity)
                                            prefManager.resetOnboarding()
                                            navController.navigate(Screen.Login.route) {
                                                popUpTo(0) { inclusive = true }
                                            }
                                        }) {
                                            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout")
                                        }
                                    }
                                }
                            )
                        }
                    },
                    bottomBar = {
                        // Bottom Navigation Bar
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
                    // Observer untuk track route changes
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
                            // SPLASH SCREEN
                            composable(Screen.Splash.route) {
                                com.example.konekumkm.view.ui.splash.SplashScreen(
                                    navController = navController,
                                    authViewModel = viewModelAuth
                                )
                            }
                            
                            // ONBOARDING / LANDING PAGE
                            composable(Screen.Onboarding.route) {
                                com.example.konekumkm.view.ui.onboarding.OnboardingScreen(navController)
                            }
                            
                            // 1. HOME
                            composable(Screen.Home.route) {
                                if (isLoading) {
                                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        CircularProgressIndicator()
                                    }
                                } else if (umkmList.isEmpty()) {
                                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        Text("Tidak ada data UMKM.")
                                    }
                                } else {
                                    LazyColumn {
                                        items(umkmList) { umkm ->
                                            UMKMListItem(umkm = umkm) {
                                                navController.navigate(Screen.Detail.createRoute(umkm.id))
                                            }
                                        }
                                    }
                                }
                            }

                            // 2. MAP (Panggil MapScreen yang sudah Anda buat)
                            composable(Screen.Map.route) {
                                com.example.konekumkm.view.ui.map.MapScreen(navController, viewModel)
                            }

                            // 3. DETAIL (Panggil DetailScreen)
                            composable(Screen.Detail.route) { backStackEntry ->
                                val umkmId = backStackEntry.arguments?.getString("umkmId") ?: ""
                                com.example.konekumkm.view.ui.detail.DetailScreen(umkmId, navController)
                            }
                            composable(Screen.Produk.route) {
                                com.example.konekumkm.view.ui.product.ProductListScreen(
                                    navController = navController
                                )
                            }

                            // 4. Placeholder Halaman Lain (Agar tidak error saat diklik)
                            composable(Screen.Blog.route) { PlaceholderScreen("Halaman Blog") }
                            composable(Screen.About.route) { PlaceholderScreen("Halaman About") }
                            composable(Screen.Login.route) {
                                com.example.konekumkm.view.ui.auth.LoginScreen(navController)
                            }
                            composable(Screen.Register.route) {
                                com.example.konekumkm.view.ui.auth.RegisterScreen(navController)
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
                                        // Logout sementara
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

// Komponen sementara untuk halaman yang belum jadi
@Composable
fun PlaceholderScreen(title: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = title, style = MaterialTheme.typography.headlineMedium)
    }
}