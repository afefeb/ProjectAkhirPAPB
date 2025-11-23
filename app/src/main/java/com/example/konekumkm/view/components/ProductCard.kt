package com.example.konekumkm.view.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.konekumkm.data.local.entity.Produk
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ProductCard(produk: Produk, onClick: () -> Unit) {
    // Format Rupiah
    val formatRp = NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(produk.price)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            // Gambar Produk
            AsyncImage(
                model = produk.imageUrl,
                contentDescription = produk.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp) // Tinggi gambar
                    .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
                contentScale = ContentScale.Crop
            )

            // Info Produk
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = produk.name,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatRp,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}