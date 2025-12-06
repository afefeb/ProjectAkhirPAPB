package com.example.konekumkm.view.ui.product

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
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
import com.example.konekumkm.view.navigation.Screen
import com.example.konekumkm.view.components.ProductCard
import com.example.konekumkm.view.viewmodel.CartViewModel
import com.example.konekumkm.view.viewmodel.HomeViewModel
import com.example.konekumkm.view.viewmodel.ProductViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    navController: NavController,
    viewModel: ProductViewModel = viewModel(),
    cartViewModel: CartViewModel = viewModel(),
    homeViewModel: HomeViewModel = viewModel()
) {
    val products by viewModel.allProducts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val cartItemCount by cartViewModel.cartItemCount.collectAsState()
    
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Semua") }
    var currentPage by remember { mutableStateOf(1) }
    
    val categories = listOf("Semua", "Kuliner", "Fashion", "Kerajinan", "Kesehatan", "Retail")
    
    val itemsPerPage = 8
    
    LaunchedEffect(Unit) {
        viewModel.fetchAllProducts()
    }
    
    val filteredProducts = remember(products, searchQuery, selectedCategory) {
        products.filter { product ->
            val matchesSearch = product.name.contains(searchQuery, ignoreCase = true) ||
                              product.category.contains(searchQuery, ignoreCase = true)
            val matchesCategory = selectedCategory == "Semua" || 
                                 product.category.equals(selectedCategory, ignoreCase = true)
            matchesSearch && matchesCategory
        }
    }
    
    val totalPages = kotlin.math.ceil(filteredProducts.size.toDouble() / itemsPerPage).toInt()
    val paginatedProducts = remember(filteredProducts, currentPage) {
        val startIndex = (currentPage - 1) * itemsPerPage
        val endIndex = minOf(startIndex + itemsPerPage, filteredProducts.size)
        if (startIndex < filteredProducts.size) {
            filteredProducts.subList(startIndex, endIndex)
        } else {
            emptyList()
        }
    }
    
    LaunchedEffect(searchQuery, selectedCategory) {
        currentPage = 1
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
        topBar = {
            TopAppBar(
                title = { Text("Produk") },
                actions = {
                    // Cart Icon with Badge
                    BadgedBox(
                        badge = {
                            if (cartItemCount > 0) {
                                Badge {
                                    Text(cartItemCount.toString())
                                }
                            }
                        }
                    ) {
                        IconButton(onClick = { navController.navigate(Screen.Cart.route) }) {
                            Icon(
                                Icons.Default.ShoppingCart,
                                contentDescription = "Cart"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
        ) {
            OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            placeholder = { Text("Cari produk...") },
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
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = Color.Gray
            ),
            singleLine = true
        )
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { selectedCategory = category },
                    label = { Text(category) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }
        
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (filteredProducts.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = if (searchQuery.isNotEmpty() || selectedCategory != "Semua") {
                        "Tidak ada produk ditemukan"
                    } else {
                        "Belum ada produk tersedia"
                    },
                    color = Color.Gray
                )
            }
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                // Grid Produk (2 kolom, max 4 baris)
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    items(paginatedProducts.chunked(2)) { rowProducts ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            rowProducts.forEach { produk ->
                                Box(modifier = Modifier.weight(1f)) {
                                    ProductCard(
                                        produk = produk,
                                        onClick = {
                                        },
                                        onAddToCart = {
                                            homeViewModel.umkmList.value.find { it.id == produk.umkmId }?.let { umkm ->
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
                
                if (totalPages > 1) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = { if (currentPage > 1) currentPage-- },
                                enabled = currentPage > 1,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text("Prev")
                            }
                            
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val visiblePages = when {
                                    totalPages <= 5 -> (1..totalPages).toList()
                                    currentPage <= 3 -> (1..5).toList()
                                    currentPage >= totalPages - 2 -> (totalPages - 4..totalPages).toList()
                                    else -> (currentPage - 2..currentPage + 2).toList()
                                }
                                
                                visiblePages.forEach { page ->
                                    if (page == currentPage) {
                                        Button(
                                            onClick = { },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.primary
                                            ),
                                            modifier = Modifier.size(40.dp),
                                            contentPadding = PaddingValues(0.dp)
                                        ) {
                                            Text(
                                                text = page.toString(),
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    } else {
                                        OutlinedButton(
                                            onClick = { currentPage = page },
                                            modifier = Modifier.size(40.dp),
                                            contentPadding = PaddingValues(0.dp)
                                        ) {
                                            Text(page.toString())
                                        }
                                    }
                                }
                            }
                            
                            Button(
                                onClick = { if (currentPage < totalPages) currentPage++ },
                                enabled = currentPage < totalPages,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text("Next")
                            }
                        }
                        
                        Text(
                            text = "Halaman $currentPage dari $totalPages (${filteredProducts.size} produk)",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }
        }
    }
}