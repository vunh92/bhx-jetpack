package com.vunh.jetpack.bhx.presentation.cart

import androidx.lifecycle.ViewModel
import com.vunh.jetpack.bhx.data.local.ProfileManager
import com.vunh.jetpack.bhx.domain.model.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class CartCategoryUi(
    val title: String,
    val isViewAll: Boolean = false
)

data class CartUiState(
    val userProfile: UserProfile? = null,
    val categories: List<CartCategoryUi> = listOf(
        CartCategoryUi("Thịt heo"),
        CartCategoryUi("Mì ăn liền"),
        CartCategoryUi("Cá, hải sản"),
        CartCategoryUi("Thịt gà, vịt"),
        CartCategoryUi("Trứng gà, vịt"),
        CartCategoryUi("Xem tất cả", isViewAll = true)
    )
) {
    val isLoggedIn: Boolean = userProfile != null
}

@HiltViewModel
class CartViewModel @Inject constructor(
    private val profileManager: ProfileManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    init {
        refreshProfile()
    }

    fun refreshProfile() {
        _uiState.value = _uiState.value.copy(userProfile = profileManager.getProfile())
    }
}
