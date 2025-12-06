package com.example.konekumkm.view.ui.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.konekumkm.view.components.ProductCard
import com.example.konekumkm.view.components.UMKMListItem
import com.example.konekumkm.view.navigation.Screen
import com.example.konekumkm.view.viewmodel.CartViewModel
import com.example.konekumkm.view.viewmodel.HomeViewModel
import com.example.konekumkm.view.viewmodel.ProductViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = viewModel(),
    productViewModel: ProductViewModel = viewModel(),
    cartViewModel: CartViewModel = viewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    val umkmList by homeViewModel.umkmList.collectAsState()
    val products by productViewModel.allProducts.collectAsState()
    
    val filteredUmkm = remember(searchQuery, umkmList) {
        if (searchQuery.isBlank()) {
            emptyList()
        } else {
            umkmList.filter { umkm ->
                umkm.name.contains(searchQuery, ignoreCase = true) ||
                umkm.category.contains(searchQuery, ignoreCase = true) ||
                umkm.address.contains(searchQuery, ignoreCase = true)
            }
        }
    }
    
    val filteredProducts = remember(searchQuery, products) {
        if (searchQuery.isBlank()) {
            emptyList()
        } else {
            products.filter { product ->
                product.name.contains(searchQuery, ignoreCase = true) ||
                product.category.contains(searchQuery, ignoreCase = true)
            }
        }
    }
    
    LaunchedEffect(Unit) {
        productViewModel.fetchAllProducts()
    }
    
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
                title = { Text("Pencarian") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Cari UMKM atau Produk...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.Gray
                ),
                singleLine = true
            )
            
            if (searchQuery.isBlank()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Cari UMKM atau Produk",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    }
                }
            } else if (filteredUmkm.isEmpty() && filteredProducts.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Tidak ada hasil untuk \"$searchQuery\"",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (filteredUmkm.isNotEmpty()) {
                        item {
                            Text(
                                text = "UMKM (${filteredUmkm.size})",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                        items(filteredUmkm) { umkm ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                UMKMListItem(umkm = umkm) {
                                    navController.navigate(Screen.Detail.createRoute(umkm.id))
                                }
                            }
                        }
                        item { Spacer(modifier = Modifier.height(16.dp)) }
                    }
                    
                    if (filteredProducts.isNotEmpty()) {
                        item {
                            Text(
                                text = "Produk (${filteredProducts.size})",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                        items(filteredProducts.chunked(2)) { rowProducts ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                rowProducts.forEach { produk ->
                                    Box(modifier = Modifier.weight(1f)) {
                                        ProductCard(
                                            produk = produk,
                                            onClick = {
                                                // TODO: Navigate to product detail
                                            },
                                            onAddToCart = {
                                                umkmList.find { it.id == produk.umkmId }?.let { umkm ->
                                                    cartViewModel.addToCart(
                                                        productId = produk.id,
                                                        productName = produk.name,
                                                        productPrice = produk.price,
                                                        productImage = produk.imageUrl,
                                                        umkmId = produk.umkmId,
                                                        umkmName = umkm.name
                                                    )
                                                }
                                            }
                                        )
                                    }
                                }
                                if (rowProducts.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
