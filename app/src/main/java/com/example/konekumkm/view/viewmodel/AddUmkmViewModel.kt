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

class AddUmkmViewModel(application: Application) : AndroidViewModel(application) {

    private val umkmRepository = UMKMRepository()
    private val imageRepository = ImageRepository(application.applicationContext)

    private val _uiState = MutableStateFlow<AddState>(AddState.Idle)
    val uiState: StateFlow<AddState> = _uiState

    fun submitUMKM(
        name: String,
        category: String,
        address: String,
        desc: String,
        imageUri: Uri?,
        lat: Double,
        lng: Double
    ) {
        viewModelScope.launch {
            _uiState.value = AddState.Loading

            if (name.isEmpty() || imageUri == null) {
                _uiState.value = AddState.Error("Data tidak lengkap")
                return@launch
            }

            val localUriString = imageRepository.saveImageToFile(imageUri)

            if (localUriString == null) {
                _uiState.value = AddState.Error("Gagal memproses gambar")
                return@launch
            }

            val newUMKM = UMKM(
                name = name,
                category = category,
                address = address,
                description = desc,
                imageUrl = localUriString, // simpan URI string lokal
                latitude = lat,
                longitude = lng,
                rating = 0.0
            )

            umkmRepository.addUMKM(newUMKM) { success ->
                _uiState.value =
                    if (success) AddState.Success else AddState.Error("Gagal simpan ke database")
            }
        }
    }

    fun resetState() {
        _uiState.value = AddState.Idle
    }

    sealed class AddState {
        object Idle : AddState()
        object Loading : AddState()
        object Success : AddState()
        data class Error(val message: String) : AddState()
    }
}

