package com.example.konekumkm.view.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.konekumkm.data.local.AppDatabase
import com.example.konekumkm.data.local.entity.CartItem
import com.example.konekumkm.data.local.entity.Order
import com.example.konekumkm.data.local.entity.OrderItem
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class CartViewModel(application: Application) : AndroidViewModel(application) {
    private val cartDao = AppDatabase.getDatabase(application).cartDao()
    private val auth = FirebaseAuth.getInstance()
    
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems
    
    private val _selectedItems = MutableStateFlow<List<CartItem>>(emptyList())
    val selectedItems: StateFlow<List<CartItem>> = _selectedItems
    
    private val _cartItemCount = MutableStateFlow(0)
    val cartItemCount: StateFlow<Int> = _cartItemCount
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    // Store last order details for success screen
    private val _lastOrderItems = MutableStateFlow<List<OrderItem>>(emptyList())
    val lastOrderItems: StateFlow<List<OrderItem>> = _lastOrderItems
    
    private val _lastOrderTotal = MutableStateFlow(0.0)
    val lastOrderTotal: StateFlow<Double> = _lastOrderTotal
    
    private val _addToCartMessage = MutableStateFlow<String?>(null)
    val addToCartMessage: StateFlow<String?> = _addToCartMessage
    
    init {
        loadCartItems()
    }
    
    private fun getCurrentUserId(): String {
        return auth.currentUser?.uid ?: "guest"
    }
    
    fun loadCartItems() {
        viewModelScope.launch {
            cartDao.getCartItems(getCurrentUserId()).collect { items ->
                _cartItems.value = items
                _selectedItems.value = items.filter { it.isSelected }
            }
        }
        
        viewModelScope.launch {
            cartDao.getCartItemCount(getCurrentUserId()).collect { count ->
                _cartItemCount.value = count
            }
        }
    }
    
    fun addToCart(
        productId: String,
        productName: String,
        productPrice: Double,
        productImage: String,
        umkmId: String,
        umkmName: String
    ) {
        viewModelScope.launch {
            val existingItem = cartDao.getCartItemByProduct(getCurrentUserId(), productId)
            
            if (existingItem != null) {
                cartDao.updateCartItem(existingItem.copy(quantity = existingItem.quantity + 1))
            } else {
                val cartItem = CartItem(
                    productId = productId,
                    productName = productName,
                    productPrice = productPrice,
                    productImage = productImage,
                    umkmId = umkmId,
                    umkmName = umkmName,
                    quantity = 1,
                    isSelected = false,
                    userId = getCurrentUserId()
                )
                cartDao.insertCartItem(cartItem)
            }
            
            _addToCartMessage.value = "Berhasil menambahkan $productName ke keranjang"
        }
    }
    
    fun clearAddToCartMessage() {
        _addToCartMessage.value = null
    }
    
    fun updateQuantity(cartItem: CartItem, newQuantity: Int) {
        if (newQuantity <= 0) {
            removeFromCart(cartItem)
        } else {
            viewModelScope.launch {
                cartDao.updateCartItem(cartItem.copy(quantity = newQuantity))
            }
        }
    }
    
    fun toggleItemSelection(cartItem: CartItem) {
        viewModelScope.launch {
            cartDao.updateItemSelection(cartItem.id, !cartItem.isSelected)
        }
    }
    
    fun toggleSelectAll(selectAll: Boolean) {
        viewModelScope.launch {
            cartDao.updateAllItemsSelection(getCurrentUserId(), selectAll)
        }
    }
    
    fun removeFromCart(cartItem: CartItem) {
        viewModelScope.launch {
            cartDao.deleteCartItem(cartItem)
        }
    }
    
    fun clearCart() {
        viewModelScope.launch {
            cartDao.clearCart(getCurrentUserId())
        }
    }
    
    fun getTotalPrice(): Double {
        return _selectedItems.value.sumOf { it.productPrice * it.quantity }
    }
    
    fun getSelectedItemsCount(): Int {
        return _selectedItems.value.sumOf { it.quantity }
    }
    
    suspend fun createOrder(paymentMethod: String): String {
        val orderId = UUID.randomUUID().toString()
        val orderItems = _selectedItems.value.map { cartItem ->
            OrderItem(
                productId = cartItem.productId,
                productName = cartItem.productName,
                productPrice = cartItem.productPrice,
                productImage = cartItem.productImage,
                umkmId = cartItem.umkmId,
                umkmName = cartItem.umkmName,
                quantity = cartItem.quantity
            )
        }
        
        val totalAmount = orderItems.sumOf { it.productPrice * it.quantity }
        
        _lastOrderItems.value = orderItems
        _lastOrderTotal.value = totalAmount
        
        val order = Order(
            id = orderId,
            userId = getCurrentUserId(),
            items = Gson().toJson(orderItems),
            totalAmount = totalAmount,
            paymentMethod = paymentMethod,
            orderDate = System.currentTimeMillis(),
            status = "completed"
        )
        
        cartDao.deleteSelectedItems(getCurrentUserId())
        
        return orderId
    }
}
