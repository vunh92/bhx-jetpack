package com.vunh.jetpack.bhx.data.remote

import com.vunh.jetpack.bhx.domain.model.Post
import javax.inject.Inject

class PostRemoteDataSource @Inject constructor(
    private val escuelaApiService: EscuelaApiService
) {
    suspend fun getPosts(): List<Post> {
        return escuelaApiService.getProducts(limit = 10, offset = 0).map { product ->
            Post(
                userId = 0,
                id = product.id,
                title = product.title,
                body = "${product.price} - ${product.description}"
            )
        }
    }
}
