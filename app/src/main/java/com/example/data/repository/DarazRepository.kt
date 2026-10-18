package com.example.data.repository

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.content.FileProvider
import com.example.data.local.DarazDatabase
import com.example.data.models.CartItem
import com.example.data.models.NotificationItem
import com.example.data.models.NotificationType
import com.example.data.models.Order
import com.example.data.models.OrderStatus
import com.example.data.models.Product
import com.example.data.models.Review
import com.example.data.models.WishlistItem
import com.example.data.sample.InitialData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class DarazRepository(private val context: Context) {
    private val database = VeloraDatabase.getDatabase(context)
    private val productDao = database.productDao()
    private val cartDao = database.cartDao()
    private val wishlistDao = database.wishlistDao()
    private val orderDao = database.orderDao()
    private val reviewDao = database.reviewDao()
    private val notificationDao = database.notificationDao()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            seedDatabaseIfEmpty()
        }
    }

    private suspend fun seedDatabaseIfEmpty() {
        if (productDao.getCount() == 0) {
            productDao.insertAll(InitialData.sampleProducts)
            reviewDao.insertAll(InitialData.sampleReviews)
            for (notif in InitialData.sampleNotifications) {
                notificationDao.insert(notif)
            }
            // Seed a starter sample order so user can test tracking immediately
            val starterOrder = Order(
                orderId = "DZ-8492",
                orderDate = System.currentTimeMillis() - 1000 * 60 * 120, // 2 hours ago
                itemsSummary = "Xiaomi Redmi Note 13 (x1)",
                totalAmount = 49999.0,
                discountAmount = 500.0,
                shippingFee = 0.0,
                paymentMethod = "Easypaisa",
                status = OrderStatus.SHIPPED.name,
                trackingStep = 3,
                recipientName = "Muhammad Ali",
                recipientPhone = "+92 300 1234567",
                shippingAddress = "Flat 402, Al-Razi Heights, Block 6, Gulshan-e-Iqbal",
                city = "Karachi",
                riderName = "Tariq Mahmood (VELORA Rider)",
                riderPhone = "0321-7654321",
                trackingNumber = "VEX-PK-88492"
            )
            orderDao.insert(starterOrder)
        }
    }

    // Products
    fun getAllProducts(): Flow<List<Product>> = productDao.getAllProducts()
    fun getFlashSaleProducts(): Flow<List<Product>> = productDao.getFlashSaleProducts()
    fun getProductsByCategory(category: String): Flow<List<Product>> = productDao.getProductsByCategory(category)
    fun searchProducts(query: String): Flow<List<Product>> = productDao.searchProducts(query)
    suspend fun getProductById(id: String): Product? = productDao.getProductById(id)

    suspend fun addProduct(product: Product) = productDao.insert(product)
    suspend fun updateProduct(product: Product) = productDao.update(product)
    suspend fun deleteProduct(id: String) = productDao.deleteById(id)

    // Cart
    fun getCartItems(): Flow<List<CartItem>> = cartDao.getCartItems()

    suspend fun addToCart(product: Product, quantity: Int = 1) {
        val existing = cartDao.getCartItems() // flow
        cartDao.insertOrUpdate(
            CartItem(
                productId = product.id,
                title = product.title,
                price = product.price,
                originalPrice = product.originalPrice,
                imageUrl = product.imageUrl,
                quantity = quantity,
                isSelected = true
            )
        )
    }

    suspend fun updateCartQuantity(productId: String, quantity: Int) {
        if (quantity <= 0) {
            cartDao.deleteItem(productId)
        } else {
            cartDao.updateQuantity(productId, quantity)
        }
    }

    suspend fun toggleCartSelection(productId: String, isSelected: Boolean) {
        cartDao.updateSelection(productId, isSelected)
    }

    suspend fun selectAllCart(isSelected: Boolean) {
        cartDao.updateAllSelection(isSelected)
    }

    suspend fun removeCartItem(productId: String) {
        cartDao.deleteItem(productId)
    }

    suspend fun clearCart() {
        cartDao.clearCart()
    }

    // Wishlist
    fun getWishlistItems(): Flow<List<WishlistItem>> = wishlistDao.getWishlistItems()
    fun isWishlisted(productId: String): Flow<Boolean> = wishlistDao.isWishlisted(productId)

    suspend fun toggleWishlist(product: Product, isCurrentlyWishlisted: Boolean) {
        if (isCurrentlyWishlisted) {
            wishlistDao.delete(product.id)
        } else {
            wishlistDao.insert(
                WishlistItem(
                    productId = product.id,
                    title = product.title,
                    price = product.price,
                    originalPrice = product.originalPrice,
                    imageUrl = product.imageUrl,
                    rating = product.rating,
                    isFreeShipping = product.isFreeShipping
                )
            )
        }
    }

    // Orders & Tracking
    fun getAllOrders(): Flow<List<Order>> = orderDao.getAllOrders()
    fun getOrderById(orderId: String): Flow<Order?> = orderDao.getOrderById(orderId)

    suspend fun placeOrder(
        items: List<CartItem>,
        totalAmount: Double,
        discountAmount: Double,
        shippingFee: Double,
        paymentMethod: String,
        recipientName: String,
        recipientPhone: String,
        address: String,
        city: String
    ): Order {
        val orderId = "DZ-" + (1000..9999).random()
        val summary = items.joinToString(", ") { "${it.title} (x${it.quantity})" }

        val newOrder = Order(
            orderId = orderId,
            orderDate = System.currentTimeMillis(),
            itemsSummary = summary,
            totalAmount = totalAmount,
            discountAmount = discountAmount,
            shippingFee = shippingFee,
            paymentMethod = paymentMethod,
            status = OrderStatus.PLACED.name,
            trackingStep = 0,
            recipientName = recipientName,
            recipientPhone = recipientPhone,
            shippingAddress = address,
            city = city,
            riderName = "VELORA Express Rider",
            riderPhone = "0321-7654321",
            trackingNumber = "VEX-PK-" + (10000..99999).random()
        )

        orderDao.insert(newOrder)
        cartDao.clearCart()

        // Trigger in-app & push notification
        dispatchNotification(
            title = "🎉 Order Confirmed! #$orderId",
            message = "Your payment via $paymentMethod was successful. We are packing your order!",
            type = NotificationType.ORDER,
            refId = orderId
        )

        return newOrder
    }

    suspend fun updateTrackingStep(orderId: String, step: Int) {
        val status = when (step) {
            0 -> OrderStatus.PLACED.name
            1 -> OrderStatus.PAID.name
            2 -> OrderStatus.PROCESSING.name
            3 -> OrderStatus.SHIPPED.name
            4 -> OrderStatus.OUT_FOR_DELIVERY.name
            5 -> OrderStatus.DELIVERED.name
            else -> OrderStatus.DELIVERED.name
        }
        orderDao.updateTracking(orderId, status, step)

        val statusMsg = when (step) {
            1 -> "Payment verified for order #$orderId"
            2 -> "Warehouse has packed your package #$orderId"
            3 -> "Package #$orderId handed over to VELORA Express"
            4 -> "Rider is out for delivery with parcel #$orderId"
            5 -> "Package #$orderId has been delivered. Enjoy your purchase!"
            else -> "Status updated for #$orderId"
        }

        dispatchNotification(
            title = "📦 Order #$orderId Update",
            message = statusMsg,
            type = NotificationType.ORDER,
            refId = orderId
        )
    }

    // Reviews
    fun getReviews(productId: String): Flow<List<Review>> = reviewDao.getReviewsForProduct(productId)

    suspend fun addReview(
        productId: String,
        userName: String,
        rating: Int,
        comment: String
    ) {
        val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val review = Review(
            id = "rev_" + UUID.randomUUID().toString().take(6),
            productId = productId,
            userName = userName,
            rating = rating,
            dateString = formatter.format(Date()),
            comment = comment,
            isVerifiedPurchase = true,
            helpfulCount = 0
        )
        reviewDao.insert(review)
    }

    suspend fun upvoteReview(id: String) = reviewDao.upvoteHelpful(id)

    // Notifications
    fun getNotifications(): Flow<List<NotificationItem>> = notificationDao.getAllNotifications()
    fun getUnreadCount(): Flow<Int> = notificationDao.getUnreadCount()

    suspend fun markNotificationAsRead(id: String) = notificationDao.markAsRead(id)
    suspend fun markAllNotificationsAsRead() = notificationDao.markAllAsRead()

    suspend fun dispatchNotification(
        title: String,
        message: String,
        type: NotificationType,
        refId: String = ""
    ) {
        val notif = NotificationItem(
            id = "notif_" + System.currentTimeMillis(),
            title = title,
            message = message,
            timestamp = System.currentTimeMillis(),
            isRead = false,
            type = type,
            referenceId = refId
        )
        notificationDao.insert(notif)
        triggerHaptic()
    }

    private fun triggerHaptic() {
        try {
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(120, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(120)
            }
        } catch (_: Exception) {}
    }

    // CSV Export
    suspend fun exportOrdersCsv(): String {
        val orders = orderDao.getAllOrdersList()
        val sb = StringBuilder()
        sb.append("Order ID,Date,Recipient,City,Payment Method,Status,Tracking No,Total Amount (PKR),Discount,Shipping\n")
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        for (o in orders) {
            val dateStr = sdf.format(Date(o.orderDate))
            val sanitizedSummary = o.itemsSummary.replace(",", ";")
            sb.append("\"${o.orderId}\",\"$dateStr\",\"${o.recipientName}\",\"${o.city}\",\"${o.paymentMethod}\",\"${o.status}\",\"${o.trackingNumber}\",${o.totalAmount},${o.discountAmount},${o.shippingFee}\n")
        }
        return sb.toString()
    }
}
