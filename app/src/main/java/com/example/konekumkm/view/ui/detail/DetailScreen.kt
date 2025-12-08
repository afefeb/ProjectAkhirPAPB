package com.example.konekumkm.view.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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

// Brand colors from logo
val BrandBlue = Color(0xFF6B9BD1)
val BrandOrange = Color(0xFFFF9066)
val BrandPink = Color(0xFFD77FA1)

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

    LaunchedEffect(addToCartMessage) {
        addToCartMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            cartViewModel.clearAddToCartMessage()
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = BrandBlue,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        containerColor = Color(0xFFF8F9FA)
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            when (val state = uiState) {
                is DetailUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = BrandBlue
                    )
                }
                is DetailUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray
                        )
                    }
                }
                is DetailUiState.Success -> {
                    val umkm = state.umkm
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Hero Image with Gradient Overlay
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(320.dp)
                        ) {
                            AsyncImage(
                                model = umkm.imageUrl,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )

                            // Gradient overlay at bottom
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp)
                                    .align(Alignment.BottomCenter)
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                Color.Black.copy(alpha = 0.6f)
                                            )
                                        )
                                    )
                            )
                        }

                        // Main Content Card
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .offset(y = (-24).dp),
                            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                            color = Color.White,
                            shadowElevation = 8.dp
                        ) {
                            Column(modifier = Modifier.padding(24.dp)) {
                                // Category Badge
                                Surface(
                                    color = BrandOrange.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(20.dp)
                                ) {
                                    Text(
                                        text = umkm.category,
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.SemiBold
                                        ),
                                        color = BrandOrange
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // UMKM Name
                                Text(
                                    text = umkm.name,
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = Color(0xFF2C3E50)
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Rating Card
                                Surface(
                                    color = Color(0xFFFFF8E1),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.Star,
                                            contentDescription = null,
                                            tint = Color(0xFFFFB300),
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "${umkm.rating}",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = Color(0xFFFF8F00)
                                        )
                                        Text(
                                            text = " / 5.0",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color(0xFFFF8F00).copy(alpha = 0.7f)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                // Address Section
                                Surface(
                                    color = Color(0xFFF5F7FA),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Surface(
                                            color = BrandPink.copy(alpha = 0.2f),
                                            shape = CircleShape,
                                            modifier = Modifier.size(40.dp)
                                        ) {
                                            Icon(
                                                Icons.Outlined.LocationOn,
                                                contentDescription = null,
                                                tint = BrandPink,
                                                modifier = Modifier.padding(8.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "Alamat",
                                                style = MaterialTheme.typography.labelLarge.copy(
                                                    fontWeight = FontWeight.SemiBold
                                                ),
                                                color = Color(0xFF2C3E50)
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = umkm.address,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = Color(0xFF5A6C7D),
                                                lineHeight = 20.sp
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                // Description Section
                                Column {
                                    Text(
                                        text = "Tentang UMKM",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = Color(0xFF2C3E50)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = umkm.description,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color(0xFF5A6C7D),
                                        lineHeight = 22.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(32.dp))

                                // Products Section
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Produk yang Dijual",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = Color(0xFF2C3E50)
                                    )
                                    if (products.isNotEmpty()) {
                                        Surface(
                                            color = BrandBlue.copy(alpha = 0.15f),
                                            shape = CircleShape
                                        ) {
                                            Text(
                                                text = "${products.size}",
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                                style = MaterialTheme.typography.labelMedium.copy(
                                                    fontWeight = FontWeight.Bold
                                                ),
                                                color = BrandBlue
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                if (products.isNotEmpty()) {
                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        contentPadding = PaddingValues(vertical = 8.dp)
                                    ) {
                                        items(products) { produk ->
                                            Box(modifier = Modifier.width(170.dp)) {
                                                ProductCard(
                                                    produk = produk,
                                                    onClick = {},
                                                    onAddToCart = {
                                                        cartViewModel.addToCart(
                                                            productId = produk.id,
                                                            productName = produk.name,
                                                            productPrice = produk.price,
                                                            productImage = produk.imageUrl,
                                                            umkmId = produk.umkmId,
                                                            umkmName = umkm.name
                                                        )
                                                    }
                                                )
                                            }
                                        }
                                    }
                                } else {
                                    Surface(
                                        modifier = Modifier.fillMaxWidth(),
                                        color = Color(0xFFF5F7FA),
                                        shape = RoundedCornerShape(16.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(32.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = "📦",
                                                style = MaterialTheme.typography.displayMedium
                                            )
                                            Spacer(modifier = Modifier.height(12.dp))
                                            Text(
                                                text = "Belum ada produk",
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    fontWeight = FontWeight.SemiBold
                                                ),
                                                color = Color(0xFF5A6C7D)
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "Produk akan segera ditambahkan",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color.Gray
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(24.dp))
                            }
                        }
                    }
                }
            }

            // Floating TopBar
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .padding(8.dp)
                            .background(Color.White.copy(alpha = 0.95f), CircleShape)
                            .size(40.dp)
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Kembali",
                            tint = BrandBlue
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                modifier = Modifier.padding(padding)
            )
        }
    }
}