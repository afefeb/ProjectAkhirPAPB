package com.example.konekumkm.view.ui.home

import android.os.Bundle
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
import androidx.compose.material.icons.Icons
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
import androidx.compose.runtime.Composable

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
                val umkmList by viewModel.umkmList.collectAsState()
                val isLoading by viewModel.isLoading.collectAsState()

                // State untuk Drawer (Menu Samping)
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()

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
                            // --- HEADER DRAWER ---
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "UMKMConnect",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Platform Ekonomi Lokal",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            HorizontalDivider()

                            // --- MENU ITEMS ---
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

                            // Spacer agar tombol Login turun ke bawah
                            Spacer(modifier = Modifier.weight(1f))

                            // --- TOMBOL LOGIN ---
                            Button(
                                onClick = { navigateTo(Screen.Login.route) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Icon(Icons.Default.AccountCircle, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Login / Masuk")
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
                            composable(Screen.Login.route) { PlaceholderScreen("Halaman Login") }
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