package com.vunh.jetpack.bhx.domain.repository

import com.vunh.jetpack.bhx.domain.model.Post

interface HomeRepository {
    suspend fun getPosts(): List<Post>
}
