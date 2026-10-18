package com.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.models.Product
import com.example.ui.DarazViewModel
import com.example.ui.Screen
import com.example.ui.components.DarazTopAppBar
import com.example.ui.components.PushNotificationBanner
import com.example.ui.screens.*
import com.example.ui.theme.DarazOrange
import com.example.ui.theme.MyApplicationTheme
import com.example.util.AppLanguage
import com.example.util.AppStrings

class MainActivity : ComponentActivity() {
    private val viewModel: DarazViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
            val currentLanguage by viewModel.currentLanguage.collectAsStateWithLifecycle()

            MyApplicationTheme(darkTheme = isDarkMode) {
                DarazAppContent(
                    viewModel = viewModel,
                    isDarkMode = isDarkMode,
                    currentLanguage = currentLanguage,
                    onCallRider = { phone ->
                        try {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                            startActivity(intent)
                        } catch (_: Exception) {}
                    }
                )
            }
        }
    }
}

@Composable
fun DarazAppContent(
    viewModel: DarazViewModel,
    isDarkMode: Boolean,
    currentLanguage: AppLanguage,
    onCallRider: (String) -> Unit
) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val activePushAlert by viewModel.activePushAlert.collectAsStateWithLifecycle()

    val allProducts by viewModel.allProducts.collectAsStateWithLifecycle()
    val flashSaleProducts by viewModel.flashSaleProducts.collectAsStateWithLifecycle()
    val displayedProducts by viewModel.displayedProducts.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val flashCountdown by viewModel.flashCountdown.collectAsStateWithLifecycle()

    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
    val wishlistItems by viewModel.wishlistItems.collectAsStateWithLifecycle()
    val orders by viewModel.orders.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val unreadNotifCount by viewModel.unreadNotifCount.collectAsStateWithLifecycle()

    val currentProduct by viewModel.currentProduct.collectAsStateWithLifecycle()
    val currentReviews by viewModel.currentReviews.collectAsStateWithLifecycle()
    val currentTrackingOrder by viewModel.currentTrackingOrder.collectAsStateWithLifecycle()

    val appliedVoucher by viewModel.appliedVoucher.collectAsStateWithLifecycle()
    val voucherMessage by viewModel.voucherMessage.collectAsStateWithLifecycle()
    val selectedPaymentMethod by viewModel.selectedPaymentMethod.collectAsStateWithLifecycle()
    val selectedAddressIndex by viewModel.selectedAddressIndex.collectAsStateWithLifecycle()
    val isPaymentProcessing by viewModel.isPaymentProcessing.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val csvPreviewContent by viewModel.csvPreviewContent.collectAsStateWithLifecycle()

    var showLanguagePicker by remember { mutableStateOf(false) }

    // Intercept Back Press on sub-screens
    BackHandler(enabled = currentScreen != Screen.HOME) {
        viewModel.navigateBack()
    }

    val isWishlisted = currentProduct != null && wishlistItems.any { it.productId == currentProduct!!.id }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                // Show DarazTopAppBar only on Home and Categories
                if (currentScreen == Screen.HOME || currentScreen == Screen.CATEGORIES) {
                    DarazTopAppBar(
                        searchQuery = searchQuery,
                        onSearchChange = { viewModel.onSearchQueryChange(it) },
                        cartItemCount = cartItems.sumOf { it.quantity },
                        unreadNotifCount = unreadNotifCount,
                        currentLanguage = currentLanguage,
                        onLanguageClick = { showLanguagePicker = true },
                        isDarkMode = isDarkMode,
                        onDarkModeToggle = { viewModel.toggleDarkMode() },
                        onCartClick = { viewModel.navigateTo(Screen.CART) },
                        onNotificationClick = { viewModel.navigateTo(Screen.NOTIFICATIONS) },
                        onAdminClick = { viewModel.navigateTo(Screen.ADMIN_PANEL) }
                    )
                }
            },
            bottomBar = {
                // Bottom Bar on main navigation destinations
                if (currentScreen == Screen.HOME || currentScreen == Screen.CATEGORIES || currentScreen == Screen.CART || currentScreen == Screen.NOTIFICATIONS || currentScreen == Screen.ACCOUNT) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 8.dp
                    ) {
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                            label = { Text(AppStrings.get("home", currentLanguage), fontSize = 10.sp) },
                            selected = currentScreen == Screen.HOME,
                            onClick = { viewModel.setBottomTab(0) },
                            colors = NavigationBarItemDefaults.colors(selectedIconColor = DarazOrange, selectedTextColor = DarazOrange)
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.Category, contentDescription = "Categories") },
                            label = { Text(AppStrings.get("categories", currentLanguage), fontSize = 10.sp) },
                            selected = currentScreen == Screen.CATEGORIES,
                            onClick = { viewModel.setBottomTab(1) },
                            colors = NavigationBarItemDefaults.colors(selectedIconColor = DarazOrange, selectedTextColor = DarazOrange)
                        )
                        NavigationBarItem(
                            icon = {
                                BadgedBox(badge = {
                                    if (unreadNotifCount > 0) {
                                        Badge { Text("$unreadNotifCount") }
                                    }
                                }) {
                                    Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                                }
                            },
                            label = { Text(AppStrings.get("notifications", currentLanguage), fontSize = 10.sp) },
                            selected = currentScreen == Screen.NOTIFICATIONS,
                            onClick = { viewModel.setBottomTab(2) },
                            colors = NavigationBarItemDefaults.colors(selectedIconColor = DarazOrange, selectedTextColor = DarazOrange)
                        )
                        NavigationBarItem(
                            icon = {
                                val count = cartItems.sumOf { it.quantity }
                                BadgedBox(badge = {
                                    if (count > 0) {
                                        Badge(containerColor = DarazOrange) { Text("$count", color = Color.White) }
                                    }
                                }) {
                                    Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                                }
                            },
                            label = { Text(AppStrings.get("cart", currentLanguage), fontSize = 10.sp) },
                            selected = currentScreen == Screen.CART,
                            onClick = { viewModel.setBottomTab(3) },
                            colors = NavigationBarItemDefaults.colors(selectedIconColor = DarazOrange, selectedTextColor = DarazOrange)
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.Person, contentDescription = "Account") },
                            label = { Text(AppStrings.get("account", currentLanguage), fontSize = 10.sp) },
                            selected = currentScreen == Screen.ACCOUNT,
                            onClick = { viewModel.setBottomTab(4) },
                            colors = NavigationBarItemDefaults.colors(selectedIconColor = DarazOrange, selectedTextColor = DarazOrange)
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentScreen) {
                    Screen.HOME, Screen.CATEGORIES -> {
                        HomeScreen(
                            flashSaleProducts = flashSaleProducts,
                            displayedProducts = displayedProducts,
                            selectedCategory = selectedCategory,
                            onCategorySelect = { viewModel.onCategorySelect(it) },
                            flashCountdown = flashCountdown,
                            onProductClick = { viewModel.openProduct(it) },
                            onAddToCart = { viewModel.addToCart(it) },
                            onClaimVoucher = {
                                viewModel.applyVoucher(it)
                                viewModel.showPushNotification("🎟️ Voucher Claimed!", "Code $it activated for your checkout!")
                            },
                            currentLanguage = currentLanguage,
                            onViewAllFlashSale = { viewModel.onCategorySelect("All") }
                        )
                    }

                    Screen.PRODUCT_DETAIL -> {
                        ProductDetailScreen(
                            product = currentProduct,
                            reviews = currentReviews,
                            isWishlisted = isWishlisted,
                            onBack = { viewModel.navigateBack() },
                            onAddToCart = { viewModel.addToCart(it) },
                            onBuyNow = {
                                viewModel.addToCart(it)
                                viewModel.navigateTo(Screen.CHECKOUT)
                            },
                            onToggleWishlist = { viewModel.toggleWishlist(it) },
                            onSubmitReview = { pId, rating, comment ->
                                viewModel.submitReview(pId, rating, comment)
                            },
                            onUpvoteReview = { viewModel.upvoteReview(it) },
                            onCartClick = { viewModel.navigateTo(Screen.CART) },
                            currentLanguage = currentLanguage
                        )
                    }

                    Screen.CART -> {
                        CartScreen(
                            cartItems = cartItems,
                            appliedVoucher = appliedVoucher,
                            voucherMessage = voucherMessage,
                            onApplyVoucher = { viewModel.applyVoucher(it) },
                            onRemoveVoucher = { viewModel.removeVoucher() },
                            onUpdateQuantity = { id, qty -> viewModel.updateCartQuantity(id, qty) },
                            onToggleSelect = { id, sel -> viewModel.toggleCartItemSelection(id, sel) },
                            onToggleSelectAll = { viewModel.toggleSelectAllCart(it) },
                            onRemoveItem = { viewModel.removeCartItem(it) },
                            onProceedCheckout = { viewModel.navigateTo(Screen.CHECKOUT) },
                            onStartShopping = { viewModel.navigateTo(Screen.HOME) },
                            currentLanguage = currentLanguage
                        )
                    }

                    Screen.CHECKOUT -> {
                        CheckoutScreen(
                            items = cartItems,
                            appliedVoucher = appliedVoucher,
                            savedAddresses = userProfile.savedAddresses,
                            selectedAddressIndex = selectedAddressIndex,
                            onSelectAddress = { viewModel.selectAddress(it) },
                            selectedPaymentMethod = selectedPaymentMethod,
                            onSelectPaymentMethod = { viewModel.selectPaymentMethod(it) },
                            isProcessing = isPaymentProcessing,
                            onConfirmOrder = { viewModel.proceedToPaymentAndOrder() },
                            onBack = { viewModel.navigateBack() },
                            currentLanguage = currentLanguage
                        )
                    }

                    Screen.ORDER_TRACKING -> {
                        OrderTrackingScreen(
                            order = currentTrackingOrder,
                            onAdvanceStep = { viewModel.advanceTrackingStep(it) },
                            onBack = { viewModel.navigateBack() },
                            onCallRider = onCallRider,
                            currentLanguage = currentLanguage
                        )
                    }

                    Screen.ADMIN_PANEL -> {
                        AdminDashboardScreen(
                            products = allProducts,
                            orders = orders,
                            csvContent = csvPreviewContent,
                            onAddProduct = { title, cat, pr, orig, stk, br, isFs ->
                                viewModel.addAdminProduct(title, cat, pr, orig, stk, br, isFs)
                            },
                            onDeleteProduct = { viewModel.deleteAdminProduct(it) },
                            onAdvanceOrderStatus = { viewModel.advanceTrackingStep(it) },
                            onGenerateCsv = { viewModel.generateOrdersCsv() },
                            onDismissCsv = { viewModel.dismissCsvPreview() },
                            onBack = { viewModel.navigateBack() },
                            currentLanguage = currentLanguage
                        )
                    }

                    Screen.NOTIFICATIONS -> {
                        NotificationsScreen(
                            notifications = notifications,
                            onMarkAsRead = { viewModel.markNotificationAsRead(it) },
                            onMarkAllAsRead = { viewModel.markAllNotificationsAsRead() },
                            onSimulatePushNotification = {
                                viewModel.showPushNotification(
                                    title = "⚡ Daraz Mega Rush Hour",
                                    message = "Flat 50% discount unlocked for the next 30 minutes! Tap to check deals."
                                )
                            },
                            onOpenOrder = { viewModel.openOrderTracking(it) },
                            currentLanguage = currentLanguage
                        )
                    }

                    Screen.WISHLIST -> {
                        WishlistScreen(
                            wishlistItems = wishlistItems,
                            onProductClick = { viewModel.openProduct(it) },
                            onRemove = { id ->
                                val prod = allProducts.find { it.id == id }
                                if (prod != null) viewModel.toggleWishlist(prod)
                            },
                            onMoveToCart = { item ->
                                val prod = allProducts.find { it.id == item.productId }
                                if (prod != null) {
                                    viewModel.addToCart(prod)
                                    viewModel.toggleWishlist(prod)
                                }
                            },
                            onBack = { viewModel.navigateBack() },
                            currentLanguage = currentLanguage
                        )
                    }

                    Screen.ACCOUNT -> {
                        AccountScreen(
                            userProfile = userProfile,
                            isDarkMode = isDarkMode,
                            currentLanguage = currentLanguage,
                            onToggleDarkMode = { viewModel.toggleDarkMode() },
                            onLanguageClick = { showLanguagePicker = true },
                            onOpenAdmin = { viewModel.navigateTo(Screen.ADMIN_PANEL) },
                            onOpenWishlist = { viewModel.navigateTo(Screen.WISHLIST) },
                            onOpenOrders = {
                                val latest = orders.firstOrNull()
                                if (latest != null) viewModel.openOrderTracking(latest.orderId)
                                else viewModel.showPushNotification("📦 Orders", "No past orders yet. Place an order to start tracking!")
                            },
                            onSocialLogin = { viewModel.simulateSocialLogin(it) }
                        )
                    }
                }
            }
        }

        // Push notification heads-up banner overlay
        PushNotificationBanner(
            notification = activePushAlert,
            onDismiss = { viewModel.dismissPushAlert() },
            onClick = {
                val ref = activePushAlert?.referenceId
                if (!ref.isNullOrBlank()) {
                    viewModel.openOrderTracking(ref)
                }
                viewModel.dismissPushAlert()
            }
        )

        // Language Picker Dialog
        if (showLanguagePicker) {
            AlertDialog(
                onDismissRequest = { showLanguagePicker = false },
                title = { Text("Select Language / زبان منتخب کریں") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        AppLanguage.values().forEach { lang ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (currentLanguage == lang) DarazOrange.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.setLanguage(lang)
                                        showLanguagePicker = false
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(lang.flag, fontSize = 20.sp)
                                    Spacer(Modifier.width(12.dp))
                                    Text(lang.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Spacer(Modifier.weight(1f))
                                    if (currentLanguage == lang) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = DarazOrange)
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showLanguagePicker = false }) { Text("Done") }
                }
            )
        }
    }
}
