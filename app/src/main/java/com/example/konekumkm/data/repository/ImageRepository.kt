package com.example.konekumkm.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class ImageRepository(private val context: Context) { // Butuh Context

    // Fungsi ini TIDAK upload ke internet, tapi ubah gambar jadi String
    suspend fun imageUriToBase64(uri: Uri): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                // 1. Baca File Gambar dari URI
                val imageStream: InputStream? = context.contentResolver.openInputStream(uri)
                val originalBitmap = BitmapFactory.decodeStream(imageStream)

                if (originalBitmap == null) {
                    return@withContext Result.failure(Exception("Gagal membaca gambar"))
                }

                // 2. Kompres Gambar (PENTING! Agar tidak menuhin database)
                // Firestore punya batas 1MB per dokumen. Kita harus hemat.
                val outputStream = ByteArrayOutputStream()

                // Kualitas 50% cukup untuk tugas kuliah
                originalBitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)

                val byteArrays = outputStream.toByteArray()

                // 3. Ubah jadi String Base64
                val base64String = Base64.encodeToString(byteArrays, Base64.NO_WRAP)

                // Tambahkan prefix agar bisa dibaca Coil
                val finalString = "data:image/jpeg;base64,$base64String"

                Result.success(finalString)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    fun saveImageToFile(uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            if (bitmap == null) return null

            // Simpan ke cache folder app
            val file = File(context.cacheDir, "umkm_${System.currentTimeMillis()}.jpg")
            val out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out)
            out.flush()
            out.close()

            // Return path lokal sebagai string
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}