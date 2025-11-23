package com.example.konekumkm.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "produk")
data class Produk(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    // Ubah ini dari Int ke String agar cocok dengan UMKM.id
    val umkmId: String,

    val name: String,
    val price: Double,
    val description: String,
    val imageUrl: String,
    val isFavorite: Boolean = false
)