package com.vunh.jetpack.bhx.presentation.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vunh.jetpack.bhx.data.remote.UpdateCartProduct
import com.vunh.jetpack.bhx.data.remote.model.CartProduct
import com.vunh.jetpack.bhx.data.remote.model.CartResponse
import com.vunh.jetpack.bhx.domain.usecase.UpdateCartUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CartProductUpdateUiState(
    val updatedCart: CartResponse? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class CartProductDetailViewModel @Inject constructor(
    private val updateCartUseCase: UpdateCartUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CartProductUpdateUiState())
    val uiState: StateFlow<CartProductUpdateUiState> = _uiState.asStateFlow()

    fun updateProductQuantity(cartId: Int, productId: Int, quantity: Int) {
        viewModelScope.launch {
            _uiState.value = CartProductUpdateUiState(isLoading = true)
            try {
                // In a real scenario, we might want to pass all products in the cart, 
                // but dummyjson PUT /carts/{id} expects the products to be updated.
                val productsToUpdate = listOf(UpdateCartProduct(id = productId, quantity = quantity))
                val response = updateCartUseCase(cartId, productsToUpdate)
                _uiState.value = CartProductUpdateUiState(
                    updatedCart = response,
                    isLoading = false,
                    isSuccess = true
                )
            } catch (e: Exception) {
                _uiState.value = CartProductUpdateUiState(
                    isLoading = false,
                    error = e.message ?: "Failed to update cart"
                )
            }
        }
    }
    
    fun resetState() {
        _uiState.value = CartProductUpdateUiState()
    }
}
