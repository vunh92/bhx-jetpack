package com.vunh.jetpack.bhx.presentation.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vunh.jetpack.bhx.data.local.ProfileManager
import com.vunh.jetpack.bhx.presentation.common.HeaderSection

@Composable
fun OrderHistoryScreen(onMenuClick: () -> Unit, onNavigateToLogin: () -> Unit) {
    val context = LocalContext.current
    val profileManager = remember { ProfileManager(context) }
    val isLoggedIn = profileManager.isLoggedIn()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        HeaderSection(isHome = false, onMenuClick = onMenuClick)

        if (!isLoggedIn) {
            Box(
                modifier = Modifier.fillMaxSize(),
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
            var selectedTab by remember { mutableIntStateOf(0) }
            val tabs = listOf("Tất cả", "Chờ giao", "Đang giao", "Giao thành công", "Hủy đơn")

            Text(
                text = "Đơn hàng từng mua",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                edgePadding = 16.dp,
                containerColor = Color.White,
                contentColor = Color(0xFF008848),
                divider = {},
                indicator = {}
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            val isSelected = selectedTab == index
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (isSelected) Color(0xFF008848).copy(alpha = 0.8f) else Color(0xFFF0F2F5),
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                Text(
                                    text = title,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                                    color = if (isSelected) Color.White else Color.Black,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    )
                }
            }

            // Empty State for logged in user
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 56.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    modifier = Modifier.size(100.dp),
                    tint = Color(0xFF008848).copy(alpha = 0.3f)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Có vẻ bạn chưa có đơn hàng nào\nTiếp tục mua sắm cùng Bách hóa XANH nhé!",
                    textAlign = TextAlign.Center,
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                    color = Color.Black
                )
            }
        }
    }
}
