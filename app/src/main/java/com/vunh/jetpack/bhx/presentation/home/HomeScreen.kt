package com.vunh.jetpack.bhx.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.vunh.jetpack.bhx.domain.model.*
import com.vunh.jetpack.bhx.presentation.common.HeaderSection
import kotlinx.coroutines.delay
import kotlinx.coroutines.yield

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onMenuClick: () -> Unit, onNavigateToProfile: () -> Unit, isLoggedIn: Boolean) {
    var showLoginDialog by remember { mutableStateOf(false) }
    var showProductSheet by remember { mutableStateOf(false) }
    var showIngredientSheet by remember { mutableStateOf(false) }
    var selectedProductForSheet by remember { mutableStateOf<ProductItem?>(null) }
    var selectedRecipeForSheet by remember { mutableStateOf<Recipe?>(null) }
    val productSheetState = rememberModalBottomSheetState()
    val ingredientSheetState = rememberModalBottomSheetState()

    val onActionClick = {
        if (isLoggedIn) {
            // Logged in action
        } else {
            showLoginDialog = true
        }
    }

    if (showLoginDialog) {
        LoginRequiredDialog(
            onDismiss = { showLoginDialog = false },
            onConfirm = {
                showLoginDialog = false
                onNavigateToProfile()
            }
        )
    }

    if (showProductSheet) {
        ModalBottomSheet(
            onDismissRequest = { showProductSheet = false },
            sheetState = productSheetState,
            dragHandle = null,
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
        ) {
            ProductSelectionBottomSheet(
                productName = selectedProductForSheet?.name ?: "",
                onDismiss = { showProductSheet = false }
            )
        }
    }

    if (showIngredientSheet) {
        ModalBottomSheet(
            onDismissRequest = { showIngredientSheet = false },
            sheetState = ingredientSheetState,
            dragHandle = null,
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
        ) {
            IngredientSelectionBottomSheet(
                recipeName = selectedRecipeForSheet?.name ?: "",
                onDismiss = { showIngredientSheet = false }
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        HeaderSection(isHome = true, onMenuClick = onMenuClick)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            MainBannerSection(onClick = onActionClick)
            PromoGridSection(onClick = onActionClick)
            CategorySection(onClick = onActionClick)
            EssentialProductsSection(onClick = { product ->
                if (isLoggedIn) {
                    selectedProductForSheet = product
                    showProductSheet = true
                } else {
                    showLoginDialog = true
                }
            })
            DailyMarketSection(
                onCategoryClick = onActionClick,
                onRecipeClick = { _ -> onActionClick() },
                onProductClick = { product ->
                    if (isLoggedIn) {
                        selectedRecipeForSheet = Recipe(product.name)
                        showIngredientSheet = true
                    } else {
                        showLoginDialog = true
                    }
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun IngredientSelectionBottomSheet(recipeName: String, onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.9f) // Allow sheet to expand
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp) // Space for fixed button
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Nguyên liệu món $recipeName",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E3A59)
                )
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color(0xFFE4E9F2), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color(0xFF8F9BB3),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Cooking instructions link
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable { },
                color = Color(0xFFF7F9FC),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("IMG", fontSize = 10.sp, color = Color.Gray)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Xem hướng dẫn chế biến",
                        modifier = Modifier.weight(1f),
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main ingredients section
            Text(
                text = "Lựa chọn loại nguyên liệu chính",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                fontSize = 14.sp,
                color = Color.Gray
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(4) { index ->
                    IngredientOptionCard(index)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(3) { index ->
                    IngredientOptionCard(index + 4)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Extra spices section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF7F9FC))
                    .padding(16.dp)
            ) {
                Text(
                    text = "Mua thêm gia vị",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(3) {
                        SuggestedProductCard()
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Fixed Add to cart button at the bottom
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Button(
                onClick = { /* Add to cart logic */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9EABB8))
            ) {
                Text(
                    text = "THÊM VÀO GIỎ",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun IngredientOptionCard(index: Int) {
    val names = listOf(
        "Ba rọi heo C.P 300...", "Sườn non heo C.P...", "Sườn non heo Bra...", "Thịt nạc heo...",
        "Trứng gà tươi hộp...", "Trứng cút hộp 30...", "Trứng vịt hộp 10..."
    )
    val prices = listOf("44.145₫", "63.175₫", "47.025₫", "41.860₫", "27.000₫", "25.000₫", "32.000₫")
    val units = listOf("/Vỉ 0.3 Kg", "/Vỉ 0.3 Kg", "/Túi 0.3 Kg", "/Vỉ 0.3 Kg", "", "", "")

    Card(
        modifier = Modifier
            .width(140.dp)
            .border(1.dp, Color(0xFFEDF1F7), RoundedCornerShape(8.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(Color(0xFFF7F9FC)),
                contentAlignment = Alignment.Center
            ) {
                Text("IMAGE", color = Color.LightGray, fontSize = 10.sp)
                // Badge for CP or Import
                if (index < 4) {
                    Box(modifier = Modifier.align(Alignment.TopStart).padding(4.dp).size(20.dp).background(Color.Red, CircleShape), contentAlignment = Alignment.Center) {
                        Text("CP", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = names[index % names.size],
                fontSize = 12.sp,
                maxLines = 2,
                minLines = 2,
                lineHeight = 16.sp,
                color = Color.Black
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Column {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = prices[index % prices.size],
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = units[index % units.size],
                        fontSize = 10.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(start = 2.dp, bottom = 1.dp)
                    )
                }
                if (index == 0) {
                    Text("Tối đa 10 sản phẩm/ đơn", color = Color(0xFFE67E22), fontSize = 10.sp, lineHeight = 12.sp)
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedButton(
                onClick = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp),
                shape = RoundedCornerShape(4.dp),
                border = borderStroke(1.dp, Color(0xFFE8F5E9)),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFFF1F8E9))
            ) {
                Text("MUA", color = Color(0xFF008848), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ProductSelectionBottomSheet(productName: String, onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 80.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Chọn mua $productName",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E3A59)
                )
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color(0xFFE4E9F2), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color(0xFF8F9BB3),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Product Options List
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(4) { index ->
                    ProductOptionCard(index)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Xem tất cả >",
                color = Color(0xFF008848),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(end = 16.dp)
                    .clickable { }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Suggested items
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF7F9FC))
                    .padding(16.dp)
            ) {
                Text(
                    text = "Gợi ý mua thêm",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(3) {
                        SuggestedProductCard()
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Fixed Add to cart button
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Button(
                onClick = { /* Add to cart logic */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9EABB8))
            ) {
                Text(
                    text = "THÊM VÀO GIỎ",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun ProductOptionCard(index: Int) {
    val names = listOf("Thùng 30 gói mì 3...", "Mì xào tương đen...", "Thùng 20 gói mì tr...", "Mì Omachi l...")
    val prices = listOf("90.000₫", "11.500₫", "124.000₫", "9.000₫")
    val discounts = listOf("-6%", "-15%", "-20%", null)

    Card(
        modifier = Modifier
            .width(140.dp)
            .border(1.dp, Color(0xFFEDF1F7), RoundedCornerShape(8.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(Color(0xFFF7F9FC)),
                contentAlignment = Alignment.Center
            ) {
                Text("IMAGE", color = Color.LightGray, fontSize = 10.sp)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = names[index % names.size],
                fontSize = 12.sp,
                maxLines = 2,
                minLines = 2,
                lineHeight = 16.sp,
                color = Color.Black
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = prices[index % prices.size],
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                discounts[index % discounts.size]?.let {
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = it,
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .background(Color.Red, RoundedCornerShape(2.dp))
                            .padding(horizontal = 2.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedButton(
                onClick = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp),
                shape = RoundedCornerShape(4.dp),
                border = borderStroke(1.dp, Color(0xFFE8F5E9)),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFFF1F8E9))
            ) {
                Text("MUA", color = Color(0xFF008848), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun SuggestedProductCard() {
    Card(
        modifier = Modifier
            .width(220.dp)
            .height(80.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize().padding(8.dp)) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(Color(0xFFF7F9FC)),
                contentAlignment = Alignment.Center
            ) {
                Text("IMG", fontSize = 10.sp, color = Color.LightGray)
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text("Xúc xích heo tiệt...", fontSize = 12.sp, maxLines = 1)
                Text("25.000₫", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                
                Spacer(modifier = Modifier.weight(1f))
                
                Surface(
                    color = Color(0xFFF1F8E9),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(24.dp)
                        .clickable { }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("MUA", color = Color(0xFF008848), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun LoginRequiredDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Tính năng này cần đăng nhập để sử dụng. Anh/Chị vui lòng đăng nhập để tiếp tục",
                    fontSize = 16.sp,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        border = borderStroke(1.dp, Color(0xFF008848))
                    ) {
                        Text("Hủy", color = Color(0xFF008848), fontWeight = FontWeight.Bold)
                    }
                    
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF008848))
                    ) {
                        Text("Đồng ý", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// Helper for border stroke
@Composable
fun borderStroke(width: androidx.compose.ui.unit.Dp, color: Color) = androidx.compose.foundation.BorderStroke(width, color)

@Composable
fun MainBannerSection(onClick: () -> Unit) {
    val bannerCount = 6
    val pagerState = rememberPagerState(pageCount = { bannerCount })

    LaunchedEffect(Unit) {
        while (true) {
            yield()
            delay(3000)
            val nextPage = (pagerState.currentPage + 1) % bannerCount
            pagerState.animateScrollToPage(nextPage)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clickable { onClick() }
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val bgColor = when (page % 3) {
                0 -> Color(0xFF004D40)
                1 -> Color(0xFF00695C)
                else -> Color(0xFF00796B)
            }
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(bgColor)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Ưu đãi tháng ${page + 1}", color = Color.White, fontSize = 14.sp)
                    Text(
                        "NÂNG CHUẨN AN TOÀN\nBANNER KHUYẾN MÃI ${page + 1}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        lineHeight = 24.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = Color(0xFF8CC63F),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            "Xem ngay",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
        
        Row(
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            repeat(bannerCount) { index ->
                val isSelected = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .size(if (isSelected) 12.dp else 6.dp, 6.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) Color.White else Color.White.copy(alpha = 0.5f))
                )
            }
        }
    }
}

@Composable
fun PromoGridSection(onClick: () -> Unit) {
    Column(modifier = Modifier.padding(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PromoCard(
                title = "Mua hàng Unilever X2 X3 TÍCH ĐIỂM",
                backgroundColor = Color(0xFFFFEBEE),
                modifier = Modifier.weight(1f).clickable { onClick() }
            )
            PromoCard(
                title = "GIẶT - XẢ GIÁ TỐT",
                backgroundColor = Color(0xFFE3F2FD),
                modifier = Modifier.weight(1f).clickable { onClick() }
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PromoCard(
                title = "DEAL ĐÃ GIÁ HỜI CÙNG PEPSI",
                backgroundColor = Color(0xFFE8EAF6),
                modifier = Modifier.weight(1f).clickable { onClick() },
                height = 100.dp
            )
            PromoCard(
                title = "GÓP BÀN CHẢI CŨ DỰNG TƯƠNG LAI XANH",
                backgroundColor = Color(0xFFE8F5E9),
                modifier = Modifier.weight(1f).clickable { onClick() },
                height = 100.dp
            )
        }
    }
}

@Composable
fun PromoCard(title: String, backgroundColor: Color, modifier: Modifier = Modifier, height: androidx.compose.ui.unit.Dp = 180.dp) {
    Card(
        modifier = modifier.height(height),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(8.dp)) {
            Text(
                title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.TopCenter),
                color = Color.Black
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .align(Alignment.Center)
                    .background(Color.White.copy(alpha = 0.5f), RoundedCornerShape(4.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("IMAGE", color = Color.Gray, fontSize = 10.sp)
            }
        }
    }
}

@Composable
fun CategorySection(onClick: () -> Unit) {
    val categories = listOf(
        CategoryItem("FLASH SALE", Color.Red),
        CategoryItem("Giặt xả", Color.Transparent, "-35%"),
        CategoryItem("Bia", Color.Transparent),
        CategoryItem("Nước suối", Color.Transparent, "79k/thùng"),
        CategoryItem("Sữa tươi", Color.Transparent),
        CategoryItem("Xúc xích", Color.Transparent)
    )

    LazyRow(
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.background(Color.White)
    ) {
        items(categories) { item ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(70.dp).clickable { onClick() }
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF0F0F0)),
                    contentAlignment = Alignment.Center
                ) {
                    if (item.name == "FLASH SALE") {
                        Text("SALE", color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                    } else {
                        Box(modifier = Modifier.fillMaxSize().padding(8.dp).background(Color.LightGray))
                    }
                    if (item.badge != null) {
                        Text(
                            item.badge,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .background(Color.Red, RoundedCornerShape(bottomStart = 4.dp))
                                .padding(horizontal = 4.dp),
                            color = Color.White,
                            fontSize = 8.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    item.name,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    lineHeight = 14.sp,
                    color = if (item.name == "FLASH SALE") Color(0xFF008848) else Color.Black,
                    fontWeight = if (item.name == "FLASH SALE") FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun EssentialProductsSection(onClick: (ProductItem) -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            "SẢN PHẨM THIẾT YẾU",
            color = Color(0xFFE67E22),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(12.dp))

        val products = listOf(
            ProductItem("MÌ 3 MIỀN 65G", "CHỈ 90K/THÙNG", Color.Red),
            ProductItem("SỮA THÙNG", "GIÁ TIẾT KIỆM", Color(0xFFE91E63)),
            ProductItem("NƯỚC KHOÁNG", "TỪ 79K", Color(0xFF1976D2)),
            ProductItem("SỮA CHUA", "MUA 2 TẶNG 1", Color(0xFFE91E63)),
            ProductItem("KEM", "MUA 2 TẶNG 1", Color(0xFFE91E63)),
            ProductItem("BÁNH KARO", "2 GÓI 59K", Color(0xFFF57C00))
        )

        val rows = products.chunked(2)
        rows.forEach { rowProducts ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowProducts.forEach { product ->
                    ProductCard(product, modifier = Modifier.weight(1f).clickable { onClick(product) })
                }
                if (rowProducts.size < 2) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun ProductCard(product: ProductItem, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .aspectRatio(0.7f)
            .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(8.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(8.dp)
                    .background(Color(0xFFF9F9F9)),
                contentAlignment = Alignment.Center
            ) {
                Text("IMAGE", color = Color.LightGray)
            }
            
            Column(
                modifier = Modifier.padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    product.name,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = product.buttonColor,
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        product.promoText,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DailyMarketSection(
    onCategoryClick: () -> Unit,
    onRecipeClick: (Recipe) -> Unit,
    onProductClick: (FreshProduct) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFE8F5E9))
            .padding(vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                buildAnnotatedString {
                    withStyle(style = SpanStyle(color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)) {
                        append("ĐI CHỢ MỖI NGÀY ")
                    }
                    withStyle(style = SpanStyle(color = Color(0xFF2E7D32), fontSize = 12.sp)) {
                        append("(Mua hàng tươi sống 150k, freeship 3km)")
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        val tabs = listOf(
            MarketTab("Món mặn", true),
            MarketTab("Xào, luộc", false, "-46%"),
            MarketTab("Món canh", false),
            MarketTab("Rau sống", false),
            MarketTab("Trái cây", false, "-32%")
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(tabs) { tab ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (tab.isSelected) Color(0xFF2E7D32) else Color.White)
                        .border(1.dp, if (tab.isSelected) Color(0xFF2E7D32) else Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .clickable { onCategoryClick() }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            tab.name,
                            color = if (tab.isSelected) Color.White else Color(0xFF2E7D32),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    if (tab.badge != null) {
                        Text(
                            tab.badge,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .background(Color.Red, RoundedCornerShape(4.dp))
                                .padding(horizontal = 2.dp),
                            color = Color.White,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val recipes = listOf(
            Recipe("Thịt kho trứng"),
            Recipe("Gà luộc"),
            Recipe("Cơm rang dưa")
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(recipes) { recipe ->
                RecipeCard(recipe, onClick = { onRecipeClick(recipe) })
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val freshProducts = listOf(
            FreshProduct("Trứng gà tươi hộp 10 quả", "27.000đ"),
            FreshProduct("Ba rọi heo 300g", "44.145đ", "54.500đ", "-19%", "CP"),
            FreshProduct("Ba rọi heo Nhập khẩu Nga 300g", "37.720đ", "46.000đ", null, null, "Nhập Khẩu Nga")
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(freshProducts) { product ->
                FreshProductCard(product, onClick = { onProductClick(product) })
            }
        }
    }
}

@Composable
fun RecipeCard(recipe: Recipe, onClick: () -> Unit) {
    Card(
        modifier = Modifier.width(140.dp).clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(Color.LightGray),
                contentAlignment = Alignment.BottomStart
            ) {
                Text(
                    recipe.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.3f))
                        .padding(8.dp),
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable { onClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "MUA NGUYÊN LIỆU",
                    color = Color(0xFF008848),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun FreshProductCard(product: FreshProduct, onClick: () -> Unit) {
    Card(
        modifier = Modifier.width(140.dp).clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Box(modifier = Modifier.fillMaxWidth()) {
                if (product.topBadge != null) {
                    Text(
                        product.topBadge,
                        modifier = Modifier.align(Alignment.TopEnd),
                        color = Color.Red,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                if (product.brandBadge != null) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Color.Red)
                            .align(Alignment.TopStart),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(product.brandBadge, color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    }
                }
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .padding(top = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("IMAGE", color = Color.LightGray, fontSize = 10.sp)
                }

                if (product.discountBadge != null) {
                    Text(
                        product.discountBadge,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .background(Color.Red, RoundedCornerShape(2.dp))
                            .padding(horizontal = 2.dp),
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                product.name,
                fontSize = 12.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.height(32.dp),
                lineHeight = 16.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                product.price,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            if (product.oldPrice != null) {
                Text(
                    product.oldPrice,
                    fontSize = 11.sp,
                    color = Color.Gray,
                    textDecoration = TextDecoration.LineThrough
                )
            }
        }
    }
}
