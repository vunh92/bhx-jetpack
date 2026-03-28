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
    private val dummyJsonApiService: DummyJsonApiService,
    private val profileManager: ProfileManager
) : ViewModel() {

    private val _cartProducts = MutableStateFlow<List<CartProduct>>(emptyList())
    private val _isLoading = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow<String?>(null)

    // Kết hợp các flow để tạo ra uiState duy nhất, tự động cập nhật khi userProfile thay đổi
    val uiState: StateFlow<CartUiState> = combine(
        profileManager.profileFlow,
        _cartProducts,
        _isLoading,
        _errorMessage
    ) { profile, products, loading, error ->
        CartUiState(
            userProfile = profile,
            categories = getCartUiStateUseCase().categories,
            cartProducts = products,
            isLoading = loading,
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
        // Theo dõi sự thay đổi của profile để tải dữ liệu giỏ hàng tương ứng
        viewModelScope.launch {
            profileManager.profileFlow.collect { profile ->
                if (profile != null) {
                    loadUserCarts(profile.id)
                } else {
                    _cartProducts.value = emptyList()
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
                val allProducts = response.carts.flatMap { it.products }
                _cartProducts.value = allProducts
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to load cart"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refresh() {
        profileManager.getProfile()?.let { loadUserCarts(it.id) }
    }
}
