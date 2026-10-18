package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entities.CachedCartItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CachedCartDao {

    @Query("SELECT * FROM cached_cart_items ORDER BY addedAtTimestamp DESC")
    fun getCachedCartItems(): Flow<List<CachedCartItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(item: CachedCartItemEntity)

    @Query("UPDATE cached_cart_items SET quantity = :quantity WHERE productId = :productId")
    suspend fun updateQuantity(productId: String, quantity: Int)

    @Query("UPDATE cached_cart_items SET isSelected = :isSelected WHERE productId = :productId")
    suspend fun updateSelection(productId: String, isSelected: Boolean)

    @Query("UPDATE cached_cart_items SET isSelected = :isSelected")
    suspend fun updateAllSelection(isSelected: Boolean)

    @Query("DELETE FROM cached_cart_items WHERE productId = :productId")
    suspend fun deleteById(productId: String)

    @Query("DELETE FROM cached_cart_items")
    suspend fun clearCart()

    @Query("SELECT COUNT(*) FROM cached_cart_items")
    suspend fun getCartItemCount(): Int
}
