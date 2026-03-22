package com.vunh.jetpack.bhx.presentation.profile

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.vunh.jetpack.bhx.data.local.ProfileManager
import com.vunh.jetpack.bhx.domain.model.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class ProfileUiState(
    val phoneNumber: String = "",
    val showOtpDialog: Boolean = false,
    val userProfile: UserProfile? = null
) {
    val isLoggedIn: Boolean = userProfile != null
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileManager: ProfileManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        ProfileUiState(userProfile = profileManager.getProfile())
    )
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
        val phoneNumber = _uiState.value.phoneNumber
        val profile = UserProfile(
            name = "Anh Vu",
            phoneNumber = phoneNumber,
            rank = "CHƯA CÓ HẠNG",
            points = 17450,
            memberCode = "450138"
        )
        profileManager.saveProfile(profile)
        _uiState.value = _uiState.value.copy(
            userProfile = profile,
            showOtpDialog = false
        )
    }

    fun logout() {
        profileManager.clearProfile()
        _uiState.value = _uiState.value.copy(userProfile = null)
    }
}

data class NotificationUiState(
    val notifications: List<NotificationItem> = listOf(
        NotificationItem(
            title = "PHIẾU MUA HÀNG GIẢM 20.000đ",
            content = "Tặng Anh VU mã giảm 20.000đ áp dụng khi mua các sản phẩm dầu gội Nguyên Xuân tại siêu thị hoặc Online Bách Hóa XANH\nMã: 6X0TZW62WP\nHạn sử dụng: 11/03/2026",
            time = "13:47 04/03/2026",
            isRead = false
        ),
        NotificationItem(
            title = "PHIẾU MUA HÀNG GIẢM 20.000đ",
            content = "Tặng Anh VU mã giảm 20.000đ áp dụng khi mua các sản phẩm băng vệ sinh từ 50.000đ tại siêu thị hoặc Online Bách Hóa XANH\nMã: JNUN3B65SU\nHạn sử dụng: 11/03/2026",
            time = "13:47 04/03/2026",
            isRead = false
        )
    )
) {
    val unreadCount: Int
        get() = notifications.count { !it.isRead }
}

@HiltViewModel
class NotificationViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(NotificationUiState())
    val uiState: StateFlow<NotificationUiState> = _uiState.asStateFlow()

    fun markAllAsRead() {
        _uiState.value = _uiState.value.copy(
            notifications = _uiState.value.notifications.map { it.copy(isRead = true) }
        )
    }
}

data class ScannerUiState(
    val codeLength: Int = 6,
    val title: String = "Quét mã QR",
    val hint: String = "Nhập mã QR in trên hoá đơn",
    val galleryActionLabel: String = "Chọn ảnh mã QR từ thư viện"
)

@HiltViewModel
class ScannerViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(ScannerUiState())
    val uiState: StateFlow<ScannerUiState> = _uiState.asStateFlow()
}

data class WalletUiState(
    val balance: String = "0",
    val transactionMessage: String = "Chưa có giao dịch nào được thực hiện."
)

@HiltViewModel
class WalletViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(WalletUiState())
    val uiState: StateFlow<WalletUiState> = _uiState.asStateFlow()

    fun setBalance(balance: String) {
        if (_uiState.value.balance != balance) {
            _uiState.value = _uiState.value.copy(balance = balance)
        }
    }
}

data class PromotionCollectionUiState(
    val featuredCoupons: List<CouponInfo> = listOf(
        CouponInfo("Tặng 20K", "Mua sản phẩm Kem các loại từ 120.000đ", "KT: 15/03/2026"),
        CouponInfo("Tặng 50k", "Mua sản phẩm Kem, Sữa chua, Đông mát từ 250.000đ", "KT: 15/03/2026"),
        CouponInfo("Tặng 30k", "Mua trái cây nhập khẩu từ 80.000đ", "KT: 15/03/2026")
    ),
    val products: List<CouponProduct> = listOf(
        CouponProduct("Nước mắm cá cơm K...", "29.000đ", "53.000đ", "-45%"),
        CouponProduct("Nước xả Comfort diệ...", "168.000đ", "238.000đ", "-29%"),
        CouponProduct("Nước xả Comfort tinh...", "168.000đ", "238.000đ", "-29%"),
        CouponProduct("Nước xả Comfort hươ...", "168.000đ", "238.000đ", "-29%"),
        CouponProduct("Bột giặt Omo Matic...", "168.000đ", "238.000đ", "-28%"),
        CouponProduct("Nước xả Comfort...", "168.000đ", "238.000đ", "-29%")
    )
)

@HiltViewModel
class CouponViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(PromotionCollectionUiState())
    val uiState: StateFlow<PromotionCollectionUiState> = _uiState.asStateFlow()
}

@HiltViewModel
class SpecialOfferViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(PromotionCollectionUiState())
    val uiState: StateFlow<PromotionCollectionUiState> = _uiState.asStateFlow()
}

data class GiftScreenUiState(
    val gifts: List<GiftItem> = listOf(
        GiftItem(
            title = "PHIẾU MUA HÀNG GIẢM 20.000Đ MUA DẦU GỘI NGUYÊN XUÂN BẤT KỲ",
            expiry = "Hạn dùng: 11/03/2026",
            status = "Còn 2 ngày",
            statusColor = Color(0xFFE67E22),
            isExpired = false
        ),
        GiftItem(
            title = "PHIẾU MUA HÀNG GIẢM 20.000Đ MUA BĂNG VỆ SINH TỪ 50.000Đ",
            expiry = "Hạn dùng: 11/03/2026",
            status = "Còn 2 ngày",
            statusColor = Color(0xFFE67E22),
            isExpired = false
        ),
        GiftItem(
            title = "PHIẾU MUA HÀNG GIẢM 40.000Đ MUA GẠO ST 25 TẠI BÁCH HÓA XANH",
            expiry = "Đã hết hạn",
            status = "",
            statusColor = Color.Gray,
            isExpired = true
        )
    )
)

@HiltViewModel
class GiftViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(GiftScreenUiState())
    val uiState: StateFlow<GiftScreenUiState> = _uiState.asStateFlow()
}

data class PointExchangeUiState(
    val banners: List<PointExchangeBannerUi> = listOf(
        PointExchangeBannerUi(
            color = Color(0xFFFFF176),
            title = "Hóa Đơn Mua Hàng Bách Hóa Xanh Bất Kỳ",
            subtitle = "Tặng PMH 15K Mua Omo,Comfort,Sunlight,Clear,Dove,.. Từ 159K"
        ),
        PointExchangeBannerUi(
            color = Color(0xFF81C784),
            title = "Mua gạo tích lũy 500.000đ",
            subtitle = "Nhận phiếu mua hàng 20.000đ"
        ),
        PointExchangeBannerUi(
            color = Color(0xFFFFB74D),
            title = "TÍCH LŨY SỮA ABBOTT GROW & PEDIASURE",
            subtitle = "TẶNG PHIẾU MUA HÀNG TƯƠI SỐNG"
        ),
        PointExchangeBannerUi(
            color = Color(0xFFF8BBD0),
            title = "TÍCH LŨY NHẬN QUÀ",
            subtitle = "VOUCHER GIẢM 3% - 5% - 10%"
        )
    )
)

data class PointExchangeBannerUi(
    val color: Color,
    val title: String,
    val subtitle: String
)

@HiltViewModel
class PointExchangeViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(PointExchangeUiState())
    val uiState: StateFlow<PointExchangeUiState> = _uiState.asStateFlow()
}
