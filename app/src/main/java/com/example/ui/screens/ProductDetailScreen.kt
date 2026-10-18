package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.models.Product
import com.example.data.models.Review
import com.example.ui.components.formatPkr
import com.example.ui.theme.*
import com.example.util.AppLanguage
import com.example.util.AppStrings

@Composable
fun ProductDetailScreen(
    product: Product?,
    reviews: List<Review>,
    isWishlisted: Boolean,
    onBack: () -> Unit,
    onAddToCart: (Product) -> Unit,
    onBuyNow: (Product) -> Unit,
    onToggleWishlist: (Product) -> Unit,
    onSubmitReview: (String, Int, String) -> Unit,
    onUpvoteReview: (String) -> Unit,
    onCartClick: () -> Unit,
    currentLanguage: AppLanguage
) {
    if (product == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = DarazOrange)
        }
        return
    }

    var showReviewDialog by remember { mutableStateOf(false) }
    var userRating by remember { mutableIntStateOf(5) }
    var userComment by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("detail_back_btn")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                    Text(
                        text = product.category,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        maxLines = 1
                    )
                    Row {
                        IconButton(onClick = onCartClick) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Cart", tint = DarazOrange)
                        }
                    }
                }
            }
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 12.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Chat button
                    IconButton(
                        onClick = { /* simulated seller chat */ },
                        modifier = Modifier.size(42.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Chat, contentDescription = "Chat", tint = Color.Gray, modifier = Modifier.size(18.dp))
                            Text("Chat", fontSize = 9.sp, color = Color.Gray)
                        }
                    }

                    // Wishlist toggle
                    IconButton(
                        onClick = { onToggleWishlist(product) },
                        modifier = Modifier
                            .size(42.dp)
                            .testTag("detail_wishlist_btn")
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = if (isWishlisted) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Wishlist",
                                tint = if (isWishlisted) DarazRed else Color.Gray,
                                modifier = Modifier.size(18.dp)
                            )
                            Text("Wish", fontSize = 9.sp, color = if (isWishlisted) DarazRed else Color.Gray)
                        }
                    }

                    // Add to Cart
                    Button(
                        onClick = { onAddToCart(product) },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DarazOrangeLight),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("detail_add_to_cart_btn")
                    ) {
                        Text(
                            text = AppStrings.get("add_to_cart", currentLanguage),
                            color = DarazOrangeDark,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    // Buy Now
                    Button(
                        onClick = { onBuyNow(product) },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DarazOrange),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("detail_buy_now_btn")
                    ) {
                        Text(
                            text = AppStrings.get("buy_now", currentLanguage),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Product Hero Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .background(Color.White)
            ) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.title,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )

                if (product.discountPercent > 0) {
                    Surface(
                        color = DarazRed,
                        shape = RoundedCornerShape(bottomStart = 8.dp),
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Text(
                            text = "-${product.discountPercent}% OFF",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Price & Title Card
            Card(
                shape = RoundedCornerShape(0.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = formatPkr(product.price),
                            color = DarazOrange,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black
                        )
                        if (product.originalPrice > product.price) {
                            Text(
                                text = formatPkr(product.originalPrice),
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                fontSize = 14.sp,
                                textDecoration = TextDecoration.LineThrough
                            )
                        }
                    }

                    Spacer(Modifier.height(6.dp))

                    if (product.isDarazMall) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = DarazNavy,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "VeloraMall",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "100% Authentic Guaranteed",
                                color = DarazNavy,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Spacer(Modifier.height(6.dp))
                    }

                    Text(
                        text = product.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 22.sp
                    )

                    Spacer(Modifier.height(8.dp))

                    // Rating Summary Row
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = DarazGold, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "${product.rating} / 5.0",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = " (${product.reviewCount} customer ratings)",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.weight(1f))
                        Text(
                            text = "In Stock (${product.stock})",
                            fontSize = 12.sp,
                            color = DarazGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Delivery & Services Box
            Card(
                shape = RoundedCornerShape(0.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Delivery & Services", fontWeight = FontWeight.Bold, fontSize = 13.sp)

                    DeliveryFeature(
                        icon = Icons.Default.LocalShipping,
                        title = if (product.isFreeShipping) "Free Delivery" else "Standard Delivery (Rs. 199)",
                        subtitle = "Guaranteed delivery within ${product.deliveryTimeDays} days by VELORA Express"
                    )

                    DeliveryFeature(
                        icon = Icons.Default.Payment,
                        title = "Cash on Delivery Available",
                        subtitle = "Also supports Easypaisa, JazzCash, and Cards"
                    )

                    DeliveryFeature(
                        icon = Icons.Default.VerifiedUser,
                        title = product.warranty,
                        subtitle = "14 Days Free & Easy Return"
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Description Box
            Card(
                shape = RoundedCornerShape(0.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Product Description & Specs", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = product.description,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 20.sp
                    )
                    Spacer(Modifier.height(6.dp))
                    Text("• Brand: ${product.brand}", fontSize = 12.sp)
                    Text("• Category: ${product.category}", fontSize = 12.sp)
                }
            }

            Spacer(Modifier.height(8.dp))

            // Customer Reviews Section
            Card(
                shape = RoundedCornerShape(0.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = AppStrings.get("reviews", currentLanguage),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        OutlinedButton(
                            onClick = { showReviewDialog = true },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.testTag("write_review_btn")
                        ) {
                            Icon(Icons.Default.RateReview, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(AppStrings.get("write_review", currentLanguage), fontSize = 11.sp)
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    if (reviews.isEmpty()) {
                        Text(
                            text = "No reviews yet. Be the first to share your experience!",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        reviews.forEach { rev ->
                            ReviewItem(review = rev, onUpvote = { onUpvoteReview(rev.id) })
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.surfaceVariant)
                        }
                    }
                }
            }
        }
    }

    // Write Review Dialog
    if (showReviewDialog) {
        AlertDialog(
            onDismissRequest = { showReviewDialog = false },
            title = { Text("Rate & Review Product") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Select Rating:", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        for (i in 1..5) {
                            Icon(
                                imageVector = if (i <= userRating) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = "$i star",
                                tint = DarazGold,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clickable { userRating = i }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = userComment,
                        onValueChange = { userComment = it },
                        label = { Text("Your Review / Comment") },
                        placeholder = { Text("How was the quality, delivery speed, and packing?") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .testTag("review_input_field")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (userComment.isNotBlank()) {
                            onSubmitReview(product.id, userRating, userComment)
                            userComment = ""
                            showReviewDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DarazOrange),
                    modifier = Modifier.testTag("submit_review_dialog_btn")
                ) {
                    Text("Submit Review")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReviewDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun DeliveryFeature(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(icon, contentDescription = null, tint = DarazOrange, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(10.dp))
        Column {
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
            Text(subtitle, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun ReviewItem(review: Review, onUpvote: () -> Unit) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(DarazOrangeLight),
                    contentAlignment = Alignment.Center
                ) {
                    Text(review.userName.take(1), fontWeight = FontWeight.Bold, color = DarazOrangeDark, fontSize = 11.sp)
                }
                Spacer(Modifier.width(6.dp))
                Text(review.userName, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                Spacer(Modifier.width(6.dp))
                if (review.isVerifiedPurchase) {
                    Surface(color = DarazGreen.copy(alpha = 0.15f), shape = RoundedCornerShape(4.dp)) {
                        Text("Verified", color = DarazGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                    }
                }
            }
            Text(review.dateString, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        Spacer(Modifier.height(4.dp))

        Row {
            for (i in 1..5) {
                Icon(
                    imageVector = if (i <= review.rating) Icons.Default.Star else Icons.Default.StarBorder,
                    contentDescription = null,
                    tint = DarazGold,
                    modifier = Modifier.size(12.dp)
                )
            }
        }

        Spacer(Modifier.height(4.dp))

        Text(review.comment, fontSize = 12.sp, lineHeight = 16.sp)

        Spacer(Modifier.height(4.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(
                onClick = onUpvote,
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.height(24.dp)
            ) {
                Icon(Icons.Default.ThumbUp, contentDescription = "Helpful", modifier = Modifier.size(12.dp), tint = Color.Gray)
                Spacer(Modifier.width(4.dp))
                Text("Helpful (${review.helpfulCount})", fontSize = 10.sp, color = Color.Gray)
            }
        }
    }
}
