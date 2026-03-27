package com.vunh.jetpack.bhx.data.remote.model

data class DummyCategoryProductsResponse(
    val products: List<DummyCategoryProductModel>,
    val total: Int,
    val skip: Int,
    val limit: Int
)

data class DummyCategoryProductModel(
    val id: Int,
    val title: String,
    val description: String,
    val price: Double,
    val category: String,
    val thumbnail: String,
    val images: List<String>
)
