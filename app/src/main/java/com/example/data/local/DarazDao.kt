package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.models.CartItem
import com.example.data.models.NotificationItem
import com.example.data.models.Order
import com.example.data.models.Product
import com.example.data.models.Review
import com.example.data.models.WishlistItem
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products")
    fun getAllProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE isFlashSale = 1")
    fun getFlashSaleProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE category = :category")
    fun getProductsByCategory(category: String): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getProductById(id: String): Product?

    @Query("SELECT * FROM products WHERE title LIKE '%' || :query || '%' OR brand LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%'")
    fun searchProducts(query: String): Flow<List<Product>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<Product>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: Product)

    @Update
    suspend fun update(product: Product)

    @Query("DELETE FROM products WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT COUNT(*) FROM products")
    suspend fun getCount(): Int
}

@Dao
interface CartDao {
    @Query("SELECT * FROM cart_items")
    fun getCartItems(): Flow<List<CartItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(item: CartItem)

    @Query("UPDATE cart_items SET quantity = :quantity WHERE productId = :productId")
    suspend fun updateQuantity(productId: String, quantity: Int)

    @Query("UPDATE cart_items SET isSelected = :isSelected WHERE productId = :productId")
    suspend fun updateSelection(productId: String, isSelected: Boolean)

    @Query("UPDATE cart_items SET isSelected = :isSelected")
    suspend fun updateAllSelection(isSelected: Boolean)

    @Query("DELETE FROM cart_items WHERE productId = :productId")
    suspend fun deleteItem(productId: String)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()
}

@Dao
interface WishlistDao {
    @Query("SELECT * FROM wishlist_items")
    fun getWishlistItems(): Flow<List<WishlistItem>>

    @Query("SELECT EXISTS(SELECT 1 FROM wishlist_items WHERE productId = :productId)")
    fun isWishlisted(productId: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: WishlistItem)

    @Query("DELETE FROM wishlist_items WHERE productId = :productId")
    suspend fun delete(productId: String)
}

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders ORDER BY orderDate DESC")
    fun getAllOrders(): Flow<List<Order>>

    @Query("SELECT * FROM orders WHERE orderId = :orderId")
    fun getOrderById(orderId: String): Flow<Order?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(order: Order)

    @Query("UPDATE orders SET status = :status, trackingStep = :step WHERE orderId = :orderId")
    suspend fun updateTracking(orderId: String, status: String, step: Int)

    @Query("SELECT * FROM orders ORDER BY orderDate DESC")
    suspend fun getAllOrdersList(): List<Order>
}

@Dao
interface ReviewDao {
    @Query("SELECT * FROM reviews WHERE productId = :productId")
    fun getReviewsForProduct(productId: String): Flow<List<Review>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(review: Review)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(reviews: List<Review>)

    @Query("UPDATE reviews SET helpfulCount = helpfulCount + 1 WHERE id = :id")
    suspend fun upvoteHelpful(id: String)
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationItem>>

    @Query("SELECT COUNT(*) FROM notifications WHERE isRead = 0")
    fun getUnreadCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(notification: NotificationItem)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: String)

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllAsRead()

    @Query("DELETE FROM notifications")
    suspend fun clearAll()
}
