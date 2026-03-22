package com.vunh.jetpack.bhx.domain.usecase

import com.vunh.jetpack.bhx.presentation.category.CategoryUiState
import javax.inject.Inject

class GetCategoryUiStateUseCase @Inject constructor() {
    operator fun invoke(): CategoryUiState = CategoryUiState()
}
