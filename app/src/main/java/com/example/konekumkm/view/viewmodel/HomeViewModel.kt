package com.example.konekumkm.view.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.konekumkm.data.local.entity.UMKM
import com.example.konekumkm.data.repository.UMKMRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    // Panggil Repository yang sudah kita update sebelumnya
    private val repository = UMKMRepository()

    // State untuk menyimpan list UMKM
    private val _umkmList = MutableStateFlow<List<UMKM>>(emptyList())
    val umkmList: StateFlow<List<UMKM>> = _umkmList

    // State untuk status loading
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        // Otomatis ambil data saat ViewModel dibuat
        fetchData()
    }

    fun fetchData() {
        viewModelScope.launch {
            _isLoading.value = true
            // Ambil data dari Firebase lewat Repository
            val data = repository.getAllUMKM()
            _umkmList.value = data
            _isLoading.value = false
        }
    }
}