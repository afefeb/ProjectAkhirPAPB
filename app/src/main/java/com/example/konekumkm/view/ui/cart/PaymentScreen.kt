package com.example.konekumkm.view.ui.cart

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    navController: NavController,
    paymentMethod: String,
    cartViewModel: CartViewModel = viewModel()
) {
    val selectedItems by cartViewModel.selectedItems.collectAsState()
    
    val totalPrice = remember(selectedItems) {
        selectedItems.sumOf { it.productPrice * it.quantity }
    }
    
    val scope = rememberCoroutineScope()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pembayaran") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F5F5)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Metode Pembayaran",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = paymentMethod,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Ringkasan Pesanan",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        selectedItems.forEach { item ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${item.productName} x${item.quantity}",
                                    fontSize = 14.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
                                        .format(item.productPrice * item.quantity),
                                    fontSize = 14.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Total Pembayaran",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
                                    .format(totalPrice),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
            
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Instruksi Pembayaran",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = getPaymentInstructions(paymentMethod),
                            fontSize = 13.sp,
                            lineHeight = 20.sp
                        )
                    }
                }
            }
            
            item {
                Button(
                    onClick = {
                        scope.launch {
                            val orderId = cartViewModel.createOrder(paymentMethod)
                            navController.navigate(Screen.OrderSuccess.createRoute(orderId)) {
                                popUpTo(Screen.Cart.route) { inclusive = true }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = "Saya Sudah Bayar",
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}

fun getPaymentInstructions(paymentMethod: String): String {
    return when {
        paymentMethod.contains("BCA") -> "1. Transfer ke rekening BCA: 1234567890\n2. Atas nama: UMKM Connect\n3. Nominal: sesuai total pembayaran\n4. Kirim bukti transfer via WhatsApp"
        paymentMethod.contains("Mandiri") -> "1. Transfer ke rekening Mandiri: 9876543210\n2. Atas nama: UMKM Connect\n3. Nominal: sesuai total pembayaran\n4. Kirim bukti transfer via WhatsApp"
        paymentMethod.contains("GoPay") -> "1. Buka aplikasi Gojek\n2. Pilih GoPay\n3. Transfer ke 081234567890\n4. Nominal: sesuai total pembayaran"
        paymentMethod.contains("OVO") -> "1. Buka aplikasi OVO\n2. Pilih Transfer\n3. Transfer ke 081234567890\n4. Nominal: sesuai total pembayaran"
        paymentMethod.contains("COD") -> "1. Siapkan uang tunai sesuai total pembayaran\n2. Bayar saat barang diterima\n3. Pastikan uang pas atau siapkan kembalian"
        else -> "Silakan lakukan pembayaran sesuai metode yang dipilih dan konfirmasi pembayaran Anda."
    }
}
