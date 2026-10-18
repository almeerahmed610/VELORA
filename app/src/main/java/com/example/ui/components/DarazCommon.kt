package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.models.NotificationItem
import com.example.data.models.Product
import com.example.ui.theme.*
import com.example.util.AppLanguage
import com.example.util.AppStrings
import java.text.NumberFormat
import java.util.Locale

fun formatPkr(amount: Double): String {
    val formatter = NumberFormat.getNumberInstance(Locale.US)
    return "Rs. " + formatter.format(amount.toLong())
}

@Composable
fun DarazTopAppBar(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    cartItemCount: Int,
    unreadNotifCount: Int,
    currentLanguage: AppLanguage,
    onLanguageClick: () -> Unit,
    isDarkMode: Boolean,
    onDarkModeToggle: () -> Unit,
    onCartClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onAdminClick: () -> Unit
) {
    Surface(
        color = DarazOrange,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            // Top Row: Logo, Language Toggle, Dark Mode, Admin, Notif, Cart
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "V",
                            color = DarazOrange,
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp
                        )
                    }
                    Text(
                        text = "VELORA",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 22.sp,
                        letterSpacing = 1.sp
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    // Language Switcher Chip
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White.copy(alpha = 0.2f),
                        modifier = Modifier
                            .clickable { onLanguageClick() }
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                            .testTag("lang_toggle_btn")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Text(currentLanguage.flag, fontSize = 12.sp)
                            Spacer(Modifier.width(3.dp))
                            Text(
                                text = currentLanguage.title,
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Dark Mode Toggle
                    IconButton(
                        onClick = onDarkModeToggle,
                        modifier = Modifier.testTag("dark_mode_btn")
                    ) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle Dark Mode",
                            tint = Color.White
                        )
                    }

                    // Admin Shortcut
                    IconButton(
                        onClick = onAdminClick,
                        modifier = Modifier.testTag("admin_shortcut_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Storefront,
                            contentDescription = "Seller Admin",
                            tint = Color.White
                        )
                    }

                    // Notifications with badge
                    BadgedBox(
                        badge = {
                            if (unreadNotifCount > 0) {
                                Badge(containerColor = DarazRed) {
                                    Text("$unreadNotifCount", color = Color.White, fontSize = 9.sp)
                                }
                            }
                        }
                    ) {
                        IconButton(
                            onClick = onNotificationClick,
                            modifier = Modifier.testTag("notification_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = Color.White
                            )
                        }
                    }

                    // Cart with live badge
                    BadgedBox(
                        badge = {
                            if (cartItemCount > 0) {
                                Badge(containerColor = Color.White) {
                                    Text("$cartItemCount", color = DarazOrange, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    ) {
                        IconButton(
                            onClick = onCartClick,
                            modifier = Modifier.testTag("cart_appbar_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Cart",
                                tint = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Search Bar
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    TextField(
                        value = searchQuery,
                        onValueChange = onSearchChange,
                        placeholder = {
                            Text(
                                text = AppStrings.get("search_hint", currentLanguage),
                                fontSize = 13.sp,
                                color = Color.Gray,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("search_text_input")
                    )
                    if (searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = { onSearchChange("") },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear search",
                                tint = Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PushNotificationBanner(
    notification: NotificationItem?,
    onDismiss: () -> Unit,
    onClick: () -> Unit
) {
    AnimatedVisibility(
        visible = notification != null,
        enter = slideInVertically(initialOffsetY = { -it }),
        exit = slideOutVertically(targetOffsetY = { -it })
    ) {
        if (notification != null) {
            Surface(
                color = DarazNavy,
                shadowElevation = 8.dp,
                shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .clickable { onClick() }
                    .testTag("push_notification_banner")
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(DarazOrange),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = "Alert",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = notification.title,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = notification.message,
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 11.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Dismiss",
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProductCard(
    product: Product,
    onProductClick: () -> Unit,
    onAddToCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onProductClick() }
            .testTag("product_card_${product.id}")
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Discount Badge Top Right
                if (product.discountPercent > 0) {
                    Surface(
                        color = DarazRed,
                        shape = RoundedCornerShape(bottomStart = 8.dp),
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Text(
                            text = "-${product.discountPercent}%",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                // DarazMall tag Top Left
                if (product.isDarazMall) {
                    Surface(
                        color = DarazNavy,
                        shape = RoundedCornerShape(bottomEnd = 8.dp),
                        modifier = Modifier.align(Alignment.TopStart)
                    ) {
                        Text(
                            text = "Mall",
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = product.title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 16.sp
                )

                Spacer(Modifier.height(4.dp))

                // Price and Original Price
                Text(
                    text = formatPkr(product.price),
                    color = DarazOrange,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                if (product.originalPrice > product.price) {
                    Text(
                        text = formatPkr(product.originalPrice),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        fontSize = 10.sp,
                        textDecoration = TextDecoration.LineThrough
                    )
                }

                Spacer(Modifier.height(4.dp))

                // Ratings & Free Shipping Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = DarazGold,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(Modifier.width(2.dp))
                        Text(
                            text = String.format("%.1f", product.rating),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = " (${product.reviewCount})",
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (product.isFreeShipping) {
                        Surface(
                            color = DarazGreen.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "Free Delivery",
                                color = DarazGreen,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(6.dp))

                // Quick Add to Cart button
                Button(
                    onClick = onAddToCart,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DarazOrangeLight),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(28.dp)
                        .testTag("quick_add_${product.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.AddShoppingCart,
                        contentDescription = "Add",
                        tint = DarazOrangeDark,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "Add to Cart",
                        color = DarazOrangeDark,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
