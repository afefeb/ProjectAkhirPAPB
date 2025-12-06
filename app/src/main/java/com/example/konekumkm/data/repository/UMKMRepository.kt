package com.example.konekumkm.data.repository

import com.example.konekumkm.data.local.entity.UMKM
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint // Import wajib untuk Geopoint
import kotlinx.coroutines.tasks.await

class UMKMRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val umkmCollection = firestore.collection("umkm_list")

    suspend fun getAllUMKM(): List<UMKM> {
        return try {
            val snapshot = umkmCollection.get().await()
            snapshot.documents.mapNotNull { doc ->
                val geoPoint = doc.getGeoPoint("location")
                val lat = geoPoint?.latitude ?: 0.0
                val lng = geoPoint?.longitude ?: 0.0

                UMKM(
                    id = doc.id,

                    name = doc.getString("name") ?: "",
                    category = doc.getString("category") ?: "",
                    address = doc.getString("address") ?: "",
                    description = doc.getString("description") ?: "",
                    imageUrl = doc.getString("imageUrl") ?: "",
                    latitude = lat,
                    longitude = lng,
                    rating = doc.getDouble("rating") ?: 0.0
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun addUMKM(umkm: UMKM, onResult: (Boolean) -> Unit) {
        val locationGeo = GeoPoint(umkm.latitude, umkm.longitude)

        val newUMKM = hashMapOf(
            "name" to umkm.name,
            "category" to umkm.category,
            "description" to umkm.description,
            "imageUrl" to umkm.imageUrl,

            "location" to locationGeo
        )
        umkmCollection.add(newUMKM)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    suspend fun getUMKMById(umkmId: String): UMKM? {
        return try {
            val document = umkmCollection.document(umkmId).get().await()
            if (document.exists()) {
                val geoPoint = document.getGeoPoint("location")
                val lat = geoPoint?.latitude ?: 0.0
                val lng = geoPoint?.longitude ?: 0.0

                UMKM(
                    id = document.id,
                    name = document.getString("name") ?: "",
                    category = document.getString("category") ?: "",
                    address = document.getString("address") ?: "",
                    description = document.getString("description") ?: "",
                    imageUrl = document.getString("imageUrl") ?: "",
                    rating = document.getDouble("rating") ?: 0.0,
                    latitude = lat,
                    longitude = lng
                )
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}