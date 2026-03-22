package com.vunh.jetpack.bhx.domain.usecase

import com.vunh.jetpack.bhx.domain.model.Post
import com.vunh.jetpack.bhx.domain.repository.HomeRepository
import javax.inject.Inject

class GetPostsUseCase @Inject constructor(
    private val homeRepository: HomeRepository
) {
    suspend operator fun invoke(): List<Post> = homeRepository.getPosts()
}
