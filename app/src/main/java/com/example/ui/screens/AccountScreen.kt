package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import com.example.data.models.UserProfile
import com.example.ui.theme.*
import com.example.util.AppLanguage
import com.example.util.AppStrings

@Composable
fun AccountScreen(
    userProfile: UserProfile,
    isDarkMode: Boolean,
    currentLanguage: AppLanguage,
    onToggleDarkMode: () -> Unit,
    onLanguageClick: () -> Unit,
    onOpenAdmin: () -> Unit,
    onOpenWishlist: () -> Unit,
    onOpenOrders: () -> Unit,
    onSocialLogin: (String) -> Unit
) {
    var showLoginDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Surface(
                color = DarazOrange,
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
                        text = "My VELORA Account",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                    IconButton(onClick = onOpenAdmin) {
                        Icon(Icons.Default.Storefront, contentDescription = "Admin", tint = Color.White)
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
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Profile Card
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(DarazOrange),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = userProfile.name.take(1),
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Spacer(Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(userProfile.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(userProfile.email, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(userProfile.phone, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    OutlinedButton(
                        onClick = { showLoginDialog = true },
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("fast_login_btn")
                    ) {
                        Text("Switch", fontSize = 11.sp)
                    }
                }
            }

            // V-Coins Balance Card
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = DarazNavy,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(DarazGold),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = Color.Black, modifier = Modifier.size(24.dp))
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("V-Coins Wallet", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("${userProfile.dCoins} V-Coins = Rs. ${userProfile.dCoins} OFF", color = DarazGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Surface(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            "Earn 2x",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Account Action Menu Cards
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    AccountMenuItem(
                        icon = Icons.Default.LocalShipping,
                        title = "My Orders & Tracking",
                        subtitle = "Check active orders, DEX tracking & history",
                        onClick = onOpenOrders
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp), color = MaterialTheme.colorScheme.surfaceVariant)

                    AccountMenuItem(
                        icon = Icons.Default.Favorite,
                        title = "My Wishlist",
                        subtitle = "Saved items and price drop alerts",
                        onClick = onOpenWishlist
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp), color = MaterialTheme.colorScheme.surfaceVariant)

                    AccountMenuItem(
                        icon = Icons.Default.Storefront,
                        title = "Seller Central & Admin Panel",
                        subtitle = "Manage inventory, orders & sales analytics",
                        onClick = onOpenAdmin
                    )
                }
            }

            // App Settings (Language, Dark Mode, Offline DB)
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("App Preferences", fontWeight = FontWeight.Bold, fontSize = 13.sp)

                    // Dark Mode Toggle Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.DarkMode, contentDescription = null, tint = DarazOrange)
                            Spacer(Modifier.width(10.dp))
                            Text("Dark Theme", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        }
                        Switch(
                            checked = isDarkMode,
                            onCheckedChange = { onToggleDarkMode() },
                            colors = SwitchDefaults.colors(checkedThumbColor = DarazOrange)
                        )
                    }

                    // Language Selector Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onLanguageClick() },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Translate, contentDescription = null, tint = DarazOrange)
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text("Language / زبان", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                Text("${currentLanguage.flag} ${currentLanguage.title}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
                    }

                    // Offline Mode Status
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CloudDone, contentDescription = null, tint = DarazGreen)
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text("Offline Access & Local Room Storage", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            Text("Product catalogue and cart are 100% accessible offline", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }

    // Social Media / Fast Login Dialog
    if (showLoginDialog) {
        AlertDialog(
            onDismissRequest = { showLoginDialog = false },
            title = { Text("Fast Social Login") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Select a quick sign-in method:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    OutlinedButton(
                        onClick = {
                            onSocialLogin("Google")
                            showLoginDialog = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.AccountCircle, contentDescription = null, tint = Color(0xFFEA4335))
                        Spacer(Modifier.width(8.dp))
                        Text("Continue with Google")
                    }

                    OutlinedButton(
                        onClick = {
                            onSocialLogin("Phone OTP")
                            showLoginDialog = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = DarazGreen)
                        Spacer(Modifier.width(8.dp))
                        Text("Sign In with Phone OTP (+92)")
                    }

                    OutlinedButton(
                        onClick = {
                            onSocialLogin("Facebook")
                            showLoginDialog = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, tint = Color(0xFF1877F2))
                        Spacer(Modifier.width(8.dp))
                        Text("Continue with Facebook")
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showLoginDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun AccountMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = DarazOrange, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Text(subtitle, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp))
    }
}
