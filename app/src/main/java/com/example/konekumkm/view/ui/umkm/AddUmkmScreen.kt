package com.example.konekumkm.view.ui.umkm

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.konekumkm.view.viewmodel.AddUmkmViewModel.AddState
import com.example.konekumkm.view.viewmodel.AddUmkmViewModel
import com.google.android.gms.location.LocationServices

// Brand colors
val BrandBlue = Color(0xFF6B9BD1)
val BrandOrange = Color(0xFFFF9066)
val BrandPink = Color(0xFFD77FA1)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUmkmScreen(
    navController: NavController,
    viewModel: AddUmkmViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    // Form State
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Kuliner") }
    var address by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var latitude by remember { mutableStateOf(0.0) }
    var longitude by remember { mutableStateOf(0.0) }
    var showImagePickerDialog by remember { mutableStateOf(false) }
    var showCategoryDialog by remember { mutableStateOf(false) }

    val categories = listOf("Kuliner", "Fashion", "Kerajinan", "Jasa", "Pertanian", "Teknologi", "Lainnya")

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    // Gallery Picker
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { imageUri = it }
    }

    // Camera Launcher (using default camera)
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (!success) {
            imageUri = null
            Toast.makeText(context, "Gagal mengambil foto", Toast.LENGTH_SHORT).show()
        }
    }

    // Camera Permission
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Create temp file for camera
            val uri = androidx.core.content.FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                java.io.File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg").apply {
                    createNewFile()
                }
            )
            imageUri = uri
            cameraLauncher.launch(uri)
        } else {
            Toast.makeText(context, "Izin kamera ditolak", Toast.LENGTH_SHORT).show()
        }
    }

    // Location Permission
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
            ) {
                fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                    if (loc != null) {
                        latitude = loc.latitude
                        longitude = loc.longitude
                        Toast.makeText(context, "Lokasi berhasil diambil", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Lokasi tidak terdeteksi, aktifkan GPS", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            Toast.makeText(context, "Izin lokasi ditolak", Toast.LENGTH_SHORT).show()
        }
    }

    // Image Picker Dialog
    if (showImagePickerDialog) {
        AlertDialog(
            onDismissRequest = { showImagePickerDialog = false },
            title = { Text("Pilih Sumber Gambar") },
            text = {
                Column {
                    ListItem(
                        headlineContent = { Text("Kamera") },
                        leadingContent = {
                            Icon(Icons.Outlined.CameraAlt, null, tint = BrandBlue)
                        },
                        modifier = Modifier.clickable {
                            showImagePickerDialog = false
                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    )
                    ListItem(
                        headlineContent = { Text("Galeri") },
                        leadingContent = {
                            Icon(Icons.Outlined.Photo, null, tint = BrandOrange)
                        },
                        modifier = Modifier.clickable {
                            showImagePickerDialog = false
                            galleryLauncher.launch("image/*")
                        }
                    )
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showImagePickerDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    // Category Picker Dialog
    if (showCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showCategoryDialog = false },
            title = { Text("Pilih Kategori") },
            text = {
                Column {
                    categories.forEach { cat ->
                        ListItem(
                            headlineContent = { Text(cat) },
                            leadingContent = {
                                RadioButton(
                                    selected = category == cat,
                                    onClick = null
                                )
                            },
                            modifier = Modifier.clickable {
                                category = cat
                                showCategoryDialog = false
                            }
                        )
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showCategoryDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is AddState.Success -> {
                Toast.makeText(context, "UMKM Berhasil Didaftarkan!", Toast.LENGTH_LONG).show()
                navController.popBackStack()
            }
            is AddState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Daftar UMKM Baru",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2C3E50)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = BrandBlue)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color(0xFFF8F9FA)
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Image Upload Section
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Foto UMKM",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2C3E50)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFF5F7FA))
                            .border(
                                width = 2.dp,
                                color = if (imageUri != null) BrandBlue else Color(0xFFE0E0E0),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable { showImagePickerDialog = true },
                        contentAlignment = Alignment.Center
                    ) {
                        if (imageUri != null) {
                            AsyncImage(
                                model = imageUri,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            // Edit overlay
                            Surface(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(12.dp),
                                shape = RoundedCornerShape(8.dp),
                                color = BrandBlue
                            ) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        } else {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Outlined.AddPhotoAlternate,
                                    contentDescription = null,
                                    tint = Color.Gray,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Tap untuk tambah foto",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Form Section
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "Informasi UMKM",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2C3E50)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Nama UMKM
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nama UMKM") },
                        leadingIcon = {
                            Icon(Icons.Outlined.Store, null, tint = BrandBlue)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandBlue,
                            focusedLabelColor = BrandBlue
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Kategori (Clickable)
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        label = { Text("Kategori") },
                        leadingIcon = {
                            Icon(Icons.Outlined.Category, null, tint = BrandOrange)
                        },
                        trailingIcon = {
                            Icon(Icons.Default.ArrowDropDown, null)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showCategoryDialog = true },
                        enabled = false,
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledBorderColor = Color(0xFFE0E0E0),
                            disabledTextColor = Color(0xFF2C3E50),
                            disabledLabelColor = Color.Gray,
                            disabledLeadingIconColor = BrandOrange
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Alamat
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Alamat Lengkap") },
                        leadingIcon = {
                            Icon(Icons.Outlined.LocationOn, null, tint = BrandPink)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandBlue,
                            focusedLabelColor = BrandBlue
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Deskripsi
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Deskripsi Singkat") },
                        leadingIcon = {
                            Icon(Icons.Outlined.Description, null, tint = BrandBlue)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 4,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandBlue,
                            focusedLabelColor = BrandBlue
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // GPS Location Button
                    OutlinedButton(
                        onClick = {
                            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (latitude != 0.0) BrandPink.copy(alpha = 0.1f) else Color.Transparent,
                            contentColor = BrandPink
                        )
                    ) {
                        Icon(Icons.Default.MyLocation, null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            if (latitude == 0.0) "Ambil Lokasi GPS"
                            else "Lokasi Tersimpan ✓"
                        )
                    }

                    if (latitude != 0.0) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Koordinat: ${String.format("%.6f", latitude)}, ${String.format("%.6f", longitude)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Submit Button
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Button(
                        onClick = {
                            if (name.isEmpty()) {
                                Toast.makeText(context, "Nama UMKM harus diisi", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (imageUri == null) {
                                Toast.makeText(context, "Foto UMKM harus diisi", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (address.isEmpty()) {
                                Toast.makeText(context, "Alamat harus diisi", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (latitude == 0.0) {
                                Toast.makeText(context, "Lokasi GPS harus diambil", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            viewModel.submitUMKM(name, category, address, description, imageUri, latitude, longitude)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = uiState !is AddState.Loading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrandBlue
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (uiState is AddState.Loading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Icon(Icons.Default.CheckCircle, null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Daftarkan UMKM",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}