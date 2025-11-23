package com.example.konekumkm.view.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage // <-- Import Wajib
import com.example.konekumkm.data.local.entity.UMKM
import com.example.konekumkm.R // Pastikan R terimport untuk placeholder error (opsional)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UMKMListItem(
    umkm: UMKM,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            // --- BAGIAN FOTO UMKM ---
            AsyncImage(
                model = umkm.imageUrl, // Mengambil URL dari Entity UMKM
                contentDescription = "Foto ${umkm.name}",
                modifier = Modifier
                    .size(80.dp) // Ukuran kotak foto
                    .clip(RoundedCornerShape(8.dp)), // Sudut melengkung
                contentScale = ContentScale.Crop, // Potong gambar biar pas (cover)
                // (Opsional) Gambar pengganti jika loading/error
                // placeholder = painterResource(R.drawable.ic_launcher_foreground),
                // error = painterResource(R.drawable.ic_launcher_foreground)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // --- BAGIAN TEKS ---
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = umkm.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = umkm.category,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = umkm.address,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2
                )
            }
        }
    }
}