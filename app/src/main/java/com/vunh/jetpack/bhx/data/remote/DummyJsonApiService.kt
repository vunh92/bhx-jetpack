package com.vunh.jetpack.bhx.data.remote

import com.vunh.jetpack.bhx.data.remote.model.DummyCategoryModel
import com.vunh.jetpack.bhx.data.remote.model.DummyProductResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface DummyJsonApiService {
    @GET("products")
    suspend fun getProducts(
        @Query("limit") limit: Int,
        @Query("skip") skip: Int
    ): DummyProductResponse

    @GET("products/categories")
    suspend fun getCategories(): List<DummyCategoryModel>
}
