package com.example.data.repository

import android.content.Context
import com.example.data.local.VeloraDatabase
import com.example.data.local.dao.CachedCartDao
import com.example.data.local.dao.CachedProductDao
import com.example.data.local.entities.toCachedEntity
import com.example.data.local.entities.toCartItem
import com.example.data.local.entities.toProduct
import com.example.data.models.CartItem
import com.example.data.models.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class OfflineCacheRepository(
    private val productCacheDao: CachedProductDao,
    private val cartCacheDao: CachedCartDao
) {

    constructor(context: Context) : this(
        VeloraDatabase.getDatabase(context).cachedProductDao(),
        VeloraDatabase.getDatabase(context).cachedCartDao()
    )

    // ==========================================
    // PRODUCTS CACHE
    // ==========================================

    /**
     * Observe all cached products reactively for offline catalog browsing
     */
    fun getCachedProducts(): Flow<List<Product>> {
        return productCacheDao.getAllCachedProducts().map { entities ->
            entities.map { it.toProduct() }
        }
    }

    /**
     * Observe cached flash sale items
     */
    fun getCachedFlashSaleProducts(): Flow<List<Product>> {
        return productCacheDao.getCachedFlashSaleProducts().map { entities ->
            entities.map { it.toProduct() }
        }
    }

    /**
     * Observe cached products filtered by category
     */
    fun getCachedProductsByCategory(category: String): Flow<List<Product>> {
        return productCacheDao.getCachedProductsByCategory(category).map { entities ->
            entities.map { it.toProduct() }
        }
    }

    /**
     * Search products in local offline cache
     */
    fun searchCachedProducts(query: String): Flow<List<Product>> {
        return productCacheDao.searchCachedProducts(query).map { entities ->
            entities.map { it.toProduct() }
        }
    }

    /**
     * Get single cached product by ID
     */
    suspend fun getCachedProductById(productId: String): Product? {
        return productCacheDao.getCachedProductById(productId)?.toProduct()
    }

    /**
     * Store list of products into local Room cache
     */
    suspend fun cacheProducts(products: List<Product>) {
        val entities = products.map { it.toCachedEntity() }
        productCacheDao.insertAll(entities)
    }

    /**
     * Store or update a single product into local cache
     */
    suspend fun cacheProduct(product: Product) {
        productCacheDao.insertOrUpdate(product.toCachedEntity())
    }

    /**
     * Remove product from local cache
     */
    suspend fun evictProduct(productId: String) {
        productCacheDao.deleteById(productId)
    }

    /**
     * Clear expired cached items older than threshold (e.g. 24 hours)
     */
    suspend fun clearExpiredCache(maxAgeMillis: Long = 24 * 60 * 60 * 1000L) {
        val cutoff = System.currentTimeMillis() - maxAgeMillis
        productCacheDao.clearExpired(cutoff)
    }

    // ==========================================
    // CART ITEMS CACHE
    // ==========================================

    /**
     * Observe cached cart items reactively
     */
    fun getCachedCartItems(): Flow<List<CartItem>> {
        return cartCacheDao.getCachedCartItems().map { entities ->
            entities.map { it.toCartItem() }
        }
    }

    /**
     * Add or update an item in the offline cached cart
     */
    suspend fun cacheCartItem(cartItem: CartItem) {
        cartCacheDao.insertOrUpdate(cartItem.toCachedEntity())
    }

    /**
     * Update quantity of a cached cart item
     */
    suspend fun updateCartQuantity(productId: String, quantity: Int) {
        if (quantity <= 0) {
            cartCacheDao.deleteById(productId)
        } else {
            cartCacheDao.updateQuantity(productId, quantity)
        }
    }

    /**
     * Toggle selection of an offline cart item
     */
    suspend fun updateCartSelection(productId: String, isSelected: Boolean) {
        cartCacheDao.updateSelection(productId, isSelected)
    }

    /**
     * Toggle selection of all offline cart items
     */
    suspend fun updateAllCartSelection(isSelected: Boolean) {
        cartCacheDao.updateAllSelection(isSelected)
    }

    /**
     * Remove an item from the offline cart
     */
    suspend fun removeCartItem(productId: String) {
        cartCacheDao.deleteById(productId)
    }

    /**
     * Clear all items from the offline cart
     */
    suspend fun clearCart() {
        cartCacheDao.clearCart()
    }
}
