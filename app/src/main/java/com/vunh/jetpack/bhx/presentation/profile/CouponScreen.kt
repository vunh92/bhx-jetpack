package com.vunh.jetpack.bhx.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vunh.jetpack.bhx.presentation.common.HeaderSection

@Composable
fun CouponScreen(
    onBack: () -> Unit,
    onMenuClick: () -> Unit,
    viewModel: CouponViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
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
                        text = "Phiếu mua hàng",
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
                    .background(Color(0xFFEEF59B).copy(alpha = 0.5f))
                    .verticalScroll(rememberScrollState())
            ) {
                // Section 1: NHẬN TIỀN XÀI LIỀN
                WoodenSectionTitle("NHẬN TIỀN XÀI LIỀN")
                
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    items(uiState.featuredCoupons) { coupon ->
                        CouponCard(coupon)
                    }
                }

                // Section 2: ƯU ĐÃI CHO KHÁCH MỚI
                WoodenSectionTitle("ƯU ĐÃI CHO KHÁCH MỚI")
                
                uiState.products.chunked(2).forEach { rowItems ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowItems.forEach { product ->
                            CouponProductCard(product, modifier = Modifier.weight(1f))
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        // Floating Action Buttons (Red Envelope and Chat)
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Red Envelope Button
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Red)
                    .clickable { },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("100%", color = Color.Yellow, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("NHẬN LÌ XÌ", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 8.sp)
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Chat Button
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF23395D))
                    .clickable { },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    Text("Chat", color = Color.White, fontSize = 10.sp)
                }
            }
        }
    }
}

@Composable
fun WoodenSectionTitle(title: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            color = Color(0xFF8D4B26),
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier.border(2.dp, Color(0xFFC66935), RoundedCornerShape(4.dp))
        ) {
            Text(
                text = title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
fun CouponCard(info: CouponInfo) {
    Card(
        modifier = Modifier
            .width(280.dp)
            .height(200.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(Color(0xFFF5F5F5), RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("IMG", fontSize = 12.sp, color = Color.Gray)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = info.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2C3E50)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = info.description,
                fontSize = 14.sp,
                color = Color.Gray,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = info.expiry,
                fontSize = 12.sp,
                color = Color.LightGray
            )
            Spacer(modifier = Modifier.weight(1f))
            Surface(
                color = Color(0xFFE8F5E9),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { }
            ) {
                Text(
                    text = "LẤY NGAY",
                    modifier = Modifier.padding(vertical = 8.dp),
                    textAlign = TextAlign.Center,
                    color = Color(0xFF008848),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun CouponProductCard(product: CouponProduct, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(4.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(4.dp)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .background(Color(0xFFF9F9F9)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("IMAGE", fontSize = 10.sp, color = Color.LightGray)
                }
                
                // Discount Badge
                Surface(
                    color = Color.Red,
                    shape = RoundedCornerShape(bottomStart = 4.dp),
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Text(
                        text = product.discount,
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
                
                // Point Badge at bottom
                Surface(
                    color = Color(0xFFF39C12),
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                ) {
                    Text(
                        text = "x2 ĐIỂM THÀNH VIÊN",
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
            
            Column(modifier = Modifier.padding(8.dp)) {
                Surface(
                    color = Color.Yellow,
                    shape = RoundedCornerShape(2.dp)
                ) {
                    Text(
                        "Ưu đãi khách hàng mới",
                        fontSize = 9.sp,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = product.name,
                    fontSize = 13.sp,
                    maxLines = 2,
                    minLines = 2,
                    lineHeight = 16.sp,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = product.price,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = product.oldPrice,
                        fontSize = 11.sp,
                        color = Color.Gray,
                        textDecoration = TextDecoration.LineThrough
                    )
                }
                Text(
                    "Có khuyến mãi",
                    fontSize = 11.sp,
                    color = Color(0xFFE67E22)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = Color(0xFFE8F5E9),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { }
                ) {
                    Text(
                        text = "MUA",
                        modifier = Modifier.padding(vertical = 6.dp),
                        textAlign = TextAlign.Center,
                        color = Color(0xFF008848),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

data class CouponInfo(val title: String, val description: String, val expiry: String)
data class CouponProduct(val name: String, val price: String, val oldPrice: String, val discount: String)
