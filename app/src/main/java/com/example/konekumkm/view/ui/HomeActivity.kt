package com.example.konekumkm.view.ui.home

import android.os.Bundle
import android.widget.Toast
import androidx.compose.material.icons.automirrored.filled.List
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.konekumkm.ui.theme.KonekumkmTheme // Sesuaikan nama tema Anda
import com.example.konekumkm.view.components.UMKMListItem
import com.example.konekumkm.view.navigation.Screen
import com.example.konekumkm.view.viewmodel.HomeViewModel
import kotlinx.coroutines.launch
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.runtime.Composable
import com.example.konekumkm.view.viewmodel.AuthState
import com.example.konekumkm.view.viewmodel.*
// Import Screen map & add umkm jika sudah dibuat filenya nanti

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

                // State untuk Drawer (Menu Samping)
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()
                val authState by viewModelAuth.authState.collectAsState()

                // Fungsi helper untuk pindah halaman & tutup drawer
                fun navigateTo(route: String) {
                    scope.launch {
                        drawerState.close()
                        navController.navigate(route)
                    }
                }

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet {
                            // --- HEADER DRAWER (Dinamis) ---
                            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                                // LOGIKA: Cek Status Login
                                if (authState is AuthState.Success) {
                                    val user = (authState as AuthState.Success).user
                                    Text(
                                        text = "Halo, ${user.name}!", // Nama User
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(text = user.email, style = MaterialTheme.typography.bodySmall)

                                    // Badge Role
                                    Surface(
                                        color = MaterialTheme.colorScheme.secondaryContainer,
                                        shape = RoundedCornerShape(4.dp),
                                        modifier = Modifier.padding(top = 8.dp)
                                    ) {
                                        Text(
                                            text = user.role.uppercase(),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                } else {
                                    // Jika Belum Login
                                    Text(
                                        text = "UMKMConnect",
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text("Silakan Login Dahulu")
                                }
                            }
                            HorizontalDivider()

                            // --- MENU ITEMS (Tetap Sama) ---
                            Spacer(modifier = Modifier.height(16.dp))
                            NavigationDrawerItem(
                                label = { Text("Home") },
                                icon = { Icon(Icons.Default.Home, null) },
                                selected = false,
                                onClick = { navigateTo(Screen.Home.route) },
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )
                            NavigationDrawerItem(
                                label = { Text("Produk") },
                                icon = { Icon(Icons.Default.List, null) }, // Icon List sebagai pengganti Keranjang
                                selected = false,
                                onClick = { navigateTo(Screen.Produk.route) },
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )
                            NavigationDrawerItem(
                                label = { Text("Peta UMKM") },
                                icon = { Icon(Icons.Default.LocationOn, null) },
                                selected = false,
                                onClick = { navigateTo(Screen.Map.route) },
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )
                            NavigationDrawerItem(
                                label = { Text("Blog") },
                                icon = { Icon(Icons.Default.Info, null) }, // Icon Info sementara
                                selected = false,
                                onClick = { navigateTo(Screen.Blog.route) },
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )
                            NavigationDrawerItem(
                                label = { Text("About") },
                                icon = { Icon(Icons.Default.Person, null) },
                                selected = false,
                                onClick = { navigateTo(Screen.About.route) },
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            // --- TOMBOL LOGIN / LOGOUT (Dinamis) ---
                            if (authState is AuthState.Success) {
                                // JIKA SUDAH LOGIN -> TAMPILKAN LOGOUT
                                Button(
                                    onClick = {
                                        viewModelAuth.logout() // Panggil fungsi logout
                                        scope.launch { drawerState.close() }
                                        Toast.makeText(this@HomeActivity, "Berhasil Logout", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                                ) {
                                    Icon(Icons.Default.ExitToApp, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Logout / Keluar")
                                }
                            } else {
                                // JIKA BELUM LOGIN -> TAMPILKAN LOGIN
                                Button(
                                    onClick = { navigateTo(Screen.Login.route) },
                                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                                ) {
                                    Icon(Icons.Default.AccountCircle, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Login / Masuk")
                                }
                            }
                        }
                    }
                ) {
                    // --- KONTEN UTAMA APLIKASI ---
                    Scaffold(
                        topBar = {
                            CenterAlignedTopAppBar(
                                title = {
                                    Text(
                                        "UMKMConnect",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                },
                                navigationIcon = {
                                    // Tombol Hamburger untuk Buka Menu
                                    IconButton(onClick = {
                                        scope.launch { drawerState.open() }
                                    }) {
                                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                                    }
                                }
                            )
                        },
                        floatingActionButton = {
                            FloatingActionButton(onClick = { navController.navigate(Screen.Map.route) }) {
                                Icon(Icons.Default.LocationOn, contentDescription = "Peta")
                            }
                        }
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = Screen.Home.route,
                            modifier = Modifier.padding(innerPadding)
                        ) {
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
                                    navController = navController,
                                    onMenuClick = {
                                        // Buka drawer saat ikon hamburger di halaman produk diklik
                                        scope.launch { drawerState.open() }
                                    }
                                )
                            }

                            // 4. Placeholder Halaman Lain (Agar tidak error saat diklik)
                            composable(Screen.Blog.route) { PlaceholderScreen("Halaman Blog") }
                            composable(Screen.About.route) { PlaceholderScreen("Halaman About") }
                            composable(Screen.Login.route) {
                                com.example.konekumkm.view.ui.auth.LoginScreen(navController)
                            }
                            composable("register") { // String "register" atau Screen.Register.route (jika sudah dibuat di Screen.kt)
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
                            composable(Screen.AddUmkm.route) { backStackEntry ->
                                // Cek apakah ada kiriman 'imageUri' dari CameraScreen?
                                val imageUriString = backStackEntry.savedStateHandle.get<String>("capturedImageUri")
                                val imageUri = if (imageUriString != null) android.net.Uri.parse(imageUriString) else null

                                com.example.konekumkm.view.ui.umkm.AddUmkmScreen(navController, imageUri)
                            }
                            composable("camera_capture") {
                                com.example.konekumkm.view.ui.camera.CameraScreen(
                                    navController = navController,
                                    onImageCaptured = { uri ->
                                        // Saat foto didapat, simpan URI ke state navigasi sebelumnya (AddUmkm)
                                        navController.previousBackStackEntry
                                            ?.savedStateHandle
                                            ?.set("capturedImageUri", uri.toString())

                                        // Kembali ke form
                                        navController.popBackStack()
                                    }
                                )
                            }
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