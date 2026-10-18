package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class OrderStatus(val displayName: String) {
    PLACED("Order Placed"),
    PAID("Payment Confirmed"),
    PROCESSING("Warehouse Packed"),
    SHIPPED("Shipped by Daraz Express"),
    OUT_FOR_DELIVERY("Out for Delivery"),
    DELIVERED("Delivered"),
    CANCELLED("Cancelled")
}

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey val orderId: String,
    val orderDate: Long,
    val itemsSummary: String,
    val totalAmount: Double,
    val discountAmount: Double,
    val shippingFee: Double,
    val paymentMethod: String,
    val status: String = OrderStatus.PLACED.name,
    val trackingStep: Int = 0, // 0 = Placed, 1 = Paid, 2 = Packed, 3 = Shipped, 4 = Out for delivery, 5 = Delivered
    val recipientName: String,
    val recipientPhone: String,
    val shippingAddress: String,
    val city: String,
    val riderName: String = "Tariq Mahmood (Daraz Rider)",
    val riderPhone: String = "0321-7654321",
    val estimatedDeliveryDate: String = "In 2-3 business days",
    val trackingNumber: String = "DEX-PK-" + (10000..99999).random()
)
