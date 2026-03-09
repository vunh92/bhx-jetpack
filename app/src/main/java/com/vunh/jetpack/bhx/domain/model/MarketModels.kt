package com.vunh.jetpack.bhx.domain.model

import androidx.compose.ui.graphics.Color

data class CategoryItem(val name: String, val color: Color, val badge: String? = null)

data class ProductItem(val name: String, val promoText: String, val buttonColor: Color)

data class MarketTab(val name: String, val isSelected: Boolean, val badge: String? = null)

data class Recipe(val name: String)

data class FreshProduct(
    val name: String,
    val price: String,
    val oldPrice: String? = null,
    val discountBadge: String? = null,
    val brandBadge: String? = null,
    val topBadge: String? = null
)
