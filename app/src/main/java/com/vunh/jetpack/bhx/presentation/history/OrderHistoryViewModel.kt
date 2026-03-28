package com.vunh.jetpack.bhx.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vunh.jetpack.bhx.data.local.ProfileManager
import com.vunh.jetpack.bhx.data.remote.model.CartResponse
import com.vunh.jetpack.bhx.domain.usecase.GetOrderHistoryUiStateUseCase
import com.vunh.jetpack.bhx.domain.usecase.GetOrderHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OrderHistoryUiState(
    val isLoggedIn: Boolean = false,
    val selectedTabIndex: Int = 0,
    val tabs: List<String> = listOf("Tất cả", "Chờ giao", "Đang giao", "Giao thành công", "Hủy đơn"),
    val orders: List<CartResponse> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class OrderHistoryViewModel @Inject constructor(
    private val getOrderHistoryUiStateUseCase: GetOrderHistoryUiStateUseCase,
    private val getOrderHistoryUseCase: GetOrderHistoryUseCase,
    private val profileManager: ProfileManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(getOrderHistoryUiStateUseCase())
    val uiState: StateFlow<OrderHistoryUiState> = _uiState.asStateFlow()

    init {
        loadOrderHistory()
    }

    private fun loadOrderHistory() {
        val userProfile = profileManager.getProfile()
        if (userProfile != null) {
            viewModelScope.launch {
                _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
                try {
                    val orders = getOrderHistoryUseCase(userProfile.id)
                    _uiState.value = _uiState.value.copy(
                        orders = orders,
                        isLoading = false
                    )
                } catch (e: Exception) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Failed to load order history"
                    )
                }
            }
        }
    }

    fun selectTab(index: Int) {
        _uiState.value = _uiState.value.copy(selectedTabIndex = index)
    }

    fun refresh() {
        _uiState.value = getOrderHistoryUiStateUseCase()
        loadOrderHistory()
    }
}
