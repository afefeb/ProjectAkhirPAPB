package com.example.konekumkm.data.repository

import com.example.konekumkm.data.local.entity.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("users")

    // Cek user yang sedang login saat ini
    fun getCurrentUser() = auth.currentUser

    // Fungsi Login
    suspend fun login(email: String, pass: String): Result<User> {
        return try {
            // 1. Login ke Firebase Auth
            val authResult = auth.signInWithEmailAndPassword(email, pass).await()
            val uid = authResult.user?.uid ?: throw Exception("User ID not found")

            // 2. Ambil Data Role dari Firestore
            val document = usersCollection.document(uid).get().await()
            val role = document.getString("role") ?: "user"
            val name = document.getString("name") ?: ""

            Result.success(User(id = uid, name = name, email = email, role = role))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Fungsi Register (Default jadi USER biasa)
    suspend fun register(name: String, email: String, pass: String): Result<User> {
        return try {
            // 1. Buat Akun di Firebase Auth
            val authResult = auth.createUserWithEmailAndPassword(email, pass).await()
            val uid = authResult.user?.uid ?: throw Exception("Gagal membuat user ID")

            // 2. Simpan Data User ke Firestore (Default Role: user)
            val newUser = User(id = uid, name = name, email = email, role = "user")

            // Simpan ke collection 'users' dengan ID dokumen = UID Auth
            usersCollection.document(uid).set(newUser).await()

            Result.success(newUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        auth.signOut()
    }
}