package com.vunh.jetpack.bhx.domain.usecase

import com.vunh.jetpack.bhx.domain.repository.NotificationRepository
import com.vunh.jetpack.bhx.presentation.profile.NotificationItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveNotificationsUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
) {
    operator fun invoke(): Flow<List<NotificationItem>> = notificationRepository.observeNotifications()
}

class SeedNotificationsUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke() = notificationRepository.seedNotifications()
}

class MarkAllNotificationsReadUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke() = notificationRepository.markAllAsRead()
}
