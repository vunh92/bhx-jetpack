package com.vunh.jetpack.bhx.domain.repository

import com.vunh.jetpack.bhx.domain.model.Post
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    fun observePosts(): Flow<List<Post>>
    suspend fun syncPosts()
}
