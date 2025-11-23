package com.example.konekumkm.view.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.konekumkm.data.local.entity.Produk
import com.example.konekumkm.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProductViewModel : ViewModel() {
    private val repository = ProductRepository()

    // State untuk list semua produk
    private val _allProducts = MutableStateFlow<List<Produk>>(emptyList())
    val allProducts: StateFlow<List<Produk>> = _allProducts

    // State untuk list produk spesifik (per UMKM)
    private val _umkmProducts = MutableStateFlow<List<Produk>>(emptyList())
    val umkmProducts: StateFlow<List<Produk>> = _umkmProducts

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Panggil ini di Halaman "Produk"
    fun fetchAllProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            _allProducts.value = repository.getAllProducts()
            _isLoading.value = false
        }
    }

    // Panggil ini di Halaman "Detail UMKM"
    fun fetchProductsByUmkm(umkmId: String) {
        viewModelScope.launch {
            _umkmProducts.value = repository.getProductsByUmkm(umkmId)
        }
    }
}