package com.example.konekumkm.view.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Produk : Screen("produk") // Baru
    object Map : Screen("map")
    object Blog : Screen("blog")     // Baru
    object About : Screen("about")   // Baru
    object Login : Screen("login")   // Baru

    object Detail : Screen("detail/{umkmId}") {
        fun createRoute(umkmId: String) = "detail/$umkmId"
    }
}