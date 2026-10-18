package com.example.data.firestore

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class FirestoreProduct(
    @DocumentId val id: String = "",
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val price: Double = 0.0,
    val originalPrice: Double = 0.0,
    val discountPercent: Int = 0,
    val rating: Float = 0f,
    val reviewCount: Int = 0,
    val stock: Int = 0,
    val imageUrl: String = "",
    val brand: String = "",
    val isFreeShipping: Boolean = true,
    val isDarazMall: Boolean = false,
    val isFlashSale: Boolean = false,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class FirestoreOrder(
    @DocumentId val orderId: String = "",
    val userId: String = "",
    val orderDate: Long = 0L,
    val itemsSummary: String = "",
    val totalAmount: Double = 0.0,
    val discountAmount: Double = 0.0,
    val shippingFee: Double = 0.0,
    val paymentMethod: String = "",
    val status: String = "PLACED",
    val trackingStep: Int = 0,
    val recipientName: String = "",
    val recipientPhone: String = "",
    val shippingAddress: String = "",
    val city: String = "",
    val riderName: String = "VELORA Express Rider",
    val riderPhone: String = "0321-7654321",
    val trackingNumber: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class FirestoreReview(
    @DocumentId val id: String = "",
    val productId: String = "",
    val userId: String = "",
    val userName: String = "",
    val rating: Int = 5,
    val comment: String = "",
    val isVerifiedPurchase: Boolean = true,
    val helpfulCount: Int = 0,
    @ServerTimestamp val createdAt: Timestamp? = null
)
