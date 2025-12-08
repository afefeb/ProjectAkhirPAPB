package com.example.konekumkm.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.konekumkm.data.local.entity.Order
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders WHERE userId = :userId ORDER BY orderDate DESC")
    fun getOrdersByUserId(userId: String): Flow<List<Order>>

    @Query("SELECT * FROM orders WHERE id = :orderId")
    suspend fun getOrderById(orderId: String): Order?

    @Insert
    suspend fun insertOrder(order: Order)

    @Query("DELETE FROM orders WHERE id = :orderId")
    suspend fun deleteOrder(orderId: String)
}
