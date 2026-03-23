package com.vunh.jetpack.bhx.data.repository

import com.vunh.jetpack.bhx.data.local.NotificationLocalDataSource
import com.vunh.jetpack.bhx.data.mapper.toEntity
import com.vunh.jetpack.bhx.data.mapper.toUiModel
import com.vunh.jetpack.bhx.domain.repository.NotificationRepository
import com.vunh.jetpack.bhx.presentation.profile.NotificationItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NotificationRepositoryImpl(
    private val localDataSource: NotificationLocalDataSource
) : NotificationRepository {
    override fun observeNotifications(): Flow<List<NotificationItem>> {
        return localDataSource.observeNotifications().map { entities ->
            entities.map { it.toUiModel() }
        }
    }

    override suspend fun seedNotifications() {
        localDataSource.seedNotifications(defaultNotifications.map { it.toEntity() })
    }

    override suspend fun markAllAsRead() {
        localDataSource.markAllAsRead()
    }

    private companion object {
        val defaultNotifications = listOf(
            NotificationItem(
                id = "notification_1",
                title = "PHIẾU MUA HÀNG GIẢM 20.000đ",
                content = "Tặng Anh VU mã giảm 20.000đ áp dụng khi mua các sản phẩm dầu gội Nguyên Xuân tại siêu thị hoặc Online Bách Hóa XANH\nMã: 6X0TZW62WP\nHạn sử dụng: 11/03/2026",
                time = "13:47 04/03/2026",
                isRead = false
            ),
            NotificationItem(
                id = "notification_2",
                title = "PHIẾU MUA HÀNG GIẢM 20.000đ",
                content = "Tặng Anh VU mã giảm 20.000đ áp dụng khi mua các sản phẩm băng vệ sinh từ 50.000đ tại siêu thị hoặc Online Bách Hóa XANH\nMã: JNUN3B65SU\nHạn sử dụng: 11/03/2026",
                time = "13:46 04/03/2026",
                isRead = false
            )
        )
    }
}
