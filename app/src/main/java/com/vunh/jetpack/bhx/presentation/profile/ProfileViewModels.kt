package com.vunh.jetpack.bhx.presentation.profile

import android.content.Context
import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vunh.jetpack.bhx.data.remote.EscuelaApiService
import com.vunh.jetpack.bhx.domain.usecase.GetGiftUiStateUseCase
import com.vunh.jetpack.bhx.domain.usecase.GetPointExchangeUiStateUseCase
import com.vunh.jetpack.bhx.domain.usecase.GetProfileUiStateUseCase
import com.vunh.jetpack.bhx.domain.usecase.GetPromotionCollectionUiStateUseCase
import com.vunh.jetpack.bhx.domain.usecase.GetScannerUiStateUseCase
import com.vunh.jetpack.bhx.domain.usecase.GetWalletUiStateUseCase
import com.vunh.jetpack.bhx.domain.usecase.LoginUseCase
import com.vunh.jetpack.bhx.domain.usecase.MarkAllNotificationsReadUseCase
import com.vunh.jetpack.bhx.domain.usecase.ObserveNotificationsUseCase
import com.vunh.jetpack.bhx.domain.usecase.LogoutUseCase
import com.vunh.jetpack.bhx.domain.usecase.SeedNotificationsUseCase
import com.vunh.jetpack.bhx.domain.model.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

data class ProfileUiState(
    val phoneNumber: String = "",
    val showOtpDialog: Boolean = false,
    val userProfile: UserProfile? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val avatarUrl: String? = null
) {
    val isLoggedIn: Boolean = userProfile != null
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getProfileUiStateUseCase: GetProfileUiStateUseCase,
    private val loginUseCase: LoginUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val escuelaApiService: EscuelaApiService
) : ViewModel() {
    private val _uiState = MutableStateFlow(getProfileUiStateUseCase())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun showOtpDialog(phoneNumber: String) {
        _uiState.value = _uiState.value.copy(
            phoneNumber = phoneNumber,
            showOtpDialog = true
        )
    }

    fun dismissOtpDialog() {
        _uiState.value = _uiState.value.copy(showOtpDialog = false)
    }

    fun completeLogin() {
        val profile = loginUseCase(_uiState.value.phoneNumber)
        _uiState.value = _uiState.value.copy(
            userProfile = profile,
            showOtpDialog = false
        )
    }

    fun loginWithCredentials(username: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val profile = loginUseCase(username, password)
                _uiState.value = _uiState.value.copy(
                    userProfile = profile,
                    showOtpDialog = false,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "An unexpected error occurred"
                )
            }
        }
    }

    fun uploadAvatar(context: Context, uri: Uri) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val file = uriToFile(context, uri)
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
                
                val response = escuelaApiService.uploadFile(body)
                _uiState.value = _uiState.value.copy(
                    avatarUrl = response.location,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Upload failed: ${e.message}"
                )
            }
        }
    }

    private fun uriToFile(context: Context, uri: Uri): File {
        val file = File(context.cacheDir, "temp_avatar_${System.currentTimeMillis()}.jpg")
        context.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }
        return file
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun logout() {
        logoutUseCase()
        _uiState.value = _uiState.value.copy(userProfile = null)
    }
}

data class NotificationUiState(
    val notifications: List<NotificationItem> = emptyList()
) {
    val unreadCount: Int
        get() = notifications.count { !it.isRead }
}

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val observeNotificationsUseCase: ObserveNotificationsUseCase,
    private val seedNotificationsUseCase: SeedNotificationsUseCase,
    private val markAllNotificationsReadUseCase: MarkAllNotificationsReadUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(NotificationUiState())
    val uiState: StateFlow<NotificationUiState> = _uiState.asStateFlow()

    init {
        observeNotifications()
        seedNotifications()
    }

    private fun observeNotifications() {
        viewModelScope.launch {
            observeNotificationsUseCase().collectLatest { notifications ->
                _uiState.value = NotificationUiState(notifications = notifications)
            }
        }
    }

    private fun seedNotifications() {
        viewModelScope.launch {
            seedNotificationsUseCase()
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            markAllNotificationsReadUseCase()
        }
    }
}

data class ScannerUiState(
    val codeLength: Int = 6,
    val title: String = "Quét mã QR",
    val hint: String = "Nhập mã QR in trên hoá đơn",
    val galleryActionLabel: String = "Chọn ảnh mã QR từ thư viện"
)

@HiltViewModel
class ScannerViewModel @Inject constructor(
    getScannerUiStateUseCase: GetScannerUiStateUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(getScannerUiStateUseCase())
    val uiState: StateFlow<ScannerUiState> = _uiState.asStateFlow()
}

data class WalletUiState(
    val balance: String = "0",
    val transactionMessage: String = "Chưa có giao dịch nào được thực hiện."
)

@HiltViewModel
class WalletViewModel @Inject constructor(
    getWalletUiStateUseCase: GetWalletUiStateUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(getWalletUiStateUseCase())
    val uiState: StateFlow<WalletUiState> = _uiState.asStateFlow()

    fun setBalance(balance: String) {
        if (_uiState.value.balance != balance) {
            _uiState.value = _uiState.value.copy(balance = balance)
        }
    }
}

data class PromotionCollectionUiState(
    val featuredCoupons: List<CouponInfo> = emptyList(),
    val products: List<CouponProduct> = emptyList()
)

@HiltViewModel
class CouponViewModel @Inject constructor(
    getPromotionCollectionUiStateUseCase: GetPromotionCollectionUiStateUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(getPromotionCollectionUiStateUseCase())
    val uiState: StateFlow<PromotionCollectionUiState> = _uiState.asStateFlow()
}

@HiltViewModel
class SpecialOfferViewModel @Inject constructor(
    getPromotionCollectionUiStateUseCase: GetPromotionCollectionUiStateUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(getPromotionCollectionUiStateUseCase())
    val uiState: StateFlow<PromotionCollectionUiState> = _uiState.asStateFlow()
}

data class GiftScreenUiState(
    val gifts: List<GiftItem> = emptyList()
)

@HiltViewModel
class GiftViewModel @Inject constructor(
    getGiftUiStateUseCase: GetGiftUiStateUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(getGiftUiStateUseCase())
    val uiState: StateFlow<GiftScreenUiState> = _uiState.asStateFlow()
}

data class PointExchangeUiState(
    val banners: List<PointExchangeBannerUi> = emptyList()
)

data class PointExchangeBannerUi(
    val color: Color,
    val title: String,
    val subtitle: String
)

@HiltViewModel
class PointExchangeViewModel @Inject constructor(
    getPointExchangeUiStateUseCase: GetPointExchangeUiStateUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(getPointExchangeUiStateUseCase())
    val uiState: StateFlow<PointExchangeUiState> = _uiState.asStateFlow()
}
