package com.example.konekumkm.view.ui.order

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.konekumkm.data.local.entity.Order
import com.example.konekumkm.data.local.entity.OrderItem
import com.example.konekumkm.viewmodel.OrderHistoryViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

val BrandBlue = Color(0xFF3498DB)
val BrandPink = Color(0xFFE74C3C)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreen(
    navController: NavController,
    viewModel: OrderHistoryViewModel = viewModel()
) {
    val orders by viewModel.orders.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Riwayat Pesanan",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color(0xFFF8F9FA)
    ) { paddingValues ->
        if (orders.isEmpty()) {
            EmptyOrdersView(Modifier.padding(paddingValues))
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(orders) { order ->
                    OrderCard(order)
                }
            }
        }
    }
}

@Composable
fun EmptyOrdersView(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.ShoppingBag,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = Color.Gray.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Belum Ada Pesanan",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Pesanan Anda akan muncul di sini",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
    }
}

@Composable
fun OrderCard(order: Order) {
    val gson = Gson()
    val itemType = object : TypeToken<List<OrderItem>>() {}.type
    val orderItems: List<OrderItem> = try {
        gson.fromJson(order.items, itemType)
    } catch (e: Exception) {
        emptyList()
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Order Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = formatDate(order.orderDate),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "ID: ${order.id.take(8)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
                StatusBadge(order.status)
            }

            Divider(modifier = Modifier.padding(vertical = 12.dp))

            // Order Items
            orderItems.forEach { item ->
                OrderItemRow(item)
                Spacer(modifier = Modifier.height(8.dp))
            }

            Divider(modifier = Modifier.padding(vertical = 12.dp))

            // Payment Method
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Metode Pembayaran:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Text(
                    text = order.paymentMethod,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF2C3E50)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total Pembayaran:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2C3E50)
                )
                Text(
                    text = formatCurrency(order.totalAmount),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = BrandBlue
                )
            }
        }
    }
}

@Composable
fun OrderItemRow(item: OrderItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.productName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF2C3E50)
            )
            Text(
                text = item.umkmName,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
        Text(
            text = "${item.quantity}x ${formatCurrency(item.productPrice)}",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF2C3E50)
        )
    }
}

@Composable
fun StatusBadge(status: String) {
    val backgroundColor = when (status.lowercase()) {
        "completed" -> Color(0xFF27AE60).copy(alpha = 0.1f)
        "processing" -> Color(0xFFF39C12).copy(alpha = 0.1f)
        "cancelled" -> Color(0xFFE74C3C).copy(alpha = 0.1f)
        else -> Color.Gray.copy(alpha = 0.1f)
    }

    val textColor = when (status.lowercase()) {
        "completed" -> Color(0xFF27AE60)
        "processing" -> Color(0xFFF39C12)
        "cancelled" -> Color(0xFFE74C3C)
        else -> Color.Gray
    }

    val statusText = when (status.lowercase()) {
        "completed" -> "Selesai"
        "processing" -> "Diproses"
        "cancelled" -> "Dibatalkan"
        else -> status
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = backgroundColor
    ) {
        Text(
            text = statusText,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = textColor
        )
    }
}

fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
    return sdf.format(Date(timestamp))
}

fun formatCurrency(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    return format.format(amount)
}
