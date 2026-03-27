package com.vunh.jetpack.bhx.data.remote

import com.vunh.jetpack.bhx.data.remote.model.DummyCategoryModel
import com.vunh.jetpack.bhx.data.remote.model.DummyCategoryProductsResponse
import com.vunh.jetpack.bhx.data.remote.model.DummyProductResponse
import com.vunh.jetpack.bhx.data.remote.model.LoginRequest
import com.vunh.jetpack.bhx.data.remote.model.LoginResponse
import retrofit2.http.GET
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface DummyJsonApiService {
    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse

    @GET("products")
    suspend fun getProducts(
        @Query("limit") limit: Int,
        @Query("skip") skip: Int
    ): DummyProductResponse

    @GET("products/categories")
    suspend fun getCategories(): List<DummyCategoryModel>

    @GET("products/category/{name}")
    suspend fun getProductsByCategory(
        @Path("name") name: String,
        @Query("limit") limit: Int,
        @Query("skip") skip: Int
    ): DummyCategoryProductsResponse
}
