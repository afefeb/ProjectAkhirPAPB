package com.example.konekumkm.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val productId: String = "",
    val productName: String = "",
    val productPrice: Double = 0.0,
    val productImage: String = "",
    val umkmId: String = "",
    val umkmName: String = "",
    val quantity: Int = 1,
    val isSelected: Boolean = false,
    val userId: String = ""
)
