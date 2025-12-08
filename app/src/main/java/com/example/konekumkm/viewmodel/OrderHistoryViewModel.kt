package com.example.konekumkm.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.konekumkm.data.local.AppDatabase
import com.example.konekumkm.data.local.entity.Order
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OrderHistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val orderDao = database.orderDao()
    private val auth = FirebaseAuth.getInstance()

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    init {
        loadOrders()
    }

    private fun loadOrders() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            viewModelScope.launch {
                orderDao.getOrdersByUserId(currentUser.uid).collect { orderList ->
                    _orders.value = orderList
                }
            }
        }
    }
}
