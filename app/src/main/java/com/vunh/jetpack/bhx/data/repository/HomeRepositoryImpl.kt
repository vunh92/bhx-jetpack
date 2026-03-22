package com.vunh.jetpack.bhx.data.repository

import com.vunh.jetpack.bhx.data.local.PostLocalDataSource
import com.vunh.jetpack.bhx.data.mapper.toDomain
import com.vunh.jetpack.bhx.data.mapper.toEntity
import com.vunh.jetpack.bhx.data.remote.PostRemoteDataSource
import com.vunh.jetpack.bhx.domain.model.Post
import com.vunh.jetpack.bhx.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class HomeRepositoryImpl(
    private val remoteDataSource: PostRemoteDataSource,
    private val localDataSource: PostLocalDataSource
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
}
