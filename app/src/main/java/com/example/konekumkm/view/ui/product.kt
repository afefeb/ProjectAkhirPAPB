package com.example.konekumkm.view.ui.product

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.konekumkm.view.components.ProductCard
import com.example.konekumkm.view.viewmodel.ProductViewModel

@Composable
fun ProductListScreen(
    navController: NavController,
    viewModel: ProductViewModel = viewModel()
) {
    val products by viewModel.allProducts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchAllProducts()
    }

    // Tidak perlu Scaffold dengan TopBar karena sudah ada di HomeActivity
    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (products.isEmpty()) {
            Text("Belum ada produk tersedia.", modifier = Modifier.align(Alignment.Center))
        } else {
            // Tampilan GRID 2 Kolom
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(products) { produk ->
                    ProductCard(produk = produk) {
                        // Nanti bisa diarahkan ke Detail Produk (Langkah Next)
                    }
                }
            }
        }
    }
}