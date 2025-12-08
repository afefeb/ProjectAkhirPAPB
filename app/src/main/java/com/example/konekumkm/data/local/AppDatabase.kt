package com.example.konekumkm.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.konekumkm.data.local.dao.OrderDao
//import com.example.konekumkm.data.local.dao.ProdukDao
import com.example.konekumkm.data.local.dao.CartDao
import com.example.konekumkm.data.local.dao.UMKMDao
import com.example.konekumkm.data.local.entity.CartItem
import com.example.konekumkm.data.local.entity.Order
//import com.example.konekumkm.data.local.entity.Produk
import com.example.konekumkm.data.local.entity.UMKM

@Database(
    entities = [UMKM::class, CartItem::class, Order::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun umkmDao(): UMKMDao
    abstract fun cartDao(): CartDao
//    abstract fun produkDao(): ProdukDao
    abstract fun orderDao(): OrderDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "konekumkm_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
