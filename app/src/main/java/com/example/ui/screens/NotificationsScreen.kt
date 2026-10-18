package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.NotificationItem
import com.example.data.models.NotificationType
import com.example.ui.theme.*
import com.example.util.AppLanguage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NotificationsScreen(
    notifications: List<NotificationItem>,
    onMarkAsRead: (String) -> Unit,
    onMarkAllAsRead: () -> Unit,
    onSimulatePushNotification: () -> Unit,
    onOpenOrder: (String) -> Unit,
    currentLanguage: AppLanguage
) {
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
                    Text("Notifications (${notifications.size})", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    if (notifications.isNotEmpty()) {
                        TextButton(onClick = onMarkAllAsRead) {
                            Text("Mark all read", fontSize = 11.sp, color = DarazOrange)
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
        ) {
            // Push Notification Simulator Banner
            Surface(
                color = DarazOrangeLight,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Live Push Notification Demo", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DarazOrangeDark)
                        Text("Simulate system notification dispatch with haptic feedback", fontSize = 10.sp, color = DarazOrangeDark.copy(alpha = 0.8f))
                    }
                    Button(
                        onClick = onSimulatePushNotification,
                        colors = ButtonDefaults.buttonColors(containerColor = DarazOrange),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("test_push_btn")
                    ) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Trigger", fontSize = 11.sp)
                    }
                }
            }

            if (notifications.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No notifications right now", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(notifications, key = { it.id }) { notif ->
                        NotificationRow(
                            notification = notif,
                            onClick = {
                                onMarkAsRead(notif.id)
                                if (notif.referenceId.isNotBlank()) {
                                    onOpenOrder(notif.referenceId)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationRow(
    notification: NotificationItem,
    onClick: () -> Unit
) {
    val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    val dateStr = sdf.format(Date(notification.timestamp))

    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (!notification.isRead) DarazOrangeLight.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        when (notification.type) {
                            NotificationType.ORDER -> DarazBlue.copy(alpha = 0.15f)
                            NotificationType.FLASH_SALE -> DarazOrange.copy(alpha = 0.15f)
                            NotificationType.WALLET -> DarazGold.copy(alpha = 0.15f)
                            NotificationType.PROMO -> DarazGreen.copy(alpha = 0.15f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (notification.type) {
                        NotificationType.ORDER -> Icons.Default.LocalShipping
                        NotificationType.FLASH_SALE -> Icons.Default.Bolt
                        NotificationType.WALLET -> Icons.Default.MonetizationOn
                        NotificationType.PROMO -> Icons.Default.LocalOffer
                    },
                    contentDescription = null,
                    tint = when (notification.type) {
                        NotificationType.ORDER -> DarazBlue
                        NotificationType.FLASH_SALE -> DarazOrange
                        NotificationType.WALLET -> DarazNavy
                        NotificationType.PROMO -> DarazGreen
                    },
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(notification.title, fontWeight = if (!notification.isRead) FontWeight.Bold else FontWeight.SemiBold, fontSize = 12.sp)
                    if (!notification.isRead) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(DarazOrange))
                    }
                }
                Spacer(Modifier.height(3.dp))
                Text(notification.message, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 15.sp)
                Spacer(Modifier.height(4.dp))
                Text(dateStr, fontSize = 9.sp, color = Color.Gray)
            }
        }
    }
}
