package com.vunh.jetpack.bhx.data.repository

import com.vunh.jetpack.bhx.data.remote.ApiService
import com.vunh.jetpack.bhx.domain.model.Post
import com.vunh.jetpack.bhx.domain.repository.HomeRepository

class HomeRepositoryImpl(private val apiService: ApiService) : HomeRepository {
    override suspend fun getPosts(): List<Post> {
        return apiService.getPosts()
    }
}
