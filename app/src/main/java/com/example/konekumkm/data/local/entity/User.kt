package com.example.konekumkm.data.local.entity

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "user" // Default role adalah 'user'
)