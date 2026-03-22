package com.vunh.jetpack.bhx.presentation.category

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun CategoryScreen(
    onClose: () -> Unit,
    onHomeClick: () -> Unit,
    viewModel: CategoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        // Header
        CategoryHeader(onClose, onHomeClick)

        Row(modifier = Modifier.fillMaxSize()) {
            // Sidebar bên trái
            Box(modifier = Modifier.weight(0.3f).fillMaxHeight().background(Color(0xFFF1F8E9))) {
                LazyColumn {
                    items(uiState.availableMainCategories) { category ->
                        CategorySideItem(
                            category = category,
                            isSelected = uiState.selectedCategoryId == category.id,
                            onClick = { viewModel.selectCategory(category.id) }
                        )
                    }
                }
            }

            // Nội dung bên phải
            Box(modifier = Modifier.weight(0.7f).fillMaxHeight().padding(8.dp)) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.selectedSubCategories) { subCat ->
                        SubCategoryItem(subCat)
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryHeader(onClose: () -> Unit, onHomeClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF4CAF50))
            .padding(top = 48.dp, bottom = 12.dp, start = 8.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable { onClose() }.padding(horizontal = 8.dp)
        ) {
            Icon(Icons.Default.Close, contentDescription = null, tint = Color.White)
            Text("Đóng", color = Color.White, fontSize = 10.sp)
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable { onHomeClick() }.padding(horizontal = 8.dp)
        ) {
            Icon(Icons.Default.Home, contentDescription = null, tint = Color.White)
            Text("Trang chủ", color = Color.White, fontSize = 10.sp)
        }
        
        Surface(
            modifier = Modifier.weight(1f).height(40.dp).padding(start = 8.dp),
            shape = RoundedCornerShape(8.dp),
            color = Color.White
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 12.dp)
            ) {
                Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Tìm nhanh trong nhóm hàng", color = Color.Gray, fontSize = 13.sp)
            }
        }
    }
}

@Composable
fun CategorySideItem(category: MainCat, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(if (isSelected) Color.White else Color.Transparent)
            .padding(vertical = 12.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (category.isHot) {
            Text("🔥", fontSize = 16.sp)
        } else {
            // Placeholder for icon
            Box(modifier = Modifier.size(30.dp).background(Color.LightGray, RoundedCornerShape(4.dp)))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = category.name,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            lineHeight = 14.sp,
            color = if (isSelected) Color(0xFF2E7D32) else Color.Black,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
        if (isSelected) {
            // Dash line indicator can be added here
        }
    }
}

@Composable
fun SubCategoryItem(subCat: SubCat) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .aspectRatio(1f)
                .fillMaxWidth()
                .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("IMG", fontSize = 10.sp, color = Color.Gray)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = subCat.name,
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            lineHeight = 13.sp
        )
    }
}

data class MainCat(val id: String, val name: String, val isHot: Boolean = false)
data class SubCat(val id: String, val mainId: String, val name: String)

val mainCategories = listOf(
    MainCat("1", "Khuyến mãi Hot", true),
    MainCat("2", "Thịt, cá, trứng, hải sản"),
    MainCat("3", "Rau, củ, nấm, trái cây"),
    MainCat("4", "Dầu ăn, nước chấm, gia vị"),
    MainCat("5", "Gạo, bột, đồ khô"),
    MainCat("6", "Mì, miến, cháo, phở"),
    MainCat("7", "Sữa các loại")
)

val subCategories = listOf(
    SubCat("101", "2", "Thịt heo"),
    SubCat("102", "2", "Thịt bò"),
    SubCat("103", "2", "Thịt gà, vịt"),
    SubCat("104", "2", "Cá, hải sản"),
    SubCat("105", "2", "Trứng gà, vịt, cút"),
    SubCat("201", "3", "Trái cây"),
    SubCat("202", "3", "Rau lá"),
    SubCat("203", "3", "Củ, quả"),
    SubCat("204", "3", "Nấm các loại"),
    SubCat("205", "3", "Hoa tươi"),
    SubCat("301", "4", "Dầu ăn"),
    SubCat("302", "4", "Nước mắm"),
    SubCat("303", "4", "Nước tương")
)
