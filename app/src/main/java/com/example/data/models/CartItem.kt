package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItem(
    @PrimaryKey val productId: String,
    val title: String,
    val price: Double,
    val originalPrice: Double,
    val imageUrl: String,
    val quantity: Int,
    val isSelected: Boolean = true,
    val sellerName: String = "Daraz Verified Express"
)

@Entity(tableName = "wishlist_items")
data class WishlistItem(
    @PrimaryKey val productId: String,
    val title: String,
    val price: Double,
    val originalPrice: Double,
    val imageUrl: String,
    val rating: Float,
    val isFreeShipping: Boolean
)
