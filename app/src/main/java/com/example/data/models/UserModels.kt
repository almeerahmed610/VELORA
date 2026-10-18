package com.example.data.models

data class UserProfile(
    val id: String = "u_daraz_8912",
    val name: String = "Muhammad Ali",
    val email: String = "ali.daraz@example.pk",
    val phone: String = "+92 300 1234567",
    val avatarUrl: String = "",
    val dCoins: Int = 450, // 450 Coins = Rs. 450
    val savedAddresses: List<UserAddress> = listOf(
        UserAddress(
            id = "addr_1",
            label = "Home",
            fullName = "Muhammad Ali",
            phone = "+92 300 1234567",
            address = "Flat 402, Al-Razi Heights, Block 6, Gulshan-e-Iqbal",
            city = "Karachi",
            province = "Sindh",
            isDefault = true
        ),
        UserAddress(
            id = "addr_2",
            label = "Office",
            fullName = "Muhammad Ali (Work)",
            phone = "+92 321 9876543",
            address = "Floor 5, Software Technology Park, Constitution Ave",
            city = "Islamabad",
            province = "Federal",
            isDefault = false
        )
    )
)

data class UserAddress(
    val id: String,
    val label: String,
    val fullName: String,
    val phone: String,
    val address: String,
    val city: String,
    val province: String,
    val isDefault: Boolean
)

data class Voucher(
    val code: String,
    val discountPercent: Int,
    val minSpend: Double,
    val maxDiscount: Double,
    val description: String
)
