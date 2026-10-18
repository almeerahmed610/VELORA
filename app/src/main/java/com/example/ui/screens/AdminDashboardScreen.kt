package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.Order
import com.example.data.models.Product
import com.example.ui.components.formatPkr
import com.example.ui.theme.*
import com.example.util.AppLanguage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminDashboardScreen(
    products: List<Product>,
    orders: List<Order>,
    csvContent: String?,
    onAddProduct: (String, String, Double, Double, Int, String, Boolean) -> Unit,
    onDeleteProduct: (String) -> Unit,
    onAdvanceOrderStatus: (String) -> Unit,
    onGenerateCsv: () -> Unit,
    onDismissCsv: () -> Unit,
    onBack: () -> Unit,
    currentLanguage: AppLanguage
) {
    val context = LocalContext.current
    var showAddDialog by remember { mutableStateOf(false) }

    // Dialog form state
    var titleInput by remember { mutableStateOf("") }
    var categoryInput by remember { mutableStateOf("Electronics") }
    var priceInput by remember { mutableStateOf("12500") }
    var originalPriceInput by remember { mutableStateOf("15000") }
    var stockInput by remember { mutableStateOf("25") }
    var brandInput by remember { mutableStateOf("Daraz Express") }
    var isFlashSaleInput by remember { mutableStateOf(false) }

    val totalRevenue = orders.sumOf { it.totalAmount }
    val avgOrderValue = if (orders.isNotEmpty()) totalRevenue / orders.size else 0.0

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
                    IconButton(onClick = onBack, modifier = Modifier.testTag("admin_back_btn")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                    Spacer(Modifier.width(8.dp))
                    Text("VELORA Seller & Admin Hub", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(Modifier.weight(1f))
                    Button(
                        onClick = onGenerateCsv,
                        colors = ButtonDefaults.buttonColors(containerColor = DarazNavy),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("export_csv_btn")
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("CSV", fontSize = 11.sp)
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Analytics KPI Grid
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                KpiCard(
                    title = "Total Revenue",
                    value = formatPkr(totalRevenue),
                    subtitle = "+18.4% vs last week",
                    color = DarazOrange,
                    modifier = Modifier.weight(1f)
                )
                KpiCard(
                    title = "Total Orders",
                    value = "${orders.size + 148}",
                    subtitle = "98.2% Fulfillment",
                    color = DarazGreen,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                KpiCard(
                    title = "Avg Order Value",
                    value = formatPkr(avgOrderValue),
                    subtitle = "Average basket size",
                    color = DarazBlue,
                    modifier = Modifier.weight(1f)
                )
                KpiCard(
                    title = "Conversion Rate",
                    value = "3.85%",
                    subtitle = "4,920 Store Visitors",
                    color = DarazGold,
                    modifier = Modifier.weight(1f)
                )
            }

            // Interactive Weekly Sales Chart
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Weekly Sales Analytics", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("PKR Thousands", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Spacer(Modifier.height(14.dp))

                    val weeklyPoints = listOf(45f, 62f, 38f, 75f, 90f, 120f, 85f)
                    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val maxVal = 130f
                            val barWidth = 24.dp.toPx()
                            val spacing = (size.width - (barWidth * weeklyPoints.size)) / (weeklyPoints.size + 1)

                            weeklyPoints.forEachIndexed { i, value ->
                                val x = spacing * (i + 1) + barWidth * i
                                val barHeight = (value / maxVal) * (size.height - 30.dp.toPx())
                                val y = size.height - barHeight - 20.dp.toPx()

                                drawRoundRect(
                                    brush = Brush.verticalGradient(
                                        listOf(DarazOrange, DarazOrangeLight)
                                    ),
                                    topLeft = Offset(x, y),
                                    size = Size(barWidth, barHeight),
                                    cornerRadius = CornerRadius(6.dp.toPx())
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        days.forEach { day ->
                            Text(day, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            // Inventory Management Header with Add Product button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Product Inventory (${products.size})", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Button(
                    onClick = { showAddDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = DarazOrange),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("admin_add_product_btn")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Add Product", fontSize = 11.sp)
                }
            }

            // Products list
            products.forEach { prod ->
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(prod.title, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, maxLines = 1)
                            Spacer(Modifier.height(2.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(formatPkr(prod.price), color = DarazOrange, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("Stock: ${prod.stock} units", fontSize = 11.sp, color = if (prod.stock < 10) DarazRed else DarazGreen)
                                Text("• ${prod.category}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        IconButton(
                            onClick = { onDeleteProduct(prod.id) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = Color.Gray, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Order Fulfillment Section
            Text("Recent Customer Orders (${orders.size})", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())

            orders.forEach { ord ->
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("#${ord.orderId} • ${ord.recipientName}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Surface(
                                color = if (ord.trackingStep >= 5) DarazGreen.copy(alpha = 0.15f) else DarazOrangeLight,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    ord.status,
                                    color = if (ord.trackingStep >= 5) DarazGreen else DarazOrangeDark,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(ord.itemsSummary, fontSize = 11.sp, maxLines = 1, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(formatPkr(ord.totalAmount), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DarazOrange)
                            TextButton(
                                onClick = { onAdvanceOrderStatus(ord.orderId) },
                                enabled = ord.trackingStep < 5
                            ) {
                                Text(if (ord.trackingStep < 5) "Advance Status >" else "Delivered ✓", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Product Dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("List New Product (VELORA Seller)") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = titleInput,
                        onValueChange = { titleInput = it },
                        label = { Text("Product Title") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = categoryInput,
                        onValueChange = { categoryInput = it },
                        label = { Text("Category") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedTextField(
                            value = priceInput,
                            onValueChange = { priceInput = it },
                            label = { Text("Price (Rs.)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = originalPriceInput,
                            onValueChange = { originalPriceInput = it },
                            label = { Text("Original (Rs.)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedTextField(
                            value = stockInput,
                            onValueChange = { stockInput = it },
                            label = { Text("Stock Qty") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = brandInput,
                            onValueChange = { brandInput = it },
                            label = { Text("Brand") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = isFlashSaleInput,
                            onCheckedChange = { isFlashSaleInput = it },
                            colors = CheckboxDefaults.colors(checkedColor = DarazOrange)
                        )
                        Text("Add to ⚡ Flash Sale", fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val p = priceInput.toDoubleOrNull() ?: 1000.0
                        val orig = originalPriceInput.toDoubleOrNull() ?: p
                        val stk = stockInput.toIntOrNull() ?: 10
                        if (titleInput.isNotBlank()) {
                            onAddProduct(titleInput, categoryInput, p, orig, stk, brandInput, isFlashSaleInput)
                            titleInput = ""
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DarazOrange)
                ) {
                    Text("Publish Product")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Cancel") }
            }
        )
    }

    // CSV Preview Dialog
    if (csvContent != null) {
        AlertDialog(
            onDismissRequest = onDismissCsv,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.TableChart, contentDescription = null, tint = DarazGreen)
                    Spacer(Modifier.width(8.dp))
                    Text("Exported Orders (CSV)")
                }
            },
            text = {
                Column {
                    Text("Data exported in standard CSV format:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(8.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .padding(4.dp)
                    ) {
                        Text(
                            text = csvContent,
                            fontSize = 10.sp,
                            modifier = Modifier
                                .padding(8.dp)
                                .verticalScroll(rememberScrollState()),
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val sendIntent: Intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, csvContent)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Share Orders CSV"))
                        onDismissCsv()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DarazNavy)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Share / Export")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissCsv) { Text("Close") }
            }
        )
    }
}

@Composable
private fun KpiCard(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.Black, color = color)
            Spacer(Modifier.height(2.dp))
            Text(subtitle, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
