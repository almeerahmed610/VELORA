package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.CartItem
import com.example.data.models.UserAddress
import com.example.data.models.Voucher
import com.example.ui.components.formatPkr
import com.example.ui.theme.*
import com.example.util.AppLanguage
import com.example.util.AppStrings

@Composable
fun CheckoutScreen(
    items: List<CartItem>,
    appliedVoucher: Voucher?,
    savedAddresses: List<UserAddress>,
    selectedAddressIndex: Int,
    onSelectAddress: (Int) -> Unit,
    selectedPaymentMethod: String,
    onSelectPaymentMethod: (String) -> Unit,
    isProcessing: Boolean,
    onConfirmOrder: () -> Unit,
    onBack: () -> Unit,
    currentLanguage: AppLanguage
) {
    val subtotal = items.filter { it.isSelected }.sumOf { it.price * it.quantity }
    val discount = if (appliedVoucher != null) {
        (subtotal * appliedVoucher.discountPercent / 100.0).coerceAtMost(appliedVoucher.maxDiscount)
    } else 0.0
    val shipping = if (subtotal > 2000 || appliedVoucher?.code == "FREESHIP") 0.0 else 199.0
    val grandTotal = (subtotal - discount + shipping).coerceAtLeast(0.0)

    val currentAddress = savedAddresses.getOrNull(selectedAddressIndex) ?: savedAddresses.first()

    // Interactive simulated fields
    var easypaisaNumber by remember { mutableStateOf("03001234567") }
    var cardNumber by remember { mutableStateOf("4214 **** **** 8921") }
    var cardExpiry by remember { mutableStateOf("12/28") }
    var cardCvv by remember { mutableStateOf("786") }

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
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("checkout_back_btn")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Secure Checkout",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                    Spacer(Modifier.weight(1f))
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Encrypted",
                        tint = DarazGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "256-Bit SSL",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarazGreen
                    )
                }
            }
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 10.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Total Amount", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = formatPkr(grandTotal),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = DarazOrange
                        )
                    }

                    Button(
                        onClick = onConfirmOrder,
                        enabled = !isProcessing,
                        colors = ButtonDefaults.buttonColors(containerColor = DarazOrange),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .height(46.dp)
                            .testTag("place_order_btn")
                    ) {
                        if (isProcessing) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Encrypting & Placing...", fontSize = 12.sp)
                        } else {
                            Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = AppStrings.get("place_order", currentLanguage),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
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
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Delivery Address Card
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = DarazOrange, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Delivery Address", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Surface(
                            color = DarazOrangeLight,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                currentAddress.label,
                                color = DarazOrangeDark,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                    Text(currentAddress.fullName, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Text(currentAddress.phone, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${currentAddress.address}, ${currentAddress.city}, ${currentAddress.province}", fontSize = 12.sp, lineHeight = 16.sp)

                    Spacer(Modifier.height(6.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        savedAddresses.forEachIndexed { idx, addr ->
                            FilterChip(
                                selected = selectedAddressIndex == idx,
                                onClick = { onSelectAddress(idx) },
                                label = { Text(addr.label, fontSize = 10.sp) }
                            )
                        }
                    }
                }
            }

            // Payment Gateways Selection
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Payment, contentDescription = null, tint = DarazOrange, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Select Secure Payment Method", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    Spacer(Modifier.height(10.dp))

                    // Gateways
                    PaymentMethodOption(
                        title = "Easypaisa Mobile Wallet",
                        subtitle = "Instant OTP verification & 1-tap checkout",
                        icon = Icons.Default.PhoneAndroid,
                        isSelected = selectedPaymentMethod == "Easypaisa",
                        onClick = { onSelectPaymentMethod("Easypaisa") }
                    )

                    PaymentMethodOption(
                        title = "JazzCash Mobile Account",
                        subtitle = "Pay securely via your JazzCash MPIN",
                        icon = Icons.Default.SendToMobile,
                        isSelected = selectedPaymentMethod == "JazzCash",
                        onClick = { onSelectPaymentMethod("JazzCash") }
                    )

                    PaymentMethodOption(
                        title = "Credit / Debit Card",
                        subtitle = "Visa, Mastercard (256-Bit SSL Encrypted)",
                        icon = Icons.Default.CreditCard,
                        isSelected = selectedPaymentMethod == "Card",
                        onClick = { onSelectPaymentMethod("Card") }
                    )

                    PaymentMethodOption(
                        title = "Cash on Delivery (COD)",
                        subtitle = "Pay cash at doorstep upon delivery",
                        icon = Icons.Default.LocalAtm,
                        isSelected = selectedPaymentMethod == "COD",
                        onClick = { onSelectPaymentMethod("COD") }
                    )

                    // Interactive details based on method
                    Spacer(Modifier.height(10.dp))
                    when (selectedPaymentMethod) {
                        "Easypaisa", "JazzCash" -> {
                            OutlinedTextField(
                                value = easypaisaNumber,
                                onValueChange = { easypaisaNumber = it },
                                label = { Text("Account / Mobile Number") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("payment_phone_input")
                            )
                        }
                        "Card" -> {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                OutlinedTextField(
                                    value = cardNumber,
                                    onValueChange = { cardNumber = it },
                                    label = { Text("Card Number") },
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("card_number_input")
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedTextField(
                                        value = cardExpiry,
                                        onValueChange = { cardExpiry = it },
                                        label = { Text("Expiry (MM/YY)") },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f)
                                    )
                                    OutlinedTextField(
                                        value = cardCvv,
                                        onValueChange = { cardCvv = it },
                                        label = { Text("CVV") },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                        "COD" -> {
                            Surface(
                                color = DarazGreen.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "✓ Please keep exact cash ready upon arrival of the VELORA Express rider.",
                                    fontSize = 11.sp,
                                    color = DarazGreen,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Security Guarantee Banner
            Surface(
                color = DarazNavy,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Security",
                        tint = DarazGold,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text("VELORA Safe Payment Guarantee", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text(
                            "All transactions are encrypted with 256-bit TLS bank-grade security. Card details are never stored on device.",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 10.sp,
                            lineHeight = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PaymentMethodOption(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) DarazOrangeLight else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .border(
                width = if (isSelected) 1.5.dp else 0.dp,
                color = if (isSelected) DarazOrange else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(selectedColor = DarazOrange)
            )
            Spacer(Modifier.width(6.dp))
            Icon(icon, contentDescription = null, tint = if (isSelected) DarazOrange else Color.Gray, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Column {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text(subtitle, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
