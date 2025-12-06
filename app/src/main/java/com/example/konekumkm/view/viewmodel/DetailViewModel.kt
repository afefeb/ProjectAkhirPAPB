package com.example.konekumkm.view.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.konekumkm.data.local.entity.UMKM
import com.example.konekumkm.data.repository.UMKMRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DetailViewModel : ViewModel() {
    private val repository = UMKMRepository()

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState

    fun getUMKM(id: String) {
        viewModelScope.launch {
            _uiState.value = DetailUiState.Loading
            val result = repository.getUMKMById(id)
            if (result != null) {
                _uiState.value = DetailUiState.Success(result)
            } else {
                _uiState.value = DetailUiState.Error("Data tidak ditemukan")
            }
        }
    }
}

sealed class DetailUiState {
    object Loading : DetailUiState()
    data class Success(val umkm: UMKM) : DetailUiState()
    data class Error(val message: String) : DetailUiState()
}