package com.example.konekumkm.view.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.konekumkm.data.local.entity.UMKM
import com.example.konekumkm.data.repository.ImageRepository
import com.example.konekumkm.data.repository.UMKMRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Pastikan extends AndroidViewModel dan menerima application di constructor
class AddUmkmViewModel(application: Application) : AndroidViewModel(application) {

    private val umkmRepository = UMKMRepository()

    // Menggunakan context dari application untuk ImageRepository (Base64)
    private val imageRepository = ImageRepository(application.applicationContext)

    // State UI
    private val _uiState = MutableStateFlow<AddState>(AddState.Idle)
    val uiState: StateFlow<AddState> = _uiState

    fun submitUMKM(name: String, category: String, address: String, desc: String, imageUri: Uri?, lat: Double, lng: Double) {
        viewModelScope.launch {
            _uiState.value = AddState.Loading

            if (name.isEmpty() || imageUri == null) {
                _uiState.value = AddState.Error("Data tidak lengkap")
                return@launch
            }

            // 1. Ubah Gambar ke Base64
            val base64Result = imageRepository.imageUriToBase64(imageUri)
            val finalImageString = base64Result.getOrNull()

            if (finalImageString == null) {
                _uiState.value = AddState.Error("Gagal memproses gambar (Terlalu besar/Corrupt)")
                return@launch
            }

            // 2. Buat Object UMKM
            val newUMKM = UMKM(
                name = name,
                category = category,
                address = address,
                description = desc,
                imageUrl = finalImageString, // Simpan String Base64
                latitude = lat,
                longitude = lng
            )

            // 3. Simpan ke Firestore
            umkmRepository.addUMKM(newUMKM) { success ->
                if (success) _uiState.value = AddState.Success
                else _uiState.value = AddState.Error("Gagal simpan ke database")
            }
        }
    }

    fun resetState() { _uiState.value = AddState.Idle }
}

// --- JANGAN LUPA BAGIAN INI ---
// Definisi Sealed Class untuk State (Harus ada agar 'AddState' dikenali)
sealed class AddState {
    object Idle : AddState()
    object Loading : AddState()
    object Success : AddState()
    data class Error(val message: String) : AddState()
}