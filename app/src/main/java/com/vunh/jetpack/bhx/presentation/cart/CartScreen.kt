package com.vunh.jetpack.bhx.presentation.cart

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vunh.jetpack.bhx.data.local.ProfileManager
import com.vunh.jetpack.bhx.presentation.common.HeaderSection

@Composable
fun CartScreen(onMenuClick: () -> Unit, onNavigateToLogin: () -> Unit) {
    val context = LocalContext.current
    val profileManager = remember { ProfileManager(context) }
    val userProfile = profileManager.getProfile()
    val isLoggedIn = userProfile != null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        HeaderSection(isHome = false, onMenuClick = onMenuClick)

        if (!isLoggedIn) {
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = onNavigateToLogin,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF008848)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("ĐĂNG NHẬP", fontWeight = FontWeight.Bold)
                }
            }
        } else {
            val name = userProfile?.name?.split(" ")?.lastOrNull() ?: ""

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Empty Cart Illustration Placeholder
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Simulating the basket image from the screenshot
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF008848).copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("0", fontSize = 48.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Vẫn còn 10.000+ sản phẩm đang chờ phục vụ $name",
                    fontSize = 15.sp,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Category List (Simplified)
                val categories = listOf(
                    "Thịt heo" to Icons.Default.ShoppingBasket,
                    "Mì ăn liền" to Icons.Default.ShoppingBasket,
                    "Cá, hải sản" to Icons.Default.ShoppingBasket,
                    "Thịt gà, vịt" to Icons.Default.ShoppingBasket,
                    "Trứng gà, vịt" to Icons.Default.ShoppingBasket
                )

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(categories) { (title, icon) ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.width(70.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFF5F5F5)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(icon, contentDescription = null, tint = Color.Gray)
                            }
                            Text(
                                text = title,
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 14.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                    item {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.width(70.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFF5F5F5)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Menu, contentDescription = null, tint = Color(0xFF008848))
                            }
                            Text(
                                text = "Xem tất cả",
                                fontSize = 11.sp,
                                color = Color.DarkGray,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { /* Navigate to shopping */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF008848)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Tiếp tục mua hàng", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = { /* Navigate to orders */ }) {
                    Text(
                        buildAnnotatedString {
                            withStyle(style = SpanStyle(color = Color.Gray)) {
                                append("hoặc ")
                            }
                            withStyle(style = SpanStyle(color = Color(0xFF008848))) {
                                append("Xem đơn hàng $name vừa đặt trước ")
                            }
                        },
                        fontSize = 14.sp
                    )
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = Color(0xFF008848),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
