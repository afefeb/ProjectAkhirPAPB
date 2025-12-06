package com.example.konekumkm.view.ui.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.konekumkm.R
import com.example.konekumkm.view.components.ProductCard
import com.example.konekumkm.view.components.UMKMListItem
import com.example.konekumkm.view.navigation.Screen
import com.example.konekumkm.view.viewmodel.AuthState
import com.example.konekumkm.view.viewmodel.AuthViewModel
import com.example.konekumkm.view.viewmodel.CartViewModel
import com.example.konekumkm.view.viewmodel.HomeViewModel
import com.example.konekumkm.view.viewmodel.ProductViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel(),
    productViewModel: ProductViewModel = viewModel(),
    cartViewModel: CartViewModel = viewModel()
) {
    val umkmList by homeViewModel.umkmList.collectAsState()
    val isLoading by homeViewModel.isLoading.collectAsState()
    val authState by authViewModel.authState.collectAsState()
    val products by productViewModel.allProducts.collectAsState()
    
    // Load data
    LaunchedEffect(Unit) {
        productViewModel.fetchAllProducts()
    }

    // Banner carousel state
    val bannerImages = listOf(
        R.drawable.car_1,
        R.drawable.car_2,
        R.drawable.car_3
    )
    val pagerState = rememberPagerState(pageCount = { bannerImages.size })
    
    // Auto scroll banner
    LaunchedEffect(pagerState) {
        while (true) {
            delay(3000)
            val nextPage = (pagerState.currentPage + 1) % bannerImages.size
            pagerState.animateScrollToPage(nextPage)
        }
    }
    
    // Snackbar for add to cart notification
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
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {
        // Header Sambutan
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary,
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        val userName = when (val state = authState) {
                            is AuthState.Success -> state.user.name
                            else -> "Guest"
                        }
                        
                        Text(
                            text = "Halo ! $userName",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Ayo Jelajah UMKM di sekitarmu",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = { navController.navigate(Screen.Cart.route) }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Keranjang",
                                tint = Color.White
                            )
                        }
                        IconButton(
                            onClick = { /* TODO: Navigate to profile */ }
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Profile",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }

        // Search Bar (Fake)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .clickable {
                        navController.navigate(Screen.Search.route)
                    },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Cari UMKM / Produk",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            }
        }

        // Banner Carousel
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .height(160.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        Image(
                            painter = painterResource(id = bannerImages[page]),
                            contentDescription = "Banner ${page + 1}",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    
                    // Indicator dots
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        repeat(bannerImages.size) { index ->
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (pagerState.currentPage == index) Color.White
                                        else Color.White.copy(alpha = 0.5f)
                                    )
                            )
                        }
                    }
                }
            }
        }

        // Rekomendasi UMKM
        item {
            Column(
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Rekomendasi UMKM",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Saran rekomendasi UMKM untukmu!",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                    TextButton(
                        onClick = { navController.navigate(Screen.Map.route) }
                    ) {
                        Text("Lihat UMKM lainnya", fontSize = 12.sp)
                    }
                }
                
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (umkmList.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Belum ada UMKM", color = Color.Gray)
                    }
                } else {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(umkmList.take(5)) { umkm ->
                            Card(
                                modifier = Modifier
                                    .width(280.dp)
                                    .clickable {
                                        navController.navigate(Screen.Detail.createRoute(umkm.id))
                                    },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                UMKMListItem(umkm = umkm) {
                                    navController.navigate(Screen.Detail.createRoute(umkm.id))
                                }
                            }
                        }
                    }
                }
            }
        }

        // Rekomendasi Produk
        item {
            Column(
                modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Rekomendasi Produk",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Saran rekomendasi produk dari UMKM terbaik!",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                    TextButton(
                        onClick = { navController.navigate(Screen.Produk.route) }
                    ) {
                        Text("Temukan Produk lainnya", fontSize = 12.sp)
                    }
                }
            }
        }
        
        // Grid Produk (only show 4 products as recommendation)
        if (products.isNotEmpty()) {
            items(products.take(4).chunked(2)) { rowProducts ->
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
                    // Fill empty space if odd number
                    if (rowProducts.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        } else {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Belum ada produk", color = Color.Gray)
                }
            }
        }
        }
    }
}
