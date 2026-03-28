package com.vunh.jetpack.bhx.presentation.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vunh.jetpack.bhx.data.remote.DummyJsonApiService
import com.vunh.jetpack.bhx.data.remote.model.CartProduct
import com.vunh.jetpack.bhx.domain.usecase.GetCartUiStateUseCase
import com.vunh.jetpack.bhx.domain.model.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CartCategoryUi(
    val title: String,
    val isViewAll: Boolean = false
)

data class CartUiState(
    val userProfile: UserProfile? = null,
    val categories: List<CartCategoryUi> = emptyList(),
    val cartProducts: List<CartProduct> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val isLoggedIn: Boolean = userProfile != null
}

@HiltViewModel
class CartViewModel @Inject constructor(
    private val getCartUiStateUseCase: GetCartUiStateUseCase,
    private val dummyJsonApiService: DummyJsonApiService
) : ViewModel() {
    private val _uiState = MutableStateFlow(getCartUiStateUseCase())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    init {
        loadUserCarts()
    }

    private fun loadUserCarts() {
        val userProfile = uiState.value.userProfile
        if (userProfile != null) {
            viewModelScope.launch {
                _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
                try {
                    val response = dummyJsonApiService.getUserCarts(userProfile.id)
                    // Lấy danh sách sản phẩm từ tất cả các giỏ hàng của user
                    val allProducts = response.carts.flatMap { it.products }
                    _uiState.value = _uiState.value.copy(
                        cartProducts = allProducts,
                        isLoading = false
                    )
                } catch (e: Exception) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Failed to load cart"
                    )
                }
            }
        }
    }

    fun refreshProfile() {
        _uiState.value = getCartUiStateUseCase()
        loadUserCarts()
    }
}
