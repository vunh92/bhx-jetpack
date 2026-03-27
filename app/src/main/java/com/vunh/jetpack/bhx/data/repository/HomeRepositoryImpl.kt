package com.vunh.jetpack.bhx.data.repository

import com.vunh.jetpack.bhx.data.local.PostLocalDataSource
import com.vunh.jetpack.bhx.data.mapper.toDomain
import com.vunh.jetpack.bhx.data.mapper.toEntity
import com.vunh.jetpack.bhx.data.remote.DummyJsonApiService
import com.vunh.jetpack.bhx.data.remote.EscuelaApiService
import com.vunh.jetpack.bhx.data.remote.PostRemoteDataSource
import com.vunh.jetpack.bhx.domain.model.Category
import com.vunh.jetpack.bhx.domain.model.Post
import com.vunh.jetpack.bhx.domain.model.Product
import com.vunh.jetpack.bhx.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val remoteDataSource: PostRemoteDataSource,
    private val localDataSource: PostLocalDataSource,
    private val escuelaApiService: EscuelaApiService,
    private val dummyJsonApiService: DummyJsonApiService
) : HomeRepository {
    override fun observePosts(): Flow<List<Post>> {
        return localDataSource.observePosts().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun syncPosts() {
        val remotePosts = remoteDataSource.getPosts()
        localDataSource.replacePosts(remotePosts.map { it.toEntity() })
    }

    override suspend fun getEscuelaProducts(limit: Int, offset: Int): List<Product> {
        return escuelaApiService.getProducts(limit, offset).map { it.toDomain() }
    }

    override suspend fun getDummyCategories(): List<Category> {
        return dummyJsonApiService.getCategories().map { it.toDomain() }
    }

    override suspend fun getDummyProductsByCategory(
        name: String,
        limit: Int,
        offset: Int
    ): List<Product> {
        return dummyJsonApiService.getProductsByCategory(
            name = name,
            limit = limit,
            skip = offset
        ).products.map { it.toDomain() }
    }
}
