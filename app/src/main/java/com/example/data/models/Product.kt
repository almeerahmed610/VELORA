package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val category: String,
    val price: Double,
    val originalPrice: Double,
    val discountPercent: Int,
    val rating: Float,
    val reviewCount: Int,
    val stock: Int,
    val imageUrl: String,
    val brand: String,
    val isFreeShipping: Boolean = true,
    val isDarazMall: Boolean = false,
    val isFlashSale: Boolean = false,
    val flashSaleSoldPercent: Int = 0,
    val deliveryTimeDays: Int = 3,
    val warranty: String = "1 Year Brand Warranty"
)
