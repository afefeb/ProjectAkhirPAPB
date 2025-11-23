package com.example.konekumkm.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "umkm")
data class UMKM(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String
)
