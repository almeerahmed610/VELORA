package com.example.ui.screens

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.Order
import com.example.data.models.OrderStatus
import com.example.ui.components.formatPkr
import com.example.ui.theme.*
import com.example.util.AppLanguage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun OrderTrackingScreen(
    order: Order?,
    onAdvanceStep: (String) -> Unit,
    onBack: () -> Unit,
    onCallRider: (String) -> Unit,
    currentLanguage: AppLanguage
) {
    if (order == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = DarazOrange)
                Spacer(Modifier.height(8.dp))
                Text("Loading Order Tracking...")
            }
        }
        return
    }

    val steps = listOf(
        TrackingStepData(0, "Order Placed", "Your order has been recorded in VELORA system", Icons.Default.ReceiptLong),
        TrackingStepData(1, "Payment Confirmed", "Verified via ${order.paymentMethod}", Icons.Default.CheckCircle),
        TrackingStepData(2, "Warehouse Packed", "Package sorted at VELORA Fulfillment Center", Icons.Default.Inventory),
        TrackingStepData(3, "Shipped with VEX", "Package dispatched via VELORA Express", Icons.Default.LocalShipping),
        TrackingStepData(4, "Out for Delivery", "Rider ${order.riderName} is arriving", Icons.Default.TwoWheeler),
        TrackingStepData(5, "Delivered", "Delivered to ${order.recipientName}", Icons.Default.Home)
    )

    val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    val orderDateFormatted = sdf.format(Date(order.orderDate))

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
                    IconButton(onClick = onBack, modifier = Modifier.testTag("tracking_back_btn")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text("Live Order Tracking", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(order.trackingNumber, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Spacer(Modifier.weight(1f))
                    Surface(
                        color = if (order.trackingStep >= 5) DarazGreen.copy(alpha = 0.15f) else DarazOrangeLight,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = order.status,
                            color = if (order.trackingStep >= 5) DarazGreen else DarazOrangeDark,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
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
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Live Step Advance Action Bar for Demo/Real-time Simulation
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = DarazNavy),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Real-Time Tracking Demo", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(
                            text = if (order.trackingStep < 5) "Tap button to simulate rider progressing to next milestone" else "Order has reached final destination!",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 11.sp
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = { onAdvanceStep(order.orderId) },
                        enabled = order.trackingStep < 5,
                        colors = ButtonDefaults.buttonColors(containerColor = DarazOrange),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("advance_tracking_step_btn")
                    ) {
                        Icon(Icons.Default.FastForward, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(if (order.trackingStep < 5) "Next Step" else "Complete", fontSize = 11.sp)
                    }
                }
            }

            // Simulated Map Route Card
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
                            Icon(Icons.Default.Navigation, contentDescription = null, tint = DarazOrange, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Delivery Route", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Text("Estimated: ${order.estimatedDeliveryDate}", fontSize = 11.sp, color = DarazGreen, fontWeight = FontWeight.Bold)
                    }

                    Spacer(Modifier.height(10.dp))

                    // Simulated Map Graphic
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier.size(10.dp).clip(CircleShape).background(DarazNavy)
                                )
                                Spacer(Modifier.width(6.dp))
                                 Text("Warehouse: VELORA Hub, Karachi", fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                LinearProgressIndicator(
                                    progress = { (order.trackingStep.coerceAtMost(5)) / 5f },
                                    color = DarazOrange,
                                    trackColor = Color.LightGray,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                )
                                Spacer(Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.Default.TwoWheeler,
                                    contentDescription = "Rider",
                                    tint = DarazOrange,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Place, contentDescription = null, tint = DarazRed, modifier = Modifier.size(14.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Destination: ${order.shippingAddress}, ${order.city}", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
                            }
                        }
                    }
                }
            }

            // Assigned Rider Card
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(DarazOrangeLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.DeliveryDining, contentDescription = null, tint = DarazOrangeDark, modifier = Modifier.size(24.dp))
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(order.riderName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("VELORA Express Rider • 4.9★ (640 Deliveries)", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(order.riderPhone, fontSize = 11.sp, color = DarazOrange, fontWeight = FontWeight.SemiBold)
                    }
                    OutlinedButton(
                        onClick = { onCallRider(order.riderPhone) },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("call_rider_btn")
                    ) {
                        Icon(Icons.Default.Phone, contentDescription = "Call", modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Call", fontSize = 11.sp)
                    }
                }
            }

            // Timeline Steps Detailed List
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Order Status Timeline", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(Modifier.height(14.dp))

                    steps.forEachIndexed { index, step ->
                        val isDone = order.trackingStep >= step.stepIndex
                        val isCurrent = order.trackingStep == step.stepIndex

                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when {
                                                isCurrent -> DarazOrange
                                                isDone -> DarazGreen
                                                else -> Color.LightGray.copy(alpha = 0.5f)
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = step.icon,
                                        contentDescription = null,
                                        tint = if (isDone || isCurrent) Color.White else Color.Gray,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }

                                if (index < steps.size - 1) {
                                    Box(
                                        modifier = Modifier
                                            .width(2.dp)
                                            .height(34.dp)
                                            .background(if (isDone && order.trackingStep > step.stepIndex) DarazGreen else Color.LightGray.copy(alpha = 0.5f))
                                    )
                                }
                            }

                            Spacer(Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = step.title,
                                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                    color = if (isCurrent) DarazOrange else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = step.description,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(Modifier.height(10.dp))
                            }
                        }
                    }
                }
            }

            // Order Items & Payment Details
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Order Details", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Items: ${order.itemsSummary}", fontSize = 12.sp)
                    Text("Placed On: $orderDateFormatted", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Payment: ${order.paymentMethod}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total Paid", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(formatPkr(order.totalAmount), fontWeight = FontWeight.Black, fontSize = 14.sp, color = DarazOrange)
                    }
                }
            }
        }
    }
}

private data class TrackingStepData(
    val stepIndex: Int,
    val title: String,
    val description: String,
    val icon: ImageVector
)
