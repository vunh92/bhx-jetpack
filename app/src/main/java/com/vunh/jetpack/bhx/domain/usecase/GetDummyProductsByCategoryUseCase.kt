package com.vunh.jetpack.bhx.domain.usecase

import com.vunh.jetpack.bhx.domain.model.Product
import com.vunh.jetpack.bhx.domain.repository.HomeRepository
import javax.inject.Inject

class GetDummyProductsByCategoryUseCase @Inject constructor(
    private val homeRepository: HomeRepository
) {
    suspend operator fun invoke(name: String, limit: Int, offset: Int): List<Product> {
        return homeRepository.getDummyProductsByCategory(name, limit, offset)
    }
}
