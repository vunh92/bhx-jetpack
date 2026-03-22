package com.vunh.jetpack.bhx.data.local

import com.vunh.jetpack.bhx.data.local.dao.PostDao
import com.vunh.jetpack.bhx.data.local.entity.PostEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PostLocalDataSource @Inject constructor(
    private val postDao: PostDao
) {
    fun observePosts(): Flow<List<PostEntity>> = postDao.observePosts()

    suspend fun replacePosts(posts: List<PostEntity>) {
        postDao.clearPosts()
        postDao.insertPosts(posts)
    }
}
