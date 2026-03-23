package com.vunh.jetpack.bhx.domain.repository

import com.vunh.jetpack.bhx.presentation.profile.NotificationItem
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    fun observeNotifications(): Flow<List<NotificationItem>>
    suspend fun seedNotifications()
    suspend fun markAllAsRead()
}
