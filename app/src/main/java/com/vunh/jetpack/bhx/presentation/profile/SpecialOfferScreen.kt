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
import com.vunh.jetpack.bhx.presentation.common.HeaderSection

@Composable
fun SpecialOfferScreen(onBack: () -> Unit, onMenuClick: () -> Unit) {
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
                        text = "Ưu đãi đặc biệt",
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
                    val coupons = listOf(
                        CouponInfo("Tặng 20K", "Mua sản phẩm Kem các loại từ 120.000đ", "KT: 15/03/2026"),
                        CouponInfo("Tặng 50k", "Mua sản phẩm Kem, Sữa chua, Đông mát từ 250.000đ", "KT: 15/03/2026"),
                        CouponInfo("Tặng 30k", "Mua trái cây nhập khẩu từ 80.000đ", "KT: 15/03/2026")
                    )
                    items(coupons) { coupon ->
                        CouponCard(coupon)
                    }
                }

                // Section 2: ƯU ĐÃI CHO KHÁCH MỚI
                WoodenSectionTitle("ƯU ĐÃI CHO KHÁCH MỚI")
                
                val products = listOf(
                    CouponProduct("Nước mắm cá cơm K...", "29.000đ", "53.000đ", "-45%"),
                    CouponProduct("Nước xả Comfort diệ...", "168.000đ", "238.000đ", "-29%"),
                    CouponProduct("Nước xả Comfort tinh...", "168.000đ", "238.000đ", "-29%"),
                    CouponProduct("Nước xả Comfort hươ...", "168.000đ", "238.000đ", "-29%"),
                    CouponProduct("Bột giặt Omo Matic...", "168.000đ", "238.000đ", "-28%"),
                    CouponProduct("Nước xả Comfort...", "168.000đ", "238.000đ", "-29%")
                )
                
                products.chunked(2).forEach { rowItems ->
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
