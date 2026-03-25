package com.vunh.jetpack.bhx.domain.usecase

import com.vunh.jetpack.bhx.domain.model.Product
import com.vunh.jetpack.bhx.domain.repository.HomeRepository
import javax.inject.Inject

class GetEscuelaProductsUseCase @Inject constructor(
    private val homeRepository: HomeRepository
) {
    suspend operator fun invoke(limit: Int, offset: Int): List<Product> {
        return homeRepository.getEscuelaProducts(limit, offset)
    }
}
