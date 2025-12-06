package com.example.konekumkm.data.repository

import com.example.konekumkm.data.local.entity.Produk
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ProductRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val productCollection = firestore.collection("products")

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

    suspend fun getProductsByUmkm(umkmId: String): List<Produk> {
        return try {
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