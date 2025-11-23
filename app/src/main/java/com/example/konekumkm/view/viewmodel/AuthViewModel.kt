package com.example.konekumkm.view.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.konekumkm.data.local.entity.User
import com.example.konekumkm.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val repository = AuthRepository()

    // Ubah default state jadi Loading dulu biar aman
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    // --- UPDATE BAGIAN INI ---
    init {
        val currentUser = repository.getCurrentUser()
        if (currentUser != null) {
            // Jika di Firebase ada user, kita anggap Login Sukses!
            // Kita ambil data role sederhana dulu (atau fetch ulang dari Firestore jika mau lengkap)
            // Untuk sekarang, kita pakai data dasar dari Auth
            _authState.value = AuthState.Success(
                User(
                    id = currentUser.uid,
                    name = currentUser.displayName ?: "User",
                    email = currentUser.email ?: "",
                    role = "user" // Default sementara, idealnya fetch dari Firestore lagi
                )
            )
        }
    }

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repository.login(email, pass)
            result.fold(
                onSuccess = { user -> _authState.value = AuthState.Success(user) },
                onFailure = { error -> _authState.value = AuthState.Error(error.message ?: "Login Gagal") }
            )
        }
    }

    fun register(name: String, email: String, pass: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repository.register(name, email, pass)
            result.fold(
                onSuccess = { user -> _authState.value = AuthState.Success(user) },
                onFailure = { error -> _authState.value = AuthState.Error(error.message ?: "Register Gagal") }
            )
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }

    fun logout() {
        repository.logout()
        _authState.value = AuthState.Idle
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
}