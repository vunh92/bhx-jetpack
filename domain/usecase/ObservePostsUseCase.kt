package com.vunh.jetpack.bhx.domain.usecase

import com.vunh.jetpack.bhx.domain.model.Post
import com.vunh.jetpack.bhx.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObservePostsUseCase @Inject constructor(
    private val homeRepository: HomeRepository
) {
    operator fun invoke(): Flow<List<Post>> {
        return homeRepository.observePosts()
    }
}
