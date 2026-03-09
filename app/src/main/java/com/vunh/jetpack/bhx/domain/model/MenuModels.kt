package com.vunh.jetpack.bhx.domain.model

data class MainCategory(
    val id: String,
    val name: String,
    val iconRes: Int? = null, // Placeholder for icon
    val isHot: Boolean = false
)

data class SubCategory(
    val id: String,
    val mainCategoryId: String,
    val name: String,
    val iconRes: Int? = null // Placeholder for icon
)
