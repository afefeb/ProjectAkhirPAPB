package com.example.konekumkm.view.ui.umkm

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.konekumkm.view.viewmodel.AddState
import com.example.konekumkm.view.viewmodel.AddUmkmViewModel
import com.google.android.gms.location.LocationServices

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUmkmScreen(
    navController: NavController,
    capturedImageUri: Uri? = null,
    viewModel: AddUmkmViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    // Form State
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Kuliner") }
    var address by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(capturedImageUri) }
    var latitude by remember { mutableStateOf(0.0) }
    var longitude by remember { mutableStateOf(0.0) }

    LaunchedEffect(capturedImageUri) {
        if (capturedImageUri != null) imageUri = capturedImageUri
    }

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    // --- PERBAIKAN DI SINI ---
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Cek ulang izin secara eksplisit untuk memuaskan Linter Android
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
            ) {
                fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                    if (loc != null) {
                        latitude = loc.latitude
                        longitude = loc.longitude
                        Toast.makeText(context, "Lokasi ditemukan: ${loc.latitude}, ${loc.longitude}", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Lokasi GPS tidak terdeteksi, coba nyalakan GPS", Toast.LENGTH_LONG).show()
                    }
                }
            }
        } else {
            Toast.makeText(context, "Izin lokasi ditolak", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(uiState) {
        if (uiState is AddState.Success) {
            Toast.makeText(context, "UMKM Berhasil Didaftarkan!", Toast.LENGTH_LONG).show()
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daftar UMKM Baru") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // --- BAGIAN FOTO ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.LightGray)
                    .clickable {
                        navController.navigate("camera_capture")
                    },
                contentAlignment = Alignment.Center
            ) {
                if (imageUri != null) {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text("Sentuh untuk Ambil Foto (Kamera)")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Form Input
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nama UMKM") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Kategori (Kuliner, Jasa, dll)") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Alamat Lengkap") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Deskripsi Singkat") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
            Spacer(modifier = Modifier.height(16.dp))

            // Tombol Ambil Lokasi GPS
            Button(
                onClick = { locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Place, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (latitude == 0.0) "Ambil Lokasi Saya (GPS)" else "Lokasi Terkunci: $latitude, $longitude")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Tombol Submit
            Button(
                onClick = {
                    viewModel.submitUMKM(name, category, address, description, imageUri, latitude, longitude)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState !is AddState.Loading
            ) {
                if (uiState is AddState.Loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Daftarkan UMKM")
                }
            }
        }
    }
}