package com.example.konekumkm.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.konekumkm.data.local.entity.UMKM

@Dao
interface UMKMDao {

    @Insert
    fun insertUMKM(data: UMKM)

    @Query("SELECT * FROM umkm")
    fun getAllUMKM(): List<UMKM>
}
