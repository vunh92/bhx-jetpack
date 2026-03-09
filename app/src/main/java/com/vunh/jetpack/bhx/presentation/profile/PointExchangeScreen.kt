package com.vunh.jetpack.bhx.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vunh.jetpack.bhx.presentation.common.HeaderSection

@Composable
fun PointExchangeScreen(onBack: () -> Unit, onMenuClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F2F5))
    ) {
        HeaderSection(isHome = false, onMenuClick = onMenuClick)

        // Title Bar
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(0.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                        contentDescription = "Back",
                        modifier = Modifier.size(20.dp),
                        tint = Color(0xFF1A1A1A)
                    )
                }
                Text(
                    text = "Tích điểm đổi quà",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.width(48.dp))
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Simulated Banners based on image
            PointBanner(
                color = Color(0xFFFFF176),
                title = "Hóa Đơn Mua Hàng Bách Hóa Xanh Bất Kỳ",
                subtitle = "Tặng PMH 15K Mua Omo,Comfort,Sunlight,Clear,Dove,.. Từ 159K"
            )

            PointBanner(
                color = Color(0xFF81C784),
                title = "Mua gạo tích lũy 500.000đ",
                subtitle = "Nhận phiếu mua hàng 20.000đ"
            )

            PointBanner(
                color = Color(0xFFFFB74D),
                title = "TÍCH LŨY SỮA ABBOTT GROW & PEDIASURE",
                subtitle = "TẶNG PHIẾU MUA HÀNG TƯƠI SỐNG"
            )

            PointBanner(
                color = Color(0xFFF8BBD0),
                title = "TÍCH LŨY NHẬN QUÀ",
                subtitle = "VOUCHER GIẢM 3% - 5% - 10%"
            )
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun PointBanner(color: Color, title: String, subtitle: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clickable { },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Box(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    color = Color.Black.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = subtitle,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = Color.Red.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text("XEM CHI TIẾT", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
