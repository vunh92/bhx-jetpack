package com.vunh.jetpack.bhx.data.remote

import com.vunh.jetpack.bhx.data.remote.model.CartResponse
import com.vunh.jetpack.bhx.data.remote.model.DummyCategoryModel
import com.vunh.jetpack.bhx.data.remote.model.DummyCategoryProductsResponse
import com.vunh.jetpack.bhx.data.remote.model.DummyProductModel
import com.vunh.jetpack.bhx.data.remote.model.DummyProductResponse
import com.vunh.jetpack.bhx.data.remote.model.LoginRequest
import com.vunh.jetpack.bhx.data.remote.model.LoginResponse
import retrofit2.http.*

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

    @GET("products/{id}")
    suspend fun getProductById(
        @Path("id") productId: Int
    ): DummyProductModel

    @GET("products/categories")
    suspend fun getCategories(): List<DummyCategoryModel>

    @GET("products/category/{name}")
    suspend fun getProductsByCategory(
        @Path("name") name: String,
        @Query("limit") limit: Int,
        @Query("skip") skip: Int
    ): DummyCategoryProductsResponse

    @GET("carts/{id}")
    suspend fun getCartById(
        @Path("id") cartId: Int
    ): CartResponse

    @GET("carts/user/{id}")
    suspend fun getUserCarts(
        @Path("id") userId: Int
    ): UserCartsResponse

    @POST("carts/add")
    suspend fun addCart(
        @Body request: AddCartRequest
    ): CartResponse

    @PUT("carts/{id}")
    suspend fun updateCart(
        @Path("id") cartId: Int,
        @Body request: UpdateCartRequest
    ): CartResponse

    @DELETE("carts/{id}")
    suspend fun deleteCart(
        @Path("id") cartId: Int
    ): CartResponse
}

data class UserCartsResponse(
    val carts: List<CartResponse>,
    val total: Int,
    val skip: Int,
    val limit: Int
)

data class AddCartRequest(
    val userId: Int,
    val products: List<AddCartProduct>
)

data class AddCartProduct(
    val id: Int,
    val quantity: Int
)

data class UpdateCartRequest(
    val products: List<UpdateCartProduct>
)

data class UpdateCartProduct(
    val id: Int,
    val quantity: Int
)
