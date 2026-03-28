package com.vunh.jetpack.bhx.presentation.cart

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vunh.jetpack.bhx.data.remote.model.CartResponse
import com.vunh.jetpack.bhx.domain.usecase.GetCartByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CartDetailUiState(
    val cart: CartResponse? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class CartDetailViewModel @Inject constructor(
    private val getCartByIdUseCase: GetCartByIdUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(CartDetailUiState())
    val uiState: StateFlow<CartDetailUiState> = _uiState.asStateFlow()

    init {
        val cartId: Int? = savedStateHandle["cartId"]
        cartId?.let { id ->
            loadCartDetail(id)
        }
    }

    private fun loadCartDetail(cartId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val cart = getCartByIdUseCase(cartId)
                _uiState.value = _uiState.value.copy(cart = cart, isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to load cart detail"
                )
            }
        }
    }
}
