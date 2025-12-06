package com.example.konekumkm.view.ui.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.konekumkm.data.local.entity.CartItem
import com.example.konekumkm.view.navigation.Screen
import com.example.konekumkm.view.viewmodel.CartViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navController: NavController,
    cartViewModel: CartViewModel = viewModel()
) {
    val cartItems by cartViewModel.cartItems.collectAsState()
    val selectedItems by cartViewModel.selectedItems.collectAsState()
    
    val totalPrice = remember(selectedItems) {
        selectedItems.sumOf { it.productPrice * it.quantity }
    }
    val selectedCount = remember(selectedItems) {
        selectedItems.sumOf { it.quantity }
    }
    
    val allSelected = remember(cartItems, selectedItems) {
        cartItems.isNotEmpty() && cartItems.all { it.isSelected }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Keranjang") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        },
        bottomBar = {
            if (selectedItems.isNotEmpty()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White,
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Total ($selectedCount item)",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(totalPrice),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        
                        Button(
                            onClick = {
                                navController.navigate(Screen.PaymentMethod.route)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text("Checkout")
                        }
                    }
                }
            }
        }
    ) { padding ->
        if (cartItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Keranjang kosong",
                        fontSize = 18.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { navController.navigate(Screen.Produk.route) }) {
                        Text("Belanja Sekarang")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color(0xFFF5F5F5)),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Select All Header
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = allSelected,
                                onCheckedChange = { isChecked ->
                                    cartViewModel.toggleSelectAll(isChecked)
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Pilih Semua",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
                
                items(cartItems) { cartItem ->
                    CartItemCard(
                        cartItem = cartItem,
                        onQuantityChange = { newQuantity ->
                            cartViewModel.updateQuantity(cartItem, newQuantity)
                        },
                        onSelectionChange = {
                            cartViewModel.toggleItemSelection(cartItem)
                        },
                        onDelete = {
                            cartViewModel.removeFromCart(cartItem)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CartItemCard(
    cartItem: CartItem,
    onQuantityChange: (Int) -> Unit,
    onSelectionChange: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox
            Checkbox(
                checked = cartItem.isSelected,
                onCheckedChange = { onSelectionChange() }
            )
            
            AsyncImage(
                model = cartItem.productImage.ifEmpty { "https://via.placeholder.com/80" },
                contentDescription = cartItem.productName,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = cartItem.productName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 2
                )
                
                Text(
                    text = cartItem.umkmName,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
                        .format(cartItem.productPrice),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = { onQuantityChange(cartItem.quantity - 1) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Text("-", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                    
                    Text(
                        text = cartItem.quantity.toString(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    IconButton(
                        onClick = { onQuantityChange(cartItem.quantity + 1) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Text("+", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.Red
                )
            }
        }
    }
}
