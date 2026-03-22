package com.vunh.jetpack.bhx.data.remote

import com.vunh.jetpack.bhx.domain.model.Post
import javax.inject.Inject

class PostRemoteDataSource @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getPosts(): List<Post> = apiService.getPosts()
}
