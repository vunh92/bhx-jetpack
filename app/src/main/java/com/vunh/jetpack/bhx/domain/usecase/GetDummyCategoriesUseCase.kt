package com.vunh.jetpack.bhx.domain.usecase

import com.vunh.jetpack.bhx.domain.model.Category
import com.vunh.jetpack.bhx.domain.repository.HomeRepository
import javax.inject.Inject

class GetDummyCategoriesUseCase @Inject constructor(
    private val homeRepository: HomeRepository
) {
    suspend operator fun invoke(): List<Category> {
        return homeRepository.getDummyCategories()
    }
}
