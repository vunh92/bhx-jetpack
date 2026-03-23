package com.vunh.jetpack.bhx.domain.usecase

import androidx.compose.ui.graphics.Color
import com.vunh.jetpack.bhx.data.local.ProfileManager
import com.vunh.jetpack.bhx.domain.model.UserProfile
import com.vunh.jetpack.bhx.presentation.profile.CouponInfo
import com.vunh.jetpack.bhx.presentation.profile.CouponProduct
import com.vunh.jetpack.bhx.presentation.profile.GiftItem
import com.vunh.jetpack.bhx.presentation.profile.GiftScreenUiState
import com.vunh.jetpack.bhx.presentation.profile.PointExchangeBannerUi
import com.vunh.jetpack.bhx.presentation.profile.PointExchangeUiState
import com.vunh.jetpack.bhx.presentation.profile.ProfileUiState
import com.vunh.jetpack.bhx.presentation.profile.PromotionCollectionUiState
import com.vunh.jetpack.bhx.presentation.profile.ScannerUiState
import com.vunh.jetpack.bhx.presentation.profile.WalletUiState
import javax.inject.Inject

class GetProfileUiStateUseCase @Inject constructor(
    private val profileManager: ProfileManager
) {
    operator fun invoke(): ProfileUiState = ProfileUiState(
        userProfile = profileManager.getProfile()
    )
}

class LoginUseCase @Inject constructor(
    private val profileManager: ProfileManager
) {
    operator fun invoke(phoneNumber: String): UserProfile {
        val profile = UserProfile(
            name = "Anh Vu",
            phoneNumber = phoneNumber,
            rank = "CHƯA CÓ HẠNG",
            points = 17450,
            memberCode = "450138"
        )
        profileManager.saveProfile(profile)
        return profile
    }
}

class LogoutUseCase @Inject constructor(
    private val profileManager: ProfileManager
) {
    operator fun invoke() {
        profileManager.clearProfile()
    }
}

class GetScannerUiStateUseCase @Inject constructor() {
    operator fun invoke(): ScannerUiState = ScannerUiState()
}

class GetWalletUiStateUseCase @Inject constructor() {
    operator fun invoke(): WalletUiState = WalletUiState()
}

class GetPromotionCollectionUiStateUseCase @Inject constructor() {
    operator fun invoke(): PromotionCollectionUiState = PromotionCollectionUiState(
        featuredCoupons = listOf(
            CouponInfo("Tặng 20K", "Mua sản phẩm Kem các loại từ 120.000đ", "KT: 15/03/2026"),
            CouponInfo("Tặng 50k", "Mua sản phẩm Kem, Sữa chua, Đông mát từ 250.000đ", "KT: 15/03/2026"),
            CouponInfo("Tặng 30k", "Mua trái cây nhập khẩu từ 80.000đ", "KT: 15/03/2026")
        ),
        products = listOf(
            CouponProduct("Nước mắm cá cơm K...", "29.000đ", "53.000đ", "-45%"),
            CouponProduct("Nước xả Comfort diệ...", "168.000đ", "238.000đ", "-29%"),
            CouponProduct("Nước xả Comfort tinh...", "168.000đ", "238.000đ", "-29%"),
            CouponProduct("Nước xả Comfort hươ...", "168.000đ", "238.000đ", "-29%"),
            CouponProduct("Bột giặt Omo Matic...", "168.000đ", "238.000đ", "-28%"),
            CouponProduct("Nước xả Comfort...", "168.000đ", "238.000đ", "-29%")
        )
    )
}

class GetGiftUiStateUseCase @Inject constructor() {
    operator fun invoke(): GiftScreenUiState = GiftScreenUiState(
        gifts = listOf(
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
}

class GetPointExchangeUiStateUseCase @Inject constructor() {
    operator fun invoke(): PointExchangeUiState = PointExchangeUiState(
        banners = listOf(
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
}
