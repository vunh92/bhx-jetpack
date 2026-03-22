package com.vunh.jetpack.bhx.domain.usecase

import com.vunh.jetpack.bhx.data.local.ProfileManager
import com.vunh.jetpack.bhx.presentation.history.OrderHistoryUiState
import javax.inject.Inject

class GetOrderHistoryUiStateUseCase @Inject constructor(
    private val profileManager: ProfileManager
) {
    operator fun invoke(): OrderHistoryUiState = OrderHistoryUiState(
        isLoggedIn = profileManager.isLoggedIn()
    )
}
