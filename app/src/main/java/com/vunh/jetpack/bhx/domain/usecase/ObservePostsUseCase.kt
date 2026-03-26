package com.vunh.jetpack.bhx.domain.usecase

import com.vunh.jetpack.bhx.domain.repository.HomeRepository
import javax.inject.Inject

class ObservePostsUseCase @Inject constructor(
    private val homeRepository: HomeRepository
) {
    operator fun invoke() = homeRepository.observePosts()
}
