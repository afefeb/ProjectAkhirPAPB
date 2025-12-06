package com.example.konekumkm.view.ui.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.konekumkm.R
import com.example.konekumkm.utils.PreferenceManager
import com.example.konekumkm.view.navigation.Screen
import com.example.konekumkm.view.viewmodel.AuthState
import com.example.konekumkm.view.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current
    val prefManager = PreferenceManager(context)
    val authState by authViewModel.authState.collectAsState()

    // Tampilkan logo terlebih dahulu
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier.size(250.dp)
            )
        }
    }

    // Auto navigate setelah 2 detik
    LaunchedEffect(Unit) {
        delay(2000) // 2 detik
        
        val isOnboardingDone = prefManager.isOnboardingCompleted()
        
        // Cek status login dan onboarding
        val destination = when {
            // Jika sudah login, langsung ke Home
            authState is AuthState.Success -> Screen.Home.route
            // Jika belum pernah lihat onboarding (first time user), tampilkan onboarding
            !isOnboardingDone -> Screen.Onboarding.route
            // Jika sudah lihat onboarding tapi belum login, ke Login
            else -> Screen.Login.route
        }
        
        navController.navigate(destination) {
            popUpTo(Screen.Splash.route) { inclusive = true }
        }
    }
}
