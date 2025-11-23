package com.example.konekumkm.view.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage // Library Coil untuk load gambar URL
import com.example.konekumkm.data.local.entity.UMKM

@Composable
fun UMKMListItem(umkm: UMKM, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(modifier = Modifier.padding(8.dp)) {
            // Gambar UMKM dari URL (Firebase Storage / Link)
            AsyncImage(
                model = umkm.imageUrl,
                contentDescription = "Foto ${umkm.name}",
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(text = umkm.name, style = MaterialTheme.typography.titleMedium)
                Text(text = umkm.category, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                Text(text = umkm.address, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}