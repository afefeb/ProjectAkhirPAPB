package com.example.konekumkm.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey(autoGenerate = false)
    val id: String = "",
    val userId: String = "",
    val items: String = "",
    val totalAmount: Double = 0.0,
    val paymentMethod: String = "",
    val orderDate: Long = System.currentTimeMillis(),
    val status: String = "completed"
)

data class OrderItem(
    val productId: String = "",
    val productName: String = "",
    val productPrice: Double = 0.0,
    val productImage: String = "",
    val umkmId: String = "",
    val umkmName: String = "",
    val quantity: Int = 1
)