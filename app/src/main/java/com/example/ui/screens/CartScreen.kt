package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.models.CartItem
import com.example.data.models.Voucher
import com.example.ui.components.formatPkr
import com.example.ui.theme.*
import com.example.util.AppLanguage
import com.example.util.AppStrings

@Composable
fun CartScreen(
    cartItems: List<CartItem>,
    appliedVoucher: Voucher?,
    voucherMessage: String?,
    onApplyVoucher: (String) -> Unit,
    onRemoveVoucher: () -> Unit,
    onUpdateQuantity: (String, Int) -> Unit,
    onToggleSelect: (String, Boolean) -> Unit,
    onToggleSelectAll: (Boolean) -> Unit,
    onRemoveItem: (String) -> Unit,
    onProceedCheckout: () -> Unit,
    onStartShopping: () -> Unit,
    currentLanguage: AppLanguage
) {
    var voucherInput by remember { mutableStateOf("") }

    val allSelected = cartItems.isNotEmpty() && cartItems.all { it.isSelected }
    val selectedItems = cartItems.filter { it.isSelected }
    val subtotal = selectedItems.sumOf { it.price * it.quantity }
    val voucherDiscount = if (appliedVoucher != null) {
        (subtotal * appliedVoucher.discountPercent / 100.0).coerceAtMost(appliedVoucher.maxDiscount)
    } else 0.0
    val shipping = if (subtotal > 2000 || appliedVoucher?.code == "FREESHIP" || selectedItems.isEmpty()) 0.0 else 199.0
    val grandTotal = (subtotal - voucherDiscount + shipping).coerceAtLeast(0.0)

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
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Shopping Cart (${cartItems.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    if (cartItems.isNotEmpty()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = allSelected,
                                onCheckedChange = { onToggleSelectAll(it) },
                                colors = CheckboxDefaults.colors(checkedColor = DarazOrange)
                            )
                            Text("All", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        },
        bottomBar = {
            if (cartItems.isNotEmpty()) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text("Total: ", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(
                                        text = formatPkr(grandTotal),
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Black,
                                        color = DarazOrange
                                    )
                                }
                                if (voucherDiscount > 0) {
                                    Text(
                                        text = "Saved ${formatPkr(voucherDiscount)}",
                                        fontSize = 10.sp,
                                        color = DarazGreen,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Button(
                                onClick = onProceedCheckout,
                                enabled = selectedItems.isNotEmpty(),
                                colors = ButtonDefaults.buttonColors(containerColor = DarazOrange),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .height(44.dp)
                                    .testTag("cart_checkout_btn")
                            ) {
                                Text(
                                    text = "Check Out (${selectedItems.sumOf { it.quantity }})",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        if (cartItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.RemoveShoppingCart,
                        contentDescription = "Empty Cart",
                        tint = Color.Gray,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(Modifier.height(12.dp))
                    Text("Your cart is empty!", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("Explore top deals and add items to your cart", fontSize = 12.sp, color = Color.Gray)
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = onStartShopping,
                        colors = ButtonDefaults.buttonColors(containerColor = DarazOrange),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("empty_cart_shop_btn")
                    ) {
                        Text("Start Shopping Now")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Voucher Entry Box
                item {
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = voucherInput,
                                    onValueChange = { voucherInput = it },
                                    placeholder = { Text("Enter Promo Code", fontSize = 12.sp) },
                                    singleLine = true,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(50.dp)
                                        .testTag("voucher_input_field")
                                )
                                Spacer(Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        if (voucherInput.isNotBlank()) {
                                            onApplyVoucher(voucherInput)
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = DarazNavy),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.testTag("voucher_apply_btn")
                                ) {
                                    Text("Apply", fontSize = 12.sp)
                                }
                            }

                            // Quick Voucher Chips
                            Spacer(Modifier.height(8.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Suggestions: ", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                listOf("DARAZ10", "FREESHIP").forEach { code ->
                                    Surface(
                                        color = DarazOrangeLight,
                                        shape = RoundedCornerShape(4.dp),
                                        modifier = Modifier.clickable {
                                            voucherInput = code
                                            onApplyVoucher(code)
                                        }
                                    ) {
                                        Text(
                                            text = code,
                                            color = DarazOrangeDark,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }

                            if (appliedVoucher != null) {
                                Spacer(Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(DarazGreen.copy(alpha = 0.1f), RoundedCornerShape(6.dp))
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("✓ ${appliedVoucher.code} (${appliedVoucher.description})", fontSize = 11.sp, color = DarazGreen, fontWeight = FontWeight.Bold)
                                    IconButton(onClick = onRemoveVoucher, modifier = Modifier.size(20.dp)) {
                                        Icon(Icons.Default.Close, contentDescription = "Remove", tint = DarazRed, modifier = Modifier.size(14.dp))
                                    }
                                }
                            } else if (voucherMessage != null) {
                                Spacer(Modifier.height(4.dp))
                                Text(voucherMessage, fontSize = 11.sp, color = DarazRed)
                            }
                        }
                    }
                }

                // Cart Items
                items(cartItems, key = { it.productId }) { item ->
                    CartItemRow(
                        item = item,
                        onToggleSelect = { onToggleSelect(item.productId, it) },
                        onUpdateQuantity = { onUpdateQuantity(item.productId, it) },
                        onRemove = { onRemoveItem(item.productId) }
                    )
                }

                // Order summary breakdown
                item {
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Order Summary", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            SummaryRow("Subtotal (${selectedItems.sumOf { it.quantity }} items)", formatPkr(subtotal))
                            SummaryRow("Delivery Fee", if (shipping == 0.0) "FREE" else formatPkr(shipping))
                            if (voucherDiscount > 0) {
                                SummaryRow("Voucher Discount", "-${formatPkr(voucherDiscount)}", DarazGreen)
                            }
                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                            SummaryRow("Estimated Total", formatPkr(grandTotal), DarazOrange, isBold = true)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CartItemRow(
    item: CartItem,
    onToggleSelect: (Boolean) -> Unit,
    onUpdateQuantity: (Int) -> Unit,
    onRemove: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = item.isSelected,
                onCheckedChange = onToggleSelect,
                colors = CheckboxDefaults.colors(checkedColor = DarazOrange)
            )

            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = formatPkr(item.price),
                    color = DarazOrange,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )

                Spacer(Modifier.height(6.dp))

                // Quantity stepper
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(6.dp))
                            .padding(2.dp)
                    ) {
                        IconButton(
                            onClick = { onUpdateQuantity(item.quantity - 1) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "Minus", modifier = Modifier.size(14.dp))
                        }
                        Text(
                            text = "${item.quantity}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        IconButton(
                            onClick = { onUpdateQuantity(item.quantity + 1) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Plus", modifier = Modifier.size(14.dp))
                        }
                    }

                    IconButton(onClick = onRemove, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = Color.Gray, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String, valueColor: Color = Color.Unspecified, isBold: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 12.sp, fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal)
        Text(value, fontSize = 12.sp, fontWeight = if (isBold) FontWeight.Black else FontWeight.SemiBold, color = valueColor)
    }
}
