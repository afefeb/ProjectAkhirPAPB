package com.example.konekumkm.view.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.konekumkm.view.viewmodel.DetailUiState
import com.example.konekumkm.view.viewmodel.DetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    umkmId: String,
    navController: NavController,
    viewModel: DetailViewModel = viewModel()
) {
    // Panggil data saat pertama kali dibuka
    LaunchedEffect(umkmId) {
        viewModel.getUMKM(umkmId)
    }

    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail UMKM") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val state = uiState) {
                is DetailUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is DetailUiState.Error -> {
                    Text(text = state.message, modifier = Modifier.align(Alignment.Center))
                }
                is DetailUiState.Success -> {
                    val umkm = state.umkm
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Gambar Utama
                        AsyncImage(
                            model = umkm.imageUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp),
                            contentScale = ContentScale.Crop
                        )

                        Column(modifier = Modifier.padding(16.dp)) {
                            // Kategori Badge
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = umkm.category,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Nama UMKM
                            Text(text = umkm.name, style = MaterialTheme.typography.headlineMedium)

                            // Rating
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFD700))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "${umkm.rating} / 5.0", style = MaterialTheme.typography.titleMedium)
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Alamat
                            Text(text = "Alamat:", style = MaterialTheme.typography.titleSmall)
                            Text(text = umkm.address, style = MaterialTheme.typography.bodyMedium)

                            Spacer(modifier = Modifier.height(16.dp))

                            // Deskripsi
                            Text(text = "Tentang:", style = MaterialTheme.typography.titleSmall)
                            Text(text = umkm.description, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}