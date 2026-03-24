package com.vunh.jetpack.bhx.data.remote.model

data class ProductEscuelaModel(
    val id: Int,
    val title: String,
    val slug: String,
    val price: Int,
    val description: String,
    val category: ProductEscuelaCategoryModel,
    val images: List<String>,
    val creationAt: String,
    val updatedAt: String
)

data class ProductEscuelaCategoryModel(
    val id: Int,
    val name: String,
    val slug: String,
    val image: String,
    val creationAt: String,
    val updatedAt: String
)
