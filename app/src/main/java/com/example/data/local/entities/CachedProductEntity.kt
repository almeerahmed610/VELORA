package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.models.Product

@Entity(tableName = "cached_products")
data class CachedProductEntity(
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
    val warranty: String = "1 Year Brand Warranty",
    val cachedAtTimestamp: Long = System.currentTimeMillis()
)

fun Product.toCachedEntity(): CachedProductEntity = CachedProductEntity(
    id = id,
    title = title,
    description = description,
    category = category,
    price = price,
    originalPrice = originalPrice,
    discountPercent = discountPercent,
    rating = rating,
    reviewCount = reviewCount,
    stock = stock,
    imageUrl = imageUrl,
    brand = brand,
    isFreeShipping = isFreeShipping,
    isDarazMall = isDarazMall,
    isFlashSale = isFlashSale,
    flashSaleSoldPercent = flashSaleSoldPercent,
    deliveryTimeDays = deliveryTimeDays,
    warranty = warranty
)

fun CachedProductEntity.toProduct(): Product = Product(
    id = id,
    title = title,
    description = description,
    category = category,
    price = price,
    originalPrice = originalPrice,
    discountPercent = discountPercent,
    rating = rating,
    reviewCount = reviewCount,
    stock = stock,
    imageUrl = imageUrl,
    brand = brand,
    isFreeShipping = isFreeShipping,
    isDarazMall = isDarazMall,
    isFlashSale = isFlashSale,
    flashSaleSoldPercent = flashSaleSoldPercent,
    deliveryTimeDays = deliveryTimeDays,
    warranty = warranty
)
