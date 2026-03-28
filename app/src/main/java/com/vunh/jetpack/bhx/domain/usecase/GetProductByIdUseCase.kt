package com.vunh.jetpack.bhx.domain.usecase

import com.vunh.jetpack.bhx.data.mapper.toDomain
import com.vunh.jetpack.bhx.data.remote.DummyJsonApiService
import com.vunh.jetpack.bhx.domain.model.Product
import javax.inject.Inject

class GetProductByIdUseCase @Inject constructor(
    private val dummyJsonApiService: DummyJsonApiService
) {
    suspend operator fun invoke(productId: Int): Product {
        return dummyJsonApiService.getProductById(productId).toDomain()
    }
}
