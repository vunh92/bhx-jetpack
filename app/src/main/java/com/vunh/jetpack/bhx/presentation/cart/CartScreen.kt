package com.vunh.jetpack.bhx.presentation.cart

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.vunh.jetpack.bhx.R
import com.vunh.jetpack.bhx.data.remote.model.CartProduct
import com.vunh.jetpack.bhx.presentation.common.HeaderSection

@Composable
fun CartScreen(
    onMenuClick: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: CartViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val userProfile = uiState.userProfile
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
        } else if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF008848))
            }
        } else if (uiState.cartProducts.isEmpty()) {
            EmptyCartContent(
                userName = userProfile?.name?.split(" ")?.lastOrNull() ?: "",
                categories = uiState.categories,
                onNavigateToLogin = onNavigateToLogin
            )
        } else {
            CartListContent(
                products = uiState.cartProducts,
                userName = userProfile?.name?.split(" ")?.lastOrNull() ?: ""
            )
        }
    }
}

@Composable
fun CartListContent(products: List<CartProduct>, userName: String) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Giỏ hàng của bạn ($userName)",
            modifier = Modifier.padding(16.dp),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(products) { product ->
                CartProductItem(product)
            }
        }

        // Summary bar
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Tổng cộng", fontSize = 14.sp, color = Color.Gray)
                    val totalPrice = products.sumOf { it.total }
                    Text(
                        text = "${String.format("%,.0f", totalPrice).replace(',', '.')}đ",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.Red
                    )
                }
                Button(
                    onClick = { /* Checkout */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF008848)),
                    modifier = Modifier.height(48.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("THANH TOÁN", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun CartProductItem(product: CartProduct) {
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
                    .size(80.dp)
                    .clip(RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${String.format("%,.0f", product.price).replace(',', '.')}đ",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Red
                    )
                    if (product.discountPercentage > 0) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "-${product.discountPercentage.toInt()}%",
                            fontSize = 12.sp,
                            color = Color.White,
                            modifier = Modifier
                                .background(Color.Red, RoundedCornerShape(4.dp))
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Số lượng: ${product.quantity}",
                    fontSize = 13.sp,
                    color = Color.DarkGray
                )
            }
        }
    }
}

@Composable
fun EmptyCartContent(
    userName: String,
    categories: List<CartCategoryUi>,
    onNavigateToLogin: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Empty Cart Illustration Placeholder
        Box(
            modifier = Modifier
                .size(200.dp)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF008848).copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.ShoppingBasket,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color.LightGray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.cart_waiting_products, userName),
            fontSize = 15.sp,
            color = Color.DarkGray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Category List (Simplified)
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(categories) { category ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(70.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (category.isViewAll) Icons.Default.Menu else Icons.Default.ShoppingBasket,
                            contentDescription = null,
                            tint = if (category.isViewAll) Color(0xFF008848) else Color.Gray
                        )
                    }
                    Text(
                        text = category.title,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 14.sp,
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
            Text(stringResource(R.string.action_continue_shopping), fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = { /* Navigate to orders */ }) {
            Text(
                buildAnnotatedString {
                    withStyle(style = SpanStyle(color = Color.Gray)) {
                        append(stringResource(R.string.cart_or))
                    }
                    withStyle(style = SpanStyle(color = Color(0xFF008848))) {
                        append(stringResource(R.string.cart_view_previous_orders, userName))
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
