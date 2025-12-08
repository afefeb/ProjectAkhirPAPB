package com.example.konekumkm.view.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.konekumkm.view.navigation.Screen
import com.example.konekumkm.view.viewmodel.AuthState
import com.example.konekumkm.view.viewmodel.AuthViewModel

// Brand colors
val BrandBlue = Color(0xFF6B9BD1)
val BrandOrange = Color(0xFFFF9066)
val BrandPink = Color(0xFFD77FA1)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: AuthViewModel = viewModel()
) {
    val authState by viewModel.authState.collectAsState()
    var showEditDialog by remember { mutableStateOf(false) }

    // Ambil data user dari authState
    val currentUser = when (val state = authState) {
        is AuthState.Success -> state.user
        else -> null
    }
    val isLoggedIn = currentUser != null

    // Dialog Edit Profile
    if (showEditDialog && currentUser != null) {
        EditProfileDialog(
            currentName = currentUser.name,
            currentEmail = currentUser.email,
            onDismiss = { showEditDialog = false },
            onSave = { name, email ->
                // TODO: Implement update profile di AuthRepository & ViewModel
                showEditDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Profil",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2C3E50)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Kembali",
                            tint = BrandBlue
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color(0xFFF8F9FA)
    ) { paddingValues ->

        if (!isLoggedIn) {
            // Guest View - Belum Login
            GuestProfileView(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                onLoginClick = {
                    navController.navigate(Screen.Login.route)
                },
                onRegisterClick = {
                    navController.navigate(Screen.Register.route)
                }
            )
        } else {
            // Logged In View
            LoggedInProfileView(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                userName = currentUser?.name ?: "",
                userEmail = currentUser?.email ?: "",
                onEditProfile = { showEditDialog = true },
                onMyOrders = { navController.navigate(Screen.OrderHistory.route) },
                onFavorites = { /* TODO: Navigate to favorites */ },
                onHelp = { /* TODO: Navigate to help */ },
                onLogout = {
                    viewModel.logout()
                }
            )
        }
    }
}

@Composable
fun GuestProfileView(
    modifier: Modifier = Modifier,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        // Icon Placeholder
        Surface(
            modifier = Modifier.size(120.dp),
            shape = CircleShape,
            color = BrandBlue.copy(alpha = 0.1f)
        ) {
            Icon(
                imageVector = Icons.Outlined.AccountCircle,
                contentDescription = null,
                tint = BrandBlue,
                modifier = Modifier.padding(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Belum Masuk",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2C3E50)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Masuk untuk akses fitur lengkap",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Login Button
        Button(
            onClick = onLoginClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = BrandBlue
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                "Masuk",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Register Button
        OutlinedButton(
            onClick = onRegisterClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = BrandBlue
            ),
            border = ButtonDefaults.outlinedButtonBorder.copy(
                width = 2.dp,
                brush = androidx.compose.ui.graphics.SolidColor(BrandBlue)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                "Daftar",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Guest Features
        Text(
            text = "Fitur Tamu",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2C3E50),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        GuestMenuItem(
            icon = Icons.Outlined.Store,
            title = "Jelajahi UMKM",
            subtitle = "Lihat semua UMKM lokal"
        )

        GuestMenuItem(
            icon = Icons.Outlined.Inventory,
            title = "Lihat Produk",
            subtitle = "Temukan produk berkualitas"
        )

        GuestMenuItem(
            icon = Icons.Outlined.Info,
            title = "Bantuan",
            subtitle = "Pusat bantuan pengguna"
        )
    }
}

@Composable
fun LoggedInProfileView(
    modifier: Modifier = Modifier,
    userName: String,
    userEmail: String,
    onEditProfile: () -> Unit,
    onMyOrders: () -> Unit,
    onFavorites: () -> Unit,
    onHelp: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState())
    ) {
        // Profile Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile Picture
                Surface(
                    modifier = Modifier.size(100.dp),
                    shape = CircleShape,
                    color = BrandPink.copy(alpha = 0.2f)
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = null,
                        tint = BrandPink,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = userName.ifEmpty { "Nama Pengguna" },
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2C3E50)
                )

                Text(
                    text = userEmail.ifEmpty { "email@example.com" },
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Edit Profile Button
                OutlinedButton(
                    onClick = onEditProfile,
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = BrandBlue
                    )
                ) {
                    Icon(
                        Icons.Outlined.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Edit Profil")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Menu Section
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White
        ) {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                ProfileMenuItem(
                    icon = Icons.Outlined.ShoppingBag,
                    title = "Pesanan Saya",
                    subtitle = "Lihat riwayat pesanan",
                    onClick = onMyOrders
                )

                ProfileMenuItem(
                    icon = Icons.Outlined.FavoriteBorder,
                    title = "Favorit",
                    subtitle = "UMKM & produk favorit",
                    onClick = onFavorites
                )

                ProfileMenuItem(
                    icon = Icons.Outlined.Help,
                    title = "Bantuan",
                    subtitle = "Pusat bantuan & FAQ",
                    onClick = onHelp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Logout Section
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White
        ) {
            ProfileMenuItem(
                icon = Icons.Outlined.Logout,
                title = "Keluar",
                subtitle = "Keluar dari akun",
                iconTint = Color.Red,
                onClick = onLogout
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconTint: Color = BrandBlue,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = iconTint.copy(alpha = 0.1f)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.padding(12.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF2C3E50)
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.Gray
            )
        }
    }
}

@Composable
fun GuestMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        color = Color.White
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = BrandBlue.copy(alpha = 0.1f)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = BrandBlue,
                    modifier = Modifier.padding(12.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF2C3E50)
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun EditProfileDialog(
    currentName: String,
    currentEmail: String,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var name by remember { mutableStateOf(currentName) }
    var email by remember { mutableStateOf(currentEmail) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Edit Profil",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2C3E50)
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandBlue,
                        focusedLabelColor = BrandBlue
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandBlue,
                        focusedLabelColor = BrandBlue
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(name, email) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandBlue
                )
            ) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal", color = Color.Gray)
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}