package com.vunh.jetpack.bhx.data.local

import com.vunh.jetpack.bhx.data.local.dao.NotificationDao
import com.vunh.jetpack.bhx.data.local.entity.NotificationEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NotificationLocalDataSource @Inject constructor(
    private val notificationDao: NotificationDao
) {
    fun observeNotifications(): Flow<List<NotificationEntity>> = notificationDao.observeNotifications()

    suspend fun seedNotifications(notifications: List<NotificationEntity>) {
        if (notificationDao.countNotifications() == 0) {
            notificationDao.insertNotifications(notifications)
        }
    }

    suspend fun markAllAsRead() {
        notificationDao.markAllAsRead()
    }
}
