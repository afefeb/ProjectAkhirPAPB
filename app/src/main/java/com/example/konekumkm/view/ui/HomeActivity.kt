package com.example.konekumkm.view.ui.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.konekumkm.ui.theme.KonekumkmTheme // Sesuaikan nama tema Anda
import com.example.konekumkm.view.components.UMKMListItem
import com.example.konekumkm.view.navigation.Screen
import com.example.konekumkm.view.viewmodel.HomeViewModel

// Import Screen map & add umkm jika sudah dibuat filenya nanti

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KonekumkmTheme {
                val navController = rememberNavController()
                val viewModel: HomeViewModel = viewModel() // Panggil ViewModel
                val umkmList by viewModel.umkmList.collectAsState()

                Scaffold(
                    floatingActionButton = {
                        // Tombol cepat ke Peta & Tambah
                        Column {
                            FloatingActionButton(onClick = { navController.navigate(Screen.Map.route) }) {
                                Icon(Icons.Default.LocationOn, contentDescription = "Peta")
                            }
                        }
                    }
                ) { innerPadding ->
                    // Navigasi Utama
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Home.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        // Halaman HOME (List UMKM)
                        composable(Screen.Home.route) {
                            LazyColumn {
                                items(umkmList) { umkm ->
                                    UMKMListItem(umkm = umkm) {
                                        // Cukup panggil umkm.id langsung
                                        navController.navigate(Screen.Detail.createRoute(umkm.id))
                                    }
                                }
                            }
                        }

                        // Placeholder Halaman Peta (Nanti kita isi)
                        composable(Screen.Home.route) {
                            // Ambil state loading dari ViewModel
                            val isLoading by viewModel.isLoading.collectAsState()

                            if (isLoading) {
                                // TAMPILKAN LOADING (Spinner)
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator()
                                }
                            } else if (umkmList.isEmpty()) {
                                // TAMPILKAN PESAN KOSONG jika data tidak ada
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text("Tidak ada data UMKM ditemukan :(")
                                }
                            } else {
                                // TAMPILKAN LIST DATA
                                LazyColumn {
                                    items(umkmList) { umkm ->
                                        UMKMListItem(umkm = umkm) {
                                            navController.navigate(Screen.Detail.createRoute(umkm.id))
                                        }
                                    }
                                }
                            }
                        }

                        // Placeholder Halaman Detail
                        composable(
                            route = "detail/{umkmId}"
                            // (Ini sama dengan Screen.Detail.route kalau Anda definisikan 'detail/{umkmId}' di Screen.kt)
                        ) { backStackEntry ->
                            // Ambil ID yang dikirim dari List
                            val umkmId = backStackEntry.arguments?.getString("umkmId") ?: ""

                            // Tampilkan Screen Detail
                            com.example.konekumkm.view.ui.detail.DetailScreen(
                                umkmId = umkmId,
                                navController = navController
                            )
                        }
                    }
                }
            }
        }
    }
}