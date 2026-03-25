package com.vunh.jetpack.bhx.domain.usecase

import com.vunh.jetpack.bhx.domain.repository.HomeRepository
import javax.inject.Inject

class SyncPostsUseCase @Inject constructor(
    private val homeRepository: HomeRepository
) {
    suspend operator fun invoke() {
        homeRepository.syncPosts()
    }
}
