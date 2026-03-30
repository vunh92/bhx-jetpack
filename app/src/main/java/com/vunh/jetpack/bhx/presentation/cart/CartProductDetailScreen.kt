package com.vunh.jetpack.bhx.presentation.cart

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.vunh.jetpack.bhx.data.remote.model.CartProduct
import com.vunh.jetpack.bhx.ui.theme.BhxTheme
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartProductDetailScreen(
    cartId: Int,
    product: CartProduct?,
    onBack: () -> Unit,
    viewModel: CartProductDetailViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    var currentQuantity by remember(product) { mutableIntStateOf(product?.quantity ?: 1) }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            Toast.makeText(context, "Cập nhật giỏ hàng thành công", Toast.LENGTH_SHORT).show()
            viewModel.resetState()
            onBack()
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.resetState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chi tiết sản phẩm giỏ hàng") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF008848),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        bottomBar = {
            if (product != null) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 8.dp,
                    color = Color.White
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .navigationBarsPadding(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { 
                                viewModel.updateProductQuantity(cartId, product.id, currentQuantity)
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF008848)),
                            shape = RoundedCornerShape(8.dp),
                            enabled = !uiState.isLoading
                        ) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                            } else {
                                Text("Cập nhật")
                            }
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        if (product == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Không tìm thấy thông tin sản phẩm")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        AsyncImage(
                            model = product.thumbnail,
                            contentDescription = product.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Fit
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = product.title,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${String.format(Locale.getDefault(), "%,.0f", product.price).replace(',', '.')}đ",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.Red
                            )
                            if (product.discountPercentage > 0) {
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "-${product.discountPercentage.toInt()}%",
                                    fontSize = 14.sp,
                                    color = Color.White,
                                    modifier = Modifier
                                        .background(Color.Red, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(16.dp))

                        DetailRow(
                            label = "Đơn giá", 
                            value = "${String.format(Locale.getDefault(), "%,.0f", product.price).replace(',', '.')}đ"
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Số lượng", color = Color.Gray, fontSize = 14.sp)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                OutlinedIconButton(
                                    onClick = { if (currentQuantity > 1) currentQuantity-- },
                                    modifier = Modifier.size(32.dp),
                                    shape = RoundedCornerShape(4.dp),
                                    border = BorderStroke(1.dp, Color.LightGray)
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(16.dp))
                                }
                                Text(
                                    text = currentQuantity.toString(),
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                OutlinedIconButton(
                                    onClick = { currentQuantity++ },
                                    modifier = Modifier.size(32.dp),
                                    shape = RoundedCornerShape(4.dp),
                                    border = BorderStroke(1.dp, Color.LightGray)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(16.dp))
                                }
                            }
                        }

                        val displayTotal = product.price * currentQuantity
                        val displayDiscountedTotal = displayTotal * (1 - product.discountPercentage / 100.0)

                        DetailRow(
                            label = "Tổng cộng", 
                            value = "${String.format(Locale.getDefault(), "%,.0f", displayTotal).replace(',', '.')}đ"
                        )
                        DetailRow(
                            label = "Giá sau giảm", 
                            value = "${String.format(Locale.getDefault(), "%,.0f", displayDiscountedTotal).replace(',', '.')}đ",
                            valueColor = Color.Red
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun DetailRow(
    label: String, 
    value: String, 
    valueColor: Color = Color.Unspecified
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color.Gray, fontSize = 14.sp)
        Text(
            text = value, 
            fontWeight = FontWeight.Medium, 
            fontSize = 16.sp,
            color = valueColor
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CartProductDetailScreenPreview() {
    BhxTheme {
        CartProductDetailScreen(
            cartId = 1,
            product = CartProduct(
                id = 1,
                title = "Sữa tươi Vinamilk ít đường 1L",
                price = 35000.0,
                quantity = 2,
                total = 70000.0,
                discountPercentage = 10.0,
                discountedTotal = 63000.0,
                thumbnail = ""
            ),
            onBack = {}
        )
    }
}
