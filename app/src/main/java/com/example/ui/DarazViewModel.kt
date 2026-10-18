package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.models.CartItem
import com.example.data.models.NotificationItem
import com.example.data.models.NotificationType
import com.example.data.models.Order
import com.example.data.models.OrderStatus
import com.example.data.models.Product
import com.example.data.models.Review
import com.example.data.models.UserAddress
import com.example.data.models.UserProfile
import com.example.data.models.Voucher
import com.example.data.models.WishlistItem
import com.example.data.repository.DarazRepository
import com.example.data.sample.InitialData
import com.example.util.AppLanguage
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class Screen {
    HOME,
    CATEGORIES,
    PRODUCT_DETAIL,
    CART,
    CHECKOUT,
    ORDER_TRACKING,
    ADMIN_PANEL,
    NOTIFICATIONS,
    WISHLIST,
    ACCOUNT
}

class DarazViewModel(application: Application) : AndroidViewModel(application) {
    val repository = DarazRepository(application)

    // Navigation Backstack
    private val _screenStack = MutableStateFlow(listOf(Screen.HOME))
    val currentScreen: StateFlow<Screen> = MutableStateFlow(Screen.HOME)

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _selectedProductId = MutableStateFlow<String?>(null)
    val selectedProductId: StateFlow<String?> = _selectedProductId.asStateFlow()

    private val _selectedOrderId = MutableStateFlow<String?>("DZ-8492")
    val selectedOrderId: StateFlow<String?> = _selectedOrderId.asStateFlow()

    // Settings
    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _currentLanguage = MutableStateFlow(AppLanguage.ENGLISH)
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    // Push notification banner state (in-app heads up alert)
    private val _activePushAlert = MutableStateFlow<NotificationItem?>(null)
    val activePushAlert: StateFlow<NotificationItem?> = _activePushAlert.asStateFlow()

    // User Profile & Auth
    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(true)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    // Products & Filtering
    val allProducts: StateFlow<List<Product>> = repository.getAllProducts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val flashSaleProducts: StateFlow<List<Product>> = repository.getFlashSaleProducts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _sortBy = MutableStateFlow("Recommended") // Recommended, PriceLow, PriceHigh, Rating
    val sortBy: StateFlow<String> = _sortBy.asStateFlow()

    // Filtered / Displayed products
    val displayedProducts: StateFlow<List<Product>> = combine(
        allProducts,
        _selectedCategory,
        _searchQuery,
        _sortBy
    ) { products, category, query, sort ->
        var list = products
        if (category != "All") {
            list = list.filter { it.category.equals(category, ignoreCase = true) }
        }
        if (query.isNotBlank()) {
            list = list.filter {
                it.title.contains(query, ignoreCase = true) ||
                it.brand.contains(query, ignoreCase = true) ||
                it.category.contains(query, ignoreCase = true)
            }
        }
        when (sort) {
            "PriceLow" -> list.sortedBy { it.price }
            "PriceHigh" -> list.sortedByDescending { it.price }
            "Rating" -> list.sortedByDescending { it.rating }
            else -> list
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Cart
    val cartItems: StateFlow<List<CartItem>> = repository.getCartItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Wishlist
    val wishlistItems: StateFlow<List<WishlistItem>> = repository.getWishlistItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Orders
    val orders: StateFlow<List<Order>> = repository.getAllOrders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Notifications
    val notifications: StateFlow<List<NotificationItem>> = repository.getNotifications()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadNotifCount: StateFlow<Int> = repository.getUnreadCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Current Product for detail screen
    val currentProduct: StateFlow<Product?> = _selectedProductId.flatMapLatest { id ->
        if (id == null) flowOf(null)
        else combine(allProducts) { prods -> prods[0].find { it.id == id } }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Current Product Reviews
    val currentReviews: StateFlow<List<Review>> = _selectedProductId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList())
        else repository.getReviews(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Current Tracking Order
    val currentTrackingOrder: StateFlow<Order?> = _selectedOrderId.flatMapLatest { id ->
        if (id == null) flowOf(null)
        else repository.getOrderById(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Flash sale countdown timer (e.g. 05h 42m 19s)
    private val _flashCountdown = MutableStateFlow("04:32:18")
    val flashCountdown: StateFlow<String> = _flashCountdown.asStateFlow()

    // Vouchers & Checkout
    private val _appliedVoucher = MutableStateFlow<Voucher?>(null)
    val appliedVoucher: StateFlow<Voucher?> = _appliedVoucher.asStateFlow()

    private val _voucherMessage = MutableStateFlow<String?>(null)
    val voucherMessage: StateFlow<String?> = _voucherMessage.asStateFlow()

    private val _selectedPaymentMethod = MutableStateFlow("Easypaisa")
    val selectedPaymentMethod: StateFlow<String> = _selectedPaymentMethod.asStateFlow()

    private val _selectedAddressIndex = MutableStateFlow(0)
    val selectedAddressIndex: StateFlow<Int> = _selectedAddressIndex.asStateFlow()

    private val _isPaymentProcessing = MutableStateFlow(false)
    val isPaymentProcessing: StateFlow<Boolean> = _isPaymentProcessing.asStateFlow()

    private val _lastPlacedOrder = MutableStateFlow<Order?>(null)
    val lastPlacedOrder: StateFlow<Order?> = _lastPlacedOrder.asStateFlow()

    // CSV preview state
    private val _csvPreviewContent = MutableStateFlow<String?>(null)
    val csvPreviewContent: StateFlow<String?> = _csvPreviewContent.asStateFlow()

    init {
        startFlashCountdownTimer()
    }

    private fun startFlashCountdownTimer() {
        viewModelScope.launch {
            var secondsRemaining = 4 * 3600 + 32 * 60 + 18
            while (isActive) {
                val h = secondsRemaining / 3600
                val m = (secondsRemaining % 3600) / 60
                val s = secondsRemaining % 60
                _flashCountdown.value = String.format("%02d:%02d:%02d", h, m, s)
                delay(1000)
                if (secondsRemaining > 0) secondsRemaining--
                else secondsRemaining = 24 * 3600
            }
        }
    }

    // Navigation Methods
    fun navigateTo(screen: Screen) {
        val stack = _screenStack.value.toMutableList()
        stack.add(screen)
        _screenStack.value = stack
        (currentScreen as MutableStateFlow).value = screen
    }

    fun navigateBack(): Boolean {
        val stack = _screenStack.value.toMutableList()
        if (stack.size > 1) {
            stack.removeAt(stack.size - 1)
            _screenStack.value = stack
            (currentScreen as MutableStateFlow).value = stack.last()
            return true
        }
        return false
    }

    fun setBottomTab(index: Int) {
        _selectedTab.value = index
        when (index) {
            0 -> navigateTo(Screen.HOME)
            1 -> navigateTo(Screen.CATEGORIES)
            2 -> navigateTo(Screen.NOTIFICATIONS)
            3 -> navigateTo(Screen.CART)
            4 -> navigateTo(Screen.ACCOUNT)
        }
    }

    fun openProduct(productId: String) {
        _selectedProductId.value = productId
        navigateTo(Screen.PRODUCT_DETAIL)
    }

    fun openOrderTracking(orderId: String) {
        _selectedOrderId.value = orderId
        navigateTo(Screen.ORDER_TRACKING)
    }

    // Settings
    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    fun setLanguage(language: AppLanguage) {
        _currentLanguage.value = language
    }

    fun dismissPushAlert() {
        _activePushAlert.value = null
    }

    fun showPushNotification(title: String, message: String, type: NotificationType = NotificationType.PROMO) {
        viewModelScope.launch {
            repository.dispatchNotification(title, message, type)
            _activePushAlert.value = NotificationItem(
                id = "alert_${System.currentTimeMillis()}",
                title = title,
                message = message,
                timestamp = System.currentTimeMillis(),
                type = type
            )
            delay(4000)
            if (_activePushAlert.value?.title == title) {
                _activePushAlert.value = null
            }
        }
    }

    // Search and Category
    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onCategorySelect(category: String) {
        _selectedCategory.value = category
    }

    fun onSortChange(sort: String) {
        _sortBy.value = sort
    }

    // Cart Actions
    fun addToCart(product: Product, quantity: Int = 1) {
        viewModelScope.launch {
            repository.addToCart(product, quantity)
            showPushNotification("🛒 Added to Cart", "${product.title.take(30)}... added to your VELORA cart!")
        }
    }

    fun updateCartQuantity(productId: String, quantity: Int) {
        viewModelScope.launch {
            repository.updateCartQuantity(productId, quantity)
        }
    }

    fun toggleCartItemSelection(productId: String, isSelected: Boolean) {
        viewModelScope.launch {
            repository.toggleCartSelection(productId, isSelected)
        }
    }

    fun toggleSelectAllCart(isSelected: Boolean) {
        viewModelScope.launch {
            repository.selectAllCart(isSelected)
        }
    }

    fun removeCartItem(productId: String) {
        viewModelScope.launch {
            repository.removeCartItem(productId)
        }
    }

    // Wishlist Actions
    fun toggleWishlist(product: Product) {
        viewModelScope.launch {
            val isCurrentlyWishlisted = wishlistItems.value.any { it.productId == product.id }
            repository.toggleWishlist(product, isCurrentlyWishlisted)
            val msg = if (isCurrentlyWishlisted) "Removed from Wishlist" else "❤️ Saved to your Wishlist"
            showPushNotification(msg, product.title.take(30))
        }
    }

    // Voucher
    fun applyVoucher(code: String) {
        val found = InitialData.vouchers.find { it.code.equals(code.trim(), ignoreCase = true) }
        if (found != null) {
            _appliedVoucher.value = found
            _voucherMessage.value = "✓ Voucher applied: ${found.description}"
        } else {
            _voucherMessage.value = "✗ Invalid voucher code. Try 'DARAZ10' or 'FREESHIP'"
        }
    }

    fun removeVoucher() {
        _appliedVoucher.value = null
        _voucherMessage.value = null
    }

    // Checkout & Payment
    fun selectPaymentMethod(method: String) {
        _selectedPaymentMethod.value = method
    }

    fun selectAddress(index: Int) {
        _selectedAddressIndex.value = index
    }

    fun proceedToPaymentAndOrder() {
        viewModelScope.launch {
            val selectedItems = cartItems.value.filter { it.isSelected }
            if (selectedItems.isEmpty()) return@launch

            _isPaymentProcessing.value = true
            delay(1500) // Simulate secure 256-bit SSL gateway handshake

            val subtotal = selectedItems.sumOf { it.price * it.quantity }
            val voucher = _appliedVoucher.value
            val discount = if (voucher != null) (subtotal * voucher.discountPercent / 100.0).coerceAtMost(voucher.maxDiscount) else 0.0
            val shipping = if (subtotal > 2000 || voucher?.code == "FREESHIP") 0.0 else 199.0
            val total = (subtotal - discount + shipping).coerceAtLeast(0.0)

            val address = _userProfile.value.savedAddresses.getOrNull(_selectedAddressIndex.value)
                ?: _userProfile.value.savedAddresses.first()

            val order = repository.placeOrder(
                items = selectedItems,
                totalAmount = total,
                discountAmount = discount,
                shippingFee = shipping,
                paymentMethod = _selectedPaymentMethod.value,
                recipientName = address.fullName,
                recipientPhone = address.phone,
                address = address.address,
                city = address.city
            )

            _lastPlacedOrder.value = order
            _selectedOrderId.value = order.orderId
            _isPaymentProcessing.value = false
            _appliedVoucher.value = null
            _voucherMessage.value = null
            navigateTo(Screen.ORDER_TRACKING)
        }
    }

    // Order Tracking Live Simulation
    fun advanceTrackingStep(orderId: String) {
        viewModelScope.launch {
            val current = currentTrackingOrder.value ?: return@launch
            val nextStep = (current.trackingStep + 1).coerceAtMost(5)
            repository.updateTrackingStep(orderId, nextStep)
        }
    }

    // Reviews
    fun submitReview(productId: String, rating: Int, comment: String) {
        viewModelScope.launch {
            repository.addReview(productId, _userProfile.value.name, rating, comment)
            showPushNotification("⭐ Review Submitted", "Thank you for rating this product!")
        }
    }

    fun upvoteReview(reviewId: String) {
        viewModelScope.launch {
            repository.upvoteReview(reviewId)
        }
    }

    // Notifications
    fun markNotificationAsRead(id: String) {
        viewModelScope.launch {
            repository.markNotificationAsRead(id)
        }
    }

    fun markAllNotificationsAsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsAsRead()
        }
    }

    // Admin & Analytics
    fun addAdminProduct(
        title: String,
        category: String,
        price: Double,
        originalPrice: Double,
        stock: Int,
        brand: String,
        isFlashSale: Boolean
    ) {
        viewModelScope.launch {
            val newP = Product(
                id = "p_" + System.currentTimeMillis().toString().takeLast(5),
                title = title,
                description = "Authentic premium product listed via VELORA Seller Central.",
                category = category,
                price = price,
                originalPrice = originalPrice,
                discountPercent = if (originalPrice > price) (((originalPrice - price) / originalPrice) * 100).toInt() else 0,
                rating = 5.0f,
                reviewCount = 0,
                stock = stock,
                imageUrl = "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=600",
                brand = brand,
                isFreeShipping = true,
                isDarazMall = true,
                isFlashSale = isFlashSale
            )
            repository.addProduct(newP)
            showPushNotification("🛍️ Seller Center", "New product '$title' has been listed successfully!")
        }
    }

    fun deleteAdminProduct(productId: String) {
        viewModelScope.launch {
            repository.deleteProduct(productId)
        }
    }

    fun generateOrdersCsv() {
        viewModelScope.launch {
            val csv = repository.exportOrdersCsv()
            _csvPreviewContent.value = csv
            showPushNotification("📊 CSV Export Ready", "Orders report generated successfully. Ready to view or share.")
        }
    }

    fun dismissCsvPreview() {
        _csvPreviewContent.value = null
    }

    // Social Media / Fast Login
    fun simulateSocialLogin(provider: String) {
        viewModelScope.launch {
            _userProfile.value = UserProfile(
                name = if (provider == "Google") "Rashid Ali (Google)" else "Rashid Ali ($provider)",
                email = "rashidalikhushk786@gmail.com",
                phone = "+92 301 2345678",
                dCoins = 850
            )
            _isLoggedIn.value = true
            showPushNotification("✅ Signed in via $provider", "Welcome back, Rashid Ali! 850 D-Coins available.")
        }
    }
}
