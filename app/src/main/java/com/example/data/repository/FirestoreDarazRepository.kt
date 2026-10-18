package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.firestore.FirestoreOrder
import com.example.data.firestore.FirestoreProduct
import com.example.data.firestore.FirestoreReview
import com.example.data.firestore.OperationType
import com.example.data.firestore.handleFirestoreError
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FirestoreDarazRepository(private val db: FirebaseFirestore) {

    // Secondary constructor that resolves the named database ID from string resources
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val auth = Firebase.auth

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be authenticated before performing this operation.")
    }

    // ==========================================
    // 1. PRODUCT CRUD OPERATIONS
    // ==========================================

    /**
     * CREATE Product
     */
    suspend fun createProduct(product: FirestoreProduct): Result<String> {
        val path = "products"
        return try {
            val docRef = if (product.id.isNotBlank()) {
                db.collection(path).document(product.id)
            } else {
                db.collection(path).document()
            }

            val payload = hashMapOf(
                "title" to product.title,
                "description" to product.description,
                "category" to product.category,
                "price" to product.price,
                "originalPrice" to product.originalPrice,
                "discountPercent" to product.discountPercent,
                "rating" to product.rating,
                "reviewCount" to product.reviewCount,
                "stock" to product.stock,
                "imageUrl" to product.imageUrl,
                "brand" to product.brand,
                "isFreeShipping" to product.isFreeShipping,
                "isDarazMall" to product.isDarazMall,
                "isFlashSale" to product.isFlashSale,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )

            docRef.set(payload).await()
            Result.success(docRef.id)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    /**
     * READ single Product
     */
    suspend fun getProduct(productId: String): Result<FirestoreProduct?> {
        val path = "products/$productId"
        return try {
            val doc = db.collection("products").document(productId).get().await()
            val product = doc.toObject(FirestoreProduct::class.java)
            Result.success(product)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, path)
            Result.failure(e)
        }
    }

    /**
     * READ real-time Products stream
     */
    fun observeProducts(): Flow<List<FirestoreProduct>> {
        val path = "products"
        return db.collection(path)
            .snapshots()
            .map { snapshot -> snapshot.toObjects(FirestoreProduct::class.java) }
            .catch { error ->
                if (error is Exception) {
                    handleFirestoreError(error, OperationType.LIST, path)
                }
                throw error
            }
    }

    /**
     * READ real-time Flash Sale Products stream
     */
    fun observeFlashSaleProducts(): Flow<List<FirestoreProduct>> {
        val path = "products"
        return db.collection(path)
            .whereEqualTo("isFlashSale", true)
            .snapshots()
            .map { snapshot -> snapshot.toObjects(FirestoreProduct::class.java) }
            .catch { error ->
                if (error is Exception) {
                    handleFirestoreError(error, OperationType.LIST, path)
                }
                throw error
            }
    }

    /**
     * UPDATE Product
     */
    suspend fun updateProduct(product: FirestoreProduct): Result<Unit> {
        require(product.id.isNotBlank()) { "Product ID cannot be blank for update" }
        val path = "products/${product.id}"
        return try {
            val docRef = db.collection("products").document(product.id)
            val updates = mapOf(
                "title" to product.title,
                "description" to product.description,
                "category" to product.category,
                "price" to product.price,
                "originalPrice" to product.originalPrice,
                "discountPercent" to product.discountPercent,
                "stock" to product.stock,
                "brand" to product.brand,
                "isFreeShipping" to product.isFreeShipping,
                "isDarazMall" to product.isDarazMall,
                "isFlashSale" to product.isFlashSale,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.update(updates).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    /**
     * DELETE Product
     */
    suspend fun deleteProduct(productId: String): Result<Unit> {
        val path = "products/$productId"
        return try {
            db.collection("products").document(productId).delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, path)
            Result.failure(e)
        }
    }

    // ==========================================
    // 2. ORDER CRUD OPERATIONS
    // ==========================================

    /**
     * CREATE Order for authenticated user
     */
    suspend fun createOrder(order: FirestoreOrder): Result<String> {
        val uid = if (order.userId.isNotBlank()) order.userId else requireUserId()
        val path = "users/$uid/orders"
        return try {
            val docRef = if (order.orderId.isNotBlank()) {
                db.collection("users").document(uid).collection("orders").document(order.orderId)
            } else {
                db.collection("users").document(uid).collection("orders").document()
            }

            val payload = hashMapOf(
                "userId" to uid,
                "orderDate" to if (order.orderDate > 0) order.orderDate else System.currentTimeMillis(),
                "itemsSummary" to order.itemsSummary,
                "totalAmount" to order.totalAmount,
                "discountAmount" to order.discountAmount,
                "shippingFee" to order.shippingFee,
                "paymentMethod" to order.paymentMethod,
                "status" to order.status,
                "trackingStep" to order.trackingStep,
                "recipientName" to order.recipientName,
                "recipientPhone" to order.recipientPhone,
                "shippingAddress" to order.shippingAddress,
                "city" to order.city,
                "riderName" to order.riderName,
                "riderPhone" to order.riderPhone,
                "trackingNumber" to order.trackingNumber,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )

            docRef.set(payload).await()
            Result.success(docRef.id)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    /**
     * READ single Order
     */
    suspend fun getOrder(userId: String, orderId: String): Result<FirestoreOrder?> {
        val path = "users/$userId/orders/$orderId"
        return try {
            val doc = db.collection("users").document(userId).collection("orders").document(orderId).get().await()
            val order = doc.toObject(FirestoreOrder::class.java)
            Result.success(order)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, path)
            Result.failure(e)
        }
    }

    /**
     * READ real-time Orders stream for a user
     */
    fun observeUserOrders(userId: String): Flow<List<FirestoreOrder>> {
        val path = "users/$userId/orders"
        return db.collection("users").document(userId).collection("orders")
            .snapshots()
            .map { snapshot -> snapshot.toObjects(FirestoreOrder::class.java) }
            .catch { error ->
                if (error is Exception) {
                    handleFirestoreError(error, OperationType.LIST, path)
                }
                throw error
            }
    }

    /**
     * UPDATE Order tracking status
     */
    suspend fun updateOrderStatus(userId: String, orderId: String, status: String, trackingStep: Int): Result<Unit> {
        val path = "users/$userId/orders/$orderId"
        return try {
            val docRef = db.collection("users").document(userId).collection("orders").document(orderId)
            val updates = mapOf(
                "status" to status,
                "trackingStep" to trackingStep,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.update(updates).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    /**
     * DELETE / Cancel Order
     */
    suspend fun deleteOrder(userId: String, orderId: String): Result<Unit> {
        val path = "users/$userId/orders/$orderId"
        return try {
            db.collection("users").document(userId).collection("orders").document(orderId).delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, path)
            Result.failure(e)
        }
    }

    // ==========================================
    // 3. USER REVIEWS CRUD OPERATIONS
    // ==========================================

    /**
     * CREATE Review under product
     */
    suspend fun createReview(review: FirestoreReview): Result<String> {
        require(review.productId.isNotBlank()) { "Product ID cannot be blank for review" }
        val uid = if (review.userId.isNotBlank()) review.userId else requireUserId()
        val path = "products/${review.productId}/reviews"
        return try {
            val docRef = if (review.id.isNotBlank()) {
                db.collection("products").document(review.productId).collection("reviews").document(review.id)
            } else {
                db.collection("products").document(review.productId).collection("reviews").document()
            }

            val payload = hashMapOf(
                "productId" to review.productId,
                "userId" to uid,
                "userName" to review.userName,
                "rating" to review.rating,
                "comment" to review.comment,
                "isVerifiedPurchase" to review.isVerifiedPurchase,
                "helpfulCount" to review.helpfulCount,
                "createdAt" to FieldValue.serverTimestamp()
            )

            docRef.set(payload).await()
            Result.success(docRef.id)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    /**
     * READ real-time Product Reviews stream
     */
    fun observeProductReviews(productId: String): Flow<List<FirestoreReview>> {
        val path = "products/$productId/reviews"
        return db.collection("products").document(productId).collection("reviews")
            .snapshots()
            .map { snapshot -> snapshot.toObjects(FirestoreReview::class.java) }
            .catch { error ->
                if (error is Exception) {
                    handleFirestoreError(error, OperationType.LIST, path)
                }
                throw error
            }
    }

    /**
     * UPDATE Review comment and rating
     */
    suspend fun updateReview(review: FirestoreReview): Result<Unit> {
        require(review.productId.isNotBlank()) { "Product ID cannot be blank" }
        require(review.id.isNotBlank()) { "Review ID cannot be blank" }
        val path = "products/${review.productId}/reviews/${review.id}"
        return try {
            val docRef = db.collection("products").document(review.productId).collection("reviews").document(review.id)
            val updates = mapOf(
                "rating" to review.rating,
                "comment" to review.comment
            )
            docRef.update(updates).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    /**
     * UPVOTE Review (Helpful vote)
     */
    suspend fun upvoteReview(productId: String, reviewId: String): Result<Unit> {
        val path = "products/$productId/reviews/$reviewId"
        return try {
            val docRef = db.collection("products").document(productId).collection("reviews").document(reviewId)
            docRef.update("helpfulCount", FieldValue.increment(1)).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    /**
     * DELETE Review
     */
    suspend fun deleteReview(productId: String, reviewId: String): Result<Unit> {
        val path = "products/$productId/reviews/$reviewId"
        return try {
            db.collection("products").document(productId).collection("reviews").document(reviewId).delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, path)
            Result.failure(e)
        }
    }
}
