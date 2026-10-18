package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reviews")
data class Review(
    @PrimaryKey val id: String,
    val productId: String,
    val userName: String,
    val userAvatar: String = "",
    val rating: Int, // 1 to 5
    val dateString: String,
    val comment: String,
    val isVerifiedPurchase: Boolean = true,
    val helpfulCount: Int = 0,
    val reviewVariant: String = "Color: Black, Size: Standard"
)
