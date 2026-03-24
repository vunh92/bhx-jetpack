package com.vunh.jetpack.bhx.data.remote

import com.vunh.jetpack.bhx.data.remote.model.ProductEscuelaModel
import retrofit2.http.GET
import retrofit2.http.Query

interface EscuelaApiService {
    @GET("v1/products")
    suspend fun getProducts(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): List<ProductEscuelaModel>
}
