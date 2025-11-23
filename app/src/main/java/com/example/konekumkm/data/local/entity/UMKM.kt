package com.example.konekumkm.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.Exclude // Opsional, biar ID ga ikut ke-save double di body firebase

@Entity(tableName = "umkm")
data class UMKM(
    // Ubah Int jadi String & matikan autoGenerate
    @PrimaryKey(autoGenerate = false)
    val id: String = "",

    val name: String = "",
    val category: String = "",
    val address: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val rating: Double = 0.0
)