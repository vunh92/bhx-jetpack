package com.vunh.jetpack.bhx.domain.usecase

import com.vunh.jetpack.bhx.data.local.ProfileManager
import com.vunh.jetpack.bhx.presentation.cart.CartCategoryUi
import com.vunh.jetpack.bhx.presentation.cart.CartUiState
import javax.inject.Inject

class GetCartUiStateUseCase @Inject constructor(
    private val profileManager: ProfileManager
) {
    operator fun invoke(): CartUiState = CartUiState(
        userProfile = profileManager.getProfile(),
        categories = listOf(
            CartCategoryUi("Thịt heo"),
            CartCategoryUi("Mì ăn liền"),
            CartCategoryUi("Cá, hải sản"),
            CartCategoryUi("Thịt gà, vịt"),
            CartCategoryUi("Trứng gà, vịt"),
            CartCategoryUi("Xem tất cả", isViewAll = true)
        )
    )
}
