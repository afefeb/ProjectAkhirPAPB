package com.example.konekumkm.view.ui.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.konekumkm.view.components.ProductCard
import com.example.konekumkm.view.viewmodel.CartViewModel
import com.example.konekumkm.view.viewmodel.DetailUiState
import com.example.konekumkm.view.viewmodel.DetailViewModel
import com.example.konekumkm.view.viewmodel.ProductViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    umkmId: String,
    navController: NavController,
    viewModel: DetailViewModel = viewModel(),
    productViewModel: ProductViewModel = viewModel(),
    cartViewModel: CartViewModel = viewModel()
) {
    LaunchedEffect(umkmId) {
        viewModel.getUMKM(umkmId)
        productViewModel.fetchProductsByUmkm(umkmId)
    }

    val uiState by viewModel.uiState.collectAsState()
    val products by productViewModel.umkmProducts.collectAsState()
    
    val snackbarHostState = remember { SnackbarHostState() }
    val addToCartMessage by cartViewModel.addToCartMessage.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    
    LaunchedEffect(addToCartMessage) {
        addToCartMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            cartViewModel.clearAddToCartMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Detail UMKM") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val state = uiState) {
                is DetailUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is DetailUiState.Error -> {
                    Text(text = state.message, modifier = Modifier.align(Alignment.Center))
                }
                is DetailUiState.Success -> {
                    val umkm = state.umkm
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        AsyncImage(
                            model = umkm.imageUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp),
                            contentScale = ContentScale.Crop
                        )

                        Column(modifier = Modifier.padding(16.dp)) {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = umkm.category,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(text = umkm.name, style = MaterialTheme.typography.headlineMedium)

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFD700))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "${umkm.rating} / 5.0", style = MaterialTheme.typography.titleMedium)
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(text = "Alamat:", style = MaterialTheme.typography.titleSmall)
                            Text(text = umkm.address, style = MaterialTheme.typography.bodyMedium)

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(text = "Tentang:", style = MaterialTheme.typography.titleSmall)
                            Text(text = umkm.description, style = MaterialTheme.typography.bodyMedium)

                            Spacer(modifier = Modifier.height(24.dp))

                            if (products.isNotEmpty()) {
                                Text(
                                    text = "Produk yang Dijual",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    items(products) { produk ->
                                        Box(modifier = Modifier.width(160.dp)) {
                                            ProductCard(
                                                produk = produk,
                                                onClick = {
                                                },
                                                onAddToCart = {
                                                    when (val state = uiState) {
                                                        is DetailUiState.Success -> {
                                                            cartViewModel.addToCart(
                                                                productId = produk.id,
                                                                productName = produk.name,
                                                                productPrice = produk.price,
                                                                productImage = produk.imageUrl,
                                                                umkmId = produk.umkmId,
                                                                umkmName = state.umkm.name
                                                            )
                                                        }
                                                        else -> {}
                                                    }
                                                }
                                            )
                                        }
                                    }
                                }
                            } else {
                                Text(
                                    text = "Produk yang Dijual",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Belum ada produk yang terdaftar",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}