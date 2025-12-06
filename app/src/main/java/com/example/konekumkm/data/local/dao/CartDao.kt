package com.example.konekumkm.data.local.dao

import androidx.room.*
import com.example.konekumkm.data.local.entity.CartItem
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    @Query("SELECT * FROM cart_items WHERE userId = :userId")
    fun getCartItems(userId: String): Flow<List<CartItem>>
    
    @Query("SELECT * FROM cart_items WHERE userId = :userId AND isSelected = 1")
    fun getSelectedCartItems(userId: String): Flow<List<CartItem>>
    
    @Query("SELECT COUNT(*) FROM cart_items WHERE userId = :userId")
    fun getCartItemCount(userId: String): Flow<Int>
    
    @Query("SELECT * FROM cart_items WHERE userId = :userId AND productId = :productId LIMIT 1")
    suspend fun getCartItemByProduct(userId: String, productId: String): CartItem?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(cartItem: CartItem)
    
    @Update
    suspend fun updateCartItem(cartItem: CartItem)
    
    @Delete
    suspend fun deleteCartItem(cartItem: CartItem)
    
    @Query("DELETE FROM cart_items WHERE userId = :userId AND isSelected = 1")
    suspend fun deleteSelectedItems(userId: String)
    
    @Query("DELETE FROM cart_items WHERE userId = :userId")
    suspend fun clearCart(userId: String)
    
    @Query("UPDATE cart_items SET isSelected = :isSelected WHERE id = :cartItemId")
    suspend fun updateItemSelection(cartItemId: Int, isSelected: Boolean)
    
    @Query("UPDATE cart_items SET isSelected = :isSelected WHERE userId = :userId")
    suspend fun updateAllItemsSelection(userId: String, isSelected: Boolean)
}
