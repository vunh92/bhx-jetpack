package com.vunh.jetpack.bhx.presentation.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.vunh.jetpack.bhx.data.remote.model.CartProduct
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartDetailScreen(
    onBack: () -> Unit,
    viewModel: CartDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chi tiết giỏ hàng #${uiState.cart?.id ?: ""}") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                )
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color(0xFF008848)
                )
            } else if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage!!,
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center).padding(16.dp)
                )
            } else {
                uiState.cart?.let { cart ->
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            CartSummaryInfo(
                                totalProducts = cart.totalProducts,
                                totalQuantity = cart.totalQuantity,
                                totalAmount = cart.discountedTotal
                            )
                        }
                        
                        items(cart.products) { product ->
                            DetailProductItem(product)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CartSummaryInfo(totalProducts: Int, totalQuantity: Int, totalAmount: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Thông tin chung", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Số loại sản phẩm:", color = Color.Gray)
                Text("$totalProducts", fontWeight = FontWeight.Medium)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Tổng số lượng:", color = Color.Gray)
                Text("$totalQuantity", fontWeight = FontWeight.Medium)
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF5F5F5))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Tổng thanh toán:", fontWeight = FontWeight.Bold)
                Text(
                    text = "${String.format(Locale.getDefault(), "%,.0f", totalAmount).replace(',', '.')}đ",
                    fontWeight = FontWeight.Bold,
                    color = Color.Red,
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Composable
fun DetailProductItem(product: CartProduct) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = product.thumbnail,
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Đơn giá: ${String.format(Locale.getDefault(), "%,.0f", product.price).replace(',', '.')}đ",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Text(
                    text = "Số lượng: ${product.quantity}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            Text(
                text = "${String.format(Locale.getDefault(), "%,.0f", product.total).replace(',', '.')}đ",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
