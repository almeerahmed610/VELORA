package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.models.Product
import com.example.ui.components.ProductCard
import com.example.ui.components.formatPkr
import com.example.ui.theme.*
import com.example.util.AppLanguage
import com.example.util.AppStrings

@Composable
fun HomeScreen(
    flashSaleProducts: List<Product>,
    displayedProducts: List<Product>,
    selectedCategory: String,
    onCategorySelect: (String) -> Unit,
    flashCountdown: String,
    onProductClick: (String) -> Unit,
    onAddToCart: (Product) -> Unit,
    onClaimVoucher: (String) -> Unit,
    currentLanguage: AppLanguage,
    onViewAllFlashSale: () -> Unit
) {
    val categories = listOf(
        CategoryItem("All", Icons.Default.AllInclusive, DarazOrange),
        CategoryItem("Mobiles & Tablets", Icons.Default.Smartphone, Color(0xFF3B82F6)),
        CategoryItem("Fashion & Beauty", Icons.Default.Checkroom, Color(0xFFEC4899)),
        CategoryItem("Electronics", Icons.Default.Headphones, Color(0xFF8B5CF6)),
        CategoryItem("Home & Living", Icons.Default.Kitchen, Color(0xFF10B981)),
        CategoryItem("D-Mart / Groceries", Icons.Default.LocalGroceryStore, Color(0xFFF59E0B))
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(bottom = 80.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
            .testTag("home_products_grid")
    ) {
        // Hero Promo Banner
        item(span = { GridItemSpan(2) }) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(DarazOrange, Color(0xFFFF8A3D))
                            )
                        )
                        .padding(16.dp)
                ) {
                    Column(modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.Center) {
                        Surface(
                            color = Color.White.copy(alpha = 0.25f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "11.11 BIGGEST SALE OF THE YEAR",
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Up to 70% OFF",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Free Delivery + Extra 10% Voucher With Code: VELORA10",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 11.sp
                        )
                    }
                    Button(
                        onClick = { onClaimVoucher("VELORA10") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .testTag("claim_voucher_btn")
                    ) {
                        Text(
                            text = "Claim Code",
                            color = DarazOrange,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Quick Category Icons Carousel
        item(span = { GridItemSpan(2) }) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                categories.forEach { cat ->
                    val isSelected = selectedCategory == cat.name
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { onCategorySelect(cat.name) }
                            .width(68.dp)
                            .testTag("category_pill_${cat.name}")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) DarazOrange else cat.color.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = cat.icon,
                                contentDescription = cat.name,
                                tint = if (isSelected) Color.White else cat.color,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = cat.name,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) DarazOrange else MaterialTheme.colorScheme.onSurface,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = 12.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }

        // Flash Sale Section
        item(span = { GridItemSpan(2) }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "⚡ Flash Sale",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = DarazOrange
                        )
                        Spacer(Modifier.width(8.dp))
                        // Live Countdown Pill
                        Surface(
                            color = DarazNavy,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "Ends in $flashCountdown",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    TextButton(
                        onClick = onViewAllFlashSale,
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        Text(
                            text = "SHOP MORE >",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarazOrange
                        )
                    }
                }

                Spacer(Modifier.height(6.dp))

                // Flash Sale Horizontal items
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    flashSaleProducts.forEach { product ->
                        FlashSaleItemCard(
                            product = product,
                            onClick = { onProductClick(product.id) }
                        )
                    }
                }
            }
        }

        // Daraz Guarantee Strip
        item(span = { GridItemSpan(2) }) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GuaranteeItem(Icons.Default.Verified, "100% Authentic")
                    GuaranteeItem(Icons.Default.LocalShipping, "Fast Delivery")
                    GuaranteeItem(Icons.Default.Cached, "14-Day Return")
                    GuaranteeItem(Icons.Default.Lock, "Safe Payment")
                }
            }
        }

        // "Just For You" Section Header
        item(span = { GridItemSpan(2) }) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(18.dp)
                        .background(DarazOrange, RoundedCornerShape(2.dp))
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = AppStrings.get("just_for_you", currentLanguage),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Product Grid Items
        items(displayedProducts, key = { it.id }) { product ->
            ProductCard(
                product = product,
                onProductClick = { onProductClick(product.id) },
                onAddToCart = { onAddToCart(product) }
            )
        }
    }
}

private data class CategoryItem(val name: String, val icon: ImageVector, val color: Color)

@Composable
private fun GuaranteeItem(icon: ImageVector, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = label, tint = DarazOrange, modifier = Modifier.size(14.dp))
        Spacer(Modifier.width(4.dp))
        Text(label, fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun FlashSaleItemCard(
    product: Product,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .width(130.dp)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(6.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(95.dp)
            ) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(6.dp))
                )
                Surface(
                    color = DarazRed,
                    shape = RoundedCornerShape(bottomStart = 6.dp),
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Text(
                        "-${product.discountPercent}%",
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }
            }

            Spacer(Modifier.height(4.dp))
            Text(
                text = formatPkr(product.price),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = DarazOrange
            )

            // Sold progress bar
            val soldPercent = if (product.flashSaleSoldPercent > 0) product.flashSaleSoldPercent else 65
            LinearProgressIndicator(
                progress = { soldPercent / 100f },
                color = DarazOrange,
                trackColor = DarazOrangeLight,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
            )
            Text(
                text = "$soldPercent% Sold",
                fontSize = 8.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
