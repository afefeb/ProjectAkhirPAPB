package com.example.konekumkm.view.ui.map

import android.Manifest
import android.content.Context
import android.preference.PreferenceManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.konekumkm.view.viewmodel.HomeViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    navController: NavController,
    viewModel: HomeViewModel = viewModel()
) {
    val context = LocalContext.current
    val umkmList by viewModel.umkmList.collectAsState()

    // State untuk menyimpan referensi MapView agar bisa dikontrol tombol
    var mapView by remember { mutableStateOf<MapView?>(null) }
    var userLocationOverlay by remember { mutableStateOf<MyLocationNewOverlay?>(null) }

    // Launcher untuk meminta izin lokasi
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val isGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (isGranted) {
            // Jika diizinkan, aktifkan lokasi user
            userLocationOverlay?.enableMyLocation()
            userLocationOverlay?.enableFollowLocation()
        }
    }

    // Load konfigurasi OSM saat pertama kali dibuka
    LaunchedEffect(Unit) {
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context))
        // Minta izin lokasi saat layar dibuka
        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Peta Persebaran UMKM") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        floatingActionButton = {
            // Tombol untuk kembali ke lokasi user
            FloatingActionButton(
                onClick = {
                    val overlay = userLocationOverlay
                    if (overlay != null && overlay.isMyLocationEnabled) {
                        // Arahkan peta ke lokasi user dengan animasi
                        val myLocation = overlay.myLocation
                        if (myLocation != null) {
                            mapView?.controller?.animateTo(myLocation)
                            mapView?.controller?.setZoom(16.0)
                        }
                    } else {
                        // Jika belum aktif, minta izin lagi
                        locationPermissionLauncher.launch(
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
                        )
                    }
                }
            ) {
                Icon(Icons.Default.MyLocation, contentDescription = "Lokasi Saya")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    MapView(ctx).apply {
                        setTileSource(TileSourceFactory.MAPNIK)
                        setMultiTouchControls(true)

                        // Sembunyikan tombol zoom bawaan yang jelek
                        zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)

                        // 1. Tambahkan Overlay Kompas
                        val compassOverlay = CompassOverlay(ctx, InternalCompassOrientationProvider(ctx), this)
                        compassOverlay.enableCompass()
                        overlays.add(compassOverlay)

                        // 2. Tambahkan Overlay Lokasi User (Blue Dot)
                        val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(ctx), this)
                        locationOverlay.enableMyLocation()
                        overlays.add(locationOverlay)

                        // Simpan referensi ke state luar
                        userLocationOverlay = locationOverlay

                        // Set titik awal (Default ke Malang jika GPS belum dapat)
                        controller.setZoom(13.0)
                        controller.setCenter(GeoPoint(-7.9666, 112.6326))

                        mapView = this // Simpan referensi MapView
                    }
                },
                update = { map ->
                    // 3. Update Marker UMKM (Logic Marker tetap sama)
                    // Hapus marker lama (kecuali Overlay Lokasi & Kompas) agar tidak numpuk
                    val staticOverlays = map.overlays.filter {
                        it is MyLocationNewOverlay || it is CompassOverlay
                    }
                    map.overlays.clear()
                    map.overlays.addAll(staticOverlays)

                    umkmList.forEach { umkm ->
                        val marker = Marker(map)
                        marker.position = GeoPoint(umkm.latitude, umkm.longitude)
                        marker.title = umkm.name
                        marker.snippet = umkm.category + "\n" + umkm.address
                        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

                        // Icon default osmdroid sudah cukup bagus (pin),
                        // tapi Anda bisa custom icon disini dengan marker.icon = ...

                        marker.setOnMarkerClickListener { m, _ ->
                            m.showInfoWindow()
                            true
                        }
                        map.overlays.add(marker)
                    }

                    map.invalidate() // Refresh peta
                }
            )

            // Info bar jika data kosong (Opsional)
            if (umkmList.isEmpty()) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(16.dp),
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)
                ) {
                    Text(
                        text = "Memuat data UMKM...",
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}