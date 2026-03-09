package com.vunh.jetpack.bhx.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vunh.jetpack.bhx.presentation.common.HeaderSection

@Composable
fun NotificationScreen(onBack: () -> Unit, onMenuClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        HeaderSection(isHome = false, onMenuClick = onMenuClick)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 4.dp, vertical = 8.dp),
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
                text = "Thông báo",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.weight(1f)
            )
            
            Text(
                text = "Đánh dấu đã đọc tất cả (2)",
                fontSize = 13.sp,
                color = Color(0xFF007BFF),
                modifier = Modifier
                    .padding(end = 12.dp)
                    .clickable { }
            )
        }

        val notifications = listOf(
            NotificationItem(
                title = "PHIẾU MUA HÀNG GIẢM 20.000đ",
                content = "Tặng Anh VU mã giảm 20.000đ áp dụng khi mua các sản phẩm dầu gội Nguyên Xuân tại siêu thị hoặc Online Bách Hóa XANH\nMã: 6X0TZW62WP\nHạn sử dụng: 11/03/2026",
                time = "13:47 04/03/2026",
                isRead = false
            ),
            NotificationItem(
                title = "PHIẾU MUA HÀNG GIẢM 20.000đ",
                content = "Tặng Anh VU mã giảm 20.000đ áp dụng khi mua các sản phẩm băng vệ sinh từ 50.000đ tại siêu thị hoặc Online Bách Hóa XANH\nMã: JNUN3B65SU\nHạn sử dụng: 11/03/2026",
                time = "13:47 04/03/2026",
                isRead = false
            )
        )

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(notifications) { item ->
                NotificationRow(item)
                HorizontalDivider(color = Color(0xFFEEEEEE))
            }
        }
    }
}

@Composable
fun NotificationRow(item: NotificationItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (item.isRead) Color.White else Color(0xFFF0F7FF))
            .padding(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White)
                .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.LocalOffer,
                contentDescription = null,
                tint = Color(0xFF008848),
                modifier = Modifier.size(32.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.content,
                fontSize = 13.sp,
                color = Color.DarkGray,
                lineHeight = 18.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.time,
                fontSize = 11.sp,
                color = Color.Gray
            )
        }
    }
}

data class NotificationItem(
    val title: String,
    val content: String,
    val time: String,
    val isRead: Boolean
)
