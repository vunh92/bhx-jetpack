package com.vunh.jetpack.bhx.data.remote

import com.vunh.jetpack.bhx.domain.model.Post
import retrofit2.http.GET

interface ApiService {
    @GET("posts")
    suspend fun getPosts(): List<Post>
}
