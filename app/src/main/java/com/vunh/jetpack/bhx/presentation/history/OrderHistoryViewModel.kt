package com.vunh.jetpack.bhx.presentation.history

import androidx.lifecycle.ViewModel
import com.vunh.jetpack.bhx.domain.usecase.GetOrderHistoryUiStateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class OrderHistoryUiState(
    val isLoggedIn: Boolean = false,
    val selectedTabIndex: Int = 0,
    val tabs: List<String> = listOf("Tất cả", "Chờ giao", "Đang giao", "Giao thành công", "Hủy đơn")
)

@HiltViewModel
class OrderHistoryViewModel @Inject constructor(
    getOrderHistoryUiStateUseCase: GetOrderHistoryUiStateUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(getOrderHistoryUiStateUseCase())
    val uiState: StateFlow<OrderHistoryUiState> = _uiState.asStateFlow()

    fun selectTab(index: Int) {
        _uiState.value = _uiState.value.copy(selectedTabIndex = index)
    }
}
