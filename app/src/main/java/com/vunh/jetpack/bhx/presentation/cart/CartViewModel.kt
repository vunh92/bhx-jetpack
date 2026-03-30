package com.vunh.jetpack.bhx.presentation.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vunh.jetpack.bhx.data.local.ProfileManager
import com.vunh.jetpack.bhx.data.remote.DummyJsonApiService
import com.vunh.jetpack.bhx.data.remote.model.CartProduct
import com.vunh.jetpack.bhx.domain.usecase.GetCartUiStateUseCase
import com.vunh.jetpack.bhx.domain.model.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CartCategoryUi(
    val title: String,
    val isViewAll: Boolean = false
)

data class CartItemUiState(
    val cartId: Int,
    val product: CartProduct
)

data class CartUiState(
    val userProfile: UserProfile? = null,
    val categories: List<CartCategoryUi> = emptyList(),
    val cartItems: List<CartItemUiState> = emptyList(),
    val isLoading: Boolean = false,
    val isDeleting: Boolean = false,
    val errorMessage: String? = null
) {
    val isLoggedIn: Boolean = userProfile != null
}

@HiltViewModel
class CartViewModel @Inject constructor(
    private val getCartUiStateUseCase: GetCartUiStateUseCase,
    private val dummyJsonApiService: DummyJsonApiService,
    private val profileManager: ProfileManager
) : ViewModel() {

    private val _cartItems = MutableStateFlow<List<CartItemUiState>>(emptyList())
    private val _isLoading = MutableStateFlow(false)
    private val _isDeleting = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<CartUiState> = combine(
        profileManager.profileFlow,
        _cartItems,
        _isLoading,
        _isDeleting,
        _errorMessage
    ) { profile, items, loading, deleting, error ->
        CartUiState(
            userProfile = profile,
            categories = getCartUiStateUseCase().categories,
            cartItems = items,
            isLoading = loading,
            isDeleting = deleting,
            errorMessage = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CartUiState(
            userProfile = profileManager.getProfile(),
            categories = getCartUiStateUseCase().categories,
            isLoading = true
        )
    )

    init {
        viewModelScope.launch {
            profileManager.profileFlow.collect { profile ->
                if (profile != null) {
                    loadUserCarts(profile.id)
                } else {
                    _cartItems.value = emptyList()
                    _errorMessage.value = null
                }
            }
        }
    }

    private fun loadUserCarts(userId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = dummyJsonApiService.getUserCarts(userId)
                val allItems = response.carts.flatMap { cart ->
                    cart.products.map { product ->
                        CartItemUiState(cartId = cart.id, product = product)
                    }
                }
                _cartItems.value = allItems
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to load cart"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteCart(cartId: Int) {
        viewModelScope.launch {
            _isDeleting.value = true
            try {
                dummyJsonApiService.deleteCart(cartId)
                // Filter out all products belonging to this cartId from local state
                _cartItems.value = _cartItems.value.filter { it.cartId != cartId }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to delete cart"
            } finally {
                _isDeleting.value = false
            }
        }
    }

    fun refresh() {
        profileManager.getProfile()?.let { loadUserCarts(it.id) }
    }
}
