package com.vunh.jetpack.bhx.domain.usecase

import javax.inject.Inject

data class HomeUseCases @Inject constructor(
    val syncPosts: SyncPostsUseCase,
    val getEscuelaProducts: GetEscuelaProductsUseCase,
    val getDummyCategories: GetDummyCategoriesUseCase,
    val getDummyProductsByCategory: GetDummyProductsByCategoryUseCase
)
