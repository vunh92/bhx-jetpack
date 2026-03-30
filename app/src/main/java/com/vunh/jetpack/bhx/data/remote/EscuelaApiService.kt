package com.vunh.jetpack.bhx.data.remote

import com.vunh.jetpack.bhx.data.remote.model.FileUploadResponse
import com.vunh.jetpack.bhx.data.remote.model.ProductEscuelaModel
import okhttp3.MultipartBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface EscuelaApiService {
    @GET("v1/products")
    suspend fun getProducts(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): List<ProductEscuelaModel>

    @Multipart
    @POST("v1/files/upload")
    suspend fun uploadFile(
        @Part file: MultipartBody.Part
    ): FileUploadResponse
}
