package com.example.konekumkm.view.ui.onboarding

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.*
import com.example.konekumkm.utils.PreferenceManager
import com.example.konekumkm.view.navigation.Screen
import kotlinx.coroutines.launch

data class OnboardingPage(
    val title: String,
    val description: String,
    val lottieUrl: String
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(navController: NavController) {
    val context = LocalContext.current
    val prefManager = remember { PreferenceManager(context) }
    val scope = rememberCoroutineScope()
    
    val pages = listOf(
        OnboardingPage(
            title = "Temukan UMKM Lokal",
            description = "Jelajahi berbagai UMKM di sekitar Anda. Dukung usaha lokal dan temukan produk berkualitas dari tetangga Anda.",
            lottieUrl = "https://lottie.host/174e1c54-91ae-4562-b6f6-96beefdc1ccc/phoE5iSzRU.lottie"
        ),
        OnboardingPage(
            title = "Jelajahi Produk Favorit",
            description = "Telusuri berbagai produk dan layanan dari UMKM lokal. Temukan favorit Anda dan dukung ekonomi lokal dengan mudah.",
            lottieUrl = "https://lottie.host/13a24159-331e-4239-814d-56fd41181fec/Rwn4XsYzpv.lottie"
        ),
        OnboardingPage(
            title = "Belanja Lebih Mudah",
            description = "Nikmati pengalaman belanja yang mudah dan nyaman. Dapatkan produk UMKM langsung dari produsen lokal terpercaya!",
            lottieUrl = "https://lottie.host/fe49f509-79bc-4b97-afe6-58fd8a3da2e2/motGuZxU2i.lottie"
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Pager untuk slide
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                OnboardingPageContent(pages[page])
            }

            // Indikator & Tombol
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Page Indicator (Dots)
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    repeat(pages.size) { index ->
                        val width by animateDpAsState(
                            targetValue = if (pagerState.currentPage == index) 32.dp else 8.dp,
                            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                            label = "indicator"
                        )

                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .height(8.dp)
                                .width(width)
                                .clip(CircleShape)
                                .background(
                                    if (pagerState.currentPage == index)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                )
                        )
                    }
                }

                // Tombol Navigasi
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Tombol Skip (hanya muncul jika bukan halaman terakhir)
                    if (pagerState.currentPage < pages.size - 1) {
                        TextButton(
                            onClick = {
                                prefManager.setOnboardingCompleted(true)
                                navController.navigate(Screen.Login.route) {
                                    popUpTo("onboarding") { inclusive = true }
                                }
                            }
                        ) {
                            Text("Lewati", fontSize = 16.sp)
                        }
                    } else {
                        Spacer(modifier = Modifier.width(1.dp))
                    }

                    // Tombol Next / CTA
                    Button(
                        onClick = {
                            if (pagerState.currentPage < pages.size - 1) {
                                // Pindah ke halaman berikutnya
                                scope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            } else {
                                // Halaman terakhir, tandai onboarding selesai & pindah ke Login
                                prefManager.setOnboardingCompleted(true)
                                navController.navigate(Screen.Login.route) {
                                    popUpTo("onboarding") { inclusive = true }
                                }
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(50.dp)
                    ) {
                        Text(
                            text = if (pagerState.currentPage < pages.size - 1) "Selanjutnya" else "Ayo Jelajahi UMKM!",
                            fontSize = 16.sp,
                            fontWeight = if (pagerState.currentPage == pages.size - 1) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OnboardingPageContent(page: OnboardingPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Lottie Animation
        val composition by rememberLottieComposition(
            LottieCompositionSpec.Url(page.lottieUrl)
        )
        val progress by animateLottieCompositionAsState(
            composition = composition,
            iterations = LottieConstants.IterateForever,
            speed = 1f
        )

        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier
                .size(300.dp)
                .padding(bottom = 32.dp)
        )

        // Title
        Text(
            text = page.title,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Description
        Text(
            text = page.description,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )
    }
}
