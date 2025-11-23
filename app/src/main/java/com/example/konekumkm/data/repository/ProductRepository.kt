package com.example.konekumkm.data.repository

import com.example.konekumkm.data.local.entity.Produk
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ProductRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val productCollection = firestore.collection("products") // Pastikan nama collection ini

    // 1. Ambil SEMUA produk (Untuk Halaman Produk Utama)
    suspend fun getAllProducts(): List<Produk> {
        return try {
            val snapshot = productCollection.get().await()
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(Produk::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // 2. Ambil produk SPESIFIK milik satu UMKM (Untuk Halaman Detail UMKM)
    suspend fun getProductsByUmkm(umkmId: String): List<Produk> {
        return try {
            // Query: Cari produk di mana field 'umkmId' == id toko yg dibuka
            val snapshot = productCollection
                .whereEqualTo("umkmId", umkmId)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                doc.toObject(Produk::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}