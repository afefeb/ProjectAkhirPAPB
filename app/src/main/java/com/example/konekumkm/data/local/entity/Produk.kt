package com.example.konekumkm.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "produk")
data class Produk(
    @PrimaryKey(autoGenerate = false)
    val id: String = "",

    val umkmId: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val description: String = "",
    val imageUrl: String = "",
    val category: String = ""
)