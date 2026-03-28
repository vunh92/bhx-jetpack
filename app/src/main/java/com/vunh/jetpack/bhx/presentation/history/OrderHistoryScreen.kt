package com.vunh.jetpack.bhx.presentation.history

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vunh.jetpack.bhx.R
import com.vunh.jetpack.bhx.data.remote.model.CartResponse
import com.vunh.jetpack.bhx.presentation.common.HeaderSection
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreen(
    onMenuClick: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: OrderHistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isLoggedIn = uiState.isLoggedIn

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
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
                    Text(stringResource(R.string.action_login), fontWeight = FontWeight.Bold)
                }
            }
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = stringResource(R.string.order_history_title),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(vertical = 12.dp),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                ScrollableTabRow(
                    selectedTabIndex = uiState.selectedTabIndex,
                    edgePadding = 16.dp,
                    containerColor = Color.White,
                    contentColor = Color(0xFF008848),
                    divider = {},
                    indicator = {}
                ) {
                    uiState.tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = uiState.selectedTabIndex == index,
                            onClick = { viewModel.selectTab(index) },
                            text = {
                                val isSelected = uiState.selectedTabIndex == index
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

                PullToRefreshBox(
                    isRefreshing = uiState.isLoading,
                    onRefresh = viewModel::refresh,
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (uiState.orders.isEmpty() && !uiState.isLoading) {
                        EmptyHistoryContent()
                    } else {
                        OrderHistoryList(orders = uiState.orders)
                    }
                }
            }
        }
    }
}

@Composable
fun OrderHistoryList(orders: List<CartResponse>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(orders) { order ->
            OrderItem(order = order)
        }
    }
}

@Composable
fun OrderItem(order: CartResponse) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Receipt,
                        contentDescription = null,
                        tint = Color(0xFF008848),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Đơn hàng #${order.id}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
                Text(
                    text = "Giao thành công",
                    color = Color(0xFF008848),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF5F5F5))
            
            order.products.take(2).forEach { product ->
                Text(
                    text = "${product.quantity} x ${product.title}",
                    fontSize = 14.sp,
                    color = Color.DarkGray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
            if (order.totalProducts > 2) {
                Text(
                    text = "và ${order.totalProducts - 2} sản phẩm khác...",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF5F5F5))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tổng cộng (${order.totalQuantity} món)",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
                Text(
                    text = "${String.format(Locale.getDefault(), "%,.0f", order.discountedTotal).replace(',', '.')}đ",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedButton(
                onClick = { /* Reorder logic */ },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color(0xFF008848)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF008848))
            ) {
                Text("MUA LẠI", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun EmptyHistoryContent() {
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
            text = stringResource(R.string.order_history_empty_message),
            textAlign = TextAlign.Center,
            fontSize = 15.sp,
            lineHeight = 22.sp,
            color = Color.Black
        )
    }
}
