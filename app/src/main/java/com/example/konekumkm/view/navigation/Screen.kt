package com.example.konekumkm.view.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")  // Splash Screen
    object Onboarding : Screen("onboarding")  // Landing Page
    object Home : Screen("home")
    object Search : Screen("search")
    object Produk : Screen("produk") // Baru
    object Map : Screen("map")
    object Blog : Screen("blog")     // Baru
    object About : Screen("about")   // Baru
    object Login : Screen("login")   // Baru
    object Register : Screen("register")

    object AdminDashboard : Screen("admin_dashboard")
    object AddUmkm : Screen("add_umkm")

    object Profile : Screen("profile")


    object Detail : Screen("detail/{umkmId}") {
        fun createRoute(umkmId: String) = "detail/$umkmId"
    }
    
    object Cart : Screen("cart")
    object PaymentMethod : Screen("payment_method")
    
    object Payment : Screen("payment/{paymentMethod}") {
        fun createRoute(paymentMethod: String) = "payment/$paymentMethod"
    }
    
    object OrderSuccess : Screen("order_success/{orderId}") {
        fun createRoute(orderId: String) = "order_success/$orderId"
    }
}