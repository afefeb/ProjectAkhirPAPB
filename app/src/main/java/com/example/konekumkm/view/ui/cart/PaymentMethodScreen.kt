package com.example.konekumkm.view.ui.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
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
import com.example.konekumkm.view.viewmodel.CartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentMethodScreen(
    navController: NavController,
    cartViewModel: CartViewModel = viewModel()
) {
    var selectedPaymentMethod by remember { mutableStateOf<String?>(null) }
    
    val paymentMethods = listOf(
        "Transfer Bank BCA",
        "Transfer Bank Mandiri",
        "Transfer Bank BRI",
        "Transfer Bank BNI",
        "E-Wallet (GoPay)",
        "E-Wallet (OVO)",
        "E-Wallet (Dana)",
        "COD (Cash on Delivery)"
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pilih Metode Pembayaran") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Button(
                    onClick = {
                        selectedPaymentMethod?.let { method ->
                            navController.navigate(Screen.Payment.createRoute(method))
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    enabled = selectedPaymentMethod != null,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Lanjutkan Pembayaran")
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F5F5)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(paymentMethods) { method ->
                PaymentMethodItem(
                    method = method,
                    isSelected = selectedPaymentMethod == method,
                    onClick = { selectedPaymentMethod = method }
                )
            }
        }
    }
}

@Composable
fun PaymentMethodItem(
    method: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.White
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = method,
                fontSize = 16.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
            
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
