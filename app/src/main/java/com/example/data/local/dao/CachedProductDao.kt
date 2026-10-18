package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.CachedProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CachedProductDao {

    @Query("SELECT * FROM cached_products ORDER BY cachedAtTimestamp DESC")
    fun getAllCachedProducts(): Flow<List<CachedProductEntity>>

    @Query("SELECT * FROM cached_products WHERE isFlashSale = 1")
    fun getCachedFlashSaleProducts(): Flow<List<CachedProductEntity>>

    @Query("SELECT * FROM cached_products WHERE category = :category")
    fun getCachedProductsByCategory(category: String): Flow<List<CachedProductEntity>>

    @Query("SELECT * FROM cached_products WHERE id = :productId")
    suspend fun getCachedProductById(productId: String): CachedProductEntity?

    @Query("SELECT * FROM cached_products WHERE title LIKE '%' || :query || '%' OR brand LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%'")
    fun searchCachedProducts(query: String): Flow<List<CachedProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(product: CachedProductEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<CachedProductEntity>)

    @Update
    suspend fun update(product: CachedProductEntity)

    @Query("DELETE FROM cached_products WHERE id = :productId")
    suspend fun deleteById(productId: String)

    @Query("SELECT COUNT(*) FROM cached_products")
    suspend fun getCachedCount(): Int

    @Query("DELETE FROM cached_products WHERE cachedAtTimestamp < :expiryTimestamp")
    suspend fun clearExpired(expiryTimestamp: Long)

    @Query("DELETE FROM cached_products")
    suspend fun clearAll()
}
