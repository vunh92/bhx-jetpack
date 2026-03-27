package com.vunh.jetpack.bhx.domain.repository

import com.vunh.jetpack.bhx.domain.model.Category
import com.vunh.jetpack.bhx.domain.model.Post
import com.vunh.jetpack.bhx.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    fun observePosts(): Flow<List<Post>>
    suspend fun syncPosts()
    
    suspend fun getEscuelaProducts(limit: Int, offset: Int): List<Product>
    suspend fun getDummyCategories(): List<Category>
    suspend fun getDummyProductsByCategory(name: String, limit: Int, offset: Int): List<Product>
}
