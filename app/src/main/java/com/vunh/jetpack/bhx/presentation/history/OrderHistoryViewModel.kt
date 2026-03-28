package com.vunh.jetpack.bhx.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vunh.jetpack.bhx.data.local.ProfileManager
import com.vunh.jetpack.bhx.data.remote.model.CartResponse
import com.vunh.jetpack.bhx.domain.usecase.GetOrderHistoryUiStateUseCase
import com.vunh.jetpack.bhx.domain.usecase.GetOrderHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
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

    private val _selectedTabIndex = MutableStateFlow(0)
    private val _orders = MutableStateFlow<List<CartResponse>>(emptyList())
    private val _isLoading = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow<String?>(null)

    // Kết hợp các flow để tạo ra uiState duy nhất, tự động cập nhật khi login/logout
    val uiState: StateFlow<OrderHistoryUiState> = combine(
        profileManager.profileFlow,
        _selectedTabIndex,
        _orders,
        _isLoading,
        _errorMessage
    ) { profile, tabIndex, orders, loading, error ->
        OrderHistoryUiState(
            isLoggedIn = profile != null,
            selectedTabIndex = tabIndex,
            tabs = getOrderHistoryUiStateUseCase().tabs,
            orders = orders,
            isLoading = loading,
            errorMessage = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = OrderHistoryUiState(
            isLoggedIn = profileManager.isLoggedIn(),
            isLoading = true
        )
    )

    init {
        // Theo dõi sự thay đổi của profile để tải dữ liệu lịch sử đơn hàng tương ứng
        viewModelScope.launch {
            profileManager.profileFlow.collect { profile ->
                if (profile != null) {
                    loadOrderHistory(profile.id)
                } else {
                    _orders.value = emptyList()
                    _errorMessage.value = null
                }
            }
        }
    }

    private fun loadOrderHistory(userId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val orders = getOrderHistoryUseCase(userId)
                _orders.value = orders
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to load order history"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun selectTab(index: Int) {
        _selectedTabIndex.value = index
    }

    fun refresh() {
        profileManager.getProfile()?.let { loadOrderHistory(it.id) }
    }
}
