package com.example.konekumkm.view.ui.map

import android.Manifest
import android.content.Context
import android.preference.PreferenceManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.konekumkm.data.local.entity.UMKM
import com.example.konekumkm.view.navigation.Screen
import com.example.konekumkm.view.viewmodel.HomeViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.util.MapTileIndex
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

object CartoDBVoyager : OnlineTileSourceBase(
    "CartoDB Voyager",
    0, 20, 256, ".png",
    arrayOf(
        "https://a.basemaps.cartocdn.com/rastertiles/voyager/",
        "https://b.basemaps.cartocdn.com/rastertiles/voyager/",
        "https://c.basemaps.cartocdn.com/rastertiles/voyager/",
        "https://d.basemaps.cartocdn.com/rastertiles/voyager/"
    ),
    "© OpenStreetMap contributors © CARTO"
) {
    override fun getTileURLString(pMapTileIndex: Long): String {
        return baseUrl + MapTileIndex.getZoom(pMapTileIndex) +
                "/" + MapTileIndex.getX(pMapTileIndex) +
                "/" + MapTileIndex.getY(pMapTileIndex) + mImageFilenameEnding
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    navController: NavController,
    viewModel: HomeViewModel = viewModel()
) {
    val context = LocalContext.current
    val umkmList by viewModel.umkmList.collectAsState()

    var mapView by remember { mutableStateOf<MapView?>(null) }
    var userLocationOverlay by remember { mutableStateOf<MyLocationNewOverlay?>(null) }
    var selectedUmkm by remember { mutableStateOf<UMKM?>(null) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val isGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (isGranted) {
            userLocationOverlay?.enableMyLocation()
            userLocationOverlay?.enableFollowLocation()
        }
    }

    LaunchedEffect(Unit) {
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context))
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
            FloatingActionButton(
                onClick = {
                    val overlay = userLocationOverlay
                    if (overlay != null && overlay.isMyLocationEnabled) {
                        val myLocation = overlay.myLocation
                        if (myLocation != null) {
                            mapView?.controller?.animateTo(myLocation)
                            mapView?.controller?.setZoom(16.0)
                        }
                    } else {
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
                        setTileSource(CartoDBVoyager)
                        setMultiTouchControls(true)

                        zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)

                        val compassOverlay = CompassOverlay(ctx, InternalCompassOrientationProvider(ctx), this)
                        compassOverlay.enableCompass()
                        overlays.add(compassOverlay)

                        val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(ctx), this)
                        locationOverlay.enableMyLocation()
                        overlays.add(locationOverlay)

                        userLocationOverlay = locationOverlay

                        controller.setZoom(13.0)
                        controller.setCenter(GeoPoint(-7.9666, 112.6326))

                        mapView = this
                    }
                },
                update = { map ->
                    val staticOverlays = map.overlays.filter {
                        it is MyLocationNewOverlay || it is CompassOverlay
                    }
                    map.overlays.clear()
                    map.overlays.addAll(staticOverlays)

                    umkmList.forEach { umkm ->
                        val marker = Marker(map)
                        marker.position = GeoPoint(umkm.latitude, umkm.longitude)
                        marker.title = umkm.name
                        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

                        marker.setOnMarkerClickListener { m, _ ->
                            selectedUmkm = umkm
                            map.controller.animateTo(GeoPoint(umkm.latitude, umkm.longitude))
                            true
                        }
                        map.overlays.add(marker)
                    }

                    map.invalidate()
                }
            )

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
            
            selectedUmkm?.let { umkm ->
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        AsyncImage(
                            model = umkm.imageUrl.ifEmpty { "https://via.placeholder.com/400x200?text=No+Image" },
                            contentDescription = umkm.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                            contentScale = ContentScale.Crop
                        )
                        
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = umkm.name,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            Text(
                                text = umkm.category,
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Rating",
                                    tint = Color(0xFFFFC107),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = String.format("%.1f", umkm.rating),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Black
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { selectedUmkm = null },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Tutup")
                                }
                                
                                Button(
                                    onClick = {
                                        navController.navigate(Screen.Detail.createRoute(umkm.id))
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary
                                    )
                                ) {
                                    Text("Lihat Detail")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}