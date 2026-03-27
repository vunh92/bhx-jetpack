package com.vunh.jetpack.bhx.presentation.home

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.vunh.jetpack.bhx.R
import com.vunh.jetpack.bhx.domain.model.*
import com.vunh.jetpack.bhx.presentation.common.HeaderSection
import kotlinx.coroutines.delay
import kotlinx.coroutines.yield

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onMenuClick: () -> Unit,
    onNavigateToProfile: () -> Unit,
    isLoggedIn: Boolean,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val posts by viewModel.posts.collectAsState()
    val products by viewModel.products.collectAsState()
    val productByCategories by viewModel.productByCategories.collectAsState()
    val dummyCategories by viewModel.dummyCategories.collectAsState()
    val productSectionTitle by viewModel.productSectionTitle.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

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

        if (isLoading && posts.isEmpty() && products.isEmpty() && dummyCategories.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF008848))
            }
        } else {
            PullToRefreshBox(
                isRefreshing = isLoading,
                onRefresh = viewModel::refreshAll,
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    if (errorMessage != null &&
                        posts.isEmpty() &&
                        products.isEmpty() &&
                        dummyCategories.isEmpty()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            ApiProductStatusCard(
                                message = errorMessage.orEmpty(),
                                actionLabel = stringResource(R.string.home_retry),
                                onActionClick = viewModel::refreshAll
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    MainBannerSection(onClick = onActionClick)

                    DummyCategoriesSection(
                        categories = dummyCategories,
                        onCategoryClick = viewModel::fetchDummyProductsByCategory
                    )

                    EscuelaProductGridSection(
                        title = productSectionTitle,
                        products = productByCategories,
                        errorMessage = errorMessage,
                        onRetry = viewModel::refreshAll
                    )

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

                    EscuelaProductGridSection(
                        title = "SẢN PHẨM MỚI (ESCUELA API)",
                        products = products,
                        errorMessage = errorMessage,
                        onRetry = viewModel::fetchProducts
                    )

                    HomeApiProductsSection(
                        posts = posts,
                        errorMessage = errorMessage,
                        onRetry = viewModel::refreshPosts
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun DummyCategoriesSection(
    categories: List<Category>,
    onCategoryClick: (Category) -> Unit,
    modifier: Modifier = Modifier
) {
    if (categories.isEmpty()) return

    Column(
        modifier = modifier.padding(vertical = 16.dp)
    ) {
        Text(
            text = "DUMMY JSON CATEGORIES",
            modifier = Modifier.padding(horizontal = 16.dp),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color(0xFF1976D2)
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, Color(0xFFBBDEFB)),
                    modifier = Modifier.clickable { onCategoryClick(category) }
                ) {
                    Text(
                        text = category.name.replaceFirstChar { it.uppercase() },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        fontSize = 13.sp,
                        color = Color(0xFF0D47A1)
                    )
                }
            }
        }
    }
}

@Composable
private fun EscuelaProductGridSection(
    title: String,
    products: List<Product>,
    errorMessage: String?,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp)
    ) {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = Color(0xFF008848)
        )
        Spacer(modifier = Modifier.height(12.dp))

        when {
            products.isNotEmpty() -> {
                products.chunked(2).forEach { rowItems ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowItems.forEach { product ->
                            EscuelaProductItem(
                                product = product,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (rowItems.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            errorMessage != null && products.isEmpty() -> {
                ApiProductStatusCard(
                    message = errorMessage,
                    actionLabel = stringResource(R.string.home_retry),
                    onActionClick = onRetry
                )
            }
        }
    }
}

@Composable
private fun EscuelaProductItem(
    product: Product,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            AsyncImage(
                model = product.images.firstOrNull(),
                contentDescription = product.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = product.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = product.categoryName,
                fontSize = 12.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${product.price}$",
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFFE67E22)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth().height(36.dp),
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF008848)),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("MUA", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun HomeApiProductsSection(
    posts: List<Post>,
    errorMessage: String?,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (posts.isEmpty()) return
    
    Column(
        modifier = modifier.padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.home_api_products_title),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(12.dp))

        when {
            posts.isNotEmpty() -> {
                posts.forEach { post ->
                    HomeApiProductItem(
                        post = post,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                    )
                }
            }

            errorMessage != null && posts.isEmpty() -> {
                ApiProductStatusCard(
                    message = errorMessage,
                    actionLabel = stringResource(R.string.home_retry),
                    onActionClick = onRetry
                )
            }
        }
    }
}

@Composable
private fun HomeApiProductItem(
    post: Post,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFE8F5E9)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = post.id.toString(),
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF008848)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = post.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "User ID: ${post.userId}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
private fun ApiProductStatusCard(
    message: String,
    actionLabel: String,
    onActionClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = message,
                color = Color(0xFFE65100),
                textAlign = TextAlign.Center,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = onActionClick) {
                Text(actionLabel, color = Color(0xFFEF6C00), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun MainBannerSection(onClick: () -> Unit) {
    val banners = listOf(
        Color(0xFFFFD180),
        Color(0xFF80D8FF),
        Color(0xFFA7FFEB)
    )
    val pagerState = rememberPagerState(pageCount = { banners.size })

    LaunchedEffect(Unit) {
        while (true) {
            yield()
            delay(3000)
            pagerState.animateScrollToPage((pagerState.currentPage + 1) % banners.size)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(16.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(banners[page]),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "KHUYẾN MÃI HẤP DẪN ${page + 1}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.White
                )
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(banners.size) { iteration ->
                val color = if (pagerState.currentPage == iteration) Color.White else Color.White.copy(alpha = 0.5f)
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(6.dp)
                )
            }
        }
    }
}

@Composable
private fun CategorySection(onClick: () -> Unit) {
    val categories = listOf(
        "Thịt, cá, trứng", "Rau, củ, trái cây", "Gạo, mì, gia vị", "Sữa, đồ uống",
        "Bánh kẹo, ăn vặt", "Vệ sinh nhà cửa", "Chăm sóc cá nhân", "Đồ dùng gia đình"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color.White, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "DANH MỤC NGÀNH HÀNG",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        categories.chunked(4).forEach { rowCategories ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                rowCategories.forEach { category ->
                    CategoryItem(name = category, onClick = onClick)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun CategoryItem(name: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(70.dp)
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(Color(0xFFE8F5E9), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            // Placeholder for icon
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = name,
            fontSize = 10.sp,
            textAlign = TextAlign.Center,
            lineHeight = 12.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun EssentialProductsSection(onClick: (ProductItem) -> Unit) {
    val products = listOf(
        ProductItem("Nước mắm Nam Ngư 750ml", 45000, 39000, "15%"),
        ProductItem("Dầu ăn Tường An 1L", 52000, 48000, "8%"),
        ProductItem("Gạo ST25 túi 5kg", 195000, 185000, "5%"),
        ProductItem("Mì Hảo Hảo tôm chua cay", 4500, 4200, "7%")
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color(0xFFE8F5E9), RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "HÀNG THIẾT YẾU - GIÁ RẺ",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF008848)
            )
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color(0xFF008848)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            products.forEach { product ->
                ProductItemCard(product = product, onClick = { onClick(product) }, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun ProductItemCard(product: ProductItem, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(Color(0xFFF5F5F5))
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = product.name,
                fontSize = 11.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 13.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${product.price}đ",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Red
            )
            Text(
                text = "${product.originalPrice}đ",
                fontSize = 10.sp,
                color = Color.Gray,
                textDecoration = TextDecoration.LineThrough
            )
        }
    }
}

@Composable
private fun DailyMarketSection(
    onCategoryClick: () -> Unit,
    onRecipeClick: (String) -> Unit,
    onProductClick: (ProductItem) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "ĐI CHỢ MỖI NGÀY",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Left column: categories
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Thịt các loại", "Cá, hải sản", "Trứng các loại", "Rau củ tươi").forEach { cat ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .background(Color.White, RoundedCornerShape(8.dp))
                            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
                            .clickable(onClick = onCategoryClick)
                            .padding(horizontal = 8.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(text = cat, fontSize = 12.sp)
                    }
                }
            }

            // Right column: Featured recipe or product
            Card(
                modifier = Modifier
                    .weight(1.5f)
                    .height(184.dp)
                    .clickable { onRecipeClick("Canh chua cá lóc") },
                shape = RoundedCornerShape(12.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFBDBDBD)))
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .background(Color.Black.copy(alpha = 0.6f))
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Text(text = "Gợi ý hôm nay", color = Color.Yellow, fontSize = 10.sp)
                        Text(text = "Canh chua cá lóc", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun LoginRequiredDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Đăng nhập") },
        text = { Text("Vui lòng đăng nhập để thực hiện chức năng này.") },
        confirmButton = {
            Button(onClick = onConfirm, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF008848))) {
                Text("Đăng nhập")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Bỏ qua", color = Color.Gray)
            }
        }
    )
}

@Composable
fun ProductSelectionBottomSheet(productName: String, onDismiss: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Chọn định lượng", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "Close")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(text = productName, fontWeight = FontWeight.Medium, fontSize = 16.sp)
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Quantity selectors (mock)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            listOf("300g", "500g", "1kg").forEach { weight ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, Color(0xFF008848), RoundedCornerShape(8.dp))
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = weight, color = Color(0xFF008848))
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF008848))
        ) {
            Text("THÊM VÀO GIỎ HÀNG", fontWeight = FontWeight.Bold)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun IngredientSelectionBottomSheet(recipeName: String, onDismiss: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Nguyên liệu nấu món", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "Close")
            }
        }
        
        Text(text = recipeName, color = Color(0xFF008848), fontWeight = FontWeight.Medium)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Ingredients list (mock)
        repeat(3) { i ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(40.dp).background(Color(0xFFF5F5F5), RoundedCornerShape(4.dp)))
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Nguyên liệu ${i + 1}", fontSize = 14.sp)
                    Text(text = "20.000đ", fontSize = 12.sp, color = Color.Red, fontWeight = FontWeight.Bold)
                }
                Checkbox(checked = true, onCheckedChange = {}, colors = CheckboxDefaults.colors(checkedColor = Color(0xFF008848)))
            }
            HorizontalDivider(color = Color(0xFFEEEEEE))
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF008848))
        ) {
            Text("MUA TẤT CẢ NGUYÊN LIỆU", fontWeight = FontWeight.Bold)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

data class ProductItem(
    val name: String,
    val originalPrice: Int,
    val price: Int,
    val discount: String
)
