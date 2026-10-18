package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class NotificationType {
    ORDER,
    FLASH_SALE,
    PROMO,
    WALLET
}

@Entity(tableName = "notifications")
data class NotificationItem(
    @PrimaryKey val id: String,
    val title: String,
    val message: String,
    val timestamp: Long,
    val isRead: Boolean = false,
    val type: NotificationType = NotificationType.ORDER,
    val referenceId: String = "" // e.g. orderId or productId
)
