package com.vunh.jetpack.bhx.data.remote.model

import com.google.gson.annotations.SerializedName

data class DummyProductResponse(
    val products: List<DummyProductModel>,
    val total: Int,
    val skip: Int,
    val limit: Int
)

data class DummyProductModel(
    val id: Int,
    val title: String,
    val description: String,
    val price: Double,
    val category: String,
    val thumbnail: String,
    val images: List<String>
)
