package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.models.CartItem

@Entity(tableName = "cached_cart_items")
data class CachedCartItemEntity(
    @PrimaryKey val productId: String,
    val title: String,
    val price: Double,
    val originalPrice: Double,
    val imageUrl: String,
    val quantity: Int,
    val isSelected: Boolean = true,
    val sellerName: String = "VELORA Verified Express",
    val addedAtTimestamp: Long = System.currentTimeMillis()
)

fun CartItem.toCachedEntity(): CachedCartItemEntity = CachedCartItemEntity(
    productId = productId,
    title = title,
    price = price,
    originalPrice = originalPrice,
    imageUrl = imageUrl,
    quantity = quantity,
    isSelected = isSelected,
    sellerName = sellerName
)

fun CachedCartItemEntity.toCartItem(): CartItem = CartItem(
    productId = productId,
    title = title,
    price = price,
    originalPrice = originalPrice,
    imageUrl = imageUrl,
    quantity = quantity,
    isSelected = isSelected,
    sellerName = sellerName
)
