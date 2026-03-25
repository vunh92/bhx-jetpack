package com.vunh.jetpack.bhx.data.remote

import com.vunh.jetpack.bhx.domain.model.Post
import retrofit2.http.GET
import retrofit2.http.Query

interface JsonPlaceholderApiService {
    @GET("posts")
    suspend fun getPosts(
        @Query("_limit") limit: Int = 10,
        @Query("_start") start: Int = 0
    ): List<Post>
}
