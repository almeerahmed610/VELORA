package com.example.data.sample

import com.example.data.models.NotificationItem
import com.example.data.models.NotificationType
import com.example.data.models.Product
import com.example.data.models.Review
import com.example.data.models.Voucher

object InitialData {
    val vouchers = listOf(
        Voucher("VELORA10", 10, 1500.0, 500.0, "10% OFF on all items above Rs. 1,500"),
        Voucher("VELORA500", 25, 2000.0, 500.0, "Flat Rs. 500 OFF Welcome Voucher"),
        Voucher("DARAZ10", 10, 1500.0, 500.0, "10% OFF on all items above Rs. 1,500"),
        Voucher("FREESHIP", 100, 1000.0, 200.0, "Free delivery on orders above Rs. 1,000"),
        Voucher("MEGADEAL", 20, 5000.0, 1500.0, "Flat 20% OFF Mega Sale Voucher"),
        Voucher("WELCOME100", 15, 2000.0, 600.0, "New User Special: 15% OFF")
    )

    val sampleProducts = listOf(
        Product(
            id = "p_101",
            title = "Xiaomi Redmi Note 13 - 8GB RAM 256GB ROM (PTA Approved)",
            description = "AMOLED 120Hz display, 108MP triple camera, 5000mAh battery with 33W fast charging. Official 1-year brand warranty by Airlink.",
            category = "Mobiles & Tablets",
            price = 49999.0,
            originalPrice = 58999.0,
            discountPercent = 15,
            rating = 4.8f,
            reviewCount = 284,
            stock = 45,
            imageUrl = "https://images.unsplash.com/photo-1598327105666-5b89351aff97?w=600",
            brand = "Xiaomi",
            isFreeShipping = true,
            isDarazMall = true,
            isFlashSale = true,
            flashSaleSoldPercent = 78
        ),
        Product(
            id = "p_102",
            title = "Samsung Galaxy Buds 2 Pro Wireless Noise Cancelling Earbuds",
            description = "Studio quality 24-bit audio, active noise cancellation, 360 audio, IPX7 water resistance, seamless Galaxy connectivity.",
            category = "Electronics",
            price = 28500.0,
            originalPrice = 36000.0,
            discountPercent = 21,
            rating = 4.9f,
            reviewCount = 142,
            stock = 25,
            imageUrl = "https://images.unsplash.com/photo-1590658268037-6bf12165a8df?w=600",
            brand = "Samsung",
            isFreeShipping = true,
            isDarazMall = true,
            isFlashSale = true,
            flashSaleSoldPercent = 89
        ),
        Product(
            id = "p_103",
            title = "Men's Luxury Chronograph Quartz Watch - Stainless Steel Strap",
            description = "Waterproof sport quartz watch, date display, luminous hands, scratch resistant mineral glass with luxury presentation box.",
            category = "Fashion & Beauty",
            price = 3450.0,
            originalPrice = 6900.0,
            discountPercent = 50,
            rating = 4.6f,
            reviewCount = 512,
            stock = 80,
            imageUrl = "https://images.unsplash.com/photo-1524805444758-089113d48a6d?w=600",
            brand = "Curren",
            isFreeShipping = true,
            isDarazMall = false,
            isFlashSale = true,
            flashSaleSoldPercent = 64
        ),
        Product(
            id = "p_104",
            title = "Air Fryer 6.5L Digital Touchscreen with 8 Preset Cooking Modes",
            description = "360-degree rapid heat circulation, non-stick dishwasher-safe basket, 1800W, reduces fat by 85%. Ideal for crispy samosas and grilled chicken.",
            category = "Home & Living",
            price = 14800.0,
            originalPrice = 21500.0,
            discountPercent = 31,
            rating = 4.7f,
            reviewCount = 196,
            stock = 30,
            imageUrl = "https://images.unsplash.com/photo-1586201375761-83865001e31c?w=600",
            brand = "Dawlance",
            isFreeShipping = true,
            isDarazMall = true,
            isFlashSale = false,
            flashSaleSoldPercent = 42
        ),
        Product(
            id = "p_105",
            title = "D-Mart Premium Basmati Rice - 5kg Super Kernel Long Grain",
            description = "Aromatic aged Himalayan basmati rice, extra long grain, non-sticky cooking, vacuum packed for freshness guarantee.",
            category = "D-Mart / Groceries",
            price = 2450.0,
            originalPrice = 2999.0,
            discountPercent = 18,
            rating = 4.9f,
            reviewCount = 680,
            stock = 150,
            imageUrl = "https://images.unsplash.com/photo-1586201375761-83865001e31c?w=600",
            brand = "Guard",
            isFreeShipping = false,
            isDarazMall = true,
            isFlashSale = false
        ),
        Product(
            id = "p_106",
            title = "RGB Mechanical Gaming Keyboard - Blue Switches Anti-Ghosting",
            description = "Full 104 keys with dynamic RGB backlight patterns, detachable wrist rest, double-shot keycaps, aluminum frame.",
            category = "Electronics",
            price = 4800.0,
            originalPrice = 7500.0,
            discountPercent = 36,
            rating = 4.7f,
            reviewCount = 310,
            stock = 60,
            imageUrl = "https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=600",
            brand = "Redragon",
            isFreeShipping = true,
            isDarazMall = false,
            isFlashSale = true,
            flashSaleSoldPercent = 92
        ),
        Product(
            id = "p_107",
            title = "Women's Embroidered Lawn 3-Piece Stitched Suit (Summer Collection)",
            description = "Premium fine lawn fabric, digital printed chiffon dupatta, embroidered neckline and sleeves with dyed cambric trousers.",
            category = "Fashion & Beauty",
            price = 5950.0,
            originalPrice = 8500.0,
            discountPercent = 30,
            rating = 4.8f,
            reviewCount = 420,
            stock = 50,
            imageUrl = "https://images.unsplash.com/photo-1610030469983-98e550d6193c?w=600",
            brand = "Khaadi",
            isFreeShipping = true,
            isDarazMall = true,
            isFlashSale = false
        ),
        Product(
            id = "p_108",
            title = "Anker PowerCore 20000mAh Fast Charging Power Bank (22.5W)",
            description = "Dual USB-C and USB-A output, PowerIQ 3.0 technology, trickle-charging mode for earbuds, charges iPhone 15 up to 4.5 times.",
            category = "Mobiles & Tablets",
            price = 8200.0,
            originalPrice = 11000.0,
            discountPercent = 25,
            rating = 4.9f,
            reviewCount = 573,
            stock = 75,
            imageUrl = "https://images.unsplash.com/photo-1609592426868-80e9a8f278d9?w=600",
            brand = "Anker",
            isFreeShipping = true,
            isDarazMall = true,
            isFlashSale = true,
            flashSaleSoldPercent = 84
        )
    )

    val sampleReviews = listOf(
        Review(
            id = "rev_1",
            productId = "p_101",
            userName = "Hamza Farooq",
            rating = 5,
            dateString = "12 Oct 2026",
            comment = "100% genuine product! PTA approved message received immediately. Battery backup is very solid, camera quality is crisp. Daraz Express delivered in 2 days to Lahore.",
            isVerifiedPurchase = true,
            helpfulCount = 42
        ),
        Review(
            id = "rev_2",
            productId = "p_101",
            userName = "Zainab Bibi",
            rating = 5,
            dateString = "08 Oct 2026",
            comment = "Super fast delivery. Screen is bright and smooth. Packing was excellent with heavy bubble wrap.",
            isVerifiedPurchase = true,
            helpfulCount = 18
        ),
        Review(
            id = "rev_3",
            productId = "p_102",
            userName = "Bilal Siddiqui",
            rating = 5,
            dateString = "14 Oct 2026",
            comment = "Best earbuds at this price. Active Noise Cancellation works like magic on noisy roads. Bass is deep.",
            isVerifiedPurchase = true,
            helpfulCount = 29
        ),
        Review(
            id = "rev_4",
            productId = "p_103",
            userName = "Shahmeer Khan",
            rating = 4,
            dateString = "15 Oct 2026",
            comment = "Good build quality for the price. Looks very premium on wrist. Came in an original box.",
            isVerifiedPurchase = true,
            helpfulCount = 11
        )
    )

    val sampleNotifications = listOf(
        NotificationItem(
            id = "notif_1",
            title = "⚡ 11.11 Flash Sale is Live!",
            message = "Mega Discounts up to 70% OFF on Top Electronics, Mobiles & Fashion. Claim vouchers now!",
            timestamp = System.currentTimeMillis() - 1000 * 60 * 30, // 30 mins ago
            isRead = false,
            type = NotificationType.FLASH_SALE
        ),
        NotificationItem(
            id = "notif_2",
            title = "📦 Order #VL-8492 Shipped!",
            message = "Your VELORA Express parcel is on the way. Rider Tariq has been assigned for delivery.",
            timestamp = System.currentTimeMillis() - 1000 * 60 * 180, // 3 hours ago
            isRead = false,
            type = NotificationType.ORDER,
            referenceId = "VL-8492"
        ),
        NotificationItem(
            id = "notif_3",
            title = "💰 450 V-Coins Credited!",
            message = "You earned 450 V-Coins from your recent activity. Use them on your next checkout for Rs. 450 discount.",
            timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 24, // 1 day ago
            isRead = true,
            type = NotificationType.WALLET
        )
    )
}
