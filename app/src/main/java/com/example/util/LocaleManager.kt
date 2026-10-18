package com.example.util

enum class AppLanguage(val code: String, val title: String, val flag: String) {
    ENGLISH("en", "English", "🇬🇧"),
    URDU("ur", "اردو", "🇵🇰"),
    ROMAN_URDU("ru", "Roman Urdu", "🗣️")
}

object AppStrings {
    fun get(key: String, lang: AppLanguage): String {
        return strings[key]?.get(lang) ?: strings[key]?.get(AppLanguage.ENGLISH) ?: key
    }

    private val strings = mapOf(
        "app_name" to mapOf(
            AppLanguage.ENGLISH to "VELORA",
            AppLanguage.URDU to "ویلورا",
            AppLanguage.ROMAN_URDU to "VELORA"
        ),
        "search_hint" to mapOf(
            AppLanguage.ENGLISH to "Search in VELORA (e.g. Redmi, Watch, Shoes)...",
            AppLanguage.URDU to "ویلورا میں تلاش کریں (مثلاً ریڈمی، گھڑی)...",
            AppLanguage.ROMAN_URDU to "VELORA mein talaash karein..."
        ),
        "flash_sale" to mapOf(
            AppLanguage.ENGLISH to "Flash Sale",
            AppLanguage.URDU to "فلیش سیل",
            AppLanguage.ROMAN_URDU to "Flash Sale"
        ),
        "categories" to mapOf(
            AppLanguage.ENGLISH to "Categories",
            AppLanguage.URDU to "اقسام",
            AppLanguage.ROMAN_URDU to "Categories"
        ),
        "just_for_you" to mapOf(
            AppLanguage.ENGLISH to "Just For You",
            AppLanguage.URDU to "خاص آپ کے لیے",
            AppLanguage.ROMAN_URDU to "Khaas Aap Ke Liye"
        ),
        "cart" to mapOf(
            AppLanguage.ENGLISH to "Cart",
            AppLanguage.URDU to "کارٹ",
            AppLanguage.ROMAN_URDU to "Shopping Cart"
        ),
        "account" to mapOf(
            AppLanguage.ENGLISH to "Account",
            AppLanguage.URDU to "اکاؤنٹ",
            AppLanguage.ROMAN_URDU to "Mera Account"
        ),
        "home" to mapOf(
            AppLanguage.ENGLISH to "Home",
            AppLanguage.URDU to "ہوم",
            AppLanguage.ROMAN_URDU to "Home"
        ),
        "notifications" to mapOf(
            AppLanguage.ENGLISH to "Notifications",
            AppLanguage.URDU to "پیغامات",
            AppLanguage.ROMAN_URDU to "Notifications"
        ),
        "add_to_cart" to mapOf(
            AppLanguage.ENGLISH to "Add to Cart",
            AppLanguage.URDU to "کارٹ میں شامل کریں",
            AppLanguage.ROMAN_URDU to "Cart Mein Daalein"
        ),
        "buy_now" to mapOf(
            AppLanguage.ENGLISH to "Buy Now",
            AppLanguage.URDU to "ابھی خریدیں",
            AppLanguage.ROMAN_URDU to "Abhi Khareedein"
        ),
        "free_delivery" to mapOf(
            AppLanguage.ENGLISH to "Free Delivery",
            AppLanguage.URDU to "مفت ترسیل",
            AppLanguage.ROMAN_URDU to "Free Delivery"
        ),
        "daraz_mall" to mapOf(
            AppLanguage.ENGLISH to "VeloraMall 100% Authentic",
            AppLanguage.URDU to "ویلورا مال - سو فیصد اصلی",
            AppLanguage.ROMAN_URDU to "VeloraMall 100% Asli"
        ),
        "reviews" to mapOf(
            AppLanguage.ENGLISH to "Customer Reviews",
            AppLanguage.URDU to "گاہکوں کی رائے",
            AppLanguage.ROMAN_URDU to "Customer Reviews"
        ),
        "write_review" to mapOf(
            AppLanguage.ENGLISH to "Write a Review",
            AppLanguage.URDU to "اپنی رائے دیں",
            AppLanguage.ROMAN_URDU to "Review Likhein"
        ),
        "checkout" to mapOf(
            AppLanguage.ENGLISH to "Proceed to Checkout",
            AppLanguage.URDU to "چیک آؤٹ پر جائیں",
            AppLanguage.ROMAN_URDU to "Checkout Karein"
        ),
        "place_order" to mapOf(
            AppLanguage.ENGLISH to "Confirm & Place Order",
            AppLanguage.URDU to "آرڈر مکمل کریں",
            AppLanguage.ROMAN_URDU to "Order Confirm Karein"
        ),
        "track_order" to mapOf(
            AppLanguage.ENGLISH to "Track Order",
            AppLanguage.URDU to "آرڈر ٹریک کریں",
            AppLanguage.ROMAN_URDU to "Order Track Karein"
        ),
        "admin_panel" to mapOf(
            AppLanguage.ENGLISH to "VELORA Seller & Admin Dashboard",
            AppLanguage.URDU to "ویلورا ایڈمن ڈیش بورڈ",
            AppLanguage.ROMAN_URDU to "VELORA Admin / Seller Panel"
        ),
        "wishlist" to mapOf(
            AppLanguage.ENGLISH to "Wishlist",
            AppLanguage.URDU to "خواہشات کی فہرست",
            AppLanguage.ROMAN_URDU to "Wishlist"
        ),
        "offline_ready" to mapOf(
            AppLanguage.ENGLISH to "Offline Access Active",
            AppLanguage.URDU to "آف لائن رسائی فعال ہے",
            AppLanguage.ROMAN_URDU to "Offline Access Active Hai"
        ),
        "export_csv" to mapOf(
            AppLanguage.ENGLISH to "Export Orders CSV",
            AppLanguage.URDU to "آرڈرز کی سی ایس وی ایکسپورٹ",
            AppLanguage.ROMAN_URDU to "Orders CSV Export Karein"
        )
    )
}
