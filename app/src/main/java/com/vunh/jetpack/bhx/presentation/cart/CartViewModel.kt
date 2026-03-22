package com.vunh.jetpack.bhx.presentation.cart

import androidx.lifecycle.ViewModel
import com.vunh.jetpack.bhx.domain.usecase.GetCartUiStateUseCase
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
    val categories: List<CartCategoryUi> = emptyList()
) {
    val isLoggedIn: Boolean = userProfile != null
}

@HiltViewModel
class CartViewModel @Inject constructor(
    private val getCartUiStateUseCase: GetCartUiStateUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(getCartUiStateUseCase())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    fun refreshProfile() {
        _uiState.value = getCartUiStateUseCase()
    }
}
