package com.example.konekumkm.view.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Map : Screen("map")
    object AddUmkm : Screen("add_umkm")
    object Detail : Screen("detail/{umkmId}") {
        fun createRoute(umkmId: String) = "detail/$umkmId"
    }
}