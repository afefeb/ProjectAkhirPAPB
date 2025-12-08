package com.example.konekumkm.view.ui.help

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

val BrandBlue = Color(0xFF3498DB)
val BrandPink = Color(0xFFE74C3C)

data class HelpItem(
    val icon: ImageVector,
    val title: String,
    val description: String,
    val expanded: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(navController: NavController) {
    var helpItems by remember {
        mutableStateOf(
            listOf(
                HelpItem(
                    icon = Icons.Default.ShoppingCart,
                    title = "Cara Berbelanja",
                    description = "1. Jelajahi produk dari berbagai UMKM lokal\n2. Pilih produk yang diinginkan\n3. Tambahkan ke keranjang (Login diperlukan)\n4. Checkout dan pilih metode pembayaran\n5. Selesaikan pembayaran"
                ),
                HelpItem(
                    icon = Icons.Default.AccountCircle,
                    title = "Cara Membuat Akun",
                    description = "1. Klik tombol 'Daftar' di halaman profil\n2. Isi email dan password yang valid\n3. Klik 'Register' untuk membuat akun\n4. Login dengan akun yang sudah dibuat"
                ),
                HelpItem(
                    icon = Icons.Default.Store,
                    title = "Cara Mendaftar UMKM",
                    description = "1. Login ke akun Anda terlebih dahulu\n2. Buka menu 'Gabung' di bottom navigation\n3. Isi form pendaftaran UMKM:\n   - Nama UMKM\n   - Kategori usaha\n   - Deskripsi UMKM\n   - Lokasi (pilih dari peta)\n   - Foto UMKM\n4. Klik 'Daftar UMKM' untuk submit"
                ),
                HelpItem(
                    icon = Icons.Default.LocationOn,
                    title = "Cara Menggunakan Peta",
                    description = "1. Buka menu 'Map' di bottom navigation\n2. Peta akan menampilkan lokasi UMKM terdekat\n3. Klik marker untuk melihat detail UMKM\n4. Zoom in/out untuk melihat area lebih detail"
                ),
                HelpItem(
                    icon = Icons.Default.FavoriteBorder,
                    title = "Fitur Favorit",
                    description = "Fitur favorit memungkinkan Anda menyimpan UMKM dan produk kesukaan untuk akses cepat di kemudian hari. (Segera hadir)"
                ),
                HelpItem(
                    icon = Icons.Default.ShoppingBag,
                    title = "Riwayat Pesanan",
                    description = "1. Login ke akun Anda\n2. Buka menu Profil\n3. Pilih 'Pesanan Saya'\n4. Lihat semua riwayat pesanan dengan detail:\n   - Produk yang dibeli\n   - Total pembayaran\n   - Metode pembayaran\n   - Status pesanan"
                ),
                HelpItem(
                    icon = Icons.Default.Search,
                    title = "Cara Mencari Produk",
                    description = "1. Gunakan fitur Search di halaman Home\n2. Ketik nama produk atau UMKM yang dicari\n3. Hasil pencarian akan ditampilkan secara realtime\n4. Klik produk untuk melihat detail"
                ),
                HelpItem(
                    icon = Icons.Default.Info,
                    title = "Mode Tamu vs Login",
                    description = "Mode Tamu:\n- Jelajahi UMKM dan produk\n- Lihat detail produk\n- Tidak bisa menambah ke keranjang\n- Tidak bisa checkout\n\nMode Login:\n- Semua fitur mode tamu\n- Tambah produk ke keranjang\n- Checkout dan bayar\n- Lihat riwayat pesanan\n- Daftar UMKM"
                ),
                HelpItem(
                    icon = Icons.Default.Payment,
                    title = "Metode Pembayaran",
                    description = "Aplikasi mendukung berbagai metode pembayaran:\n- Transfer Bank\n- E-Wallet (GoPay, OVO, Dana)\n- COD (Cash on Delivery)\n\nPilih metode yang paling sesuai saat checkout."
                ),
                HelpItem(
                    icon = Icons.Default.Phone,
                    title = "Hubungi Kami",
                    description = "Butuh bantuan lebih lanjut?\n\nEmail: support@konekumkm.com\nWhatsApp: +62 812-3456-7890\nJam Operasional: 08.00 - 17.00 WIB\n\nTim kami siap membantu Anda!"
                )
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Bantuan",
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = BrandBlue
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Help,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Pusat Bantuan",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Temukan jawaban untuk pertanyaan Anda",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }

            // Help Items
            items(helpItems) { item ->
                HelpCard(
                    item = item,
                    isExpanded = item.expanded,
                    onExpandToggle = {
                        helpItems = helpItems.map {
                            if (it.title == item.title) {
                                it.copy(expanded = !it.expanded)
                            } else {
                                it
                            }
                        }
                    }
                )
            }

            // Footer
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Masih butuh bantuan? Hubungi tim support kami",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
fun HelpCard(
    item: HelpItem,
    isExpanded: Boolean,
    onExpandToggle: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = BrandBlue.copy(alpha = 0.1f),
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                        tint = BrandBlue,
                        modifier = Modifier.padding(12.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF2C3E50),
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = onExpandToggle) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "Tutup" else "Buka",
                        tint = BrandBlue
                    )
                }
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = Color.LightGray.copy(alpha = 0.3f))
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    lineHeight = MaterialTheme.typography.bodyMedium.lineHeight.times(1.5f)
                )
            }
        }
    }
}
