package com.vunh.jetpack.bhx.presentation.home

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.vunh.jetpack.bhx.R
import com.vunh.jetpack.bhx.domain.model.*
import com.vunh.jetpack.bhx.presentation.common.HeaderSection
import com.vunh.jetpack.bhx.ui.theme.BhxTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.yield

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onMenuClick: () -> Unit,
    onNavigateToProfile: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
    onEscuelaProductClick: (Int) -> Unit = {},
) {
    val context = LocalContext.current
    val userProfile by viewModel.userProfile.collectAsState()
    val posts by viewModel.posts.collectAsState()
    val products by viewModel.products.collectAsState()
    val productByCategories by viewModel.productByCategories.collectAsState()
    val dummyCategories by viewModel.dummyCategories.collectAsState()
    val productSectionTitle by viewModel.productSectionTitle.collectAsState()
    val selectedCategorySlug by viewModel.selectedCategorySlug.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState(initial = null)

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    HomeContent(
        userProfile = userProfile,
        posts = posts,
        products = products,
        productByCategories = productByCategories,
        dummyCategories = dummyCategories,
        productSectionTitle = productSectionTitle,
        selectedCategorySlug = selectedCategorySlug,
        isLoading = isLoading,
        isRefreshing = isRefreshing,
        errorMessage = errorMessage,
        onMenuClick = onMenuClick,
        onNavigateToProfile = onNavigateToProfile,
        onRefresh = viewModel::refreshAll,
        onFetchDummyProductsByCategory = viewModel::fetchDummyProductsByCategory,
        onEscuelaProductClick = onEscuelaProductClick,
        onFetchProducts = viewModel::fetchProducts,
        onRefreshPosts = viewModel::refreshPosts,
        onAddToCart = viewModel::addToCart
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    userProfile: UserProfile?,
    posts: List<Post>,
    products: List<Product>,
    productByCategories: List<Product>,
    dummyCategories: List<Category>,
    productSectionTitle: String,
    selectedCategorySlug: String,
    isLoading: Boolean,
    isRefreshing: Boolean,
    errorMessage: String?,
    onMenuClick: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onRefresh: () -> Unit,
    onFetchDummyProductsByCategory: (Category) -> Unit,
    onEscuelaProductClick: (Int) -> Unit,
    onFetchProducts: () -> Unit,
    onRefreshPosts: () -> Unit,
    onAddToCart: () -> Unit,
) {
    val isLoggedIn = userProfile != null
    
    var showLoginDialog by remember { mutableStateOf(false) }
    var showProductSheet by remember { mutableStateOf(false) }
    var showIngredientSheet by remember { mutableStateOf(false) }
    var selectedProductForSheet by remember { mutableStateOf<HomeDemoProduct?>(null) }
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
        HeaderSection(
            isHome = true, 
            userProfile = userProfile,
            onMenuClick = onMenuClick,
            onProfileClick = onNavigateToProfile
        )

        if (isLoading && posts.isEmpty() && products.isEmpty() && dummyCategories.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF008848))
            }
        } else {
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = onRefresh,
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
                                onActionClick = onRefresh
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    MainBannerSection(
                        products = products,
                        onClick = onActionClick
                    )

                    DummyCategoriesSection(
                        categories = dummyCategories,
                        selectedCategorySlug = selectedCategorySlug,
                        onCategoryClick = onFetchDummyProductsByCategory
                    )

                    EscuelaProductGridSection(
                        title = productSectionTitle,
                        products = productByCategories,
                        errorMessage = errorMessage,
                        onRetry = onRefresh,
                        onItemClick = onEscuelaProductClick,
                        onAddToCart = onAddToCart
                    )

                    DemoCategorySection(onClick = onActionClick)
                    DemoProductsSection(onClick = { product ->
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

                    HomeApiProductsSection(
                        posts = posts,
                        errorMessage = errorMessage,
                        onRetry = onRefreshPosts
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
    selectedCategorySlug: String,
    onCategoryClick: (Category) -> Unit,
    modifier: Modifier = Modifier
) {
    if (categories.isEmpty()) return

    val displayCategories = remember(categories) {
        listOf(
            Category(
                name = "All",
                slug = "all",
                url = ""
            )
        ) + categories
    }

    Column(
        modifier = modifier.padding(vertical = 16.dp)
    ) {
        val title = "DUMMY JSON CATEGORIES"
        Text(
            text = title,
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
            items(displayCategories) { category ->
                val isSelected = category.slug == selectedCategorySlug
                Surface(
                    color = if (isSelected) Color(0xFF008848) else Color.White,
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(
                        1.dp,
                        if (isSelected) Color(0xFF008848) else Color(0xFFBBDEFB)
                    ),
                    modifier = Modifier.clickable { onCategoryClick(category) }
                ) {
                    Text(
                        text = category.name.replaceFirstChar { it.uppercase() },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        fontSize = 13.sp,
                        color = if (isSelected) Color.White else Color(0xFF0D47A1)
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
    modifier: Modifier = Modifier,
    onItemClick: (Int) -> Unit = {},
    onAddToCart: () -> Unit = {}
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
                                modifier = Modifier.weight(1f),
                                onItemClick = { onItemClick(product.id) },
                                onAddToCart = onAddToCart
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
    modifier: Modifier = Modifier,
    onItemClick: () -> Unit,
    onAddToCart: () -> Unit = {}
) {
    Card(
        modifier = modifier.clickable(onClick = onItemClick),
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
                onClick = onAddToCart,
                modifier = Modifier.fillMaxWidth().height(36.dp),
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF008848)),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("THÊM VÀO GIỎ HÀNG", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
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
private fun MainBannerSection(
    products: List<Product>,
    onClick: () -> Unit
) {
    val bannerProducts = remember(products) { products.take(5) }
    val fallbackBanners = listOf(
        Color(0xFFFFD180),
        Color(0xFF80D8FF),
        Color(0xFFA7FFEB)
    )
    val pageCount = if (bannerProducts.isNotEmpty()) bannerProducts.size else fallbackBanners.size
    val pagerState = rememberPagerState(pageCount = { pageCount })

    LaunchedEffect(pageCount) {
        while (true) {
            yield()
            delay(3000)
            pagerState.animateScrollToPage((pagerState.currentPage + 1) % pageCount)
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
            if (bannerProducts.isNotEmpty()) {
                val product = bannerProducts[page]

                Box(modifier = Modifier.fillMaxSize()) {
                    AsyncImage(
                        model = product.images.firstOrNull(),
                        contentDescription = product.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.28f))
                    )

                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = product.categoryName.uppercase(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFF59D)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = product.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(fallbackBanners[page])
                )
            }
        }

        // Pager indicators
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            repeat(pageCount) { iteration ->
                val color = if (pagerState.currentPage == iteration) Color.White else Color.White.copy(alpha = 0.5f)
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(color)
                )
            }
        }
    }
}

@Composable
fun DailyMarketSection(
    onCategoryClick: () -> Unit,
    onRecipeClick: (Recipe) -> Unit,
    onProductClick: (HomeDemoProduct) -> Unit
) {
    val recipes = listOf(
        Recipe("Nấu canh chua"),
        Recipe("Gỏi tôm thịt"),
        Recipe("Bò kho")
    )

    val products = listOf(
        HomeDemoProduct("Cà chua", "20.000đ", "https://img.freepik.com/free-photo/red-tomatoes_144627-15413.jpg"),
        HomeDemoProduct("Hành lá", "5.000đ", "https://img.freepik.com/free-photo/green-onions_144627-15403.jpg"),
        HomeDemoProduct("Tôm tươi", "150.000đ", "https://img.freepik.com/free-photo/fresh-shrimp_144627-15421.jpg")
    )

    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "ĐI CHỢ MỖI NGÀY",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF008848)
            )
            Text(
                text = "Xem tất cả",
                color = Color(0xFF1976D2),
                fontSize = 13.sp,
                modifier = Modifier.clickable(onClick = onCategoryClick)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Recipes horizontal list
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(recipes) { recipe ->
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                    modifier = Modifier.clickable { onRecipeClick(recipe) }
                ) {
                    Text(
                        text = recipe.name,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        fontSize = 13.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Products grid (simplified row for demo)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            products.forEach { product ->
                HomeDemoProductItem(
                    product = product,
                    modifier = Modifier.weight(1f),
                    onClick = { onProductClick(product) }
                )
            }
        }
    }
}

data class Recipe(val name: String)
data class HomeDemoProduct(val name: String, val price: String, val imageUrl: String)

@Composable
fun HomeDemoProductItem(
    product: HomeDemoProduct,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = product.imageUrl,
            contentDescription = product.name,
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(4.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = product.name,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
        Text(
            text = product.price,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Red
        )
    }
}

@Composable
fun DemoCategorySection(onClick: () -> Unit) {
    val categories = listOf(
        "Thịt, cá, trứng", "Rau, củ, trái cây", "Sữa, bỉm", "Đồ khô, gia vị"
    )

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(categories) { category ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(80.dp)
                    .clickable(onClick = onClick)
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE8F5E9)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = category.take(1), fontWeight = FontWeight.Bold, color = Color(0xFF008848))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = category,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 14.sp
                )
            }
        }
    }
}

@Composable
fun DemoProductsSection(onClick: (HomeDemoProduct) -> Unit) {
    val products = listOf(
        HomeDemoProduct("Dưa hấu hắc mỹ nhân", "12.000đ/kg", "https://img.freepik.com/free-photo/watermelon_144627-15417.jpg"),
        HomeDemoProduct("Cam sành túi 2kg", "35.000đ", "https://img.freepik.com/free-photo/oranges_144627-15409.jpg"),
        HomeDemoProduct("Ức gà phi lê 500g", "45.000đ", "https://img.freepik.com/free-photo/raw-chicken-breast_144627-15411.jpg")
    )

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "SẢN PHẨM KHUYẾN MÃI",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = Color(0xFF008848)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            products.forEach { product ->
                HomeDemoProductItem(
                    product = product,
                    modifier = Modifier.weight(1f),
                    onClick = { onClick(product) }
                )
            }
        }
    }
}

@Composable
fun LoginRequiredDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Yêu cầu đăng nhập") },
        text = { Text("Vui lòng đăng nhập để thực hiện chức năng này.") },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF008848))
            ) {
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
            Text(text = "Chọn loại: $productName", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "Close")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        // Selection options...
        repeat(3) { i ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onDismiss() }
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Tùy chọn ${i + 1}")
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
            }
            HorizontalDivider(color = Color(0xFFF5F5F5))
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF008848))
        ) {
            Text("Xác nhận")
        }
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
            Text(text = "Nguyên liệu cho: $recipeName", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "Close")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        // Ingredient list with checkboxes...
        repeat(4) { i ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(checked = true, onCheckedChange = {})
                Text(text = "Nguyên liệu ${i + 1}")
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF008848))
        ) {
            Text("Thêm vào giỏ hàng")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeContentPreview() {
    BhxTheme {
        HomeContent(
            userProfile = null,
            posts = emptyList(),
            products = emptyList(),
            productByCategories = emptyList(),
            dummyCategories = emptyList(),
            productSectionTitle = "Demo",
            selectedCategorySlug = "all",
            isLoading = false,
            isRefreshing = false,
            errorMessage = null,
            onMenuClick = {},
            onNavigateToProfile = {},
            onRefresh = {},
            onFetchDummyProductsByCategory = {},
            onEscuelaProductClick = {},
            onFetchProducts = {},
            onRefreshPosts = {},
            onAddToCart = {}
        )
    }
}
